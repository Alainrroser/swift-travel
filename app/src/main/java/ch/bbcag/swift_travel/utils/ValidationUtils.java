package ch.bbcag.swift_travel.utils;

import android.app.Activity;
import android.widget.EditText;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.bbcag.swift_travel.R;

public class ValidationUtils {
	private static boolean isPasswordValid = false;

	public static boolean areInputsEmpty(Activity context, List<TextInputLayout> textInputLayouts, List<EditText> editTexts) {
		List<String> strings = new ArrayList<>();
		for (EditText text : editTexts) {
			strings.add(text.getText().toString());
		}

		int errorCounter = 0;
		for (String text : strings) {
			int position = strings.indexOf(text);
			TextInputLayout selected = textInputLayouts.get(position);
			errorCounter = checkIfInputIsEmpty(context, selected, text, editTexts, position, errorCounter);
		}
		return errorCounter == 0;
	}

	private static int checkIfInputIsEmpty(Activity context, TextInputLayout selected, String text, List<EditText> editTexts, int position, int errorCounter) {
		if (text.isEmpty()) {
			selected.setError(context.getString(R.string.please) + " " + editTexts.get(position).getHint().toString());
			selected.requestFocus();
			errorCounter += 1;
		} else {
			selected.setError(null);
		}
		return errorCounter;
	}

	public static boolean areInputsEqual(Activity context, TextInputLayout TIL1, EditText ET1, TextInputLayout TIL2, EditText ET2) {
		if (ET1.getText().toString().equals(ET2.getText().toString())) {
			TIL1.setError(null);
			TIL2.setError(null);
			return true;
		} else {
			TIL1.setError(context.getString(R.string.passwords_do_not_match));
			TIL1.requestFocus();
			TIL2.setError(context.getString(R.string.passwords_do_not_match));
			TIL2.requestFocus();
			return false;
		}
	}

	public static boolean isPasswordCorrect(Activity context, FirebaseUser currentUser, TextInputLayout textInputLayout, EditText editText) {
		AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(currentUser.getEmail()), editText.getText().toString());
		currentUser.reauthenticate(credential).addOnCompleteListener(task -> setIsPasswordValid(context, task, textInputLayout));
		return isPasswordValid;
	}

	private static void setIsPasswordValid(Activity context, Task<Void> task, TextInputLayout textInputLayout) {
		if (task.isSuccessful()) {
			isPasswordValid = true;
			textInputLayout.setError(null);
		} else {
			isPasswordValid = false;
			textInputLayout.setError(context.getString(R.string.password_does_not_match));
			textInputLayout.requestFocus();
		}
	}

	public static boolean doesNewPasswordNotEqualOldPassword(Activity context, TextInputLayout oldPasswordLayout, EditText oldPassword, TextInputLayout newPasswordLayout, EditText newPassword) {
		if (oldPassword.getText().toString().equals(newPassword.getText().toString())) {
			oldPasswordLayout.setError(context.getString(R.string.passwords_cannot_be_the_same));
			oldPasswordLayout.requestFocus();
			newPasswordLayout.setError(context.getString(R.string.passwords_cannot_be_the_same));
			newPasswordLayout.requestFocus();
			return false;
		} else {
			oldPasswordLayout.setError(null);
			newPasswordLayout.setError(null);
			return true;
		}
	}
}
