package es.deusto.p1justpark.settings;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import es.deusto.p1justpark.R;

public class SettingsFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		// Register for changes in preferences
		PreferenceManager.getDefaultSharedPreferences(getActivity())
				.registerOnSharedPreferenceChangeListener(this);

		String[] array = getResources()
				.getStringArray(R.array.general_interval);

		findPreference("general_interval").setSummary(
				array[Integer.parseInt(PreferenceManager
						.getDefaultSharedPreferences(getActivity()).getString(
								"general_interval", "0"))]);

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals("general_interval")) {
			String[] array = getResources().getStringArray(
					R.array.general_interval);
			findPreference(key).setSummary(
					array[Integer.parseInt(sharedPreferences
							.getString(key, "0"))]);
		}
	}

}
