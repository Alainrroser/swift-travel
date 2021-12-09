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
import ch.bbcag.swift_travel.activities.TripDetailsActivity;
import ch.bbcag.swift_travel.dal.SwiftTravelDatabase;
import ch.bbcag.swift_travel.entities.City;
import ch.bbcag.swift_travel.entities.Country;
import ch.bbcag.swift_travel.entities.Day;
import ch.bbcag.swift_travel.entities.Image;
import ch.bbcag.swift_travel.entities.Location;
import ch.bbcag.swift_travel.utils.Const;
import ch.bbcag.swift_travel.utils.LayoutUtils;
import ch.bbcag.swift_travel.utils.OnlineDatabaseUtils;

public class CountryAdapter extends ArrayAdapter<Country> {
	private TripDetailsActivity tripDetailsActivity;

	public CountryAdapter(TripDetailsActivity tripDetailsActivity, List<Country> countries) {
		super(tripDetailsActivity, R.layout.two_line_list, countries);
		this.tripDetailsActivity = tripDetailsActivity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Country country = getItem(position);
		final CountryAdapterViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new CountryAdapterViewHolder();
			LayoutInflater inflater = LayoutInflater.from(tripDetailsActivity);
			convertView = inflater.inflate(R.layout.two_line_list, parent, false);

			viewHolder.name = convertView.findViewById(R.id.name_two_line_list);
			viewHolder.duration = convertView.findViewById(R.id.duration_or_date_two_line_list);
			viewHolder.image = convertView.findViewById(R.id.image_two_line_list);
			viewHolder.delete = convertView.findViewById(R.id.delete_two_line_list);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (CountryAdapterViewHolder) convertView.getTag();
		}

		addInformationToAdapter(viewHolder, country);
		return convertView;
	}

	private void addInformationToAdapter(CountryAdapterViewHolder viewHolder, Country country) {
		viewHolder.delete.setOnClickListener(v -> generateConfirmDialog(country));

		viewHolder.name.setText(country.getName());
		String duration;
		if (country.getDuration() == 1) {
			duration = country.getDuration() + " " + tripDetailsActivity.getString(R.string.day);
		} else {
			duration = country.getDuration() + " " + tripDetailsActivity.getString(R.string.days);
		}
		viewHolder.duration.setText(duration);
		LayoutUtils.setFlagImageURIOnImageView(getContext(), viewHolder.image, country.getImageURI());
	}

	private void generateConfirmDialog(Country country) {
		tripDetailsActivity.generateConfirmDialog(tripDetailsActivity.getString(R.string.delete_entry_title), tripDetailsActivity.getString(R.string.delete_entry_text), () -> {
			remove(country);
			notifyDataSetChanged();
			deleteCities(country);
			OnlineDatabaseUtils.delete(Const.COUNTRIES, country.getId());
			SwiftTravelDatabase.getInstance(tripDetailsActivity.getApplicationContext()).getCountryDao().deleteById(country.getId());
			tripDetailsActivity.refreshContent();
		});
	}

	private void deleteCities(Country country) {
		List<City> cities = SwiftTravelDatabase.getInstance(tripDetailsActivity.getApplicationContext()).getCityDao().getAllFromCountry(country.getId());
		for (City city : cities) {
			deleteDays(city);
			if (city.getImageCDL() != null) {
				OnlineDatabaseUtils.deleteOnlineImage(city.getImageCDL());
			}
			OnlineDatabaseUtils.delete(Const.CITIES, city.getId());
			SwiftTravelDatabase.getInstance(tripDetailsActivity.getApplicationContext()).getCityDao().deleteById(city.getId());
		}
	}

	private void deleteDays(City city) {
		List<Day> days = SwiftTravelDatabase.getInstance(tripDetailsActivity.getApplicationContext()).getDayDao().getAllFromCity(city.getId());
		for (Day day : days) {
			deleteLocations(day);
			if (day.getImageCDL() != null) {
				OnlineDatabaseUtils.deleteOnlineImage(day.getImageCDL());
			}
			OnlineDatabaseUtils.delete(Const.DAYS, day.getId());
			SwiftTravelDatabase.getInstance(tripDetailsActivity.getApplicationContext()).getDayDao().deleteById(day.getId());
		}
	}

	private void deleteLocations(Day day) {
		List<Location> locations = SwiftTravelDatabase.getInstance(tripDetailsActivity.getApplicationContext()).getLocationDao().getAllFromDay(day.getId());
		for (Location location : locations) {
			deleteImages(location);
			if (location.getImageCDL() != null) {
				OnlineDatabaseUtils.deleteOnlineImage(location.getImageCDL());
			}
			OnlineDatabaseUtils.delete(Const.LOCATIONS, location.getId());
			SwiftTravelDatabase.getInstance(tripDetailsActivity.getApplicationContext()).getLocationDao().deleteById(location.getId());
		}
	}

	private void deleteImages(Location location) {
		List<Image> images = SwiftTravelDatabase.getInstance(tripDetailsActivity.getApplicationContext()).getImageDao().getAllFromLocation(location.getId());
		for (Image image : images) {
			if (image.getImageCDL() != null) {
				OnlineDatabaseUtils.deleteOnlineImage(image.getImageCDL());
			}
			OnlineDatabaseUtils.delete(Const.IMAGES, image.getId());
			SwiftTravelDatabase.getInstance(tripDetailsActivity.getApplicationContext()).getImageDao().deleteById(image.getId());
		}
	}

	public static class CountryAdapterViewHolder {
		TextView name;
		TextView duration;
		ImageView image;
		ImageButton delete;
	}
}