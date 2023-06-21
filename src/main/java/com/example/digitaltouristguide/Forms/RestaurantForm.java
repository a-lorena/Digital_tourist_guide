package com.example.digitaltouristguide.Forms;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.digitaltouristguide.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RestaurantForm extends AppCompatActivity {

	EditText nameET, typeET, townET, latET, lonET, hoursET, descET;
	ImageView addImageButton, imagePreview;
	Button saveButton;

	FirebaseFirestore db;
	Uri imageUri;
	private static final int PICK_IMAGE_REQUEST = 1;
	ProgressDialog pd;

	RequestQueue requestQueue;
	String URL = "https://fcm.googleapis.com/fcm/send";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form_restaurant);

		db = FirebaseFirestore.getInstance();
		requestQueue = Volley.newRequestQueue(this);

		nameET = findViewById(R.id.input_restaurant_name);
		typeET = findViewById(R.id.input_restaurant_type);
		townET = findViewById(R.id.input_restaurant_town);
		latET = findViewById(R.id.input_restaurant_latitude);
		lonET = findViewById(R.id.input_restaurant_longitude);
		hoursET = findViewById(R.id.input_restaurant_working_hours);
		descET = findViewById(R.id.input_restaurant_description);
		addImageButton = findViewById(R.id.add_restaurant_image);
		imagePreview = findViewById(R.id.restaurant_image_preview);
		saveButton = findViewById(R.id.save_restaurant_button);

		addImageButton.setOnClickListener(view -> selectImage());
		saveButton.setOnClickListener(view -> getInput());
	}

	private void getInput() {
		String name = nameET.getText().toString();
		String type = typeET.getText().toString();
		String town = townET.getText().toString();
		Double lat = Double.parseDouble(latET.getText().toString());
		Double lon = Double.parseDouble(lonET.getText().toString());
		String hours = hoursET.getText().toString();
		String desc = descET.getText().toString();

		if (checkEmptyFields(name, type, town, lat,lon, hours)) {
			saveRestaurant(name, type, town, lat,lon, hours, desc);
		}
	}

	private void saveRestaurant(String name, String type, String town, Double lat, Double lon, String hours, String desc) {
		pd = new ProgressDialog(this);
		pd.setMessage("Spremanje...");
		pd.show();

		Map<String, Object> restaurant = new HashMap<>();
		restaurant.put("name", name);
		restaurant.put("type", type);
		restaurant.put("town", town);
		restaurant.put("latitude", lat);
		restaurant.put("longitude", lon);
		restaurant.put("workingHours", hours);
		restaurant.put("description", desc);
		restaurant.put("comments", 0);
		restaurant.put("stars", 0);
		restaurant.put("rating", 0);

		if (imageUri != null) {
			String imageName = name + "_image";
			StorageReference storage = FirebaseStorage.getInstance().getReference("images_restaurants/" + imageName);
			storage.putFile(imageUri);
			restaurant.put("imageName", imageName);
		}

		db.collection("restaurants").add(restaurant).addOnSuccessListener(documentReference -> {
			pd.dismiss();
			Toast.makeText(RestaurantForm.this, "Lokacija uspješno spremljena.", Toast.LENGTH_SHORT).show();

			String id = documentReference.getId();
			sendNotification(id, name, town);
			saveNews(name, "restoran ili kafić", town);

		}).addOnFailureListener(e -> {
			pd.dismiss();
			Toast.makeText(RestaurantForm.this, "Spremanje neuspješno, pokušajte kasnije.\n Pogreška: " + e, Toast.LENGTH_SHORT).show();
		});

		finish();
	}

	private void sendNotification(String restaurantID, String restaurantName, String town) {
		JSONObject json = new JSONObject();

		try {
			json.put("to", "/topics/" + town);

			JSONObject notificationObj = new JSONObject();
			notificationObj.put("title", town + " ima novu lokaciju!");
			notificationObj.put("body", "Posjetite " + restaurantName);

			JSONObject extraData = new JSONObject();
			extraData.put("restaurantID", restaurantID);
			extraData.put("category", "restaurant");

			json.put("notification", notificationObj);
			json.put("data", extraData);

			JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, json,
					response -> {
					}, error -> {
			}) {
				@Override
				public Map<String, String> getHeaders() throws AuthFailureError {
					Map<String, String> header = new HashMap<>();
					header.put("content-type", "application/json");
					header.put("authorization", "key=");

					return header;
				}
			};

			requestQueue.add(request);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void saveNews(String name, String category, String town) {
		Map<String, Object> news = new HashMap<>();
		news.put("name", name);
		news.put("category", category);
		news.put("text", "Dodan je novi " + category + ": " + name);
		news.put("town", town);
		news.put("timestamp", Timestamp.now());

		FirebaseFirestore.getInstance().collection("news").add(news);
	}

	private boolean checkEmptyFields(String name, String type, String town, Double lat, Double lon, String hours) {
		if (name.isEmpty() || type.isEmpty() || town.isEmpty() || lat.isNaN() || lon.isNaN() || hours.isEmpty()) {
			Toast.makeText(this, "Ispunite sva polja!", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
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
}