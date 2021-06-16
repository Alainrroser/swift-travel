package ch.bbcag.swift_travel.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import ch.bbcag.swift_travel.R;

public class BaseActivity extends AppCompatActivity {
	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();
		progressBar = findViewById(R.id.progress_bar);
		progressBar.setVisibility(View.VISIBLE);
	}

	protected void generateAlertDialog(String errorTitle, String errorMessage) {
		progressBar.setVisibility(View.GONE);
		AlertDialog.Builder dialogBuilder;
		dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setPositiveButton(getString(R.string.error_close), (dialog, id) -> {
			// Closes this activity
			finish();
		});
		dialogBuilder.setMessage(errorMessage).setTitle(errorTitle);
		AlertDialog dialog = dialogBuilder.create();
		dialog.show();
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}
}