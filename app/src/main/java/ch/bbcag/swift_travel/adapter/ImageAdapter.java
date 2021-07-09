package ch.bbcag.swift_travel.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.activities.LocationDetailsActivity;
import ch.bbcag.swift_travel.dal.SwiftTravelDatabase;
import ch.bbcag.swift_travel.entities.Image;
import ch.bbcag.swift_travel.utils.Const;
import ch.bbcag.swift_travel.utils.LayoutUtils;
import ch.bbcag.swift_travel.utils.OnlineDatabaseUtils;

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

		if(FirebaseAuth.getInstance().getCurrentUser() != null) {
			if (image.getImageCDL() != null) {
				OnlineDatabaseUtils.setOnlineImageOnImageView(viewHolder.image, image.getImageCDL());
			} else if (image.getImageURI() != null && image.getImageCDL() == null) {
				image.setImageCDL(OnlineDatabaseUtils.uploadImage(Uri.parse(image.getImageURI())));
				OnlineDatabaseUtils.setOnlineImageOnImageView(viewHolder.image, image.getImageCDL());
			}
		} else {
			if (image.getImageURI() != null) {
				LayoutUtils.setImageURIOnImageView(viewHolder.image, image.getImageURI());
			}
		}
	}

	private void generateConfirmDialog(Image image) {
		locationDetailsActivity.generateConfirmDialog(locationDetailsActivity.getString(R.string.delete_entry_title), locationDetailsActivity.getString(R.string.delete_entry_text), () -> {
			remove(image);
			notifyDataSetChanged();
			OnlineDatabaseUtils.delete(Const.IMAGES, image.getId());
			SwiftTravelDatabase.getInstance(locationDetailsActivity.getApplicationContext()).getImageDao().deleteById(image.getId());
			locationDetailsActivity.refreshContent();
		});
	}

	public static class ImageAdapterViewHolder {
		ImageView image;
		ImageButton delete;
	}
}
