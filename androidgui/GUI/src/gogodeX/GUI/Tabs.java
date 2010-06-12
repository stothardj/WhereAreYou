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

import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.TabHost;

public class Tabs extends TabActivity {
    /** Called when the activity is first created. */
        private TabHost tabHost;
        private TabHost.TabSpec spec;
        private Intent intent;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setDefaultTab(0);

        tabHost = getTabHost(); // The activity TabHost

        // Create an Intent to launch an Activity for the tab
        intent = new Intent().setClass(this.getApplicationContext(), MapTabActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("map").setIndicator("Map",
                getResources().getDrawable(R.drawable.mapicon))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        /*
        intent = new Intent().setClass(this.getApplicationContext(), SettingsTabActivity.class);
        spec = tabHost.newTabSpec("setting").setIndicator("setting")
                      .setContent(intent);
        */
        intent = new Intent().setClass(this.getApplicationContext(), ZonesTabActivity.class);
        spec = tabHost.newTabSpec("zones").setIndicator("Zones",
                getResources().getDrawable(R.drawable.zoneicon)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this.getApplicationContext(), FriendsList.class);
        spec = tabHost.newTabSpec("friends list").setIndicator("Friends List",
                getResources().getDrawable(R.drawable.friendicon))
                                  .setContent(intent);
        tabHost.addTab(spec);

    }

}
