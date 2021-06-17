package ch.bbcag.swift_travel.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.entities.Trip;
import ch.bbcag.swift_travel.utils.Const;

public class CreateTripActivity extends UpButtonActivity {
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        getProgressBar().setVisibility(View.GONE);
        nameLayout = findViewById(R.id.edit_title_layout);
        descriptionLayout = findViewById(R.id.edit_description_layout);
        nameEdit = findViewById(R.id.edit_title);
        descriptionEdit = findViewById(R.id.edit_description);
        create = findViewById(R.id.create);
        chooseImage = findViewById(R.id.place_holder_image);

        onCreateClick();
        onChooseImageClick();
    }

    private void onCreateClick() {
        create.setOnClickListener(v -> startMainOrShowError());
    }

    private void onChooseImageClick() {
        chooseImage.setOnClickListener(v -> ImagePicker.with(this).cropSquare().start());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri imageURI = data.getData();
            trip.setImageURI(imageURI.toString());
            chooseImage.setImageURI(imageURI);
        }
    }

    private void startMainOrShowError() {
        if (Objects.requireNonNull(nameEdit.getText()).toString().length() > 0) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            nameLayout.setError(null);
            trip.setName(nameEdit.getText().toString());

            intent.putExtra(Const.TRIP_NAME, nameEdit.getText().toString());
            if (trip.getImageURI() != null) {
                intent.putExtra(Const.TRIP_IMAGE_URI, trip.getImageURI());
            }
            addDescriptionToTripAndIntentIfSet(intent);

            startActivity(intent);
        } else {
            nameLayout.setError(getString(R.string.trip_name_error));
        }
    }


    private void addDescriptionToTripAndIntentIfSet(Intent intent) {
        if (Objects.requireNonNull(descriptionEdit.getText()).toString().length() <= 0) {
            descriptionEdit.setText("");
        }

        trip.setDescription(descriptionEdit.getText().toString());
        intent.putExtra(Const.TRIP_DESCRIPTION, descriptionEdit.getText().toString());
    }
}