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

import com.example.digitaltouristguide.Adapters.NatureAdapter;
import com.example.digitaltouristguide.Models.NatureModel;
import com.example.digitaltouristguide.Preview.NaturePreview;
import com.example.digitaltouristguide.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class NatureUserFragment extends Fragment {

	TextView park, beach, all;
	EditText search;
	RecyclerView list;

	CollectionReference collection;
	Query query;
	FirestoreRecyclerOptions<NatureModel> options;
	NatureAdapter adapter;
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
		View view = inflater.inflate(R.layout.fragment_user_nature, container, false);

		sharedPreferences = this.getActivity().getSharedPreferences(fileName, Context.MODE_PRIVATE);
		townName = sharedPreferences.getString(pickedTownName, "");
		collection = FirebaseFirestore.getInstance().collection("nature");

		park = view.findViewById(R.id.select_park);
		beach = view.findViewById(R.id.select_beach);
		all = view.findViewById(R.id.select_all);
		search = view.findViewById(R.id.search_nature_user);
		list = view.findViewById(R.id.nature_list_user);

		park.setOnClickListener(v -> searchByType("Park"));

		beach.setOnClickListener(v -> searchByType("Plaža"));

		all.setOnClickListener(v -> searchByType("Sve"));

		search.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
			@Override
			public void afterTextChanged(Editable editable) { searchByName(editable); }
		});

		return view;
	}

	private void searchByType(String type) {
		if (type.equals("Sve")) {
			query = collection.whereEqualTo("town", townName);
		} else {
			query = collection.whereEqualTo("town", townName).whereEqualTo("type", type);
		}

		options = new FirestoreRecyclerOptions.Builder<NatureModel>()
				.setQuery(query, NatureModel.class)
				.build();

		adapter.updateOptions(options);
	}

	private void searchByName(Editable editable) {
		if (editable.toString().isEmpty()) {
			query = collection.whereEqualTo("town", townName);
		} else {
			query = collection.whereEqualTo("town", townName).whereEqualTo("name", editable.toString());
		}

		options = new FirestoreRecyclerOptions.Builder<NatureModel>()
				.setQuery(query, NatureModel.class)
				.build();

		adapter.updateOptions(options);
	}

	private void setupRecyclerView() {
		query = collection.whereEqualTo("town", townName);

		options = new FirestoreRecyclerOptions.Builder<NatureModel>()
				.setQuery(query, NatureModel.class)
				.build();

		adapter = new NatureAdapter(recyclerOrientation, getContext(), options);
		list.setHasFixedSize(true);
		list.setLayoutManager(new LinearLayoutManager(getContext()));
		list.setAdapter(adapter);

		adapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			String natureID = documentSnapshot.getId();

			Intent intent = new Intent(getContext(), NaturePreview.class);
			intent.putExtra("nature_id", natureID);
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