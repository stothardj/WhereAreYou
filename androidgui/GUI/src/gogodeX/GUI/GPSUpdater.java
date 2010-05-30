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
	
	private HashMap<String, Messenger> messengers;
	
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
				String mWhoAmI = (String) msg.getData().get("whoami");
				Messenger mMessenger = (Messenger) msg.getData().get("messenger");
				messengers.put(mWhoAmI, mMessenger);
			}
		};
		final Runnable mh = new Runnable() {
			public void run() {
				Message mess = Message.obtain();
				Bundle b = new Bundle();
				b.putString("JSON", JSONString);
				mess.setData(b);
				
				String sendTo = null;
				if(JSONString.contains("Friend"))
					sendTo = "FriendsList";
				try {
					messengers.get(sendTo).send(mess);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
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
