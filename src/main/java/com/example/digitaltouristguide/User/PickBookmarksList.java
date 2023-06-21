package com.example.digitaltouristguide.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.digitaltouristguide.Adapters.TravelListAdapter;
import com.example.digitaltouristguide.Models.TravelListModel;
import com.example.digitaltouristguide.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class PickBookmarksList extends AppCompatActivity {

	RecyclerView recyclerView;
	FloatingActionButton addBookmarksList;

	Query query;
	FirestoreRecyclerOptions<TravelListModel> options;
	TravelListAdapter adapter;
	CollectionReference collection;
	String locationID, locationName, locationCategory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pick_bookmarks_list);

		locationID = getIntent().getStringExtra("location_id");
		locationName = getIntent().getStringExtra("location_name");
		locationCategory = getIntent().getStringExtra("location_category");

		recyclerView = findViewById(R.id.pick_bookmarks_list_recycler);
		addBookmarksList = findViewById(R.id.pick_bookmarks_list_add_button);

		addBookmarksList.setOnClickListener(view -> { showAddListDialog(); });
	}

	private void showAddListDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.custom_dialog_bookmark, null);
		EditText titleET = view.findViewById(R.id.new_list_name);

		builder.setView(view)
				.setCancelable(false)
				.setTitle("Nova lista")
				.setNegativeButton("Odustani", (dialogInterface, i) -> { })
				.setPositiveButton("Spremi", (dialogInterface, i) -> {
					String title = titleET.getText().toString();
					saveBookmarksList(title);
				});
		builder.create().show();
	}

	private void saveBookmarksList(String title) {
		ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage("Spremanje...");
		pd.show();

		Map<String, Object> list = new HashMap<>();
		list.put("title", title);

		collection.add(list).addOnSuccessListener(documentReference -> {
			pd.dismiss();
			Toast.makeText(this, "Lista dodana.", Toast.LENGTH_SHORT).show();
		}).addOnFailureListener(e -> {
			pd.dismiss();
			Toast.makeText(this, "Pogreška: " + e, Toast.LENGTH_SHORT).show();
		});
	}

	private void initRecyclerView() {
		String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
		collection = FirebaseFirestore.getInstance().collection("users")
						.document(userID).collection("TravelLists");
		query = collection;

		options = new FirestoreRecyclerOptions.Builder<TravelListModel>()
				.setQuery(query, TravelListModel.class)
				.build();

		adapter = new TravelListAdapter(this, options);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(adapter);

		adapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			DocumentReference document = collection.document(documentSnapshot.getId());
			
			Map<String, Object> place = new HashMap<>();
			place.put("listID", documentSnapshot.getId());
			place.put("placeID", locationID);
			place.put("name", locationName);
			place.put("category", locationCategory);
			place.put("visited", false);
			
			document.collection("Places").document(locationID).set(place).addOnSuccessListener(documentReference -> {
				Toast.makeText(context, "Lokacija uspješno zabilježena.", Toast.LENGTH_SHORT).show();
				finish();
			}).addOnFailureListener(e -> {
				Toast.makeText(context, "Pogreška: " + e, Toast.LENGTH_SHORT).show();
				finish();
			});
		});
	}

	@Override
	protected void onStart() {
		super.onStart();

		initRecyclerView();
		adapter.startListening();
	}

	@Override
	protected void onResume() {
		super.onResume();

		initRecyclerView();
		adapter.startListening();
	}

	@Override
	protected void onStop() {
		super.onStop();

		adapter.stopListening();
	}
}