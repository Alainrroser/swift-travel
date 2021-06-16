package ch.bbcag.swift_travel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ch.bbcag.swift_travel.utils.Const;
import ch.bbcag.swift_travel.R;

public class CityDetailsActivity extends UpButtonActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_country_details);

		Intent intent = getIntent();
		String name = intent.getStringExtra(Const.CITY_NAME);
		setTitle(name);
	}

	@Override
	protected void onStart() {
		super.onStart();
		getProgressBar().setVisibility(View.GONE);
	}
}
