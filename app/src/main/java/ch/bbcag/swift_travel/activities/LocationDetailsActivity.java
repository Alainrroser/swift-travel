package ch.bbcag.swift_travel.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.adapter.ImageAdapter;
import ch.bbcag.swift_travel.adapter.SpinnerAdapter;
import ch.bbcag.swift_travel.dal.ImageDao;
import ch.bbcag.swift_travel.dal.LocationDao;
import ch.bbcag.swift_travel.dal.SwiftTravelDatabase;
import ch.bbcag.swift_travel.entities.Image;
import ch.bbcag.swift_travel.entities.Location;
import ch.bbcag.swift_travel.utils.Const;
import ch.bbcag.swift_travel.utils.LayoutUtils;
import ch.bbcag.swift_travel.utils.NetworkUtils;
import ch.bbcag.swift_travel.utils.OnlineDatabaseUtils;

public class LocationDetailsActivity extends UpButtonActivity implements AdapterView.OnItemSelectedListener {
	private ImageButton editDescriptionButton;
	private Button submitButton;

	private Spinner categorySpinner;

	private TextView titleText;
	private TextView durationText;
	private TextView descriptionText;
	private TextView transportText;
	private EditText editTitle;
	private EditText editDescription;
	private EditText editTransport;
	private ImageView locationImage;
	private ImageView categoryIcon;

	private FloatingActionButton floatingActionButton;
	private GridView imageGrid;
	private ImageAdapter imageAdapter;

	private Location selected;
	private Image clickedImage;

	private LocationDao locationDao;
	private ImageDao imageDao;

	private List<Image> imageList;

	private boolean nameValidated = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_details);

		Intent intent = getIntent();
		String name = intent.getStringExtra(Const.LOCATION_NAME);
		setTitle(name);

		locationDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getLocationDao();
		imageDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getImageDao();

		titleText = findViewById(R.id.location_title);
		durationText = findViewById(R.id.location_duration);
		descriptionText = findViewById(R.id.location_description);
		transportText = findViewById(R.id.location_transport);
		editTitle = findViewById(R.id.edit_title);
		editDescription = findViewById(R.id.location_edit_description);
		editTransport = findViewById(R.id.location_edit_transport);
		locationImage = findViewById(R.id.location_image);
		categoryIcon = findViewById(R.id.location_category_icon);

		categorySpinner = findViewById(R.id.location_category_spinner);

		imageGrid = findViewById(R.id.location_images);

		submitButton = findViewById(R.id.location_submit_button);
		floatingActionButton = findViewById(R.id.floating_action_button_location_details);
		editDescriptionButton = findViewById(R.id.edit_button);
	}

	@Override
	protected void onStart() {
		super.onStart();

		Group form = findViewById(R.id.location_form);
		form.setVisibility(View.GONE);

		long id = getIntent().getLongExtra(Const.LOCATION, -1);
		if (id != -1) {
			checkIfLoggedIn(id);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null && resultCode == Activity.RESULT_OK) {
			setImages(data, requestCode);
		}
	}

	private void setImages(Intent data, int requestCode) {
		Uri imageURI = data.getData();
		if (requestCode == Const.LOCATION_IMAGE_REQUEST_CODE) {
			setLocationImage(imageURI);
		} else if (requestCode == Const.ADD_IMAGE_REQUEST_CODE) {
			addImage(imageURI);
		} else if (requestCode == Const.REPLACE_IMAGE_REQUEST_CODE) {
			replaceImage(imageURI);
		}
	}

	private void setLocationImage(Uri imageURI) {
		selected.setImageURI(imageURI.toString());
		if (selected.getImageCDL() != null) {
			OnlineDatabaseUtils.deleteOnlineImage(selected.getImageCDL());
		}
		selected.setImageCDL(OnlineDatabaseUtils.uploadImage(imageURI));
		locationDao.update(selected);
		OnlineDatabaseUtils.add(Const.LOCATIONS, selected.getId(), selected);
		locationImage.setImageURI(imageURI);
	}

	private void addImage(Uri imageURI) {
		Image image = new Image();
		image.setImageURI(imageURI.toString());
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			image.setImageCDL(OnlineDatabaseUtils.uploadImage(imageURI));
		}
		image.setLocationId(selected.getId());
		long id = imageDao.insert(image);
		image.setId(id);
		OnlineDatabaseUtils.add(Const.IMAGES, image.getId(), image);
		imageAdapter.add(image);
	}

	private void replaceImage(Uri imageURI) {
		clickedImage.setImageURI(imageURI.toString());
		if (selected.getImageCDL() != null) {
			OnlineDatabaseUtils.deleteOnlineImage(selected.getImageCDL());
		}
		selected.setImageCDL(OnlineDatabaseUtils.uploadImage(imageURI));
		imageDao.update(clickedImage);
		OnlineDatabaseUtils.add(Const.IMAGES, clickedImage.getId(), clickedImage);
		imageAdapter.clear();
		List<Image> images = imageDao.getAllFromLocation(selected.getId());
		imageAdapter.addAll(images);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
		switch (position) {
			case Const.CATEGORY_HOTEL:
				selected.setCategory(Const.CATEGORY_HOTEL);
				break;
			case Const.CATEGORY_RESTAURANT:
				selected.setCategory(Const.CATEGORY_RESTAURANT);
				break;
			case Const.CATEGORY_LOCATION:
				selected.setCategory(Const.CATEGORY_LOCATION);
				break;
			default:
				break;
		}
		locationDao.update(selected);
		setCategory();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// Callback method to be invoked when the selection disappears from this view.
		Toast.makeText(this, getString(R.string.category_error), Toast.LENGTH_SHORT).show();
	}

	private void setCategorySpinner() {
		categorySpinner.setAdapter(new SpinnerAdapter(this, R.layout.dropdown_spinner, getResources().getStringArray(R.array.location_categories)));
		categorySpinner.setOnItemSelectedListener(this);
	}

	private void checkIfLoggedIn(long id) {
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			ifNetworkAvailable(id);
		} else {
			selected = locationDao.getById(id);
			onStartAfterSelectedInitialized();
		}
	}

	private void ifNetworkAvailable(long id) {
		if(NetworkUtils.isNetworkAvailable(getApplicationContext())) {
			OnlineDatabaseUtils.getById(Const.LOCATIONS, id, task -> setObject(task, () -> initializeSelected(task, id)));
		} else {
			generateMessageDialogAndCloseActivity(getString(R.string.internet_connection_error_title), getString(R.string.internet_connection_error_text));
		}
	}

	private void initializeSelected(Task<DocumentSnapshot> selectedTask, long id) {
		if (Objects.requireNonNull(selectedTask.getResult()).toObject(Location.class) == null) {
			OnlineDatabaseUtils.add(Const.LOCATIONS, id, locationDao.getById(id));
			OnlineDatabaseUtils.getById(Const.LOCATIONS, id, task -> setObject(task, () -> setSelected(task)));
		} else {
			setSelected(selectedTask);
		}
	}

	private void setSelected(Task<DocumentSnapshot> selectedTask) {
		selected = Objects.requireNonNull(selectedTask.getResult()).toObject(Location.class);
		onStartAfterSelectedInitialized();
	}

	private void onStartAfterSelectedInitialized() {
		imageList = imageDao.getAllFromLocation(selected.getId());
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			OnlineDatabaseUtils.getAllFromParentId(Const.IMAGES, Const.LOCATION_ID, selected.getId(), task -> addToList(task, () -> synchronizeImages(task)));
		} else {
			getProgressBar().setVisibility(View.GONE);
			onStartAfterListInitialized();
		}
	}

	private void synchronizeImages(Task<QuerySnapshot> task) {
		for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
			Image onlineImage = document.toObject(Image.class);
			addToList(onlineImage);
		}
		runOnUiThread(this::onStartAfterListInitialized);
	}

	private void addToList(Image onlineImage) {
		if (imageList.size() > 0) {
			addIfNotExists(onlineImage);
		} else {
			imageList.add(onlineImage);
		}
	}

	private void addIfNotExists(Image onlineImage) {
		if (!checkIfExistsInList(onlineImage)) {
			imageList.add(onlineImage);
		}
	}

	private boolean checkIfExistsInList(Image onlineImage) {
		boolean existsInList = false;
		for (Image listImage : imageList) {
			if (listImage.getId() == onlineImage.getId()) {
				existsInList = true;
				break;
			}
		}
		return existsInList;
	}

	private void onStartAfterListInitialized() {
		imageAdapter = new ImageAdapter(this, imageList);

		imageGrid.setAdapter(imageAdapter);
		imageGrid.setOnItemClickListener((parent, view, position, id1) -> {
			Intent intent = new Intent(getApplicationContext(), ImageDetailsActivity.class);
			intent.putExtra(Const.IMAGE_URI, imageAdapter.getItem(position).getImageURI());
			startActivity(intent);
		});
		imageGrid.setOnItemLongClickListener((parent, view, position, id1) -> {
			ImagePicker.with(this).crop().start(Const.REPLACE_IMAGE_REQUEST_CODE);
			clickedImage = imageAdapter.getItem(position);
			return true;
		});

		editTitle.setText(selected.getName());
		editDescription.setText(selected.getDescription());
		editTransport.setText(selected.getTransport());
		categorySpinner.setSelection(selected.getCategory());

		refreshContent();
		setCategory();

		editDescriptionButton.setOnClickListener(v -> toggleForm());
		locationImage.setOnClickListener(v -> ImagePicker.with(this).crop().start(Const.LOCATION_IMAGE_REQUEST_CODE));
		floatingActionButton.setOnClickListener(v -> ImagePicker.with(this).crop().start(Const.ADD_IMAGE_REQUEST_CODE));

		setCategorySpinner();
		onSubmitButtonClick();
	}

	private void setCategory() {
		switch (selected.getCategory()) {
			case Const.CATEGORY_HOTEL:
				categoryIcon.setImageResource(R.drawable.category_hotel);
				break;
			case Const.CATEGORY_RESTAURANT:
				categoryIcon.setImageResource(R.drawable.category_restaurant);
				break;
			case Const.CATEGORY_LOCATION:
				categoryIcon.setImageResource(R.drawable.category_location);
				break;
			default:
				categoryIcon.setImageResource(R.drawable.category_unknown);
				break;
		}
	}

	private void onSubmitButtonClick() {
		submitButton.setOnClickListener(v -> {
			editName();
			editDescription();
			editTransport();
			locationDao.update(selected);
			OnlineDatabaseUtils.add(Const.LOCATIONS, selected.getId(), selected);
			refreshContent();
			toggleForm();
		});
	}

	public void refreshContent() {
		LayoutUtils.setEditableTitleText(titleText, editTitle, selected.getName());
		LayoutUtils.setEditableText(descriptionText, editDescription, selected.getDescription(), getString(R.string.description_hint));
		LayoutUtils.setEditableText(transportText, editTransport, selected.getTransport(), getString(R.string.transport_hint));
		LayoutUtils.setTextOnTextView(durationText, selected.getDuration() + ", " + selected.getStartTime() + "-" + selected.getEndTime());
		if (selected.getImageURI() != null && !selected.getImageURI().isEmpty()) {
			LayoutUtils.setImageURIOnImageView(locationImage, selected.getImageURI());
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

	private void editTransport() {
		if (editTransport.getText() != null && !editTransport.getText().toString().isEmpty()) {
			selected.setTransport(editTransport.getText().toString());
		}
	}

	private void toggleForm() {
		boolean notChanged = false;

		Group form = findViewById(R.id.location_form);
		Group content = findViewById(R.id.location_content);

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
			categorySpinner.setSelection(selected.getCategory());
			form.setVisibility(View.VISIBLE);
			content.setVisibility(View.GONE);
		}
	}
}