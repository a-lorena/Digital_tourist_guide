package com.example.digitaltouristguide.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.digitaltouristguide.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class SettingsEditData extends AppCompatActivity {

	Toolbar toolbar;
	ImageView headerImage, profileImage, pickHeaderImage, pickProfileImage;
	EditText nameET;

	DocumentReference document;
	StorageReference storageHeader, storageProfile;
	String userID;

	Uri imageUriHeader, imageUriProfile;
	private static final int PICK_HEADER_REQUEST = 1;
	private static final int PICK_PROFILE_REQUEST = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_edit_profile);

		userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
		document = FirebaseFirestore.getInstance().collection("users").document(userID);

		toolbar = findViewById(R.id.edit_data_toolbar);
		headerImage = findViewById(R.id.edit_header_image);
		profileImage = findViewById(R.id.edit_profile_image);
		pickHeaderImage = findViewById(R.id.pick_header_image);
		pickProfileImage = findViewById(R.id.pick_profile_image);
		nameET = findViewById(R.id.edit_name);

		setSupportActionBar(toolbar);
		fetchData();

		pickHeaderImage.setOnClickListener(view -> selectHeaderImage());
		pickProfileImage.setOnClickListener(view -> selectProfileImage());
	}

	private void selectHeaderImage() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, PICK_HEADER_REQUEST);
	}

	private void selectProfileImage() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, PICK_PROFILE_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PICK_HEADER_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
			imageUriHeader = data.getData();
			headerImage.setImageURI(imageUriHeader);
		}

		if (requestCode == PICK_PROFILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
			imageUriProfile = data.getData();
			profileImage.setImageURI(imageUriProfile);
		}
	}

	private void fetchData() {
		String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
		DocumentReference document = FirebaseFirestore.getInstance()
				.collection("users").document(userID);

		document.get().addOnSuccessListener(snapshot -> {
			getImage(snapshot);
			nameET.setText(snapshot.getString("name"));
		}).addOnFailureListener(e -> {
			Toast.makeText(this, "Pogreška: " + e, Toast.LENGTH_SHORT).show();
			finish();
		});
	}

	private void getImage(DocumentSnapshot snapshot) {
		storageHeader = FirebaseStorage.getInstance().getReference("users/" + snapshot.getString("header"));

		try {
			File file = File.createTempFile("tempfile", ".jpeg");
			storageHeader.getFile(file).addOnSuccessListener(taskSnapshot -> {
				Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
				headerImage.setImageBitmap(bitmap);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.toolbar_menu_user_edit_data, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int itemID = item.getItemId();

		if (itemID == R.id.toolbar_save_data) {
			updateData();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void updateData() {
		String nameTXT = nameET.getText().toString();

		if (nameTXT.isEmpty()) {
			Toast.makeText(this, "Upišite ime!", Toast.LENGTH_SHORT).show();
		} else {
			ProgressDialog pd = new ProgressDialog(this);
			pd.setMessage("Spremanje...");
			pd.show();

			String headerName = userID + "_header";
			String profileName = userID + "_profile";

			storageHeader = FirebaseStorage.getInstance().getReference("users/" + headerName);
			storageProfile = FirebaseStorage.getInstance().getReference("users/" + profileName);

			if (imageUriHeader != null) { storageHeader.putFile(imageUriHeader); }
			if (imageUriProfile != null) { storageProfile.putFile(imageUriProfile); }

			document.update(
					"name", nameTXT,
					"header", headerName,
					"profile", profileName
			).addOnSuccessListener(unused -> {
				pd.dismiss();
				Toast.makeText(SettingsEditData.this, "Podaci spremljeni.", Toast.LENGTH_SHORT).show();
				finish();
			}).addOnFailureListener(e -> {
				pd.dismiss();
				Toast.makeText(SettingsEditData.this, "Pogreška: " + e, Toast.LENGTH_SHORT).show();
			});
		}
	}
}