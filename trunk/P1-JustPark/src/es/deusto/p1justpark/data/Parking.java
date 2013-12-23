package es.deusto.p1justpark.data;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class Parking implements Serializable, Parcelable {

	private static final long serialVersionUID = -7200823720806189295L;

	private int id;
	private String name;
	private String address;
	private String places;
	private double lat;
	private double lng;
	private boolean notifications;

	public Parking(int id, String name, String address, String places,
			double lat, double lng, boolean notificacions) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.places = places;
		this.lat = lat;
		this.lng = lng;
		this.notifications = notificacions;
	}

	public Parking(Parcel in) {
		id = in.readInt();
		name = in.readString();
		address = in.readString();
		places = in.readString();
		lat = in.readDouble();
		lng = in.readDouble();
		notifications = ((in.readInt() == 0) ? false : true);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPlaces() {
		return places;
	}

	public void setPlaces(String places) {
		this.places = places;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public boolean isNotifications() {
		return notifications;
	}

	public void setNotifications(boolean notifications) {
		this.notifications = notifications;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeString(address);
		dest.writeString(places);
		dest.writeDouble(lat);
		dest.writeDouble(lng);
		dest.writeInt(notifications ? 1 : 0);
	}

	public static final Parcelable.Creator<Parking> CREATOR = new Parcelable.Creator<Parking>() {
		public Parking createFromParcel(Parcel in) {
			return new Parking(in);
		}

		public Parking[] newArray(int size) {
			return new Parking[size];
		}
	};

}
