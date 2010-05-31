package gogodeX.GUI;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.*;
import android.widget.Toast;

public class SettingsTabActivity extends PreferenceActivity {
	private Messenger mSender;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        
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
					b.putString("whoami", "Settings");
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
		Intent mIntent =  new Intent(SettingsTabActivity.this, GPSUpdater.class);
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
			b.putString("whoami", "Settings");
			mess.setData(b);
			try {
				mSender.send(mess);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
}