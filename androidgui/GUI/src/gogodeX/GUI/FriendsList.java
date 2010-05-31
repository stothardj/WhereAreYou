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
	private Messenger mSender = null;
	
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
    				String[] farray = b.getStringArray("Friend List");
    				for(String s : farray) {
    					m_list.add(s);
    				}
    			} else if(msgType.equals("Friend Requested")) {
    				m_list.add(b.getString("Friend Name") + "\t\t(Pending)");
    			} else if(msgType.equals("Friend Accepted")) {
					String name = b.getString("Friend Name");
					m_list.remove(name+"\t\t(Pending)");
					m_list.add(name);
    			} else if(msgType.equals("Friend Request")) {
					String name = b.getString("From User");
					m_list.add(name+"\t\t(Unaccepted)");
    			} else if(msgType.equals("Friend Removed")) {
    				String val = b.getString("Validation");
    				if(val.equals("Accepted"))
    					m_list.remove(b.getString("Friend Name"));
    				else
    					m_list.remove(b.getString("Friend Name")+"\t\t("+val+")");
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
					b.putString("whoami", "Friends List");
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
		Intent mIntent =  new Intent(FriendsList.this, GPSUpdater.class);
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
			b.putString("whoami", "Friends List");
			mess.setData(b);
			try {
				mSender.send(mess);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

    protected void onListItemClick(ListView l, View v, int position, long id) {
    	row = position;
    	Button remove = (Button) findViewById(R.id.remove_friend);
    	Button accept = (Button) findViewById(R.id.accept_friend);
    	String name = m_list.getItem(row);
    	if(name.endsWith("\t\t(Pending)"))
    		name = name.substring(0, name.length() - new String("\t\t(Pending)").length());
    	if(name.endsWith("\t\t(Unaccepted)"))
    		name = name.substring(0, name.length() - new String("\t\t(Unaccepted)").length());
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
			    	Message mess = Message.obtain();
			    	Bundle b = new Bundle();
			    	b.putString("Message Type", "Remove Friend");
			    	b.putString("Friend Name", name);
			    	mess.setData(b);
			    	try {
						mSender.send(mess);
					} catch (RemoteException e) {
						e.printStackTrace();
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
        				m_list.remove(name);
        	    		name = name.substring(0, name.length() - new String("\t\t(Unaccepted)").length());
        	    		m_list.add(name);
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
				    	Message mess = Message.obtain();
				    	Bundle b = new Bundle();
				    	b.putString("Message Type", "Accept Friend");
				    	b.putString("Friend Name", name);
				    	mess.setData(b);
				    	try {
							mSender.send(mess);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
				    	
	            		Toast toast = Toast.makeText(context, "Accepting "+name, duration);
	            		toast.show();
        			}
        		}
        	}        	
        }); 
    }
}
