package ch.bbcag.swift_travel.utils;

import android.app.Activity;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import ch.bbcag.swift_travel.R;

public class ValidationUtils {
	public static boolean areInputsEmpty(Activity context, List<TextInputLayout> textInputLayouts, List<EditText> editTexts) {
		List<String> strings = new ArrayList<>();
		for (EditText text : editTexts) {
			strings.add(text.getText().toString());
		}

		int errorCounter = 0;
		for (String text : strings) {
			int position = strings.indexOf(text);
			TextInputLayout selected = textInputLayouts.get(position);
			if (text.isEmpty()) {
				selected.setError(context.getString(R.string.please) + " " + editTexts.get(position).getHint().toString());
				selected.requestFocus();
				errorCounter += 1;
			} else {
				selected.setError(null);
			}
		}
		return errorCounter == 0;
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
}
