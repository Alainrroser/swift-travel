package ch.bbcag.swift_travel.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.bbcag.swift_travel.R;
import ch.bbcag.swift_travel.utils.ValidationUtils;

public class LoginActivity extends UpButtonActivity {
	private Button registerButton, loginButton, loginWithGoogle;
	private TextInputLayout emailLayout, passwordLayout;
	private EditText email, password;

	private FirebaseAuth mAuth;

	private GoogleSignInClient mGoogleSignInClient;

	private ActivityResultLauncher<Intent> googleLoginActivityResultLauncher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setTitle(getString(R.string.login));

		registerButton = findViewById(R.id.login_register_button);

		mAuth = FirebaseAuth.getInstance();
		emailLayout = findViewById(R.id.login_email_input);
		email = findViewById(R.id.login_email);
		passwordLayout = findViewById(R.id.login_password_input);
		password = findViewById(R.id.login_password);
		loginButton = findViewById(R.id.login_button);
		loginWithGoogle = findViewById(R.id.login_with_google);


		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
		mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

		initializeActivityResultLauncher();
	}

	@Override
	protected void onStart() {
		super.onStart();
		getProgressBar().setVisibility(View.GONE);

		FirebaseUser currentUser = mAuth.getCurrentUser();
		if (currentUser != null) {
			Intent intent = new Intent(getApplicationContext(), UserActivity.class);
			startActivity(intent);
		}

		email.setText("");
		password.setText("");

		onRegisterButtonClick();
		onLoginButtonClick();
		onLoginWithGoogleClick();
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

	private void initializeActivityResultLauncher() {
		googleLoginActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
			if (result.getResultCode() == Activity.RESULT_OK) {
				Intent data = result.getData();
				Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
				handleGoogleLoginResult(task);
			}
		});
	}

	private void handleGoogleLoginResult(Task<GoogleSignInAccount> task) {
		try {
			GoogleSignInAccount account = task.getResult(ApiException.class);
			loginWithGoogle(Objects.requireNonNull(account));
		} catch (ApiException e) {
			e.printStackTrace();
		}
	}

	private void loginWithGoogle(GoogleSignInAccount account) {
		AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
		mAuth.signInWithCredential(credential).addOnCompleteListener(this, this::checkIfTaskWasSuccessful);
	}

	private void checkIfTaskWasSuccessful(Task<AuthResult> task) {
		if (task.isSuccessful()) {
			Intent intent = new Intent(getApplicationContext(), UserActivity.class);
			startActivity(intent);
		} else {
			generateMessageDialog(getString(R.string.login_unsuccessful_title), Objects.requireNonNull(task.getException()).getMessage());
		}
	}

	private void onLoginButtonClick() {
		loginButton.setOnClickListener(v -> {
			List<TextInputLayout> inputLayouts = new ArrayList<>();
			List<EditText> editTexts = new ArrayList<>();

			inputLayouts.add(emailLayout);
			inputLayouts.add(passwordLayout);

			editTexts.add(email);
			editTexts.add(password);

			checkIfInputsAreValid(inputLayouts, editTexts);
		});
	}

	private void checkIfInputsAreValid(List<TextInputLayout> inputLayouts, List<EditText> editTexts) {
		if (ValidationUtils.areInputsEmpty(LoginActivity.this, inputLayouts, editTexts)) {
			String emailStr = email.getText().toString();
			String passwordStr = password.getText().toString();
			mAuth.signInWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(LoginActivity.this, this::checkIfTaskWasSuccessful);
		}
	}

	private void onRegisterButtonClick() {
		registerButton.setOnClickListener(v -> {
			Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
			startActivity(intent);
		});
	}

	private void onLoginWithGoogleClick() {
		loginWithGoogle.setOnClickListener(v -> {
			Intent intent = mGoogleSignInClient.getSignInIntent();
			googleLoginActivityResultLauncher.launch(intent);
		});
	}
}
