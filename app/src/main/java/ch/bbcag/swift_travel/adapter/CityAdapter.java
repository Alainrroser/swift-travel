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
import ch.bbcag.swift_travel.activities.CountryDetailsActivity;
import ch.bbcag.swift_travel.entities.City;
import ch.bbcag.swift_travel.utils.Layout;

public class CityAdapter extends ArrayAdapter<City> {
	public static class CityAdapterViewHolder {
		TextView name;
		TextView duration;
		ImageView image;
		ImageButton delete;
	}

	public CityAdapter(Context context, List<City> cities) {
		super(context, R.layout.activity_country_details, cities);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final City city = getItem(position);
		final CityAdapterViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new CityAdapterViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.two_line_list, parent, false);

			viewHolder.name = convertView.findViewById(R.id.name_two_line_list);
			viewHolder.duration = convertView.findViewById(R.id.duration_two_line_list);
			viewHolder.image = convertView.findViewById(R.id.image_two_line_list);
			viewHolder.delete = convertView.findViewById(R.id.delete_two_line_list);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (CityAdapterViewHolder) convertView.getTag();
		}

		viewHolder.delete.setOnClickListener(v -> {
			CountryDetailsActivity countryDetailsActivity = (CountryDetailsActivity) getContext();
			countryDetailsActivity.generateConfirmDialog(countryDetailsActivity.getString(R.string.delete_entry_title), countryDetailsActivity.getString(R.string.delete_entry_text), () -> {
				countryDetailsActivity.getAdapter().remove(city);
				countryDetailsActivity.getAdapter().notifyDataSetChanged();
				countryDetailsActivity.getCityDao().delete(city.getId());
			});
		});

		viewHolder.name.setText(city.getName());
		viewHolder.duration.setText(city.getDuration());
		Layout.setOnlineImageURIOnImageView(getContext(), viewHolder.image, city.getImageURI());

		return convertView;
	}
}