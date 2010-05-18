package com.gogodeX;

import java.io.IOException;
import java.net.*;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;
import org.json.*;


public class GPSUpdater extends Service {

	//LocationManager to update GPS coordinates
	private LocationManager LM = null;
	//TextView for debug printing
	private TextView TV = null;
	//Static activity instance for activity data access
	private static gogodeX deX;
	//JavaClient to connect to the twisted server
	private JavaClient client;
	//private RspHandler handler;
	private Thread t;
	//JSONStringer to parse a string into a json string
	private JSONStringer coordinates;
	//Holds the user's current Location
	private Location location;
	//Variables for holding latitude and longitude values
	private static double latitude;
	private static double longitude;
	//Handler for handling incoming server messages
	private messageHandler serverUpdates;
	
	@Override
	public void onCreate()
	{
		//Get the LocationManager and TextView from the main activity
		LM = gogodeX.getLM();
		TV = gogodeX.getTV();
		//Have the activity obtain an instance of this service for data access
		gogodeX.setGPS(this);
		//Start the main GPS updating loop
		GPSHandler();
	}
	
	private void GPSHandler()
	{
		//Obtain the constant string value for the GPS provider
	    String mocLocationProvider = LocationManager.GPS_PROVIDER;
	    //Create a new mocLocationListener instance to handle coordinate updates
	    LocationListener mocLocation = new mocLocationListener(); 
	    //Initiate the updating of coordinates through the requestLocationUpdates method
	    LM.requestLocationUpdates(mocLocationProvider, 0, 0, mocLocation);
	    //Get the last known location of the device for the user's starting location
	    location = LM.getLastKnownLocation(mocLocationProvider);
	    //Create a new client to the twisted server
		try 
		{
			client = new JavaClient("169.232.239.171", 79);
			client.connect();
		} 
		catch (UnknownHostException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		serverUpdates = new messageHandler(client, this);
	    t = new Thread(serverUpdates);
	    t.start();
	}
	
	//Class to handle GPS updates
	private class mocLocationListener implements LocationListener 
	{ 
	            public void onLocationChanged(Location L) { 
	            	//Set the user's location to the newly received location
	            	location = L;
	            	//Create a new JSONStringer to create a JSON string to send to the server
	            	coordinates = new JSONStringer();
	            	JSONStringer login = new JSONStringer();
	            	JSONStringer newUser = new JSONStringer();
	            		//As long as the new location was received
	                    if (L != null) 
	                    { 
	                    	//Get the latitude and longitude from the current user's location
	                    	latitude = L.getLatitude(); 
	                    	longitude = L.getLongitude();          
	                    	try 
	                    	{
	                    		newUser.object();
	                    		newUser.key("User Name").value("bagrm");
	                    		newUser.key("Password").value("password");
	                    		newUser.key("First Name").value("Brian");
	                    		newUser.key("Last Name").value("Garfinkel");
	                    		newUser.key("Account Type").value("User");
	                    		newUser.key("Request Type").value("Create User");
	                    		newUser.endObject();
	                    		
	                    		login.object();
	                    		login.key("User Name").value("bagrm");
	                    		login.key("Password").value("password");
	                    		login.key("Request Type").value("Login");
	                    		login.endObject();
	                    		
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
	                    	TV.append(coordinates.toString());
	                    	//Send the parsed JSON update coordinates request
	                    		client.sendLine(newUser.toString());
	                    		client.sendLine(login.toString());
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
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void setGogo(gogodeX deX1)
	{
		deX = deX1;
	}

}
