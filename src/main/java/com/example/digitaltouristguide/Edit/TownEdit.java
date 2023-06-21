package com.example.digitaltouristguide.Edit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.digitaltouristguide.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class TownEdit extends AppCompatActivity {

	Toolbar toolbar;
	EditText nameET, countyET, latET, lonET, descET;
	ImageView addImageButton, imagePreview;
	Button saveButton;

	FirebaseFirestore db;
	DocumentReference document;
	StorageReference storage;

	Uri imageUri;
	private static final int PICK_IMAGE_REQUEST = 1;
	ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_town);

		String townID = getIntent().getStringExtra("location_id");
		db = FirebaseFirestore.getInstance();
		document = db.collection("cities").document(townID);

		toolbar = findViewById(R.id.edit_town_toolbar);
		nameET = findViewById(R.id.edit_town_name);
		countyET = findViewById(R.id.edit_town_county);
		latET = findViewById(R.id.edit_town_latitude);
		lonET = findViewById(R.id.edit_town_longitude);
		descET = findViewById(R.id.edit_town_description);
		addImageButton = findViewById(R.id.edit_town_image);
		imagePreview = findViewById(R.id.edit_town_image_preview);
		saveButton = findViewById(R.id.edit_town_save_button);

		fetchData();

		addImageButton.setOnClickListener(view -> selectImage());
		saveButton.setOnClickListener(view -> {
			String nameTXT = nameET.getText().toString();
			String countyTXT = countyET.getText().toString();
			Double latTXT = Double.parseDouble(latET.getText().toString());
			Double lonTXT = Double.parseDouble(lonET.getText().toString());
			String descTXT = descET.getText().toString();

			if (checkEmptyFields(nameTXT, countyTXT, latTXT, lonTXT)) {
				editTown(nameTXT, countyTXT, latTXT, lonTXT, descTXT);
			}
		});
	}

	private void fetchData() {
		document.get().addOnSuccessListener(snapshot -> {
			fetchImage(snapshot);

			nameET.setText(snapshot.getString("name"));
			countyET.setText(snapshot.getString("county"));
			latET.setText(String.valueOf(snapshot.getDouble("latitude")));
			lonET.setText(String.valueOf(snapshot.getDouble("longitude")));
			descET.setText(snapshot.getString("description"));
			imagePreview.setVisibility(View.VISIBLE);

		}).addOnFailureListener(e -> {
			Toast.makeText(TownEdit.this, "Pogreška: " + e, Toast.LENGTH_SHORT).show();
			finish();
		});
	}

	private void fetchImage(DocumentSnapshot snapshot) {
		storage = FirebaseStorage.getInstance().getReference("images/" + snapshot.getString("imageName"));

		try {
			File file = File.createTempFile("tempfile", ".jpeg");
			storage.getFile(file).addOnSuccessListener(taskSnapshot -> {
				Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
				imagePreview.setImageBitmap(bitmap);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void selectImage() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, PICK_IMAGE_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
			imageUri = data.getData();
			imagePreview.setImageURI(imageUri);
			imagePreview.setVisibility(View.VISIBLE);
		}
	}

	private boolean checkEmptyFields(String name, String county, Double lat, Double lon) {
		if (name.isEmpty() || county.isEmpty() || lat.isNaN() || lon.isNaN()) {
			Toast.makeText(this, "Ispunite sva polja.", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	private void editTown(String name, String county, Double lat, Double lon, String desc) {
		pd = new ProgressDialog(this);
		pd.setMessage("Spremanje...");
		pd.show();

		document.update(
				"name", name,
				"county", county,
				"latitude", lat,
				"longitude", lon,
				"description", desc
		).addOnSuccessListener(unused -> {
			if (imageUri != null) {
				String imageName = name + "_image";
				storage = FirebaseStorage.getInstance().getReference("images/" + imageName);
				storage.putFile(imageUri);
				document.update(
						"imageName", imageName
				).addOnSuccessListener(unused1 -> {
					pd.dismiss();
					Toast.makeText(getApplicationContext(), "Podaci ažurirani.", Toast.LENGTH_SHORT).show();
				}).addOnFailureListener(e -> {
					pd.dismiss();
					Log.d("UPDATE_FAILURE", e.toString());
				});
			}

		}).addOnFailureListener(e -> {
			pd.dismiss();
			Toast.makeText(this, "Pogreška: " + e, Toast.LENGTH_SHORT).show();
		});

		finish();
	}
}