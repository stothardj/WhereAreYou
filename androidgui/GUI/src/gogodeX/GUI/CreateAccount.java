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

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import gogodeX.GUI.GPSUpdater;
import android.location.LocationManager;

public class CreateAccount extends Activity{
         /** Called when the activity is first created. */
        private JavaClient client;
        private String user;
        private String pass;
        private String pass2;
        private Editable userEd;
        private Editable passEd;
        private Editable passEd2;
        private Editable Lastname;
        private Editable Firstname;
        private JSONStringer JSONUser;
        private JSONObject JSONValidate;

         private boolean createUser()
         {
        JSONUser = new JSONStringer();
        try
        {
                JSONUser.object();
                        JSONUser.key("User Name").value(userEd.toString());
                        JSONUser.key("Password").value(passEd.toString());
                        JSONUser.key("Account Type").value("User");
                        JSONUser.key("Request Type").value("Create User");
                        JSONUser.key("Last Name").value(Lastname.toString());
                        JSONUser.key("First Name").value(Firstname.toString());
                        JSONUser.endObject();
                        client.sendLine(JSONUser.toString());
                        String response = client.readOneLine();
                final Context context = getApplicationContext();
                        JSONValidate = new JSONObject(response);
                        String responseType = JSONValidate.getString("Response Type");
                        boolean isValidated = JSONValidate.getBoolean("Success");
                        if(responseType.equals("Created User") && isValidated == true)
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

        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createaccount);
        final EditText userName = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.password);
        final EditText password2 = (EditText) findViewById(R.id.password2);
        final EditText Lastname2 = (EditText) findViewById(R.id.Lastname);
        final EditText Firstname2 = (EditText) findViewById(R.id.Firstname);
        final Context context = getApplicationContext();
        client = GUI.getClient();
        Button next = (Button) findViewById(R.id.create);
        next.setOnClickListener(new View.OnClickListener(){
        public void onClick(View view) {
                userEd = userName.getText();
                passEd = password.getText();
                passEd2 = password2.getText();
                Lastname = Lastname2.getText();
                Firstname = Firstname2.getText();
                user = userEd.toString();
                pass = passEd.toString();
                pass2 = passEd2.toString();
                if(user.length() != 0 && pass.length() != 0 && pass2.length() != 0)
                {
                        if(pass.equals(pass2))
                        {
                                if(!client.getConnected())
                                {
                                        connectToServer();
                                }

                                if(client.getConnected()){
                                boolean created = createUser();
                                if(created){
                                        Intent myIntent = new Intent(view.getContext(), GUI.class);
                                        startActivity(myIntent);
                                }
                                else{
                                        if(client.getConnected() == true)
                                        {
                                                CharSequence errorText = "Unable to create account. Please try again.";
                                                Toast.makeText(context, errorText,15).show();
                                                return;
                                        }
                                        else
                                        {
                                                CharSequence errorText = "Unable to Connect to the Server. Please try again.";
                                                Toast.makeText(context, errorText,15).show();
                                                return;
                                        }
                                }
                                }
                                else{
                                CharSequence errorText = "Unable to connect to the server. Please try again.";
                                Toast.makeText(context, errorText,15).show();

                                }

                        }
                        else
                        {
                                CharSequence errorText = "Please re-enter your passwords! They were not the same.";
                                Toast.makeText(context, errorText,15).show();
                                return;

                        }
                }
                else
                {
                        CharSequence text = "Please Input a User Name and the same password twice!";
                        Toast.makeText(context, text, 15).show();
                        return;
                }

        }});

        Button back = (Button) findViewById(R.id.Back);
        back.setOnClickListener(new View.OnClickListener()
        {
                public void onClick(View view)
                {
                        Intent myIntent = new Intent(view.getContext(), GUI.class);
                        startActivity(myIntent);
                }});
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

  //  Request Type: Create User
   // Fields: First Name, Last Name, User Name, Password, Account Type

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
