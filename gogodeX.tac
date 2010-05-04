#Save in gogodeX.tac
#Run with twistd -ny gogodeX.tac as root.
#Don't worry, it drops permissions it does not need.

#Liscencse will go here

from twisted.application import internet, service
from twisted.internet import protocol, reactor, defer

import sys
sys.path.append('/home/jake/Documents/WhereAreYou')
import backend

class gogodeXFactory(protocol.ServerFactory):
  protocol = backend.gogodeXProtocol

application = service.Application('gogodeX', uid=1, gid=1)
factory = gogodeXFactory()
internet.TCPServer(79, factory).setServiceParent(
  service.IServiceCollection(application))
