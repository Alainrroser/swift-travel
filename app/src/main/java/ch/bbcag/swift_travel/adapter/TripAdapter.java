package ch.bbcag.swift_travel.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.model.Trip;

public class TripAdapter extends ArrayAdapter<Trip> {

	public TripAdapter(Context context, List<Trip> trips) {
		super(context, 0, trips);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.three_line_list, parent, false);
		}

		// Get the data item for this position
		Trip trip = getItem(position);

		// Lookup view for data population
		TextView name = convertView.findViewById(R.id.name);
		TextView destination = convertView.findViewById(R.id.destination);
		TextView duration = convertView.findViewById(R.id.duration);
		ImageView imageURI = convertView.findViewById(R.id.trip_image);
		// Populate the data into the template view using the data object
		name.setText(trip.getName());
		destination.setText(trip.getDestination());
		duration.setText(trip.getDuration());
		imageURI.setImageURI(trip.getImageURI());
		// Return the completed view to render on screen
		return convertView;
	}
}