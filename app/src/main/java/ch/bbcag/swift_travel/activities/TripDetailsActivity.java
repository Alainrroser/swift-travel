package ch.bbcag.swift_travel.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Objects;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.adapter.CountryAdapter;
import ch.bbcag.swift_travel.dal.CountryDao;
import ch.bbcag.swift_travel.dal.SwiftTravelDatabase;
import ch.bbcag.swift_travel.dal.TripDao;
import ch.bbcag.swift_travel.entities.Country;
import ch.bbcag.swift_travel.entities.Trip;
import ch.bbcag.swift_travel.utils.Const;

public class TripDetailsActivity extends UpButtonActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {
	private SearchView searchView;
	private MenuItem searchItem;

	private FloatingActionButton floatingActionButton;
	private CountryAdapter adapter;

	private ImageButton editDescriptionButton;
	private Button submitButton;

	private String tripName;
	private Trip selected;

	private TripDao tripDao;
	private CountryDao countryDao;

	private boolean countryExists = false;
	private boolean nameValidated = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_details);

		Intent intent = getIntent();
		tripName = intent.getStringExtra(Const.TRIP_NAME);
		setTitle(tripName);

		tripDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getTripDao();
		countryDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getCountryDao();

		List<Trip> trips = tripDao.getAll();
		for (Trip trip : trips) {
			if (trip.getName().equals(tripName)) {
				selected = trip;
			}
		}

		floatingActionButton = findViewById(R.id.floating_action_button_trip_details);
		editDescriptionButton = findViewById(R.id.edit_button);
		submitButton = findViewById(R.id.submit_button);
	}

	@Override
	protected void onStart() {
		super.onStart();
		getProgressBar().setVisibility(View.GONE);

		List<Country> countries = countryDao.getAllFromTrip(selected.getId());
		adapter = new CountryAdapter(this, countries);

		createCountryFromIntent();
		addCountriesToClickableList();

		Group form = findViewById(R.id.trip_form);
		form.setVisibility(View.GONE);

		refreshContent();

		onFloatingActionButtonClick();
		onEditDescriptionClick();
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

	/**
	 * Creates a trip from the information in the intent if they aren't null.
	 */
	private void createCountryFromIntent() {
		Intent intent = getIntent();
		Country country = new Country();
		addTripInformation(intent, country);
	}

	private void addTripInformation(Intent intent, Country country) {
		if (intent.getStringExtra(Const.COUNTRY_NAME) != null && intent.getBooleanExtra(Const.ADD_COUNTRY, false)) {
			checkIfCountryExists(intent);
			addCountryIfNotExists(intent, country);
		}
	}

	private void checkIfCountryExists(Intent intent) {
		for (Country existingCountry : countryDao.getAllFromTrip(selected.getId())) {
			if (existingCountry.getName().equals(intent.getStringExtra(Const.COUNTRY_NAME))) {
				countryExists = true;
				break;
			}
		}
	}

	private void addCountryIfNotExists(Intent intent, Country country) {
		if (!countryExists) {
			intent.removeExtra(Const.ADD_COUNTRY);
			country.setName(intent.getStringExtra(Const.COUNTRY_NAME));
			country.setImageURI(intent.getStringExtra(Const.FLAG_URI));
			country.setTripID(selected.getId());
			adapter.add(country);
			countryDao.insert(country);
		} else {
			generateMessageDialog(getString(R.string.entry_exists_error_title), getString(R.string.entry_exists_error_text));
		}
	}

	public void addCountriesToClickableList() {
		ListView countries = findViewById(R.id.countries);
		countries.setAdapter(adapter);

		getProgressBar().setVisibility(View.GONE);

		AdapterView.OnItemClickListener mListClickedHandler = (parent, v, position, id) -> {
			Intent intent = new Intent(getApplicationContext(), CountryDetailsActivity.class);
			Country selected = (Country) parent.getItemAtPosition(position);
			intent.putExtra(Const.COUNTRY_NAME, selected.getName());
			startActivity(intent);
		};

		countries.setOnItemClickListener(mListClickedHandler);
	}

	private void refreshContent() {
		TextView title = findViewById(R.id.trip_title);
		TextView description = findViewById(R.id.trip_description);

		EditText editTitle = findViewById(R.id.edit_title);
		EditText editDescription = findViewById(R.id.edit_description);

		ImageView tripImage = findViewById(R.id.trip_image);

		title.setText(selected.getName());
		if (selected.getName().length() >= 20) {
			title.setTextSize(18);
		} else {
			title.setTextSize(24);
		}
		setTitle(selected.getName());
		editTitle.setText(selected.getName());

		TextView duration = findViewById(R.id.trip_duration);
		duration.setText(selected.getDuration());

		if (selected.getDescription().equals("")) {
			description.setText(R.string.add_description);
		} else {
			description.setText(selected.getDescription());
		}
		description.setMovementMethod(new ScrollingMovementMethod());
		editDescription.setText(selected.getDescription());

		if (selected.getImageURI() != null) {
			tripImage.setImageURI(Uri.parse(selected.getImageURI()));
		}
	}

	private void toggleForm() {
		Group form = findViewById(R.id.trip_form);
		Group content = findViewById(R.id.trip_content);

		if (form.getVisibility() == View.VISIBLE && nameValidated) {
			form.setVisibility(View.GONE);
			content.setVisibility(View.VISIBLE);
		} else {
			form.setVisibility(View.VISIBLE);
			content.setVisibility(View.GONE);
		}
	}

	private void onFloatingActionButtonClick() {
		floatingActionButton.setOnClickListener(v -> {
			Intent intent = new Intent(getApplicationContext(), ChooseActivity.class);
			intent.putExtra(Const.NAME, Const.COUNTRY);
			intent.putExtra(Const.TRIP_NAME, tripName);
			startActivity(intent);
		});
	}

	private void onEditDescriptionClick() {
		editDescriptionButton.setOnClickListener(v -> toggleForm());
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
		if (Objects.requireNonNull(editTitle.getText()).toString().length() > 0 && Objects.requireNonNull(editTitle.getText()).toString().length() <= 40) {
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

	public CountryAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(CountryAdapter adapter) {
		this.adapter = adapter;
	}

	public CountryDao getCountryDao() {
		return countryDao;
	}

	public void setCountryDao(CountryDao countryDao) {
		this.countryDao = countryDao;
	}
}