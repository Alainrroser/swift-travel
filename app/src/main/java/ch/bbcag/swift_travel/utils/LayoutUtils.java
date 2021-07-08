package ch.bbcag.swift_travel.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
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

	@SuppressLint("UseCompatLoadingForDrawables")
	public static void setRoundedImageURIOnImageView(Context context, ImageView iv, String URI) {
		Glide.with(context)
		     .load(URI)
		     .apply(RequestOptions.bitmapTransform(new RoundedCorners(180)))
		     .into(iv);
	}

	public static void setFlagImageURIOnImageView(Context context, ImageView iv, String URI) {
		GlideToVectorYou
				.init()
				.with(context)
				.setPlaceHolder(R.drawable.placeholder_flag, R.drawable.placeholder_flag)
				.load(Uri.parse(URI), iv);
	}

	public static void setOnlineImageURIOnImageView(Context context, ImageView iv, Uri URI, boolean roundedCorners) {
		if (roundedCorners) {
			Glide.with(context).load(URI).apply(RequestOptions.bitmapTransform(new RoundedCorners(180))).into(iv);
		} else {
			Glide.with(context).load(URI).into(iv);
		}
	}
}
