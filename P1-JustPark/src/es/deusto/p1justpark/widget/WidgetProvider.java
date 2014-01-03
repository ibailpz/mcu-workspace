package es.deusto.p1justpark.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;
import es.deusto.p1justpark.R;
import es.deusto.p1justpark.data.Parking;
import es.deusto.p1justpark.db.ParkingsDatasource;

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
		}

		appWidgetManager.updateAppWidget(appWidgetId, views);
	}

}
