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

import com.example.digitaltouristguide.Adapters.EventAdapter;
import com.example.digitaltouristguide.Models.EventModel;
import com.example.digitaltouristguide.Preview.EventPreview;
import com.example.digitaltouristguide.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class EventsUserFragment extends Fragment {

	EditText search;
	RecyclerView list;

	FirebaseFirestore db;
	CollectionReference collection;
	Query query;
	EventAdapter adapter;
	FirestoreRecyclerOptions<EventModel> options;
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
		View view = inflater.inflate(R.layout.fragment_user_events, container, false);

		sharedPreferences = this.getActivity().getSharedPreferences(fileName, Context.MODE_PRIVATE);
		townName = sharedPreferences.getString(pickedTownName, "");

		db = FirebaseFirestore.getInstance();
		collection = db.collection("events");

		search = view.findViewById(R.id.search_events_user);
		list = view.findViewById(R.id.events_list_user);

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

	private void searchByName(Editable editable) {
		if (editable.toString().isEmpty()) {
			query = collection.whereEqualTo("town", townName);
		} else {
			query = collection.whereEqualTo("town", townName).whereEqualTo("name", editable.toString());
		}

		options = new FirestoreRecyclerOptions.Builder<EventModel>()
				.setQuery(query, EventModel.class)
				.build();

		adapter.updateOptions(options);
	}

	private void setupRecyclerView() {
		query = collection.whereEqualTo("town", townName);

		options = new FirestoreRecyclerOptions.Builder<EventModel>()
				.setQuery(query, EventModel.class)
				.build();

		adapter = new EventAdapter(recyclerOrientation, getContext(), options);
		list.setHasFixedSize(true);
		list.setLayoutManager(new LinearLayoutManager(getContext()));
		list.setAdapter(adapter);

		adapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			String eventID = documentSnapshot.getId();

			Intent intent = new Intent(getContext(), EventPreview.class);
			intent.putExtra("event_id", eventID);
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