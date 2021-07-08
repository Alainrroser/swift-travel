package ch.bbcag.swift_travel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.utils.Const;

public class SpinnerAdapter extends ArrayAdapter<String> {
	private String[] objects;

	public SpinnerAdapter(Context context, int textViewResourceId, String[] objects) {
		super(context, textViewResourceId, objects);
		this.objects = objects;
	}

	@Override
	public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	private View getCustomView(final int position, View convertView, ViewGroup parent) {
		View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.dropdown_spinner, parent, false);
		final TextView label = (TextView) row.findViewById(R.id.spinner_text);
		label.setText(objects[position]);
		final ImageView iv = (ImageView) row.findViewById(R.id.spinner_image);
		switch (position){
			case Const.CATEGORY_DEFAULT:
				iv.setImageResource(0);
				break;
			case Const.CATEGORY_HOTEL:
				iv.setImageResource(R.drawable.category_hotel);
				break;
			case Const.CATEGORY_RESTAURANT:
				iv.setImageResource(R.drawable.category_restaurant);
				break;
			case Const.CATEGORY_LOCATION:
				iv.setImageResource(R.drawable.category_location);
				break;
		}
		return row;
	}
}
