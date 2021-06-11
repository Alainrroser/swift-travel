package ch.bbcag.swift_travel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import ch.bbcag.swift_travel.adapter.Three_line_list;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ch.bbcag.swift_travel.dal.TripDao;
import ch.bbcag.swift_travel.model.Trip;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    FloatingActionButton floatingActionButton;
    Three_line_list adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.app_name));

        progressBar = findViewById(R.id.loading_trips_progress);
        floatingActionButton = findViewById(R.id.floating_action_button);

        List<Trip> trips = TripDao.getAll();
        adapter = new Three_line_list(this, trips);
        addTripsToClickableList();

        progressBar.setVisibility(View.VISIBLE);
        onFloatingActionButtonClick();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void addTripsToClickableList() {
        ListView listView = findViewById(R.id.trips);
        listView.setAdapter(adapter);

        progressBar.setVisibility(View.GONE);

        AdapterView.OnItemClickListener mListClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), TripDetailsActivity.class);
                Trip selected = (Trip) parent.getItemAtPosition(position);
                intent.putExtra("tripId", selected.getId());
                intent.putExtra("tripName", selected.getName());
                startActivity(intent);
            }
        };

        listView.setOnItemClickListener(mListClickedHandler);
    }

    public void addNewTrip(Trip trip){
        adapter.add(trip);
    }

    private void onFloatingActionButtonClick() {
        floatingActionButton.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), CreateTrip.class)));
    }
}