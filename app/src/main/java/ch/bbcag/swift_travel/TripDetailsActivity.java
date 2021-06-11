package ch.bbcag.swift_travel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class TripDetailsActivity extends AppCompatActivity {
    private int tripId;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        progressBar = findViewById(R.id.loading_trip_details_progress);
        Intent intent = getIntent();
        tripId = intent.getIntExtra("tripId", 0);
        String name = intent.getStringExtra("tripName");
        setTitle(name);
        progressBar.setVisibility(View.VISIBLE);
    }
}
