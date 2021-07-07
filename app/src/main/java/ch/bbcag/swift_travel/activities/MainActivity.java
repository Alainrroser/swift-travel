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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

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

		tripList = new ArrayList<>();
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			OnlineDatabaseUtils.getAllTripsFromUser(Const.TRIPS, Const.USER_ID, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), task -> addToList(task, tripList, Trip.class));
		} else {
			tripList = tripDao.getAll();
			getProgressBar().setVisibility(View.GONE);
		}
		adapter = new TripAdapter(this, tripList);

		if (FirebaseAuth.getInstance().getCurrentUser() != null && Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhotoUrl() != null) {
			LayoutUtils.setOnlineImageURIOnImageView(getApplicationContext(), loginButton, FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl(), true);
		}

		createTripFromIntent();
		addTripsToClickableList();

		onFloatingActionButtonClick();
		onLoginButtonClick();
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

	private void createTripFromIntent() {
		Intent intent = getIntent();
		Trip trip = new Trip();
		if (intent.getStringExtra(Const.TRIP_NAME) != null && intent.getBooleanExtra(Const.ADD_TRIP, false)) {
			intent.removeExtra(Const.ADD_TRIP);

			trip.setName(intent.getStringExtra(Const.TRIP_NAME));
			trip.setDescription(intent.getStringExtra(Const.TRIP_DESCRIPTION));
			trip.setImageURI(intent.getStringExtra(Const.IMAGE_URI));
			if (FirebaseAuth.getInstance().getCurrentUser() != null) {
				trip.setUserId(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
			}
			long id = tripDao.insert(trip);
			trip.setId(id);

			OnlineDatabaseUtils.add(Const.TRIPS, trip.getId(), trip);

			adapter.add(trip);
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

	private void updateDestinationAndOrigin(Trip trip, LocalDate localDate, boolean updateOrigin) {
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			List<Country> countryList = new ArrayList<>();
			OnlineDatabaseUtils.getAllFromParentId(Const.COUNTRIES, Const.TRIP_ID, trip.getId(), task -> addToList(task, countryList, Country.class));
			for (Country country : countryList) {
				localDate = loopThroughCities(trip, country, localDate, updateOrigin);
			}
		} else {
			for (Country country : countryDao.getAllFromTrip(trip.getId())) {
				localDate = loopThroughCities(trip, country, localDate, updateOrigin);
			}
		}
	}

	private LocalDate loopThroughCities(Trip trip, Country country, LocalDate localDate, boolean updateOrigin) {
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			List<City> cityList = new ArrayList<>();
			OnlineDatabaseUtils.getAllFromParentId(Const.CITIES, Const.COUNTRY_ID, country.getId(), task -> addToList(task, cityList, City.class));
			for (City city : cityList) {
				localDate = updateOriginOrDestination(trip, city, localDate, updateOrigin);
			}
		} else {
			for (City city : cityDao.getAllFromCountry(country.getId())) {
				localDate = updateOriginOrDestination(trip, city, localDate, updateOrigin);
			}
		}
		return localDate;
	}

	private LocalDate updateOriginOrDestination(Trip trip, City city, LocalDate localDate, boolean updateOrigin) {
		if (updateOrigin) {
			localDate = updateOrigin(trip, city, localDate);
		} else {
			localDate = updateDestination(trip, city, localDate);
		}
		return localDate;
	}

	private LocalDate updateOrigin(Trip trip, City city, LocalDate localDate) {
		if (localDate.compareTo(LocalDate.parse(city.getStartDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy"))) > 0) {
			localDate = LocalDate.parse(city.getStartDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
			trip.setOrigin(city.getName());
			tripDao.update(trip);
			OnlineDatabaseUtils.add(Const.TRIPS, trip.getId(), trip);
		}
		return localDate;
	}

	private LocalDate updateDestination(Trip trip, City city, LocalDate localDate) {
		if (localDate.compareTo(LocalDate.parse(city.getStartDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy"))) < 0) {
			localDate = LocalDate.parse(city.getStartDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
			trip.setDestination(city.getName());
			tripDao.update(trip);
			OnlineDatabaseUtils.add(Const.TRIPS, trip.getId(), trip);
		}
		return localDate;
	}

	private void addTripsToClickableList() {
		trips.setAdapter(adapter);
		adapter.sort(this::compareTripStartDates);
		for (Trip trip : tripList) {
			updateDestinationAndOrigin(trip, LocalDate.parse("01.01.2200", DateTimeFormatter.ofPattern("dd.MM.yyyy")), true);
			updateDestinationAndOrigin(trip, LocalDate.parse("01.01.1800", DateTimeFormatter.ofPattern("dd.MM.yyyy")), false);
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