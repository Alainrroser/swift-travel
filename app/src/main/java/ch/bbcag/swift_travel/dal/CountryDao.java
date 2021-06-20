package ch.bbcag.swift_travel.dal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ch.bbcag.swift_travel.entities.Country;

@Dao
public interface CountryDao {
	@Query("SELECT * FROM countries WHERE tripId = :tripId")
	List<Country> getAllFromTrip(long tripId);

	@Query("SELECT * FROM countries WHERE id = :id")
	Country getById(long id);

	@Query("SELECT code FROM countries WHERE id = :id")
	String getCodeById(long id);

	@Query("UPDATE countries SET name = :name")
	void setName(String name);

	@Query("UPDATE countries SET imageURI = :imageURI")
	void setImageURI(String imageURI);

	@Query("UPDATE countries SET description = :description")
	void setDescription(String description);

	@Insert
	long insert(Country country);

	@Update
	void update(Country country);

	@Query("DELETE FROM countries WHERE id = :id")
	void delete(long id);
}