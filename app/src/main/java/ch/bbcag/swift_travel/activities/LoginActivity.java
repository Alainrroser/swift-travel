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
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.utils.ValidationUtils;

public class LoginActivity extends UpButtonActivity {
	private Button registerButton, loginButton;
	private TextInputLayout emailLayout, passwordLayout;
	private EditText email, password;
	private FirebaseAuth firebaseAuth;
	private FirebaseAuth.AuthStateListener authStateListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setTitle(getString(R.string.login));

		registerButton = findViewById(R.id.login_register_button);

		firebaseAuth = FirebaseAuth.getInstance();
		emailLayout = findViewById(R.id.login_email_input);
		email = findViewById(R.id.login_email);
		passwordLayout = findViewById(R.id.login_password_input);
		password = findViewById(R.id.login_password);
		loginButton = findViewById(R.id.login_button);

		authStateListener = firebaseAuth -> {
			FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
			if (firebaseUser != null) {
				Intent intent = new Intent(getApplicationContext(), UserActivity.class);
				startActivity(intent);
			}
		};
	}

	@Override
	protected void onStart() {
		super.onStart();
		getProgressBar().setVisibility(View.GONE);

		onRegisterButtonClick();
		onLoginButtonClick();
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

	private void onLoginButtonClick() {
		loginButton.setOnClickListener(v -> {
			List<TextInputLayout> inputLayouts = new ArrayList<>();
			List<EditText> editTexts = new ArrayList<>();

			inputLayouts.add(emailLayout);
			inputLayouts.add(passwordLayout);

			editTexts.add(email);
			editTexts.add(password);

			if (ValidationUtils.areInputsEmpty(LoginActivity.this, inputLayouts, editTexts)) {
				String emailStr = email.getText().toString();
				String passwordStr = password.getText().toString();
				firebaseAuth.signInWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(LoginActivity.this, task -> {
					if (!task.isSuccessful()) {
						LoginActivity.this.generateMessageDialog(getString(R.string.default_error_title), getString(R.string.login_error));
					} else {
						Intent intent = new Intent(getApplicationContext(), UserActivity.class);
						startActivity(intent);
					}
				});
			}
		});
	}

	private void onRegisterButtonClick() {
		registerButton.setOnClickListener(v -> {
			Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
			startActivity(intent);
		});
	}
}
