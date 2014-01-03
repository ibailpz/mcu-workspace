package es.deusto.p1justpark.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import es.deusto.p1justpark.R;
import es.deusto.p1justpark.data.Parking;
import es.deusto.p1justpark.ui.ParkingsActivity;

public class ParkingsDatasource {

	private static final ArrayList<DatabaseObserver> observers = new ArrayList<DatabaseObserver>();

	public static void addDatabaseObserver(DatabaseObserver o) {
		observers.add(o);
	}

	public static void removeDatabaseObserver(DatabaseObserver o) {
		observers.remove(o);
	}

	public static ParkingsDatasource instance;

	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	private Context context;
	private String[] allColumns = { DatabaseHelper.COLUMN_ID,
			DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_ADDRESS,
			DatabaseHelper.COLUMN_PLACES, DatabaseHelper.COLUMN_LAT,
			DatabaseHelper.COLUMN_LONG, DatabaseHelper.COLUMN_NOTIFICATIONS,
			DatabaseHelper.COLUMN_FAVOURITE, DatabaseHelper.COLUMN_UPDATED };

	public static void initDatasource(Context ctx) {
		instance = new ParkingsDatasource(ctx);
	}

	public static ParkingsDatasource getInstance() {
		if (instance != null && instance.database == null) {
			instance.open();
		}
		return instance;
	}

	private ParkingsDatasource(Context context) {
		this.context = context;
		dbHelper = new DatabaseHelper(context);
	}

	private void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
		database = null;
	}

	public boolean createParking(Parking parking) {
		ContentValues values = getContentValues(parking);
		long insertId = database.insert(DatabaseHelper.TABLE_PARKINGS, null,
				values);
		return insertId > -1;
	}

	private ContentValues getContentValues(Parking parking) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_ID, parking.getId());
		values.put(DatabaseHelper.COLUMN_NAME, parking.getName());
		values.put(DatabaseHelper.COLUMN_ADDRESS, parking.getAddress());
		values.put(DatabaseHelper.COLUMN_PLACES, parking.getPlaces());
		values.put(DatabaseHelper.COLUMN_LAT, parking.getLat());
		values.put(DatabaseHelper.COLUMN_LONG, parking.getLng());
		values.put(DatabaseHelper.COLUMN_NOTIFICATIONS,
				parking.isNotifications() ? 1 : 0);
		values.put(DatabaseHelper.COLUMN_FAVOURITE, parking.isFavourite() ? 1
				: 0);
		values.put(DatabaseHelper.COLUMN_UPDATED, parking.getLastUpdatedTime()
				.getTime());
		return values;
	}

	public List<Parking> getAllParkings() {
		List<Parking> parkings = new ArrayList<Parking>();

		Cursor cursor = database.query(DatabaseHelper.TABLE_PARKINGS,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Parking p = cursorToParking(cursor);
			parkings.add(p);
			cursor.moveToNext();
		}
		cursor.close();
		return parkings;
	}

	public List<Parking> getFavouriteParkings() {
		List<Parking> parkings = new ArrayList<Parking>();
		Cursor cursor = database.query(DatabaseHelper.TABLE_PARKINGS,
				allColumns, DatabaseHelper.COLUMN_FAVOURITE + " = 1", null,
				null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Parking p = cursorToParking(cursor);
			parkings.add(p);
			cursor.moveToNext();
		}
		cursor.close();
		return parkings;
	}

	public Parking getParking(int id) {
		Cursor cursor = database.query(DatabaseHelper.TABLE_PARKINGS,
				allColumns, DatabaseHelper.COLUMN_ID + " = " + id, null, null,
				null, null);
		cursor.moveToFirst();
		Parking p = cursorToParking(cursor);
		cursor.close();
		return p;
	}

	public void updateParkingSettings(Parking p) {
		updateParking(p, true);
	}

	private void updateParking(Parking p, boolean notify) {
		ContentValues values = getContentValues(p);
		database.update(DatabaseHelper.TABLE_PARKINGS, values,
				DatabaseHelper.COLUMN_ID + " = " + p.getId(), null);
		if (notify) {
			ArrayList<Parking> al = new ArrayList<Parking>(1);
			notifyObservers(al);
		}
	}

	public void updateAllParkings(List<Parking> parkings) {
		ArrayList<Parking> updated = new ArrayList<Parking>();
		Date now = new Date();
		for (Parking p : parkings) {
			Parking orig = getParking(p.getId());
			if (!orig.getPlaces().equals(p.getPlaces())) {
				updated.add(p);
			}
			p.setLastUpdatedTime(now);
			updateParking(p, false);
		}
		notifyObservers(updated);
	}

	private void notifyObservers(List<Parking> updated) {
		for (DatabaseObserver d : observers) {
			d.onUpdate();
		}
		if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
				"automatic_update", false)
				&& PreferenceManager.getDefaultSharedPreferences(context)
						.getBoolean("switch_notifications", false)) {
			StringBuilder sb = new StringBuilder();
			for (Parking p : updated) {
				if (p.isNotifications()) {
					sb.append(p.getName()).append(": ").append(p.getPlaces())
							.append('\n');
				}
			}
			if (sb.length() > 0) {
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						context)
						.setSmallIcon(R.drawable.ic_launcher)
						.setContentTitle(
								context.getResources().getString(
										R.string.app_name)).setContentText(sb);
				Intent resultIntent = new Intent(context,
						ParkingsActivity.class);
				PendingIntent resultPendingIntent = PendingIntent.getActivity(
						context, 0, resultIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				mBuilder.setContentIntent(resultPendingIntent);
				int mNotificationId = 001;
				NotificationManager mNotifyMgr = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				Notification build = mBuilder.build();
				build.flags = Notification.FLAG_AUTO_CANCEL;
				mNotifyMgr.notify(mNotificationId, build);
			}
		}
	}

	private Parking cursorToParking(Cursor cursor) {
		Parking parking = new Parking();
		parking.setId(cursor.getInt(0));
		parking.setName(cursor.getString(1));
		parking.setAddress(cursor.getString(2));
		parking.setPlaces(cursor.getString(3));
		parking.setLat(cursor.getDouble(4));
		parking.setLng(cursor.getDouble(5));
		parking.setNotifications(cursor.getInt(6) != 0);
		parking.setFavourite(cursor.getInt(7) != 0);
		parking.setLastUpdatedTime(new Date(cursor.getLong(8)));
		return parking;
	}
}
