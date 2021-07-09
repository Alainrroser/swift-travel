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
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.adapter.DayAdapter;
import ch.bbcag.swift_travel.dal.CityDao;
import ch.bbcag.swift_travel.dal.DayDao;
import ch.bbcag.swift_travel.dal.SwiftTravelDatabase;
import ch.bbcag.swift_travel.entities.City;
import ch.bbcag.swift_travel.entities.Day;
import ch.bbcag.swift_travel.utils.Const;
import ch.bbcag.swift_travel.utils.LayoutUtils;
import ch.bbcag.swift_travel.utils.NetworkUtils;
import ch.bbcag.swift_travel.utils.OnlineDatabaseUtils;

public class CityDetailsActivity extends UpButtonActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

	private SearchView searchView;
	private MenuItem searchItem;

	private ImageButton editDescriptionButton;
	private Button submitButton;
	private DayAdapter adapter;

	private TextView titleText;
	private TextView durationText;
	private TextView descriptionText;
	private TextView transportText;
	private EditText editTitle;
	private EditText editDescription;
	private EditText editTransport;
	private ImageView cityImage;

	private City selected;

	private CityDao cityDao;
	private DayDao dayDao;

	private List<Day> dayList = new ArrayList<>();

	private boolean nameValidated = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city_details);

		Intent intent = getIntent();
		String name = intent.getStringExtra(Const.CITY_NAME);
		setTitle(name);

		cityDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getCityDao();
		dayDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getDayDao();

		titleText = findViewById(R.id.city_title);
		durationText = findViewById(R.id.city_duration);
		descriptionText = findViewById(R.id.city_description);
		transportText = findViewById(R.id.city_transport);
		editTitle = findViewById(R.id.city_edit_title);
		editDescription = findViewById(R.id.city_edit_description);
		editTransport = findViewById(R.id.city_edit_transport);
		cityImage = findViewById(R.id.city_image);

		submitButton = findViewById(R.id.city_submit_button);
		editDescriptionButton = findViewById(R.id.city_edit_button);
	}

	@Override
	protected void onStart() {
		super.onStart();

		Group form = findViewById(R.id.city_form);
		form.setVisibility(View.GONE);

		long id = getIntent().getLongExtra(Const.CITY, -1);
		if (id != -1) {
			checkIfLoggedIn(id);
		}
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
			if (selected.getImageCDL() != null) {
				OnlineDatabaseUtils.deleteOnlineImage(selected.getImageCDL());
			}
			selected.setImageCDL(OnlineDatabaseUtils.uploadImage(imageURI));
			cityDao.update(selected);
			OnlineDatabaseUtils.add(Const.CITIES, selected.getId(), selected);
			cityImage.setImageURI(imageURI);
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
		if (adapter != null) {
			adapter.getFilter().filter(searchText);
		}
	}

	private void checkIfLoggedIn(long id) {
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			ifNetworkAvailable(id);
		} else {
			selected = cityDao.getById(id);
			onStartAfterSelectedInitialized();
		}
	}

	private void ifNetworkAvailable(long id) {
		if(NetworkUtils.isNetworkAvailable(getApplicationContext())) {
			OnlineDatabaseUtils.getById(Const.CITIES, id, task -> setObject(task, () -> initializeSelected(task, id)));
		} else {
			generateMessageDialogAndCloseActivity(getString(R.string.internet_connection_error_title), getString(R.string.internet_connection_error_text));
		}
	}


	private void initializeSelected(Task<DocumentSnapshot> selectedTask, long id) {
		if (Objects.requireNonNull(selectedTask.getResult()).toObject(City.class) == null) {
			OnlineDatabaseUtils.add(Const.CITIES, id, cityDao.getById(id));
			OnlineDatabaseUtils.getById(Const.CITIES, id, task -> setObject(task, () -> setSelected(task)));
		} else {
			setSelected(selectedTask);
		}
	}

	private void setSelected(Task<DocumentSnapshot> selectedTask) {
		selected = Objects.requireNonNull(selectedTask.getResult()).toObject(City.class);
		onStartAfterSelectedInitialized();
	}

	private void onStartAfterSelectedInitialized() {
		dayList = dayDao.getAllFromCity(selected.getId());
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			OnlineDatabaseUtils.getAllFromParentId(Const.DAYS, Const.CITY_ID, selected.getId(), task -> addToList(task, () -> synchronizeDays(task)));
		} else {
			getProgressBar().setVisibility(View.GONE);
			onStartAfterListInitialized();
		}
	}

	private void synchronizeDays(Task<QuerySnapshot> task) {
		for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
			Day onlineDay = document.toObject(Day.class);
			addToList(onlineDay);
		}
		runOnUiThread(this::onStartAfterListInitialized);
	}

	private void addToList(Day onlineDay) {
		if (dayList.size() > 0) {
			addIfNotExists(onlineDay);
		} else {
			dayList.add(onlineDay);
		}
	}

	private void addIfNotExists(Day onlineDay) {
		if (!checkIfExistsInList(onlineDay)) {
			dayList.add(onlineDay);
		}
	}

	private boolean checkIfExistsInList(Day onlineDay) {
		boolean existsInList = false;
		for (Day listDay : dayList) {
			if (listDay.getId() == onlineDay.getId()) {
				existsInList = true;
				break;
			}
		}
		return existsInList;
	}

	private void onStartAfterListInitialized() {
		adapter = new DayAdapter(this, dayList);

		addDaysToClickableList();

		editTitle.setText(selected.getName());
		editDescription.setText(selected.getDescription());
		editTransport.setText(selected.getTransport());

		refreshContent();

		editDescriptionButton.setOnClickListener(v -> toggleForm());
		cityImage.setOnClickListener(v -> ImagePicker.with(this).crop().start());
		onSubmitButtonClick();
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
	}

	private void onSubmitButtonClick() {
		submitButton.setOnClickListener(v -> {
			editName();
			editDescription();
			editTransport();
			cityDao.update(selected);
			OnlineDatabaseUtils.add(Const.CITIES, selected.getId(), selected);
			refreshContent();
			toggleForm();
		});
	}

	private void refreshContent() {
		LayoutUtils.setEditableTitleText(titleText, editTitle, selected.getName());
		LayoutUtils.setEditableText(descriptionText, editDescription, selected.getDescription(), getString(R.string.description_hint));
		LayoutUtils.setEditableText(transportText, editTransport, selected.getTransport(), getString(R.string.transport_hint));
		setDuration();
		if (selected.getImageURI() != null && !selected.getImageURI().isEmpty()) {
			LayoutUtils.setImageURIOnImageView(cityImage, selected.getImageURI());
		}
		setTitle(selected.getName());
	}

	private void setDuration() {
		String duration;
		if (selected.getDuration() == 1) {
			duration = selected.getDuration() + " " + getString(R.string.day);
		} else {
			duration = selected.getDuration() + " " + getString(R.string.days);
		}
		LayoutUtils.setTextOnTextView(durationText, duration);
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

	private void editTransport() {
		if (editTransport.getText() != null && !editTransport.getText().toString().isEmpty()) {
			selected.setTransport(editTransport.getText().toString());
		}
	}

	private void toggleForm() {
		boolean notChanged = false;
		Group form = findViewById(R.id.city_form);
		Group content = findViewById(R.id.city_content);

		if (titleText.getText().equals(editTitle.getText().toString())) {
			notChanged = true;
		}

		if (form.getVisibility() == View.VISIBLE && (nameValidated || notChanged)) {
			form.setVisibility(View.GONE);
			content.setVisibility(View.VISIBLE);
		} else {
			editTitle.setText(selected.getName());
			editDescription.setText(selected.getDescription());
			editTransport.setText(selected.getTransport());
			form.setVisibility(View.VISIBLE);
			content.setVisibility(View.GONE);
		}
	}
}