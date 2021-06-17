package ch.bbcag.swift_travel.dal;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import ch.bbcag.swift_travel.entities.City;
import ch.bbcag.swift_travel.entities.Country;
import ch.bbcag.swift_travel.entities.Trip;

@androidx.room.Database(entities = {Trip.class, Country.class, City.class}, version = 1)
public abstract class SwiftTravelDatabase extends RoomDatabase {
    private static SwiftTravelDatabase INSTANCE;

    public static synchronized SwiftTravelDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    SwiftTravelDatabase.class, "SwiftTravelDatabase")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public abstract TripDao getTripDao();

    public abstract CountryDao getCountryDao();

    public abstract CityDao getCityDao();
}
