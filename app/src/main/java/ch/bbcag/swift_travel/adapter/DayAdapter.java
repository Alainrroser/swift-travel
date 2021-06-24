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
import ch.bbcag.swift_travel.activities.CityDetailsActivity;
import ch.bbcag.swift_travel.entities.Day;
import ch.bbcag.swift_travel.utils.LayoutUtils;

public class DayAdapter extends ArrayAdapter<Day> {
	private CityDetailsActivity cityDetailsActivity;

	public static class DayAdapterViewHolder {
		TextView name;
		TextView date;
		ImageView image;
		ImageButton delete;
	}

	public DayAdapter(CityDetailsActivity cityDetailsActivity, List<Day> days) {
		super(cityDetailsActivity, R.layout.two_line_list, days);
		this.cityDetailsActivity = cityDetailsActivity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Day day = getItem(position);
		final DayAdapterViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new DayAdapterViewHolder();
			LayoutInflater inflater = LayoutInflater.from(cityDetailsActivity);
			convertView = inflater.inflate(R.layout.two_line_list, parent, false);

			viewHolder.name = convertView.findViewById(R.id.name_two_line_list);
			viewHolder.date = convertView.findViewById(R.id.duration_or_date_two_line_list);
			viewHolder.image = convertView.findViewById(R.id.image_two_line_list);
			viewHolder.delete = convertView.findViewById(R.id.delete_two_line_list);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (DayAdapter.DayAdapterViewHolder) convertView.getTag();
		}

		addInformationToAdapter(viewHolder, day);
		return convertView;
	}

	private void addInformationToAdapter(DayAdapterViewHolder viewHolder, Day day) {
		viewHolder.delete.setVisibility(View.GONE);

		viewHolder.name.setText(day.getName());
		if (day.getImageURI() != null) {
			LayoutUtils.setImageURIOnImageView(viewHolder.image, day.getImageURI());
		} else {
			viewHolder.image.setImageResource(R.drawable.trip_placeholder);
		}
		viewHolder.date.setText(day.getDate());
	}
}
