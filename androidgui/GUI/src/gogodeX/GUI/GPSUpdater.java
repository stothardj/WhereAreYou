package gogodeX.GUI;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.TextView;
import android.widget.Toast;

import org.json.*;


public class GPSUpdater extends Service {

	//LocationManager to update GPS coordinates
	private LocationManager LM = null;
	//TextView for debug printing
	private TextView TV = null;
	//JavaClient to connect to the twisted server
	private JavaClient client;
	//private RspHandler handler;
	private Thread t;
	//JSONStringer to parse a string into a json string
	private JSONStringer coordinates;
	//Holds the user's current Location
	private static Location location;
	//Variables for holding latitude and longitude values
	private static double latitude;
	private static double longitude;
	//Handler for handling incoming server messages
	private static String mocLocationProvider;
	private String JSONString;
	
	private String currentTab;
	private HashMap<String, Messenger> messengers;
	private HashMap<String, User> friends;
	
	@Override
	public void onCreate()
	{
		messengers = new HashMap<String, Messenger>();
		//Get the LocationManager and TextView from the main activity
		LM = (LocationManager) getSystemService(Context.LOCATION_SERVICE); 
		client = GUI.getClient();
		location = null;
		//GUI.getLM(LM);
		//Start the main GPS updating loop
		GPSHandler();
		retrieveFriends();
	}
	
	public void retrieveFriends(){
		friends = new HashMap<String, User>();
		JSONStringer refresh = new JSONStringer();
		try {
			refresh.object();
			refresh.key("Request Type").value("Refresh Friends");
	    	refresh.endObject();
	    	GUI.getClient().sendLine(refresh.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void waitForMessage()
	{
		String JSONString = "";
		while(JSONString.equals(""))
		{
			JSONString = GUI.getClient().readLine();
		}
		this.JSONString = JSONString;
	}
	
	private void GPSHandler()
	{
		//Obtain the constant string value for the GPS provider
	    mocLocationProvider = LocationManager.GPS_PROVIDER;
	    //Create a new mocLocationListener instance to handle coordinate updates
	    LocationListener mocLocation = new mocLocationListener(); 
	    //Initiate the updating of coordinates through the requestLocationUpdates method
	    LM.requestLocationUpdates(mocLocationProvider, 0, 0, mocLocation);
	    //Get the last known location of the device for the user's starting location
	    location = LM.getLastKnownLocation(mocLocationProvider);
	}
	
	//Class to handle GPS updates
	private class mocLocationListener implements LocationListener 
	{ 
	            public void onLocationChanged(Location L) { 
	            	//Set the user's location to the newly received location
	            	location = L;
	            	//MapTabActivity.onLocationChange(L);
	            	//Create a new JSONStringer to create a JSON string to send to the server
	            	coordinates = new JSONStringer();
	            		//As long as the new location was received
	                    if (L != null) 
	                    { 
	                    	//Get the latitude and longitude from the current user's location
	                    	latitude = L.getLatitude(); 
	                    	longitude = L.getLongitude();          
	                    	try 
	                    	{                		
	                    		//Create a new JSON object
	                    		coordinates.object();
	                    		//Add the keys needed for updating a user's coordinates
								coordinates.key("Lon").value(longitude);
		                    	coordinates.key("Lat").value(latitude);
		                    	coordinates.key("Request Type").value("Update Coordinate");
		                    	//End the JSON object
		                    	coordinates.endObject();
							} 
	                    	catch (JSONException e) 
	                    	{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	                    	//Send the parsed JSON update coordinates request
								client.sendLine(coordinates.toString());
	                    } 
	                    else 
	                    {
	                    	TV.append("Bad Provider\n"); 
	                    } 
	            } 
	            public void onProviderDisabled(String provider) { 
	                    // TODO Auto-generated method stub 
	            } 
	            public void onProviderEnabled(String provider) { 
	                    // TODO Auto-generated method stub 
	            } 
	            public void onStatusChanged(String provider, int status, Bundle extras) { 
	                    // TODO Auto-generated method stub 
	            } 
		}

	@Override
	public IBinder onBind(Intent intent) {

		final Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				//Listener of interprocess communication
				Bundle b = msg.getData();
				String msgType =  b.getString("Message Type");
				if(msgType.equals("Pass Messenger")) {
					String mWhoAmI = b.getString("whoami");
					Messenger mMessenger = (Messenger) b.get("messenger");
					messengers.put(mWhoAmI, mMessenger);
					currentTab = mWhoAmI;

				} else if(msgType.equals("Declare Active")) {
					currentTab = b.getString("whoami");
				} else if(msgType.equals("Accept Friend")) {
					friends.get(b.getString("Friend Name")).setValidation("Accepted");
				} else if(msgType.equals("Remove Friend")) {
					friends.remove(b.getString("Friend Name"));
				}
				
				//Send full friends list to friends tab once it is created
				if(msgType.equals("Pass Messenger") && currentTab.equals("Friends List")) {
					Message mess = Message.obtain();
					Bundle bo = new Bundle();
					bo.putString("Message Type", "Friend List");
					String[] farray = new String[friends.size()];
					int i = 0;
					for(String name : friends.keySet()) {
						User u = friends.get(name);
						String s = name;
						if(!u.getValidation().equals("Accepted"))
							s += "\t\t("+u.getValidation()+")";
						farray[i] = s;
						i++;
					}
					bo.putStringArray("Friend List", farray);
					mess.setData(bo);
					try {
						messengers.get(currentTab).send(mess);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				
				
				
				/*
				Message mess = Message.obtain();
				Bundle bo = new Bundle();
				bo.putString("Message Type", "Toast");
				bo.putString("Toast Message", "I think "+currentTab+" is the current tab.");
				mess.setData(bo);
				try {
					messengers.get(currentTab).send(mess);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				*/
				
			}
		};
		final Runnable mh = new Runnable() {
			public void run() {
				//Parse messages from server
				//By the time this is called the message is saved
				//in JSONString (cannot put 'this' since inside Runnable)
				
				try {
					JSONObject jo = new JSONObject(JSONString);
					String resT = jo.getString("Response Type");
					if(resT.equals("Friend List")) {
						JSONArray flist = jo.getJSONArray("Friend List");
						for(int i=0; i<flist.length(); i++) {
							JSONArray friend = flist.getJSONArray(i);
							String name = friend.getString(0);
							String status = friend.getString(1);
							double lat = friend.getDouble(2);
							double lon = friend.getDouble(3);
							Location loc =  new Location("gps");
							loc.setLatitude(lat);
							loc.setLongitude(lon);
							User user = new User(name, loc);
							user.setValidation(status);
							friends.put(name, user);
						}
					}
					else if(resT.equals("Friend Requested")) {
						if(jo.getBoolean("Success")) {
							if(messengers.containsKey("Friends List")) {
								Message mess = Message.obtain();
								Bundle bo = new Bundle();
								bo.putString("Message Type", "Friend Requested");
								bo.putString("Friend Name", jo.getString("Friend Name"));
								mess.setData(bo);
								messengers.get("Friends List").send(mess);
							}
						}
						else
						{
							Message mess = Message.obtain();
							Bundle bo = new Bundle();
							bo.putString("Message Type", "Toast");
							bo.putString("Toast Message", "Cannot request "+jo.getString("Friend Name"));
			        		mess.setData(bo);
			        		messengers.get(currentTab).send(mess);
						}
					} else if(resT.equals("Friend Accepted"))
					{
						String name = jo.getString("Friend Name");
						
						Message mess = Message.obtain();
						Bundle bo = new Bundle();
						bo.putString("Message Type", "Toast");
						bo.putString("Toast Message", name + " accepted your friend request.");
		        		mess.setData(bo);
		        		messengers.get(currentTab).send(mess);
		        		
		        		Location loc = new Location("gps");
		        		loc.setLatitude(jo.getDouble("Lat"));
		        		loc.setLongitude(jo.getDouble("Lon"));
		        		User u = new User(name, loc);
		        		friends.put(name, u);
						
		        		if(messengers.containsKey("Friends List")) {
			        		Message mess2 = Message.obtain();
			        		Bundle bo2 = new Bundle();
			        		bo2.putString("Message Type", "Friend Accepted");
			        		bo2.putString("Friend Name", name);
			        		mess2.setData(bo2);
			        		messengers.get("Friends List").send(mess2);
		        		}
					} else if(resT.equals("Friend Request"))
					{
						Message mess = Message.obtain();
						Bundle bo = new Bundle();
						bo.putString("Message Type", "Toast");
						bo.putString("Toast Message", jo.getString("From User")+ " wants to be your friend.");
		        		mess.setData(bo);
		        		messengers.get(currentTab).send(mess);
		        		
		        		if(messengers.containsKey("Friends List")) {
		        			Message mess2 = Message.obtain();
		        			Bundle bo2 = new Bundle();
		        			bo2.putString("Message Type", "Friend Request");
		        			bo2.putString("From User", jo.getString("From User"));
		        			mess2.setData(bo2);
		        			messengers.get("Friends List").send(mess2);
		        		}
					} else if(resT.equals("Friend Removed")) {
						
						friends.remove(jo.getString("Friend Name"));
						
						if(messengers.containsKey("Friend List")) {
		        			Message mess2 = Message.obtain();
		        			Bundle bo2 = new Bundle();
		        			bo2.putString("Message Type", "Friend Removed");
		        			String name = jo.getString("Friend Name");
		        			String val = friends.get(name).getValidation();
		        			bo2.putString("Friend Name", name);
		        			bo2.putString("Validation", val);
		        			mess2.setData(bo2);
		        			messengers.get("Friends List").send(mess2);							
						}
					}
				} catch(JSONException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
			}
		};
	    t = new Thread() {
	    	public void run() {
	    		while(true) {
	    			waitForMessage();
	    			mHandler.post(mh);
	    		}
	    	}
	    };
	    t.start();
	    Messenger messy = new Messenger(mHandler);

		return messy.getBinder();
	}
	
	public static Location getLocation()
	{
		if(location != null)
		{
			return location;
		}
		else
		{
			location = new Location(mocLocationProvider);
			location.setLatitude(0.0);
			location.setLongitude(0.0);
			return location;
		}
	}

}
