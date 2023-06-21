package com.example.digitaltouristguide.UserFragments;

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
import android.widget.TextView;

import com.example.digitaltouristguide.Adapters.InstitutionAdapter;
import com.example.digitaltouristguide.Models.InstitutionModel;
import com.example.digitaltouristguide.Preview.InstitutionPreview;
import com.example.digitaltouristguide.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class InstitutionUserFragment extends Fragment {

	TextView museum, cinema, church, all;
	EditText search;
	RecyclerView list;

	FirebaseFirestore db;
	CollectionReference collection;
	Query query;
	InstitutionAdapter adapter;
	FirestoreRecyclerOptions<InstitutionModel> options;
	private final static String recyclerOrientation = "vertical";

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;
	public static final String fileName = "login";
	public static final String userType = "type";
	public static final String pickedTownName = "pickedTownName";
	String townName;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_user_institution, container, false);

		sharedPreferences = this.getActivity().getSharedPreferences(fileName, Context.MODE_PRIVATE);
		townName = sharedPreferences.getString(pickedTownName, "");

		db = FirebaseFirestore.getInstance();
		collection = db.collection("institutions");

		museum = view.findViewById(R.id.select_museum);
		cinema = view.findViewById(R.id.select_cinema);
		church = view.findViewById(R.id.select_church);
		all = view.findViewById(R.id.select_all);
		search = view.findViewById(R.id.search_institution_user);
		list = view.findViewById(R.id.institution_list_user);

		museum.setOnClickListener(v -> searchByType("Muzej"));

		cinema.setOnClickListener(v -> searchByType("Kino"));

		church.setOnClickListener(v -> searchByType("Crkva"));

		all.setOnClickListener(v -> searchByType("Sve"));

		search.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				searchByName(editable);
			}
		});

		return view;
	}

	private void searchByType(String type) {
		if (type.equals("Sve")) {
			query = collection.whereEqualTo("town", townName);
		} else {
			query = collection.whereEqualTo("town", townName).whereEqualTo("type", type);
		}

		options = new FirestoreRecyclerOptions.Builder<InstitutionModel>()
				.setQuery(query, InstitutionModel.class)
				.build();

		adapter.updateOptions(options);
	}

	private void searchByName(Editable editable) {
		if (editable.toString().isEmpty()) {
			query = collection.whereEqualTo("town", townName);
		} else {
			query = collection.whereEqualTo("town", townName).whereEqualTo("name", editable.toString());
		}

		options = new FirestoreRecyclerOptions.Builder<InstitutionModel>()
				.setQuery(query, InstitutionModel.class)
				.build();

		adapter.updateOptions(options);
	}

	private void setupRecyclerView() {
		query = collection.whereEqualTo("town", townName);

		options = new FirestoreRecyclerOptions.Builder<InstitutionModel>()
				.setQuery(query, InstitutionModel.class)
				.build();

		adapter = new InstitutionAdapter(recyclerOrientation, getContext(), options);
		list.setHasFixedSize(true);
		list.setLayoutManager(new LinearLayoutManager(getContext()));
		list.setAdapter(adapter);

		adapter.setOnItemClickListener((context, snapshot, position) -> {
			String institutionID = snapshot.getId();

			Intent intent = new Intent(getContext(), InstitutionPreview.class);
			intent.putExtra("institution_id", institutionID);
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