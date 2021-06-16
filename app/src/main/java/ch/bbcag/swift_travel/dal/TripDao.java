package ch.bbcag.swift_travel.dal;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ch.bbcag.swift_travel.model.Country;
import ch.bbcag.swift_travel.model.Trip;

public class TripDao {
    static List<Country> countries = new ArrayList<>();

    private static void fillCountries() {
        countries.add(new Country("Netherlands", "1.2.2021 - 13.02.2021", Uri.parse("someUri")));
        countries.add(new Country("Netherlands", "1.2.2021 - 13.02.2021", Uri.parse("someUri")));
        countries.add(new Country("Netherlands", "1.2.2021 - 13.02.2021", Uri.parse("someUri")));
    }

    public static List<Trip> getAll() {
        fillCountries();

        List<Trip> availableTrips = new ArrayList<>();
        availableTrips.add(new Trip(1, "Trip to Holland", "Holland", "1.2.2021 - 13.2.2021", countries));
        availableTrips.add(new Trip(2, "Trip to Bahamas", "Bahamas", "1.2.2021 - 13.2.2021", countries));
        availableTrips.add(new Trip(3, "Trip to Hawaii", "Hawaii", "1.2.2021 - 13.2.2021", countries));
        availableTrips.add(new Trip(4, "Trip to Atlantis", "Atlantis", "1.2.2021 - 13.2.2021", countries));
        return availableTrips;
    }
}
