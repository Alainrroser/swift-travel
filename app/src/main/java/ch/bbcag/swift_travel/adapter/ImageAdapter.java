package ch.bbcag.swift_travel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.entities.Image;
import ch.bbcag.swift_travel.utils.LayoutUtils;

public class ImageAdapter extends ArrayAdapter<Image> {
	public ImageAdapter(Context context, List<Image> images) {
		super(context, R.layout.image_list, images);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Image image = getItem(position);
		final ImageAdapterViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new ImageAdapterViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.image_list, parent, false);

			viewHolder.image = convertView.findViewById(R.id.image);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ImageAdapterViewHolder) convertView.getTag();
		}

		if (image.getImageURI() != null) {
			LayoutUtils.setImageURIOnImageView(viewHolder.image, image.getImageURI());
		}
		return convertView;
	}


	public static class ImageAdapterViewHolder {
		ImageView image;
	}
}
