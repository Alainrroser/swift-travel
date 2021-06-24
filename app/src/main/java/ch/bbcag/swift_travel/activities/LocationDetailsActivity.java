package ch.bbcag.swift_travel.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.adapter.DayAdapter;
import ch.bbcag.swift_travel.dal.LocationDao;
import ch.bbcag.swift_travel.dal.SwiftTravelDatabase;
import ch.bbcag.swift_travel.entities.City;
import ch.bbcag.swift_travel.entities.Day;
import ch.bbcag.swift_travel.entities.Location;
import ch.bbcag.swift_travel.utils.Const;
import ch.bbcag.swift_travel.utils.LayoutUtils;

public class LocationDetailsActivity extends UpButtonActivity {
	private ImageButton editDescriptionButton;
	private Button submitButton;

	private TextView titleText;
	private TextView durationText;
	private TextView descriptionText;
	private EditText editTitle;
	private EditText editDescription;
	private ImageView locationImage;

	private Location selected;

	private LocationDao locationDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_details);

		Intent intent = getIntent();
		String name = intent.getStringExtra(Const.LOCATION_NAME);
		setTitle(name);

		locationDao = SwiftTravelDatabase.getInstance(getApplicationContext()).getLocationDao();

		titleText = findViewById(R.id.location_title);
		durationText = findViewById(R.id.location_duration);
		descriptionText = findViewById(R.id.location_description);
		editTitle = findViewById(R.id.edit_title);
		editDescription = findViewById(R.id.edit_description);
		locationImage = findViewById(R.id.location_image);

		submitButton = findViewById(R.id.location_submit_button);
		editDescriptionButton = findViewById(R.id.edit_button);
	}

	@Override
	protected void onStart() {
		super.onStart();

		long id = getIntent().getLongExtra(Const.LOCATION, -1);
		if (id != -1) {
			selected = locationDao.getById(id);
		}

		editTitle.setText(selected.getName());
		editDescription.setText(selected.getDescription());

		refreshContent();

		Group form = findViewById(R.id.location_form);
		form.setVisibility(View.GONE);

		getProgressBar().setVisibility(View.GONE);

		editDescriptionButton.setOnClickListener(v -> toggleForm());
		onSubmitButtonClick();
	}

	private void onSubmitButtonClick() {
		submitButton.setOnClickListener(v -> {
			editDescription();
			refreshContent();
			toggleForm();
		});
	}

	private void refreshContent() {
		LayoutUtils.setEditableTitleText(titleText, editTitle, selected.getName());
		LayoutUtils.setEditableDescriptionText(descriptionText, editDescription, selected.getDescription());
		LayoutUtils.setTextOnTextView(durationText, selected.getDuration() + " " + getString(R.string.days_title));
		if (selected.getImageURI() != null && !selected.getImageURI().isEmpty()) {
			LayoutUtils.setImageURIOnImageView(locationImage, selected.getImageURI());
		}

		selected.setDescription(descriptionText.getText().toString());
		selected.setName(titleText.getText().toString());

		setTitle(selected.getName());

		locationDao.update(selected);
	}

	private void editDescription() {
		if (editDescription.getText() != null && !editDescription.getText().toString().isEmpty()) {
			selected.setDescription(editDescription.getText().toString());
			locationDao.update(selected);
		}
	}

	private void toggleForm() {
		Group form = findViewById(R.id.location_form);
		Group content = findViewById(R.id.location_content);

		if (form.getVisibility() == View.VISIBLE) {
			form.setVisibility(View.GONE);
			content.setVisibility(View.VISIBLE);
		} else {
			form.setVisibility(View.VISIBLE);
			content.setVisibility(View.GONE);
		}
	}
}