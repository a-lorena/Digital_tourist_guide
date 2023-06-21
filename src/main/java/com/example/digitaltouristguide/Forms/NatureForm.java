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

public class NatureForm extends AppCompatActivity {

	EditText nameET, typeET, townET, latET, lonET, descET;
	ImageView addImageButton, imagePreview;
	Button saveButton;

	ProgressDialog pd;
	Uri imageUri;
	private static final int PICK_IMAGE_REQUEST = 1;

	RequestQueue requestQueue;
	String URL = "https://fcm.googleapis.com/fcm/send";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form_nature);

		requestQueue = Volley.newRequestQueue(this);

		nameET = findViewById(R.id.input_nature_name);
		typeET = findViewById(R.id.input_nature_type);
		townET = findViewById(R.id.input_nature_town);
		latET = findViewById(R.id.input_nature_latitude);
		lonET = findViewById(R.id.input_nature_longitude);
		descET = findViewById(R.id.input_nature_description);
		addImageButton = findViewById(R.id.add_nature_image);
		imagePreview = findViewById(R.id.nature_image_preview);
		saveButton = findViewById(R.id.save_nature_button);

		addImageButton.setOnClickListener(view -> selectImage());
		saveButton.setOnClickListener(view -> getInput());
	}

	private void saveNature(String name, String type, String town, Double lat, Double lon, String desc) {
		pd = new ProgressDialog(this);
		pd.setMessage("Spremanje...");
		pd.show();

		Map<String, Object> nature = new HashMap<>();
		nature.put("name", name);
		nature.put("type", type);
		nature.put("town", town);
		nature.put("latitude", lat);
		nature.put("longitude", lon);
		nature.put("description", desc);
		nature.put("comments", 0);
		nature.put("stars", 0);
		nature.put("rating", 0);

		if (imageUri != null) {
			String imageName = name + "_image";
			StorageReference storage = FirebaseStorage.getInstance().getReference("images_nature/" + imageName);
			storage.putFile(imageUri);
			nature.put("imageName", imageName);
		}

		FirebaseFirestore db = FirebaseFirestore.getInstance();
		db.collection("nature").add(nature).addOnSuccessListener(documentReference -> {
			pd.dismiss();
			Toast.makeText(NatureForm.this, "Lokacija spremljena.", Toast.LENGTH_SHORT).show();

			String id = documentReference.getId();
			sendNotification(id, name, town);
			saveNews(name, "park ili plaža", town);
		}).addOnFailureListener(e -> {
			pd.dismiss();
			Toast.makeText(NatureForm.this, "Spremanje neuspješno, pokušajte kasnije.\n Pogreška: " + e, Toast.LENGTH_SHORT).show();
		});

		finish();
	}

	private void sendNotification(String natureID, String natureName, String town) {
		JSONObject json = new JSONObject();

		try {
			json.put("to", "/topics/" + town);

			JSONObject notificationObj = new JSONObject();
			notificationObj.put("title", town + " ima novu lokaciju!");
			notificationObj.put("body", "Posjetite " + natureName);

			JSONObject extraData = new JSONObject();
			extraData.put("natureID", natureID);
			extraData.put("category", "nature");

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

	private boolean checkEmptyFields(String name, String type, String town, Double lat, Double lon) {
		if (name.isEmpty() || type.isEmpty() || town.isEmpty() || lat.isNaN() || lon.isNaN()) {
			Toast.makeText(this, "Ispunite sva polja!", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	private void getInput() {
		String nameTXT = nameET.getText().toString();
		String typeTXT = typeET.getText().toString();
		String townTXT = townET.getText().toString();
		Double latTXT = Double.parseDouble(latET.getText().toString());
		Double lonTXT = Double.parseDouble(lonET.getText().toString());
		String descTXT = descET.getText().toString();

		if (checkEmptyFields(nameTXT, typeTXT, townTXT, latTXT, lonTXT)) {
			saveNature(nameTXT, typeTXT, townTXT, latTXT, lonTXT, descTXT);
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
}