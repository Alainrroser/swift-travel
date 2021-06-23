package ch.bbcag.swift_travel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;

import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.adapter.LocationAdapter;
import ch.bbcag.swift_travel.dal.DayDao;
import ch.bbcag.swift_travel.dal.LocationDao;
import ch.bbcag.swift_travel.dal.SwiftTravelDatabase;
import ch.bbcag.swift_travel.entities.Day;
import ch.bbcag.swift_travel.entities.Location;
import ch.bbcag.swift_travel.utils.Const;
import ch.bbcag.swift_travel.utils.LayoutUtils;

public class DayDetailsActivity extends UpButtonActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {
	private SearchView searchView;
	private MenuItem searchItem;

	private ImageButton editDescriptionButton;
	private Button submitButton;
	private LocationAdapter adapter;

	private Day selected;

	private DayDao dayDao;
	private LocationDao locationDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_day_details);

		Intent intent = getIntent();
		String name = intent.getStringExtra(Const.DAY_NAME);
		setTitle(name);

		dayDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getDayDao();
		locationDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getLocationDao();

		submitButton = findViewById(R.id.day_submit_button);
		editDescriptionButton = findViewById(R.id.edit_button);
	}

	@Override
	protected void onStart() {
		super.onStart();
		getProgressBar().setVisibility(View.VISIBLE);

		long id = getIntent().getLongExtra(Const.DAY, -1);
		if (id != -1) {
			selected = dayDao.getById(id);
		}

		List<Location> locations = locationDao.getAllFromDay(selected.getId());
		adapter = new LocationAdapter(this, locations);

		createLocationFromIntent();
		addLocationsToClickableList();

		refreshContent();

		Group form = findViewById(R.id.day_form);
		form.setVisibility(View.GONE);

		getProgressBar().setVisibility(View.GONE);

//		onFloatingActionButtonClick();
		editDescriptionButton.setOnClickListener(v -> toggleForm());
//		onSubmitButtonClick();
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

	public void addLocationsToClickableList() {
		ListView locations = findViewById(R.id.locations);
		locations.setAdapter(adapter);

		AdapterView.OnItemClickListener mListClickedHandler = (parent, v, position, id) -> {
			Intent intent = new Intent(getApplicationContext(), CityDetailsActivity.class);
			Location selected = (Location) parent.getItemAtPosition(position);
			intent.putExtra(Const.LOCATION_NAME, selected.getName());
			intent.putExtra(Const.LOCATION, selected.getId());
			startActivity(intent);
		};

		getProgressBar().setVisibility(View.GONE);

		locations.setOnItemClickListener(mListClickedHandler);
	}

	private void createLocationFromIntent() {
		Intent intent = getIntent();
		Location location = new Location();
		if (intent.getStringExtra(Const.LOCATION_NAME) != null && intent.getBooleanExtra(Const.ADD_LOCATION, false)) {
			intent.removeExtra(Const.ADD_LOCATION);
			location.setName(intent.getStringExtra(Const.LOCATION_NAME));
			location.setDescription(intent.getStringExtra(Const.LOCATION_DESCRIPTION));
			location.setImageURI(intent.getStringExtra(Const.IMAGE_URI));
			location.setDayId(selected.getId());
			long id = locationDao.insert(location);
			location.setId(id);

			adapter.add(location);
		}
	}

	public void refreshContent() {
		LayoutUtils.setTitleText(findViewById(R.id.day_title), selected.getName());
		setTitle(selected.getName());
		LayoutUtils.setEditableDescriptionText(findViewById(R.id.day_description), findViewById(R.id.edit_description), selected.getDescription());
		//TODO
		LayoutUtils.setTextOnTextView(findViewById(R.id.day_duration), "TODO");
		if (selected.getImageURI() != null) {
			LayoutUtils.setOnlineImageURIOnImageView(getApplicationContext(), findViewById(R.id.day_image), selected.getImageURI());
		}
	}

	private void toggleForm() {
		Group form = findViewById(R.id.day_form);
		Group content = findViewById(R.id.day_content);

		if (form.getVisibility() == View.VISIBLE) {
			form.setVisibility(View.GONE);
			content.setVisibility(View.VISIBLE);
		} else {
			form.setVisibility(View.VISIBLE);
			content.setVisibility(View.GONE);
		}
	}

	public LocationAdapter getAdapter() {
		return adapter;
	}

	public LocationDao getLocationDao() {
		return locationDao;
	}
}
