#Save in gogodeX.tac
#Run with twistd -ny gogodeX.tac as root.
#Don't worry, it drops permissions it does not need.

#Liscencse will go here

from twisted.application import internet, service
from twisted.internet import reactor, defer

import sys
import os
#Unfortunatly, the location of the backend module is hardcoded. Not anymore! :)
path = "/home/%s/Documents/WhereAreYou/server" % os.getlogin()
sys.path.append(path)
#sys.path.append('/home/ryan/Documents/WhereAreYou/server')
import backend

application = service.Application('gogodeX', uid=1, gid=1)
factory = backend.gogodeXFactory()
internet.TCPServer(79, factory).setServiceParent(
  service.IServiceCollection(application))
