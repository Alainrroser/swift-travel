package ch.bbcag.swift_travel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.entities.City;
import ch.bbcag.swift_travel.utils.Layout;

public class ChooseCityAdapter extends ArrayAdapter<City> {
	public static class ChooseCityViewHolder {
		TextView name;
		ImageView image;
	}

	public ChooseCityAdapter(Context context, List<City> cities) {
		super(context, R.layout.activity_choose, cities);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final City city = getItem(position);
		final ChooseCityViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new ChooseCityViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.one_line_list, parent, false);

			viewHolder.name = convertView.findViewById(R.id.name_one_line_list);
			viewHolder.image = convertView.findViewById(R.id.image_one_line_list);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ChooseCityViewHolder) convertView.getTag();
		}

		viewHolder.name.setText(city.getName());
		Layout.setOnlineImageURIOnImageView(getContext(), viewHolder.image, city.getImageURI());
		return convertView;
	}
}