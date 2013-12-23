package es.deusto.p1justpark.data;

import java.io.Serializable;

public class Parking implements Serializable{

	private static final long serialVersionUID = -7200823720806189295L;
	
	private int id;
	private String name;
	private String address;
	private String places;

	public Parking(int id, String name, String address, String places) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.places = places;
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
}
