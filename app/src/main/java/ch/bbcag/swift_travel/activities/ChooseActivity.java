package ch.bbcag.swift_travel.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import ch.bbcag.swift_travel.adapter.ChooseCountryAdapter;
import ch.bbcag.swift_travel.utils.Const;
import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.dal.ApiRepository;
import ch.bbcag.swift_travel.model.Country;

public class ChooseActivity extends UpButtonActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {
	private ChooseCountryAdapter adapter;

	private SearchView searchView;
	private MenuItem searchItem;

	private String tripName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose);
		Intent intent = getIntent();
		tripName = intent.getStringExtra(Const.TRIP_NAME);
		setTitle(Const.CHOOSE + getIntent().getStringExtra(Const.NAME));
	}

	@Override
	protected void onStart() {
		super.onStart();
		addCountriesToClickableList();
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

	private void filterAdapter(String filterString) {
		adapter.getFilter().filter(filterString);
	}

	@Override
	public boolean onQueryTextSubmit(String s) {
		filterAdapter(s);
		return true;
	}

	@Override
	public boolean onQueryTextChange(String s) {
		filterAdapter(s);
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

	private void addCountriesToClickableList() {
		Response.Listener<JSONArray> responseListener = this::addCountriesToAdapter;
		Response.ErrorListener errorListener = error -> generateAlertDialog(getString(R.string.add_countries_to_list_error_title), getString(R.string.add_countries_to_list_error_text));
		ApiRepository.getJsonArray(getApplicationContext(), Const.COUNTRIES_URL, responseListener, errorListener);
		getProgressBar().setVisibility(View.GONE);
	}

	private void addCountriesToAdapter(JSONArray response) {
		ListView allCountries = findViewById(R.id.all_countries);
		initializeAdapter(response);
		allCountries.setAdapter(adapter);
		allCountries.setOnItemClickListener((parent, view, position, id) -> {
			Intent intent = new Intent(getApplicationContext(), TripDetailsActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			Country country = (Country) ((ListView)parent).getItemAtPosition(position);
			intent.putExtra(Const.COUNTRY_NAME, country.getName());
			intent.putExtra(Const.FLAG_URI, country.getImageURI().toString());
			intent.putExtra(Const.TRIP_NAME, tripName);

			startActivity(intent);
		});
	}

	private void initializeAdapter(JSONArray response) {
		try {
			List<Country> countriesList = new ArrayList<>();
			addCountryToList(response, countriesList);
			adapter = new ChooseCountryAdapter(getApplicationContext(), countriesList);
		} catch (Exception e) {
			generateAlertDialog(getString(R.string.add_countries_to_list_error_title), getString(R.string.add_countries_to_list_error_text));
		}
	}

	private void addCountryToList(JSONArray response, List<Country> countriesList) throws JSONException {
		for (int position = 0; position < response.length(); position++) {
			Country country = new Country();
			country.setName(response.getJSONObject(position).getString(Const.NAME));
			country.setDescription("");
			country.setImageURI(Uri.parse(response.getJSONObject(position).getString(Const.FLAG)));
			countriesList.add(country);
		}
	}
}