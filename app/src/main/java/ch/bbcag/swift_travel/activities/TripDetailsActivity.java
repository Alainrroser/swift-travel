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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Objects;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.adapter.CountryAdapter;
import ch.bbcag.swift_travel.dal.CityDao;
import ch.bbcag.swift_travel.dal.CountryDao;
import ch.bbcag.swift_travel.dal.SwiftTravelDatabase;
import ch.bbcag.swift_travel.dal.TripDao;
import ch.bbcag.swift_travel.entities.Country;
import ch.bbcag.swift_travel.entities.Trip;
import ch.bbcag.swift_travel.utils.Const;
import ch.bbcag.swift_travel.utils.LayoutUtils;

public class TripDetailsActivity extends UpButtonActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {
	private SearchView searchView;
	private MenuItem searchItem;

	private FloatingActionButton floatingActionButton;
	private CountryAdapter adapter;
	private ListView countries;

	private ImageButton editDescriptionButton;
	private Button submitButton;

	private Trip selected;

	private TripDao tripDao;
	private CountryDao countryDao;
	private CityDao cityDao;

	private boolean nameValidated = false;

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

		editDescriptionButton = findViewById(R.id.edit_button);
		submitButton = findViewById(R.id.trip_submit_button);
	}

	@Override
	protected void onStart() {
		super.onStart();

		long id = getIntent().getLongExtra(Const.TRIP, -1);
		if (id != -1) {
			selected = tripDao.getById(id);
		}

		List<Country> countriesList = countryDao.getAllFromTrip(selected.getId());
		adapter = new CountryAdapter(this, countriesList);

		countries.setAdapter(adapter);
		getProgressBar().setVisibility(View.GONE);
		createCountryFromIntent();

		Group form = findViewById(R.id.trip_form);
		form.setVisibility(View.GONE);

		refreshContent();

		getProgressBar().setVisibility(View.GONE);

		onCountryClick();
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
			adapter.add(country);
		}
	}

	private void onCountryClick() {
		AdapterView.OnItemClickListener mListClickedHandler = (parent, v, position, id) -> {
			Intent intent = new Intent(getApplicationContext(), CountryDetailsActivity.class);
			Country selected = (Country) parent.getItemAtPosition(position);
			intent.putExtra(Const.COUNTRY_NAME, selected.getName());
			intent.putExtra(Const.COUNTRY, selected.getId());
			startActivity(intent);
		};
		countries.setOnItemClickListener(mListClickedHandler);
	}

	private void refreshContent() {
		LayoutUtils.setEditableTitleText(findViewById(R.id.trip_title), findViewById(R.id.edit_title), selected.getName());
		setTitle(selected.getName());
		LayoutUtils.setEditableDescriptionText(findViewById(R.id.trip_description), findViewById(R.id.edit_description), selected.getDescription());
		LayoutUtils.setTextOnTextView(findViewById(R.id.trip_duration), getTripDuration() + " " + getString(R.string.days_title));
		if (selected.getImageURI() != null) {
			LayoutUtils.setImageURIOnImageView(findViewById(R.id.trip_image), selected.getImageURI());
		}
	}

	private void toggleForm() {
		boolean notChanged = false;

		Group form = findViewById(R.id.trip_form);
		Group content = findViewById(R.id.trip_content);

		EditText editTitle = findViewById(R.id.edit_title);
		TextView title = findViewById(R.id.trip_title);

		if (title.getText().equals(editTitle.getText().toString())) {
			notChanged = true;
		}

		if (form.getVisibility() == View.VISIBLE && (nameValidated || notChanged)) {
			form.setVisibility(View.GONE);
			content.setVisibility(View.VISIBLE);
		} else {
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
			refreshContent();
			toggleForm();
		});
	}

	private void editName() {
		TextInputLayout editTitleLayout = findViewById(R.id.edit_title_layout);
		TextInputEditText editTitle = findViewById(R.id.edit_title);
		if (Objects.requireNonNull(editTitle.getText()).toString().length() > 0 && Objects.requireNonNull(editTitle.getText()).toString().length() <= Const.TITLE_LENGTH) {
			nameValidated = true;
			selected.setName(editTitle.getText().toString());
			tripDao.setName(editTitle.getText().toString());
		} else {
			nameValidated = false;
			editTitleLayout.setError(getString(R.string.trip_name_error));
		}
	}

	private void editDescription() {
		EditText editDescription = findViewById(R.id.edit_description);
		if (!editDescription.getText().toString().equals("")) {
			selected.setDescription(editDescription.getText().toString());
			tripDao.setDescription(editDescription.getText().toString());
		}
	}

	private long getTripDuration() {
		List<Country> countries = countryDao.getAllFromTrip(selected.getId());
		long duration = 0;
		for (int i = 0; i < countries.size(); i++) {
			duration += countries.get(i).getDuration();
		}
		return duration;
	}

	public CountryAdapter getAdapter() {
		return adapter;
	}

	public CountryDao getCountryDao() {
		return countryDao;
	}
}