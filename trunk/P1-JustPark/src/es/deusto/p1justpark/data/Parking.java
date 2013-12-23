package es.deusto.p1justpark.data;

import java.io.Serializable;

public class Parking implements Serializable{

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
	
	
}
