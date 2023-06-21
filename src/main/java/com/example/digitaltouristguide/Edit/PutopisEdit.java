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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class PutopisEdit extends AppCompatActivity {

	EditText title, town, text;
	ImageView addPhoto, imagePreview;
	Button saveButton;

	FirebaseFirestore db;
	CollectionReference collection;
	DocumentReference document;
	String userID, putopisID;

	Uri imageUri;
	private static final int PICK_IMAGE_REQUEST = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_putopis);

		putopisID = getIntent().getStringExtra("putopis_id");
		userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
		db = FirebaseFirestore.getInstance();
		document = db.collection("Putopisi").document(putopisID);

		title = findViewById(R.id.edit_putopis_title);
		town = findViewById(R.id.edit_putopis_town);
		text = findViewById(R.id.edit_putopis_text);
		addPhoto = findViewById(R.id.edit_putopis_image);
		imagePreview = findViewById(R.id.edit_putopis_image_preview);
		saveButton = findViewById(R.id.edit_putopis_save_button);

		fetchData();

		addPhoto.setOnClickListener(view -> selectImage());
		saveButton.setOnClickListener(view -> getInput());
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

	private void fetchData() {
		document.get().addOnSuccessListener(snapshot -> {
			getImage(snapshot);
			title.setText(snapshot.getString("title"));
			town.setText(snapshot.getString("town"));
			text.setText(snapshot.getString("text"));
		}).addOnFailureListener(e -> Log.d("FETCH_FAILURE", e.toString()));
	}

	private void getImage(DocumentSnapshot snapshot) {
		StorageReference storage = FirebaseStorage.getInstance().getReference("images_putopisi/" + snapshot.getString("imageName"));

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

		document.update(
				"title", title,
				"town", town,
				"text", text
		).addOnSuccessListener(unused -> {
			if (imageUri != null) {
				String imageName = title + "_image";
				StorageReference storage = FirebaseStorage.getInstance().getReference("images_putopisi/" + imageName);
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
			Log.d("UPDATE_FAILURE", e.toString());
		});

		finish();
	}
}