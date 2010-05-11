package com.gogodeX;

import android.app.Activity; 
import android.content.Context; 
import android.content.Intent;
import android.widget.TextView; 
import android.os.Bundle; 
import android.location.*; 


public class gogodeX extends Activity {
	 /** Called when the activity is first created. */ 
    private static TextView TV;
    private static LocationManager LM;
    private static GPSUpdater GPS;
@Override 
public void onCreate(Bundle savedInstanceState) { 
	
    super.onCreate(savedInstanceState); 
    setContentView(R.layout.main); 
    TV = new TextView(this); 
    LM = (LocationManager) getSystemService(Context.LOCATION_SERVICE); 
    setContentView(TV); 
    Intent svc = new Intent(this, GPSUpdater.class);
    startService(svc);
    GPSUpdater.setGogo(this);
    } 

public static TextView getTV()
{
	return TV;
}

public static LocationManager getLM()
{
	return LM;
}

public static void setGPS(GPSUpdater GPS1)
{
	GPS = GPS1;
}

}

