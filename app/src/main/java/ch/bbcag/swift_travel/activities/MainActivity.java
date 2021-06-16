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

import ch.bbcag.swift_travel.CreateTrip;
import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.adapter.TripAdapter;
import ch.bbcag.swift_travel.dal.TripDao;
import ch.bbcag.swift_travel.model.Trip;

public class MainActivity extends BaseActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {
	private FloatingActionButton floatingActionButton;
	private TripAdapter adapter;

	private SearchView searchView;
	private MenuItem searchItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTitle(getString(R.string.app_name));

		floatingActionButton = findViewById(R.id.floating_action_button);
	}

	@Override
	protected void onStart() {
		super.onStart();
		List<Trip> trips = TripDao.getAll();
		adapter = new TripAdapter(this, trips);
		addTripsToClickableList();
		onFloatingActionButtonClick();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.search_menu, menu);

		searchItem = menu.findItem(R.id.search);
		searchView = (SearchView) searchItem.getActionView();
		searchView.setQueryHint(getString(R.string.search_hint));
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

	public void addTripsToClickableList() {
		ListView listView = findViewById(R.id.trips);
		listView.setAdapter(adapter);

		getProgressBar().setVisibility(View.GONE);

		AdapterView.OnItemClickListener mListClickedHandler = (parent, v, position, id) -> {
			Intent intent = new Intent(getApplicationContext(), TripDetailsActivity.class);
			Trip selected = (Trip) parent.getItemAtPosition(position);
			intent.putExtra("tripName", selected.getName());
			startActivity(intent);
		};

		listView.setOnItemClickListener(mListClickedHandler);
	}

	private void onFloatingActionButtonClick() {
		floatingActionButton.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), CreateTrip.class)));
	}
}