package com.example.digitaltouristguide.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.digitaltouristguide.R;
import com.example.digitaltouristguide.Service.GeofenceBroadcastReceiver;
import com.example.digitaltouristguide.Service.GeofenceHelper;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WalkActivity extends AppCompatActivity implements OnMapReadyCallback {

	SupportMapFragment mapFragment;
	private GoogleMap map;
	private static List<Geofence> geofenceList;
	private List<LatLng> latLngList;

	private GeofencingClient geofencingClient;
	private GeofenceHelper geofenceHelper;
	private PendingIntent geofencePendingIntent;

	private final int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
	private final float GEOFENCE_RADIUS = 20;
	private static final String TAG = "WalkActivity";

	static Double lat, lng;
	Button startWalkButton;

	String category;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_walk);

		category = getIntent().getStringExtra("category");
		startWalkButton = findViewById(R.id.start_walk_button);

		geofencingClient = LocationServices.getGeofencingClient(this);
		geofenceHelper = new GeofenceHelper(this);
		geofenceList = new ArrayList<>();
		latLngList = new ArrayList<>();
		mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.walk_map);

		if (mapFragment != null) { mapFragment.getMapAsync(this); }
		fetchData(category);

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}

		startWalkButton.setOnClickListener(view -> {
			if (ActivityCompat.checkSelfPermission( getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}

			geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
					.addOnSuccessListener(aVoid -> Toast.makeText(geofenceHelper, "Geofences added.", Toast.LENGTH_SHORT).show())
					.addOnFailureListener(e -> Toast.makeText(geofenceHelper, "" + e, Toast.LENGTH_SHORT).show());
		});
	}

	@Override
	public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
		map = googleMap;
		enableUserLocation();
	}

	private void enableUserLocation() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
			map.setMyLocationEnabled(true);
		} else {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
			} else {
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
			}
		}
	}

	@SuppressLint({"MissingSuperCall", "MissingPermission"})
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				map.setMyLocationEnabled(true);
			} else {
			}
		}
	}

	private void fetchData(String category) {
		CollectionReference collection = FirebaseFirestore.getInstance().collection(category);

		collection.get().addOnSuccessListener(queryDocumentSnapshots -> {
			for (DocumentSnapshot document : queryDocumentSnapshots) {
				lat = document.getDouble("latitude");
				lng = document.getDouble("longitude");
				String name = document.getString("name");
				LatLng latLng = new LatLng(lat, lng);

				latLngList.add(latLng);

				addMarker(latLng, name);
				addCircle(latLng, GEOFENCE_RADIUS);

				Random random = new Random();
				int rand = random.nextInt(1000);
				String geofenceID = "GEOFENCE_REQUEST_" + rand;

				geofenceList.add(new Geofence.Builder()
						.setRequestId("geofenceID"+geofenceID)
						.setCircularRegion(
								lat,
								lng,
								GEOFENCE_RADIUS
						)
						.setExpirationDuration(Geofence.NEVER_EXPIRE)
						.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
						.build());
			}
		});
	}

	private GeofencingRequest getGeofencingRequest() {
		GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
		builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
		builder.addGeofences(geofenceList);
		return builder.build();
	}

	private PendingIntent getGeofencePendingIntent() {
		if (geofencePendingIntent != null) {
			return geofencePendingIntent;
		}
		Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
		intent.putExtra("title", "Željena lokacija u blizini!");
		intent.putExtra("message", "Nalazite se na 20 metara od željene lokacije.");

		geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		return geofencePendingIntent;
	}

	private void addMarker(LatLng latLng, String name) {
		MarkerOptions marker = new MarkerOptions()
				.position(latLng)
				.title(name);

		map.addMarker(marker);
	}

	private void addCircle(LatLng latLng, float radius) {
		CircleOptions circle = new CircleOptions()
				.center(latLng)
				.radius(radius)
				.strokeColor(Color.argb(255, 12, 112, 61))
				.strokeWidth(4)
				.fillColor(Color.argb(50, 12, 112, 61));

		map.addCircle(circle);
	}

	/*
	private void addGeofences() {

		GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofenceList);
		PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		geofencingClient.addGeofences(geofencingRequest, pendingIntent)
				.addOnSuccessListener(unused -> Toast.makeText(geofenceHelper, "Geofences added", Toast.LENGTH_SHORT).show())
				.addOnFailureListener(e -> {
					Toast.makeText(geofenceHelper, "Failed: " + e, Toast.LENGTH_SHORT).show();
				});
	}
	*/
}