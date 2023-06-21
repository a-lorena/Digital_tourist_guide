package com.example.digitaltouristguide.Admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.digitaltouristguide.Adapters.MonumentAdapter;
import com.example.digitaltouristguide.Forms.MonumentForm;
import com.example.digitaltouristguide.Models.MonumentModel;
import com.example.digitaltouristguide.Preview.MonumentPreview;
import com.example.digitaltouristguide.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MonumentsAdminFragment extends Fragment {

	EditText search;
	RecyclerView list;
	FloatingActionButton addButton;

	FirebaseFirestore db;
	CollectionReference collection;
	Query query;
	MonumentAdapter adapter;
	FirestoreRecyclerOptions<MonumentModel> options;
	private final static String recyclerOrientation = "vertical";

	SharedPreferences sharedPreferences;
	public static final String fileName = "login";
	public static final String userType = "type";
	String type;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_admin_monument, container, false);

		sharedPreferences = this.getActivity().getSharedPreferences(fileName, Context.MODE_PRIVATE);
		type = sharedPreferences.getString(userType, "user");

		db = FirebaseFirestore.getInstance();
		collection = db.collection("monuments");

		search = view.findViewById(R.id.search_monuments_admin);
		list = view.findViewById(R.id.monuments_list_admin);
		addButton = view.findViewById(R.id.add_monuments_button);

		if (type.equals("user")) addButton.setVisibility(View.INVISIBLE);

		search.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
			@Override
			public void afterTextChanged(Editable editable) { searchByName(editable); }
		});

		addButton.setOnClickListener(v -> {
			Intent intent = new Intent(getContext(), MonumentForm.class);
			intent.putExtra("form_type", "monument");
			startActivity(intent);
		});

		return view;
	}

	private void searchByName(Editable editable) {
		if (editable.toString().isEmpty()) {
			query = collection;
		} else {
			query = collection.whereEqualTo("name", editable.toString());
		}

		options = new FirestoreRecyclerOptions.Builder<MonumentModel>()
				.setQuery(query, MonumentModel.class)
				.build();

		adapter.updateOptions(options);
	}

	private void setupRecyclerView() {
		query = collection;

		options = new FirestoreRecyclerOptions.Builder<MonumentModel>()
				.setQuery(query, MonumentModel.class)
				.build();

		adapter = new MonumentAdapter(recyclerOrientation, getContext(), options);
		list.setHasFixedSize(true);
		list.setLayoutManager(new LinearLayoutManager(getContext()));
		list.setAdapter(adapter);

		adapter.setOnItemClickListener((context, snapshot, position) -> {
			String monumentID = snapshot.getId();
			String monumentName = snapshot.getString("name");

			Intent intent = new Intent(getContext(), MonumentPreview.class);
			intent.putExtra("monument_id", monumentID);
			intent.putExtra("monument_name", monumentName);
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