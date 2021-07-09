package ch.bbcag.swift_travel.utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkUtils {
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork()) != null;
	}
}
