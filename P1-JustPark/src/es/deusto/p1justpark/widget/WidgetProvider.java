package es.deusto.p1justpark.widget;

import java.util.ArrayList;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;
import es.deusto.p1justpark.R;
import es.deusto.p1justpark.data.Parking;

public class WidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		// We must iterate all the widget instances
		for (int i = 0; i < appWidgetIds.length; i++) {
			int widgetId = appWidgetIds[i];

			ArrayList<Parking> favoriteParkings = new ArrayList<Parking>();
			favoriteParkings.add(new Parking(1, "Parking Plaza Euskadi",
					"Plaza Euskadi Bilbao", "100", 43.26723, -2.93839, false, true));

			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.widget_layout);
			Parking parking = favoriteParkings.get(0);
			if (parking != null) {
				views.setTextViewText(R.id.widget_parking_name,
						parking.getName());
				views.setTextViewText(R.id.widget_parking_places, parking
						.getPlaces().toString());
			}

			appWidgetManager.updateAppWidget(widgetId, views);
		}
	}

}
