package gogodeX.GUI;

import android.R.string;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.EditText;
import android.text.Editable;

public class FriendsList extends ListActivity {
	private ArrayAdapter<String> m_list;
	int row = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.friends);

        m_list = new  ArrayAdapter<String>(this, R.layout.friends_rows, R.id.name);
	    m_list.add("John");
	    m_list.add("Doe");
	    setListAdapter(m_list);
	    
    	Button add = (Button) findViewById(R.id.add_friend);
    	add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = ((EditText)findViewById(R.id.friend_name_textbox)).getText().toString();
				if(!name.trim().equals("")) {
					m_list.add(name);
					((EditText)findViewById(R.id.friend_name_textbox)).setText("");
	        		Context context = getApplicationContext();
	        		int duration = Toast.LENGTH_SHORT;
            		Toast toast = Toast.makeText(context, "Hello "+name, duration);
            		toast.show();
				}
			}
		});
       
	}

    protected void onListItemClick(ListView l, View v, int position, long id) {
    	row = position;
    	Button remove = (Button) findViewById(R.id.remove_friend);
		//Context context = getApplicationContext();
		//int duration = Toast.LENGTH_SHORT;
		//Toast toast = Toast.makeText(context, "Clicked", duration);
		//toast.show();
    	((EditText)findViewById(R.id.friend_name_textbox)).setText(m_list.getItem(row));
    	
    	remove.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View view) {
        		Context context = getApplicationContext();
        		int duration = Toast.LENGTH_SHORT;
        		if (row != -1) {
            		Toast toast = Toast.makeText(context, "Good bye "+m_list.getItem(row), duration);
            		toast.show();
        			m_list.remove(m_list.getItem(row));
        			row = -1;       			
        		}
        	}        	
        });    	
    }
    
}
