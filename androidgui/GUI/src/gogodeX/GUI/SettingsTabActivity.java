package gogodeX.GUI;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SettingsTabActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("This is the settings tab");
        setContentView(textview);
    }
}