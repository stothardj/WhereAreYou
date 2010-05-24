package gogodeX.GUI;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageHandler extends Thread {
	private JavaClient twistedClient;
	private String JSONString;
	private GPSUpdater GPS;
	private JSONObject tokenJSON;
	
	public MessageHandler(JavaClient client, GPSUpdater currentGPS)
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