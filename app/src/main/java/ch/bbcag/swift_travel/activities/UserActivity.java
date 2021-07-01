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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.utils.ValidationUtils;

public class UserActivity extends UpButtonActivity {
	FirebaseAuth mAuth;
	FirebaseUser currentUser;
	TextView userEmail;
	Button changePasswordButton, logoutButton, submitButton;
	Group form, content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);

		changePasswordButton = findViewById(R.id.user_change_password);
		logoutButton = findViewById(R.id.user_logout);
		submitButton = findViewById(R.id.user_change_password_submit);

		form = findViewById(R.id.user_change_password_group);
		content = findViewById(R.id.user_content);

		userEmail = findViewById(R.id.user_email);
		mAuth = FirebaseAuth.getInstance();
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
			toggleForm();
		});
	}

	private void onSubmitButtonClick() {
		submitButton.setOnClickListener(v -> {
			TextInputLayout newPasswordLayout = findViewById(R.id.user_new_password_input);
			EditText newPassword = findViewById(R.id.user_new_password);

			TextInputLayout newPasswordConfirmLayout = findViewById(R.id.user_new_password_confirm_input);
			EditText newPasswordConfirm = findViewById(R.id.user_new_password_confirm);

			List<TextInputLayout> layouts = new ArrayList<>();
			List<EditText> editTexts = new ArrayList<>();

			layouts.add(findViewById(R.id.user_password_input));
			layouts.add(newPasswordLayout);
			layouts.add(newPasswordConfirmLayout);

			editTexts.add(findViewById(R.id.user_password));
			editTexts.add(newPassword);
			editTexts.add(newPasswordConfirm);


			if (ValidationUtils.areInputsEmpty(UserActivity.this, layouts, editTexts)
			&& ValidationUtils.areInputsEqual(UserActivity.this, newPasswordLayout, newPassword, newPasswordConfirmLayout, newPasswordConfirm)){
			currentUser.updatePassword(newPassword.getText().toString())
			           .addOnCompleteListener(task -> {
				           if (!task.isSuccessful()) {
								generateMessageDialog(getString(R.string.default_error_title), task.getException().getMessage());
				           } else {
					           generateMessageDialog(getString(R.string.success), getString(R.string.password_changed));
				           }
			           });
			}
		});
	}

	private void onLogoutButtonClick() {
		logoutButton.setOnClickListener(v -> {
			mAuth.signOut();
			Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(intent);
		});
	}

}
