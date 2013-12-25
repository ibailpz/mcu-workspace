package es.deusto.p1justpark.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import es.deusto.p1justpark.R;
import es.deusto.p1justpark.util.Utilities;

public class ParkingUpdateService extends IntentService {

	public ParkingUpdateService() {
		super("ParkingUpdateService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(getClass().getSimpleName(), "ParkingUpdateService");
		Utilities.updateParkingsFromJSON(this);

		int[] array = getResources().getIntArray(R.array.general_times);
		int time = array[Integer.parseInt(PreferenceManager
				.getDefaultSharedPreferences(this).getString(
						"general_interval", "0"))];

		AlarmManager am = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getService(this, 0, new Intent(this,
				ParkingUpdateService.class), PendingIntent.FLAG_CANCEL_CURRENT);
		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pi);
	}
}
