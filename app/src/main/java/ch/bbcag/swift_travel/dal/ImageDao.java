package ch.bbcag.swift_travel.dal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ch.bbcag.swift_travel.entities.Image;

@Dao
public interface ImageDao {
	@Query("SELECT * FROM images WHERE locationId = :locationId")
	List<Image> getAllFromLocation(long locationId);

	@Query("SELECT * FROM images WHERE id = :id")
	Image getById(long id);

	@Insert
	long insert(Image image);

	@Update
	void update(Image image);

	@Query("DELETE FROM images WHERE id = :id")
	void deleteById(long id);
}