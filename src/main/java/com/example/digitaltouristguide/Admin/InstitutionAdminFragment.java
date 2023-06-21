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
import android.widget.TextView;

import com.example.digitaltouristguide.Adapters.InstitutionAdapter;
import com.example.digitaltouristguide.Forms.InstitutionForm;
import com.example.digitaltouristguide.Preview.InstitutionPreview;
import com.example.digitaltouristguide.Models.InstitutionModel;
import com.example.digitaltouristguide.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class InstitutionAdminFragment extends Fragment {

	TextView museum, church, cinema, all;
	EditText search;
	RecyclerView list;
	FloatingActionButton addButton;

	FirebaseFirestore db;
	CollectionReference collection;
	Query query;
	InstitutionAdapter adapter;
	FirestoreRecyclerOptions<InstitutionModel> options;
	private final static String recyclerOrientation = "vertical";

	SharedPreferences sharedPreferences;
	public static final String fileName = "login";
	public static final String userType = "type";
	String type;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_admin_institution, container, false);

		sharedPreferences = this.getActivity().getSharedPreferences(fileName, Context.MODE_PRIVATE);
		type = sharedPreferences.getString(userType, "user");

		db = FirebaseFirestore.getInstance();
		collection = db.collection("institutions");

		museum = view.findViewById(R.id.select_museum);
		church = view.findViewById(R.id.select_church);
		cinema = view.findViewById(R.id.select_cinema);
		all = view.findViewById(R.id.select_all);
		search = view.findViewById(R.id.search_institution_admin);
		list = view.findViewById(R.id.institution_list_admin);
		addButton = view.findViewById(R.id.add_institution_button);

		museum.setOnClickListener(v -> searchByType("Muzej"));

		church.setOnClickListener(v -> searchByType("Crkva"));

		cinema.setOnClickListener(v -> searchByType("Kino"));

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

		addButton.setOnClickListener(v -> {
			Intent intent = new Intent(getContext(), InstitutionForm.class);
			intent.putExtra("form_type", "institution");
			startActivity(intent);
		});

		return view;
	}

	private void searchByType(String type) {
		if (type.equals("Sve")) {
			query = collection;
		} else {
			query = collection.whereEqualTo("type", type);
		}

		options = new FirestoreRecyclerOptions.Builder<InstitutionModel>()
				.setQuery(query, InstitutionModel.class)
				.build();

		adapter.updateOptions(options);
	}

	private void searchByName(Editable editable) {
		if (editable.toString().isEmpty()) {
			query = collection;
		} else {
			query = collection.whereEqualTo("name", editable.toString());
		}

		options = new FirestoreRecyclerOptions.Builder<InstitutionModel>()
				.setQuery(query, InstitutionModel.class)
				.build();

		adapter.updateOptions(options);
	}

	private void setupRecyclerView() {
		query = collection;

		options = new FirestoreRecyclerOptions.Builder<InstitutionModel>()
				.setQuery(query, InstitutionModel.class)
				.build();

		adapter = new InstitutionAdapter("vertical", getContext(), options);
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