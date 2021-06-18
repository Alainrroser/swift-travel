package ch.bbcag.swift_travel.utils;

import android.content.Context;
import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;

import ch.bbcag.swift_travel.R;

public class Layout {

	public static void setTextOnTextView(TextView tv, String text) {
		tv.setText(text);
	}

	public static void setTextOnEditText(EditText et, String text) {
		et.setText(text);
	}

	public static void setTitleSize(TextView titleView) {
		if (titleView.getText().length() >= 20) {
			titleView.setTextSize(18);
		} else {
			titleView.setTextSize(24);
		}
	}

	public static void emptyMessageOnEmptyTextView(TextView textView) {
		if (textView.getText().equals("")) {
			textView.setText(R.string.add_text);
		}
	}

	public static void setEditableTitleText(TextView titleTextView, EditText editTextBox, String text) {
		setTextOnTextView(titleTextView, text);
		setTextOnEditText(editTextBox, text);
		setTitleSize(titleTextView);
	}

	public static void setEditableDescriptionText(TextView descriptionTextView, EditText editTextBox, String text) {
		setTextOnTextView(descriptionTextView, text);
		emptyMessageOnEmptyTextView(descriptionTextView);
		descriptionTextView.setMovementMethod(new ScrollingMovementMethod());
		setTextOnEditText(editTextBox, text);
	}

	public static void setImageURIOnImageView(ImageView iv, String URI) {
		iv.setImageURI(Uri.parse(URI));
	}

	public static void setOnlineImageURIOnImageView(Context context, ImageView iv, String URI) {
		GlideToVectorYou.init().with(context).setPlaceHolder(R.drawable.flag_placeholder, R.drawable.flag_placeholder).load(Uri.parse(URI), iv);
	}
}
