package es.deusto.p1justpark.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import es.deusto.p1justpark.data.Parking;
import es.deusto.p1justpark.db.ParkingsDatasource;
import es.deusto.p1justpark.widget.WidgetProvider;

public final class Utilities {

	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private static final String URL = "http://json.internetdelascosas.es/arduino/getlast.php";
	private static final String PARKING_ID = "?device_id=";
	private static final String DATA_NAME = "&data_name=parking";
	private static final String NUM_ITEMS = "&nitems=1";

	private Utilities() {
	}

	public static void updateParkingsFromJSON(Context ctx) {
		if (ParkingsDatasource.getInstance() == null) {
			ParkingsDatasource.initDatasource(ctx);
		}
		List<Parking> parkings = ParkingsDatasource.getInstance()
				.getAllParkings();
		HttpURLConnection conn = null;
		StringBuilder jsonResults;
		for (int i = 0; i < parkings.size(); i++) {
			jsonResults = new StringBuilder();
			try {
				StringBuilder sb = new StringBuilder(URL);
				sb.append(PARKING_ID).append(parkings.get(i).getId());
				sb.append(DATA_NAME);
				sb.append(NUM_ITEMS);

				URL url = new URL(sb.toString());
				conn = (HttpURLConnection) url.openConnection();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				String line;
				while ((line = br.readLine()) != null) {
					jsonResults.append(line);
				}

				JSONArray jsonArr = new JSONArray(jsonResults.toString());
				JSONObject jsonObj = jsonArr.getJSONObject(0);
				parkings.get(i).setPlaces(jsonObj.getString("data_value"));
				parkings.get(i).setLastUpdatedTime(
						sdf.parse(jsonObj.getString("timestamp")));
			} catch (MalformedURLException e) {
				Log.w(Utilities.class.getSimpleName(), e.getMessage(), e);
			} catch (IOException e) {
				Log.w(Utilities.class.getSimpleName(), e.getMessage(), e);
			} catch (JSONException e) {
				Log.w(Utilities.class.getSimpleName(), e.getMessage(), e);
			} catch (ParseException e) {
				Log.w(Utilities.class.getSimpleName(), e.getMessage(), e);
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}
		}
		ParkingsDatasource.getInstance().updateAllParkings(parkings);
		updateWidgets(ctx);
	}

	private static void updateWidgets(Context ctx) {
		int ids[] = AppWidgetManager.getInstance(ctx).getAppWidgetIds(
				new ComponentName(ctx, WidgetProvider.class));
		Intent intent = new Intent(ctx, WidgetProvider.class);
		intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
		ctx.sendBroadcast(intent);
	}

	public static void getLocation() {
		// TODO Get user location
	}
}
