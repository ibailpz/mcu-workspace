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
		findPreference("notificacions_interval").setSummary(
				array[Integer.parseInt(PreferenceManager
						.getDefaultSharedPreferences(getActivity()).getString(
								"notificacions_interval", "0"))]);

		array = getResources().getStringArray(R.array.notificacions_interval);
		findPreference("general_interval").setSummary(
				array[Integer.parseInt(PreferenceManager
						.getDefaultSharedPreferences(getActivity()).getString(
								"general_interval", "0"))]);

		findPreference("notificacions_interval").setEnabled(
				PreferenceManager.getDefaultSharedPreferences(getActivity())
						.getBoolean("switch_notifications", true));
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals("switch_notifications")) {
			findPreference("notificacions_interval").setEnabled(
					sharedPreferences.getBoolean(key, true));
		} else if (key.equals("notificacions_interval")) {
			String[] array = getResources().getStringArray(
					R.array.notificacions_interval);
			findPreference(key).setSummary(
					array[Integer.parseInt(sharedPreferences
							.getString(key, "0"))]);
		} else if (key.equals("general_interval")) {
			String[] array = getResources().getStringArray(
					R.array.general_interval);
			findPreference(key).setSummary(
					array[Integer.parseInt(sharedPreferences
							.getString(key, "0"))]);
		}
	}

}
