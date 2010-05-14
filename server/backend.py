from pgasync import ConnectionPool
import json

import glue

class gogodeXProtocol(glue.NeutralLineReceiver):

  #Set thes to false for final
  ALLOW_DEBUG_JSON = True
  ALWAYS_RESPOND = False

  def __init__(self):
    self.pool = ConnectionPool("pgasync",dbname="mydb",user="jake",password="stupidpassword")

  def connectionMade(self):
    self.sendLine("Connection made!")

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
        if message != []: #Invalid user would return empty list
          return callback()

      d.addCallback(executeFunctionOnSuccess)
      d.addErrback(onError)


    #json message -> sql query. Tuple with parameters to be applied
    #separate so that pgasync can handle sql injection
    def getQuery(message):

      def parseCreateUser(o):
        self.pool.runOperation("INSERT INTO users VALUES (E%s, E%s, E%s, E%s, E%s, 0, 0)",
        (o['First Name'], o['Last Name'], o['User Name'], o['Password'],
        o['Account Type']))
        return "Created a user!"

      def parseRemoveUser(o):
        self.pool.runOperation("DELETE FROM users WHERE UserName=E%s AND Password=E%s",
        (o['User Name'], o['Password']))
        return "Removed a user!"

      def parseAddZone(o):
        tempfun = (lambda:
          self.pool.runOperation("INSERT INTO zonenames VALUES (E%s, E%s, %f, %f, %f)",
          (o['User Name'], o['Zone Name'], o['Lat'], o['Lon'], o['Radius'])))

        self.pool.runInteraction(validateUser, o, tempfun)
        return "Added a zone!"

      def parseRemoveZone(o):
        tempfun = (lambda:
          self.pool.runOperation("DELETE FROM zonenames WHERE UserName=E%s AND ZoneName=E%s", (o['User Name'], o['Zone Name'])))

        self.pool.runInteraction(validateUser, o, tempfun)
        return "Removed a zone!"

      def parseAddFriend(o):
        #This will be replaced by ryan

        tempfun = (lambda:
          [f() for f in [
          self.pool.runOperation("INSERT INTO friends VALUES (E%s, E%s, 'Pending')", (o['User Name'], o['Friend Name'])),
          self.pool.runOperation("INSERT INTO friends VALUES (E%s, E%s, 'Unaccepted')", (o['Friend Name'], o['User Name']))]])

        self.pool.runInteraction(validateUser, o, tempfun)
        return "Added a friend!"

      def parseAcceptFriend(o):
        tempfun = (lambda:
          self.pool.runOperation("UPDATE friends SET status='Accepted' WHERE (username=E%s AND friendname=E%s AND status='Unaccepted') OR (username=E%s AND friendname=E%s AND status='Pending')",
          (o['User Name'], o['Friend Name'], o['Friend Name'], o['User Name'])))

        self.pool.runInteraction(validateUser, o, tempfun)
        return "Accepted a friend!"

      def parseRemoveFriend(o):
        tempfun = (lambda:
          self.pool.runOperation("DELETE FROM friends WHERE (username=E%s AND friendname=E%s) OR (username=E%s AND friendname=E%s)",
          (o['User Name'], o['Friend Name'], o['Friend Name'], o['User Name'])))

        self.pool.runInteraction(validateUser, o, tempfun)
        return "Removed a friend!"

      def parseUpdateCoord(o):
        self.pool.runOperation("UPDATE users SET lat=%f, lon=%f WHERE username=E%s AND password=E%s",
        (o['Lat'], o['Lon'], o['User Name'], o['Password']))
        return "Updated position!"

      parser = {'Create User': parseCreateUser, 'Remove User': parseRemoveUser,
      'Add Zone': parseAddZone, 'Remove Zone': parseRemoveZone, 'Add Friend':
      parseAddFriend, 'Accept Friend': parseAcceptFriend, 'Remove Friend':
      parseRemoveFriend, 'Update Coordinate': parseUpdateCoord}

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
      if self.ALWAYS_RESPOND and response != None:
        self.sendLine(response)
    except (ValueError, KeyError):
      self.sendLine("Invalid query. Handle this appropriatly!")


    #NOTE: The E in name=E%s is for the funny way postgres handles backslash
    #self.pool.runInteraction(runQueries,"SELECT mystr FROM people WHERE name=E%s", line)
