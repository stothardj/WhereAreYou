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

package com.gogodeX;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.widget.TextView;

public class messageHandler extends Thread {
        private JavaClient twistedClient;
        private String JSONString;
        private GPSUpdater GPS;
        private JSONObject tokenJSON;

        public messageHandler(JavaClient client, GPSUpdater currentGPS)
        {
                JSONString = "";
                twistedClient = client;
                GPS = currentGPS;
        }

        public void run()
        {
                this.waitForMessage();
        }

        public void waitForMessage()
        {
                try
                {
                        JSONString = twistedClient.readLine();
                }
                catch (IOException e)
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                parseJSON(JSONString);
        }

        private void parseJSON(String JSONString)
        {
                System.out.println(JSONString);
                try
                {
                        tokenJSON = new JSONObject(JSONString);
                }
                catch (JSONException e)
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }

                try
                {
                        if (tokenJSON.get("Request Type") == "Coordinate Update")
                        {
                                double latitude = tokenJSON.getDouble("Lat");
                                double longitude = tokenJSON.getDouble("Lon");
                                String userName = tokenJSON.getString("User Name");
                                System.out.println(userName + latitude + longitude);

                        }
                }
                catch (JSONException e)
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                JSONString = "";
                this.waitForMessage();
        }


}
