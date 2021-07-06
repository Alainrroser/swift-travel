package ch.bbcag.swift_travel.utils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class OnlineDatabaseUtils {
	public static void add(String collection, long id, Object object, boolean saveOnline) {
		if(saveOnline) {
			FirebaseFirestore.getInstance().collection(collection).document(String.valueOf(id)).set(object);
		}
	}

	public static void getAll(String collection, OnCompleteListener<QuerySnapshot> onCompleteListener) {
		FirebaseFirestore.getInstance().collection(collection).get().addOnCompleteListener(onCompleteListener);
	}

	public static void getAllFromParent(String collection, long parentId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
		FirebaseFirestore.getInstance().collection(collection).document(String.valueOf(parentId)).get().addOnCompleteListener(onCompleteListener);
	}

	public static void getById(String collection, long id, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
		FirebaseFirestore.getInstance().collection(collection).document(String.valueOf(id)).get().addOnCompleteListener(onCompleteListener);
	}

	public static void delete(String collection, long id, boolean saveOnline) {
		if(saveOnline) {
			FirebaseFirestore.getInstance().collection(collection).document(String.valueOf(id)).delete();
		}
	}
}
