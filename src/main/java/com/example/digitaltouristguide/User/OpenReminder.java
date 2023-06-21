package com.example.digitaltouristguide.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Toast;

import com.example.digitaltouristguide.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class OpenReminder extends AppCompatActivity {

	Toolbar toolbar;

	DocumentReference document;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_open_reminder);

		Bundle extras = getIntent().getExtras();
		if(extras != null){
			if(extras.containsKey("open_id"))
			{
				String id = extras.getString("open_id");
				document = FirebaseFirestore.getInstance().collection("events").document(id);

				document.get().addOnSuccessListener(snapshot -> {
					toolbar.setTitle(snapshot.getString("name"));
					toolbar.setSubtitle(snapshot.getString("town"));
				}).addOnFailureListener(e -> {
					Toast.makeText(this, "Pogreška: " + e, Toast.LENGTH_SHORT).show();
				});
			}
		}

		//String id = getIntent().getStringExtra("open_id");
		//document = FirebaseFirestore.getInstance().collection("events").document(id);
		//fetchData();
	}

	private void fetchData() {
		document.get().addOnSuccessListener(snapshot -> {
			toolbar.setTitle(snapshot.getString("name"));
			toolbar.setSubtitle(snapshot.getString("town"));
		}).addOnFailureListener(e -> {
			Toast.makeText(this, "Pogreška: " + e, Toast.LENGTH_SHORT).show();
		});
	}
}