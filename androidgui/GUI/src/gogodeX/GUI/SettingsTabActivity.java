package gogodeX.GUI;

import android.os.Bundle;
import android.preference.*;

public class SettingsTabActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}