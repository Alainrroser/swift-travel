package ch.bbcag.swift_travel.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.view.MenuItem;

public class UpButtonActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBar();
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int itemId = item.getItemId();
		if(itemId == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setActionBar(){
		ActionBar actionBar = getSupportActionBar();
		if(actionBar != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
}