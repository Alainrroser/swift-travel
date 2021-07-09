package ch.bbcag.swift_travel.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

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

	public static StorageReference getImagesReference() {
		FirebaseStorage storage = FirebaseStorage.getInstance();
		StorageReference storageReference = storage.getReference();
		return storageReference.child(Const.IMAGES);
	}

	public static StorageReference getStorageReference() {
		FirebaseStorage storage = FirebaseStorage.getInstance();
		return storage.getReference();
	}

	public static String uploadImage(Uri filePath) {
		if (filePath != null) {
			String uuid = UUID.randomUUID().toString();
			StorageReference ref = getStorageReference().child(Const.STORAGE_PATH + uuid);
			ref.putFile(filePath);
			return uuid;
		}
		return null;
	}

	public static void setOnlineImageOnImageView(ImageView iv, String imageCDL) {
		StorageReference imagesRef = getImagesReference();
		StorageReference imageRef = imagesRef.child(imageCDL);
		final long ONE_MEGABYTE = 1024 * 1024;
		imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
			Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			iv.setImageBitmap(bmp);
		});
	}

	public static void deleteOnlineImage(String imageCDL) {
		StorageReference imagesRef = getImagesReference();
		StorageReference imageRef = imagesRef.child(imageCDL);

		imageRef.delete();
	}
}
