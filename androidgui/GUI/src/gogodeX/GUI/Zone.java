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

public class Zone {
        private String name, action, text;
        private double lat, lon;
        private double radius; //in degree distance to be checked (ie. not miles)
        final double degPerMile = .0144569;

        //Can only leave something out if you do it explicitly when you create it.
        //Since you can't update zones in the database (right now), editting a zone
        //object is discouraged
        public Zone(String name, double lat, double lon, double radius, String action, String text) {
                this.name = name;
                this.lat = lat;
                this.lon = lon;
                this.radius = radius;
                this.action = action;
                this.text = text;
        }

        public String getName() { return name; }
        public String getAction() { return action; }
        public String getText() { return text; }
        public double getLat() { return lat; }
        public double getLon() { return lon; }
        public double getRadiusInDegress() { return radius; }
        public double getRadiusInMiles() { return radius / degPerMile; }

}
