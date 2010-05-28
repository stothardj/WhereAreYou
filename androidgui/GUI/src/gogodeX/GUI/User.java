package gogodeX.GUI;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import android.location.Location;

public class User {
	private String firstName;
	private String lastName;
	private String userName;
	private Location userLocation;
	
	public User(String uName, Location l)
	{
		setUserName(uName);
		userLocation = l;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}	
	
	public Location getLocation()
	{
		return userLocation;
	}
	
	public GeoPoint getGeoPoint()
	{
		Double lat = userLocation.getLatitude();
		Double lon = userLocation.getLongitude();
		lat = lat * 1000000;
		lon = lon * 1000000;
		return new GeoPoint(lat.intValue(), lon.intValue());
	}
	
	public void setLocation(Location l)
	{
		userLocation = l;
	}

}

