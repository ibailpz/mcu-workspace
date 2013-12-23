package es.deusto.p1justpark.settings;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import es.deusto.p1justpark.R;

public class MySettingsFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		// Register for changes in preferences
		PreferenceManager.getDefaultSharedPreferences(getActivity())
				.registerOnSharedPreferenceChangeListener(this);

		findPreference("notificacions_interval").setSummary(
				PreferenceManager.getDefaultSharedPreferences(getActivity())
						.getString("notificacions_interval", ""));
		findPreference("general_interval").setSummary(
				PreferenceManager.getDefaultSharedPreferences(getActivity())
						.getString("general_interval", ""));
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

		if (key.equals("notificacions_interval")) {
			findPreference(key)
					.setSummary(sharedPreferences.getString(key, ""));
		} else if (key.equals("general_interval")) {
			findPreference(key)
					.setSummary(sharedPreferences.getString(key, ""));
		}
	}

}
