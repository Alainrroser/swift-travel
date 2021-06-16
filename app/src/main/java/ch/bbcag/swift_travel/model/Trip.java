package ch.bbcag.swift_travel.model;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class Trip {

	private int id;
	private String name;
	private String origin;
	private String destination;
	private String duration;
	private String description;
	private Uri imageURI;
	private List<Country> countries = new ArrayList<>();

	public Trip() {

	}

	public Trip(int id, String name, String destination, String duration, List<Country> countries) {
		this.id = id;
		this.name = name;
		this.destination = destination;
		this.duration = duration;
		this.countries = countries;
	}

	public Trip(String name, String description, Uri imageURI) {
		this.name = name;
		this.description = description;
		this.imageURI = imageURI;
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

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public List<Country> getCountries() {
		return countries;
	}

	public void setCountries(List<Country> countries) {
		this.countries = countries;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Uri getImageURI() {
		return imageURI;
	}

	public void setImageURI(Uri imageURI) {
		this.imageURI = imageURI;
	}
}