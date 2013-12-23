package es.deusto.p1justpark.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import es.deusto.p1justpark.data.Parking;

public class Utilities {

	private static final String PARKINGS_JSON = "json.internetdelascosas.es/arduino/getlast.php";
	private static final String PARKING_ID = "?device_id=";
	private static final String DATA_NAME = "&data_name=";
	private static final String NUM_ITEMS = "&nitems";

	public ArrayList<Parking> updateParkingsJSON(ArrayList<Parking> parkings) {
		HttpURLConnection conn = null;
		// We have 5 parkings in the database
		for (int i = 0; i < 5; i++) {
			String places = "0";
			// TODO Get and parse JSON
			StringBuilder jsonResults = new StringBuilder();
			try {
				StringBuilder sb = new StringBuilder(PARKINGS_JSON);
				sb.append(PARKING_ID + i);
				sb.append(DATA_NAME + "parking");
				sb.append(NUM_ITEMS + 1);

				URL url = new URL(sb.toString());
				conn = (HttpURLConnection) url.openConnection();
				InputStreamReader in = new InputStreamReader(
						conn.getInputStream());

				int read;
				char[] buff = new char[1024];
				while ((read = in.read(buff)) != -1) {
					jsonResults.append(buff, 0, read);
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return parkings;
			} catch (IOException e) {
				e.printStackTrace();
				return parkings;
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}

			try {
				JSONObject jsonObj = new JSONObject(jsonResults.toString());
				places = jsonObj.getString("data_value");
				System.out.println(places);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			parkings.get(i).setPlaces(places);
		}
		return parkings;
	}

}
