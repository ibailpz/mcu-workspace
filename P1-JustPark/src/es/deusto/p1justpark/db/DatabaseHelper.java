package es.deusto.p1justpark.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String TABLE_PARKINGS = "parkings";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_ADDRESS = "address";
	public static final String COLUMN_PLACES = "places";
	public static final String COLUMN_LAT = "lat";
	public static final String COLUMN_LONG = "long";
	public static final String COLUMN_NOTIFICATIONS = "notifications";
	public static final String COLUMN_FAVOURITE = "favourite";

	private static final String DATABASE_NAME = "parkings.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_PARKINGS + "(" + COLUMN_ID + " integer primary key, "
			+ COLUMN_NAME + " text not null, " + COLUMN_ADDRESS
			+ " text not null, " + COLUMN_PLACES + " text not null, "
			+ COLUMN_LAT + " real not null, " + COLUMN_LONG
			+ " real not null, " + COLUMN_NOTIFICATIONS + " integer not null, "
			+ COLUMN_FAVOURITE + " integer not null);";

	DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DatabaseHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARKINGS);
		onCreate(db);
	}

}