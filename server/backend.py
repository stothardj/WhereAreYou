from twisted.internet import protocol
from pgasync import ConnectionPool  #Postgres/Twisted interface.
from privacy import checkZonePrivacy
from privacyAction import *
import json

import glue
import os

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

class gogodeXProtocol(glue.NeutralLineReceiver):

  username = None

  #Set these to false for final
  ALLOW_DEBUG_JSON = True
  ALWAYS_RESPOND = False


  def __init__(self):
    server = os.getlogin()
    if server == "jake":
      db="mydb"
    elif server == "ryan":
      db="gogodex"
      global db
    #TO DO: Change database name, username and password. Password should be configured in a file.
    self.pool = ConnectionPool("pgasync",dbname=db,user=server,password="stupidpassword")

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

    def writeResponse(message):
      self.sendLine(str(message))

    #json message -> sql query. Tuple with parameters to be applied
    #separate so that pgasync can handle sql injection
    def getQuery(message):

      def parseCreateUser(o):
        '''
        users table schema
        fname - First Name, varchar(50)
        lname - Last Name, varchar(50)
        UserName - Username, varchar(50)
        pw - Password, varchar(50)
        type - Account Type, enum
        lastloc - Last Location (lat/lon), point
        '''

        def _createUser(users):
          msg = {}
          msg['Response Type']='Created User'
          msg['User Name']=o['User Name']
          if users == []:
            msg['Success'] = True
            self.sendLine(json.dumps(msg))
            self.pool.runOperation("INSERT INTO users VALUES (E%s, E%s, E%s, E%s, E%s, '(0.0, 0.0)')",
              (o['First Name'], o['Last Name'], o['User Name'], o['Password'],
              o['Account Type']))
          else:
            msg['Success'] = False
            self.sendLine(json.dumps(msg))
        self.pool.runQuery("SELECT * FROM users WHERE UserName=E%s", o['User Name']).addCallback(_createUser)

        return "Created a user!"

      def parseRemoveUser(o):
        if self.username != None:
          self.pool.runOperation("DELETE FROM users WHERE UserName=E%s", self.username)
          self.pool.runOperation("DELETE FROM friends WHERE UserName=E%s OR FriendName=E%s", (self.username, self.username))
          self.pool.runOperation("DELETE FROM zonenames WHERE UserName=E%s", self.username)
          self.logout()
        return "Removed a user!"

      def parseAddZone(o):
        #TO DO: Check for overlapping zones and fail if zones overlap.
        if self.username != None:
          #To RRR: the radius needs to be converted from user units (feet, miles etc.) to GPS. Sigh.
          self.pool.runOperation("INSERT INTO zonenames VALUES (E%s, E%s, circle '(( %f, %f ), %f)', E%s, E%s)",
          (self.username, o['Zone Name'], o['Lat'], o['Lon'], o['Radius'], o['Action'], o['Text']))

        return "Added a zone!"

      def parseRemoveZone(o):
        if self.username != None:
          self.pool.runOperation("DELETE FROM zonenames WHERE UserName=E%s AND ZoneName=E%s", (self.username, o['Zone Name']))

        return "Removed a zone!"

      def parseAddFriend(o):
        if self.username != None and self.username != o['Friend Name']:

          def _addFriend():
            self.pool.runOperation("INSERT INTO friends VALUES (E%s, E%s, 'Pending')", (self.username, o['Friend Name']))
            self.pool.runOperation("INSERT INTO friends VALUES (E%s, E%s, 'Unaccepted')", (o['Friend Name'], self.username))
            msg = {}
            msg['Response Type']='Friend Request'
            msg['From User']=self.username
            try:
              self.factory.loggedin_users[o['Friend Name']].sendLine(json.dumps(msg))
            except:
              pass

          def _checkFriendExists(users):
            msg = {}
            msg['Response Type']='Friend Requested'
            msg['Friend Name']=o['Friend Name']
            if users == []:
              msg['Success']=False
              self.sendLine(json.dumps(msg))
            else:
              msg['Success']=True
              self.sendLine(json.dumps(msg))

          self.pool.runQuery("SELECT * FROM users WHERE UserName=E%s", o['Friend Name']).addCallback(_checkFriendExists)

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
          self.pool.runOperation("UPDATE users SET lastloc=point(%f, %f) WHERE UserName=E%s",
          (o['Lat'], o['Lon'], self.username))

          def pushPosition(friends):
            '''
            WARNING: The Android client will need to accept responses of either
            1) GPS coordinates
            2) text containing zone name
            3) response indicating location is hidden/protected.
            '''
            msg = {}
            msg['Response Type']='Position Update'
            msg['User Name']=self.username
            #Is the user in a zone?
            action, text, lat, lon = privacy.checkZonePrivacy(self, (o['Lat'], o['Lon']))
            msg['Action']=action
            msg['Text']=text
            msg['Lat']=lat
            msg['Lon']=lon
            sentLine = json.dumps(msg)
            #If user is in a 'hidden' zone, save bandwidth and don't push.
            if action != HIDE:
              for a in friends:
                friend = a[0]
                try:
                  self.factory.loggedin_users[friend].sendLine(sentLine)
                except:
                  pass
                  #print "Friend", friend, "is not logged in."
            else:
              print "Action is hide."

          self.pool.runQuery("SELECT FriendName FROM friends WHERE UserName=E%s AND Status='Accepted'").addCallback(pushPosition)
          return "Updated position!"

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

          if users != []: #Invalid user would return empty list
            msg['Success']=True
            self.sendLine(json.dumps(msg))
            login()
          else:
            msg['Success']=False
            self.sendLine(json.dumps(msg))

        self.pool.runQuery("SELECT * FROM users WHERE UserName=E%s AND Password=E%s", (o['User Name'], o['Password'])).addCallback(validateUser)

        return "Logged in!"

      parser = {'Create User': parseCreateUser, 'Remove User': parseRemoveUser,
      'Add Zone': parseAddZone, 'Remove Zone': parseRemoveZone, 'Add Friend':
      parseAddFriend, 'Accept Friend': parseAcceptFriend, 'Remove Friend':
      parseRemoveFriend, 'Update Coordinate': parseUpdateCoord, 'Login': parseLogin}

      if self.ALLOW_DEBUG_JSON:
        def parseShowUsers(o):
          self.pool.runQuery("SELECT * FROM users").addCallback(writeResponse)

        def parseShowFriends(o):
          self.pool.runQuery("SELECT * FROM friends").addCallback(writeResponse)

        def parseShowZones(o):
          self.pool.runQuery("SELECT * FROM zonenames").addCallback(writeResponse)

        #add functions here.
        def parseEmptyFriends(o):
          self.pool.runOperation("TRUNCATE TABLE friends")
          #NOTE: If any other tables reference this table using a foreign key, this will not work.
          #Instead, use DELETE.
          return "Empty Friends"

        def parseEmptyUsers(o):
          self.pool.runOperation("TRUNCATE TABLE users")
          #Same note as above.
          return "Empty Users"

        def parseEmptyZones(o):
          self.pool.runOperation("TRUNCATE TABLE zonenames")
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
    del self.loggedin_users[username]

