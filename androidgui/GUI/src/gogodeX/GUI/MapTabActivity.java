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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import com.google.android.maps.*;

public class MapTabActivity extends MapActivity {
    /** Called when the activity is first created. */
        private static MapView mapView;
        private static Drawable icon;
        private static MyLocationOverlay myLocOverlay;
        private Messenger mSender;

        private class FriendLocation { //bag o' data
                public FriendLocation(Location location, String zoneText) { //convenience constructor
                        this.location = location;
                        this.zoneText = zoneText;
                }
                Location location;
                String zoneText;
        }

        HashMap<String, FriendLocation> friendLocations;

        // Override the back button behavior, we don't want this to do anything
    public void onBackPressed  (){}

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.maptabactivityview);

            icon = getResources().getDrawable(R.drawable.pin);

            // Get the current MapView
            mapView = (MapView) findViewById(R.id.mapview);

            // Enable Zoom
            mapView.setBuiltInZoomControls(true);

            // Enable my location icon
                myLocOverlay = new MyLocationOverlay(this, mapView);
                myLocOverlay.enableMyLocation();
                mapView.getOverlays().add(myLocOverlay);

                friendLocations = new HashMap<String, FriendLocation>();

        ////////////// Setup Two Way Communication ////////////////////
        final Handler mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                        Bundle b = msg.getData();
                        String msgType = b.getString("Message Type");
                        if(msgType.equals("Toast")) {
                                Context context = getApplicationContext();
                                int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, b.getString("Toast Message"), duration);
                        toast.show();
                        } else if(msgType.equals("Friend List")) {
                                String [] farray = b.getStringArray("Friend Names");
                                Location[] larray = (Location[]) b.getParcelableArray("Location Array");
                                int i=0;
                                for(String name : farray) {
                                        if(name==null) //don't add empty entries
                                                break;
                                        friendLocations.put(name, new FriendLocation(larray[i], null));
                                        i++;
                                }
                                createAndShowItemizedOverlay();
                        } else if(msgType.startsWith("Add Friend")) {
                                String name = b.getString("Friend Name");
                                Location loc = null;
                                if(msgType.endsWith("with Position")) {
                                        loc = b.getParcelable("Location");
                                }
                                friendLocations.put(name, new FriendLocation(loc, null));
                                if(loc!=null)
                                        createAndShowItemizedOverlay();
                        } else if(msgType.equals("Remove Friend")) {
                                        String name = b.getString("Friend Name");
                                        friendLocations.remove(name);
                                        createAndShowItemizedOverlay();
                        } else if(msgType.equals("Update Position")) {
                                        String name = b.getString("Friend Name");
                                        String action = b.getString("Zone Action");
                                        String zoneText = b.getString("Zone Text");
                                        if(action.equals("SHOWGPS")) {
                                                Location loc = b.getParcelable("Location");
                                                friendLocations.put(name, new FriendLocation(loc, zoneText));
                                        } else {
                                                //Just don't move them
                                                friendLocations.put(name, new FriendLocation(friendLocations.get(name).location, zoneText));
                                        }
                                        createAndShowItemizedOverlay();
                        }
                }
        };
        final Messenger mReceiver = new Messenger(mHandler);
                ServiceConnection conn = new ServiceConnection() {
                        @Override
                        public void onServiceConnected(ComponentName name, IBinder service) {
                                mSender = new Messenger(service);
                                try {
                                        Message mess = Message.obtain();
                                        Bundle b = new Bundle();
                                        b.putString("Message Type", "Pass Messenger");
                                        b.putString("whoami", "Map");
                                        b.putParcelable("messenger", mReceiver);
                                        mess.setData(b);
                                        mSender.send(mess);
                                } catch (RemoteException e) {
                                        e.printStackTrace();
                                }
                        }
                        @Override
                        public void onServiceDisconnected(ComponentName name) {}
                };
                Intent mIntent =  new Intent(MapTabActivity.this, GPSUpdater.class);
                this.getApplicationContext().bindService(mIntent, conn, 0);
                //////////////Setup Two Way Communication ////////////////////
        }

        @Override
        protected void onResume() {
                super.onResume();

                if(mSender != null) {
                        Message mess = Message.obtain();
                        Bundle b = new Bundle();
                        b.putString("Message Type", "Declare Active");
                        b.putString("whoami", "Map");
                        mess.setData(b);
                        try {
                                mSender.send(mess);
                        } catch (RemoteException e) {
                                e.printStackTrace();
                        }
                }
        }

        protected void createAndShowItemizedOverlay()
        {
                List overlays = mapView.getOverlays();

                // first remove old overlay
                if (overlays.size() > 0)
                {
                        for (int i = 0; i < overlays.size(); i++)
                        {
                                if(overlays.get(i) != myLocOverlay)
                                {
                                        overlays.remove(i);
                                        break;
                                }
                        }
                }

                GeoPoint geopoint = new GeoPoint(0,0);
                icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
                Location newLocation;

                if(!friendLocations.isEmpty()) {
                        BuddyOverlay overlay;
                        overlay = new BuddyOverlay(icon, getApplicationContext());
                        OverlayItem item;
                        for(String name : friendLocations.keySet())
                        {
                                FriendLocation floc = friendLocations.get(name);
                                Location loc = floc.location;
                                String zoneText = "";
                                if(floc.zoneText != null)
                                        zoneText = floc.zoneText;
                                if(loc != null) {
                                        // transform the location to a geopoint
                                        newLocation = new Location(loc);
                                        geopoint = new GeoPoint(
                                                        (int) (newLocation.getLatitude() * 1E6), (int) (newLocation
                                                                        .getLongitude() * 1E6));

                                        item = new OverlayItem(geopoint, name, zoneText);
                                        overlay.addItem(item);
                                }
                        }
                        mapView.getOverlays().add(overlay);
                        // move to location
                        mapView.getController().animateTo(geopoint);
                        // redraw map
                        mapView.postInvalidate();
                }
        }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}
