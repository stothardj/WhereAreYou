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
