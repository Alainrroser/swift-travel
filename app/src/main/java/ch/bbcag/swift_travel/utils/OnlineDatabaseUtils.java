package ch.bbcag.swift_travel.utils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class OnlineDatabaseUtils {
	public static void add(String collection, long id, Object object) {
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			FirebaseFirestore.getInstance().collection(collection).document(String.valueOf(id)).set(object);
		}
	}

	public static void getAllTripsFromUser(String userId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
		FirebaseFirestore.getInstance().collection(Const.TRIPS).whereEqualTo(Const.USER_ID, userId).get().addOnCompleteListener(onCompleteListener);
	}

	public static void getAllFromParentId(String collection, String parentIdText, long parentId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
		FirebaseFirestore.getInstance().collection(collection).whereEqualTo(parentIdText, parentId).get().addOnCompleteListener(onCompleteListener);
	}

	public static void getById(String collection, long id, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
		FirebaseFirestore.getInstance().collection(collection).document(String.valueOf(id)).get().addOnCompleteListener(onCompleteListener);
	}

	public static void delete(String collection, long id) {
		if (FirebaseAuth.getInstance().getCurrentUser() != null) {
			FirebaseFirestore.getInstance().collection(collection).document(String.valueOf(id)).delete();
		}
	}
}
