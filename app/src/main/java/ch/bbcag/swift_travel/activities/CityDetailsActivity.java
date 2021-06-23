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

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;

import java.util.ArrayList;
import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.adapter.DayAdapter;
import ch.bbcag.swift_travel.dal.CityDao;
import ch.bbcag.swift_travel.dal.DayDao;
import ch.bbcag.swift_travel.dal.SwiftTravelDatabase;
import ch.bbcag.swift_travel.entities.City;
import ch.bbcag.swift_travel.entities.Day;
import ch.bbcag.swift_travel.utils.Const;
import ch.bbcag.swift_travel.utils.LayoutUtils;

public class CityDetailsActivity extends UpButtonActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener{

	private SearchView searchView;
	private MenuItem searchItem;

	private ImageButton editDescriptionButton;
	private Button submitButton;
	private DayAdapter adapter;

	private City selected;

	private CityDao cityDao;
	private DayDao dayDao;

	private boolean durationOverlaps = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city_details);

		Intent intent = getIntent();
		String name = intent.getStringExtra(Const.CITY_NAME);
		setTitle(name);

		cityDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getCityDao();
		dayDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getDayDao();

		submitButton = findViewById(R.id.city_submit_button);
		editDescriptionButton = findViewById(R.id.edit_button);
	}

	@Override
	protected void onStart() {
		super.onStart();
		getProgressBar().setVisibility(View.VISIBLE);

		long id = getIntent().getLongExtra(Const.CITY, -1);
		if (id != -1) {
			selected = cityDao.getById(id);
		}

		selected.setDuration(selected.getDuration() * -1);

		List<Day> days = createDaysFromCityDuration();
		adapter = new DayAdapter(this, days);

		createDaysFromCityDuration();
		addDaysToClickableList();

		refreshContent();

		Group form = findViewById(R.id.city_form);
		form.setVisibility(View.GONE);

		getProgressBar().setVisibility(View.GONE);

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

	public void addDaysToClickableList() {
		ListView days = findViewById(R.id.days);
		days.setAdapter(adapter);

		AdapterView.OnItemClickListener mListClickedHandler = (parent, v, position, id) -> {
			Intent intent = new Intent(getApplicationContext(), DayDetailsActivity.class);
			Day selected = (Day) parent.getItemAtPosition(position);
			intent.putExtra(Const.DAY_NAME, selected.getName());
			intent.putExtra(Const.DAY, selected.getId());
			startActivity(intent);
		};

		days.setOnItemClickListener(mListClickedHandler);
		getProgressBar().setVisibility(View.GONE);
	}

	private List<Day> createDaysFromCityDuration() {
		List<Day> days = new ArrayList<>();
		for (int i = 0; i < selected.getDuration(); i++){
			Day day = new Day();
			day.setName(getString(R.string.day) + " " + i);
			day.setCityId(selected.getId());
			days.add(day);
		}
		return days;
	}

	private void onSubmitButtonClick() {
		submitButton.setOnClickListener(v -> {
			editDescription();
			refreshContent();
			toggleForm();
		});
	}

	private void refreshContent() {
		LayoutUtils.setTitleText(findViewById(R.id.city_title), selected.getName());
		setTitle(selected.getName());
		LayoutUtils.setTextOnTextView(findViewById(R.id.city_description), selected.getDescription());
		LayoutUtils.setTextOnTextView(findViewById(R.id.city_duration), String.valueOf(selected.getDuration()));
		if (selected.getImageURI() != null) {
			LayoutUtils.setOnlineImageURIOnImageView(getApplicationContext(), findViewById(R.id.country_image), selected.getImageURI());
		}
	}

	private void editDescription() {
		EditText editDescription = findViewById(R.id.edit_description);
		if (!editDescription.getText().toString().equals("")) {
			selected.setDescription(editDescription.getText().toString());
			cityDao.setDescription(editDescription.getText().toString());
		}
	}

	private void toggleForm() {
		Group form = findViewById(R.id.city_form);
		Group content = findViewById(R.id.city_content);

		if (form.getVisibility() == View.VISIBLE) {
			form.setVisibility(View.GONE);
			content.setVisibility(View.VISIBLE);
		} else {
			form.setVisibility(View.VISIBLE);
			content.setVisibility(View.GONE);
		}
	}

	public DayAdapter getAdapter() {
		return adapter;
	}

	public DayDao getDayDao() {
		return dayDao;
	}
}