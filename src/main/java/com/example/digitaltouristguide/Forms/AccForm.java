package com.example.digitaltouristguide.Forms;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

public class AccForm extends AppCompatActivity {

	EditText nameET, typeET, townET, latET, lonET, priceET, descET;
	ImageView addImageButton, imagePreview;
	Button saveButton;

	Uri imageUri;
	private static final int PICK_IMAGE_REQUEST = 1;
	ProgressDialog pd;

	RequestQueue requestQueue;
	String URL = "https://fcm.googleapis.com/fcm/send";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form_acc);

		requestQueue = Volley.newRequestQueue(this);

		nameET = findViewById(R.id.input_acc_name);
		typeET = findViewById(R.id.input_acc_type);
		townET = findViewById(R.id.input_acc_town);
		latET = findViewById(R.id.input_acc_latitude);
		lonET = findViewById(R.id.input_acc_longitude);
		priceET = findViewById(R.id.input_acc_price);
		descET = findViewById(R.id.input_acc_description);
		addImageButton = findViewById(R.id.add_acc_image);
		imagePreview = findViewById(R.id.acc_image_preview);
		saveButton = findViewById(R.id.save_acc_button);

		addImageButton.setOnClickListener(view -> selectImage());
		saveButton.setOnClickListener(view -> getInput());
	}

	private void getInput() {
		String name = nameET.getText().toString();
		String type = typeET.getText().toString();
		String town = townET.getText().toString();
		Double lat = Double.parseDouble(latET.getText().toString());
		Double lon = Double.parseDouble(lonET.getText().toString());
		String price = priceET.getText().toString();
		String desc = descET.getText().toString();

		if (checkEmptyFields(name, type, town, lat, lon, price)) {
			saveAcc(name, type, town, lat, lon, price, desc);
		}

	}

	private void saveAcc(String name, String type, String town, Double lat, Double lon, String price, String desc) {
		pd = new ProgressDialog(this);
		pd.setMessage("Spremanje...");
		pd.show();

		Map<String, Object> acc = new HashMap<>();
		acc.put("name", name);
		acc.put("type", type);
		acc.put("town", town);
		acc.put("latitude", lat);
		acc.put("longitude", lon);
		acc.put("price", price);
		acc.put("description", desc);
		acc.put("comments", 0);
		acc.put("stars", 0);
		acc.put("rating", 0);

		if (imageUri != null) {
			String imageName = name + "_image";
			StorageReference storage = FirebaseStorage.getInstance().getReference("images_acc/" + imageName);
			storage.putFile(imageUri);
			acc.put("imageName", imageName);
		}

		FirebaseFirestore db = FirebaseFirestore.getInstance();
		db.collection("accomodation").add(acc).addOnSuccessListener(documentReference -> {
			pd.dismiss();
			Toast.makeText(AccForm.this, "Lokacija spremljena.", Toast.LENGTH_SHORT).show();

			String id = documentReference.getId();
			sendNotification(id, name, town);
			saveNews(name, "smještaj ili parking", town);

		}).addOnFailureListener(e -> {
			pd.dismiss();
			Toast.makeText(AccForm.this, "Spremanje neuspješno, pokušajte kasnije.\n Pogreška: " + e, Toast.LENGTH_SHORT).show();
		});

		finish();
	}

	private void sendNotification(String accID, String accName, String town) {
		JSONObject json = new JSONObject();

		try {
			json.put("to", "/topics/" + town);

			JSONObject notificationObj = new JSONObject();
			notificationObj.put("title", town + " ima novi smještaj!");
			notificationObj.put("body", "Pogledajte " + accName);

			JSONObject extraData = new JSONObject();
			extraData.put("accID", accID);
			extraData.put("category", "acc");

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

	private boolean checkEmptyFields(String name, String type, String town, Double lat, Double lon, String price) {
		if (name.isEmpty() || type.isEmpty() || town.isEmpty() || lat.isNaN() || lon.isNaN() || price.isEmpty()) {
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