package ch.bbcag.swift_travel.model;

import android.net.Uri;

import java.util.List;

public class Country {
	private String name;
	private String description;
	private String duration;
	private Uri imageURI;
	private List<City> cityList;

	public Country() {

	}

	public Country(String name, String duration) {
		this.name = name;
		this.duration = duration;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public Uri getImageURI() {
		return imageURI;
	}

	public void setImageURI(Uri imageURI) {
		this.imageURI = imageURI;
	}

	public List<City> getCityList() {
		return cityList;
	}

	public void setCityList(List<City> cityList) {
		this.cityList = cityList;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
