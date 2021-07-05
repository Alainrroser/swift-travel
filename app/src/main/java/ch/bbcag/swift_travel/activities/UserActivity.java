package ch.bbcag.swift_travel.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.utils.Const;
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

	private SwitchMaterial safeOnlineSwitch;

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

		safeOnlineSwitch = findViewById(R.id.safe_online_switch);
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

		if (getIntent().getBooleanExtra(Const.SAFE_ONLINE, false)) {
			getPreferences(Context.MODE_PRIVATE).edit().putBoolean(Const.SAFE_ONLINE_SWITCH_TOGGLE_STATE, true).apply();
			safeOnlineSwitch.setChecked(true);
		}

		setSafeOnlineSwitchState();

		form.setVisibility(View.GONE);

		getProgressBar().setVisibility(View.GONE);

		onSafeOnlineSwitchToggle();
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

	private void onSafeOnlineSwitchToggle() {
		safeOnlineSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
			getPreferences(Context.MODE_PRIVATE).edit().putBoolean(Const.SAFE_ONLINE_SWITCH_TOGGLE_STATE, safeOnlineSwitch.isChecked()).apply();
			setSafeOnlineSwitchState();
		});
	}

	private void setSafeOnlineSwitchState() {
		boolean safeOnlineSwitchState = getPreferences(Context.MODE_PRIVATE).getBoolean(Const.SAFE_ONLINE_SWITCH_TOGGLE_STATE, safeOnlineSwitch.isChecked());
		safeOnlineSwitch.setChecked(safeOnlineSwitchState);
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
