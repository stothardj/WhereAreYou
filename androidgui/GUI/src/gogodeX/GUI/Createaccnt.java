package gogodeX.GUI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Createaccnt extends Activity{
	 /** Called when the activity is first created. */
	private JavaClient client;
	private String user;
	private String pass;
	private String pass2;
	private Editable userEd;
	private Editable passEd;
	private Editable passEd2;

    

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createaccount);
        final EditText userName = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.password);
        final EditText password2 = (EditText) findViewById(R.id.password2);
        final Context context = getApplicationContext();	
    Button next = (Button) findViewById(R.id.create);
    next.setOnClickListener(new View.OnClickListener(){
    	public void onClick(View view) {
           	userEd = userName.getText();
        	passEd = password.getText();
        	passEd2 = password2.getText();
        	user = userEd.toString();
        	pass = passEd.toString();
        	pass2 = passEd2.toString();
        	if(user.length() != 0 && pass.length() != 0 && pass2.length() != 0)	
        	{
        		if(pass.equals(pass2))
        		{
        		Intent myIntent = new Intent(view.getContext(), GUI.class);
        		startActivity(myIntent);
        		}
        		else
        		{
        			CharSequence errorText = "Please re-enter your passwords! They were not equal.";
        			Toast.makeText(context, errorText,15).show();
        			return;
        			
        		}
        	}
        	else
        	{
        		CharSequence text = "Please Input a User Name and passwords!";
        		Toast.makeText(context, text, 15).show();	
        		return;	
        	}

    	}});
	}
}
/*e
public void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.createaccount);
}e
public void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.createaccount);
}e
public void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.createaccount);
}e
public void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.createaccount);
}*/