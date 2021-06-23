package ch.bbcag.swift_travel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.activities.CityDetailsActivity;
import ch.bbcag.swift_travel.entities.Day;

public class DayAdapter extends ArrayAdapter<Day> {

	public static class DayAdapterViewHolder {
		TextView name;
		TextView description;
		ImageButton delete;
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
			convertView = inflater.inflate(R.layout.two_line_list, parent, false);

			viewHolder.name = convertView.findViewById(R.id.name_two_line_list);
			viewHolder.description = convertView.findViewById(R.id.duration_two_line_list);
			viewHolder.delete = convertView.findViewById(R.id.delete_two_line_list);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (DayAdapter.DayAdapterViewHolder) convertView.getTag();
		}

		viewHolder.delete.setOnClickListener(v -> generateConfirmDialog(day));


		viewHolder.name.setText(day.getName());
		viewHolder.description.setText(day.getDescription());

		return convertView;
	}

	private void generateConfirmDialog(Day day) {
		CityDetailsActivity cityDetailsActivity = (CityDetailsActivity) getContext();
		cityDetailsActivity.generateConfirmDialog(cityDetailsActivity.getString(R.string.delete_entry_title), cityDetailsActivity.getString(R.string.delete_entry_text), () -> {
			cityDetailsActivity.getAdapter().remove(day);
			cityDetailsActivity.getAdapter().notifyDataSetChanged();
			cityDetailsActivity.getDayDao().delete(day.getId());
		});
	}
}
