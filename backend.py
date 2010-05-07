from pgasync import ConnectionPool
import json

import glue

class gogodeXProtocol(glue.NeutralLineReceiver):
  def __init__(self):
    self.pool = ConnectionPool("pgasync",dbname="mydb",user="jake",password="stupidpassword")

  def connectionMade(self):
    self.sendLine("Connection made!")

  def neutralLineReceived(self, user):
    print "Received query for: "+user

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
        return ("INSERT INTO tablename VALUES (%s, %s, %s, %s %s)",
        o['First Name'], o['Last Name'], o['User Name'], o['Password'],
        o['Account Type'])
      def parseRemoveUser(o):
        return "Remove user"
      def parseAddZone(o):
        return "Add Zone"
      def parseRemoveZone(o):
        return "Remove Zone"
      def parseAddFriend(o):
        return "Add friend"
      def parseAcceptFriend(o):
        return "Accept friend"
      def parseRemoveFriend(o):
        return "Remove friend"
      def parseUpdateCoord(o):
        return "Update coord"

      parser = {'Create User': parseCreateUser, 'Remove User': parseRemoveUser,
      'Add Zone': parseAddZone, 'Remove Zone': parseRemoveZone, 'Add Friend':
      parseAddFriend, 'Accept Friend': parseRemoveFriend, 'Remove Friend':
      parseRemoveFriend, 'Update Coordinate': parseUpdateCoord}

      jd = json.JSONDecoder()
      obj = jd.decode(message)

      return parser[obj['Request Type']](obj)


    try:
      print getQuery(user)
    except (ValueError, KeyError):
      print "Invalid query recieved. Handle this appropriatly!"


    #NOTE: The E in name=E%s is for the funny way postgres handles backslash
    self.pool.runInteraction(runQueries,"SELECT mystr FROM people WHERE name=E%s", user)
