package gogodeX.GUI;

import java.io.IOException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GUI extends Activity {
    /** Called when the activity is first created. */
	private JavaClient client;
	private String user;
	private String pass;
	private Editable userEd;
	private Editable passEd;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Button next = (Button) findViewById(R.id.ok);
        Button creation = (Button) findViewById(R.id.create);
        
        final EditText userName = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.password);
        final Context context = getApplicationContext();
        final int duration = Toast.LENGTH_SHORT;
        
        client = new JavaClient("127.0.0.1", 79);
        creation.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View view) {
        		Intent myIntent = new Intent(view.getContext(), Createaccnt.class);
        		startActivity(myIntent);
        	}
        	
        	
        });
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	userEd = userName.getText();
            	passEd = password.getText();
            	user = userEd.toString();
            	pass = passEd.toString();
            	if(user.length() != 0 && pass.length() != 0)	
            	{
            		/*boolean connected = connectToServer();
            		if (connected == false)
            		{
            			CharSequence text = "Unable to Connect to the Server!";
            			Toast.makeText(context, text, duration).show();
            			return;
            		}
            		else
            		{*/
            			Intent myIntent = new Intent(view.getContext(), Tabs.class);
            			startActivity(myIntent);
            		//}
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
    	
    private boolean connectToServer(){
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

}