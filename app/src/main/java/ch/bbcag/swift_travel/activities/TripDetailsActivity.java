package ch.bbcag.swift_travel.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.adapter.CountryAdapter;
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

public class TripDetailsActivity extends UpButtonActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {
	private SearchView searchView;
	private MenuItem searchItem;

	private FloatingActionButton floatingActionButton;
	private CountryAdapter adapter;
	private ListView countries;

	private ImageButton editDescriptionButton;
	private Button submitButton;

	private TextView titleText;
	private TextView durationText;
	private TextView descriptionText;
	private EditText editTitle;
	private EditText editDescription;
	private ImageView tripImage;

	private Trip selected;

	private TripDao tripDao;
	private CountryDao countryDao;
	private CityDao cityDao;

	private List<Country> countryList;

	private boolean nameValidated = false;

	private LocalDate localDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_details);

		Intent intent = getIntent();
		String tripName = intent.getStringExtra(Const.TRIP_NAME);
		setTitle(tripName);

		tripDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getTripDao();
		countryDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getCountryDao();
		cityDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getCityDao();

		floatingActionButton = findViewById(R.id.floating_action_button_trip_details);
		countries = findViewById(R.id.countries);

		titleText = findViewById(R.id.trip_title);
		durationText = findViewById(R.id.trip_duration);
		descriptionText = findViewById(R.id.trip_description);
		editTitle = findViewById(R.id.trip_edit_title);
		editDescription = findViewById(R.id.trip_edit_description);
		tripImage = findViewById(R.id.trip_image);

		editDescriptionButton = findViewById(R.id.trip_edit_button);
		submitButton = findViewById(R.id.trip_submit_button);
	}

	@Override
	protected void onStart() {
		super.onStart();

		long id = getIntent().getLongExtra(Const.TRIP, -1);
		if (id != -1) {
			checkIfLoggedIn(id);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK && data != null) {
			Uri imageURI = data.getData();
			selected.setImageURI(imageURI.toString());
			tripDao.update(selected);
			OnlineDatabaseUtils.add(Const.TRIPS, selected.getId(), selected);
			tripImage.setImageURI(imageURI);
		}
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

	private void checkIfLoggedIn(long id) {
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			OnlineDatabaseUtils.getById(Const.TRIPS, id, task -> setObject(task, () -> initializeSelected(task, id)));
		} else {
			selected = tripDao.getById(id);
			onStartAfterSelectedInitialized();
		}
	}

	private void initializeSelected(Task<DocumentSnapshot> selectedTask, long id) {
		if (Objects.requireNonNull(selectedTask.getResult()).toObject(Trip.class) == null) {
			OnlineDatabaseUtils.add(Const.TRIPS, id, tripDao.getById(id));
			OnlineDatabaseUtils.getById(Const.TRIPS, id, task -> setObject(task, () -> setSelected(task)));
		} else {
			setSelected(selectedTask);
		}
	}

	private void setSelected(Task<DocumentSnapshot> selectedTask) {
		selected = Objects.requireNonNull(selectedTask.getResult()).toObject(Trip.class);
		onStartAfterSelectedInitialized();
	}

	private void onStartAfterSelectedInitialized() {
		countryList = countryDao.getAllFromTrip(selected.getId());
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			OnlineDatabaseUtils.getAllFromParentId(Const.COUNTRIES, Const.TRIP_ID, selected.getId(), task -> addToList(task, () -> synchronizeCountries(task)));
		} else {
			getProgressBar().setVisibility(View.GONE);
			onStartAfterListInitialized();
		}
	}

	private void synchronizeCountries(Task<QuerySnapshot> task) {
		List<Country> localNonExistingCountries = new ArrayList<>();
		for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
			Country onlineCountry = document.toObject(Country.class);
			addToList(onlineCountry);
			checkIfSavedLocal(localNonExistingCountries, onlineCountry);
		}
		addLocal(localNonExistingCountries);
		onStartAfterListInitialized();
	}

	private void addToList(Country onlineCountry) {
		if (countryList.size() > 0) {
			addIfNotExists(onlineCountry);
		} else {
			countryList.add(onlineCountry);
		}
	}

	private void addIfNotExists(Country onlineCountry) {
		if (!checkIfExistsInList(onlineCountry)) {
			countryList.add(onlineCountry);
		}
	}

	private boolean checkIfExistsInList(Country onlineCountry) {
		boolean existsInList = false;
		for (Country listCountry : countryList) {
			if (listCountry.getId() == onlineCountry.getId()) {
				existsInList = true;
				break;
			}
		}
		return existsInList;
	}

	private void ifExistsLocal(List<Country> localNonExistingCountries, Country onlineCountry) {
		if (checkIfExistsLocal(onlineCountry)) {
			localNonExistingCountries.add(onlineCountry);
		}
	}

	private boolean checkIfExistsLocal(Country onlineCountry) {
		boolean existsInLocalDatabase = false;
		for (Country localCountry : countryDao.getAllFromTrip(selected.getId())) {
			existsInLocalDatabase = localCountry.getId() != onlineCountry.getId();
		}
		return existsInLocalDatabase;
	}

	private void checkIfSavedLocal(List<Country> localNonExistingCountries, Country onlineCountry) {
		if (countryDao.getAllFromTrip(selected.getId()).size() > 0) {
			ifExistsLocal(localNonExistingCountries, onlineCountry);
		} else {
			localNonExistingCountries.add(onlineCountry);
		}
	}

	private void addLocal(List<Country> localNonExistingCountries) {
		for (Country localNonExistingCountry : localNonExistingCountries) {
			OnlineDatabaseUtils.delete(Const.COUNTRIES, localNonExistingCountry.getId());
			localNonExistingCountry.setId(0);
			long newId = countryDao.insert(localNonExistingCountry);
			localNonExistingCountry.setId(newId);

			OnlineDatabaseUtils.add(Const.COUNTRIES, newId, countryDao.getById(newId));
		}
	}

	private void onStartAfterListInitialized() {
		adapter = new CountryAdapter(this, countryList);

		if (adapter.getCount() > 0) {
			selected.setStartDate(adapter.getItem(0).getStartDate());
			selected.setEndDate(adapter.getItem(adapter.getCount() - 1).getEndDate());
			tripDao.update(selected);
			OnlineDatabaseUtils.add(Const.TRIPS, selected.getId(), selected);
		}

		createCountryFromIntent();
		addCountriesToClickableList();

		editTitle.setText(selected.getName());
		editDescription.setText(selected.getDescription());

		Group form = findViewById(R.id.trip_form);
		form.setVisibility(View.GONE);

		refreshContent();

		onFloatingActionButtonClick();
		editDescriptionButton.setOnClickListener(v -> toggleForm());
		tripImage.setOnClickListener(v -> ImagePicker.with(this).crop().start());
		onSubmitButtonClick();
	}

	private void createCountryFromIntent() {
		Intent intent = getIntent();
		Country country = new Country();
		if (intent.getStringExtra(Const.COUNTRY_NAME) != null && intent.getBooleanExtra(Const.ADD_COUNTRY, false)) {
			intent.removeExtra(Const.ADD_COUNTRY);

			country.setName(intent.getStringExtra(Const.COUNTRY_NAME));
			country.setCode(intent.getStringExtra(Const.COUNTRY_CODE));
			country.setImageURI(intent.getStringExtra(Const.FLAG_URI));
			country.setTripId(selected.getId());
			long id = countryDao.insert(country);
			country.setId(id);

			OnlineDatabaseUtils.add(Const.COUNTRIES, country.getId(), country);

			adapter.add(country);

			selected.setStartDate(adapter.getItem(0).getStartDate());
			selected.setEndDate(adapter.getItem(adapter.getCount() - 1).getEndDate());
			tripDao.update(selected);
			OnlineDatabaseUtils.add(Const.TRIPS, selected.getId(), selected);
		}
	}

	private int compareCountryStartDates(Country countryOne, Country countryTwo) {
		if (countryOne.getStartDate() != null && countryTwo.getStartDate() != null) {
			long countryOneStartDate = DateTimeUtils.parseDateToMilliseconds(countryOne.getStartDate());
			long countryTwoStartDate = DateTimeUtils.parseDateToMilliseconds(countryTwo.getStartDate());
			return Long.compare(countryOneStartDate, countryTwoStartDate);
		}
		return 0;
	}

	private void addCountriesToClickableList() {
		countries.setAdapter(adapter);
		adapter.sort(this::compareCountryStartDates);
		for (Country country : countryList) {
			updateDestinationOrOrigin(country, true);
			updateDestinationOrOrigin(country, false);
		}

		AdapterView.OnItemClickListener mListClickedHandler = (parent, v, position, id) -> {
			Intent intent = new Intent(getApplicationContext(), CountryDetailsActivity.class);
			Country selected = (Country) parent.getItemAtPosition(position);
			intent.putExtra(Const.COUNTRY_NAME, selected.getName());
			intent.putExtra(Const.COUNTRY, selected.getId());
			startActivity(intent);
		};
		countries.setOnItemClickListener(mListClickedHandler);
	}

	public void refreshContent() {
		LayoutUtils.setEditableTitleText(titleText, editTitle, selected.getName());
		LayoutUtils.setEditableText(descriptionText, editDescription, selected.getDescription(), getString(R.string.description_hint));
		String duration;
		if (getTripDuration() == 1) {
			duration = getTripDuration() + " " + getString(R.string.day);
		} else {
			duration = getTripDuration() + " " + getString(R.string.days);
		}
		LayoutUtils.setTextOnTextView(durationText, duration);
		if (selected.getImageURI() != null && !selected.getImageURI().isEmpty()) {
			LayoutUtils.setImageURIOnImageView(tripImage, selected.getImageURI());
		}

		setTitle(selected.getName());
	}

	private void toggleForm() {
		boolean notChanged = false;

		Group form = findViewById(R.id.trip_form);
		Group content = findViewById(R.id.trip_content);

		if (titleText.getText().equals(editTitle.getText().toString())) {
			notChanged = true;
		}

		if (form.getVisibility() == View.VISIBLE && (nameValidated || notChanged)) {
			form.setVisibility(View.GONE);
			content.setVisibility(View.VISIBLE);
		} else {
			editTitle.setText(selected.getName());
			editDescription.setText(selected.getDescription());
			form.setVisibility(View.VISIBLE);
			content.setVisibility(View.GONE);
		}
	}

	private void onFloatingActionButtonClick() {
		floatingActionButton.setOnClickListener(v -> {
			Intent intent = new Intent(getApplicationContext(), ChooseCountryActivity.class);
			intent.putExtra(Const.CHOOSE_TITLE, getString(R.string.add_country));
			intent.putExtra(Const.TRIP, selected.getId());
			startActivity(intent);
		});
	}

	private void onSubmitButtonClick() {
		submitButton.setOnClickListener(v -> {
			editName();
			editDescription();
			tripDao.update(selected);
			OnlineDatabaseUtils.add(Const.TRIPS, selected.getId(), selected);
			refreshContent();
			toggleForm();
		});
	}

	private void editName() {
		TextInputLayout editTitleLayout = findViewById(R.id.trip_edit_title_layout);
		if (Objects.requireNonNull(editTitle.getText()).toString().length() > 0 && Objects.requireNonNull(editTitle.getText()).toString().length() <= Const.TITLE_LENGTH) {
			nameValidated = true;
			selected.setName(editTitle.getText().toString());
		} else {
			nameValidated = false;
			editTitleLayout.setError(getString(R.string.length_error));
		}
	}

	private void editDescription() {
		if (!editDescription.getText().toString().isEmpty()) {
			selected.setDescription(editDescription.getText().toString());
		}
	}

	private long getTripDuration() {
		long duration = 0;
		for (Country country : countryList) {
			duration += country.getDuration();
		}
		selected.setDuration(duration);
		tripDao.update(selected);
		OnlineDatabaseUtils.add(Const.TRIPS, selected.getId(), selected);
		return duration;
	}

	private void updateDestinationOrOrigin(Country country, boolean updateOrigin) {
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			List<City> cityList = new ArrayList<>();
			OnlineDatabaseUtils.getAllFromParentId(Const.CITIES, Const.COUNTRY_ID, country.getId(), task -> addToList(task, () -> addCitiesToList(task, cityList, country, updateOrigin)));
		} else {
			loopThroughCities(country, updateOrigin);
		}
	}

	private void initializeLocalDate(boolean updateOrigin) {
		if (updateOrigin) {
			localDate = LocalDate.parse("01.01.2200", DateTimeFormatter.ofPattern("dd.MM.yyyy"));
		} else {
			localDate = LocalDate.parse("01.01.1800", DateTimeFormatter.ofPattern("dd.MM.yyyy"));
		}
	}

	private void loopThroughCities(Country country, boolean updateOrigin) {
		initializeLocalDate(updateOrigin);
		for (City city : cityDao.getAllFromCountry(country.getId())) {
			updateOriginOrDestination(country, city, updateOrigin);
		}
	}

	private void addCitiesToList(Task<QuerySnapshot> task, List<City> cityList, Country country, boolean updateOrigin) {
		initializeLocalDate(updateOrigin);
		for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
			cityList.add(document.toObject(City.class));
		}
		updateDestinationAndOriginIfLoggedIn(cityList, country, updateOrigin);
	}

	private void updateDestinationAndOriginIfLoggedIn(List<City> cityList, Country country, boolean updateOrigin) {
		for (City city : cityList) {
			updateOriginOrDestination(country, city, updateOrigin);
		}
	}

	private void updateOriginOrDestination(Country country, City city, boolean updateOrigin) {
		if (updateOrigin) {
			updateOrigin(country, city);
		} else {
			updateDestination(country, city);
		}
	}

	private void updateOrigin(Country country, City city) {
		if (localDate.compareTo(LocalDate.parse(city.getStartDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy"))) > 0) {
			localDate = LocalDate.parse(city.getStartDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
			country.setOrigin(city.getName());
			countryDao.update(country);
			OnlineDatabaseUtils.add(Const.COUNTRIES, country.getId(), country);
		}
	}

	private void updateDestination(Country country, City city) {
		if (localDate.compareTo(LocalDate.parse(city.getStartDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy"))) < 0) {
			localDate = LocalDate.parse(city.getStartDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
			country.setDestination(city.getName());
			countryDao.update(country);
			OnlineDatabaseUtils.add(Const.COUNTRIES, country.getId(), country);
		}
	}
}