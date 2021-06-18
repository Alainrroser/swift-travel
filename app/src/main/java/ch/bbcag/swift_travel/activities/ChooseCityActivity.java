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

import org.geonames.Style;
import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;

import java.util.ArrayList;
import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.adapter.ChooseCityAdapter;
import ch.bbcag.swift_travel.dal.CityDao;
import ch.bbcag.swift_travel.dal.CountryDao;
import ch.bbcag.swift_travel.dal.SwiftTravelDatabase;
import ch.bbcag.swift_travel.entities.City;
import ch.bbcag.swift_travel.utils.Const;

public class ChooseCityActivity extends UpButtonActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {
	private SearchView searchView;
	private MenuItem searchItem;

	private ChooseCityAdapter adapter;
	private ListView allCities;

	private List<City> addedCities;
	private boolean cityWasAdded = false;

	private CityDao cityDao;
	private CountryDao countryDao;

	private long id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_city);

		setTitle(Const.CHOOSE + Const.CITY);

		cityDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getCityDao();
		countryDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getCountryDao();
	}

	@Override
	protected void onStart() {
		super.onStart();

		id = getIntent().getLongExtra(Const.COUNTRY, -1);
		addedCities = cityDao.getAllFromCountry(id);

		allCities = findViewById(R.id.all_cities);
		new Thread(this::addAllCitiesToClickableList).start();
		onCityClick();
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

	private void addAllCitiesToClickableList() {
		try {
			ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
			searchCriteria.setCountryCode(countryDao.getCodeById(id));
			searchCriteria.setFeatureCode(Const.FEATURE_CODE);
			searchCriteria.setStyle(Style.FULL);
			WebService.setGeoNamesServer(Const.CITIES_URL);
			WebService.setUserName(Const.CITIES_URL_USER_NAME);
			ToponymSearchResult searchResult = WebService.search(searchCriteria);
			addToponymsToAdapter(searchResult);
			runOnUiThread(() -> getProgressBar().setVisibility(View.GONE));
		} catch (Exception e) {
			generateMessageDialogAndCloseActivity(getString(R.string.add_entry_to_list_error_title), getString(R.string.add_entries_to_list_error_text));
		}
	}

	private void addToponymsToAdapter(ToponymSearchResult searchResult) {
		for(Toponym toponym : searchResult.getToponyms()) {
			initializeAdapter(toponym);
		}
		runOnUiThread(() -> allCities.setAdapter(adapter));
	}

	private void onCityClick() {
		allCities.setOnItemClickListener((parent, view, position, id) -> {
			Intent intent = new Intent(getApplicationContext(), CountryDetailsActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(Const.ADD_CITY, true);

			City city = (City) parent.getItemAtPosition(position);
			intent.putExtra(Const.CITY_NAME, city.getName());
			intent.putExtra(Const.COUNTRY, getIntent().getLongExtra(Const.COUNTRY, -1));
			startActivity(intent);

			adapter.remove(city);
		});
	}

	private void initializeAdapter(Toponym toponym) {
		try {
			List<City> allCities = new ArrayList<>();
			addCityToList(toponym, allCities);
			adapter = new ChooseCityAdapter(getApplicationContext(), allCities);
		} catch (Exception e) {
			generateMessageDialog(getString(R.string.add_entry_to_list_error_title), getString(R.string.add_entries_to_list_error_text));
		}
	}

	private void addCityToList(Toponym toponym, List<City> allCities) {
		City city = new City();
		city.setName(toponym.getName());
		checkIfCountryWasAdded(city);
		if (!cityWasAdded) {
			allCities.add(city);
		}
	}

	private void checkIfCountryWasAdded(City city) {
		for (City addedCity : addedCities) {
			if (addedCity.getName().equals(city.getName())) {
				cityWasAdded = true;
				break;
			} else {
				cityWasAdded = false;
			}
		}
	}
}