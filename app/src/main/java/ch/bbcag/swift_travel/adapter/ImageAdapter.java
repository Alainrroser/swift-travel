package ch.bbcag.swift_travel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.activities.LocationDetailsActivity;
import ch.bbcag.swift_travel.dal.SwiftTravelDatabase;
import ch.bbcag.swift_travel.entities.Image;
import ch.bbcag.swift_travel.entities.Location;
import ch.bbcag.swift_travel.utils.LayoutUtils;

public class ImageAdapter extends ArrayAdapter<Image> {
	private LocationDetailsActivity locationDetailsActivity;

	public ImageAdapter(LocationDetailsActivity locationDetailsActivity, List<Image> images) {
		super(locationDetailsActivity, R.layout.image_list, images);
		this.locationDetailsActivity = locationDetailsActivity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Image image = getItem(position);
		final ImageAdapterViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new ImageAdapterViewHolder();
			LayoutInflater inflater = LayoutInflater.from(locationDetailsActivity);
			convertView = inflater.inflate(R.layout.image_list, parent, false);

			viewHolder.image = convertView.findViewById(R.id.image_image_list);
			viewHolder.delete = convertView.findViewById(R.id.delete_image_list);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ImageAdapterViewHolder) convertView.getTag();
		}

		addInformationToAdapter(viewHolder, image);
		return convertView;
	}

	private void addInformationToAdapter(ImageAdapterViewHolder viewHolder, Image image) {
		viewHolder.delete.setOnClickListener(v -> generateConfirmDialog(image));

		if (image.getImageURI() != null) {
			LayoutUtils.setImageURIOnImageView(viewHolder.image, image.getImageURI());
		}
	}

	private void generateConfirmDialog(Image image) {
		locationDetailsActivity.generateConfirmDialog(locationDetailsActivity.getString(R.string.delete_entry_title), locationDetailsActivity.getString(R.string.delete_entry_text), () -> {
			remove(image);
			notifyDataSetChanged();
			SwiftTravelDatabase.getInstance(locationDetailsActivity.getApplicationContext()).getImageDao().deleteById(image.getId());
		});
	}

	public static class ImageAdapterViewHolder {
		ImageView image;
		ImageButton delete;
	}
}
