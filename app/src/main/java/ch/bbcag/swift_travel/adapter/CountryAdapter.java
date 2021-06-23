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
import ch.bbcag.swift_travel.activities.TripDetailsActivity;
import ch.bbcag.swift_travel.entities.Country;
import ch.bbcag.swift_travel.utils.LayoutUtils;

public class CountryAdapter extends ArrayAdapter<Country> {
	public static class CountryAdapterViewHolder {
		TextView name;
		TextView duration;
		ImageView image;
		ImageButton delete;
	}

	public CountryAdapter(Context context, List<Country> countries) {
		super(context, R.layout.two_line_list, countries);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Country country = getItem(position);
		final CountryAdapterViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new CountryAdapterViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.two_line_list, parent, false);

			viewHolder.name = convertView.findViewById(R.id.name_two_line_list);
			viewHolder.duration = convertView.findViewById(R.id.duration_two_line_list);
			viewHolder.image = convertView.findViewById(R.id.image_two_line_list);
			viewHolder.delete = convertView.findViewById(R.id.delete_two_line_list);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (CountryAdapterViewHolder) convertView.getTag();
		}

		viewHolder.delete.setOnClickListener(v -> generateConfirmDialog(country));

		viewHolder.name.setText(country.getName());
		viewHolder.duration.setText(country.getDuration());
		LayoutUtils.setOnlineImageURIOnImageView(getContext(), viewHolder.image, country.getImageURI());
		return convertView;
	}

	private void generateConfirmDialog(Country country) {
		TripDetailsActivity tripDetailsActivity = (TripDetailsActivity) getContext();
		tripDetailsActivity.generateConfirmDialog(tripDetailsActivity.getString(R.string.delete_entry_title), tripDetailsActivity.getString(R.string.delete_entry_text), () -> {
			tripDetailsActivity.getAdapter().remove(country);
			tripDetailsActivity.getAdapter().notifyDataSetChanged();
			tripDetailsActivity.getCountryDao().delete(country.getId());
		});
	}
}