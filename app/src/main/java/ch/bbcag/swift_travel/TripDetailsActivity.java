package ch.bbcag.swift_travel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TripDetailsActivity extends UpButtonActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_details);

		Intent intent = getIntent();
		String name = intent.getStringExtra("tripName");
		setTitle(name);
	}

	@Override
	protected void onStart() {
		super.onStart();
		getProgressBar().setVisibility(View.GONE);
	}
}
