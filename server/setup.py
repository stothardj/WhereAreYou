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

#This file changes the hardcoded values in the server.
#You should only have to run this once (per time you download the code)

import os
import shutil

print "Edit gogodeX.tac to reference current folder"
shutil.move('gogodeX.tac', 'gogodeX.tac~')
cd = os.path.abspath(os.curdir)
with open('gogodeX.tac~', 'r') as f1:
  with open('gogodeX.tac', 'w') as f2:
    for line in f1:
      if line.startswith('path'):
        f2.write("path = '"+cd+"'\n")
      else:
        f2.write(line)

import json

print "Edit server.conf to connect to correct database"
setobj = {}
setobj['Database'] = raw_input("Database name: ")
setobj['User'] = raw_input("Database user: ")
setobj['Password'] = raw_input("Database password: ")

shutil.move('server.conf', 'server.conf~')
with open('server.conf', 'w') as f:
  json.dump(setobj, f)


#All that follows is a beautiful hack
from twisted.internet import protocol, reactor, defer
from pgasync import ConnectionPool

print "Creating postgres tables in database"

pool = ConnectionPool("pgasync",dbname=str(setobj['Database']),user=str(setobj['User']),password=str(setobj['Password']))

class StupidProtcol(protocol.ProcessProtocol):

  def endProcess(self):
    print "Ending the process"
    try:
      reactor.stop()
    except:
      print "Cannot stop reactor. Just Ctrl+C it."

  def __init__(self):

    connection = pool.connect()
    cursor = connection.cursor()
    print "This script will only work if you have already run the following command in psql:"
    print "ALTER DATABASE "+setobj['Database']+" SET client_min_messages TO WARNING"
    cursor.execute("ALTER DATABASE "+setobj['Database']+" SET client_min_messages TO WARNING")

    cursor.execute("DROP TABLE IF EXISTS users")
    cursor.execute("DROP TABLE IF EXISTS zonenames")
    cursor.execute("DROP TABLE IF EXISTS friends")
    cursor.execute("DROP TABLE IF EXISTS savedmessages")

    cursor.execute("DROP TYPE IF EXISTS friend_status")
    cursor.execute("DROP TYPE IF EXISTS account_type")
    cursor.execute("DROP TYPE IF EXISTS zone_action")
    cursor.execute("CREATE TYPE friend_status AS ENUM ('Accepted', 'Unaccepted', 'Pending')")
    cursor.execute("CREATE TYPE account_type AS ENUM ('User')")
    cursor.execute("CREATE TYPE zone_action AS ENUM ('SHOWGPS', 'SHOWTEXT', 'HIDE')")
    cursor.execute("CREATE TABLE friends (\
    username  varchar(50) NOT NULL,\
    friendname  varchar(50) NOT NULL,\
    status    friend_status NOT NULL\
    )")
    cursor.execute("CREATE TABLE users (\
    firstname  varchar(50),\
    lastname  varchar(50),\
    UserName  varchar(50) PRIMARY KEY,\
    Password  varchar(50),\
    accounttype  account_type,\
    lastloc    point\
    )")
    cursor.execute("CREATE TABLE zonenames (\
    UserName  varchar(50) NOT NULL,\
    ZoneName  varchar(255) NOT NULL,\
    zone    circle NOT NULL,\
    action      zone_action NOT NULL,\
    text        varchar(255)\
    )")
    connection.commit()
    cursor.release()
    #Ugly way to probably avoid data race
    print "Wait 3 seconds."
    reactor.callLater(3, self.endProcess)


myProcess = StupidProtcol()
reactor.spawnProcess(myProcess, 'mine')
reactor.run()

print "Done."
