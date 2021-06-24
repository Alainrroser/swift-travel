package ch.bbcag.swift_travel.dal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ch.bbcag.swift_travel.entities.City;

@Dao
public interface CityDao {
	@Query("SELECT * FROM cities WHERE countryId = :countryId")
	List<City> getAllFromCountry(long countryId);

	@Query("SELECT * FROM cities WHERE id = :id")
	City getById(long id);

	@Insert
	long insert(City city);

	@Update
	void update(City city);

	@Query("DELETE FROM cities WHERE id = :id")
	void deleteById(long id);
}