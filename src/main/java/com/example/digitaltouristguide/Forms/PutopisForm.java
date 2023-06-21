package com.example.digitaltouristguide.Forms;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.digitaltouristguide.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class PutopisForm extends AppCompatActivity {

	EditText title, town, text;
	ImageView addPhoto, imagePreview;
	Button saveButton;

	FirebaseFirestore db;
	CollectionReference collection;
	String userID;

	Uri imageUri;
	private static final int PICK_IMAGE_REQUEST = 1;
	StorageReference storage;

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;
	public static final String fileName = "login";
	public static final String userType = "type";
	public static final String pickedTown = "id";
	public static final String pickedTownName = "pickedTownName";
	String townName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form_putopis);

		sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
		townName = sharedPreferences.getString("pickedTownName", "");

		userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
		db = FirebaseFirestore.getInstance();
		collection = db.collection("users").document(userID).collection("Putopisi");
		storage = FirebaseStorage.getInstance().getReference();

		title = findViewById(R.id.putopis_form_title);
		town = findViewById(R.id.putopis_form_town);
		text = findViewById(R.id.putopis_form_text);
		addPhoto = findViewById(R.id.add_putopis_image);
		imagePreview = findViewById(R.id.edit_putopis_image_preview);
		saveButton = findViewById(R.id.putopis_form_save_button);

		getTownName();

		addPhoto.setOnClickListener(view -> selectImage());
		saveButton.setOnClickListener(view -> getInput());
	}

	private void getTownName() {
		town.setText(townName);
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
		String titleTXT = title.getText().toString();
		String townTXT = town.getText().toString();
		String textTXT = text.getText().toString();

		if (checkEmptyFields(titleTXT, townTXT, textTXT)) {
			savePutopis(titleTXT, townTXT, textTXT);
		}
	}

	private boolean checkEmptyFields(String title, String town, String text) {
		if (title.isEmpty() || town.isEmpty() || text.isEmpty()) {
			Toast.makeText(this, "Ispunite sva polja!", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private void savePutopis(String title, String town, String text) {
		ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage("Spremanje...");
		pd.show();

		Map<String, Object> putopis = new HashMap<>();
		putopis.put("title", title);
		putopis.put("town", town);
		putopis.put("text", text);
		putopis.put("author", userID);
		putopis.put("timestamp", Timestamp.now());

		if (imageUri != null) {
			String imageName = title + "_image";
			StorageReference storage = FirebaseStorage.getInstance().getReference("images_putopisi/" + imageName);
			storage.putFile(imageUri);
			putopis.put("imageName", imageName);
		}

		collection.add(putopis).addOnSuccessListener(documentReference -> {
			String putopisID = documentReference.getId();

			db.collection("Putopisi").document(putopisID).set(putopis).addOnSuccessListener(documentReference1 -> {
				pd.dismiss();
				Toast.makeText(PutopisForm.this, "Putopis spremljen.", Toast.LENGTH_SHORT).show();

			}).addOnFailureListener(e -> {
				pd.dismiss();
				Toast.makeText(PutopisForm.this, "Pogreška: " + e, Toast.LENGTH_SHORT).show();
			});

		}).addOnFailureListener(e -> {
			pd.dismiss();
			Toast.makeText(PutopisForm.this, "Pogreška: " + e, Toast.LENGTH_SHORT).show();
		});

		finish();
	}
}