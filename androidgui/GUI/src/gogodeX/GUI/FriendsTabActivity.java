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

public class FriendsTabActivity extends ListActivity{
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
	}
	
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	row = position;
    	Button remove = (Button) findViewById(R.id.friend_remove);
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, "Clicked", duration);
		toast.show();
    	remove.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View view) {
        		Context context = getApplicationContext();
        		int duration = Toast.LENGTH_SHORT;
        		Toast toast = Toast.makeText(context, "REMOVE IT!!!", duration);
        		toast.show();
        		if (row != -1) {
        			m_list.remove(m_list.getItem(row));
        			row = -1;       			
        		}
        			
        	}        	
        });    	
    }
}