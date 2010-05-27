package gogodeX.GUI;

import com.google.android.maps.GeoPoint;

import android.location.Location;

public class User {
	private Location location;
	private String firstName;
	private String lastName;
	private String userName;
	
	public User(Location l, String fName, String lName, String uName)
	{
		setLocation(l);
		setFirstName(fName);
		setLastName(lName);
		setUserName(uName);
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}	
	
	public GeoPoint getGeoPoint()
	{
		return new GeoPoint((int)(location.getLatitude()*1000000),(int)(location.getLongitude()*1000000));
	}

}
