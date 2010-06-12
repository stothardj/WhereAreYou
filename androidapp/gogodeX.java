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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import android.os.Bundle;
import android.location.*;


public class gogodeX extends Activity {
         /** Called when the activity is first created. */
    private static TextView TV;
    private static LocationManager LM;
    private static GPSUpdater GPS;

@Override
public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    TV = new TextView(this);
    LM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    setContentView(TV);
    Intent svc = new Intent(this, GPSUpdater.class);
    startService(svc);
    GPSUpdater.setGogo(this);
    }

public static TextView getTV()
{
        return TV;
}

public static LocationManager getLM()
{
        return LM;
}

public static void setGPS(GPSUpdater GPS1)
{
        GPS = GPS1;
}

}
