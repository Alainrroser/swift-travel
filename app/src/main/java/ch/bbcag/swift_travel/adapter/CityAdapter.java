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
import ch.bbcag.swift_travel.activities.CountryDetailsActivity;
import ch.bbcag.swift_travel.dal.SwiftTravelDatabase;
import ch.bbcag.swift_travel.entities.City;
import ch.bbcag.swift_travel.entities.Day;
import ch.bbcag.swift_travel.entities.Location;
import ch.bbcag.swift_travel.utils.LayoutUtils;

public class CityAdapter extends ArrayAdapter<City> {
	private CountryDetailsActivity countryDetailsActivity;

	public CityAdapter(CountryDetailsActivity countryDetailsActivity, List<City> cities) {
		super(countryDetailsActivity, R.layout.two_line_list, cities);
		this.countryDetailsActivity = countryDetailsActivity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final City city = getItem(position);
		final CityAdapterViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new CityAdapterViewHolder();
			LayoutInflater inflater = LayoutInflater.from(countryDetailsActivity);
			convertView = inflater.inflate(R.layout.two_line_list, parent, false);

			viewHolder.name = convertView.findViewById(R.id.name_two_line_list);
			viewHolder.duration = convertView.findViewById(R.id.duration_or_date_two_line_list);
			viewHolder.image = convertView.findViewById(R.id.image_two_line_list);
			viewHolder.delete = convertView.findViewById(R.id.delete_two_line_list);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (CityAdapterViewHolder) convertView.getTag();
		}

		addInformationToAdapter(viewHolder, city);
		return convertView;
	}

	private void addInformationToAdapter(CityAdapterViewHolder viewHolder, City city) {
		viewHolder.delete.setOnClickListener(v -> generateConfirmDialog(city));

		viewHolder.name.setText(city.getName());
		String dateRange = city.getStartDate() + "-" + city.getEndDate();
		viewHolder.duration.setText(dateRange);
		if (city.getImageURI() != null) {
			LayoutUtils.setImageURIOnImageView(viewHolder.image, city.getImageURI());
		} else {
			viewHolder.image.setImageResource(R.drawable.trip_placeholder);
		}
	}

	private void generateConfirmDialog(City city) {
		countryDetailsActivity.generateConfirmDialog(countryDetailsActivity.getString(R.string.delete_entry_title), countryDetailsActivity.getString(R.string.delete_entry_text), () -> {
			remove(city);
			notifyDataSetChanged();
			deleteDays(city);
			SwiftTravelDatabase.getInstance(countryDetailsActivity.getApplicationContext()).getCityDao().deleteById(city.getId());
			countryDetailsActivity.refreshContent();
		});
	}

	private void deleteDays(City city) {
		List<Day> days = SwiftTravelDatabase.getInstance(countryDetailsActivity.getApplicationContext()).getDayDao().getAllFromCity(city.getId());
		for (Day day : days) {
			deleteLocations(day);
			SwiftTravelDatabase.getInstance(countryDetailsActivity.getApplicationContext()).getDayDao().deleteById(day.getId());
		}
	}

	private void deleteLocations(Day day) {
		List<Location> locations = SwiftTravelDatabase.getInstance(countryDetailsActivity.getApplicationContext()).getLocationDao().getAllFromDay(day.getId());
		for (Location location : locations) {
			SwiftTravelDatabase.getInstance(countryDetailsActivity.getApplicationContext()).getLocationDao().deleteById(location.getId());
		}
	}

	public static class CityAdapterViewHolder {
		TextView name;
		TextView duration;
		ImageView image;
		ImageButton delete;
	}
}