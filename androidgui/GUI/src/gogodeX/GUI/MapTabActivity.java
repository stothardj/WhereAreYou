package gogodeX.GUI;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import com.google.android.maps.*;

public class MapTabActivity extends MapActivity {
    /** Called when the activity is first created. */
	private static MapView mapView;
	private static Drawable icon;
	private static MyLocationOverlay myLocOverlay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.maptabactivityview);
	    
	    icon = getResources().getDrawable(R.drawable.icon);
	    
	    // Get the current MapView
	    mapView = (MapView) findViewById(R.id.mapview);
	    
	    // Enable Zoom
	    mapView.setBuiltInZoomControls(true);
	    
	    // Enable my location icon
		myLocOverlay = new MyLocationOverlay(this, mapView);
		myLocOverlay.enableMyLocation();
		mapView.getOverlays().add(myLocOverlay);
		
		createAndShowItemizedOverlay();

	}
	
	protected static void createAndShowItemizedOverlay() 
	{
		List overlays = mapView.getOverlays();
 
		// first remove old overlay
		if (overlays.size() > 0) 
		{
			for (Iterator iterator = overlays.iterator(); iterator.hasNext();) 
			{
				if(iterator.next() != myLocOverlay)
				{
					iterator.remove();
				}
			}
		}
 
		Collection<User> userCollection = MessageHandler.getFriendsList().values();
		GeoPoint geopoint = new GeoPoint(0,0);
		icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
		Location newLocation;
		BuddyOverlay overlay;
		overlay = new BuddyOverlay(icon);
		OverlayItem item;
		
		for(Iterator<User> userIterator = userCollection.iterator(); userIterator.hasNext();)
		{
			// transform the location to a geopoint
			newLocation = new Location(userIterator.next().getLocation());
			geopoint = new GeoPoint(
					(int) (newLocation.getLatitude() * 1E6), (int) (newLocation
							.getLongitude() * 1E6));
			
			item = new OverlayItem(geopoint, "My Location", null);
			overlay.addItem(item);
		}
		
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