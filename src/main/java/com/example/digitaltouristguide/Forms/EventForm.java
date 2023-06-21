package com.example.digitaltouristguide.Forms;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.digitaltouristguide.R;
import com.example.digitaltouristguide.Service.DatePickerFragment;
import com.example.digitaltouristguide.Service.TimePickerFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EventForm extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

	EditText nameET, townET, locationET, dateStartET, dateEndET, startET, endET, descET;
	ImageView addImageButton, imagePreview;
	Button saveButton;
	ProgressDialog pd;

	EditText clickedET;

	FirebaseFirestore db;
	StorageReference storage;

	Uri imageUri;
	private static final int PICK_IMAGE_REQUEST = 1;

	RequestQueue requestQueue;
	String URL = "https://fcm.googleapis.com/fcm/send";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form_event);

		nameET = findViewById(R.id.input_event_name);
		townET = findViewById(R.id.input_event_town);
		locationET = findViewById(R.id.input_event_location);
		dateStartET = findViewById(R.id.input_event_date_start);
		dateEndET = findViewById(R.id.input_event_date_end);
		startET = findViewById(R.id.input_event_time_start);
		endET = findViewById(R.id.input_event_time_end);
		descET = findViewById(R.id.input_event_description);
		addImageButton = findViewById(R.id.add_event_image);
		imagePreview = findViewById(R.id.event_image_preview);
		saveButton = findViewById(R.id.save_event_button);

		db = FirebaseFirestore.getInstance();
		requestQueue = Volley.newRequestQueue(this);

		dateStartET.setOnClickListener(view -> {
			clickedET = (EditText) view;
			pickDate();
		});

		dateEndET.setOnClickListener(view -> {
			clickedET = (EditText) view;
			pickDate();
		});

		startET.setOnClickListener(view -> {
			clickedET = (EditText) view;
			pickTime();
		});

		endET.setOnClickListener(view -> {
			clickedET = (EditText) view;
			pickTime();
		});

		addImageButton.setOnClickListener(view -> selectImage());
		saveButton.setOnClickListener(view -> { getInput(); });
	}

	private void pickDate() {
		DialogFragment datePicker = new DatePickerFragment();
		datePicker.show(getSupportFragmentManager(), "Date picker");
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		month++;
		String d = String.valueOf(day);
		String m = String.valueOf(month);

		if (day < 10) { d = "0" + day; }
		if (month < 10) { m = "0" + month; }

		String date = d + "." + m + "." + year + ".";
		clickedET.setText(date);
	}

	private void pickTime() {
		DialogFragment timePicker = new TimePickerFragment();
		timePicker.show(getSupportFragmentManager(), "Time picker");
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		String hours = String.valueOf(hourOfDay);
		String minutes = String.valueOf(minute);

		if (hourOfDay < 10) { hours = "0" + hourOfDay; }
		if (minute < 10) { minutes = "0" + minute; }

		String time = hours + ":" + minutes;
		clickedET.setText(time);
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
		String locationTXT = locationET.getText().toString();
		String dateStartTXT = dateStartET.getText().toString();
		String dateEndTXT = dateEndET.getText().toString();
		String startTXT = startET.getText().toString();
		String endTXT = endET.getText().toString();
		String descTXT = descET.getText().toString();

		if (checkEmptyFields(nameTXT, townTXT, locationTXT, dateStartTXT, startTXT)) {
			saveEvent(nameTXT, townTXT, locationTXT, dateStartTXT, dateEndTXT, startTXT, endTXT, descTXT);
		}
	}

	private boolean checkEmptyFields(String name, String town, String location, String dateStart, String start) {
		if (name.isEmpty() || town.isEmpty() || location.isEmpty() || dateStart.isEmpty() || start.isEmpty()) {
			Toast.makeText(this, "Ispunite sva polja!", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	private void saveEvent(String name, String town, String location, String dateStart,
						   String dateEnd, String start, String end, String desc) {
		pd = new ProgressDialog(this);
		pd.setMessage("Spremanje...");
		pd.show();

		Map<String, Object> event = new HashMap<>();
		event.put("name", name);
		event.put("town", town);
		event.put("location", location);
		event.put("dateStart", dateStart);
		event.put("dateEnd", dateEnd);
		event.put("start", start);
		event.put("end", end);
		event.put("description", desc);
		event.put("comments", 0);
		event.put("stars", 0);
		event.put("rating", 0);

		if (imageUri != null) {
			String imageName = name + "_image";
			storage = FirebaseStorage.getInstance().getReference("images_events/" + imageName);
			storage.putFile(imageUri);
			event.put("imageName", imageName);
		}

		db.collection("events").add(event).addOnSuccessListener(documentReference -> {
			pd.dismiss();
			Toast.makeText(EventForm.this, "Događaj je spremljen.", Toast.LENGTH_SHORT).show();

			String id = documentReference.getId();
			sendNotification(id, name, town);
			saveNews(name, "događaj", town);

		}).addOnFailureListener(e -> {
			pd.dismiss();
			Toast.makeText(EventForm.this, "Spremanje neuspješno, pokušajte kasnije.\n Pogreška: " + e, Toast.LENGTH_SHORT).show();
		});

		finish();
	}

	private void sendNotification(String eventID, String eventName, String town) {
		JSONObject json = new JSONObject();

		try {
			json.put("to", "/topics/" + town);

			JSONObject notificationObj = new JSONObject();
			notificationObj.put("title", town + " ima novi događaj!");
			notificationObj.put("body", "Istražite " + eventName);

			JSONObject extraData = new JSONObject();
			extraData.put("eventID", eventID);
			extraData.put("category", "event");

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