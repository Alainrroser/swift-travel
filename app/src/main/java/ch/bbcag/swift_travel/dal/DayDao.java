package ch.bbcag.swift_travel.dal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ch.bbcag.swift_travel.entities.Day;

@Dao
public interface DayDao {
	@Query("SELECT * FROM days WHERE id = :cityId")
	List<Day> getAllFromCity(long cityId);

	@Insert
	long insert(Day day);

	@Update
	void update(Day day);

	@Query("DELETE FROM days WHERE id = :id")
	void delete(long id);
}