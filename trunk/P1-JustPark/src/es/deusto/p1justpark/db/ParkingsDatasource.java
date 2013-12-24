package es.deusto.p1justpark.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import es.deusto.p1justpark.data.Parking;

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
	private String[] allColumns = { DatabaseHelper.COLUMN_ID,
			DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_ADDRESS,
			DatabaseHelper.COLUMN_PLACES, DatabaseHelper.COLUMN_LAT,
			DatabaseHelper.COLUMN_LONG, DatabaseHelper.COLUMN_NOTIFICATIONS,
			DatabaseHelper.COLUMN_FAVOURITE };
	
	public static void initDatasource(Context ctx) {
		instance = new ParkingsDatasource(ctx);
		instance.open();
	}
	
	public static ParkingsDatasource getInstance() {
		return instance;
	}

	private ParkingsDatasource(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	private void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
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
		values.put(DatabaseHelper.COLUMN_FAVOURITE,
				parking.isFavourite() ? 1 : 0);
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

	public void updateParking(Parking p) {
		ContentValues values = getContentValues(p);
		database.update(DatabaseHelper.TABLE_PARKINGS, values,
				DatabaseHelper.COLUMN_ID + " = " + p.getId(), null);
		notifyObservers();
	}

	private void notifyObservers() {
		for (DatabaseObserver d : observers) {
			d.onUpdate();
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
		return parking;
	}
}
