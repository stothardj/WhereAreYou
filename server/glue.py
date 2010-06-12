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

#This is code not related to the logic of gogodeX.
#It is hidden away glue to make gogodeX logic look
#simple while realistically connecting with other
#services.

from twisted.protocols import basic

class NeutralLineReceiver(basic.LineReceiver):
  #Line receiver which does it's best to hide
  #the difference between \r\n and \n.
  delimiter = '\n'
  def lineReceived(self, line):
    #Do NOT overrride this.
    if line[-1] == '\r':
      line = line[0:-1]
    return self.neutralLineReceived(line)
  def neutralLineReceived(self, line):
    #Override this function instead of lineReceived
    raise NotImplementedError
  def sendLine(self, line):
    print line
    return self.transport.write(line + '\r\n')
