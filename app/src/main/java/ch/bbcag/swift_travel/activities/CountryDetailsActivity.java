package ch.bbcag.swift_travel.activities;

import android.content.Intent;
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
import androidx.constraintlayout.widget.Group;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.adapter.CityAdapter;
import ch.bbcag.swift_travel.dal.CityDao;
import ch.bbcag.swift_travel.dal.CountryDao;
import ch.bbcag.swift_travel.dal.DayDao;
import ch.bbcag.swift_travel.dal.SwiftTravelDatabase;
import ch.bbcag.swift_travel.entities.City;
import ch.bbcag.swift_travel.entities.Country;
import ch.bbcag.swift_travel.entities.Day;
import ch.bbcag.swift_travel.utils.Const;
import ch.bbcag.swift_travel.utils.DateTimeUtils;
import ch.bbcag.swift_travel.utils.LayoutUtils;
import ch.bbcag.swift_travel.utils.OnlineDatabaseUtils;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class CountryDetailsActivity extends UpButtonActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {
	private SearchView searchView;
	private MenuItem searchItem;

	private FloatingActionButton floatingActionButton;
	private ImageButton editDescriptionButton;
	private Button submitButton;

	private TextView titleText;
	private TextView durationText;
	private TextView descriptionText;
	private EditText editDescription;
	private ImageView countryImage;

	private CityAdapter adapter;

	private Country selected;

	private CountryDao countryDao;
	private CityDao cityDao;
	private DayDao dayDao;

	private boolean durationOverlaps = false;

	private List<City> cityList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_country_details);

		Intent intent = getIntent();
		String name = intent.getStringExtra(Const.COUNTRY_NAME);
		setTitle(name);

		countryDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getCountryDao();
		cityDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getCityDao();
		dayDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getDayDao();

		titleText = findViewById(R.id.country_title);
		durationText = findViewById(R.id.country_duration);
		descriptionText = findViewById(R.id.country_description);
		editDescription = findViewById(R.id.country_edit_description);
		countryImage = findViewById(R.id.country_image);

		submitButton = findViewById(R.id.country_submit_button);
		editDescriptionButton = findViewById(R.id.country_edit_button);
		floatingActionButton = findViewById(R.id.floating_action_button_country_details);
	}

	@Override
	protected void onStart() {
		super.onStart();

		Group form = findViewById(R.id.country_form);
		form.setVisibility(View.GONE);

		long id = getIntent().getLongExtra(Const.COUNTRY, -1);
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
		if (adapter != null) {
			adapter.getFilter().filter(searchText);
		}
	}

	private void checkIfLoggedIn(long id) {
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			OnlineDatabaseUtils.getById(Const.COUNTRIES, id, task -> setObject(task, () -> initializeSelected(task, id)));
		} else {
			selected = countryDao.getById(id);
			onStartAfterSelectedInitialized();
		}
	}

	private void initializeSelected(Task<DocumentSnapshot> selectedTask, long id) {
		if (Objects.requireNonNull(selectedTask.getResult()).toObject(Country.class) == null) {
			OnlineDatabaseUtils.add(Const.COUNTRIES, id, countryDao.getById(id));
			OnlineDatabaseUtils.getById(Const.COUNTRIES, id, task -> setObject(task, () -> setSelected(task)));
		} else {
			setSelected(selectedTask);
		}
	}

	private void setSelected(Task<DocumentSnapshot> selectedTask) {
		selected = Objects.requireNonNull(selectedTask.getResult()).toObject(Country.class);
		onStartAfterSelectedInitialized();
	}

	private void onStartAfterSelectedInitialized() {
		cityList = cityDao.getAllFromCountry(selected.getId());
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			OnlineDatabaseUtils.getAllFromParentId(Const.CITIES, Const.COUNTRY_ID, selected.getId(), task -> addToList(task, () -> synchronizeCities(task)));
		} else {
			getProgressBar().setVisibility(View.GONE);
			onStartAfterListInitialized();
		}
	}

	private void synchronizeCities(Task<QuerySnapshot> task) {
		for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
			City onlineCity = document.toObject(City.class);
			addToList(onlineCity);
		}
		runOnUiThread(this::onStartAfterListInitialized);
	}

	private void addToList(City onlineCity) {
		if (cityList.size() > 0) {
			addIfNotExists(onlineCity);
		} else {
			cityList.add(onlineCity);
		}
	}

	private void addIfNotExists(City onlineCity) {
		if (!checkIfExistsInList(onlineCity)) {
			cityList.add(onlineCity);
		}
	}

	private boolean checkIfExistsInList(City onlineCity) {
		boolean existsInList = false;
		for (City listCity : cityList) {
			if (listCity.getId() == onlineCity.getId()) {
				existsInList = true;
				break;
			}
		}
		return existsInList;
	}

	private void onStartAfterListInitialized() {
		adapter = new CityAdapter(this, cityList);

		createCityFromIntent();
		addCitiesToClickableList();

		editDescription.setText(selected.getDescription());

		refreshContent();

		onFloatingActionButtonClick();
		editDescriptionButton.setOnClickListener(v -> toggleForm());
		onSubmitButtonClick();
	}

	public void addCitiesToClickableList() {
		ListView cities = findViewById(R.id.cities);
		cities.setAdapter(adapter);
		adapter.sort(this::compareCityStartDates);

		AdapterView.OnItemClickListener mListClickedHandler = (parent, v, position, id) -> {
			Intent intent = new Intent(getApplicationContext(), CityDetailsActivity.class);
			City selected = (City) parent.getItemAtPosition(position);
			intent.putExtra(Const.CITY_NAME, selected.getName());
			intent.putExtra(Const.CITY, selected.getId());
			startActivity(intent);
		};

		cities.setOnItemClickListener(mListClickedHandler);
	}

	private void createCityFromIntent() {
		Intent intent = getIntent();
		City city = new City();
		if (intent.getStringExtra(Const.CITY_NAME) != null && intent.getBooleanExtra(Const.ADD_CITY, false)) {
			intent.removeExtra(Const.ADD_CITY);

			checkIfDurationOverlaps(intent);
			addCityIfNotOverlaps(intent, city);
		}
	}

	private void checkIfDurationOverlaps(Intent intent) {
		for (City existingCity : cityList) {
			long startDateExisting = DateTimeUtils.parseDateToMilliseconds(existingCity.getStartDate());
			long startDateNew = DateTimeUtils.parseDateToMilliseconds(intent.getStringExtra(Const.START_DATE));
			long endDateExisting = DateTimeUtils.parseDateToMilliseconds(existingCity.getEndDate());
			long endDateNew = DateTimeUtils.parseDateToMilliseconds(intent.getStringExtra(Const.END_DATE));
			if (max(startDateNew, startDateExisting) < min(endDateNew, endDateExisting)) {
				durationOverlaps = true;
				break;
			}
		}
	}

	private void addCityIfNotOverlaps(Intent intent, City city) {
		if (!durationOverlaps) {
			city.setName(intent.getStringExtra(Const.CITY_NAME));
			city.setDescription(intent.getStringExtra(Const.CITY_DESCRIPTION));
			city.setTransport(intent.getStringExtra(Const.TRANSPORT));
			city.setImageURI(intent.getStringExtra(Const.IMAGE_URI));
			city.setImageCDL(intent.getStringExtra(Const.IMAGE_CDL));
			city.setStartDate(intent.getStringExtra(Const.START_DATE));
			city.setEndDate(intent.getStringExtra(Const.END_DATE));

			long startDate = DateTimeUtils.parseDateToMilliseconds(city.getStartDate());
			long endDate = DateTimeUtils.parseDateToMilliseconds(city.getEndDate());
			city.setDuration(DateTimeUtils.getDaysCountFromTimeSpan(startDate, endDate));

			city.setCountryId(selected.getId());
			long id = cityDao.insert(city);
			city.setId(id);
			addDaysToCity(city);

			OnlineDatabaseUtils.add(Const.CITIES, city.getId(), city);

			adapter.add(city);
			adapter.sort(this::compareCityStartDates);

			selected.setStartDate(adapter.getItem(0).getStartDate());
			selected.setEndDate(adapter.getItem(adapter.getCount() - 1).getEndDate());
			countryDao.update(selected);
			OnlineDatabaseUtils.add(Const.COUNTRIES, selected.getId(), selected);
		} else {
			generateMessageDialog(getString(R.string.duration_overlap_error_title), getString(R.string.duration_overlap_error_text));
		}
	}

	private void addDaysToCity(City city) {
		for (int dayCount = 1; dayCount <= city.getDuration(); dayCount++) {
			Day day = new Day();
			day.setName(getString(R.string.day) + " " + dayCount);

			LocalDate dayDate = LocalDate.parse(city.getStartDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy")).plusDays(dayCount - 1);
			day.setDate(dayDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
			day.setCityId(city.getId());

			long dayID = dayDao.insert(day);
			day.setId(dayID);

			OnlineDatabaseUtils.add(Const.DAYS, day.getId(), day);
		}
	}

	private int compareCityStartDates(City cityOne, City cityTwo) {
		long cityOneStartDate = DateTimeUtils.parseDateToMilliseconds(cityOne.getStartDate());
		long cityTwoStartDate = DateTimeUtils.parseDateToMilliseconds(cityTwo.getStartDate());
		return Long.compare(cityOneStartDate, cityTwoStartDate);
	}

	private void onFloatingActionButtonClick() {
		floatingActionButton.setOnClickListener(v -> {
			Intent intent = new Intent(getApplicationContext(), CreateActivity.class);
			intent.putExtra(Const.ADD_CITY, true);
			intent.putExtra(Const.CREATE_TITLE, getString(R.string.create_city_title));
			intent.putExtra(Const.COUNTRY, selected.getId());
			startActivity(intent);
		});
	}

	private void onSubmitButtonClick() {
		submitButton.setOnClickListener(v -> {
			editDescription();
			countryDao.update(selected);
			OnlineDatabaseUtils.add(Const.COUNTRIES, selected.getId(), selected);
			refreshContent();
			toggleForm();
		});
	}

	private void toggleForm() {
		Group form = findViewById(R.id.country_form);
		Group content = findViewById(R.id.country_content);

		if (form.getVisibility() == View.VISIBLE) {
			form.setVisibility(View.GONE);
			content.setVisibility(View.VISIBLE);
		} else {
			editDescription.setText(selected.getDescription());
			form.setVisibility(View.VISIBLE);
			content.setVisibility(View.GONE);
		}
	}

	private void editDescription() {
		if (editDescription.getText() != null && !editDescription.getText().toString().isEmpty()) {
			selected.setDescription(editDescription.getText().toString());
		}
	}

	public void refreshContent() {
		LayoutUtils.setTitleText(titleText, selected.getName());
		LayoutUtils.setEditableText(descriptionText, editDescription, selected.getDescription(), getString(R.string.description_hint));
		String duration;
		if (getCountryDuration() == 1) {
			duration = getCountryDuration() + " " + getString(R.string.day);
		} else {
			duration = getCountryDuration() + " " + getString(R.string.days);
		}
		LayoutUtils.setTextOnTextView(durationText, duration);
		if (selected.getImageURI() != null && !selected.getImageURI().isEmpty()) {
			LayoutUtils.setFlagImageURIOnImageView(getApplicationContext(), countryImage, selected.getImageURI());
		}
	}

	private long getCountryDuration() {
		long duration = 0;
		for (int i = 0; i < cityList.size(); i++) {
			duration += cityList.get(i).getDuration();
		}
		selected.setDuration(duration);
		countryDao.update(selected);
		OnlineDatabaseUtils.add(Const.COUNTRIES, selected.getId(), selected);
		return duration;
	}
}
