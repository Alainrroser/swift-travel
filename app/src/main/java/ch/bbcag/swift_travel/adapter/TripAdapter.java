package ch.bbcag.swift_travel.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.activities.MainActivity;
import ch.bbcag.swift_travel.dal.SwiftTravelDatabase;
import ch.bbcag.swift_travel.entities.Trip;
import ch.bbcag.swift_travel.utils.LayoutUtils;

public class TripAdapter extends ArrayAdapter<Trip> {
	private MainActivity mainActivity;

	public static class TripViewHolder {
		TextView name;
		TextView destination;
		TextView duration;
		ImageView image;
		ImageButton delete;
	}

	public TripAdapter(MainActivity mainActivity, List<Trip> trips) {
		super(mainActivity, R.layout.three_line_list, trips);
		this.mainActivity = mainActivity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Trip trip = getItem(position);
		final TripViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new TripViewHolder();
			LayoutInflater inflater = LayoutInflater.from(mainActivity);
			convertView = inflater.inflate(R.layout.three_line_list, parent, false);

			viewHolder.name = convertView.findViewById(R.id.name_three_line_list);
			viewHolder.destination = convertView.findViewById(R.id.destination_three_line_list);
			viewHolder.duration = convertView.findViewById(R.id.duration_three_line_list);
			viewHolder.image = convertView.findViewById(R.id.image_three_line_list);
			viewHolder.delete = convertView.findViewById(R.id.delete_three_line_list);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (TripViewHolder) convertView.getTag();
		}

		viewHolder.delete.setOnClickListener(v -> generateConfirmDialog(trip));

		viewHolder.name.setText(trip.getName());
		viewHolder.destination.setText(trip.getDestination());
		String duration = trip.getDuration() + " " + mainActivity.getString(R.string.days_title);
		viewHolder.duration.setText(duration);
		if (trip.getImageURI() != null) {
			LayoutUtils.setImageURIOnImageView(viewHolder.image, trip.getImageURI());
		}
		return convertView;
	}

	private void generateConfirmDialog(Trip trip) {
		MainActivity mainActivity = (MainActivity) getContext();
		mainActivity.generateConfirmDialog(mainActivity.getString(R.string.delete_entry_title), mainActivity.getString(R.string.delete_entry_text), () -> {
			remove(trip);
			notifyDataSetChanged();
			SwiftTravelDatabase.getInstance(mainActivity.getApplicationContext()).getTripDao().delete(trip.getId());
		});
	}
}