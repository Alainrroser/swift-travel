package ch.bbcag.swift_travel.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

	public static StorageReference getImagesReference(){
		FirebaseStorage storage = FirebaseStorage.getInstance();
		StorageReference storageReference = storage.getReference();
		return storageReference.child(Const.IMAGES);
	}

	public static StorageReference getStorageReference(){
		FirebaseStorage storage = FirebaseStorage.getInstance();
		return storage.getReference();
	}

	public static String uploadImage(Context context, Uri filePath) {
		if (filePath != null) {
			String uuid = UUID.randomUUID().toString();

			ProgressDialog progressDialog
					= new ProgressDialog(context);
			progressDialog.setTitle("Uploading...");
			progressDialog.show();

			StorageReference ref
					= getStorageReference()
					.child(
							Const.STORAGE_PATH
							+ uuid);

			ref.putFile(filePath)
			   .addOnSuccessListener(
					   taskSnapshot -> {
						   progressDialog.dismiss();
						   System.out.println("Successfully uploaded image.");
					   })

			   .addOnFailureListener(e -> {
				   progressDialog.dismiss();
				   System.out.println("failed to upload image: " + e.getMessage());
			   })
			   .addOnProgressListener(
					   taskSnapshot -> {
						   double progress
								   = (100.0
								      * taskSnapshot.getBytesTransferred()
								      / taskSnapshot.getTotalByteCount());
						   progressDialog.setMessage(
								   "Uploaded "
								   + (int) progress + "%");
					   });
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
		}).addOnFailureListener(exception -> {
			System.out.println("FAILURE! " + exception.getMessage());
		});
	}

	public static void deleteOnlineImage(String imageCDL){
		StorageReference imagesRef = getImagesReference();
		StorageReference imageRef = imagesRef.child(imageCDL);

		imageRef.delete().addOnSuccessListener(aVoid -> {
			System.out.println("\n\n FILE DELETED!!!!!!!\n\n");
		}).addOnFailureListener(exception -> {
			System.out.println("\n\n ERRRRRRRRRRROR!!!!!!!\n\n");
		});
	}
}
