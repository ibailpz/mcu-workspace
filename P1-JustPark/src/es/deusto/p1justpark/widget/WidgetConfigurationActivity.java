package es.deusto.p1justpark.widget;

import android.app.Activity;
import android.app.ListActivity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import es.deusto.p1justpark.db.ParkingsDatasource;
import es.deusto.p1justpark.ui.ParkingsAdapter;

public class WidgetConfigurationActivity extends ListActivity {

	private static final String PREFS_NAME = WidgetProvider.class.getName();
	private static final String PREF_PREFIX_KEY = "widget_";

	private int mAppWidgetId;
	private ParkingsAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		setResult(Activity.RESULT_CANCELED);
		if (ParkingsDatasource.getInstance() == null) {
			ParkingsDatasource.initDatasource(this);
		}
		adapter = new ParkingsAdapter(this, ParkingsDatasource.getInstance()
				.getAllParkings());
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		int parkingId = adapter.getItem(position).getId();
		saveParkingPref(this, mAppWidgetId, parkingId);
		updateWidget(parkingId);

		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);
		finish();
	}

	private void updateWidget(int parkingId) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		WidgetProvider.updateAppWidget(this, appWidgetManager, mAppWidgetId,
				parkingId);
	}

	private static void saveParkingPref(Context context, int appWidgetId, int id) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				PREFS_NAME, 0).edit();
		prefs.putInt(PREF_PREFIX_KEY + appWidgetId, id);
		prefs.commit();
	}

	static int loadParkingPref(Context context, int appWidgetId) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		return prefs.getInt(PREF_PREFIX_KEY + appWidgetId, -1);
	}

	static void deleteParkingPref(Context context, int appWidgetId) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		prefs.edit().remove(PREF_PREFIX_KEY + appWidgetId).commit();
	}

}
