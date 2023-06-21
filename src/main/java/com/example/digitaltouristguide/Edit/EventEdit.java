package com.example.digitaltouristguide.Edit;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import com.example.digitaltouristguide.R;
import com.example.digitaltouristguide.Service.DatePickerFragment;
import com.example.digitaltouristguide.Service.TimePickerFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class EventEdit extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

	EditText nameET, townET, locationET, dateStartET, dateEndET, startET, endET, descET;
	ImageView addImageButton, imagePreview;
	Button saveButton;

	EditText clickedET;

	Uri imageUri;
	private static final int PICK_IMAGE_REQUEST = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_event);

		String eventID = getIntent().getStringExtra("event_id");
		DocumentReference document = FirebaseFirestore.getInstance().collection("events").document(eventID);

		nameET = findViewById(R.id.edit_event_name);
		townET = findViewById(R.id.edit_event_town);
		locationET = findViewById(R.id.edit_event_location);
		dateStartET = findViewById(R.id.edit_event_date_start);
		dateEndET = findViewById(R.id.edit_event_date_end);
		startET = findViewById(R.id.edit_event_time_start);
		endET = findViewById(R.id.edit_event_time_end);
		descET = findViewById(R.id.edit_event_description);
		addImageButton = findViewById(R.id.edit_event_image);
		imagePreview = findViewById(R.id.edit_event_image_preview);
		saveButton = findViewById(R.id.edit_event_save_button);

		fetchData(document);

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
		saveButton.setOnClickListener(view -> getInput(document));
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

	private void fetchData(DocumentReference document) {
		document.get().addOnSuccessListener(snapshot -> {
			fetchImage(snapshot.getString("imageName"));

			nameET.setText(snapshot.getString("name"));
			townET.setText(snapshot.getString("town"));
			locationET.setText(snapshot.getString("location"));
			dateStartET.setText(snapshot.getString("dateStart"));
			dateEndET.setText(snapshot.getString("dateEnd"));
			startET.setText(snapshot.getString("start"));
			endET.setText(snapshot.getString("end"));
			descET.setText(snapshot.getString("description"));

		}).addOnFailureListener(e -> {
			Toast.makeText(this, "Pogreška: " + e, Toast.LENGTH_SHORT).show();
			finish();
		});
	}

	private void fetchImage(String imageName) {
		StorageReference storage = FirebaseStorage.getInstance().getReference("images_events/" + imageName);

		try {
			File file = File.createTempFile("tempfile", ".jpeg");
			storage.getFile(file).addOnSuccessListener(taskSnapshot -> {
				Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
				imagePreview.setImageBitmap(bitmap);
				imagePreview.setVisibility(View.VISIBLE);
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

	private void getInput(DocumentReference document) {
		String name = nameET.getText().toString();
		String town = townET.getText().toString();
		String location = locationET.getText().toString();
		String dateStart = dateStartET.getText().toString();
		String dateEnd = dateEndET.getText().toString();
		String start = startET.getText().toString();
		String end = endET.getText().toString();
		String desc = descET.getText().toString();

		if (checkEmptyFields(name, town, location, dateStart, start)) {
			updateEvent(document, name, town, location, dateStart, dateEnd, start, end, desc);
		}
	}

	private boolean checkEmptyFields(String name, String town, String location, String dateStart, String start) {
		if (name.isEmpty() || town.isEmpty() || location.isEmpty() || dateStart.isEmpty() || start.isEmpty()) {
			Toast.makeText(this, "Ispunite sva polja.", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private void updateEvent(DocumentReference document, String name, String town, String location,
							 String dateStart, String dateEnd, String start, String end, String desc) {
		ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage("Spremanje...");
		pd.show();

		document.update(
				"name", name,
				"town", town,
				"location", location,
				"dateStart", dateStart,
				"dateEnd", dateEnd,
				"start", start,
				"end", end,
				"description", desc
		).addOnSuccessListener(unused -> {
			if (imageUri != null) {
				String imageName = name + "_image";
				StorageReference storage = FirebaseStorage.getInstance().getReference("images_events/" + imageName);
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