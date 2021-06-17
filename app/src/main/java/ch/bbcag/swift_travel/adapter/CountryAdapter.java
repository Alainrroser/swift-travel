package ch.bbcag.swift_travel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;

import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.model.Country;

public class CountryAdapter extends ArrayAdapter<Country> {
	public static class CountryAdapterViewHolder {
		TextView name;
		TextView duration;
		ImageView image;
	}

	public CountryAdapter(Context context, List<Country> countries) {
		super(context, R.layout.activity_trip_details, countries);
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

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (CountryAdapterViewHolder) convertView.getTag();
		}

		viewHolder.name.setText(country.getName());
		viewHolder.duration.setText(country.getDuration());
		GlideToVectorYou.init().with(getContext()).load(country.getImageURI(), viewHolder.image);
		return convertView;
	}
}