package com.example.digitaltouristguide.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitaltouristguide.Adapters.PutopisAdapter;
import com.example.digitaltouristguide.Models.PutopisModel;
import com.example.digitaltouristguide.Preview.PutopisPreview;
import com.example.digitaltouristguide.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

	ImageView headerImage, profileImage;
	TextView name;
	RecyclerView putopisiList;

	StorageReference storageHeader, storageProfile;
	String userID;
	CollectionReference collection;
	PutopisAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		userID = getIntent().getStringExtra("user_id");
		collection = FirebaseFirestore.getInstance().collection("Putopisi");

		headerImage = findViewById(R.id.profile_header_image);
		profileImage = findViewById(R.id.profile_profile_image);
		name = findViewById(R.id.profile_name);
		putopisiList = findViewById(R.id.user_putopisi);

		getData();
	}

	private void getData() {
		DocumentReference document = FirebaseFirestore.getInstance().collection("users").document(userID);

		document.get().addOnSuccessListener(snapshot -> {
			getImage(snapshot);
			name.setText(snapshot.getString("name"));
		}).addOnFailureListener(e -> {
			Toast.makeText(this, "Pogreška: " + e, Toast.LENGTH_SHORT).show();
		});
	}

	private void getImage(DocumentSnapshot snapshot) {
		storageHeader = FirebaseStorage.getInstance().getReference("users/" + snapshot.getString("header"));
		storageProfile = FirebaseStorage.getInstance().getReference("users/" + snapshot.getString("profile"));

		try {
			File file = File.createTempFile("tempfile", ".jpeg");
			storageHeader.getFile(file).addOnSuccessListener(taskSnapshot -> {
				Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
				headerImage.setImageBitmap(bitmap);
			});
			storageProfile.getFile(file).addOnSuccessListener(taskSnapshot -> {
				Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
				profileImage.setImageBitmap(bitmap);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setupRecyclerView() {
		Query query = collection.whereEqualTo("author", userID)
				.orderBy("timestamp", Query.Direction.DESCENDING);

		FirestoreRecyclerOptions<PutopisModel> options = new FirestoreRecyclerOptions.Builder<PutopisModel>()
				.setQuery(query, PutopisModel.class)
				.build();

		adapter = new PutopisAdapter(this, options);
		putopisiList.setHasFixedSize(true);
		putopisiList.setLayoutManager(new LinearLayoutManager(this));
		putopisiList.setAdapter(adapter);

		adapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			Intent intent = new Intent(this, PutopisPreview.class);
			intent.putExtra("putopis_id", documentSnapshot.getId());
			intent.putExtra("putopis_author", documentSnapshot.getString("author"));
			startActivity(intent);
		});
	}

	@Override
	public void onStart() {
		super.onStart();

		setupRecyclerView();
		adapter.startListening();
	}

	@Override
	public void onResume() {
		super.onResume();

		setupRecyclerView();
		adapter.startListening();
	}

	@Override
	public void onStop() {
		super.onStop();

		adapter.stopListening();
	}
}