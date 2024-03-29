package ch.bbcag.swift_travel.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

	public void generateMessageDialog(String title, String text) {
		progressBar.setVisibility(View.GONE);
		AlertDialog.Builder dialogBuilder;
		dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setPositiveButton(getString(R.string.dialog_close), (dialog, id) -> dialog.cancel());
		dialogBuilder.setMessage(text).setTitle(title);
		AlertDialog dialog = dialogBuilder.create();
		dialog.show();
	}

	public void generateMessageDialogAndCloseActivity(String title, String text) {
		progressBar.setVisibility(View.GONE);
		AlertDialog.Builder dialogBuilder;
		dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setPositiveButton(getString(R.string.dialog_close), (dialog, id) -> finish());
		dialogBuilder.setMessage(text).setTitle(title);
		AlertDialog dialog = dialogBuilder.create();
		dialog.show();
	}

	public void generateConfirmDialog(String title, String message, Runnable runnable) {
		progressBar.setVisibility(View.GONE);
		AlertDialog.Builder dialogBuilder;
		dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setPositiveButton(getString(R.string.dialog_yes), (dialog, id) -> runnable.run());
		dialogBuilder.setNegativeButton(getString(R.string.dialog_no), (dialog, id) -> dialog.cancel());
		dialogBuilder.setMessage(message).setTitle(title);
		AlertDialog dialog = dialogBuilder.create();
		dialog.show();
	}

	protected ProgressBar getProgressBar() {
		return progressBar;
	}

	protected void addToList(Task<QuerySnapshot> task, Runnable runnable) {
		if (task.isSuccessful()) {
			runnable.run();
			progressBar.setVisibility(View.GONE);
		}
	}

	protected void setObject(Task<DocumentSnapshot> task, Runnable runnable) {
		if (task.isSuccessful()) {
			runnable.run();
			progressBar.setVisibility(View.GONE);
		}
	}
}