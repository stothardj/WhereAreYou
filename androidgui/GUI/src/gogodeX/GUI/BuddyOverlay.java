package gogodeX.GUI;

import java.util.ArrayList;
import java.util.List;
 
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

 
public class BuddyOverlay extends ItemizedOverlay {
 
	private List<OverlayItem> items;
	private Drawable marker;
	private Context mContext;
 
	public BuddyOverlay(Drawable defaultMarker, Context context) {
		super(defaultMarker);
		items = new ArrayList<OverlayItem>();
		marker = defaultMarker;
		mContext = context;
	}
 
	@Override
	protected OverlayItem createItem(int index) {
		return items.get(index);
	}
 
	@Override
	public int size() {
		return items.size();
 
	}
	
	@Override
	protected boolean onTap(int pIndex) {
	   Toast.makeText(mContext, items.get(pIndex).getTitle() + "\n" + items.get(pIndex).getSnippet(),Toast.LENGTH_SHORT).show();
	   return true;
	   
	}
 
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.google.android.maps.ItemizedOverlay#draw(android.graphics.Canvas,
	 * com.google.android.maps.MapView, boolean)
	 */
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		boundCenterBottom(marker);
 
	}
	
	public void addItem(OverlayItem item) {
		items.add(item);
		populate();
	}
 
}
