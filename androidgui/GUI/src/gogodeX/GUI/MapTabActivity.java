package gogodeX.GUI;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import com.google.android.maps.*;

public class MapTabActivity extends MapActivity {
    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.maptabactivityview);
	    MapView mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
	    
	    Location L = GPSUpdater.getLocation();
	    Double lat = L.getLatitude();
	    Double lon = L.getLongitude();
	    int lat1 = (int) (lat * 1000000);
	    int lon1 = (int) (lon * 1000000);
	    
	    List<Overlay> mapOverlays = mapView.getOverlays();
	    Drawable drawable = this.getResources().getDrawable(R.drawable.icon);
	    BuddyOverlay itemizedoverlay = new BuddyOverlay(drawable);
	    BuddyOverlay myOverlay = new BuddyOverlay(drawable);
	    
	    GeoPoint point = new GeoPoint(19240000,-99120000);
	    GeoPoint myPoint = new GeoPoint(lat1, lon1);
	    OverlayItem overlayitem = new OverlayItem(point, "", "");
	    OverlayItem myOverlayItem = new OverlayItem(myPoint, "", "");
	    
	    itemizedoverlay.addOverlay(overlayitem);
	    myOverlay.addOverlay(myOverlayItem);
	    mapOverlays.add(itemizedoverlay);
	    mapOverlays.add(myOverlay);
	    
	}
	
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}