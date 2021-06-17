package ch.bbcag.swift_travel.activities;

import android.content.Intent;
import android.net.Uri;
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

import ch.bbcag.swift_travel.utils.Const;
import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.adapter.TripAdapter;
import ch.bbcag.swift_travel.dal.TripDao;
import ch.bbcag.swift_travel.model.Trip;

public class MainActivity extends BaseActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {
	private SearchView searchView;
	private MenuItem searchItem;

	private FloatingActionButton floatingActionButton;
	private TripAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTitle(getString(R.string.app_name));

		floatingActionButton = findViewById(R.id.floating_action_button_main);
	}

	@Override
	protected void onStart() {
		super.onStart();
		List<Trip> trips = TripDao.getAll();
		adapter = new TripAdapter(this, trips);
		addTripsToClickableList();
		createTripFromIntent();
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
		System.out.println(searchView.getQuery());
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

	/**
	 * Creates a trip from the information in the intent if they aren't null.
	 */
	private void createTripFromIntent() {
		Intent intent = getIntent();
		Trip trip = new Trip();
		addTripInformation(intent, trip);
	}

	private void addTripInformation(Intent intent, Trip trip) {
		if (intent.getStringExtra(Const.TRIP_NAME) != null) {
			trip.setName(intent.getStringExtra(Const.TRIP_NAME));
			addDescription(intent, trip);
			addImageURI(intent, trip);
			adapter.add(trip);
		}
	}

	private void addDescription(Intent intent, Trip trip) {
		if (intent.getStringExtra(Const.TRIP_DESCRIPTION) != null) {
			trip.setDescription(intent.getStringExtra(Const.TRIP_DESCRIPTION));
		}
	}

	private void addImageURI(Intent intent, Trip trip) {
		if (intent.getStringExtra(Const.TRIP_IMAGE_URI) != null) {
			trip.setImageURI(Uri.parse(intent.getStringExtra(Const.TRIP_IMAGE_URI)));
		}
	}

	public void addTripsToClickableList() {
		ListView allTrips = findViewById(R.id.trips);
		allTrips.setAdapter(adapter);
		getProgressBar().setVisibility(View.GONE);
		onTripClick(allTrips);
	}

	private void onTripClick(ListView allTrips) {
		AdapterView.OnItemClickListener mListClickedHandler = (parent, v, position, id) -> {
			Intent intent = new Intent(getApplicationContext(), TripDetailsActivity.class);
			Trip selected = (Trip) parent.getItemAtPosition(position);
			intent.putExtra(Const.TRIP_NAME, selected.getName());
			startActivity(intent);
		};
		allTrips.setOnItemClickListener(mListClickedHandler);
	}

	private void onFloatingActionButtonClick() {
		floatingActionButton.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), CreateTripActivity.class)));
	}
}