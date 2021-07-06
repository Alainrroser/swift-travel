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

import static ch.bbcag.swift_travel.R.drawable.*;

public class LocationAdapter extends ArrayAdapter<Location> {
	private DayDetailsActivity dayDetailsActivity;
	private static final int VIEW_TYPE_TOP = 0;
	private static final int VIEW_TYPE_MIDDLE = 1;
	private static final int VIEW_TYPE_BOTTOM = 2;
	private FrameLayout itemLine;
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
			itemLine = convertView.findViewById(R.id.item_line);
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

	@SuppressLint("UseCompatLoadingForDrawables")
	private void addInformationToAdapter(LocationAdapterViewHolder viewHolder, Location location) {
		switch(getItemViewType(location)) {
			case VIEW_TYPE_TOP:
				itemLine.setBackground(dayDetailsActivity.getDrawable(line_bg_top));
				break;
			case VIEW_TYPE_MIDDLE:
				itemLine.setBackground(dayDetailsActivity.getDrawable(line_bg_middle));
				break;
			case VIEW_TYPE_BOTTOM:
				itemLine.setBackground(dayDetailsActivity.getDrawable(line_bg_bottom));
				break;
		}
		viewHolder.delete.setOnClickListener(v -> generateConfirmDialog(location));

		viewHolder.name.setText(location.getName());
		String duration = location.getDuration() + ", " + location.getStartTime() + "-" + location.getEndTime();
		viewHolder.duration.setText(duration);
		if (location.getImageURI() != null) {
			LayoutUtils.setImageURIOnImageView(viewHolder.image, location.getImageURI());
		} else {
			System.out.println(location.getCategory());
			switch(location.getCategory()){
				case Const.CATEGORY_HOTEL:
					viewHolder.image.setImageResource(ic_baseline_hotel_24);
					break;
				case Const.CATEGORY_RESTAURANT:
					viewHolder.image.setImageResource(ic_baseline_restaurant_24);
					break;
				case Const.CATEGORY_PLACE:
					viewHolder.image.setImageResource(ic_baseline_location_on_24);
					break;
				default:
					viewHolder.image.setImageResource(trip_placeholder);
					break;
			}
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

	public int getItemViewType(Location location) {
		if (locations.indexOf(location) == 0) {
			return VIEW_TYPE_TOP;
		} else if (locations.indexOf(location) == locations.size() - 1) {
			return VIEW_TYPE_BOTTOM;
		}
		return VIEW_TYPE_MIDDLE;
	}
}