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
      print "Reactor claims to be done."

  def __init__(self):

    #pgasync's transactions are fickle.
    def writeResponse(message):
      print message

    def dropTables():
      d = defer.Deferred()
      def _dropTa(ignore):
        print "Removing any old tables"
        pool.runOperation("DROP TABLE IF EXISTS users")
        pool.runOperation("DROP TABLE IF EXISTS zonenames")
        pool.runOperation("DROP TABLE IF EXISTS friends")
      d.addCallback(_dropTa)
      d.addErrback(writeResponse)
      return d

    def dropTypes():
      d = defer.Deferred()
      def _dropTy(ignore):
        print "Removing any old types"
        pool.runOperation("DROP TYPE IF EXISTS friend_status")
        pool.runOperation("DROP TYPE IF EXISTS account_type")
        pool.runOperation("DROP TYPE IF EXISTS zone_action")
      d.addCallback(_dropTy)
      d.addErrback(writeResponse)
      return d

    def createTypes():
      d = defer.Deferred()
      def _createType(ignore):
        print "Creating types"
        pool.runOperation("CREATE TYPE friend_status AS ENUM ('Accepted', 'Unaccepted', 'Pending')")
        pool.runOperation("CREATE TYPE account_type AS ENUM ('User')")
        pool.runOperation("CREATE TYPE zone_action AS ENUM ('SHOWGPS', 'SHOWTEXT', 'HIDE')")
      d.addCallback(_createType)
      d.addErrback(writeResponse)
      return d

    def createTables():
      d = defer.Deferred()
      def _createTable(ignore):
        print "Creating tables"

        ### BUG ### Could not get setup script to work with primary key
        pool.runOperation("CREATE TABLE friends (\
        username  varchar(50) PRIMARY KEY,\
        friendname  varchar(50) NOT NULL,\
        status    friend_status NOT NULL\
        )")
        pool.runOperation("CREATE TABLE users (\
        firstname  varchar(50),\
        lastname  varchar(50),\
        UserName  varchar(50) PRIMARY KEY,\
        Password  varchar(50),\
        accounttype  account_type,\
        lastloc    point\
        )")
        pool.runOperation("CREATE TABLE zonenames (\
        UserName  varchar(50) NOT NULL,\
        ZoneName  varchar(255) NOT NULL,\
        zone    circle NOT NULL,\
        action      zone_action NOT NULL,\
        text        varchar(255)\
        )")
      d.addCallback(_createTable)
      d.addErrback(writeResponse)
      return d

    d0 = dropTables()
    d1 = dropTypes()
    d2 = createTypes()
    d3 = createTables()

    #Lazy scheduling
    d0.callback("One")
    reactor.callLater(2, d1.callback, "Two")
    reactor.callLater(4, d2.callback, "Three")
    reactor.callLater(6, d3.callback, "Four")

    reactor.callLater(10, self.endProcess)


myProcess = StupidProtcol()
reactor.spawnProcess(myProcess, 'mine')
reactor.run()

print "Done."
