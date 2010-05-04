from twisted.protocols import basic
from pgasync import ConnectionPool

class NeutralLineReceiver(basic.LineReceiver):
  #Line receiver which does it's best to hide
  #the difference between \r\n and \n.
  delimiter = '\n'
  def lineReceived(self, line):
    #Do NOT overwrite this.
    if line[-1] == '\r':
      line = line[0:-1]
    return self.neutralLineReceived(line)
  def neutralLineReceived(self, line):
    #Overwrite this function instead of lineReceived
    raise NotImplementedError
  def sendLine(self, line):
    print line
    return self.transport.write(line + '\r\n')

class gogodeXProtocol(NeutralLineReceiver):
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

    #NOTE: The E in name=E%s is for the funny way postgres handles backslash
    self.pool.runInteraction(runQueries,"SELECT mystr FROM people WHERE name=E%s", user)
