package ch.bbcag.swift_travel.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

import ch.bbcag.swift_travel.utils.Const;

@Entity(tableName = Const.CITIES)
public class City {
	@Ignore
	List<Day> days = new ArrayList<>();
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = Const.ID)
	private long id;
	@ColumnInfo(name = Const.NAME)
	private String name;
	@ColumnInfo(name = Const.DESCRIPTION)
	private String description;
	@ColumnInfo(name = Const.IMAGE_URI)
	private String imageURI;
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

	public List<Day> getDays() {
		return days;
	}

	public void setDays(List<Day> days) {
		this.days = days;
	}

	public void addDay(Day day) {
		this.days.add(day);
	}
}
