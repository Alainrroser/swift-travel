package ch.bbcag.swift_travel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.entities.Day;

public class DayAdapter extends ArrayAdapter<Day> {

	public static class DayAdapterViewHolder {
		TextView name;
		TextView description;
	}

	public DayAdapter(Context context, List<Day> days) {
		super(context, R.layout.two_line_list, days);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Day day = getItem(position);
		final DayAdapterViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new DayAdapterViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
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
