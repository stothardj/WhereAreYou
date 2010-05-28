package gogodeX.GUI;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import android.location.Location;

public class User {
	private String firstName;
	private String lastName;
	private String userName;
	private OverlayItem overlay;
	
	public User(String fName, String lName, String uName, OverlayItem o)
	{
		setFirstName(fName);
		setLastName(lName);
		setUserName(uName);
		setOverlay(o);
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
		return overlay.getPoint();
	}

	public void setOverlay(OverlayItem overlay) {
		this.overlay = overlay;
	}

	public OverlayItem getOverlay() {
		return overlay;
	}

}
