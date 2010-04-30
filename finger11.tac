# Read username, output from non-empty factory, drop connections
# Use deferreds, to minimize synchronicity assumptions
# Write application. Save in 'finger.tpy' <- .tac also works

from pgasync import ConnectionPool
from twisted.application import internet, service
from twisted.internet import protocol, reactor, defer
from twisted.protocols import basic

class FingerProtocol(basic.LineReceiver):
  #Manually specify the delimeter in the code so that
  #you don't forget that it is important. Note this
  #must be the same as what is used in the client.
  delimiter = '\n'

  def connectionMade(self):
    self.transport.write("Connection made!\r\n")
    print "Connection made"

  def lineReceived(self, user):
    user = user.strip()
    print "Received query for: "+user

    def onError(err):
      return 'Internal error in server'

    def writeResponse(message):
      if message != []:
        print message[0][0]
        self.transport.write(message[0][0] + '\r\n')
        #self.transport.loseConnection()
      else:
        print "User not found."
        self.transport.write('User not found.\r\n')

    def runQueries(cur,query,args):
      cur.execute(query,args)
      d = cur.fetchall()
      d.addCallback(writeResponse)
      d.addErrback(onError)

    pool.runInteraction(runQueries,"SELECT mystr FROM people WHERE name='"+user+"'")

class FingerFactory(protocol.ServerFactory):
  protocol = FingerProtocol

  def __init__(self, **kwargs):
    self.users = kwargs

#Change this based on how you have set up postgres
pool = ConnectionPool("pgasync",dbname="mydb",user="jake",password="stupidpassword")

application = service.Application('finger', uid=1, gid=1)
factory = FingerFactory(moshez='Happy and well')
internet.TCPServer(79, factory).setServiceParent(
  service.IServiceCollection(application))
