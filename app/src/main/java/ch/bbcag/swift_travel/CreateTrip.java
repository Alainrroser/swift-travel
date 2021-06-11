package ch.bbcag.swift_travel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.net.URI;
import java.util.Objects;

import ch.bbcag.swift_travel.model.Trip;

public class CreateTrip extends AppCompatActivity {
	private static final int PICK_IMAGE_REQUEST_CODE = 1;

	private Trip trip = new Trip();

	private TextInputLayout nameLayout;
	private TextInputLayout descriptionLayout;
	private TextInputEditText nameEdit;
	private TextInputEditText descriptionEdit;
	private ImageButton chooseImage;
	private Button create;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_trip);
		setTitle(getString(R.string.create_trip_title));

		nameLayout = findViewById(R.id.tripNameLayout);
		descriptionLayout = findViewById(R.id.tripDescriptionLayout);
		nameEdit = findViewById(R.id.tripNameEdit);
		descriptionEdit = findViewById(R.id.tripDescriptionEdit);
		create = findViewById(R.id.create);
		chooseImage = findViewById(R.id.placeHolderImage);

		onCreateClick();
		onChooseImageClick();

		setUpVisible();
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int itemId = item.getItemId();
		if(itemId == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setUpVisible(){
		ActionBar actionBar = getSupportActionBar();
		if(actionBar != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	private void onCreateClick() {
		create.setOnClickListener(v -> {
			startTripDetailsActivityOrShowError();
		});
	}

	private void onChooseImageClick() {
		chooseImage.setOnClickListener(v -> {
			ImagePicker.with(this).cropSquare().start();
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK && data != null) {
			Uri imageURI = data.getData();
			trip.setImageURI(imageURI);
			chooseImage.setImageURI(imageURI);
		}
	}

	private void startTripDetailsActivityOrShowError() {
		if (Objects.requireNonNull(nameEdit.getText()).toString().length() > 0) {
			Intent intent = new Intent(getApplicationContext(), TripDetailsActivity.class);

			nameLayout.setError(null);
			trip.setName(nameEdit.getText().toString());

			intent.putExtra("tripName", nameEdit.getText().toString());
			intent.putExtra("tripImageUri", trip.getImageURI());

			addDescriptionToTripAndIntentIfSet(intent);
			startActivity(intent);
		} else {
			nameLayout.setError(getString(R.string.trip_name_error));
		}
	}


	private void addDescriptionToTripAndIntentIfSet(Intent intent) {
		if (Objects.requireNonNull(descriptionEdit.getText()).toString().length() > 0) {
			trip.setDescription(descriptionEdit.getText().toString());
			intent.putExtra("tripDescription", descriptionEdit.getText().toString());
		}
	}
}