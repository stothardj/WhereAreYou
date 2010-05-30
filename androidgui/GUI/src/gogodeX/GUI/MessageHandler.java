package gogodeX.GUI;

import org.json.JSONException;
import org.json.JSONObject;
import android.location.Location;

public class MessageHandler{
	private JSONObject tokenJSON;

	public void parseJSON(String JSONString)
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
				//TODO: Stuff
			}
			else if(tokenJSON.get("Response Type").equals("Friend Requested"))
			{
				String friendName = tokenJSON.getString("Friend Name");
				boolean success = tokenJSON.getBoolean("Success");
				//TODO: Stuff
			}
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}