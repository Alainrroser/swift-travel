package ch.bbcag.swift_travel.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.core.util.Pair;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.adapter.SpinnerAdapter;
import ch.bbcag.swift_travel.entities.City;
import ch.bbcag.swift_travel.entities.Location;
import ch.bbcag.swift_travel.entities.Trip;
import ch.bbcag.swift_travel.utils.Const;
import ch.bbcag.swift_travel.utils.DateTimeUtils;
import ch.bbcag.swift_travel.utils.OnlineDatabaseUtils;
import zion830.com.range_picker_dialog.TimeRange;
import zion830.com.range_picker_dialog.TimeRangePickerDialog;

public class CreateActivity extends UpButtonActivity implements AdapterView.OnItemSelectedListener {
	TimeRangePickerDialog timeRangePickerDialog = new TimeRangePickerDialog();
	private Trip trip = new Trip();
	private City city = new City();
	private Location location = new Location();

	private TextInputLayout nameLayout;
	private EditText nameEdit;
	private EditText descriptionEdit;
	private EditText transportEdit;
	private ImageButton chooseImage;
	private TextView durationDate;
	private Button selectDurationDate;
	private TextView durationTime;
	private Button selectDurationTime;
	private Spinner categorySpinner;
	private Button create;
	private MaterialDatePicker<Pair<Long, Long>> materialDatePicker;
	private String startDate;
	private String endDate;
	private boolean dateSelected = false;
	private boolean datePickerOpened = false;

	private String startTime;
	private String endTime;
	private String timeDuration;
	private boolean timeSelected = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create);
		setTitle(getIntent().getStringExtra(Const.CREATE_TITLE));

		TextView createTitle = findViewById(R.id.create_title);
		createTitle.setText(getIntent().getStringExtra(Const.CREATE_TITLE));

		nameLayout = findViewById(R.id.add_title_layout);
		nameEdit = findViewById(R.id.add_title);
		descriptionEdit = findViewById(R.id.add_description);
		transportEdit = findViewById(R.id.add_transport);
		chooseImage = findViewById(R.id.create_place_holder_image);
		durationDate = findViewById(R.id.duration_date);
		selectDurationDate = findViewById(R.id.select_duration_date);
		durationTime = findViewById(R.id.duration_time);
		selectDurationTime = findViewById(R.id.select_duration_time);
		categorySpinner = findViewById(R.id.category_spinner);
		create = findViewById(R.id.create);
	}

	@Override
	protected void onStart() {
		super.onStart();
		getProgressBar().setVisibility(View.GONE);

		setGroupVisibility();
		create.setOnClickListener(v -> startIntentOrShowError());
		chooseImage.setOnClickListener(v -> ImagePicker.with(this).crop().start());
	}

	private void setGroupVisibility() {
		Group cityDuration = findViewById(R.id.city_duration);
		Group locationDuration = findViewById(R.id.location_duration);
		if (getIntent().getBooleanExtra(Const.ADD_CITY, false)) {
			cityDuration.setVisibility(View.VISIBLE);
			locationDuration.setVisibility(View.GONE);
			categorySpinner.setVisibility(View.GONE);
			setDatePicker();
		} else if (getIntent().getBooleanExtra(Const.ADD_LOCATION, false)) {
			cityDuration.setVisibility(View.GONE);
			categorySpinner.setVisibility(View.VISIBLE);
			locationDuration.setVisibility(View.VISIBLE);
			setCategorySpinner();
			setTimePicker();
		} else {
			cityDuration.setVisibility(View.GONE);
			locationDuration.setVisibility(View.GONE);
			categorySpinner.setVisibility(View.GONE);
			findViewById(R.id.add_transport_layout).setVisibility(View.GONE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK && data != null) {
			Uri imageURI = setImageURI(data);
			chooseImage.setScaleType(ImageView.ScaleType.FIT_XY);
			chooseImage.setImageURI(imageURI);
		}
	}

	private Uri setImageURI(Intent data) {
		Uri imageURI = data.getData();
		String imageURIString = imageURI.toString();
		String cdl = "";
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			cdl = OnlineDatabaseUtils.uploadImage(imageURI);
		}
		setImages(cdl, imageURIString);
		return imageURI;
	}

	private void setImages(String cdl, String imageURIString) {
		Intent intent = getIntent();
		if (intent.getBooleanExtra(Const.ADD_TRIP, false)) {
			setTripImage(cdl, imageURIString);
		} else if (intent.getBooleanExtra(Const.ADD_CITY, false)) {
			setCityImage(cdl, imageURIString);
		} else if (intent.getBooleanExtra(Const.ADD_LOCATION, false)) {
			setLocationImage(cdl, imageURIString);
		}
	}

	private void setTripImage(String cdl, String imageURIString) {
		trip.setImageURI(imageURIString);
		if (!cdl.equals("")) {
			trip.setImageCDL(cdl);
		}
	}

	private void setCityImage(String cdl, String imageURIString) {
		city.setImageURI(imageURIString);
		if (!cdl.equals("")) {
			city.setImageCDL(cdl);
		}
	}

	private void setLocationImage(String cdl, String imageURIString) {
		location.setImageURI(imageURIString);
		if (!cdl.equals("")) {
			location.setImageCDL(cdl);
		}
	}

	private void setDatePicker() {
		long millisecondsInAWeek = System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 7);
		MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
		materialDateBuilder.setTitleText(getString(R.string.select_city_duration)).setSelection(new Pair<>(System.currentTimeMillis(), millisecondsInAWeek));
		materialDatePicker = materialDateBuilder.build();

		selectDurationDate.setOnClickListener(v -> showDatePickerIfClosed());
		materialDatePicker.addOnPositiveButtonClickListener(this::onPositiveMaterialDatePickerClick);
		materialDatePicker.addOnCancelListener(selection -> datePickerOpened = false);
		materialDatePicker.addOnDismissListener(selection -> datePickerOpened = false);
		materialDatePicker.addOnNegativeButtonClickListener(selection -> datePickerOpened = false);
	}


	private void setTimePicker() {
		timeRangePickerDialog.setOneDayMode(true);
		selectDurationTime.setOnClickListener(v -> timeRangePickerDialog.show(getSupportFragmentManager().beginTransaction(), timeRangePickerDialog.toString()));
		timeRangePickerDialog.setOnTimeRangeSelectedListener(timeRange -> {
			timeDuration = getDuration(timeRange);
			startTime = DateTimeUtils.addZeroToHour(timeRange.getStartHour()) + ":" + DateTimeUtils.addZeroToMinute(timeRange.getStartMinute());
			endTime = DateTimeUtils.addZeroToHour(timeRange.getEndHour()) + ":" + DateTimeUtils.addZeroToMinute(timeRange.getEndMinute());
			String timeRangeString = startTime + "-" + endTime;
			durationTime.setText(timeRangeString);
			timeSelected = true;
		});
	}

	private void setCategorySpinner() {
		categorySpinner.setAdapter(new SpinnerAdapter(this, R.layout.dropdown_spinner, getResources().getStringArray(R.array.location_categories)));
		categorySpinner.setOnItemSelectedListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
		switch (position) {
			case Const.CATEGORY_DEFAULT:
				location.setCategory(Const.CATEGORY_DEFAULT);
				break;
			case Const.CATEGORY_HOTEL:
				location.setCategory(Const.CATEGORY_HOTEL);
				break;
			case Const.CATEGORY_RESTAURANT:
				location.setCategory(Const.CATEGORY_RESTAURANT);
				break;
			case Const.CATEGORY_LOCATION:
				location.setCategory(Const.CATEGORY_LOCATION);
				break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// Callback method to be invoked when the selection disappears from this view.
		Toast.makeText(this, getString(R.string.category_error), Toast.LENGTH_SHORT).show();
	}

	private String getDuration(TimeRange timeRange) {
		int hours = timeRange.getEndHour();
		int minutes = timeRange.getEndMinute();
		if (timeRange.getStartMinute() > timeRange.getEndMinute()) {
			hours -= 1;
			minutes += 60;
		}
		minutes -= timeRange.getStartMinute();
		hours -= timeRange.getStartHour();
		return hours + ":" + minutes + "h";
	}

	private void onPositiveMaterialDatePickerClick(Pair<Long, Long> selection) {
		startDate = (Instant.ofEpochMilli(selection.first).atZone(ZoneId.of("+0")).toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
		endDate = (Instant.ofEpochMilli(selection.second).atZone(ZoneId.of("+0")).toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
		String dateRange = startDate + "-" + endDate;
		durationDate.setText(dateRange);
		dateSelected = true;
		datePickerOpened = false;
	}

	private void showDatePickerIfClosed() {
		// Check if selectDuration was clicked already because you cannot open two at the same time
		if (!datePickerOpened) {
			materialDatePicker.show(getSupportFragmentManager(), materialDatePicker.toString());
			datePickerOpened = true;
		}
	}

	private void startIntentOrShowError() {
		if (Objects.requireNonNull(nameEdit.getText()).toString().length() > 0 && Objects.requireNonNull(nameEdit.getText()).toString().length() <= 40) {
			startActivity();
		} else {
			nameLayout.setError(getString(R.string.length_error));
		}
	}

	private void startActivity() {
		if (getIntent().getBooleanExtra(Const.ADD_TRIP, false)) {
			startMain();
		} else if (getIntent().getBooleanExtra(Const.ADD_CITY, false)) {
			startCountryDetailsIfDateSelected();
		} else if (getIntent().getBooleanExtra(Const.ADD_LOCATION, false)) {
			startDayDetailsIfTimeSelected();
		}
	}

	private void startCountryDetailsIfDateSelected() {
		if (dateSelected) {
			startCountryDetails();
		} else {
			durationDate.requestFocus();
			durationDate.setError(getString(R.string.duration_error));
		}
	}

	private void startDayDetailsIfTimeSelected() {
		if (timeSelected) {
			startDayDetails();
		} else {
			durationTime.requestFocus();
			durationTime.setError(getString(R.string.duration_error));
		}
	}

	private void startMain() {
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(Const.ADD_TRIP, true);

		nameLayout.setError(null);
		trip.setName(Objects.requireNonNull(nameEdit.getText()).toString());
		intent.putExtra(Const.TRIP_NAME, trip.getName());

		intent.putExtra(Const.IMAGE_URI, trip.getImageURI());
		intent.putExtra(Const.IMAGE_CDL, trip.getImageCDL());

		if (descriptionEdit.getText() == null) {
			descriptionEdit.setText("");
		}
		trip.setDescription(descriptionEdit.getText().toString());
		intent.putExtra(Const.TRIP_DESCRIPTION, trip.getDescription());

		startActivity(intent);
	}

	private void startCountryDetails() {
		Intent intent = new Intent(getApplicationContext(), CountryDetailsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(Const.ADD_CITY, true);

		nameLayout.setError(null);
		city.setName(Objects.requireNonNull(nameEdit.getText()).toString());
		intent.putExtra(Const.CITY_NAME, city.getName());

		intent.putExtra(Const.IMAGE_URI, city.getImageURI());
		intent.putExtra(Const.IMAGE_CDL, city.getImageCDL());

		if (descriptionEdit.getText() == null) {
			descriptionEdit.setText("");
		}
		city.setDescription(descriptionEdit.getText().toString());
		intent.putExtra(Const.CITY_DESCRIPTION, city.getDescription());

		if (transportEdit.getText() == null) {
			transportEdit.setText("");
		}
		city.setTransport(transportEdit.getText().toString());
		intent.putExtra(Const.TRANSPORT, city.getTransport());

		intent.putExtra(Const.START_DATE, startDate);
		intent.putExtra(Const.END_DATE, endDate);

		intent.putExtra(Const.COUNTRY, getIntent().getLongExtra(Const.COUNTRY, -1));

		startActivity(intent);
	}

	private void startDayDetails() {
		Intent intent = new Intent(getApplicationContext(), DayDetailsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(Const.ADD_LOCATION, true);

		nameLayout.setError(null);
		location.setName(Objects.requireNonNull(nameEdit.getText()).toString());
		intent.putExtra(Const.LOCATION_NAME, location.getName());

		intent.putExtra(Const.IMAGE_URI, location.getImageURI());
		intent.putExtra(Const.IMAGE_CDL, location.getImageCDL());

		if (descriptionEdit.getText() == null) {
			descriptionEdit.setText("");
		}
		location.setDescription(descriptionEdit.getText().toString());
		intent.putExtra(Const.LOCATION_DESCRIPTION, location.getDescription());

		if (transportEdit.getText() == null) {
			transportEdit.setText("");
		}
		location.setTransport(transportEdit.getText().toString());
		intent.putExtra(Const.TRANSPORT, location.getTransport());

		intent.putExtra(Const.CATEGORY, location.getCategory());

		intent.putExtra(Const.START_TIME, startTime);
		intent.putExtra(Const.END_TIME, endTime);
		intent.putExtra(Const.TIME_DURATION, timeDuration);

		intent.putExtra(Const.DAY, getIntent().getLongExtra(Const.DAY, -1));

		startActivity(intent);
	}
}