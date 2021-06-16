package ch.bbcag.swift_travel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import ch.bbcag.swift_travel.CreateTrip;
import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.adapter.CountryAdapter;
import ch.bbcag.swift_travel.adapter.TripAdapter;
import ch.bbcag.swift_travel.dal.TripDao;
import ch.bbcag.swift_travel.model.Country;
import ch.bbcag.swift_travel.model.Trip;

public class TripDetailsActivity extends UpButtonActivity {
	private FloatingActionButton floatingActionButton;
	private CountryAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_details);

		Intent intent = getIntent();
		String name = intent.getStringExtra("countryName");
		setTitle(name);
	}

	@Override
	protected void onStart() {
		super.onStart();
		getProgressBar().setVisibility(View.GONE);
		Intent intent = getIntent();
		List<Trip> trips = TripDao.getAll();
		Trip trip = trips.get(0);
		List<Country> countries = trip.getCountries();
		adapter = new CountryAdapter(this, countries);
		addTripsToClickableList();
//		onFloatingActionButtonClick();
	}

	public void addTripsToClickableList() {
		ListView listView = findViewById(R.id.countries);
		listView.setAdapter(adapter);

		getProgressBar().setVisibility(View.GONE);

		AdapterView.OnItemClickListener mListClickedHandler = (parent, v, position, id) -> {
			Intent intent = new Intent(getApplicationContext(), CityDetailsActivity.class);
			Country selected = (Country) parent.getItemAtPosition(position);
			intent.putExtra("countryName", selected.getName());
			startActivity(intent);
		};

		listView.setOnItemClickListener(mListClickedHandler);
	}

//	private void onFloatingActionButtonClick() {
//		floatingActionButton.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), CreateTrip.class)));
//	}
}
