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

	@Insert
	long insert(Country country);

	@Update
	void update(Country country);

	@Query("DELETE FROM countries WHERE id = :id")
	void delete(long id);
}