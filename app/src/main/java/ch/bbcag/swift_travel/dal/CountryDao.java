package ch.bbcag.swift_travel.dal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ch.bbcag.swift_travel.entities.Country;

@Dao
public interface CountryDao {
    @Query("SELECT * FROM countries WHERE tripID = :tripID")
    List<Country> getAllFromTrip(int tripID);

    @Query("UPDATE countries SET name = :name")
    void setName(String name);

    @Query("UPDATE countries SET imageURI = :imageURI")
    void setImageURI(String imageURI);

    @Query("UPDATE countries SET description = :description")
    void setDescription(String description);

    @Insert
    void insert(Country country);

    @Update
    void update(Country country);

    @Query("DELETE FROM countries WHERE id = :id")
    void delete(int id);
}