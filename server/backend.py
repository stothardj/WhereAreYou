from twisted.internet import protocol
from pgasync import ConnectionPool
import json

import glue

class gogodeXProtocol(glue.NeutralLineReceiver):

  username = None

  #Set these to false for final
  ALLOW_DEBUG_JSON = True
  ALWAYS_RESPOND = False

  def __init__(self):
    self.pool = ConnectionPool("pgasync",dbname="mydb",user="jake",password="stupidpassword")

  def logout(self):
    if self.username != None:
      self.factory.logoutUser(self.username)
      self.username = None

  def connectionMade(self):
    if self.ALWAYS_RESPOND:
      self.sendLine("Connection made!")
    else:
      print "Connection made!"

  def connectionLost(self, reason):
    self.logout()
    print "Connection lost!"

  def neutralLineReceived(self, line):
    print "Received query for: "+line

    def onError(err):
      return 'Internal error in server'

    def writeResponse(message):
      self.sendLine(str(message))

    def runQueries(cur,query,args):
      cur.execute(query,args)
      d = cur.fetchall()
      d.addCallback(writeResponse)
      d.addErrback(onError)

    def validateUser(cur, o, callback):
      cur.execute("SELECT * FROM users WHERE UserName=E%s AND Password=E%s", (o['User Name'], o['Password']))
      d = cur.fetchall()

      def executeFunctionOnSuccess(message):
        msg = {}
        msg['Response Type']='User Validation'

        if message != []: #Invalid user would return empty list
          msg['Success']=True
          self.sendLine(json.dumps(msg))
          return callback()
        else:
          msg['Success']=False
          self.sendLine(json.dumps(msg))

      d.addCallback(executeFunctionOnSuccess)
      d.addErrback(onError)

    def uniqueUser(cur, uname, calltuple):
      (unique, notunique) = calltuple
      cur.execute("SELECT * FROM users WHERE UserName=E%s", uname)
      d = cur.fetchall()

      def executeAppropriateFunction(message):
        msg = {}
        msg['Response Type']='Pre-existing User'
        msg['User Name']=uname

        if message == []:
          msg['Exists']=False
          self.sendLine(json.dumps(msg))
          if unique != None:
            return unique()
        else:
          msg['Exists']=True
          self.sendLine(json.dumps(msg))
          if notunique != None:
            return notunique()
      d.addCallback(executeAppropriateFunction)
      d.addErrback(onError)

    #json message -> sql query. Tuple with parameters to be applied
    #separate so that pgasync can handle sql injection
    def getQuery(message):

      def parseCreateUser(o):
        tempfun = (lambda:
          self.pool.runOperation("INSERT INTO users VALUES (E%s, E%s, E%s, E%s, E%s, 0, 0)",
          (o['First Name'], o['Last Name'], o['User Name'], o['Password'],
          o['Account Type'])))
        self.pool.runInteraction(uniqueUser, o['User Name'], (tempfun, None))
        return "Created a user!"

      def parseRemoveUser(o):
        if self.username != None:
          self.pool.runOperation("DELETE FROM users WHERE UserName=E%s", self.username)
          self.pool.runOperation("DELETE FROM friends WHERE UserName=E%s OR FriendName=E%s", (self.username, self.username))
          self.pool.runOperation("DELETE FROM zonenames WHERE UserName=E%s", self.username)

        return "Removed a user!"

      def parseAddZone(o):
        if self.username != None:
          self.pool.runOperation("INSERT INTO zonenames VALUES (E%s, E%s, %f, %f, %f)",
          (username, o['Zone Name'], o['Lat'], o['Lon'], o['Radius']))

        return "Added a zone!"

      def parseRemoveZone(o):
        if self.username != None:
          self.pool.runOperation("DELETE FROM zonenames WHERE UserName=E%s AND ZoneName=E%s", (self.username, o['Zone Name']))

        return "Removed a zone!"

      def parseAddFriend(o):
        if self.username != None:
          tempfun = (lambda:
                       [f() for f in [
                self.pool.runOperation("INSERT INTO friends VALUES (E%s, E%s, 'Pending')", (self.username, o['Friend Name'])),
                self.pool.runOperation("INSERT INTO friends VALUES (E%s, E%s, 'Unaccepted')", (o['Friend Name'], self.username))]])
          self.pool.runInteraction(uniqueUser, o['Friend Name'], (None, tempfun))

        return "Added a friend!"

      def parseAcceptFriend(o):
        if self.username != None:
          self.pool.runOperation("UPDATE friends SET status='Accepted' WHERE (username=E%s AND friendname=E%s AND status='Unaccepted') OR (username=E%s AND friendname=E%s AND status='Pending')",
          (self.username, o['Friend Name'], o['Friend Name'], self.username))

        return "Accepted a friend!"

      def parseRemoveFriend(o):
        if self.username != None:
          self.pool.runOperation("DELETE FROM friends WHERE (username=E%s AND friendname=E%s) OR (username=E%s AND friendname=E%s)",
          (self.username, o['Friend Name'], o['Friend Name'], self.username))

        return "Removed a friend!"

      def parseUpdateCoord(o):
        if self.username != None:
          self.pool.runOperation("UPDATE users SET lat=%f, lon=%f WHERE username=E%s",
          (o['Lat'], o['Lon'], self.username))

          def pushPosition(friends):
            msg = {}
            msg['Response Type']='Position Update'
            msg['User Name']=self.username
            msg['Lat']=o['Lat']
            msg['Lon']=o['Lon']
            sentLine = json.dumps(msg)
            for a in friends:
              friend = a[0]
              try:
                self.factory.loggedin_users[friend].sendLine(sentLine)
              except:
                pass
                #print "Friend", friend, "is not logged in."

          def getFriends(cur,query,args):
            cur.execute(query,args)
            d = cur.fetchall()
            d.addCallback(pushPosition)
            d.addErrback(onError)

          self.pool.runInteraction(getFriends,"SELECT FriendName FROM friends WHERE UserName=E%s AND Status='Accepted'", self.username)

          return "Updated position!"

      def parseLogin(o):
        def login():
          if self.username != None:
            self.logout()
          self.username = o['User Name']
          self.factory.loginUser(o['User Name'], self)
          print "User name is now ", self.username

        self.pool.runInteraction(validateUser, o, login)
        return "Logged in!"

      parser = {'Create User': parseCreateUser, 'Remove User': parseRemoveUser,
      'Add Zone': parseAddZone, 'Remove Zone': parseRemoveZone, 'Add Friend':
      parseAddFriend, 'Accept Friend': parseAcceptFriend, 'Remove Friend':
      parseRemoveFriend, 'Update Coordinate': parseUpdateCoord, 'Login': parseLogin}

      if self.ALLOW_DEBUG_JSON:
        def parseShowUsers(o):
          self.pool.runInteraction(runQueries,"SELECT * FROM users")

        def parseShowFriends(o):
          self.pool.runInteraction(runQueries,"SELECT * FROM friends")

        def parseShowZones(o):
          self.pool.runInteraction(runQueries,"SELECT * FROM zonenames")

        test_parser = {'Show Users' : parseShowUsers, 'Show Friends' : parseShowFriends,
        'Show Zones': parseShowZones}
        parser.update(test_parser)

      jd = json.JSONDecoder()
      obj = jd.decode(message)

      return parser[obj['Request Type']](obj)

    try:
      response = getQuery(line)
      if response != None:
        if self.ALWAYS_RESPOND:
          self.sendLine(response)
        else:
          print response
    except (ValueError, KeyError):
      if self.ALWAYS_RESPOND:
        self.sendLine("Invalid query. Handle this appropriatly!")
      else:
        print "Invalid query. Handle this appropriatly!"

class gogodeXFactory(protocol.ServerFactory):
  protocol = gogodeXProtocol

  loggedin_users = {}

  def loginUser(self, username, protocolInstance):
    self.loggedin_users[username] = protocolInstance

  def logoutUser(self, username):
    del self.loggedin_users[username]
