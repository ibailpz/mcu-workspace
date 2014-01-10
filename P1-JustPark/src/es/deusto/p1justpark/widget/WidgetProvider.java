package es.deusto.p1justpark.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import es.deusto.p1justpark.R;
import es.deusto.p1justpark.data.Parking;
import es.deusto.p1justpark.db.ParkingsDatasource;
import es.deusto.p1justpark.ui.ParkingsActivity;

public class WidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		if (ParkingsDatasource.getInstance() == null) {
			ParkingsDatasource.initDatasource(context);
		}

		for (int i = 0; i < appWidgetIds.length; i++) {
			int widgetId = appWidgetIds[i];
			int parkingId = WidgetConfigurationActivity.loadParkingPref(
					context, widgetId);
			if (parkingId > -1) {
				updateAppWidget(context, appWidgetManager, widgetId, parkingId);
			}
		}
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		for (int i = 0; i < appWidgetIds.length; i++) {
			WidgetConfigurationActivity.deleteParkingPref(context,
					appWidgetIds[i]);
		}
	}

	static void updateAppWidget(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId, int parkingId) {
		Parking parking = ParkingsDatasource.getInstance()
				.getParking(parkingId);

		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);
		if (parking != null) {
			views.setTextViewText(R.id.widget_parking_name, parking.getName());
			views.setTextViewText(R.id.widget_parking_places, parking
					.getPlaces().toString());

			Intent intent = null;
			int widgetOption = Integer.parseInt(PreferenceManager
					.getDefaultSharedPreferences(context).getString(
							context.getResources().getString(
									R.string.widget_preference_key), "0"));

			if (widgetOption == 0) {
				intent = new Intent(context, ParkingsActivity.class);
				intent.putExtra(ParkingsActivity.LAUNCH_PARKING_KEY, parking);
			} else if (widgetOption == 1) {
				intent = new Intent(android.content.Intent.ACTION_VIEW,
						Uri.parse("http://maps.google.com/maps?q="
								+ parking.getLat() + "," + parking.getLng()));
			}

			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					intent, PendingIntent.FLAG_CANCEL_CURRENT);
			views.setOnClickPendingIntent(R.id.widget, pendingIntent);
		}

		appWidgetManager.updateAppWidget(appWidgetId, views);
	}

}
