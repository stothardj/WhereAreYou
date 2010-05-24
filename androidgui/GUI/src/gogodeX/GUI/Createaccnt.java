package gogodeX.GUI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Createaccnt extends Activity{
	 /** Called when the activity is first created. */

    

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createaccount);
    
	
    Button next = (Button) findViewById(R.id.create);
    next.setOnClickListener(new View.OnClickListener(){
    	public void onClick(View view) {
    		Intent myIntent = new Intent(view.getContext(), GUI.class);
    		startActivity(myIntent);
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