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

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.utils.ValidationUtils;

public class UserActivity extends UpButtonActivity {
	FirebaseAuth mAuth;
	FirebaseUser currentUser;
	TextView userEmail;
	Button changePasswordButton, logoutButton, deleteButton, submitButton;
	Group form, content;

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
	}

	@Override
	protected void onStart() {
		super.onStart();

		System.out.println("hi");
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
		changePasswordButton.setOnClickListener(v -> toggleForm());
	}

	private void onSubmitButtonClick() {
		submitButton.setOnClickListener(v -> {
			TextInputLayout passwordLayout = findViewById(R.id.user_password_input);
			EditText password = findViewById(R.id.user_password);

			TextInputLayout newPasswordLayout = findViewById(R.id.user_new_password_input);
			EditText newPassword = findViewById(R.id.user_new_password);

			TextInputLayout newPasswordConfirmLayout = findViewById(R.id.user_new_password_confirm_input);
			EditText newPasswordConfirm = findViewById(R.id.user_new_password_confirm);

			List<TextInputLayout> layouts = new ArrayList<>();
			List<EditText> editTexts = new ArrayList<>();

			layouts.add(passwordLayout);
			layouts.add(newPasswordLayout);
			layouts.add(newPasswordConfirmLayout);

			editTexts.add(password);
			editTexts.add(newPassword);
			editTexts.add(newPasswordConfirm);


			if (ValidationUtils.areInputsEmpty(UserActivity.this, layouts, editTexts)
			    && ValidationUtils.isPasswordCorrect(this, currentUser, passwordLayout, password)
			    && ValidationUtils.doesNewPasswordNotEqualOldPassword(this, newPasswordLayout, password, newPasswordLayout, newPassword)
			    && ValidationUtils.areInputsEqual(UserActivity.this, newPasswordLayout, newPassword, newPasswordConfirmLayout, newPasswordConfirm)) {
				currentUser.updatePassword(newPassword.getText().toString()).addOnCompleteListener(task -> {
					if (!task.isSuccessful()) {
						generateMessageDialog(getString(R.string.default_error_title), Objects.requireNonNull(task.getException()).getMessage());
					} else {
						generateMessageDialog(getString(R.string.success), getString(R.string.password_changed));
						toggleForm();
					}
				});
			}
		});
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
			currentUser.delete();
			currentUser = null;
			logout();
		}));
	}
}
