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

  def sendToOther(self, msg, name):
    try:
      self.factory.loggedin_users[name].sendLine(msg)
    except KeyError:
      pass

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
          def _addZoneIfUnique(zonesExisting):
            msg = {}
            msg['Response Type']='Zone Added'
            msg['Zone Name']=o['Zone Name']
            msg['Lat']=o['Lat']
            msg['Lon']=o['Lon']
            msg['Radius']=o['Radius']
            msg['Action']=o['Action']
            msg['Text']=o['Text']
            if zonesExisting == []:
              pool.runOperation("INSERT INTO zonenames VALUES (E%s, E%s, circle '(( %f, %f ), %f)', E%s, E%s)",
              (self.username, o['Zone Name'], o['Lat'], o['Lon'], o['Radius'], o['Action'], o['Text']))
              msg['Success']=True
              self.sendLine(json.dumps(msg))
            else:
              msg['Success']=False
              self.sendLine(json.dumps(msg))

          pool.runQuery("SELECT zonename FROM zonenames WHERE username=E%s AND zonename=E%s LIMIT 1", (self.username, o['Zone Name'])).addCallback(_addZoneIfUnique)

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
            self.sendToOther(json.dumps(msg), o['Friend Name'])

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
        def _sendAcceptWithLocation(loc):
          s = loc[0][0]
          (lat, _, lon) = s.replace('(','').replace(')','').partition(',')
          msg = {}
          msg['Response Type']='Friend Accepted'
          msg['Friend Name']=self.username
          msg['Lat']=float(lat)
          msg['Lon']=float(lon)
          self.sendToOther(json.dumps(msg), o['Friend Name'])
          parseRequestPosition(o)

        if self.username != None:
          pool.runOperation("UPDATE friends SET status='Accepted' WHERE (username=E%s AND friendname=E%s AND status='Unaccepted') OR (username=E%s AND friendname=E%s AND status='Pending')",
          (self.username, o['Friend Name'], o['Friend Name'], self.username))
          pool.runQuery("SELECT lastloc FROM users WHERE UserName=E%s", self.username).addCallback(_sendAcceptWithLocation)


        return "Accepted a friend!"

      def parseRemoveFriend(o):
        if self.username != None:
          pool.runOperation("DELETE FROM friends WHERE (username=E%s AND friendname=E%s) OR (username=E%s AND friendname=E%s)",
          (self.username, o['Friend Name'], o['Friend Name'], self.username))
          msg = {}
          msg['Response Type']='Friend Removed'
          msg['Friend Name']=self.username
          self.sendToOther(json.dumps(msg), o['Friend Name'])

        return "Removed a friend!"

      def parseRefreshFriends(o):
        if self.username != None:
          def _compileFriends(flist):
            def reformatCoord(list):
              s = list[2]
              (lat, _, lon) = s.replace('(','').replace(')','').partition(',')
              return [list[0], list[1], float(lat), float(lon)]
            def flipFriendPerspective(list):
              #Since join has to be done from friend's perspective to get their location
              #we need to flip the relationship status (this should be faster than two queries)
              #Also enforces privacy
              if list[1] == 'Unaccepted':
                list[1] = 'Pending'
                list[2] = 0
                list[3] = 0
              elif list[1] == 'Pending':
                list[1] = 'Unaccepted'
                list[2] = 0
                list[3] = 0
              return list
            msg = {}
            msg['Response Type']='Friend List'
            msg['Friend List']=map(flipFriendPerspective, map(reformatCoord, flist))
            self.sendLine(json.dumps(msg))

          pool.runQuery("select users.username, status, lastloc from users inner join friends on users.username=friends.username where friends.friendname=E%s", self.username).addCallback(_compileFriends)
        return "Sent back friends list!"

      def parseRefresh(o):
        if self.username != None:
          def _sendFriendsAndZones(returnList):
            [(_, flist), (_, zonelist)] = returnList
            def reformatCoord(list):
              s = list[2]
              (lat, _, lon) = s.replace('(','').replace(')','').partition(',')
              return [list[0], list[1], float(lat), float(lon)]
            def flipFriendPerspective(list):
              #Since join has to be done from friend's perspective to get their location
              #we need to flip the relationship status (this should be faster than two queries)
              #Also enforces privacy
              if list[1] == 'Unaccepted':
                list[1] = 'Pending'
                list[2] = 0
                list[3] = 0
              elif list[1] == 'Pending':
                list[1] = 'Unaccepted'
                list[2] = 0
                list[3] = 0
              return list
            def _reformatLocation(list):
              loc = list[-1]
              reform = loc.replace('<','').replace('>','').replace('(','').replace(')','').split(',', 2)
              ret = list[0:-1]
              ret.extend(map(float, reform))
              return ret
            msg = {}
            msg['Response Type']='Refresh List'
            msg['Friend List']=map(flipFriendPerspective, map(reformatCoord, flist))
            msg['Zone List']=map(_reformatLocation, zonelist)
            self.sendLine(json.dumps(msg))


          d1 = pool.runQuery("select users.username, status, lastloc from users inner join friends on users.username=friends.username where friends.friendname=E%s", self.username)
          d2 = pool.runQuery("select zonename, action, text, zone from zonenames where username=E%s", self.username)
          d = defer.DeferredList([d1, d2])
          d.addCallback(_sendFriendsAndZones)

      def parseRefreshZones(o):
        if self.username != None:
          def _reformatLocation(list):
            loc = list[-1]
            reform = loc.replace('<','').replace('>','').replace('(','').replace(')','').split(',', 2)
            ret = list[0:-1]
            ret.extend(map(float, reform))
            return ret

          def _sendZones(zonelist):
            msg = {}
            msg['Response Type']='Zone List'
            msg['Zone List']=map(_reformatLocation, zonelist)
            self.sendLine(json.dumps(msg))
          pool.runQuery("select zonename, action, text, zone from zonenames where username=E%s", self.username).addCallback(_sendZones)

      def parseRequestPosition(o): #This is called by accept friend, so does not check accepted status. If this were to be made stand-alone that should be changed
        if self.username != None:
          msg = {}
          msg['Response Type']='Position Update'
          msg['User Name']=o['Friend Name']
          def _checkPrivacy(zone):
            try:
              text = zone[0][0]
              action = zone[0][1]
            except:
              text = ""
              #TODO: Have master setting in users table
              action = "SHOWGPS"
            if action != 'HIDE':
              msg['Action'] = action
              msg['Text'] = text
              self.sendLine(json.dumps(msg))

          def _confirmFriendship(returnList):
            [(_, friendShip), (_, location)] = returnList
            if friendShip != []: #If i am actually their friend
              (lat, _, lon) = location[0][0].replace('(','').replace(')','').partition(',')
              lat = float(lat)
              lon = float(lon)
              msg['Lat'] = lat
              msg['Lon'] = lon
              pool.runQuery("SELECT zonename, action FROM zonenames WHERE UserName=E%s AND point(%f, %f) <@ zone LIMIT 1", (o['Friend Name'], lat, lon)).addCallback(_checkPrivacy)

          print "User name is "+self.username
          print "Friend name is"+o['Friend Name']
          d1 = pool.runQuery("select friendname from friends where username=E%s and friendname=E%s limit 1", (self.username, o['Friend Name']))
          d2 = pool.runQuery("select lastloc from users where username=E%s limit 1", o['Friend Name'])
          d = defer.DeferredList([d1, d2])
          print "Adding callback"
          d.addCallback(_confirmFriendship)
          return "Requested Position!"

      def parseUpdateCoord(o):
        if self.username != None:

          pool.runOperation("UPDATE users SET lastloc=point '(%f, %f)' WHERE UserName=E%s",
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
              text = zone[0][0]
              action = zone[0][1]
            except:
              text = ""
              #TODO: Have master setting in users table
              action = "SHOWGPS"

            if action != 'HIDE':
              pool.runQuery("SELECT FriendName FROM friends WHERE UserName=E%s AND Status='Accepted'", self.username).addCallback(_pushPosition, action, o['Lat'], o['Lon'], text)

          pool.runQuery("SELECT zonename, action FROM zonenames WHERE UserName=E%s AND point(%f, %f) <@ zone LIMIT 1", (self.username, o['Lat'], o['Lon'])).addCallback(_checkPrivacy)

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
      'Refresh Friends': parseRefreshFriends, 'Logout': parseLogout, 'Refresh Zones':
      parseRefreshZones, 'Refresh': parseRefresh}

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

