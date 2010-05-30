package gogodeX.GUI;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import android.app.ListActivity;
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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.EditText;

public class FriendsList extends ListActivity {
	private ArrayAdapter<String> m_list;
	int row = -1;
	private FriendsList ref = this;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.friends);

        m_list = new  ArrayAdapter<String>(this, R.layout.friends_rows, R.id.name);
	    setListAdapter(m_list);
	    
    	Button add = (Button) findViewById(R.id.add_friend);
    	add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = ((EditText)findViewById(R.id.friend_name_textbox)).getText().toString();
				if(!name.equals("")) {
					
			    	JSONStringer friendRequest = new JSONStringer();
			    	try 
			    	{
			    		friendRequest.object();
			    		friendRequest.key("Friend Name").value(name);
			    		friendRequest.key("Request Type").value("Add Friend");
			    		friendRequest.endObject();
						GUI.getClient().sendLine(friendRequest.toString());
					} 
			    	catch (JSONException e1) 
					{
			    		e1.printStackTrace();		
					}
					((EditText)findViewById(R.id.friend_name_textbox)).setText("");
	        		Context context = getApplicationContext();
	        		int duration = Toast.LENGTH_SHORT;
            		Toast toast = Toast.makeText(context, "Hello "+name, duration);
            		toast.show();
				}
			}
		});
       
    	final Handler mHandler = new Handler() {
    		@Override
    		public void handleMessage(Message msg) {
    			try {
    				String mJSON = msg.getData().getString("JSON");
    				
					JSONObject jo = new JSONObject(mJSON);
					String resT = jo.getString("Response Type");
					if(resT.equals("Friend Requested"))
					{
						if(jo.getBoolean("Success"))
							m_list.add(jo.getString("Friend Name") + "\t\t(Pending)");
						else
						{
			        		Context context = getApplicationContext();
			        		int duration = Toast.LENGTH_SHORT;
		            		Toast toast = Toast.makeText(context, "Cannot request "+jo.getString("Friend Name"), duration);
		            		toast.show();
						}
					} else if(resT.equals("Friend Accepted"))
					{
						String name = jo.getString("Friend Name");
						m_list.remove(name+"\t\t(Pending)");
						m_list.add(name);
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
    			
    		}
    	};
    	final Messenger mMessenger = new Messenger(mHandler);
    	
		ServiceConnection conn = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				Messenger m = new Messenger(service);
				try {
					Message mess = Message.obtain();
					Bundle b = new Bundle();
					b.putString("whoami", "FriendsList");
					b.putParcelable("messenger", mMessenger);
					mess.setData(b);
					m.send(mess);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
			}
		};
		Intent mIntent =  new Intent(FriendsList.this, GPSUpdater.class);
		boolean success = this.getApplicationContext().bindService(mIntent, conn, 0);
		System.out.println(success);
	}

    protected void onListItemClick(ListView l, View v, int position, long id) {
    	row = position;
    	Button remove = (Button) findViewById(R.id.remove_friend);
    	Button accept = (Button) findViewById(R.id.accept_friend);
    	String name = m_list.getItem(row);
    	if(name.endsWith("\t\t(Pending)"))
    		name = name.substring(0, name.length() - new String("\t\t(Pending)").length());
    	((EditText)findViewById(R.id.friend_name_textbox)).setText(name);
    	
    	remove.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View view) {
        		Context context = getApplicationContext();
        		int duration = Toast.LENGTH_SHORT;
        		if (row != -1) {
        			String name = m_list.getItem(row);
        			if(name.endsWith("\t\t(Pending)"))
        	    		name = name.substring(0, name.length() - new String("\t\t(Pending)").length());
			    	JSONStringer friendRequest = new JSONStringer();
			    	try 
			    	{
			    		friendRequest.object();
			    		friendRequest.key("Friend Name").value(name);
			    		friendRequest.key("Request Type").value("Remove Friend");
			    		friendRequest.endObject();
						GUI.getClient().sendLine(friendRequest.toString());
					} 
			    	catch (JSONException e1) 
					{
			    		e1.printStackTrace();		
					}
            		Toast toast = Toast.makeText(context, "Good bye "+name, duration);
            		toast.show();
        			m_list.remove(m_list.getItem(row));
        			row = -1;
        		}
        	}        	
        }); 
    	
    	
    	accept.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View view) {
        		Context context = getApplicationContext();
        		int duration = Toast.LENGTH_SHORT;
        		if (row != -1) {
        			String name = m_list.getItem(row);
        			if(name.endsWith("\t\t(Unaccepted)"))
        			{
        	    		name = name.substring(0, name.length() - new String("\t\t(Unaccepted)").length());
				    	JSONStringer friendRequest = new JSONStringer();
				    	try 
				    	{
				    		friendRequest.object();
				    		friendRequest.key("Friend Name").value(name);
				    		friendRequest.key("Request Type").value("Accept Friend");
				    		friendRequest.endObject();
							GUI.getClient().sendLine(friendRequest.toString());
						} 
				    	catch (JSONException e1) 
						{
				    		e1.printStackTrace();		
						}
	            		Toast toast = Toast.makeText(context, "Accepting "+name, duration);
	            		toast.show();
        			}
        		}
        	}        	
        }); 
    }
}
