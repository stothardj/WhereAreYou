package gogodeX.GUI;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import com.google.android.maps.*;

public class MapTabActivity extends MapActivity {
    /** Called when the activity is first created. */
	private MapView mapView;
	MyLocationOverlay myLocOverlay;
	BuddyOverlay friendOverlay;
	Drawable drawable;
	//private Location L;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.maptabactivityview);
	    
	    // Retrieve icon
	    drawable = this.getResources().getDrawable(R.drawable.icon);	
	    
	    // Get the current MapView
	    mapView = (MapView) findViewById(R.id.mapview);
	    
	    // Enable Zoom
	    mapView.setBuiltInZoomControls(true);
	    
	    // Enable my location icon
		myLocOverlay = new MyLocationOverlay(this, mapView);
		myLocOverlay.enableMyLocation();
		mapView.getOverlays().add(myLocOverlay);
		
		// Enable friend overlay
		friendOverlay = new BuddyOverlay(drawable);
		mapView.getOverlays().add(friendOverlay);
		
		// Display a marker at Mexico City
	    addFriend("Average","Joe","avgJoe",new OverlayItem(new GeoPoint(19240000,-99120000),"",""));
	}

	public void addFriend(String firstName, String lastName, String userName, OverlayItem overlay)
	{
		friendOverlay.add("avgJoe", new User("Average","Joe","avgJoe", overlay));
	}


    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}