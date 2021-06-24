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
import ch.bbcag.swift_travel.activities.TripDetailsActivity;
import ch.bbcag.swift_travel.dal.SwiftTravelDatabase;
import ch.bbcag.swift_travel.entities.Country;
import ch.bbcag.swift_travel.utils.LayoutUtils;

public class CountryAdapter extends ArrayAdapter<Country> {
	private TripDetailsActivity tripDetailsActivity;

	public static class CountryAdapterViewHolder {
		TextView name;
		TextView duration;
		ImageView image;
		ImageButton delete;
	}

	public CountryAdapter(TripDetailsActivity tripDetailsActivity, List<Country> countries) {
		super(tripDetailsActivity, R.layout.two_line_list, countries);
		this.tripDetailsActivity = tripDetailsActivity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Country country = getItem(position);
		final CountryAdapterViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new CountryAdapterViewHolder();
			LayoutInflater inflater = LayoutInflater.from(tripDetailsActivity);
			convertView = inflater.inflate(R.layout.two_line_list, parent, false);

			viewHolder.name = convertView.findViewById(R.id.name_two_line_list);
			viewHolder.duration = convertView.findViewById(R.id.duration_or_date_two_line_list);
			viewHolder.image = convertView.findViewById(R.id.image_two_line_list);
			viewHolder.delete = convertView.findViewById(R.id.delete_two_line_list);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (CountryAdapterViewHolder) convertView.getTag();
		}

		viewHolder.delete.setOnClickListener(v -> generateConfirmDialog(country));

		viewHolder.name.setText(country.getName());
		String duration;
		if (country.getDuration() == 1) {
			duration = country.getDuration() + " " + tripDetailsActivity.getString(R.string.day);
		} else {
			duration = country.getDuration() + " " + tripDetailsActivity.getString(R.string.days_title);
		}
		viewHolder.duration.setText(duration);
		LayoutUtils.setOnlineImageURIOnImageView(getContext(), viewHolder.image, country.getImageURI());
		return convertView;
	}

	private void generateConfirmDialog(Country country) {
		tripDetailsActivity.generateConfirmDialog(tripDetailsActivity.getString(R.string.delete_entry_title), tripDetailsActivity.getString(R.string.delete_entry_text), () -> {
			remove(country);
			notifyDataSetChanged();
			SwiftTravelDatabase.getInstance(tripDetailsActivity.getApplicationContext()).getCountryDao().delete(country.getId());
		});
	}
}