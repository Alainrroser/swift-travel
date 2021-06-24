package ch.bbcag.swift_travel.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.activities.ChooseCountryActivity;
import ch.bbcag.swift_travel.entities.Country;
import ch.bbcag.swift_travel.utils.LayoutUtils;

public class ChooseCountryAdapter extends ArrayAdapter<Country> {
	private ChooseCountryActivity chooseCountryActivity;

	public static class ChooseCountryAdapterViewHolder {
		TextView name;
		ImageView image;
	}

	public ChooseCountryAdapter(ChooseCountryActivity chooseCountryActivity, List<Country> countries) {
		super(chooseCountryActivity, R.layout.one_line_list, countries);
		this.chooseCountryActivity = chooseCountryActivity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Country country = getItem(position);
		final ChooseCountryAdapterViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new ChooseCountryAdapterViewHolder();
			LayoutInflater inflater = LayoutInflater.from(chooseCountryActivity);
			convertView = inflater.inflate(R.layout.one_line_list, parent, false);

			viewHolder.name = convertView.findViewById(R.id.name_one_line_list);
			viewHolder.image = convertView.findViewById(R.id.image_one_line_list);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ChooseCountryAdapterViewHolder) convertView.getTag();
		}

		addInformationToAdapter(viewHolder, country);
		return convertView;
	}

	private void addInformationToAdapter(ChooseCountryAdapterViewHolder viewHolder, Country country) {
		viewHolder.name.setText(country.getName());
		LayoutUtils.setOnlineImageURIOnImageView(getContext(), viewHolder.image, country.getImageURI());
	}
}