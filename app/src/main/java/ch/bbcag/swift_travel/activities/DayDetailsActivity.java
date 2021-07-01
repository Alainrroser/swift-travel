package ch.bbcag.swift_travel.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Objects;

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
	private FloatingActionButton floatingActionButton;
	private LocationAdapter adapter;

	private TextView titleText;
	private TextView dateText;
	private TextView descriptionText;
	private EditText editTitle;
	private EditText editDescription;
	private ImageView dayImage;

	private Day selected;

	private DayDao dayDao;
	private LocationDao locationDao;

	private boolean nameValidated = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_day_details);

		Intent intent = getIntent();
		String name = intent.getStringExtra(Const.DAY_NAME);
		setTitle(name);

		dayDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getDayDao();
		locationDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getLocationDao();

		titleText = findViewById(R.id.day_title);
		dateText = findViewById(R.id.day_date);
		descriptionText = findViewById(R.id.day_description);
		editTitle = findViewById(R.id.day_edit_title);
		editDescription = findViewById(R.id.day_edit_description);
		dayImage = findViewById(R.id.day_image);

		submitButton = findViewById(R.id.day_submit_button);
		floatingActionButton = findViewById(R.id.floating_action_button_day_details);
		editDescriptionButton = findViewById(R.id.day_edit_button);
	}

	@Override
	protected void onStart() {
		super.onStart();

		long id = getIntent().getLongExtra(Const.DAY, -1);
		if (id != -1) {
			selected = dayDao.getById(id);
		}

		List<Location> locations = locationDao.getAllFromDay(selected.getId());
		adapter = new LocationAdapter(this, locations);

		createLocationFromIntent();
		addLocationsToClickableList();

		editTitle.setText(selected.getName());
		editDescription.setText(selected.getDescription());

		refreshContent();

		Group form = findViewById(R.id.day_form);
		form.setVisibility(View.GONE);

		getProgressBar().setVisibility(View.GONE);

		onFloatingActionButtonClick();
		editDescriptionButton.setOnClickListener(v -> toggleForm());
		dayImage.setOnClickListener(v -> ImagePicker.with(this).crop().start());
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK && data != null) {
			Uri imageURI = data.getData();
			selected.setImageURI(imageURI.toString());
			dayDao.update(selected);
			dayImage.setImageURI(imageURI);
		}
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
			Intent intent = new Intent(getApplicationContext(), LocationDetailsActivity.class);
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
			location.setTransport(intent.getStringExtra(Const.TRANSPORT));
			location.setImageURI(intent.getStringExtra(Const.IMAGE_URI));
			location.setStartTime(intent.getStringExtra(Const.START_TIME));
			location.setEndTime(intent.getStringExtra(Const.END_TIME));
			location.setDuration(intent.getStringExtra(Const.TIME_DURATION));
			location.setDayId(selected.getId());
			long id = locationDao.insert(location);
			location.setId(id);

			adapter.add(location);
		}
	}

	private void onFloatingActionButtonClick() {
		floatingActionButton.setOnClickListener(v -> {
			Intent intent = new Intent(getApplicationContext(), CreateActivity.class);
			intent.putExtra(Const.ADD_LOCATION, true);
			intent.putExtra(Const.CREATE_TITLE, getString(R.string.create_location_title));
			intent.putExtra(Const.DAY, selected.getId());
			startActivity(intent);
		});
	}

	private void onSubmitButtonClick() {
		submitButton.setOnClickListener(v -> {
			editName();
			editDescription();
			dayDao.update(selected);
			refreshContent();
			toggleForm();
		});
	}

	public void refreshContent() {
		LayoutUtils.setEditableTitleText(titleText, editTitle, selected.getName());
		LayoutUtils.setEditableText(descriptionText, editDescription, selected.getDescription(), getString(R.string.description_hint));
		LayoutUtils.setTextOnTextView(dateText, selected.getDate());
		if (selected.getImageURI() != null && !selected.getImageURI().isEmpty()) {
			LayoutUtils.setImageURIOnImageView(dayImage, selected.getImageURI());
		}
		setTitle(selected.getName());
	}

	private void editName() {
		TextInputLayout editTitleLayout = findViewById(R.id.trip_edit_title_layout);
		if (Objects.requireNonNull(editTitle.getText()).toString().length() > 0 && Objects.requireNonNull(editTitle.getText()).toString().length() <= Const.TITLE_LENGTH) {
			nameValidated = true;
			selected.setName(editTitle.getText().toString());
		} else {
			nameValidated = false;
			editTitleLayout.setError(getString(R.string.length_error));
		}
	}

	private void editDescription() {
		if (editDescription.getText() != null && !editDescription.getText().toString().isEmpty()) {
			selected.setDescription(editDescription.getText().toString());
		}
	}

	private void toggleForm() {
		boolean notChanged = false;

		Group form = findViewById(R.id.day_form);
		Group content = findViewById(R.id.day_content);

		if (titleText.getText().equals(editTitle.getText().toString())) {
			notChanged = true;
		}

		if (form.getVisibility() == View.VISIBLE && (nameValidated || notChanged)) {
			form.setVisibility(View.GONE);
			content.setVisibility(View.VISIBLE);
		} else {
			editTitle.setText(selected.getName());
			editDescription.setText(selected.getDescription());
			form.setVisibility(View.VISIBLE);
			content.setVisibility(View.GONE);
		}
	}
}
