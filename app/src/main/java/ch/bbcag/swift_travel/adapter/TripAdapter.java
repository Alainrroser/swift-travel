package ch.bbcag.swift_travel.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.activities.MainActivity;
import ch.bbcag.swift_travel.activities.TripDetailsActivity;
import ch.bbcag.swift_travel.model.Country;
import ch.bbcag.swift_travel.model.Trip;

public class TripAdapter extends ArrayAdapter<Trip> {
	public static class TripViewHolder {
		TextView name;
		TextView destination;
		TextView duration;
		ImageView image;
		ImageButton delete;
	}

	public TripAdapter(Context context, List<Trip> trips) {
		super(context, R.layout.activity_main, trips);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Trip trip = getItem(position);
		final TripViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new TripViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
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

		viewHolder.delete.setOnClickListener(v -> {
			MainActivity mainActivity = (MainActivity) getContext();
			mainActivity.generateConfirmDialog(mainActivity.getString(R.string.delete_entry_title), mainActivity.getString(R.string.delete_entry_text), new Runnable() {
				@Override
				public void run() {
					mainActivity.getAdapter().remove(trip);
					mainActivity.getAdapter().notifyDataSetChanged();
				}
			});
		});


		viewHolder.name.setText(trip.getName());
		viewHolder.destination.setText(trip.getDestination());
		viewHolder.duration.setText(trip.getDuration());
		viewHolder.image.setImageURI(trip.getImageURI());
		return convertView;
	}
}