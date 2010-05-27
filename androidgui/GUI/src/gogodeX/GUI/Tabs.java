package gogodeX.GUI;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class Tabs extends TabActivity {
    /** Called when the activity is first created. */
	private TabHost tabHost;
	private TabHost.TabSpec spec;
	private Intent intent;
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setDefaultTab(0);
        
        tabHost = getTabHost();	// The activity TabHost

        // Create an Intent to launch an Activity for the tab
        intent = new Intent().setClass(this.getApplicationContext(), MapTabActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("map").setIndicator("map")
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this.getApplicationContext(), SettingsTabActivity.class);
        spec = tabHost.newTabSpec("setting").setIndicator("setting")
                      .setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this.getApplicationContext(), FriendsList.class);
        spec = tabHost.newTabSpec("friends list").setIndicator("friends list")
        			  .setContent(intent);
        tabHost.addTab(spec);
    }
    
}