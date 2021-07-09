package ch.bbcag.swift_travel.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import ch.bbcag.swift_travel.utils.Const;

@Entity(tableName = Const.LOCATIONS)
public class Location {
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = Const.ID)
	private long id;
	@ColumnInfo(name = Const.NAME)
	private String name;
	@ColumnInfo(name = Const.DESCRIPTION)
	private String description;
	@ColumnInfo(name = Const.IMAGE_URI)
	private String imageURI;
	@ColumnInfo(name = Const.IMAGE_CDL)
	private String imageCDL;
	@ColumnInfo(name = Const.START_TIME)
	private String startTime;
	@ColumnInfo(name = Const.END_TIME)
	private String endTime;
	@ColumnInfo(name = Const.TIME_DURATION)
	private String duration;
	@ColumnInfo(name = Const.TRANSPORT)
	private String transport;
	@ColumnInfo(name = "category")
	private int category;
	@ColumnInfo(name = Const.DAY_ID)
	private long dayId;

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

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
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

	public String getImageCDL() {
		return imageCDL;
	}

	public void setImageCDL(String imageCDL) {
		this.imageCDL = imageCDL;
	}

	public long getDayId() {
		return dayId;
	}

	public void setDayId(long dayId) {
		this.dayId = dayId;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}
}
