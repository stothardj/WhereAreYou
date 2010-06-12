/*
Copyright 2010 Jake Stothard, Brian Garfinkel, Adam Shwert, Hongchen Yu, Yijie Wang, Ryan Rosario, Jiho Kim

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

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
                String snippet = items.get(pIndex).getSnippet();
                if(snippet.equals("")) {
                        Toast.makeText(mContext, items.get(pIndex).getTitle(),Toast.LENGTH_SHORT).show();
                } else {
                        Toast.makeText(mContext, items.get(pIndex).getTitle() + "\n" + snippet,Toast.LENGTH_SHORT).show();
                }
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
