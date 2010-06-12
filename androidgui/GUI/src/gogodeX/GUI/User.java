/*
Copyright 2010 Jake Stothard, Brian Garfinkel, Adam Shwert, Hongchen Yu, Yijie Wang, Ryan Rosario, Jiho Kim

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package gogodeX.GUI;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import android.location.Location;

public class User {
        private String firstName;
        private String lastName;
        private String userName;
        private Location userLocation;
        private String validation;
        private String zoneText;

        public User(String uName, Location l, String validation)
        {
                setUserName(uName);
                userLocation = l;
                this.validation = validation;
        }

        public void setUserName(String userName) {
                this.userName = userName;
        }

        public String getUserName() {
                return userName;
        }
        public void setValidation(String validation) {
                this.validation = validation;
        }
        public String getValidation() {
                return validation;
        }

        public Location getLocation()
        {
                return userLocation;
        }

        public GeoPoint getGeoPoint()
        {
                Double lat = userLocation.getLatitude();
                Double lon = userLocation.getLongitude();
                lat = lat * 1000000;
                lon = lon * 1000000;
                return new GeoPoint(lat.intValue(), lon.intValue());
        }

        public void setLocation(Location l)
        {
                userLocation = l;
        }

        public void setZoneText(String zoneText) {
                this.zoneText = zoneText;
        }
        public String getZoneText(String zoneText) {
                return zoneText;
        }

}

