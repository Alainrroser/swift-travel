package ch.bbcag.swift_travel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.utils.ValidationUtils;

public class RegisterActivity extends UpButtonActivity {
	private Button loginButton, registerButton;
	private TextInputLayout emailLayout, passwordLayout, passwordConfirmLayout;
	private EditText email, password, passwordConfirm;
	private FirebaseAuth firebaseAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		setTitle(getString(R.string.register));

		loginButton = findViewById(R.id.register_login_button);
		registerButton = findViewById(R.id.register_button);

		firebaseAuth = FirebaseAuth.getInstance();
		emailLayout = findViewById(R.id.register_email_input);
		email = findViewById(R.id.register_email);
		passwordLayout = findViewById(R.id.register_password_input);
		password = findViewById(R.id.register_password);
		passwordConfirmLayout = findViewById(R.id.register_password_confirm_input);
		passwordConfirm = findViewById(R.id.register_password_confirm);
		registerButton = findViewById(R.id.register_button);
	}

	@Override
	protected void onStart() {
		super.onStart();

		getProgressBar().setVisibility(View.GONE);

		email.setText("");
		password.setText("");
		passwordConfirm.setText("");

		onLoginButtonClick();
		onRegisterButtonClick();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == android.R.id.home) {
			onBackPressed();
			return true;
		} else if (itemId == R.id.back_home) {
			Intent intent = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	private void onRegisterButtonClick() {
		registerButton.setOnClickListener(v -> {
			List<TextInputLayout> inputLayouts = new ArrayList<>();
			List<EditText> editTexts = new ArrayList<>();

			inputLayouts.add(emailLayout);
			inputLayouts.add(passwordLayout);
			inputLayouts.add(passwordConfirmLayout);

			editTexts.add(email);
			editTexts.add(password);
			editTexts.add(passwordConfirm);

			if (ValidationUtils.areInputsEmpty(RegisterActivity.this, inputLayouts, editTexts)
			    && ValidationUtils.areInputsEqual(RegisterActivity.this, passwordLayout, password, passwordConfirmLayout, passwordConfirm)) {
				String emailStr = email.getText().toString();
				String passwordStr = password.getText().toString();
				firebaseAuth.createUserWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(RegisterActivity.this, task -> {
					if (!task.isSuccessful()) {
						RegisterActivity.this.generateMessageDialog(getString(R.string.login_unsuccessful_title), Objects.requireNonNull(task.getException()).getMessage());
					} else {
						Intent intent = new Intent(getApplicationContext(), UserActivity.class);
						startActivity(intent);
					}
				});
			}
		});
	}

	private void onLoginButtonClick() {
		loginButton.setOnClickListener(v -> {
			Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(intent);
		});
	}
}
