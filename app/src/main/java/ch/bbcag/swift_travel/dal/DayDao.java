package ch.bbcag.swift_travel.dal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ch.bbcag.swift_travel.entities.Day;

@Dao
public interface DayDao {
    @Query("SELECT * FROM days")
    List<Day> getAll();

    @Insert
    void insert(Day day);

    @Update
    void update(Day day);
}