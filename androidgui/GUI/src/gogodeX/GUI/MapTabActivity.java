package gogodeX.GUI;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import com.google.android.maps.*;

public class MapTabActivity extends MapActivity {
    /** Called when the activity is first created. */
	private static MapView mapView;
	private static Drawable icon;
	private static MyLocationOverlay myLocOverlay;
	private Messenger mSender;
	HashMap<String, Location> friendLocations;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.maptabactivityview);
	    
	    icon = getResources().getDrawable(R.drawable.pin);
	    
	    // Get the current MapView
	    mapView = (MapView) findViewById(R.id.mapview);
	    
	    // Enable Zoom
	    mapView.setBuiltInZoomControls(true);
	    
	    // Enable my location icon
		myLocOverlay = new MyLocationOverlay(this, mapView);
		myLocOverlay.enableMyLocation();
		mapView.getOverlays().add(myLocOverlay);
		
		friendLocations = new HashMap<String, Location>();
		
    	////////////// Setup Two Way Communication ////////////////////
    	final Handler mHandler = new Handler() {
    		@Override
    		public void handleMessage(Message msg) {
    			Bundle b = msg.getData();
    			String msgType = b.getString("Message Type");
    			if(msgType.equals("Toast")) {
	        		Context context = getApplicationContext();
	        		int duration = Toast.LENGTH_SHORT;
            		Toast toast = Toast.makeText(context, b.getString("Toast Message"), duration);
            		toast.show();
    			} else if(msgType.equals("Friend List")) {
    				String [] farray = b.getStringArray("Friend Names");
    				Location[] larray = (Location[]) b.getParcelableArray("Location Array");
    				int i=0;
    				for(String name : farray) {
    					if(name==null) //don't add empty entries
    						break;
    					friendLocations.put(name, larray[i]);
    					i++;
    				}
    				createAndShowItemizedOverlay();
    			} else if(msgType.startsWith("Add Friend")) {
    				String name = b.getString("Friend Name");
    				Location loc = null;
    				if(msgType.endsWith("with Position")) {
    					loc = b.getParcelable("Location");
    				}
    				friendLocations.put(name, loc);
    			}
    		}
    	};
    	final Messenger mReceiver = new Messenger(mHandler);
		ServiceConnection conn = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				mSender = new Messenger(service);
				try {
					Message mess = Message.obtain();
					Bundle b = new Bundle();
					b.putString("Message Type", "Pass Messenger");
					b.putString("whoami", "Map");
					b.putParcelable("messenger", mReceiver);
					mess.setData(b);
					mSender.send(mess);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onServiceDisconnected(ComponentName name) {}
		};
		Intent mIntent =  new Intent(MapTabActivity.this, GPSUpdater.class);
		this.getApplicationContext().bindService(mIntent, conn, 0);
		//////////////Setup Two Way Communication ////////////////////
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(mSender != null) {
			Message mess = Message.obtain();
			Bundle b = new Bundle();
			b.putString("Message Type", "Declare Active");
			b.putString("whoami", "Map");
			mess.setData(b);
			try {
				mSender.send(mess);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void createAndShowItemizedOverlay() 
	{
		List overlays = mapView.getOverlays();
 
		// first remove old overlay
		if (overlays.size() > 0) 
		{
			for (int i = 0; i < overlays.size(); i++) 
			{
				if(overlays.get(i) != myLocOverlay)
				{
					overlays.remove(i);
					break;
				}
			}
		}
 
		GeoPoint geopoint = new GeoPoint(0,0);
		icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
		Location newLocation;
		
		if(!friendLocations.isEmpty()) {
			BuddyOverlay overlay;
			overlay = new BuddyOverlay(icon);
			OverlayItem item;
			for(String name : friendLocations.keySet())
			{
				Location loc = friendLocations.get(name);
				if(loc != null) {
					// transform the location to a geopoint
					newLocation = new Location(loc);
					geopoint = new GeoPoint(
							(int) (newLocation.getLatitude() * 1E6), (int) (newLocation
									.getLongitude() * 1E6));
					
					item = new OverlayItem(geopoint, "My Location", name);
					overlay.addItem(item);
				}
			}
			mapView.getOverlays().add(overlay);
			// move to location
			mapView.getController().animateTo(geopoint); 
			// redraw map
			mapView.postInvalidate();
		}
	}
 
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}