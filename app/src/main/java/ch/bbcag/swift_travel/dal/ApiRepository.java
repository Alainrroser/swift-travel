package ch.bbcag.swift_travel.dal;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

public class ApiRepository {

	public static void getJsonArray(Context applicationContext, String url, Response.Listener<JSONArray> responseListener, Response.ErrorListener errorListener) {
		RequestQueue queue = Volley.newRequestQueue(applicationContext);
		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, new JSONArray(), responseListener, errorListener);
		queue.add(jsonArrayRequest);
	}
}
