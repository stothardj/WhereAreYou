package gogodeX.GUI;

import java.util.Iterator;
import java.util.List;

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
		//myLocOverlay = new MyLocationOverlay(this, mapView);
		//myLocOverlay.enableMyLocation();
		//mapView.getOverlays().add(myLocOverlay);
		
		// Enable friend overlay
		//friendOverlay = new BuddyOverlay(drawable);
		//mapView.getOverlays().add(friendOverlay);
		
		Location l = new Location("test");
		l.setLatitude(0.0);
		l.setLongitude(0.0);
		this.createAndShowItemizedOverlay(l);
		
		l.setLatitude(37.422006);
		l.setLongitude(-122.084095);
		
		
		this.createAndShowItemizedOverlay(l);
		
		// Display a marker at mexico city
	    //addFriend(19240000,-99120000,drawable,"","");
	}
	
	protected void createAndShowItemizedOverlay(Location newLocation) {
		List overlays = mapView.getOverlays();
 
		// first remove old overlay
		if (overlays.size() > 0) {
			for (Iterator iterator = overlays.iterator(); iterator.hasNext();) {
				iterator.next();
				iterator.remove();
			}
		}
 
		// transform the location to a geopoint
		GeoPoint geopoint = new GeoPoint(
				(int) (newLocation.getLatitude() * 1E6), (int) (newLocation
						.getLongitude() * 1E6));
 
		// initialize icon
		Drawable icon = getResources().getDrawable(R.drawable.icon);
		icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon
				.getIntrinsicHeight());
 
		// create my overlay and show it
		BuddyOverlay overlay = new BuddyOverlay(icon);
		OverlayItem item = new OverlayItem(geopoint, "My Location", null);
		overlay.addItem(item);
		mapView.getOverlays().add(overlay);
 
		// move to location
		mapView.getController().animateTo(geopoint);
 
		// redraw map
		mapView.postInvalidate();
	}
 
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}