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
import ch.bbcag.swift_travel.model.Country;

public class CountryAdapter extends ArrayAdapter<Country> {

	public CountryAdapter(Context context, List<Country> countries) {
		super(context, 0, countries);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.two_line_list, parent, false);
		}

		// Get the data item for this position
		Country country = getItem(position);

		// Lookup view for data population
		TextView name = convertView.findViewById(R.id.country);
		TextView duration = convertView.findViewById(R.id.duration);
		ImageView imageURI = convertView.findViewById(R.id.two_line_image);

		// Populate the data into the template view using the data object
		name.setText(country.getName());
		duration.setText(country.getDuration());
		imageURI.setImageURI(country.getImageURI());

		// Return the completed view to render on screen
		return convertView;
	}
}