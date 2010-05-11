from pgasync import ConnectionPool
import json

import glue

class gogodeXProtocol(glue.NeutralLineReceiver):
  def __init__(self):
    self.pool = ConnectionPool("pgasync",dbname="mydb",user="jake",password="stupidpassword")

  def connectionMade(self):
    self.sendLine("Connection made!")

  def neutralLineReceived(self, line):
    print "Received query for: "+line

    def onError(err):
      return 'Internal error in server'

    def writeResponse(message):
      if message != []:
        self.sendLine(message[0][0])
      else:
        self.sendLine('User not found.')

    def runQueries(cur,query,args):
      cur.execute(query,args)
      d = cur.fetchall()
      d.addCallback(writeResponse)
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
        self.pool.runOperation("INSERT INTO zonenames VALUES (E%s, E%s, %f, %f, %f)",
        (o['User Name'], o['Zone Name'], o['Lat'], o['Lon'], o['Radius']))
        return "Added a zone!"

      def parseRemoveZone(o):
        self.pool.runOperation("DELETE FROM zonenames WHERE ZoneName=E%s", o['Zone Name'])
        return "Removed a zone!"

      def parseAddFriend(o):
        #This will be replaced by ryan
        #Horribly inefficient
        self.pool.runOperation("INSERT INTO friends VALUES (E%s, E%s, 'Unaccepted')", (o['User Name'], o['Friend Name']))
        self.pool.runOperation("INSERT INTO friends VALUES (E%s, E%s, 'Unaccepted')", (o['Friend Name'], o['User Name']))
        return "Added a friend!"

      def parseAcceptFriend(o):
        self.pool.runOperation("UPDATE friends SET status='Accepted' WHERE (username=E%s AND friendname=E%s) OR (username=E%s AND friendname=E%s)",
        (o['User Name'], o['Friend Name'], o['Friend Name'], o['User Name']))
        return "Accepted a friend!"

      def parseRemoveFriend(o):
        self.pool.runOperation("DELETE FROM friends WHERE (username=E%s AND friendname=E%s) OR (username=E%s AND friendname=E%s)",
        (o['User Name'], o['Friend Name'], o['Friend Name'], o['User Name']))
        return "Removed a friend!"

      def parseUpdateCoord(o):
        self.pool.runOperation("UPDATE users SET lat=%f, lon=%f WHERE username=E%s AND password=E%s",
        (o['Lat'], o['Lon'], o['User Name'], o['Password']))
        return "Updated position!"

      parser = {'Create User': parseCreateUser, 'Remove User': parseRemoveUser,
      'Add Zone': parseAddZone, 'Remove Zone': parseRemoveZone, 'Add Friend':
      parseAddFriend, 'Accept Friend': parseAcceptFriend, 'Remove Friend':
      parseRemoveFriend, 'Update Coordinate': parseUpdateCoord}

      jd = json.JSONDecoder()
      obj = jd.decode(message)

      return parser[obj['Request Type']](obj)

    try:
      self.sendLine(getQuery(line))
    except (ValueError, KeyError):
      self.sendLine("Invalid query. Handle this appropriatly!")


    #NOTE: The E in name=E%s is for the funny way postgres handles backslash
    #self.pool.runInteraction(runQueries,"SELECT mystr FROM people WHERE name=E%s", line)
