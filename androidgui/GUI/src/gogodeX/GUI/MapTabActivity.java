package gogodeX.GUI;

import android.graphics.drawable.Drawable;
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
		
		// Display a marker at mexico city
	    addFriend(19240000,-99120000,drawable,"","");
	}

	public void addFriend(int lat, int lon, Drawable drawable, String title, String snippet)
	{
		GeoPoint point = new GeoPoint(lat,lon);
		friendOverlay.addOverlay(new OverlayItem(point, title, snippet));
	}


    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}