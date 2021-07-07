package ch.bbcag.swift_travel.dal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ch.bbcag.swift_travel.entities.Trip;

@Dao
public interface TripDao {
	@Query("SELECT * FROM trips")
	List<Trip> getAll();

	@Query("SELECT * FROM trips WHERE id = :id")
	Trip getById(long id);

	@Insert
	long insert(Trip trip);

	@Update
	void update(Trip trip);

	@Query("DELETE FROM trips WHERE id = :id")
	void deleteById(long id);
}