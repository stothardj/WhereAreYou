package gogodeX.GUI;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import com.google.android.maps.*;

public class MapTabActivity extends MapActivity {
    /** Called when the activity is first created. */
	private static List<Overlay> mapOverlays;
	private MapView mapView;
	//private Location L;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.maptabactivityview);
	    mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
	    mapOverlays = mapView.getOverlays();
	    Drawable drawable = this.getResources().getDrawable(R.drawable.icon);
	    
	    /*
	    L = GPSUpdater.getLocation();
	    Double lat = L.getLatitude();
	    Double lon = L.getLongitude();
	    int lat1 = (int) (lat * 1000000);
	    int lon1 = (int) (lon * 1000000);	    
	    BuddyOverlay myOverlay = new BuddyOverlay(drawable);
	    GeoPoint myPoint = new GeoPoint(lat1, lon1);
	    OverlayItem myOverlayItem = new OverlayItem(myPoint, "", "");
	    myOverlay.addOverlay(myOverlayItem);
	    mapOverlays.add(myOverlay);
		*/
	    
	    displayCoordinate(19240000,-99120000,drawable,"","");
	    initMyLocation();
	}
	
	public void displayCoordinate(int lat, int lon, Drawable drawable, String title, String snippet)
	{
		BuddyOverlay itemizedoverlay = new BuddyOverlay(drawable);
		GeoPoint point = new GeoPoint(lat,lon);
		itemizedoverlay.addOverlay(new OverlayItem(point, title, snippet));
		mapOverlays.add(itemizedoverlay);
	}
	
	// Function no longer used
	public static void onLocationChange(Location L)
	{
		/*mapOverlays.remove(0);
	    BuddyOverlay myOverlay = new BuddyOverlay(drawable);
	    
	    Double lat = L.getLatitude();
	    Double lon = L.getLongitude();
	    int lat1 = (int) (lat * 1000000);
	    int lon1 = (int) (lon * 1000000);
	    
	    GeoPoint myPoint = new GeoPoint(lat1, lon1);
	    OverlayItem myOverlayItem = new OverlayItem(myPoint, "", "");
	    myOverlay.addOverlay(myOverlayItem);
	    mapOverlays.add(myOverlay);*/
		
	}
	
	// Sets up the self marker on the map
	// marker is updated automatically
	private void initMyLocation()
	{
		MyLocationOverlay myLocOverlay = new MyLocationOverlay(this, mapView);
		myLocOverlay.enableMyLocation();
		mapView.getOverlays().add(myLocOverlay);
	}
	
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}