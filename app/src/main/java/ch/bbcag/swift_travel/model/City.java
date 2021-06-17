package ch.bbcag.swift_travel.model;

import androidx.annotation.NonNull;

public class City {
	private String name;

	public City() {
	}

	@NonNull
	@Override
	public String toString() {
		return name;
	}
}
