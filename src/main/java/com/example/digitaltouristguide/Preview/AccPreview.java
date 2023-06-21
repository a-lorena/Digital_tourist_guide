package com.example.digitaltouristguide.Preview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitaltouristguide.Adapters.RatingAdapter;
import com.example.digitaltouristguide.Edit.AccEdit;
import com.example.digitaltouristguide.Models.RatingModel;
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

public class AccPreview extends AppCompatActivity implements OnMapReadyCallback {

	Toolbar toolbar;
	ImageView image;
	TextView showAllComments, descText;
	ExpandableTextView price, description;
	MapView map;
	RecyclerView commentsList;

	DocumentReference document;
	CollectionReference collection;
	RatingAdapter adapter;
	String accID;
	static String accName;
	static Double accLat, accLon;

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;
	public static final String fileName = "login";
	public static final String userType = "type";
	String type;

	private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview_acc);

		sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
		type = sharedPreferences.getString(userType, "user");

		accID = getIntent().getStringExtra("acc_id");
		document = FirebaseFirestore.getInstance().collection("accomodation").document(accID);
		collection = document.collection("Ratings");

		toolbar = findViewById(R.id.acc_preview_toolbar);
		image = findViewById(R.id.acc_preview_image);
		price = findViewById(R.id.acc_preview_price);
		descText = findViewById(R.id.text_description);
		description = findViewById(R.id.acc_preview_description);
		map = findViewById(R.id.map_view);
		commentsList = findViewById(R.id.acc_preview_comments);
		showAllComments = findViewById(R.id.acc_show_all_comments);

		setSupportActionBar(toolbar);
		initMapView(savedInstanceState);
		fetchData();

		showAllComments.setOnClickListener(view -> {
			Intent intent = new Intent(AccPreview.this, RatingsPreview.class);
			intent.putExtra("location_id", accID);
			intent.putExtra("category", "accomodation");
			startActivity(intent);
		});
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
			accName = snapshot.getString("name");

			toolbar.setTitle(snapshot.getString("name"));
			toolbar.setSubtitle(snapshot.getString("town"));
			price.setText(snapshot.getString("price"));
			price.resetState(true);

			if (snapshot.getString("description").isEmpty()) {
				descText.setVisibility(View.GONE);
				description.setVisibility(View.GONE);
			} else {
				description.setText(snapshot.getString("description"));
				description.resetState(true);
			}

		}).addOnFailureListener(e -> {
			Toast.makeText(this, "Pogreška: " + e, Toast.LENGTH_SHORT).show();
			finish();
		});
	}

	private void fetchImage(DocumentSnapshot snapshot) {
		StorageReference storage = FirebaseStorage.getInstance().getReference("images_acc/" + snapshot.getString("imageName"));

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (type.equals("admin")) {
			getMenuInflater().inflate(R.menu.toolbar_menu_admin, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int itemID = item.getItemId();

		switch (itemID) {
			case R.id.toolbar_edit_action:
				Intent intent = new Intent(AccPreview.this, AccEdit.class);
				intent.putExtra("acc_id", accID);
				startActivity(intent);
				return true;

			case R.id.toolbar_delete_action:
				showAlert();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void showAlert() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Pozor!")
				.setMessage("Jeste li sigurni da želite obrisati " + accName + "?")
				.setPositiveButton("DA", (dialogInterface, i) -> deleteAcc())
				.setNegativeButton("NE", (dialogInterface, i) -> {
				});

		alert.create().show();
	}

	private void deleteAcc() {
		ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage("Brisanje...");
		pd.show();

		document.delete().addOnSuccessListener(unused -> {
			pd.dismiss();
			Toast.makeText(AccPreview.this, "Smještaj uspješno obrisan.", Toast.LENGTH_SHORT).show();
			finish();

		}). addOnFailureListener(e -> {
			pd.dismiss();
			Toast.makeText(AccPreview.this, "Brisanje neuspješno. Pokušajte kasnije.", Toast.LENGTH_SHORT).show();
			finish();
		});
	}

	private void setupRecycler() {
		Query query = collection.orderBy("time", Query.Direction.DESCENDING).limit(3);
		FirestoreRecyclerOptions<RatingModel> options = new FirestoreRecyclerOptions.Builder<RatingModel>()
				.setQuery(query, RatingModel.class)
				.build();

		adapter = new RatingAdapter(this, options);
		commentsList.setHasFixedSize(true);
		commentsList.setLayoutManager(new LinearLayoutManager(this));
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
	protected void onResume() {
		super.onResume();
		map.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
		map.onStart();

		setupRecycler();
		adapter.startListening();
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		setupRecycler();
		adapter.startListening();
	}

	@Override
	protected void onStop() {
		super.onStop();
		map.onStop();

		adapter.stopListening();
	}

	@Override
	protected void onPause() {
		map.onPause();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
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
			accLat = snapshot.getDouble("latitude");
			accLon = snapshot.getDouble("longitude");
			LatLng latlng = new LatLng(accLat, accLon);

			googleMap.addMarker(new MarkerOptions().position(latlng).title(accName));
			float zoomLevel = (float) 17.5;
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoomLevel));
			//googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
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
			googleMap.setMyLocationEnabled(true);
			googleMap.setMinZoomPreference(15);

		}).addOnFailureListener(e -> {
			Toast.makeText(AccPreview.this, "Pogreška: " + e, Toast.LENGTH_SHORT).show();
			finish();
		});
	}
}