package ch.bbcag.swift_travel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.adapter.ChooseCountryAdapter;
import ch.bbcag.swift_travel.dal.ApiRepository;
import ch.bbcag.swift_travel.dal.CountryDao;
import ch.bbcag.swift_travel.dal.SwiftTravelDatabase;
import ch.bbcag.swift_travel.entities.Country;
import ch.bbcag.swift_travel.utils.Const;
import ch.bbcag.swift_travel.utils.NetworkUtils;

public class ChooseCountryActivity extends UpButtonActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {
	private SearchView searchView;
	private MenuItem searchItem;

	private ChooseCountryAdapter adapter;

	private List<Country> addedCountries;
	private boolean countryWasAdded = false;
	private boolean countriesLoaded = false;

	private CountryDao countryDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_country);

		setTitle(getIntent().getStringExtra(Const.CHOOSE_TITLE));

		countryDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getCountryDao();
	}

	@Override
	protected void onStart() {
		super.onStart();

		addAllCountriesToClickableList();
		addedCountries = countryDao.getAllFromTrip(getIntent().getLongExtra(Const.TRIP, -1));
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
				return countriesLoaded;
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

	private void addAllCountriesToClickableList() {
		if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {
			Response.Listener<JSONArray> responseListener = this::addAllCountriesToAdapter;
			Response.ErrorListener errorListener = error -> generateMessageDialogAndCloseActivity(getString(R.string.add_entry_to_list_error_title), getString(R.string.add_entries_to_list_error_text));
			ApiRepository.getJsonArray(getApplicationContext(), Const.COUNTRIES_URL, responseListener, errorListener);
		} else {
			generateMessageDialogAndCloseActivity(getString(R.string.internet_connection_error_title), getString(R.string.internet_connection_error_text));
		}
	}

	private void addAllCountriesToAdapter(JSONArray response) {
		ListView allCountries = findViewById(R.id.all_countries);
		initializeAdapter(response);
		allCountries.setAdapter(adapter);
		onCountryClick(allCountries);
		getProgressBar().setVisibility(View.GONE);
		countriesLoaded = true;
	}

	private void onCountryClick(ListView allCountries) {
		allCountries.setOnItemClickListener((parent, view, position, id) -> {
			Intent intent = new Intent(getApplicationContext(), TripDetailsActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(Const.ADD_COUNTRY, true);

			Country country = (Country) parent.getItemAtPosition(position);
			intent.putExtra(Const.COUNTRY_NAME, country.getName());
			intent.putExtra(Const.COUNTRY_CODE, country.getCode());
			intent.putExtra(Const.FLAG_URI, country.getImageURI());
			intent.putExtra(Const.TRIP, getIntent().getLongExtra(Const.TRIP, -1));
			startActivity(intent);

			adapter.remove(country);
		});
	}

	private void initializeAdapter(JSONArray response) {
		try {
			List<Country> allCountries = new ArrayList<>();
			addCountryToList(response, allCountries);
			adapter = new ChooseCountryAdapter(this, allCountries);
		} catch (Exception e) {
			generateMessageDialog(getString(R.string.add_entry_to_list_error_title), getString(R.string.add_entries_to_list_error_text));
		}
	}

	private void addCountryToList(JSONArray response, List<Country> allCountries) throws JSONException {
		for (int position = 0; position < response.length(); position++) {
			Country country = new Country();
			country.setName(response.getJSONObject(position).getString(Const.NAME));
			country.setCode(response.getJSONObject(position).getString(Const.ALPHA_2_CODE));
			country.setImageURI(response.getJSONObject(position).getString(Const.FLAG));
			checkIfCountryWasAdded(country);
			if (!countryWasAdded) {
				allCountries.add(country);
			}
		}
	}

	private void checkIfCountryWasAdded(Country country) {
		for (Country addedCountry : addedCountries) {
			if (addedCountry.getName().equals(country.getName())) {
				countryWasAdded = true;
				break;
			} else {
				countryWasAdded = false;
			}
		}
	}
}