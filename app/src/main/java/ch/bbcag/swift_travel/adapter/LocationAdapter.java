package ch.bbcag.swift_travel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.activities.DayDetailsActivity;
import ch.bbcag.swift_travel.entities.Location;
import ch.bbcag.swift_travel.utils.LayoutUtils;

public class LocationAdapter extends ArrayAdapter<Location> {
	public static class LocationAdapterViewHolder {
		TextView name;
		TextView duration;
		ImageView image;
		ImageButton delete;
	}

	public LocationAdapter(Context context, List<Location> locations) {
		super(context, R.layout.three_line_list, locations);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Location location = getItem(position);
		final LocationAdapterViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new LocationAdapterViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.three_line_list, parent, false);

			viewHolder.name = convertView.findViewById(R.id.name_two_line_list);
			viewHolder.duration = convertView.findViewById(R.id.duration_two_line_list);
			viewHolder.image = convertView.findViewById(R.id.image_two_line_list);
			viewHolder.delete = convertView.findViewById(R.id.delete_two_line_list);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (LocationAdapterViewHolder) convertView.getTag();
		}

		viewHolder.delete.setOnClickListener(v -> generateConfirmDialog(location));

		viewHolder.name.setText(location.getName());
		viewHolder.duration.setText(location.getDuration());
		LayoutUtils.setOnlineImageURIOnImageView(getContext(), viewHolder.image, location.getImageURI());
		return convertView;
	}

	private void generateConfirmDialog(Location location) {
		DayDetailsActivity dayDetailsActivity = (DayDetailsActivity) getContext();
		dayDetailsActivity.generateConfirmDialog(dayDetailsActivity.getString(R.string.delete_entry_title), dayDetailsActivity.getString(R.string.delete_entry_text), () -> {
			dayDetailsActivity.getAdapter().remove(location);
			dayDetailsActivity.getAdapter().notifyDataSetChanged();
			dayDetailsActivity.getLocationDao().delete(location.getId());
		});
	}
}