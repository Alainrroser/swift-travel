package ch.bbcag.swift_travel.adapter;

        import java.util.ArrayList;
        import java.util.List;

        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;

        import ch.bbcag.swift_travel.R;
        import ch.bbcag.swift_travel.model.Trip;

public class Three_line_list extends ArrayAdapter<Trip> {
    public Three_line_list(Context context, List<Trip> trips) {
        super(context, 0, trips);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.multi_lines, parent, false);
        }

        // Get the data item for this position
        Trip trip = getItem(position);

        // Lookup view for data population
        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvDestination = convertView.findViewById(R.id.tvDestination);
        TextView tvDuration = convertView.findViewById(R.id.tvDuration);
        ImageView IvImageURI = convertView.findViewById(R.id.trip_image);
        // Populate the data into the template view using the data object
        tvName.setText(trip.getName());
        tvDestination.setText(trip.getDestination());
        tvDuration.setText(trip.getDuration());
        IvImageURI.setImageURI(trip.getImageURI());
        // Return the completed view to render on screen
        return convertView;
    }
}