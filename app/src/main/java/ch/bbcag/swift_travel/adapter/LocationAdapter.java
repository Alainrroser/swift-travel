package ch.bbcag.swift_travel.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.activities.DayDetailsActivity;
import ch.bbcag.swift_travel.dal.SwiftTravelDatabase;
import ch.bbcag.swift_travel.entities.Image;
import ch.bbcag.swift_travel.entities.Location;
import ch.bbcag.swift_travel.utils.Const;
import ch.bbcag.swift_travel.utils.LayoutUtils;
import ch.bbcag.swift_travel.utils.OnlineDatabaseUtils;

import static ch.bbcag.swift_travel.R.drawable.category_hotel;
import static ch.bbcag.swift_travel.R.drawable.category_location;
import static ch.bbcag.swift_travel.R.drawable.category_restaurant;
import static ch.bbcag.swift_travel.R.drawable.placeholder_icon;
import static ch.bbcag.swift_travel.R.drawable.timeline_bottom;
import static ch.bbcag.swift_travel.R.drawable.timeline_middle;
import static ch.bbcag.swift_travel.R.drawable.timeline_single;
import static ch.bbcag.swift_travel.R.drawable.timeline_top;

public class LocationAdapter extends ArrayAdapter<Location> {
	private DayDetailsActivity dayDetailsActivity;
	private List<Location> locations;

	public LocationAdapter(DayDetailsActivity dayDetailsActivity, List<Location> locations) {
		super(dayDetailsActivity, R.layout.timeline_list, locations);
		this.dayDetailsActivity = dayDetailsActivity;
		this.locations = locations;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Location location = getItem(position);
		final LocationAdapterViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new LocationAdapterViewHolder();
			LayoutInflater inflater = LayoutInflater.from(dayDetailsActivity);
			convertView = inflater.inflate(R.layout.timeline_list, parent, false);
			viewHolder.itemLine = convertView.findViewById(R.id.item_line);
			viewHolder.name = convertView.findViewById(R.id.name_timeline_list);
			viewHolder.duration = convertView.findViewById(R.id.duration_or_date_timeline_list);
			viewHolder.image = convertView.findViewById(R.id.image_timeline_list);
			viewHolder.delete = convertView.findViewById(R.id.delete_timeline_list);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (LocationAdapterViewHolder) convertView.getTag();
		}

		addInformationToAdapter(viewHolder, location);
		return convertView;
	}

	private void addInformationToAdapter(LocationAdapterViewHolder viewHolder, Location location) {
		setItemLineBackground(viewHolder, location);
		viewHolder.delete.setOnClickListener(v -> generateConfirmDialog(location));

		viewHolder.name.setText(location.getName());
		String duration = location.getDuration() + ", " + location.getStartTime() + "-" + location.getEndTime();
		viewHolder.duration.setText(duration);
		if (location.getImageURI() != null) {
			LayoutUtils.setImageURIOnImageView(viewHolder.image, location.getImageURI());
		} else {
			setImage(viewHolder, location);
		}
	}

	private void setImage(LocationAdapterViewHolder viewHolder, Location location) {
		switch (location.getCategory()) {
			case Const.CATEGORY_HOTEL:
				viewHolder.image.setImageResource(category_hotel);
				break;
			case Const.CATEGORY_RESTAURANT:
				viewHolder.image.setImageResource(category_restaurant);
				break;
			case Const.CATEGORY_PLACE:
				viewHolder.image.setImageResource(category_location);
				break;
			default:
				viewHolder.image.setImageResource(placeholder_icon);
				break;
		}
	}

	@SuppressLint("UseCompatLoadingForDrawables")
	private void setItemLineBackground(LocationAdapterViewHolder viewHolder, Location location) {
		switch (getItemViewType(location)) {
			case Const.VIEW_TYPE_TOP:
				viewHolder.itemLine.setBackground(dayDetailsActivity.getDrawable(timeline_top));
				break;
			case Const.VIEW_TYPE_MIDDLE:
				viewHolder.itemLine.setBackground(dayDetailsActivity.getDrawable(timeline_middle));
				break;
			case Const.VIEW_TYPE_BOTTOM:
				viewHolder.itemLine.setBackground(dayDetailsActivity.getDrawable(timeline_bottom));
				break;
			case Const.VIEW_TYPE_SINGLE:
				viewHolder.itemLine.setBackground(dayDetailsActivity.getDrawable(timeline_single));
				break;
		}
	}

	private void generateConfirmDialog(Location location) {
		dayDetailsActivity.generateConfirmDialog(dayDetailsActivity.getString(R.string.delete_entry_title), dayDetailsActivity.getString(R.string.delete_entry_text), () -> {
			remove(location);
			notifyDataSetChanged();
			deleteImages(location);
			OnlineDatabaseUtils.delete(Const.LOCATIONS, location.getId());
			SwiftTravelDatabase.getInstance(dayDetailsActivity.getApplicationContext()).getLocationDao().deleteById(location.getId());
			dayDetailsActivity.refreshContent();
		});
	}

	private void deleteImages(Location location) {
		List<Image> images = SwiftTravelDatabase.getInstance(dayDetailsActivity.getApplicationContext()).getImageDao().getAllFromLocation(location.getId());
		for (Image image : images) {
			OnlineDatabaseUtils.delete(Const.IMAGES, image.getId());
			SwiftTravelDatabase.getInstance(dayDetailsActivity.getApplicationContext()).getImageDao().deleteById(image.getId());
		}
	}

	public int getItemViewType(Location location) {
		if (locations.indexOf(location) == 0 && !(locations.size() <= 1)) {
			return Const.VIEW_TYPE_TOP;
		} else if (locations.indexOf(location) == locations.size() - 1 && !(locations.size() <= 1)) {
			return Const.VIEW_TYPE_BOTTOM;
		} else if (locations.size() <= 1) {
			return Const.VIEW_TYPE_SINGLE;
		}
		return Const.VIEW_TYPE_MIDDLE;
	}

	public static class LocationAdapterViewHolder {
		TextView name;
		TextView duration;
		ImageView image;
		ImageButton delete;
		FrameLayout itemLine;
	}
}