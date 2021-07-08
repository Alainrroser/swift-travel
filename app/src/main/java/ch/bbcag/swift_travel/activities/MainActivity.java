package ch.bbcag.swift_travel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.adapter.TripAdapter;
import ch.bbcag.swift_travel.dal.CityDao;
import ch.bbcag.swift_travel.dal.CountryDao;
import ch.bbcag.swift_travel.dal.SwiftTravelDatabase;
import ch.bbcag.swift_travel.dal.TripDao;
import ch.bbcag.swift_travel.entities.City;
import ch.bbcag.swift_travel.entities.Country;
import ch.bbcag.swift_travel.entities.Trip;
import ch.bbcag.swift_travel.utils.Const;
import ch.bbcag.swift_travel.utils.DateTimeUtils;
import ch.bbcag.swift_travel.utils.LayoutUtils;
import ch.bbcag.swift_travel.utils.OnlineDatabaseUtils;

public class MainActivity extends BaseActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {
	private SearchView searchView;
	private MenuItem searchItem;

	private FloatingActionButton floatingActionButton;
	private ImageButton loginButton;
	private TripAdapter adapter;
	private ListView trips;

	private TripDao tripDao;
	private CountryDao countryDao;
	private CityDao cityDao;

	private List<Trip> tripList;

	private LocalDate localDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTitle(getString(R.string.app_name));

		tripDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getTripDao();
		countryDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getCountryDao();
		cityDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getCityDao();

		floatingActionButton = findViewById(R.id.floating_action_button_main);
		loginButton = findViewById(R.id.login_button);
		trips = findViewById(R.id.trips);
	}

	@Override
	protected void onStart() {
		super.onStart();

		tripList = tripDao.getAll();
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			OnlineDatabaseUtils.getAllTripsFromUser(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), task -> addToList(task, () -> synchronizeTrips(task)));
		} else {
			getProgressBar().setVisibility(View.GONE);
			onStartAfterListInitialized();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.search_menu, menu);

		searchItem = menu.findItem(R.id.search);

		searchView = (SearchView) searchItem.getActionView();
		searchView.setQueryHint(getString(R.string.search));
		searchView.setIconified(false);
		searchView.setOnQueryTextListener(this);
		searchView.setOnCloseListener(this);
		setOnActionExpandListener();

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.search) {
			searchView.setIconified(false);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onQueryTextSubmit(String searchText) {
		filterAdapter(searchText);
		return true;
	}

	@Override
	public boolean onQueryTextChange(String searchText) {
		filterAdapter(searchText);
		return false;
	}

	@Override
	public boolean onClose() {
		if (!searchView.isIconified()) {
			searchItem.collapseActionView();
		} else {
			searchView.setIconified(false);
		}

		return false;
	}

	private void setOnActionExpandListener() {
		searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				return true;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				searchView.setQuery("", false);
				filterAdapter("");
				return true;
			}
		});
	}

	private void filterAdapter(String searchText) {
		adapter.getFilter().filter(searchText);
	}

	private void synchronizeTrips(Task<QuerySnapshot> task) {
		List<Trip> localNonExistingTrips = new ArrayList<>();
		for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
			Trip onlineTrip = document.toObject(Trip.class);
			addToList(onlineTrip);
			checkIfSavedLocal(localNonExistingTrips, onlineTrip);
		}
		addLocal(localNonExistingTrips);
		runOnUiThread(this::onStartAfterListInitialized);
	}

	private void addToList(Trip onlineTrip) {
		if (tripList.size() > 0) {
			addIfNotExists(onlineTrip);
		} else {
			tripList.add(onlineTrip);
		}
	}

	private void addIfNotExists(Trip onlineTrip) {
		if (!checkIfExistsInList(onlineTrip)) {
			tripList.add(onlineTrip);
		}
	}

	private boolean checkIfExistsInList(Trip onlineTrip) {
		boolean existsInList = false;
		for (Trip listTrip : tripList) {
			if (listTrip.getId() == onlineTrip.getId()) {
				existsInList = true;
				break;
			}
		}
		return existsInList;
	}

	private void ifExistsLocal(List<Trip> localNonExistingTrips, Trip onlineTrip) {
		if (checkIfExistsLocal(onlineTrip)) {
			localNonExistingTrips.add(onlineTrip);
		}
	}

	private boolean checkIfExistsLocal(Trip onlineTrip) {
		boolean existsInLocalDatabase = false;
		for (Trip localTrip : tripDao.getAll()) {
			if(localTrip.getStartDate() != null && onlineTrip.getStartDate() != null) {
				existsInLocalDatabase = !localTrip.getStartDate().equals(onlineTrip.getStartDate());
			} else {
				existsInLocalDatabase = !localTrip.getName().equals(onlineTrip.getName());
			}
		}
		return existsInLocalDatabase;
	}

	private void checkIfSavedLocal(List<Trip> localNonExistingTrips, Trip onlineTrip) {
		if (tripDao.getAll().size() > 0) {
			ifExistsLocal(localNonExistingTrips, onlineTrip);
		} else {
			localNonExistingTrips.add(onlineTrip);
		}
	}

	private void addLocal(List<Trip> localNonExistingTrips) {
		for (Trip localNonExistingTrip : localNonExistingTrips) {
			long oldId = localNonExistingTrip.getId();
			OnlineDatabaseUtils.delete(Const.TRIPS, localNonExistingTrip.getId());
			localNonExistingTrip.setId(0);
			long newId = tripDao.insert(localNonExistingTrip);
			localNonExistingTrip.setId(newId);
			localNonExistingTrip.setUserId(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

			OnlineDatabaseUtils.add(Const.TRIPS, newId, tripDao.getById(newId));
			updateCountries(oldId, newId);
		}
	}

	private void updateCountries(long oldId, long newId) {
		List<Country> countryList = new ArrayList<>();
		OnlineDatabaseUtils.getAllFromParentId(Const.COUNTRIES, Const.TRIP_ID, oldId, task -> updateCountryTripIds(task, countryList, newId));
	}

	private void updateCountryTripIds(Task<QuerySnapshot> task, List<Country> countryList, long newId) {
		for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
			countryList.add(document.toObject(Country.class));
		}
		for (Country country : countryList) {
			country.setTripId(newId);
			OnlineDatabaseUtils.add(Const.COUNTRIES, country.getId(), country);
		}
	}

	private void onStartAfterListInitialized() {
		adapter = new TripAdapter(this, tripList);

		if (FirebaseAuth.getInstance().getCurrentUser() != null && Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhotoUrl() != null) {
			LayoutUtils.setOnlineImageURIOnImageView(getApplicationContext(), loginButton, FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl(), true);
		}

		createTripFromIntent();
		addTripsToClickableList();

		onFloatingActionButtonClick();
		onLoginButtonClick();
	}

	private void createTripFromIntent() {
		Intent intent = getIntent();
		Trip trip = new Trip();
		if (intent.getStringExtra(Const.TRIP_NAME) != null && intent.getBooleanExtra(Const.ADD_TRIP, false)) {
			intent.removeExtra(Const.ADD_TRIP);

			trip.setName(intent.getStringExtra(Const.TRIP_NAME));
			trip.setDescription(intent.getStringExtra(Const.TRIP_DESCRIPTION));
			trip.setImageURI(intent.getStringExtra(Const.IMAGE_URI));
			setUserId(trip);
			long id = tripDao.insert(trip);
			trip.setId(id);

			OnlineDatabaseUtils.add(Const.TRIPS, trip.getId(), trip);

			adapter.add(trip);
		}
	}

	private void setUserId(Trip trip) {
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			trip.setUserId(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
		}
	}

	private int compareTripStartDates(Trip tripOne, Trip tripTwo) {
		if (tripOne.getStartDate() != null && tripTwo.getStartDate() != null) {
			long tripOneStartDate = DateTimeUtils.parseDateToMilliseconds(tripOne.getStartDate());
			long tripTwoStartDate = DateTimeUtils.parseDateToMilliseconds(tripTwo.getStartDate());
			return Long.compare(tripOneStartDate, tripTwoStartDate);
		}
		return 0;
	}

	private void updateDestinationOrOrigin(Trip trip, boolean updateOrigin) {
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			List<Country> countryList = new ArrayList<>();
			OnlineDatabaseUtils.getAllFromParentId(Const.COUNTRIES, Const.TRIP_ID, trip.getId(), task -> addToList(task, () -> loopThroughCountriesIfLoggedIn(task, countryList, trip, updateOrigin)));
		} else {
			loopThroughCountries(trip, updateOrigin);
		}
	}

	private void initializeLocalDate(boolean updateOrigin) {
		if (updateOrigin) {
			localDate = LocalDate.parse("01.01.2200", DateTimeFormatter.ofPattern("dd.MM.yyyy"));
		} else {
			localDate = LocalDate.parse("01.01.1800", DateTimeFormatter.ofPattern("dd.MM.yyyy"));
		}
	}

	private void loopThroughCountries(Trip trip, boolean updateOrigin) {
		initializeLocalDate(updateOrigin);
		for (Country country : countryDao.getAllFromTrip(trip.getId())) {
			loopThroughAllCities(trip, country, updateOrigin);
		}
	}

	private void loopThroughCountriesIfLoggedIn(Task<QuerySnapshot> task, List<Country> countryList, Trip trip, boolean updateOrigin) {
		for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
			countryList.add(document.toObject(Country.class));
			loopThroughAllCities(trip, document.toObject(Country.class), updateOrigin);
		}
	}

	private void loopThroughAllCities(Trip trip, Country country, boolean updateOrigin) {
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			List<City> cityList = new ArrayList<>();
			OnlineDatabaseUtils.getAllFromParentId(Const.CITIES, Const.COUNTRY_ID, country.getId(), task -> addToList(task, () -> loopThroughCitiesIfLoggedIn(task, cityList, trip, updateOrigin)));
		} else {
			loopThroughCities(trip, country, updateOrigin);
		}
	}

	private void loopThroughCities(Trip trip, Country country, boolean updateOrigin) {
		for (City city : cityDao.getAllFromCountry(country.getId())) {
			updateOriginOrDestination(trip, city, updateOrigin);
		}
	}

	private void loopThroughCitiesIfLoggedIn(Task<QuerySnapshot> task, List<City> cityList, Trip trip, boolean updateOrigin) {
		initializeLocalDate(updateOrigin);
		for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
			cityList.add(document.toObject(City.class));
			updateOriginOrDestination(trip, document.toObject(City.class), updateOrigin);
		}
	}

	private void updateOriginOrDestination(Trip trip, City city, boolean updateOrigin) {
		if (updateOrigin) {
			updateOrigin(trip, city);
		} else {
			updateDestination(trip, city);
		}
	}

	private void updateOrigin(Trip trip, City city) {
		if (localDate.compareTo(LocalDate.parse(city.getStartDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy"))) > 0) {
			localDate = LocalDate.parse(city.getStartDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
			trip.setOrigin(city.getName());
			tripDao.update(trip);
			OnlineDatabaseUtils.add(Const.TRIPS, trip.getId(), trip);
		}
	}

	private void updateDestination(Trip trip, City city) {
		if (localDate.compareTo(LocalDate.parse(city.getStartDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy"))) < 0) {
			localDate = LocalDate.parse(city.getStartDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
			trip.setDestination(city.getName());
			tripDao.update(trip);
			OnlineDatabaseUtils.add(Const.TRIPS, trip.getId(), trip);
		}
	}

	private void addTripsToClickableList() {
		trips.setAdapter(adapter);
		adapter.sort(this::compareTripStartDates);
		for (Trip trip : tripList) {
			updateDestinationOrOrigin(trip, true);
			updateDestinationOrOrigin(trip, false);
		}

		AdapterView.OnItemClickListener onItemClickListener = (parent, v, position, id) -> {
			Intent intent = new Intent(getApplicationContext(), TripDetailsActivity.class);
			Trip selected = (Trip) parent.getItemAtPosition(position);
			intent.putExtra(Const.TRIP, selected.getId());
			intent.putExtra(Const.TRIP_NAME, selected.getName());
			startActivity(intent);
		};
		trips.setOnItemClickListener(onItemClickListener);
	}

	private void onFloatingActionButtonClick() {
		floatingActionButton.setOnClickListener(v -> {
			Intent intent = new Intent(getApplicationContext(), CreateActivity.class);
			intent.putExtra(Const.ADD_TRIP, true);
			intent.putExtra(Const.CREATE_TITLE, getString(R.string.create_trip_title));
			startActivity(intent);
		});
	}

	private void onLoginButtonClick() {
		loginButton.setOnClickListener(v -> {
			Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(intent);
		});
	}
}