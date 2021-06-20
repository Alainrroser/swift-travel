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

	@Query("UPDATE cities SET name = :name")
	void setName(String name);

	@Query("UPDATE cities SET imageURI = :imageURI")
	void setImageURI(String imageURI);

	@Query("UPDATE cities SET description = :description")
	void setDescription(String description);

	@Query("UPDATE cities SET startDate = :startDate")
	void setStartDate(String startDate);

	@Query("UPDATE cities SET startDate = :endDate")
	void setEndDate(String endDate);

	@Insert
	long insert(City city);

	@Update
	void update(City city);

	@Query("DELETE FROM cities WHERE id = :id")
	void delete(long id);
}