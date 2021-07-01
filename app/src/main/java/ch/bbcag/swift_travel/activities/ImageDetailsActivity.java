package ch.bbcag.swift_travel.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.utils.Const;

public class ImageDetailsActivity extends UpButtonActivity {
	private ImageView image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_details);

		image = findViewById(R.id.image_image_details);
	}

	@Override
	protected void onStart() {
		super.onStart();

		image.setImageURI(Uri.parse(getIntent().getStringExtra(Const.IMAGE_URI)));

		getProgressBar().setVisibility(View.GONE);
	}
}