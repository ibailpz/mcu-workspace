package es.deusto.p1justpark.settings;

import android.app.Activity;
import android.os.Bundle;
import es.deusto.p1justpark.R;

public class SettingsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_settings);

		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingsFragment()).commit();
	}
}
