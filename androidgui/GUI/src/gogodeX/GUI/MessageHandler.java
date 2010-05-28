package gogodeX.GUI;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

public class MessageHandler extends Thread {
	private static JavaClient twistedClient;
	private String JSONString;
	private GPSUpdater GPS;
	private JSONObject tokenJSON;
	private static HashMap<String, User> friendsList;
	
	public MessageHandler(JavaClient client, GPSUpdater currentGPS)
	{
		JSONString = "";
		twistedClient = GUI.getClient();
		GPS = currentGPS;
		friendsList = new HashMap<String, User>();
		Location l = new Location ("test");
		l.setLatitude(0.0);
		l.setLongitude(0.0);
		User a = new User("bagrm", l);
		Location l1 = new Location("test");
		l1.setLatitude(37.422006);
		l1.setLongitude(-122.084095);
		User b = new User("jake", l1);
		friendsList.put("bagrm", a);
		friendsList.put("jake", b);
		Location l2 = new Location("test");
		l2.setLatitude(55.00);
		l2.setLongitude(55.00);
		friendsList.get("bagrm").setLocation(l2);
	}
	
	public void run()
	{
		this.waitForMessage();
	}
	
	public void waitForMessage()
	{
		while(JSONString.equals(""))
		{
			JSONString = twistedClient.readLine();
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
			if (tokenJSON.get("Response Type").equals("Coordinate Update"))
			{
				double latitude = tokenJSON.getDouble("Lat");
				double longitude = tokenJSON.getDouble("Lon");
				Location userLocation = new Location("User Location");
				userLocation.setLatitude(latitude);
				userLocation.setLongitude(longitude);
				String userName = tokenJSON.getString("User Name");
				User newUser = new User(userName, userLocation); 
				friendsList.put(userName, newUser);
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
	
	public static HashMap<String, User> getFriendsList()
	{
		return friendsList;
	}
}