package com.example.digitaltouristguide.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.digitaltouristguide.Adapters.TownAdapter;
import com.example.digitaltouristguide.Models.TownModel;
import com.example.digitaltouristguide.Preview.TownPreview;
import com.example.digitaltouristguide.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class TownUser extends AppCompatActivity {

	EditText search;
	RecyclerView list;
	ImageView getLocation;

	FirebaseFirestore db;
	CollectionReference collection;
	Query query;
	TownAdapter adapter;
	FirestoreRecyclerOptions<TownModel> options;
	private final static String typeOfRecycler = "vertical";

	FusedLocationProviderClient fusedLocationProviderClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_town);

		search = findViewById(R.id.search_town_user);
		list = findViewById(R.id.town_list_user);
		getLocation = findViewById(R.id.town_get_location);

		db = FirebaseFirestore.getInstance();
		collection = db.collection("cities");

		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

		search.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				if (editable.toString().isEmpty()) {
					query = collection;
				} else {
					query = collection.whereEqualTo("name", editable.toString());
				}

				options = new FirestoreRecyclerOptions.Builder<TownModel>()
						.setQuery(query, TownModel.class)
						.build();

				adapter.updateOptions(options);
			}
		});

		getLocation.setOnClickListener(view -> {
			if (ActivityCompat.checkSelfPermission(TownUser.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
				getLocation();

			} else {
				ActivityCompat.requestPermissions(TownUser.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

			}
		});
	}

	private void getLocation() {
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
			Location location = task.getResult();
			if (location != null) {
				Geocoder geocoder = new Geocoder(TownUser.this, Locale.getDefault());
				try {
					List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
					search.setText(addresses.get(0).getLocality());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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

	private void setupRecyclerView() {
		query = collection;

		options = new FirestoreRecyclerOptions.Builder<TownModel>()
				.setQuery(query, TownModel.class)
				.build();

		adapter = new TownAdapter(typeOfRecycler, this, options);
		list.setHasFixedSize(true);
		list.setLayoutManager(new LinearLayoutManager(this));
		list.setAdapter(adapter);

		adapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			String locationID = documentSnapshot.getId();

			Intent intent = new Intent(this, TownPreview.class);
			intent.putExtra("location_type", "town");
			intent.putExtra("location_id", locationID);
			intent.putExtra("previewType", "pickTown");
			startActivity(intent);
		});
	}
}