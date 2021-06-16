package ch.bbcag.swift_travel.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import ch.bbcag.swift_travel.utils.Const;
import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.adapter.CountryAdapter;
import ch.bbcag.swift_travel.dal.TripDao;
import ch.bbcag.swift_travel.model.Country;
import ch.bbcag.swift_travel.model.Trip;

public class TripDetailsActivity extends UpButtonActivity {
	private FloatingActionButton floatingActionButton;
	private CountryAdapter adapter;

	private String tripName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_details);

		Intent intent = getIntent();
		tripName = intent.getStringExtra(Const.TRIP_NAME);
		setTitle(tripName);

		floatingActionButton = findViewById(R.id.floating_action_button_countries);
	}

	@Override
	protected void onStart() {
		super.onStart();
		getProgressBar().setVisibility(View.GONE);

		Intent intent = getIntent();
		Trip selected = new Trip();
		if(intent.getStringExtra(Const.TRIP_DESCRIPTION) != null) {
			selected.setDescription(intent.getStringExtra(Const.TRIP_DESCRIPTION));
		}
		List<Trip> trips = TripDao.getAll();
		for (Trip trip : trips) {
			if (trip.getName().equals(intent.getStringExtra(Const.TRIP_NAME))) {
				selected = trip;
			}
		}

		TextView titleView = findViewById(R.id.trip_title);
		titleView.setText(selected.getName());

		TextView descriptionView = findViewById(R.id.trip_description);
		if (selected.getDescription() == null) {
			//TODO
			descriptionView.setText(R.string.add_description);
		} else {
			descriptionView.setText(selected.getDescription());
		}
		descriptionView.setMovementMethod(new ScrollingMovementMethod());

		List<Country> countries = selected.getCountries();
		adapter = new CountryAdapter(this, countries);
		createCountryFromIntent();
		addTripsToClickableList();
		onFloatingActionButtonClick();
	}

	/**
	 * Creates a trip from the information in the intent if they aren't null.
	 */
	private void createCountryFromIntent() {
		Intent intent = getIntent();
		Country country = new Country();
		addTripInformation(intent, country);
	}

	private void addTripInformation(Intent intent, Country country) {
		if (intent.getStringExtra(Const.COUNTRY_NAME) != null) {
			country.setName(intent.getStringExtra(Const.COUNTRY_NAME));
			country.setImageURI(Uri.parse(intent.getStringExtra(Const.FLAG_URI)));
			adapter.add(country);
		}
	}

	public void addTripsToClickableList() {
		ListView listView = findViewById(R.id.countries);
		listView.setAdapter(adapter);

		getProgressBar().setVisibility(View.GONE);

		AdapterView.OnItemClickListener mListClickedHandler = (parent, v, position, id) -> {
			Intent intent = new Intent(getApplicationContext(), CityDetailsActivity.class);
			Country selected = (Country) parent.getItemAtPosition(position);
			intent.putExtra(Const.COUNTRY_NAME, selected.getName());
			startActivity(intent);
		};

		listView.setOnItemClickListener(mListClickedHandler);
	}

	private void onFloatingActionButtonClick() {
		floatingActionButton.setOnClickListener(v -> {
			Intent intent = new Intent(getApplicationContext(), ChooseActivity.class);
			intent.putExtra(Const.NAME, Const.COUNTRY);
			intent.putExtra(Const.TRIP_NAME, tripName);
			startActivity(intent);
		});
	}
}