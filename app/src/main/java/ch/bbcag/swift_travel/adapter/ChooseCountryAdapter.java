package ch.bbcag.swift_travel.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;

import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.entities.Country;

public class ChooseCountryAdapter extends ArrayAdapter<Country> {
    public static class ChooseCountryAdapterViewHolder {
        TextView name;
        ImageView image;
    }

    public ChooseCountryAdapter(Context context, List<Country> countries) {
        super(context, R.layout.activity_choose, countries);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Country country = getItem(position);
        final ChooseCountryAdapterViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ChooseCountryAdapterViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.one_line_list, parent, false);

            viewHolder.name = convertView.findViewById(R.id.name_one_line_list);
            viewHolder.image = convertView.findViewById(R.id.image_one_line_list);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChooseCountryAdapterViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(country.getName());
        GlideToVectorYou.init().with(getContext()).load(Uri.parse(country.getImageURI()), viewHolder.image);
        return convertView;
    }
}