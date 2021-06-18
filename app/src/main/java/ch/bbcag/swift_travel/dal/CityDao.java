package ch.bbcag.swift_travel.dal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ch.bbcag.swift_travel.entities.City;
import ch.bbcag.swift_travel.entities.Country;

@Dao
public interface CityDao {
	@Query("SELECT * FROM cities WHERE countryId = :countryId")
	List<City> getAllFromCountry(int countryId);

	@Query("UPDATE cities SET name = :name")
	void setName(String name);

	@Query("UPDATE cities SET imageURI = :imageURI")
	void setImageURI(String imageURI);

	@Query("UPDATE cities SET description = :description")
	void setDescription(String description);

	@Query("UPDATE cities SET duration = :duration")
	void setDuration(String duration);

	@Insert
	void insert(City city);

	@Update
	void update(City city);

	@Query("DELETE FROM cities WHERE id = :id")
	void delete(int id);
}