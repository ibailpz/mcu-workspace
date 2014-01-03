package es.deusto.p1justpark.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.Toast;
import es.deusto.p1justpark.R;
import es.deusto.p1justpark.data.Parking;
import es.deusto.p1justpark.db.DatabaseObserver;
import es.deusto.p1justpark.db.ParkingsDatasource;
import es.deusto.p1justpark.services.ParkingUpdateService;
import es.deusto.p1justpark.util.Utilities;

public class ParkingsActivity extends Activity implements
		ActionBar.OnNavigationListener, AdapterObserver, DatabaseObserver {

	private static final int settingsIntent = 1;
	private static final String STATE_TYPE_LIST = "all_or_favorites";

	private ArrayList<Parking> arrParkings;
	private ArrayList<Parking> arrFavoriteParkings;

	private Fragment list;
	private Fragment favs;
	private ArrayAdapter<Parking> currentAdapter;
	private ActionMode mActionMode;
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			ShareActionProvider shareProv = (ShareActionProvider) menu
					.findItem(R.id.parking_share).getActionProvider();
			shareProv
					.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(
					Intent.EXTRA_TEXT,
					currentAdapter.getItem(
							((ListView) findViewById(android.R.id.list))
									.getCheckedItemPosition()).getName()
							+ " "
							+ currentAdapter
									.getItem(
											((ListView) findViewById(android.R.id.list))
													.getCheckedItemPosition())
									.getPlaces());
			shareProv.setShareIntent(intent);

			((ListView) findViewById(android.R.id.list)).setEnabled(false);
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			if (((ListView) findViewById(android.R.id.list))
					.getCheckedItemPosition() >= 0)
				((ListView) findViewById(android.R.id.list)).setItemChecked(
						((ListView) findViewById(android.R.id.list))
								.getCheckedItemPosition(), false);
			((ListView) findViewById(android.R.id.list)).setEnabled(true);
			mActionMode = null;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.parking_action, menu);
			int position = ((ListView) findViewById(android.R.id.list))
					.getCheckedItemPosition();
			if (position >= 0
					&& !currentAdapter.getItem(position).isFavourite()) {
				menu.findItem(R.id.parking_favorite).setIcon(
						R.drawable.ic_action_not_favorite);
				menu.findItem(R.id.parking_favorite).setTitle(
						R.string.parking_add_favorite);
			}
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			final int position = ((ListView) findViewById(android.R.id.list))
					.getCheckedItemPosition();

			switch (item.getItemId()) {
			case R.id.parking_favorite:
				Parking parking = currentAdapter.getItem(position);
				if (parking.isFavourite()) {
					parking.setFavourite(false);
					Toast.makeText(
							ParkingsActivity.this,
							String.format(
									getResources().getString(
											R.string.remove_favourite),
									parking.getName()), Toast.LENGTH_SHORT)
							.show();
				} else {
					parking.setFavourite(true);
					Toast.makeText(
							ParkingsActivity.this,
							String.format(
									getResources().getString(
											R.string.added_favourite),
									parking.getName()), Toast.LENGTH_SHORT)
							.show();
				}
				ParkingsDatasource.getInstance().updateParkingSettings(parking);
				mode.finish();
				return true;
			case R.id.parking_navigate:
				new AsyncTask<Void, Void, Location>() {
					@Override
					protected Location doInBackground(Void... params) {
						return Utilities.getLocation(ParkingsActivity.this);
					}

					protected void onPostExecute(Location result) {
						if (result == null) {
							Toast.makeText(ParkingsActivity.this,
									R.string.location_error, Toast.LENGTH_LONG)
									.show();
							return;
						}
						Intent intent = new Intent(
								android.content.Intent.ACTION_VIEW,
								Uri.parse("http://maps.google.com/maps?saddr="
										+ result.getLatitude()
										+ ","
										+ result.getLongitude()
										+ "&daddr="
										+ currentAdapter.getItem(position)
												.getLat()
										+ ","
										+ currentAdapter.getItem(position)
												.getLng()));
						startActivity(intent);
					}
				}.execute();
				mode.finish();
				return true;
			case R.id.parking_share:
				return true;
			default:
				return false;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ParkingsDatasource.addDatabaseObserver(this);
		ParkingsDatasource.initDatasource(this);
		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
				"automatic_update", true)) {
			startService(new Intent(this, ParkingUpdateService.class));
		}
		createParkingsList();
		loadParkings();
		setActionBar(getActionBar());
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.parking_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.mnu_search_closest) {
			new AsyncTask<Void, Void, Location>() {

				private Location user;

				@Override
				protected Location doInBackground(Void... params) {
					user = Utilities.getLocation(ParkingsActivity.this);
					if (user == null) {
						return null;
					}
					List<Parking> parkings = ParkingsDatasource.getInstance()
							.getAllParkings();
					Location min = new Location("");
					min.setLatitude(-user.getLatitude());
					min.setLongitude(-user.getLongitude());
					float minDist = user.distanceTo(min);
					for (Parking p : parkings) {
						Location temp = new Location("");
						temp.setLatitude(p.getLat());
						temp.setLongitude(p.getLng());
						float tempDist = user.distanceTo(temp);
						if (Integer.parseInt(p.getPlaces()) > 0
								&& tempDist < minDist) {
							minDist = tempDist;
							min = temp;
						}
					}
					return min;
				}

				@Override
				protected void onPostExecute(Location result) {
					if (result == null) {
						Toast.makeText(ParkingsActivity.this,
								R.string.location_error, Toast.LENGTH_LONG)
								.show();
						return;
					}
					Intent intent = new Intent(
							android.content.Intent.ACTION_VIEW,
							Uri.parse("http://maps.google.com/maps?saddr="
									+ user.getLatitude() + ","
									+ user.getLongitude() + "&daddr="
									+ result.getLatitude() + ","
									+ result.getLongitude()));
					startActivity(intent);
				}
			}.execute();
		} else if (id == R.id.action_settings) {
			Intent intent = new Intent(this,
					es.deusto.p1justpark.settings.SettingsActivity.class);
			startActivityForResult(intent, settingsIntent);
		}
		return super.onOptionsItemSelected(item);
	}

	private void setActionBar(ActionBar actionBar) {
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		final String[] dropdownValues = { "All", "Favorites" };

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActionBar()
				.getThemedContext(), android.R.layout.simple_spinner_item,
				android.R.id.text1, dropdownValues);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		actionBar.setListNavigationCallbacks(adapter, this);

		onNavigationItemSelected(0, 0);
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		Fragment fragment;
		Bundle args = new Bundle();
		switch (position) {
		case 0:
			// TOD Inflate adapter with arrParkings from BD
			if (list == null) {
				list = new ParkingsFragment();
				args.putParcelableArrayList(ParkingsFragment.PARKINGS_ARRAY,
						arrParkings);
				list.setArguments(args);
			} else {
				list.getArguments().putParcelableArrayList(
						ParkingsFragment.PARKINGS_ARRAY, arrParkings);
			}
			fragment = list;
			break;
		case 1:
			// TOD Inflate adapter with arrFavoriteParkings from BD
			args.putParcelableArrayList(ParkingsFragment.PARKINGS_ARRAY,
					arrFavoriteParkings);
			if (favs == null) {
				favs = new ParkingsFragment();
				args.putParcelableArrayList(ParkingsFragment.PARKINGS_ARRAY,
						arrFavoriteParkings);
				favs.setArguments(args);
			} else {
				favs.getArguments().putParcelableArrayList(
						ParkingsFragment.PARKINGS_ARRAY, arrFavoriteParkings);
			}
			fragment = favs;
			break;
		default:
			fragment = list;
		}
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, fragment).commit();
		return true;
	}

	private void setCAB() {
		((ListView) findViewById(android.R.id.list))
				.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		((ListView) findViewById(android.R.id.list))
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						if (mActionMode != null) {
							return false;
						}
						((ListView) findViewById(android.R.id.list))
								.setItemChecked(position, true);
						mActionMode = ParkingsActivity.this
								.startActionMode(mActionModeCallback);
						return true;
					}
				});
	}

	private void loadParkings() {
		if (arrParkings == null) {
			arrParkings = new ArrayList<Parking>();
		}
		arrParkings.clear();
		arrParkings.addAll(ParkingsDatasource.getInstance().getAllParkings());
		if (arrFavoriteParkings == null) {
			arrFavoriteParkings = new ArrayList<Parking>();
		}
		arrFavoriteParkings.clear();
		arrFavoriteParkings.addAll(ParkingsDatasource.getInstance()
				.getFavouriteParkings());
	}

	@Override
	public void onAdapterChanged(ArrayAdapter<Parking> adapter) {
		currentAdapter = adapter;
		setCAB();
	}

	@Override
	public void onUpdate() {
		loadParkings();
		if (currentAdapter != null) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					currentAdapter.notifyDataSetChanged();
				}
			});
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == settingsIntent) {
			if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
					"automatic_update", true)) {
				startService(new Intent(this, ParkingUpdateService.class));
			} else {
				AlarmManager am = (AlarmManager) this
						.getSystemService(Context.ALARM_SERVICE);
				PendingIntent pi = ParkingUpdateService
						.getServicePendingIntent(this);
				am.cancel(pi);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ParkingsDatasource.removeDatabaseObserver(this);
		ParkingsDatasource.getInstance().close();
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized dropdown position
		if (savedInstanceState.containsKey(STATE_TYPE_LIST)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_TYPE_LIST));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position
		outState.putInt(STATE_TYPE_LIST, getActionBar()
				.getSelectedNavigationIndex());
	}

	private void createParkingsList() {
		if (ParkingsDatasource.getInstance().getAllParkings().isEmpty()) {
			ArrayList<Parking> list = new ArrayList<Parking>();
			list.add(new Parking(200, "Parking Plaza Euskadi",
					"Plaza Euskadi, 48009 Bilbao, Bizkaia", "", 43.26723,
					-2.93839, false, false, new Date()));
			list.add(new Parking(201, "Parking El Corte Ingles",
					"Gran Via 19, 48009 Bilbao, Bizkaia", "", 43.261950, -2.930847, false, false,
					new Date()));
			list.add(new Parking(202, "Parking Indautxu",
					"Urquijo Aldapa 65, 48013 Bilbao, Bizkaia", "", 43.261201, -2.942305, false, false,
					new Date()));
			list.add(new Parking(203, "Parking Pio Baroja",
					"Plaza de Pío Baroja, 48001 Bilbao, Bizkaia", "", 43.264151, -2.925873, false, false,
					new Date()));
			list.add(new Parking(204, "Parking Instituto Miguel de Unamuno",
					"Calle Urquijo 14, 48009 Bilbao, Bizkaia", "", 43.264358, -2.932303, false, false,
					new Date()));
			for (Parking p : list) {
				ParkingsDatasource.getInstance().createParking(p);
			}
		}
	}
}
