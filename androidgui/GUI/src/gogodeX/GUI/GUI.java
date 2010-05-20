package gogodeX.GUI;

import java.io.IOException;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import gogodeX.GUI.GPSUpdater;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GUI extends Activity {
    /** Called when the activity is first created. */
	private static JavaClient client;
	private String user;
	private String pass;
	private Editable userEd;
	private Editable passEd;
	private JSONStringer JSONUser;
	private static LocationManager LM;
	private JSONObject JSONValidate;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Button next = (Button) findViewById(R.id.ok);
        
        final EditText userName = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.password);
        final Context context = getApplicationContext();
        final int duration = Toast.LENGTH_SHORT;
        final Intent startUpdatingCoordinates = new Intent(this, GPSUpdater.class);
        
        client = new JavaClient("169.232.101.67", 79);
        LM = (LocationManager) getSystemService(Context.LOCATION_SERVICE); 
        
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	userEd = userName.getText();
            	passEd = password.getText();
            	user = userEd.toString();
            	pass = passEd.toString();
            	if(user.length() != 0 && pass.length() != 0)	
            	{
            		boolean connected = connectToServer();
            		if (connected == false)
            		{
            			CharSequence text = "Unable to Connect to the Server!";
            			Toast.makeText(context, text, duration).show();
            			return;
            		}
            		boolean validated = validateUser();
            		if(validated == false)
            		{
            			CharSequence text = "Sorry, User Name and/or Password not Found!";
            			Toast.makeText(context, text, duration).show();
            			return;	
            		}
            		
            	    startService(startUpdatingCoordinates);
            	    
            		Intent myIntent = new Intent(view.getContext(), Tabs.class);
            		startActivity(myIntent);
            	}
            	else
            	{
            		CharSequence text = "Please Input a User Name and Password!";
            		Toast.makeText(context, text, duration).show();	
            		return;	
            	}
            }
         });    
      }
    
    private boolean connectToServer()
    {
    	try 
    	{
			client.connect();
		} 
    	catch (UnknownHostException e) 
    	{
    		return false;
		} catch (IOException e) 
		{
			return false;
		}
		return true;
    }
    
    private boolean validateUser()
    {
    	JSONUser = new JSONStringer();
    	try 
    	{
    		JSONUser.object();
			JSONUser.key("User Name").value(userEd.toString());
			JSONUser.key("Password").value(passEd.toString());
			JSONUser.key("Request Type").value("Login");
			JSONUser.endObject();
			client.sendLine(JSONUser.toString());
			String response = client.readLine();
			JSONValidate = new JSONObject(response);
			if(JSONValidate.get("Response Type") == "User Validation" && JSONValidate.getBoolean("Success") == true)
			{
				return true;	
			}
			else
			{
				return false;
			}	
		} 
    	catch (JSONException e) 
		{
    		return false;			
		}
    	catch (IOException e)
    	{
    		e.printStackTrace();
    		return false;
    	}
    	
    }
    
    public static void getClient(JavaClient client)
    {
    	client = GUI.client;
    }
    
    public static void getLM(LocationManager LM)
    {
    	LM = GUI.LM;
    }
    

}