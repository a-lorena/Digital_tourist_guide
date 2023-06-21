package com.example.digitaltouristguide.User;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitaltouristguide.Adapters.RatingAdapter;
import com.example.digitaltouristguide.Models.RatingModel;
import com.example.digitaltouristguide.Preview.RatingsPreview;
import com.example.digitaltouristguide.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import ru.embersoft.expandabletextview.ExpandableTextView;

public class HomeInfoTab extends Fragment implements OnMapReadyCallback {

	ImageView image;
	ExpandableTextView description;
	TextView showAllComments, descText;
	MapView map;
	RecyclerView commentsList;

	DocumentReference document;
	CollectionReference collection;
	RatingAdapter adapter;
	StorageReference storage;
	String townID;
	static String townName, oldTown;
	static Double townLat, townLon;

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;
	public static final String fileName = "login";
	public static final String userType = "type";
	public static final String pickedTown = "id";
	public static final String pickedTownName = "pickedTownName";

	private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home_info, container, false);

		sharedPreferences = requireActivity().getSharedPreferences(fileName, Context.MODE_PRIVATE);
		townID = sharedPreferences.getString(pickedTown, "");
		oldTown = sharedPreferences.getString("pickedTownName", "");

		document = FirebaseFirestore.getInstance().collection("cities").document(townID);
		collection = document.collection("Ratings");

		image = view.findViewById(R.id.town_preview_image);
		descText = view.findViewById(R.id.text_description);
		description = view.findViewById(R.id.town_preview_description);
		map = view.findViewById(R.id.map_view);
		commentsList = view.findViewById(R.id.town_preview_comments);
		showAllComments = view.findViewById(R.id.town_show_all_comments);

		description.resetState(true);
		initMapView(savedInstanceState);
		fetchData();

		showAllComments.setOnClickListener(v -> {
			Intent intent = new Intent(getContext(), RatingsPreview.class);
			intent.putExtra("location_id", townID);
			intent.putExtra("category", "cities");
			startActivity(intent);
		});

		return view;
	}

	private void initMapView(Bundle savedInstanceState) {
		Bundle mapViewBundle = null;
		if (savedInstanceState != null) {
			mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
		}
		map.onCreate(mapViewBundle);
		map.getMapAsync(this);
	}

	private void fetchData() {
		document.get().addOnSuccessListener(snapshot -> {
			fetchImage(snapshot);
			townName = snapshot.getString("name");

			if (snapshot.getString("description").isEmpty()) {
				descText.setVisibility(View.GONE);
				description.setVisibility(View.GONE);
			} else {
				description.setText(snapshot.getString("description"));
				description.resetState(true);
			}

		}).addOnFailureListener(e -> {
			Toast.makeText(getContext(), "Pogreška: " + e, Toast.LENGTH_SHORT).show();
		});
	}

	private void fetchImage(DocumentSnapshot snapshot) {
		storage = FirebaseStorage.getInstance().getReference("images/" + snapshot.getString("imageName"));

		try {
			File file = File.createTempFile("tempfile", ".jpeg");
			storage.getFile(file).addOnSuccessListener(taskSnapshot -> {
				Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
				image.setImageBitmap(bitmap);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setupRecycler() {
		Query query = collection.orderBy("time", Query.Direction.DESCENDING).limit(3);
		FirestoreRecyclerOptions<RatingModel> options = new FirestoreRecyclerOptions.Builder<RatingModel>()
				.setQuery(query, RatingModel.class)
				.build();

		adapter = new RatingAdapter(getContext(), options);
		commentsList.setHasFixedSize(true);
		commentsList.setLayoutManager(new LinearLayoutManager(getContext()));
		commentsList.setAdapter(adapter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
		if (mapViewBundle == null) {
			mapViewBundle = new Bundle();
			outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
		}

		map.onSaveInstanceState(mapViewBundle);
	}

	@Override
	public void onStart() {
		super.onStart();
		map.onStart();

		setupRecycler();
		adapter.startListening();
	}

	@Override
	public void onResume() {
		super.onResume();
		map.onResume();

		setupRecycler();
		adapter.startListening();
	}

	@Override
	public void onStop() {
		super.onStop();
		map.onStop();

		adapter.stopListening();
	}

	@Override
	public void onPause() {
		map.onPause();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		map.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		map.onLowMemory();
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		document.get().addOnSuccessListener(snapshot -> {
			townLat = snapshot.getDouble("latitude");
			townLon = snapshot.getDouble("longitude");
			LatLng latlng = new LatLng(townLat, townLon);

			googleMap.addMarker(new MarkerOptions().position(latlng).title(townName));
			float zoomLevel = (float) 8.0; //This goes up to 21
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoomLevel));
			//googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
			if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			googleMap.setMyLocationEnabled(true);
			googleMap.setMinZoomPreference(8);

		}).addOnFailureListener(e -> {
			Toast.makeText(getContext(), "Pogreška: " + e, Toast.LENGTH_SHORT).show();
		});
	}
}