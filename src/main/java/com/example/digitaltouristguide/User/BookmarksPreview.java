package com.example.digitaltouristguide.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.digitaltouristguide.Adapters.TravelListPlacesAdapter;
import com.example.digitaltouristguide.Models.TravelListPlaceModel;
import com.example.digitaltouristguide.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

public class BookmarksPreview extends AppCompatActivity {

	Toolbar toolbar;
	RecyclerView placesList;

	private String listID, listName;

	TravelListPlacesAdapter adapter;
	CollectionReference collection;
	DocumentReference document;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview_bookmarks);

		listID = getIntent().getStringExtra("list_id");
		listName = getIntent().getStringExtra("list_title");

		String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
		document = FirebaseFirestore.getInstance()
				.collection("users").document(userID)
				.collection("TravelLists").document(listID);

		toolbar = findViewById(R.id.list_toolbar);
		placesList = findViewById(R.id.list_places);

		toolbar.setTitle(listName);
		setSupportActionBar(toolbar);
	}

	private void setupRecyclerView() {
		Query query = document.collection("Places");

		FirestoreRecyclerOptions<TravelListPlaceModel> options = new FirestoreRecyclerOptions.Builder<TravelListPlaceModel>()
				.setQuery(query, TravelListPlaceModel.class)
				.build();

		adapter = new TravelListPlacesAdapter(this, options);

		placesList.setHasFixedSize(true);
		placesList.setLayoutManager(new LinearLayoutManager(this));
		placesList.setAdapter(adapter);

		new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
			@Override
			public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) { return false; }
			@Override
			public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
				adapter.deletePlace(viewHolder.getAdapterPosition());
			}
		}).attachToRecyclerView(placesList);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.toolbar_menu_travel_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int itemID = item.getItemId();
		if (itemID == R.id.toolbar_delete_list) {
			showAlert();
		}
		return super.onOptionsItemSelected(item);
	}

	private void showAlert() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Pozor!")
				.setMessage("Jeste li sigurni da želite obrisati listu " + listName + "?")
				.setPositiveButton("DA", (dialogInterface, i) -> deleteList())
				.setNegativeButton("NE", (dialogInterface, i) -> { });

		alert.create().show();
	}

	private void deleteList() {
		ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage("Brisanje...");
		pd.show();

		document.delete().addOnSuccessListener(unused -> {
			pd.dismiss();
			Toast.makeText(BookmarksPreview.this, "Lista je uspješno obrisana.", Toast.LENGTH_SHORT).show();
			finish();
		}). addOnFailureListener(e -> {
			pd.dismiss();
			Toast.makeText(BookmarksPreview.this, "Brisanje neuspješno. Pokušajte kasnije.", Toast.LENGTH_SHORT).show();
			finish();
		});
	}

	@Override
	protected void onStart() {
		super.onStart();

		setupRecyclerView();
		adapter.startListening();
	}

	@Override
	protected void onResume() {
		super.onResume();

		setupRecyclerView();
		adapter.startListening();
	}

	@Override
	protected void onStop() {
		super.onStop();

		adapter.stopListening();
	}
}