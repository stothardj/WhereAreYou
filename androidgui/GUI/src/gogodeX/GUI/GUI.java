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
        private EditText userName;
    private EditText password;
    private Context context;
    private int duration;
    private Intent startUpdatingCoordinates;
    private boolean validated;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Button next = (Button) findViewById(R.id.ok);
        Button creation = (Button) findViewById(R.id.create);

        creation.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                        Intent myIntent2 = new Intent(view.getContext(), CreateAccount.class); // change the
                        startActivity(myIntent2);                                               // Createaccnt.class to Tabs.class if you
                                                                                                                        // want to test the stuff in the tabs w/o a server
                }
        });

        userName = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        context = getApplicationContext();
        duration = Toast.LENGTH_SHORT;
        startUpdatingCoordinates = new Intent(this, GPSUpdater.class);

        client = new JavaClient("169.232.101.143", 79);
        //connected = connectToServer();

        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                        userEd = userName.getText();
                        passEd = password.getText();
                        user = userEd.toString();
                        pass = passEd.toString();
                        if(user.length() == 0 || pass.length() == 0)
                        {
                                CharSequence text = "Please Input a User Name and Password!";
                                Toast.makeText(context, text, duration).show();
                                return;
                        }
                        else if(client.getConnected() == false)
                        {
                                connectToServer();
                                if(client.getConnected() == true)
                                {
                                        validated = validateUser();
                                        if(validated == false)
                                        {
                                                if (client.getConnected() == true)
                                                {
                                                        CharSequence text = "Sorry, User Name and/or Password not Found!";
                                                        Toast.makeText(context, text, duration).show();
                                                        return;
                                                }
                                                else
                                                {
                                                        CharSequence text = "Unable to Connect to the Server";
                                                        Toast.makeText(context, text, duration).show();
                                                        return;
                                                }
                                        }

                                        startService(startUpdatingCoordinates);

                                        Intent myIntent = new Intent(view.getContext(), Tabs.class);
                                        startActivity(myIntent);
                                }
                                else
                                {
                                        CharSequence text = "Unable to Connect to the Server!";
                                        Toast.makeText(context, text, duration).show();
                                        return;
                                }
                        }
                        else
                        {
                                validated = validateUser();
                                if(validated == false)
                                {
                                        if (client.getConnected() == true)
                                        {
                                                CharSequence text = "Sorry, User Name and/or Password not Found!";
                                                Toast.makeText(context, text, duration).show();
                                                return;
                                        }
                                        else
                                        {
                                                CharSequence text = "Unable to Connect to the Server";
                                                Toast.makeText(context, text, duration).show();
                                                return;
                                        }
                                }

                                startService(startUpdatingCoordinates);

                                Intent myIntent = new Intent(view.getContext(), Tabs.class);
                                startActivity(myIntent);
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
                }
        catch (IOException e)
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
                        String response = client.readOneLine();
                        JSONValidate = new JSONObject(response);
                        String responseType = JSONValidate.getString("Response Type");
                        boolean isValidated = JSONValidate.getBoolean("Success");
                        if(responseType.equals("User Validation") && isValidated == true)
                        {
                                return true;
                        }
                        else
                        {
                                return false;
                        }
                }
        catch (JSONException e1)
                {
                e1.printStackTrace();
                return false;
                }

    }
    public static JavaClient getClient()
    {
        return client;
    }

    public static void getLM(LocationManager LM)
    {
        LM = GUI.LM;
    }
}
