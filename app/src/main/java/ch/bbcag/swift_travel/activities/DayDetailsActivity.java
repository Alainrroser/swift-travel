package ch.bbcag.swift_travel.activities;

import android.os.Bundle;
import android.view.View;

import ch.bbcag.swift_travel.R;

public class DayDetailsActivity extends UpButtonActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city_details);
	}

	@Override
	protected void onStart() {
		super.onStart();
		getProgressBar().setVisibility(View.GONE);
	}
}
