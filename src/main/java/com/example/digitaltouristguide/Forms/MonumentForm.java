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

public class MonumentForm extends AppCompatActivity {

	EditText nameET, typeET, townET, countyET, latET, lonET, workingHoursET, descET;
	ImageView addImageButton, imagePreview;
	Button saveButton;
	ProgressDialog pd;

	FirebaseFirestore db;
	StorageReference storage;

	Uri imageUri;
	private static final int PICK_IMAGE_REQUEST = 1;

	RequestQueue requestQueue;
	String URL = "https://fcm.googleapis.com/fcm/send";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form_location);

		String formType = getIntent().getStringExtra("form_type");
		db = FirebaseFirestore.getInstance();
		requestQueue = Volley.newRequestQueue(this);

		nameET = findViewById(R.id.input_location_name);
		typeET = findViewById(R.id.input_location_type);
		townET = findViewById(R.id.input_location_town);
		countyET = findViewById(R.id.input_location_county);
		latET = findViewById(R.id.input_location_latitude);
		lonET = findViewById(R.id.input_location_longitude);
		workingHoursET = findViewById(R.id.input_working_hours);
		descET = findViewById(R.id.input_location_description);
		addImageButton = findViewById(R.id.add_event_image);
		imagePreview = findViewById(R.id.event_image_preview);
		saveButton = findViewById(R.id.save_location_button);

		if (formType.equals("monument")) {
			typeET.setVisibility(View.GONE);
			countyET.setVisibility(View.GONE);
			workingHoursET.setVisibility(View.GONE);
		}

		addImageButton.setOnClickListener(view -> selectImage());
		saveButton.setOnClickListener(view -> { getInput(); });
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
		String nameTXT = nameET.getText().toString();
		String townTXT = townET.getText().toString();
		Double latTXT = Double.parseDouble(latET.getText().toString());
		Double lonTXT = Double.parseDouble(lonET.getText().toString());
		String descTXT = descET.getText().toString();

		if (checkEmptyFields(nameTXT, townTXT, latTXT, lonTXT)) {
			saveInstitution(nameTXT, townTXT, latTXT, lonTXT, descTXT);
		}
	}

	private boolean checkEmptyFields(String name, String town, Double lat, Double lon) {
		if (name.isEmpty() || town.isEmpty() || lat.isNaN() || lon.isNaN()) {
			Toast.makeText(this, "Ispunite sva polja.", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	private void saveInstitution(String name, String town, Double lat, Double lon, String desc) {
		pd = new ProgressDialog(this);
		pd.setMessage("Spremanje...");
		pd.show();

		Map<String, Object> monument = new HashMap<>();
		monument.put("name", name);
		monument.put("town", town);
		monument.put("latitude", lat);
		monument.put("longitude", lon);
		monument.put("description", desc);
		monument.put("comments", 0);
		monument.put("stars", 0);
		monument.put("rating", 0);

		if (imageUri != null) {
			String imageName = name + "_image";
			storage = FirebaseStorage.getInstance().getReference("images_monuments/" + imageName);
			storage.putFile(imageUri);
			monument.put("imageName", imageName);
		}

		db.collection("monuments").add(monument).addOnSuccessListener(documentReference -> {
			pd.dismiss();
			Toast.makeText(MonumentForm.this, "Spomenik je spremljen.", Toast.LENGTH_SHORT).show();

			String id = documentReference.getId();
			sendNotification(id, name, town);
			saveNews(name, "spomenik", town);

		}).addOnFailureListener(e -> {
			pd.dismiss();
			Toast.makeText(MonumentForm.this, "Spremanje neuspješno, pokušajte kasnije.\n Pogreška: " + e, Toast.LENGTH_SHORT).show();
		});

		finish();
	}

	private void sendNotification(String monumentID, String monumentName, String town) {
		JSONObject json = new JSONObject();

		try {
			json.put("to", "/topics/" + town);

			JSONObject notificationObj = new JSONObject();
			notificationObj.put("title", town + " ima novi spomenik!");
			notificationObj.put("body", "Posjetite " + monumentName);

			JSONObject extraData = new JSONObject();
			extraData.put("monumentID", monumentID);
			extraData.put("category", "monument");

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
}