package ch.bbcag.swift_travel.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "locations")
public class Location {
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	private long id;
	@ColumnInfo(name = "name")
	private String name;
	@ColumnInfo(name = "description")
	private String description;
	@ColumnInfo(name = "imageURI")
	private String imageURI;
	@ColumnInfo(name = "dayId")
	private long dayId;
	@Ignore
	private String duration;

	public Location() {
	}

	@NonNull
	@Override
	public String toString() {
		return name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getImageURI() {
		return imageURI;
	}

	public void setImageURI(String imageURI) {
		this.imageURI = imageURI;
	}

	public long getDayId() {
		return dayId;
	}

	public void setDayId(long dayId) {
		this.dayId = dayId;
	}
}