package com.sobattani;

import com.google.android.gms.maps.model.LatLng;

public class EventInfo {
	private LatLng latLong;
	private String name;
	private String type;
	
	public EventInfo(LatLng latLong, String name, String type) {
		super();
		this.latLong = latLong;
		this.name    = name;
		this.type    = type;
	}
	
	public LatLng getLatLong() {
		return latLong;
	}
	public void setLatLong(LatLng latLong) {
		this.latLong = latLong;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
