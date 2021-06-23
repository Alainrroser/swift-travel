package ch.bbcag.swift_travel.dal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ch.bbcag.swift_travel.entities.Trip;

@Dao
public interface TripDao {
	@Query("SELECT * FROM trips")
	List<Trip> getAll();

	@Query("SELECT * FROM trips WHERE id = :id")
	Trip getById(long id);

	@Query("UPDATE trips SET name = :name")
	void setName(String name);

	@Query("UPDATE trips SET imageURI = :imageURI")
	void setImageURI(String imageURI);

	@Query("UPDATE trips SET description = :description")
	void setDescription(String description);

	@Insert
	long insert(Trip trip);

	@Query("DELETE FROM trips WHERE id = :id")
	void delete(long id);
}