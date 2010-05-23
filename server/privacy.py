'''
privacy.py

Methods in this file  are used to query various privacy zone preferences.
'''

from privacyAction import * 

def checkZonePrivacy(self, point):
    '''
    method sentry

    INPUT:
    self    An object of type gododeXprotocol. 

    Checks the transmitted GPS coordinate against the 
    logged in user's privacy zone settings.
    '''
    def getZoneOfLocation(cur,user,pt):
        cur.execute('SELECT zonename, action FROM zonenames WHERE UserName=E%s AND point (%f, %f) <@ zone LIMIT 1', (user, pt[0], pt[1])))
        d = cur.fetchall()
        return d[0]
    response = self.pool.runInteraction(getZoneOfLocation, self.username, point)
    lat = lon = 0.0
    text = ""
    if response[0] == "SHOWGPS":
        action = SHOWGPS
        lat = point[0]
        lon = point[1]
    elif response[0] == "SHOWTEXT":
        action = SHOWTEXT
        text = response[1]
    elif response[0] == "HIDE":
        pass
    else:
        pass #Should raise an exception.
    return action, text, lat, lon
    
   
    

