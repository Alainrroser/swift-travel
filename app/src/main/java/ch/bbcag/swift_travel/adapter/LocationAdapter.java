package ch.bbcag.swift_travel.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.activities.DayDetailsActivity;
import ch.bbcag.swift_travel.dal.SwiftTravelDatabase;
import ch.bbcag.swift_travel.entities.Image;
import ch.bbcag.swift_travel.entities.Location;
import ch.bbcag.swift_travel.utils.Const;
import ch.bbcag.swift_travel.utils.LayoutUtils;
import ch.bbcag.swift_travel.utils.OnlineDatabaseUtils;

public class LocationAdapter extends ArrayAdapter<Location> {
	private DayDetailsActivity dayDetailsActivity;

	public LocationAdapter(DayDetailsActivity dayDetailsActivity, List<Location> locations) {
		super(dayDetailsActivity, R.layout.two_line_list, locations);
		this.dayDetailsActivity = dayDetailsActivity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Location location = getItem(position);
		final LocationAdapterViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new LocationAdapterViewHolder();
			LayoutInflater inflater = LayoutInflater.from(dayDetailsActivity);
			convertView = inflater.inflate(R.layout.two_line_list, parent, false);

			viewHolder.name = convertView.findViewById(R.id.name_two_line_list);
			viewHolder.duration = convertView.findViewById(R.id.duration_or_date_two_line_list);
			viewHolder.image = convertView.findViewById(R.id.image_two_line_list);
			viewHolder.delete = convertView.findViewById(R.id.delete_two_line_list);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (LocationAdapterViewHolder) convertView.getTag();
		}

		addInformationToAdapter(viewHolder, location);
		return convertView;
	}

	private void addInformationToAdapter(LocationAdapterViewHolder viewHolder, Location location) {
		viewHolder.delete.setOnClickListener(v -> generateConfirmDialog(location));

		viewHolder.name.setText(location.getName());
		String duration = location.getDuration() + ", " + location.getStartTime() + "-" + location.getEndTime();
		viewHolder.duration.setText(duration);
		if (location.getImageURI() != null) {
			LayoutUtils.setImageURIOnImageView(viewHolder.image, location.getImageURI());
		} else {
			viewHolder.image.setImageResource(R.drawable.trip_placeholder);
		}
	}

	private void generateConfirmDialog(Location location) {
		dayDetailsActivity.generateConfirmDialog(dayDetailsActivity.getString(R.string.delete_entry_title), dayDetailsActivity.getString(R.string.delete_entry_text), () -> {
			remove(location);
			notifyDataSetChanged();
			deleteImages(location);
			SwiftTravelDatabase.getInstance(dayDetailsActivity.getApplicationContext()).getLocationDao().deleteById(location.getId());
			OnlineDatabaseUtils.delete(Const.LOCATIONS, location.getId(), dayDetailsActivity.isSaveOnline());
		});
	}

	private void deleteImages(Location location) {
		List<Image> images = SwiftTravelDatabase.getInstance(dayDetailsActivity.getApplicationContext()).getImageDao().getAllFromLocation(location.getId());
		for (Image image : images) {
			SwiftTravelDatabase.getInstance(dayDetailsActivity.getApplicationContext()).getImageDao().deleteById(image.getId());
			OnlineDatabaseUtils.delete(Const.IMAGES, image.getId(), dayDetailsActivity.isSaveOnline());
		}
	}

	public static class LocationAdapterViewHolder {
		TextView name;
		TextView duration;
		ImageView image;
		ImageButton delete;
	}
}