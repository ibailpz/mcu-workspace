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

		PreferenceManager.getDefaultSharedPreferences(getActivity())
				.registerOnSharedPreferenceChangeListener(this);

		String[] array = getResources()
				.getStringArray(R.array.general_interval);

		findPreference(getResources().getString(R.string.general_interval_key))
				.setSummary(
						array[Integer.parseInt(PreferenceManager
								.getDefaultSharedPreferences(getActivity())
								.getString(
										getResources().getString(
												R.string.general_interval_key),
										"0"))]);

		if (PreferenceManager
				.getDefaultSharedPreferences(getActivity())
				.getBoolean(
						getResources().getString(R.string.automatic_update_key),
						true)) {
			findPreference(
					getResources().getString(R.string.general_interval_key))
					.setEnabled(true);
			findPreference(
					getResources().getString(R.string.switch_notifications_key))
					.setEnabled(true);
		} else {
			findPreference(
					getResources().getString(R.string.general_interval_key))
					.setEnabled(false);
			findPreference(
					getResources().getString(R.string.switch_notifications_key))
					.setEnabled(false);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(getResources().getString(R.string.general_interval_key))) {
			String[] array = getResources().getStringArray(
					R.array.general_interval);
			findPreference(key).setSummary(
					array[Integer.parseInt(sharedPreferences
							.getString(key, "0"))]);
		} else if (key.equals(getResources().getString(
				R.string.automatic_update_key))) {
			if (sharedPreferences.getBoolean(key, true)) {
				findPreference(
						getResources().getString(R.string.general_interval_key))
						.setEnabled(true);
				findPreference(
						getResources().getString(
								R.string.switch_notifications_key)).setEnabled(
						true);
			} else {
				findPreference(
						getResources().getString(R.string.general_interval_key))
						.setEnabled(false);
				findPreference(
						getResources().getString(
								R.string.switch_notifications_key)).setEnabled(
						false);
			}
		}
	}

}
