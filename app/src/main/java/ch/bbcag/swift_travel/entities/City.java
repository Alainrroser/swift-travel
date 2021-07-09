package ch.bbcag.swift_travel.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import ch.bbcag.swift_travel.utils.Const;

@Entity(tableName = Const.CITIES)
public class City {
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
	@ColumnInfo(name = Const.START_DATE)
	private String startDate;
	@ColumnInfo(name = Const.END_DATE)
	private String endDate;
	@ColumnInfo(name = Const.DATE_DURATION)
	private long duration;
	@ColumnInfo(name = Const.TRANSPORT)
	private String transport;
	@ColumnInfo(name = Const.COUNTRY_ID)
	private long countryId;

	public City() {
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

	public long getCountryId() {
		return countryId;
	}

	public void setCountryId(long countryId) {
		this.countryId = countryId;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}
}
