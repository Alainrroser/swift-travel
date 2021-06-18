package ch.bbcag.swift_travel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.constraintlayout.widget.Group;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.adapter.CityAdapter;
import ch.bbcag.swift_travel.dal.CityDao;
import ch.bbcag.swift_travel.dal.CountryDao;
import ch.bbcag.swift_travel.dal.SwiftTravelDatabase;
import ch.bbcag.swift_travel.entities.City;
import ch.bbcag.swift_travel.entities.Country;
import ch.bbcag.swift_travel.utils.Const;
import ch.bbcag.swift_travel.utils.Layout;

public class CountryDetailsActivity extends UpButtonActivity {
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

	private boolean cityExists = false;

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

	public void addCitiesToClickableList() {
		ListView cities = findViewById(R.id.cities);
		cities.setAdapter(adapter);

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
		addTripInformation(intent, city);
	}

	private void addTripInformation(Intent intent, City city) {
		if (intent.getStringExtra(Const.CITY_NAME) != null && intent.getBooleanExtra(Const.ADD_CITY, false)) {
			checkIfCityExists(intent);
			addCityIfNotExists(intent, city);
		}
	}

	private void checkIfCityExists(Intent intent) {
		for (City existingCity : cityDao.getAllFromCountry(selected.getId())) {
			if (existingCity.getName().equals(intent.getStringExtra(Const.CITY_NAME))) {
				cityExists = true;
				break;
			}
		}
	}

	private void addCityIfNotExists(Intent intent, City city) {
		if (!cityExists) {
			intent.removeExtra(Const.ADD_CITY);
			city.setName(intent.getStringExtra(Const.CITY_NAME));
			city.setImageURI(intent.getStringExtra(Const.FLAG_URI));
			city.setCountryId(selected.getId());
			adapter.add(city);
			cityDao.insert(city);
		} else {
			generateMessageDialog(getString(R.string.entry_exists_error_title), getString(R.string.entry_exists_error_text));
		}
	}

	private void onFloatingActionButtonClick() {
		floatingActionButton.setOnClickListener(v -> {
			Intent intent = new Intent(getApplicationContext(), ChooseCountryActivity.class);
			intent.putExtra(Const.NAME, Const.CITY);
			intent.putExtra(Const.COUNTRY_NAME, cityName);
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
		Layout.setTitleText(findViewById(R.id.country_title), selected.getName());
		setTitle(selected.getName());
		Layout.setTextOnTextView(findViewById(R.id.country_description), selected.getDescription());
		Layout.setTextOnTextView(findViewById(R.id.country_duration), selected.getDuration());
		if (selected.getImageURI() != null) {
			Layout.setOnlineImageURIOnImageView(getApplicationContext(), findViewById(R.id.country_image), selected.getImageURI());
		}

	}

	public CityAdapter getAdapter() {
		return adapter;
	}

	public CityDao getCityDao() {
		return cityDao;
	}
}
