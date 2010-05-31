from twisted.internet import protocol, defer
from pgasync import ConnectionPool  #Postgres/Twisted interface.
import json

import glue

'''
GENERAL TO DOs:
- Allow modification of zones.
- Check for duplicates in everything.
'''

'''
TO DO RRR
- Add zone does not work.
- Add friend does not work.
'''

with open('server.conf') as f:
  settings = f.read()
setobj = json.loads(settings)
pool = ConnectionPool("pgasync",dbname=str(setobj['Database']),user=str(setobj['User']),password=str(setobj['Password']))

class gogodeXProtocol(glue.NeutralLineReceiver):

  username = None

  #Set these to false for final
  ALLOW_DEBUG_JSON = True
  ALWAYS_RESPOND = False

  def __init__(self):
    pass

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

  def sendToOthers(self, msg, namelist, **kwargs):
    #save = If the user is not currently logged in, should the json be
    #saved in a table to be sent to that user when they login?
    saveJson = kwargs.get('save', False)
    for name in namelist:
      try:
        self.factory.loggedin_users[name].sendLine(msg)
      except KeyError:
        if saveJson:
          pool.runOperation("INSERT INTO savedmessages VALUES (E%s, E%s)", (name, msg))

  def neutralLineReceived(self, line):
    print "Received query for: "+line

    def writeResponse(message):
      self.sendLine(str(message))

    #json message -> sql query. Tuple with parameters to be applied
    #separate so that pgasync can handle sql injection
    def getQuery(message):

      def parseCreateUser(o):

        def _createUser(users):
          msg = {}
          msg['Response Type']='Created User'
          msg['User Name']=o['User Name']
          if users == []:
            msg['Success'] = True
            self.sendLine(json.dumps(msg))
            pool.runOperation("INSERT INTO users VALUES (E%s, E%s, E%s, E%s, E%s, '(0.0, 0.0)')",
              (o['First Name'], o['Last Name'], o['User Name'], o['Password'],
              o['Account Type']))
          else:
            msg['Success'] = False
            self.sendLine(json.dumps(msg))
        pool.runQuery("SELECT * FROM users WHERE UserName=E%s", o['User Name']).addCallback(_createUser)

        return "Created a user!"

      def parseRemoveUser(o):
        if self.username != None:
          pool.runOperation("DELETE FROM users WHERE UserName=E%s", self.username)
          pool.runOperation("DELETE FROM friends WHERE UserName=E%s OR FriendName=E%s", (self.username, self.username))
          pool.runOperation("DELETE FROM zonenames WHERE UserName=E%s", self.username)
          self.logout()
        return "Removed a user!"

      def parseAddZone(o):
        #TO DO: Check for overlapping zones and fail if zones overlap.
        if self.username != None:
          #To RRR: the radius needs to be converted from user units (feet, miles etc.) to GPS. Sigh.
          pool.runOperation("INSERT INTO zonenames VALUES (E%s, E%s, circle '(( %f, %f ), %f)', E%s, E%s)",
          (self.username, o['Zone Name'], o['Lat'], o['Lon'], o['Radius'], o['Action'], o['Text']))

        return "Added a zone!"

      def parseRemoveZone(o):
        if self.username != None:
          pool.runOperation("DELETE FROM zonenames WHERE UserName=E%s AND ZoneName=E%s", (self.username, o['Zone Name']))

        return "Removed a zone!"

      def parseAddFriend(o):
        if self.username != None and self.username != o['Friend Name']:

          def _addFriend():
            pool.runOperation("INSERT INTO friends VALUES (E%s, E%s, 'Pending')", (self.username, o['Friend Name']))
            pool.runOperation("INSERT INTO friends VALUES (E%s, E%s, 'Unaccepted')", (o['Friend Name'], self.username))
            msg = {}
            msg['Response Type']='Friend Request'
            msg['From User']=self.username
            self.sendToOthers(json.dumps(msg), [o['Friend Name']], save = True)
            #try:
            #  self.factory.loggedin_users[o['Friend Name']].sendLine(json.dumps(msg))
            #except:
            #  pass

          def _checkIfShouldFriend(returnList):
            #self.sendLine(str(returnList))
            [(_, userExisting), (_, friendshipExisting)] = returnList
            msg = {}
            msg['Response Type']='Friend Requested'
            msg['Friend Name']=o['Friend Name']
            if userExisting == [] or friendshipExisting != []:
              msg['Success']=False
              self.sendLine(json.dumps(msg))
            else:
              msg['Success']=True
              self.sendLine(json.dumps(msg))
              _addFriend()

          d1 = pool.runQuery("SELECT * FROM users WHERE UserName=E%s", o['Friend Name'])
          d2 = pool.runQuery("SELECT * FROM friends WHERE UserName=E%s AND FriendName=E%s", (self.username, o['Friend Name']))
          d = defer.DeferredList([d1, d2])
          d.addCallback(_checkIfShouldFriend)

        return "Added a friend!"

      def parseAcceptFriend(o):
        if self.username != None:
          pool.runOperation("UPDATE friends SET status='Accepted' WHERE (username=E%s AND friendname=E%s AND status='Unaccepted') OR (username=E%s AND friendname=E%s AND status='Pending')",
          (self.username, o['Friend Name'], o['Friend Name'], self.username))
          msg = {}
          msg['Response Type']='Friend Accepted'
          msg['Friend Name']=self.username
          self.sendToOthers(json.dumps(msg), [o['Friend Name']], save = True)

        return "Accepted a friend!"

      def parseRemoveFriend(o):
        if self.username != None:
          pool.runOperation("DELETE FROM friends WHERE (username=E%s AND friendname=E%s) OR (username=E%s AND friendname=E%s)",
          (self.username, o['Friend Name'], o['Friend Name'], self.username))
          msg = {}
          msg['Response Type']='Friend Removed'
          msg['Friend Name']=self.username
          self.sendToOthers(json.dumps(msg), [o['Friend Name']], save = True)

        return "Removed a friend!"

      def parseRefreshFriends(o):
        if self.username != None:
          def _compileFriends(flist):
            #Separate friend name and status by comma. Separate each entry by period
            msg = {}
            msg['Response Type']='Friend List'
            msg['Friend List']=flist
            self.sendLine(json.dumps(msg))

          pool.runQuery("SELECT FriendName, Status, lastloc FROM users INNER JOIN friends ON users.username=friends.username WHERE users.username=E%s", self.username).addCallback(_compileFriends)
        return "Sent back friends list!"

      def parseUpdateCoord(o):
        if self.username != None:

          pool.runOperation("UPDATE users SET lastloc=point(%f, %f) WHERE UserName=E%s",
          (o['Lat'], o['Lon'], self.username))

          def _pushPosition(friends, action, lat, lon, text):
            msg = {}
            msg['Response Type']='Position Update'
            msg['User Name']=self.username
            msg['Action']=action
            #Either way you can see the text of the zone
            msg['Text']=text

            if action == 'SHOWGPS':
              msg['Lat']=lat
              msg['Lon']=lon

            sentLine = json.dumps(msg)

            for a in friends:
              friend = a[0]
              try:
                self.factory.loggedin_users[friend].sendLine(sentLine)
              except:
                pass
                #print "Friend", friend, "is not logged in."

          def _checkPrivacy(zone):
            '''
            WARNING: The Android client will need to accept responses of either
            1) GPS coordinates
            2) text containing zone name
            3) response indicating location is hidden/protected.
            '''

            try:
              text = zone[0][1]
              action = zone[0][1]
            except:
              text = ""
              #TODO: Have master setting in users table
              action = "SHOWGPS"

            if action != 'HIDE':
              pool.runQuery("SELECT FriendName FROM friends WHERE UserName=E%s AND Status='Accepted'", self.username).addCallback(_pushPosition, action, o['Lat'], o['Lon'], text)

          pool.runQuery("SELECT zonename, action FROM zonenames WHERE UserName=E%s AND point(%f, %f) <@ zone LIMIT 1", (self.username, o['Lat'], o['Lon'])).addCallback(_checkPrivacy)



        return "Updated position!"

      def parseGetSaved(o):
        if self.username != None:
          def _sendMessages(messages):
            for m in messages:
              self.sendLine(m[0])
            pool.runOperation("DELETE FROM savedmessages WHERE UserName=E%s", self.username)
          pool.runQuery("SELECT message FROM savedmessages WHERE UserName=E%s", self.username).addCallback(_sendMessages)

      def parseLogin(o):

        def login():
          if self.username != None:
            self.logout()
          self.username = o['User Name']
          self.factory.loginUser(o['User Name'], self)
          print "User name is now ", self.username

        def validateUser(users):
          msg = {}
          msg['Response Type']='User Validation'
          #Invalid user would return empty list
          if users != []:
            msg['Success']=True
            self.sendLine(json.dumps(msg))
            login()
          else:
            msg['Success']=False
            self.sendLine(json.dumps(msg))

        if o['User Name'] in self.factory.loggedin_users:
          #Only one person can be signed in as you at a time
          msg = {}
          msg['Response Type']='User Validation'
          msg['Success']=False
          self.sendLine(json.dumps(msg))
        else:
          pool.runQuery("SELECT * FROM users WHERE UserName=E%s AND Password=E%s", (o['User Name'], o['Password'])).addCallback(validateUser)

        return "Logged in!"

      def parseLogout(o):
        self.logout()
        return "Logged out!"

      parser = {'Create User': parseCreateUser, 'Remove User': parseRemoveUser,
      'Add Zone': parseAddZone, 'Remove Zone': parseRemoveZone, 'Add Friend':
      parseAddFriend, 'Accept Friend': parseAcceptFriend, 'Remove Friend':
      parseRemoveFriend, 'Update Coordinate': parseUpdateCoord, 'Login': parseLogin,
      'Refresh Friends': parseRefreshFriends, 'Logout': parseLogout, 'Get Saved': parseGetSaved}

      if self.ALLOW_DEBUG_JSON:
        def parseShowUsers(o):
          pool.runQuery("SELECT * FROM users").addCallback(writeResponse)

        def parseShowFriends(o):
          pool.runQuery("SELECT * FROM friends").addCallback(writeResponse)

        def parseShowZones(o):
          pool.runQuery("SELECT * FROM zonenames").addCallback(writeResponse)

        #add functions here.
        def parseEmptyFriends(o):
          pool.runOperation("TRUNCATE TABLE friends")
          #NOTE: If any other tables reference this table using a foreign key, this will not work.
          #Instead, use DELETE.
          return "Empty Friends"

        def parseEmptyUsers(o):
          pool.runOperation("TRUNCATE TABLE users")
          #Same note as above.
          return "Empty Users"

        def parseEmptyZones(o):
          pool.runOperation("TRUNCATE TABLE zonenames")
          #Same note as above.
          return "Empty Zones"

        def parseEmptyAll(o):
          parseEmptyFriends(o)
          parseEmptyUsers(o)
          parseEmptyZones(o)
          return "Empty All"

        test_parser = {'Show Users' : parseShowUsers, 'Show Friends' : parseShowFriends,
                       'Show Zones': parseShowZones, 'Empty Friends': parseEmptyFriends,
                       'Empty Users': parseEmptyUsers, 'Empty Zones': parseEmptyUsers,
                       'Empty All' : parseEmptyAll}
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
        print "Invalid query. Handle this appropriately!"

class gogodeXFactory(protocol.ServerFactory):
  protocol = gogodeXProtocol

  loggedin_users = {}

  def loginUser(self, username, protocolInstance):
    self.loggedin_users[username] = protocolInstance

  def logoutUser(self, username):
    try:
      del self.loggedin_users[username]
    except KeyError:
      print "Attempted to logout a user not stored as logged in."
      print "This is an error but not worth crashing the server over."

