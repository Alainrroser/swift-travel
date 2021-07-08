package ch.bbcag.swift_travel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.dal.SwiftTravelDatabase;
import ch.bbcag.swift_travel.entities.City;
import ch.bbcag.swift_travel.entities.Country;
import ch.bbcag.swift_travel.entities.Day;
import ch.bbcag.swift_travel.entities.Image;
import ch.bbcag.swift_travel.entities.Location;
import ch.bbcag.swift_travel.entities.Trip;
import ch.bbcag.swift_travel.utils.Const;
import ch.bbcag.swift_travel.utils.OnlineDatabaseUtils;
import ch.bbcag.swift_travel.utils.ValidationUtils;

public class UserActivity extends UpButtonActivity {
	private FirebaseAuth mAuth;
	private FirebaseUser currentUser;

	private TextView userEmail;

	private Button changePasswordButton;
	private Button logoutButton;
	private Button deleteButton;
	private Button submitButton;

	private Group form;
	private Group content;

	private TextInputLayout passwordLayout;
	private EditText password;
	private TextInputLayout newPasswordLayout;
	private EditText newPassword;
	private TextInputLayout newPasswordConfirmLayout;
	private EditText newPasswordConfirm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);

		changePasswordButton = findViewById(R.id.user_change_password);
		logoutButton = findViewById(R.id.user_logout);
		deleteButton = findViewById(R.id.user_delete_account);
		submitButton = findViewById(R.id.user_change_password_submit);

		form = findViewById(R.id.user_change_password_group);
		content = findViewById(R.id.user_content);

		userEmail = findViewById(R.id.user_email);
		mAuth = FirebaseAuth.getInstance();

		passwordLayout = findViewById(R.id.user_password_input);
		password = findViewById(R.id.user_password);
		newPasswordLayout = findViewById(R.id.user_new_password_input);
		newPassword = findViewById(R.id.user_new_password);
		newPasswordConfirmLayout = findViewById(R.id.user_new_password_confirm_input);
		newPasswordConfirm = findViewById(R.id.user_new_password_confirm);
	}

	@Override
	protected void onStart() {
		super.onStart();

		currentUser = mAuth.getCurrentUser();
		if (currentUser == null) {
			Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(intent);
		}

		setTitle(currentUser.getEmail());
		userEmail.setText(currentUser.getEmail());

		form.setVisibility(View.GONE);

		getProgressBar().setVisibility(View.GONE);

		onChangePasswordClick();
		onSubmitButtonClick();
		onLogoutButtonClick();
		onDeleteButtonClick();
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == android.R.id.home) {
			Intent intent = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	private void toggleForm() {
		if (form.getVisibility() == View.VISIBLE) {
			form.setVisibility(View.GONE);
			content.setVisibility(View.VISIBLE);
		} else {
			form.setVisibility(View.VISIBLE);
			content.setVisibility(View.GONE);

		}
	}

	private void onChangePasswordClick() {
		changePasswordButton.setOnClickListener(v -> {
			password.setText("");
			newPassword.setText("");
			newPasswordConfirm.setText("");
			toggleForm();
		});
	}

	private void onSubmitButtonClick() {
		submitButton.setOnClickListener(v -> {
			List<TextInputLayout> inputLayouts = new ArrayList<>();
			List<EditText> editTexts = new ArrayList<>();

			inputLayouts.add(passwordLayout);
			inputLayouts.add(newPasswordLayout);
			inputLayouts.add(newPasswordConfirmLayout);

			editTexts.add(password);
			editTexts.add(newPassword);
			editTexts.add(newPasswordConfirm);

			validateInputsAndChangePassword(inputLayouts, editTexts);
		});
	}

	private void validateInputsAndChangePassword(List<TextInputLayout> layouts, List<EditText> editTexts) {
		if (ValidationUtils.areInputsEmpty(UserActivity.this, layouts, editTexts)
		    && ValidationUtils.isPasswordCorrect(this, currentUser, passwordLayout, password)
		    && ValidationUtils.doesNewPasswordNotEqualOldPassword(this, newPasswordLayout, password, newPasswordLayout, newPassword)
		    && ValidationUtils.areInputsEqual(UserActivity.this, newPasswordLayout, newPassword, newPasswordConfirmLayout, newPasswordConfirm)) {
			currentUser.updatePassword(newPassword.getText().toString()).addOnCompleteListener(this::changePassword);
		}
	}

	private void changePassword(Task<Void> task) {
		if (!task.isSuccessful()) {
			generateMessageDialog(getString(R.string.success), getString(R.string.password_changed));
			toggleForm();
		} else {
			generateMessageDialog(getString(R.string.default_error_title), Objects.requireNonNull(task.getException()).getMessage());
		}
	}

	private void onLogoutButtonClick() {
		logoutButton.setOnClickListener(v -> logout());
	}

	private void logout() {
		mAuth.signOut();
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		startActivity(intent);
	}

	private void onDeleteButtonClick() {
		deleteButton.setOnClickListener(v -> generateConfirmDialog(getString(R.string.delete_account_title), getString(R.string.delete_account_text), () -> {
			deleteTrips();
			currentUser.delete();
			currentUser = null;
			logout();
		}));
	}

	private void deleteTrips() {
		List<Trip> trips = SwiftTravelDatabase.getInstance(getApplicationContext()).getTripDao().getAll();
		for (Trip trip : trips) {
			deleteCountries(trip);
			OnlineDatabaseUtils.delete(Const.TRIPS, trip.getId());
			SwiftTravelDatabase.getInstance(getApplicationContext()).getTripDao().deleteById(trip.getId());
		}
	}

	private void deleteCountries(Trip trip) {
		List<Country> countries = SwiftTravelDatabase.getInstance(getApplicationContext()).getCountryDao().getAllFromTrip(trip.getId());
		for (Country country : countries) {
			deleteCities(country);
			OnlineDatabaseUtils.delete(Const.COUNTRIES, country.getId());
			SwiftTravelDatabase.getInstance(getApplicationContext()).getCountryDao().deleteById(country.getId());
		}
	}

	private void deleteCities(Country country) {
		List<City> cities = SwiftTravelDatabase.getInstance(getApplicationContext()).getCityDao().getAllFromCountry(country.getId());
		for (City city : cities) {
			deleteDays(city);
			OnlineDatabaseUtils.delete(Const.CITIES, city.getId());
			SwiftTravelDatabase.getInstance(getApplicationContext()).getCityDao().deleteById(city.getId());
		}
	}

	private void deleteDays(City city) {
		List<Day> days = SwiftTravelDatabase.getInstance(getApplicationContext()).getDayDao().getAllFromCity(city.getId());
		for (Day day : days) {
			deleteLocations(day);
			OnlineDatabaseUtils.delete(Const.DAYS, day.getId());
			SwiftTravelDatabase.getInstance(getApplicationContext()).getDayDao().deleteById(day.getId());
		}
	}

	private void deleteLocations(Day day) {
		List<Location> locations = SwiftTravelDatabase.getInstance(getApplicationContext()).getLocationDao().getAllFromDay(day.getId());
		for (Location location : locations) {
			deleteImages(location);
			OnlineDatabaseUtils.delete(Const.LOCATIONS, location.getId());
			SwiftTravelDatabase.getInstance(getApplicationContext()).getLocationDao().deleteById(location.getId());
		}
	}

	private void deleteImages(Location location) {
		List<Image> images = SwiftTravelDatabase.getInstance(getApplicationContext()).getImageDao().getAllFromLocation(location.getId());
		for (Image image : images) {
			OnlineDatabaseUtils.delete(Const.IMAGES, image.getId());
			SwiftTravelDatabase.getInstance(getApplicationContext()).getImageDao().deleteById(image.getId());
		}
	}
}
