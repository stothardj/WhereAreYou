package gogodeX.GUI;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import android.location.Location;

public class User {
	private String firstName;
	private String lastName;
	private String userName;
	private Location userLocation;
	private String validation;
	
	public User(String uName, Location l, String validation)
	{
		setUserName(uName);
		userLocation = l;
		this.validation = validation;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}
	public void setValidation(String validation) {
		this.validation = validation;
	}
	public String getValidation() {
		return validation;
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

