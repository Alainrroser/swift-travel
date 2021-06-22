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
import android.widget.ListView;
import android.widget.SearchView;

import androidx.constraintlayout.widget.Group;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.util.Comparator;
import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.adapter.CityAdapter;
import ch.bbcag.swift_travel.dal.CityDao;
import ch.bbcag.swift_travel.dal.CountryDao;
import ch.bbcag.swift_travel.dal.SwiftTravelDatabase;
import ch.bbcag.swift_travel.entities.City;
import ch.bbcag.swift_travel.entities.Country;
import ch.bbcag.swift_travel.utils.Const;
import ch.bbcag.swift_travel.utils.DateUtils;
import ch.bbcag.swift_travel.utils.LayoutUtils;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class CountryDetailsActivity extends UpButtonActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {
	private SearchView searchView;
	private MenuItem searchItem;

	private FloatingActionButton floatingActionButton;
	private ImageButton editDescriptionButton;
	private Button submitButton;
	private CityAdapter adapter;

	private String cityName;
	private Country selected;

	private CountryDao countryDao;
	private CityDao cityDao;

	private boolean durationOverlaps = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_country_details);

		Intent intent = getIntent();
		String name = intent.getStringExtra(Const.COUNTRY_NAME);
		setTitle(name);

		countryDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getCountryDao();
		cityDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getCityDao();

		submitButton = findViewById(R.id.country_submit_button);
		editDescriptionButton = findViewById(R.id.edit_button);
		floatingActionButton = findViewById(R.id.floating_action_button_country_details);
	}

	@Override
	protected void onStart() {
		super.onStart();
		getProgressBar().setVisibility(View.GONE);

		long id = getIntent().getLongExtra(Const.COUNTRY, -1);
		if (id != -1) {
			selected = countryDao.getById(id);
		}

		List<City> cities = cityDao.getAllFromCountry(selected.getId());
		adapter = new CityAdapter(this, cities);

		createCityFromIntent();
		addCitiesToClickableList();

		refreshContent();

		Group form = findViewById(R.id.country_form);
		form.setVisibility(View.GONE);

		onFloatingActionButtonClick();
		editDescriptionButton.setOnClickListener(v -> toggleForm());
		onSubmitButtonClick();
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

	public void addCitiesToClickableList() {
		ListView cities = findViewById(R.id.cities);
		cities.setAdapter(adapter);
		sortCitiesByStartDate();

		getProgressBar().setVisibility(View.GONE);

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
			checkIfDurationOverlaps(intent);
			addCityIfNotExists(intent, city);
		}
	}

	private void checkIfDurationOverlaps(Intent intent) {
		try {
			for (City existingCity : cityDao.getAllFromCountry(selected.getId())) {
				long startDateExisting = DateUtils.parseDateToMillis(existingCity.getStartDate());
				long startDateNew = DateUtils.parseDateToMillis(intent.getStringExtra(Const.START_DATE));
				long endDateExisting = DateUtils.parseDateToMillis(existingCity.getEndDate());
				long endDateNew = DateUtils.parseDateToMillis(intent.getStringExtra(Const.END_DATE));
				if (startDateExisting != -1 && startDateNew != -1 && endDateExisting != -1 && endDateNew != -1) {
					if (max(startDateNew, startDateExisting) < min(endDateNew, endDateExisting)) {
						durationOverlaps = true;
						break;
					}
				} else {
					throw new Exception();
				}
			}
		} catch (Exception e) {
			generateMessageDialogAndCloseActivity(getString(R.string.duration_parse_error_title), getString(R.string.duration_parse_error_text));
		}
	}

	private void addCityIfNotExists(Intent intent, City city) {
		if (!durationOverlaps) {
			intent.removeExtra(Const.ADD_CITY);

			city.setName(intent.getStringExtra(Const.CITY_NAME));
			city.setDescription(intent.getStringExtra(Const.CITY_DESCRIPTION));
			city.setImageURI(intent.getStringExtra(Const.IMAGE_URI));
			city.setStartDate(intent.getStringExtra(Const.START_DATE));
			city.setEndDate(intent.getStringExtra(Const.END_DATE));
			if(getCityDuration(city) != -1) {
				city.setDuration(getCityDuration(city));
			} else {
				generateMessageDialogAndCloseActivity(getString(R.string.duration_parse_error_title), getString(R.string.duration_parse_error_text));
				System.out.println(getCityDuration(city));
			}
			city.setCountryId(selected.getId());
			long id = cityDao.insert(city);
			city.setId(id);

			adapter.add(city);
			sortCitiesByStartDate();
		} else {
			generateMessageDialog(getString(R.string.duration_overlap_error_title), getString(R.string.duration_overlap_error_text));
		}
	}

	private long getCityDuration(City city) {
		try {
			long startDate = DateUtils.parseDateToMillis(city.getStartDate());
			long endDate = DateUtils.parseDateToMillis(city.getEndDate());
			if(startDate != -1 && endDate != -1) {
				return DateUtils.getDaysCountFromTimeSpan(startDate, endDate);
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			generateMessageDialogAndCloseActivity(getString(R.string.duration_parse_error_title), getString(R.string.duration_parse_error_text));
		}
		return -1;
	}

	private void sortCitiesByStartDate() {
		adapter.sort((Comparator<City>) (cityOne, cityTwo) -> {
			try {
				return compareCityStartDates(cityOne, cityTwo);
			} catch (ParseException e) {
				generateMessageDialogAndCloseActivity(getString(R.string.duration_parse_error_title), getString(R.string.duration_parse_error_text));
			}
			return 0;
		});
	}

	private int compareCityStartDates(City cityOne, City cityTwo) throws ParseException {
		long cityOneStartDate = DateUtils.parseDateToMillis(cityOne.getStartDate());
		long cityTwoStartDate = DateUtils.parseDateToMillis(cityTwo.getStartDate());
		if (cityOneStartDate != -1 && cityTwoStartDate != -1) {
			return Long.compare(cityOneStartDate, cityTwoStartDate);
		} else {
			generateMessageDialogAndCloseActivity(getString(R.string.duration_parse_error_title), getString(R.string.duration_parse_error_text));
		}
		return 0;
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
			refreshContent();
			toggleForm();
		});
	}

	private void editDescription() {
		EditText editDescription = findViewById(R.id.edit_description);
		if (!editDescription.getText().toString().equals("")) {
			selected.setDescription(editDescription.getText().toString());
			countryDao.setDescription(editDescription.getText().toString());
		}
	}

	private void toggleForm() {
		Group form = findViewById(R.id.country_form);
		Group content = findViewById(R.id.country_content);

		if (form.getVisibility() == View.VISIBLE) {
			form.setVisibility(View.GONE);
			content.setVisibility(View.VISIBLE);
		} else {
			form.setVisibility(View.VISIBLE);
			content.setVisibility(View.GONE);
		}
	}

	private void refreshContent() {
		LayoutUtils.setTitleText(findViewById(R.id.country_title), selected.getName());
		setTitle(selected.getName());
		LayoutUtils.setTextOnTextView(findViewById(R.id.country_description), selected.getDescription());
		LayoutUtils.setTextOnTextView(findViewById(R.id.country_duration), selected.getDuration());
		if (selected.getImageURI() != null) {
			LayoutUtils.setOnlineImageURIOnImageView(getApplicationContext(), findViewById(R.id.country_image), selected.getImageURI());
		}

	}

	public CityAdapter getAdapter() {
		return adapter;
	}

	public CityDao getCityDao() {
		return cityDao;
	}
}
