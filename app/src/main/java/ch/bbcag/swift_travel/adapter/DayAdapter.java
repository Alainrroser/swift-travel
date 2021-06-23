package ch.bbcag.swift_travel.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.activities.CityDetailsActivity;
import ch.bbcag.swift_travel.entities.Day;

public class DayAdapter extends ArrayAdapter<Day> {
	private CityDetailsActivity cityDetailsActivity;

	public static class DayAdapterViewHolder {
		TextView name;
		TextView description;
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
			convertView = inflater.inflate(R.layout.two_line_list_no_delete_btn, parent, false);

			viewHolder.name = convertView.findViewById(R.id.name_two_line_list_no_delete_btn);
			viewHolder.description = convertView.findViewById(R.id.duration_two_line_list_no_delete_btn);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (DayAdapter.DayAdapterViewHolder) convertView.getTag();
		}

		viewHolder.name.setText(day.getName());
		viewHolder.description.setText(day.getDescription());

		return convertView;
	}
}
