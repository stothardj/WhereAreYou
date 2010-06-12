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

import org.json.JSONException;
import org.json.JSONStringer;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ZonesTabActivity extends ListActivity {
        private ArrayAdapter<String> zone_list;
        private Messenger mSender;
        private double mLat, mLon;
        private String mZoneName;

        // Override the back button behavior, we don't want this to do anything
    public void onBackPressed  (){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zones);

        zone_list = new  ArrayAdapter<String>(this, R.layout.zones_rows, R.id.zone_row);
            setListAdapter(zone_list);

            Button add = (Button) findViewById(R.id.add_zone);
            add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                                //Creating a zone receives no confirmation since there is no logical reason to stop it
                                //We do allow duplicate zone names. All will be removed at the same time.
                                String name = ((EditText) findViewById(R.id.zone_name_text)).getText().toString();
                                double mileRadius = Double.parseDouble(((EditText) findViewById(R.id.zone_radius_text)).getText().toString());
                                if(mileRadius > 0) {

                                        //Approximate, does not account for earth curvature
                                        final double degPerMile = .0144569;
                                        RadioGroup r = ((RadioGroup) findViewById(R.id.zone_name_privacy));
                                        int id = r.getCheckedRadioButtonId();
                                        RadioButton rb = (RadioButton) findViewById(id);
                                        String chosen = rb.getText().toString().toUpperCase();
                                        //zone_list.add(name + "\t\t" + chosen);
                                JSONStringer zoneAdd = new JSONStringer();
                                try
                                {
                                        zoneAdd.object();
                                        zoneAdd.key("Zone Name").value(name);
                                        zoneAdd.key("Lat").value(mLat);
                                        zoneAdd.key("Lon").value(mLon);
                                        zoneAdd.key("Radius").value(mileRadius * degPerMile);
                                        zoneAdd.key("Action").value(chosen);
                                        zoneAdd.key("Text").value(name);
                                        zoneAdd.key("Request Type").value("Add Zone");
                                        zoneAdd.endObject();
                                                GUI.getClient().sendLine(zoneAdd.toString());
                                        }
                                catch (JSONException e1)
                                        {
                                        e1.printStackTrace();
                                        }

                                } else {
                                Context context = getApplicationContext();
                                int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, "Please use a positive zone radius.", duration);
                        toast.show();
                                }
                        }
            });

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
                        } else if(msgType.equals("Zone List")) {
                                Zone[] zarray = (Zone[]) b.getSerializable("Zone Array");
                                for(Zone z : zarray) {
                                        zone_list.add(z.getName() + "\t\t" + z.getAction());
                                }
                        } else if(msgType.equals("Current Position")) {
                                mLat = b.getDouble("Lat");
                                mLon = b.getDouble("Lon");
                        } else if(msgType.equals("Zone Added")) {
                                boolean success = b.getBoolean("Success");
                                if(success) {
                                        zone_list.add(b.getString("Zone Name") + "\t\t" + b.getString("Action"));
                                } else {
                                Context context = getApplicationContext();
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, "Cannot add "+b.getString("Zone Name"), duration);
                                toast.show();
                                }
                        }
                }
        };
        final Messenger mReceiver = new Messenger(mHandler);
                ServiceConnection conn = new ServiceConnection() {
                        @Override
                        public void onServiceDisconnected(ComponentName name) {}
                        @Override
                        public void onServiceConnected(ComponentName name, IBinder service) {
                                mSender = new Messenger(service);
                                try {
                                        Message mess = Message.obtain();
                                        Bundle b = new Bundle();
                                        b.putString("Message Type", "Pass Messenger");
                                        b.putString("whoami", "Zones");
                                        b.putParcelable("messenger", mReceiver);
                                        mess.setData(b);
                                        mSender.send(mess);
                                } catch (RemoteException e) {
                                        e.printStackTrace();
                                }
                        }
                };
                Intent mIntent =  new Intent(ZonesTabActivity.this, GPSUpdater.class);
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
                        b.putString("whoami", "Zones");
                        mess.setData(b);
                        try {
                                mSender.send(mess);
                        } catch (RemoteException e) {
                                e.printStackTrace();
                        }
                }
        }

        protected void onListItemClick(ListView l, View v, int position, long id) {
                int row = position;
                String name = zone_list.getItem(row);
                String[] possibleEnds = {"SHOWTEXT", "SHOWGPS", "HIDE"};
                for(String end : possibleEnds) {
                        if(name.endsWith("\t\t"+end))
                                name = name.substring(0, name.length() - new String("\t\t" + end).length());
                }
                mZoneName = name;
                ((EditText)findViewById(R.id.zone_name_text)).setText(name);

                Button remove = (Button) findViewById(R.id.remove_zone);
                remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                //Remove all zones with this name!
                                String[] possibleEnds = {"SHOWTEXT", "SHOWGPS", "HIDE"};

                                for(String end : possibleEnds)
                                        zone_list.remove(mZoneName+"\t\t"+end);

                        JSONStringer zoneRemove = new JSONStringer();
                        try
                        {
                                zoneRemove.object();
                                zoneRemove.key("Zone Name").value(mZoneName);
                                zoneRemove.key("Request Type").value("Remove Zone");
                                zoneRemove.endObject();
                                        GUI.getClient().sendLine(zoneRemove.toString());
                                }
                        catch (JSONException e1)
                                {
                                e1.printStackTrace();
                                }

                        Message mess = Message.obtain();
                        Bundle b = new Bundle();
                        b.putString("Message Type", "Remove Zone");
                        b.putString("Zone Name", mZoneName);
                        mess.setData(b);
                        try {
                                        mSender.send(mess);
                                } catch (RemoteException e) {
                                        e.printStackTrace();
                                }
                        }
                });
        }

}
