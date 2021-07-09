package ch.bbcag.swift_travel.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import ch.bbcag.swift_travel.utils.Const;

@Entity(tableName = Const.IMAGES)
public class Image {
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = Const.ID)
	private long id;
	@ColumnInfo(name = Const.IMAGE_URI)
	private String imageURI;
	@ColumnInfo(name = Const.IMAGE_CDL)
	private String imageCDL;
	@ColumnInfo(name = Const.LOCATION_ID)
	private long locationId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public long getLocationId() {
		return locationId;
	}

	public void setLocationId(long locationId) {
		this.locationId = locationId;
	}
}
