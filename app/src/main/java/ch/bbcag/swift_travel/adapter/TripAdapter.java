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
import ch.bbcag.swift_travel.activities.MainActivity;
import ch.bbcag.swift_travel.dal.SwiftTravelDatabase;
import ch.bbcag.swift_travel.entities.City;
import ch.bbcag.swift_travel.entities.Country;
import ch.bbcag.swift_travel.entities.Day;
import ch.bbcag.swift_travel.entities.Image;
import ch.bbcag.swift_travel.entities.Location;
import ch.bbcag.swift_travel.entities.Trip;
import ch.bbcag.swift_travel.utils.Const;
import ch.bbcag.swift_travel.utils.LayoutUtils;
import ch.bbcag.swift_travel.utils.OnlineDatabaseUtils;

public class TripAdapter extends ArrayAdapter<Trip> {
	private MainActivity mainActivity;

	public TripAdapter(MainActivity mainActivity, List<Trip> trips) {
		super(mainActivity, R.layout.three_line_list, trips);
		this.mainActivity = mainActivity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Trip trip = getItem(position);
		final TripAdapterViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new TripAdapterViewHolder();
			LayoutInflater inflater = LayoutInflater.from(mainActivity);
			convertView = inflater.inflate(R.layout.three_line_list, parent, false);

			viewHolder.name = convertView.findViewById(R.id.name_three_line_list);
			viewHolder.originDestination = convertView.findViewById(R.id.origin_destination_three_line_list);
			viewHolder.duration = convertView.findViewById(R.id.duration_three_line_list);
			viewHolder.image = convertView.findViewById(R.id.image_three_line_list);
			viewHolder.delete = convertView.findViewById(R.id.delete_three_line_list);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (TripAdapterViewHolder) convertView.getTag();
		}

		addInformationToAdapter(viewHolder, trip);
		return convertView;
	}

	private void addInformationToAdapter(TripAdapterViewHolder viewHolder, Trip trip) {
		viewHolder.delete.setOnClickListener(v -> generateConfirmDialog(trip));

		viewHolder.name.setText(trip.getName());
		if (trip.getOrigin() != null && trip.getDestination() != null) {
			String originDestination = trip.getOrigin() + "-" + trip.getDestination();
			viewHolder.originDestination.setText(originDestination);
		} else {
			viewHolder.originDestination.setText(mainActivity.getString(R.string.trip_origin_destination));
		}

		String duration;
		if (trip.getDuration() == 1) {
			duration = trip.getDuration() + " " + mainActivity.getString(R.string.day);
		} else {
			duration = trip.getDuration() + " " + mainActivity.getString(R.string.days);
		}
		viewHolder.duration.setText(duration);
		if (trip.getImageURI() != null) {
			LayoutUtils.setImageURIOnImageView(viewHolder.image, trip.getImageURI());
		} else {
			viewHolder.image.setImageResource(R.drawable.placeholder_icon);
		}
	}

	private void generateConfirmDialog(Trip trip) {
		MainActivity mainActivity = (MainActivity) getContext();
		mainActivity.generateConfirmDialog(mainActivity.getString(R.string.delete_entry_title), mainActivity.getString(R.string.delete_entry_text), () -> {
			remove(trip);
			notifyDataSetChanged();
			deleteCountries(trip);
			OnlineDatabaseUtils.delete(Const.TRIPS, trip.getId());
			SwiftTravelDatabase.getInstance(mainActivity.getApplicationContext()).getTripDao().deleteById(trip.getId());
		});
	}

	private void deleteCountries(Trip trip) {
		List<Country> countries = SwiftTravelDatabase.getInstance(mainActivity.getApplicationContext()).getCountryDao().getAllFromTrip(trip.getId());
		for (Country country : countries) {
			deleteCities(country);
			OnlineDatabaseUtils.delete(Const.COUNTRIES, country.getId());
			SwiftTravelDatabase.getInstance(mainActivity.getApplicationContext()).getCountryDao().deleteById(country.getId());
		}
	}

	private void deleteCities(Country country) {
		List<City> cities = SwiftTravelDatabase.getInstance(mainActivity.getApplicationContext()).getCityDao().getAllFromCountry(country.getId());
		for (City city : cities) {
			deleteDays(city);
			OnlineDatabaseUtils.delete(Const.CITIES, city.getId());
			SwiftTravelDatabase.getInstance(mainActivity.getApplicationContext()).getCityDao().deleteById(city.getId());
		}
	}

	private void deleteDays(City city) {
		List<Day> days = SwiftTravelDatabase.getInstance(mainActivity.getApplicationContext()).getDayDao().getAllFromCity(city.getId());
		for (Day day : days) {
			deleteLocations(day);
			OnlineDatabaseUtils.delete(Const.DAYS, day.getId());
			SwiftTravelDatabase.getInstance(mainActivity.getApplicationContext()).getDayDao().deleteById(day.getId());
		}
	}

	private void deleteLocations(Day day) {
		List<Location> locations = SwiftTravelDatabase.getInstance(mainActivity.getApplicationContext()).getLocationDao().getAllFromDay(day.getId());
		for (Location location : locations) {
			deleteImages(location);
			OnlineDatabaseUtils.delete(Const.LOCATIONS, location.getId());
			SwiftTravelDatabase.getInstance(mainActivity.getApplicationContext()).getLocationDao().deleteById(location.getId());
		}
	}

	private void deleteImages(Location location) {
		List<Image> images = SwiftTravelDatabase.getInstance(mainActivity.getApplicationContext()).getImageDao().getAllFromLocation(location.getId());
		for (Image image : images) {
			OnlineDatabaseUtils.delete(Const.IMAGES, image.getId());
			SwiftTravelDatabase.getInstance(mainActivity.getApplicationContext()).getImageDao().deleteById(image.getId());
		}
	}

	public static class TripAdapterViewHolder {
		TextView name;
		TextView originDestination;
		TextView duration;
		ImageView image;
		ImageButton delete;
	}
}