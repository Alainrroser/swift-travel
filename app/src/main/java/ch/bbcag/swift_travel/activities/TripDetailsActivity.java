package ch.bbcag.swift_travel.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.constraintlayout.widget.Group;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.adapter.CountryAdapter;
import ch.bbcag.swift_travel.dal.TripDao;
import ch.bbcag.swift_travel.model.Country;
import ch.bbcag.swift_travel.model.Trip;
import ch.bbcag.swift_travel.utils.Const;

public class TripDetailsActivity extends UpButtonActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {
	private SearchView searchView;
	private MenuItem searchItem;

	private FloatingActionButton floatingActionButton;
	private CountryAdapter adapter;

    private ImageButton editDescriptionButton;
    private Button submitButton;

    private String tripName;
    private Trip selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        Intent intent = getIntent();
        tripName = intent.getStringExtra(Const.TRIP_NAME);
        setTitle(tripName);

        List<Trip> trips = TripDao.getAll();
        for (Trip trip : trips) {
            if (trip.getName().equals(intent.getStringExtra(Const.TRIP_NAME))) {
                selected = trip;
            }
        }

        floatingActionButton = findViewById(R.id.floating_action_button_trip_details);
        editDescriptionButton = findViewById(R.id.edit_button);
        submitButton = findViewById(R.id.submit_button);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getProgressBar().setVisibility(View.GONE);

        Intent intent = getIntent();

        if (intent.getStringExtra(Const.TRIP_DESCRIPTION) != null) {
            selected.setDescription(intent.getStringExtra(Const.TRIP_DESCRIPTION));
        }

        TextView title = findViewById(R.id.trip_title);
        title.setText(selected.getName());

        TextView description = findViewById(R.id.trip_description);
        if (selected.getDescription() == null) {
            description.setText(R.string.add_description);
        } else {
            description.setText(selected.getDescription());
        }
        description.setMovementMethod(new ScrollingMovementMethod());

        TextView duration = findViewById(R.id.trip_duration);
        duration.setText(selected.getDuration());

		List<Country> countries = selected.getCountries();
		adapter = new CountryAdapter(this, countries);

        createCountryFromIntent();
	    addCountriesToClickableList();

        Group form = findViewById(R.id.trip_form);
        form.setVisibility(View.GONE);

        refreshContent();

        onFloatingActionButtonClick();
        onEditDescriptionClick();
        onSubmitButtonClick();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.search_menu, menu);

		searchItem = menu.findItem(R.id.search);

		searchView = (SearchView) searchItem.getActionView();
		searchView.setQueryHint(getString(R.string.search));
		searchView.setIconified(false);
		searchView.setOnQueryTextListener(this);
		searchView.setOnCloseListener(this);

		searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				return true;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				searchView.setQuery("", false);
				filterAdapter("");
				return true;
			}
		});

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.search) {
			searchView.setIconified(false);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onQueryTextSubmit(String searchText) {
		filterAdapter(searchText);
		return true;
	}

	@Override
	public boolean onQueryTextChange(String searchText) {
		filterAdapter(searchText);
		return false;
	}

	@Override
	public boolean onClose() {
		if (!searchView.isIconified()) {
			searchItem.collapseActionView();
		} else {
			searchView.setIconified(false);
		}

		return false;
	}

	private void filterAdapter(String searchText) {
		adapter.getFilter().filter(searchText);
	}

	/**
	 * Creates a trip from the information in the intent if they aren't null.
	 */
	private void createCountryFromIntent() {
		Intent intent = getIntent();
		Country country = new Country();
		addTripInformation(intent, country);
	}

    private void addTripInformation(Intent intent, Country country) {
        if (intent.getStringExtra(Const.COUNTRY_NAME) != null) {
            country.setName(intent.getStringExtra(Const.COUNTRY_NAME));
            country.setImageURI(Uri.parse(intent.getStringExtra(Const.FLAG_URI)));
            adapter.add(country);
        }
    }

	public void addCountriesToClickableList() {
		ListView countries = findViewById(R.id.countries);
		countries.setAdapter(adapter);

        getProgressBar().setVisibility(View.GONE);

        AdapterView.OnItemClickListener mListClickedHandler = (parent, v, position, id) -> {
            Intent intent = new Intent(getApplicationContext(), CountryDetailsActivity.class);
            Country selected = (Country) parent.getItemAtPosition(position);
            intent.putExtra(Const.COUNTRY_NAME, selected.getName());
            startActivity(intent);
        };

		countries.setOnItemClickListener(mListClickedHandler);
	}

    private void refreshContent() {
        TextView title = findViewById(R.id.trip_title);
        TextView description = findViewById(R.id.trip_description);

        EditText editTitle = findViewById(R.id.edit_title);
        EditText editDescription = findViewById(R.id.edit_description);

        title.setText(selected.getName());
        setTitle(selected.getName());
        editTitle.setText(selected.getName());

        description.setText(selected.getDescription());
        editDescription.setText(selected.getDescription());
    }

    private void toggleForm(){
        Group form = findViewById(R.id.trip_form);
        Group content = findViewById(R.id.trip_content);

        if (form.getVisibility() == View.VISIBLE) {
            form.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
        } else {
            form.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
        }
    }

    private void onFloatingActionButtonClick() {
        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChooseActivity.class);
            intent.putExtra(Const.NAME, Const.COUNTRY);
            intent.putExtra(Const.TRIP_NAME, tripName);
            startActivity(intent);
        });
    }

    private void onEditDescriptionClick() {
        editDescriptionButton.setOnClickListener(v -> {
            toggleForm();
        });
    }

    private void onSubmitButtonClick() {
        submitButton.setOnClickListener(v -> {
            EditText editTitle = findViewById(R.id.edit_title);
            EditText editDescription = findViewById(R.id.edit_description);

            if(!editTitle.getText().toString().equals("")){selected.setName(editTitle.getText().toString());}

            if(!editDescription.getText().toString().equals("")){selected.setDescription(editDescription.getText().toString());}

            refreshContent();
            toggleForm();
        });
    }
}