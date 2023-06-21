package com.example.digitaltouristguide.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitaltouristguide.Adapters.TownAdapter;
import com.example.digitaltouristguide.Forms.TownForm;
import com.example.digitaltouristguide.Models.TownModel;
import com.example.digitaltouristguide.Preview.TownPreview;
import com.example.digitaltouristguide.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TownAdminFragment extends Fragment {

	EditText search;
	RecyclerView list;
	FloatingActionButton addButton;

	FirebaseFirestore db;
	CollectionReference collection;
	Query query;
	TownAdapter adapter;
	FirestoreRecyclerOptions<TownModel> options;
	private final static String typeOfRecycler = "vertical";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_admin_town, container, false);

		search = view.findViewById(R.id.search_town_admin);
		list = view.findViewById(R.id.town_list_admin);
		addButton = view.findViewById(R.id.add_town_button);

		db = FirebaseFirestore.getInstance();
		collection = db.collection("cities");

		search.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

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

		addButton.setOnClickListener(v -> {
			Intent intent = new Intent(getContext(), TownForm.class);
			intent.putExtra("form_type", "town");
			startActivity(intent);
		});

		return view;
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

		adapter = new TownAdapter(typeOfRecycler, getContext(), options);
		list.setHasFixedSize(true);
		list.setLayoutManager(new LinearLayoutManager(getContext()));
		list.setAdapter(adapter);

		adapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			String locationID = documentSnapshot.getId();

			Intent intent = new Intent(getContext(), TownPreview.class);
			intent.putExtra("location_type", "town");
			intent.putExtra("location_id", locationID);
			intent.putExtra("previewType", "admin");
			startActivity(intent);
		});
	}
}