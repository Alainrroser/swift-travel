package ch.bbcag.swift_travel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import ch.bbcag.swift_travel.dal.TripDao;
import ch.bbcag.swift_travel.model.Trip;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.app_name));
        progressBar = findViewById(R.id.loading_trips_progress);
        addTripsToClickableList();
        progressBar.setVisibility(View.VISIBLE);
    }

    public void addTripsToClickableList(){
        ListView trips = findViewById(R.id.trips);
        final ArrayAdapter<Trip> tripAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1);
        tripAdapter.addAll(TripDao.getAll());
        trips.setAdapter(tripAdapter);

        AdapterView.OnItemClickListener mListClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), TripDetailsActivity.class);
                Trip selected = (Trip) parent.getItemAtPosition(position);
                intent.putExtra("tripId", selected.getId());
                intent.putExtra("tripName", selected.getName());
                startActivity(intent);
            }
        };
        trips.setOnItemClickListener(mListClickedHandler);

        progressBar.setVisibility(View.GONE);
    }
}