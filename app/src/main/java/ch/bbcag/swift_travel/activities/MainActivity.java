package ch.bbcag.swift_travel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.adapter.TripAdapter;
import ch.bbcag.swift_travel.dal.SwiftTravelDatabase;
import ch.bbcag.swift_travel.dal.TripDao;
import ch.bbcag.swift_travel.entities.Trip;
import ch.bbcag.swift_travel.utils.Const;

public class MainActivity extends BaseActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {
	private SearchView searchView;
	private MenuItem searchItem;

	private FloatingActionButton floatingActionButton;
	private TripAdapter adapter;
	private ListView allTrips;

	private TripDao tripDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTitle(getString(R.string.app_name));

		tripDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getTripDao();

		floatingActionButton = findViewById(R.id.floating_action_button_main);
		allTrips = findViewById(R.id.trips);
	}

	@Override
	protected void onStart() {
		super.onStart();
		List<Trip> trips = tripDao.getAll();
		adapter = new TripAdapter(this, trips);
		allTrips.setAdapter(adapter);
		getProgressBar().setVisibility(View.GONE);
		createTripFromIntent();
		onTripClick();

		onFloatingActionButtonClick();
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
			if (intent.getStringExtra(Const.TRIP_DESCRIPTION) != null) {
				trip.setDescription(intent.getStringExtra(Const.TRIP_DESCRIPTION));
			}
			if (intent.getStringExtra(Const.IMAGE_URI) != null) {
				trip.setImageURI(intent.getStringExtra(Const.IMAGE_URI));
			}
			long id = tripDao.insert(trip);
			trip.setId(id);

			adapter.add(trip);
		}
	}

	private void onTripClick() {
		AdapterView.OnItemClickListener mListClickedHandler = (parent, v, position, id) -> {
			Intent intent = new Intent(getApplicationContext(), TripDetailsActivity.class);
			Trip selected = (Trip) parent.getItemAtPosition(position);
			intent.putExtra(Const.TRIP, selected.getId());
			intent.putExtra(Const.TRIP_NAME, selected.getName());
			startActivity(intent);
		};
		allTrips.setOnItemClickListener(mListClickedHandler);
	}

	private void onFloatingActionButtonClick() {
		floatingActionButton.setOnClickListener(v -> {
			Intent intent = new Intent(getApplicationContext(), CreateTripActivity.class);
			startActivity(intent);
		});
	}

	public TripAdapter getAdapter() {
		return adapter;
	}

	public TripDao getTripDao() {
		return tripDao;
	}
}