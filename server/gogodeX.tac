#Copyright 2010 Jake Stothard, Brian Garfinkel, Adam Shwert, Hongchen Yu, Yijie Wang, Ryan Rosario, Jiho Kim

#   Licensed under the Apache License, Version 2.0 (the "License");
#   you may not use this file except in compliance with the License.
#   You may obtain a copy of the License at

#       http://www.apache.org/licenses/LICENSE-2.0

#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.

#Save in gogodeX.tac
#Run with twistd -ny gogodeX.tac as root.
#Don't worry, it drops permissions it does not need.

from twisted.application import internet, service
from twisted.internet import reactor, defer

import sys

path = '/home/jake/Documents/WhereAreYou/server'
sys.path.append(path)

import backend

application = service.Application('gogodeX', uid=1, gid=1)
factory = backend.gogodeXFactory()
internet.TCPServer(79, factory).setServiceParent(
  service.IServiceCollection(application))
