package ch.bbcag.swift_travel.dal;

import java.util.ArrayList;
import java.util.List;

import ch.bbcag.swift_travel.model.Country;
import ch.bbcag.swift_travel.model.Trip;

public class TripDao {
    private static List<Country> fillCountries() {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("Netherlands", "1.2.2021 - 13.2.2021"));
        countries.add(new Country("Mozambique", "4.4.2021 - 5.5.2021"));
        countries.add(new Country("Ni-Vanuatu", "8.8.2021 - 1.9.2021"));
        return countries;
    }
    private static List<Country> fillCountries2() {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("TestLand", "1.2.2021 - 13.2.2021"));
        countries.add(new Country("FestLand", "4.4.2021 - 5.5.2021"));
        countries.add(new Country("RestLand", "8.8.2021 - 1.9.2021"));
        return countries;
    }

    public static List<Trip> getAll() {
        List<Trip> availableTrips = new ArrayList<>();
        Trip hollandTrip = new Trip(1, "Trip to Holland", "Holland", "1.2.2021 - 13.2.2021", fillCountries());
        hollandTrip.setDescription("Bacon ipsum dolor amet fatback shoulder chicken, buffalo chuck picanha drumstick pig sausage boudin tenderloin cupim kevin. Frankfurter pastrami cow turducken, prosciutto short loin t-bone ham hock. Fatback landjaeger tongue spare ribs brisket chicken ribeye.");
        availableTrips.add(hollandTrip);
        availableTrips.add(new Trip(2, "Trip to Bahamas", "Bahamas", "1.2.2021 - 13.2.2021", fillCountries2()));
        availableTrips.add(new Trip(3, "Trip to Hawaii", "Hawaii", "1.2.2021 - 13.2.2021", fillCountries()));
        availableTrips.add(new Trip(4, "Trip to Atlantis", "Atlantis", "1.2.2021 - 13.2.2021", fillCountries()));
        return availableTrips;
    }
}
