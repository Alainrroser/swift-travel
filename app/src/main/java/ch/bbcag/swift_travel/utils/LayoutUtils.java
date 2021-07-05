package ch.bbcag.swift_travel.utils;

import android.content.Context;
import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;

import ch.bbcag.swift_travel.R;

public class LayoutUtils {

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

	public static void emptyMessageOnEmptyTextView(TextView textView, String placeHolderText) {
		if (textView.getText().toString().isEmpty()) {
			textView.setText(placeHolderText);
		}
	}

	public static void setEditableTitleText(TextView titleTextView, EditText editTextBox, String text) {
		setTitleText(titleTextView, text);
		setTextOnEditText(editTextBox, text);
	}

	public static void setTitleText(TextView titleTextView, String text) {
		setTextOnTextView(titleTextView, text);
		setTitleSize(titleTextView);
	}

	public static void setEditableText(TextView descriptionTextView, EditText editTextBox, String text, String placeHolderText) {
		setTextOnTextView(descriptionTextView, editTextBox.getText().toString());
		emptyMessageOnEmptyTextView(descriptionTextView, placeHolderText);
		descriptionTextView.setMovementMethod(new ScrollingMovementMethod());
		setTextOnEditText(editTextBox, text);
	}

	public static void setImageURIOnImageView(ImageView iv, String URI) {
		iv.setImageURI(Uri.parse(URI));
	}

	public static void setFlagImageURIOnImageView(Context context, ImageView iv, String URI) {
		GlideToVectorYou.init().with(context).setPlaceHolder(R.drawable.flag_placeholder, R.drawable.flag_placeholder).load(Uri.parse(URI), iv);
	}

	public static void setOnlineImageURIOnImageView(Context context, ImageView iv, Uri URI) {
		Glide.with(context).load(URI).into(iv);
	}
}
