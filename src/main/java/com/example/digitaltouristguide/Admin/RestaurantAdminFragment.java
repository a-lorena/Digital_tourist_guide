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

import com.example.digitaltouristguide.Adapters.RestaurantAdapter;
import com.example.digitaltouristguide.Forms.RestaurantForm;
import com.example.digitaltouristguide.Models.RestaurantModel;
import com.example.digitaltouristguide.Preview.RestaurantPreview;
import com.example.digitaltouristguide.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class RestaurantAdminFragment extends Fragment {

	TextView restaurant, bar, all;
	EditText search;
	RecyclerView list;
	FloatingActionButton addButton;

	CollectionReference collection;
	Query query;
	FirestoreRecyclerOptions<RestaurantModel> options;
	RestaurantAdapter adapter;
	private final static String recyclerOrientation = "vertical";

	SharedPreferences sharedPreferences;
	public static final String fileName = "login";
	public static final String userType = "type";
	String type;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_admin_restaurant, container, false);

		sharedPreferences = this.getActivity().getSharedPreferences(fileName, Context.MODE_PRIVATE);
		type = sharedPreferences.getString(userType, "user");
		collection = FirebaseFirestore.getInstance().collection("restaurants");

		restaurant = view.findViewById(R.id.select_restaurant);
		bar = view.findViewById(R.id.select_bar);
		all = view.findViewById(R.id.select_all);
		search = view.findViewById(R.id.search_restaurant_admin);
		list = view.findViewById(R.id.restaurant_list_admin);
		addButton = view.findViewById(R.id.add_restaurant_button);

		restaurant.setOnClickListener(view12 -> searchByType("Restoran"));

		bar.setOnClickListener(view13 -> searchByType("Kafić"));

		all.setOnClickListener(view14 -> searchByType("Sve"));

		search.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
			@Override
			public void afterTextChanged(Editable editable) { searchByName(editable); }
		});

		addButton.setOnClickListener(v -> startActivity(new Intent(getContext(), RestaurantForm.class)));

		return view;
	}

	private void searchByType(String type) {
		if (type.equals("Sve")) {
			query = collection;
		} else {
			query = collection.whereEqualTo("type", type);
		}

		options = new FirestoreRecyclerOptions.Builder<RestaurantModel>()
				.setQuery(query, RestaurantModel.class)
				.build();

		adapter.updateOptions(options);
	}

	private void searchByName(Editable editable) {
		if (editable.toString().isEmpty()) {
			query = collection;
		} else {
			query = collection.whereEqualTo("name", editable.toString());
		}

		options = new FirestoreRecyclerOptions.Builder<RestaurantModel>()
				.setQuery(query, RestaurantModel.class)
				.build();

		adapter.updateOptions(options);
	}

	private void setupRecyclerView() {
		query = collection;

		options = new FirestoreRecyclerOptions.Builder<RestaurantModel>()
				.setQuery(query, RestaurantModel.class)
				.build();

		adapter = new RestaurantAdapter(recyclerOrientation, getContext(), options);
		list.setHasFixedSize(true);
		list.setLayoutManager(new LinearLayoutManager(getContext()));
		list.setAdapter(adapter);

		adapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			String restaurantID = documentSnapshot.getId();

			Intent intent = new Intent(getContext(), RestaurantPreview.class);
			intent.putExtra("restaurant_id", restaurantID);
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