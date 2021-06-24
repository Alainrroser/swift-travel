package ch.bbcag.swift_travel.dal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ch.bbcag.swift_travel.entities.Location;

@Dao
public interface LocationDao {
	@Query("SELECT * FROM locations WHERE dayId = :dayId")
	List<Location> getAllFromDay(long dayId);

	@Query("SELECT * FROM locations WHERE id = :id")
	Location getById(long id);

	@Insert
	long insert(Location location);

	@Update
	void update(Location location);

	@Query("DELETE FROM locations WHERE id = :id")
	void deleteById(long id);

	@Query("DELETE FROM locations WHERE dayId = :dayId")
	void deleteByDayId(long dayId);
}