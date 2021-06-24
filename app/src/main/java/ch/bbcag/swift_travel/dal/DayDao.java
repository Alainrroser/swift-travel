package ch.bbcag.swift_travel.dal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ch.bbcag.swift_travel.entities.Day;

@Dao
public interface DayDao {
	@Query("SELECT * FROM days WHERE cityId = :cityId")
	List<Day> getAllFromCity(long cityId);

	@Query("SELECT * FROM days WHERE id = :id")
	Day getById(long id);

	@Insert
	long insert(Day day);

	@Update
	void update(Day day);

	@Query("DELETE FROM days WHERE id = :id")
	void deleteById(long id);
}