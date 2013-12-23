package es.deusto.p1justpark.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.content.Context;

public class ParkingManager {
	
	private static final String FILENAME = "EventsList";
	private Context mContext;

	public ParkingManager(Context c) {
		mContext = c;
	}

	public ArrayList<Parking> loadParkings() {
		try {
			FileInputStream fis = mContext.openFileInput(FILENAME);
			ObjectInputStream ois = new ObjectInputStream(fis);
			@SuppressWarnings("unchecked")
			ArrayList<Parking> arrParkings = (ArrayList<Parking>) ois.readObject();
			ois.close();
			fis.close();
			return arrParkings;
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (OptionalDataException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<Parking> loadFavoriteParkings() {
		try {
			FileInputStream fis = mContext.openFileInput(FILENAME);
			ObjectInputStream ois = new ObjectInputStream(fis);
			@SuppressWarnings("unchecked")
			ArrayList<Parking> arrFavoriteParkings = (ArrayList<Parking>) ois.readObject();
			ois.close();
			fis.close();
			return arrFavoriteParkings;
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (OptionalDataException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void saveParkings(ArrayList<Parking> arrParkings) {
		try {
			FileOutputStream fos = mContext.openFileOutput(FILENAME,
					Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(arrParkings);
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveFavoriteParkings(ArrayList<Parking> arrFavoriteParkings) {
		try {
			FileOutputStream fos = mContext.openFileOutput(FILENAME,
					Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(arrFavoriteParkings);
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
