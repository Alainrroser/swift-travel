package ch.bbcag.swift_travel.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.core.util.Pair;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.entities.City;
import ch.bbcag.swift_travel.entities.Trip;
import ch.bbcag.swift_travel.utils.Const;

public class CreateActivity extends UpButtonActivity {
	private Trip trip = new Trip();
	private City city = new City();

	private TextInputLayout nameLayout;
	private TextInputEditText nameEdit;
	private TextInputEditText descriptionEdit;
	private ImageButton chooseImage;
	private TextView duration;
	private Button selectDuration;
	private Button create;

	private MaterialDatePicker<Pair<Long, Long>> materialDatePicker;

	private boolean dateSelected = false;

	private String startDate;
	private String endDate;
	private String dateRange;

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
		chooseImage = findViewById(R.id.place_holder_image);
		duration = findViewById(R.id.duration);
		selectDuration = findViewById(R.id.select_duration);
		create = findViewById(R.id.create);
	}

	@Override
	protected void onStart() {
		super.onStart();
		getProgressBar().setVisibility(View.GONE);

		Group city_duration = findViewById(R.id.city_duration);
		if (getIntent().getBooleanExtra(Const.ADD_CITY, false)) {
			city_duration.setVisibility(View.VISIBLE);

			MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker().setTitleText(getString(R.string.select_city_duration_title));
			materialDatePicker = materialDateBuilder.build();

			selectDuration.setOnClickListener(v -> materialDatePicker.show(getSupportFragmentManager(), materialDatePicker.toString()));
			materialDatePicker.addOnPositiveButtonClickListener(selection -> {

				startDate = Instant.ofEpochMilli(selection.first).atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
				endDate = Instant.ofEpochMilli(selection.second).atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
				dateRange = startDate + "-" + endDate;
				dateSelected = true;
				duration.setText(dateRange);
			});
		} else {
			city_duration.setVisibility(View.GONE);
		}
		create.setOnClickListener(v -> startIntentOrShowError());
		chooseImage.setOnClickListener(v -> ImagePicker.with(this).cropSquare().start());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK && data != null) {
			Uri imageURI = data.getData();
			if (getIntent().getBooleanExtra(Const.ADD_TRIP, false)) {
				trip.setImageURI(imageURI.toString());
			} else if (getIntent().getBooleanExtra(Const.ADD_CITY, false)) {
				city.setImageURI(imageURI.toString());
			}

			chooseImage.setImageURI(imageURI);
		}
	}

	private void startIntentOrShowError() {
		if (Objects.requireNonNull(nameEdit.getText()).toString().length() > 0 && Objects.requireNonNull(nameEdit.getText()).toString().length() <= 40) {
			if (getIntent().getBooleanExtra(Const.ADD_TRIP, false)) {
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra(Const.ADD_TRIP, true);

				nameLayout.setError(null);
				trip.setName(nameEdit.getText().toString());
				intent.putExtra(Const.TRIP_NAME, nameEdit.getText().toString());

				if (trip.getImageURI() != null) {
					intent.putExtra(Const.IMAGE_URI, trip.getImageURI());
				}

				if (Objects.requireNonNull(descriptionEdit.getText()).toString().length() <= 0) {
					descriptionEdit.setText("");
				}
				trip.setDescription(descriptionEdit.getText().toString());
				intent.putExtra(Const.TRIP_DESCRIPTION, trip.getDescription());

				startActivity(intent);
			} else if (getIntent().getBooleanExtra(Const.ADD_CITY, false)) {
				if (dateSelected) {
					Intent intent = new Intent(getApplicationContext(), CountryDetailsActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra(Const.ADD_CITY, true);

					nameLayout.setError(null);
					city.setName(nameEdit.getText().toString());
					intent.putExtra(Const.CITY_NAME, nameEdit.getText().toString());

					if (city.getImageURI() != null) {
						intent.putExtra(Const.IMAGE_URI, city.getImageURI());
					}

					if (Objects.requireNonNull(descriptionEdit.getText()).toString().length() <= 0) {
						descriptionEdit.setText("");
					}
					city.setDescription(descriptionEdit.getText().toString());
					intent.putExtra(Const.CITY_DESCRIPTION, city.getDescription());

					if (startDate != null && endDate != null) {
						intent.putExtra(Const.START_DATE, startDate);
						intent.putExtra(Const.END_DATE, endDate);
					}

					intent.putExtra(Const.COUNTRY, getIntent().getLongExtra(Const.COUNTRY, -1));

					startActivity(intent);
				} else {
					duration.requestFocus();
					duration.setError(getString(R.string.city_duration_error));
				}
			}
		} else {
			nameLayout.setError(getString(R.string.trip_name_error));
		}
	}
}