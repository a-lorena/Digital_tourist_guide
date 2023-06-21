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

import com.example.digitaltouristguide.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class RestaurantEdit extends AppCompatActivity {

	EditText nameET, typeET, townET, latET, lonET, hoursET, descET;
	ImageView addImageButton, imagePreview;
	Button saveButton;

	DocumentReference document;
	StorageReference storage;

	Uri imageUri;
	private static final int PICK_IMAGE_REQUEST = 1;
	ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_restaurant);

		String restaurantID = getIntent().getStringExtra("restaurant_id");
		document = FirebaseFirestore.getInstance().collection("restaurants").document(restaurantID);

		nameET = findViewById(R.id.edit_restaurant_name);
		typeET = findViewById(R.id.edit_restaurant_type);
		townET = findViewById(R.id.edit_restaurant_town);
		latET = findViewById(R.id.edit_restaurant_latitude);
		lonET = findViewById(R.id.edit_restaurant_longitude);
		hoursET = findViewById(R.id.edit_restaurant_working_hours);
		descET = findViewById(R.id.edit_restaurant_description);
		addImageButton = findViewById(R.id.edit_restaurant_image);
		imagePreview = findViewById(R.id.edit_restaurant_image_preview);
		saveButton = findViewById(R.id.edit_restaurant_save_button);

		fetchData();

		addImageButton.setOnClickListener(view -> selectImage());
		saveButton.setOnClickListener(view -> getInput());
	}

	private void fetchData() {
		document.get().addOnSuccessListener(snapshot -> {
			fetchImage(snapshot);

			nameET.setText(snapshot.getString("name"));
			typeET.setText(snapshot.getString("type"));
			townET.setText(snapshot.getString("town"));
			latET.setText(String.valueOf(snapshot.getDouble("latitude")));
			lonET.setText(String.valueOf(snapshot.getDouble("longitude")));
			hoursET.setText(snapshot.getString("workingHours"));
			descET.setText(snapshot.getString("description"));
			imagePreview.setVisibility(View.VISIBLE);

		}).addOnFailureListener(e -> {
			Toast.makeText(this, "Pogreška: " + e, Toast.LENGTH_SHORT).show();
			finish();
		});
	}

	private void fetchImage(DocumentSnapshot snapshot) {
		storage = FirebaseStorage.getInstance().getReference("images_restaurants/" + snapshot.getString("imageName"));

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

	private void getInput() {
		String name = nameET.getText().toString();
		String type = typeET.getText().toString();
		String town = townET.getText().toString();
		Double lat = Double.parseDouble(latET.getText().toString());
		Double lon = Double.parseDouble(lonET.getText().toString());
		String hours = hoursET.getText().toString();
		String desc = descET.getText().toString();

		if (checkEmptyFields(name,type, town, lat, lon, hours)) {
			updateRestaurant(name,type, town, lat, lon, hours, desc);
		}
	}

	private boolean checkEmptyFields(String name, String type, String town, Double lat, Double lon, String hours) {
		if (name.isEmpty() || type.isEmpty() || town.isEmpty() || lat.isNaN() || lon.isNaN() || hours.isEmpty()) {
			Toast.makeText(this, "Ispunite sva polja!", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private void updateRestaurant(String name, String type, String town, Double lat, Double lon, String hours, String desc) {
		pd = new ProgressDialog(this);
		pd.setMessage("Spremanje...");
		pd.show();

		document.update(
				"name", name,
				"type", type,
				"town", town,
				"latitude", lat,
				"longitude", lon,
				"workingHours", hours,
				"description", desc
		).addOnSuccessListener(unused -> {
			if (imageUri != null) {
				String imageName = name + "_image";
				StorageReference storage = FirebaseStorage.getInstance().getReference("images_restaurants/" + imageName);
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