package gogodeX.GUI;

import java.util.ArrayList;
import java.util.HashMap;
import android.graphics.drawable.Drawable;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class BuddyOverlay extends ItemizedOverlay<OverlayItem> {
	private ArrayList<User> mOverlays = new ArrayList<User>();
	private HashMap<String,User> myOverlayMap = new HashMap<String,User>();

	public BuddyOverlay(Drawable defaultMarker) {
		  super(boundCenterBottom(defaultMarker));
	}
	
	public void add(String userName, User user) {
		myOverlayMap.put(userName, user);
	    mOverlays.add(user);
	    populate();
	}	
	
	public void remove (String userName)
	{
		mOverlays.remove(myOverlayMap.get(userName));
		myOverlayMap.remove(userName);
	}
	
	public void edit(String userName, GeoPoint loc)
	{
		User current = myOverlayMap.get(userName);
		current.setOverlay(new OverlayItem(loc,"",""));
	}
	
	@Override
	protected OverlayItem createItem(int i) {
	  return mOverlays.get(i).getOverlay();
	}

	@Override
	public int size() {
	  return mOverlays.size();
	}
}
