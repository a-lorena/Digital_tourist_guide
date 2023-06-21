package com.example.digitaltouristguide.User;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.digitaltouristguide.Adapters.AccAdapter;
import com.example.digitaltouristguide.Adapters.EventRecAdapter;
import com.example.digitaltouristguide.Adapters.InstitutionAdapter;
import com.example.digitaltouristguide.Adapters.MonumentAdapter;
import com.example.digitaltouristguide.Adapters.NatureAdapter;
import com.example.digitaltouristguide.Adapters.NewsAdapter;
import com.example.digitaltouristguide.Adapters.RestaurantAdapter;
import com.example.digitaltouristguide.Models.AccModel;
import com.example.digitaltouristguide.Models.InstitutionModel;
import com.example.digitaltouristguide.Models.MonumentModel;
import com.example.digitaltouristguide.Models.NatureModel;
import com.example.digitaltouristguide.Models.NewsModel;
import com.example.digitaltouristguide.Models.RestaurantModel;
import com.example.digitaltouristguide.Preview.AccPreview;
import com.example.digitaltouristguide.Preview.InstitutionPreview;
import com.example.digitaltouristguide.Preview.MonumentPreview;
import com.example.digitaltouristguide.Preview.NaturePreview;
import com.example.digitaltouristguide.Preview.RestaurantPreview;
import com.example.digitaltouristguide.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HomeNewsTab extends Fragment {

	TextView refresh;
	RecyclerView newsList, institutions, monuments, events, nature, restaurants, acc;

	DocumentReference document;
	CollectionReference collection, collInstitution, collMonument, collEvent, collNature, collRestaurant, collAcc;
	NewsAdapter adapter;
	InstitutionAdapter institutionAdapter;
	MonumentAdapter monumentAdapter;
	EventRecAdapter eventAdapter;
	NatureAdapter natureAdapter;
	RestaurantAdapter restaurantAdapter;
	AccAdapter accAdapter;
	Query query;

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;
	public static final String fileName = "login";
	public static final String userType = "type";
	public static final String pickedTown = "id";
	public static final String pickedTownName = "pickedTownName";
	String townName;
	private final static String recyclerHorizontal = "horizontal";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home_news, container, false);

		sharedPreferences = this.requireActivity().getSharedPreferences(fileName, Context.MODE_PRIVATE);
		townName = sharedPreferences.getString(pickedTownName, "");
		collection = FirebaseFirestore.getInstance().collection("news");

		refresh = view.findViewById(R.id.home_refresh_news);
		newsList = view.findViewById(R.id.home_news);
		institutions = view.findViewById(R.id.user_institutions_rec);
		monuments = view.findViewById(R.id.user_monument_rec);
		//events = view.findViewById(R.id.user_event_rec);
		nature = view.findViewById(R.id.user_nature_rec);
		restaurants = view.findViewById(R.id.user_restaurants_rec);
		acc = view.findViewById(R.id.user_acc_rec);

		collInstitution = FirebaseFirestore.getInstance().collection("institutions");
		collMonument = FirebaseFirestore.getInstance().collection("monuments");
		collEvent = FirebaseFirestore.getInstance().collection("events");
		collNature = FirebaseFirestore.getInstance().collection("nature");
		collRestaurant = FirebaseFirestore.getInstance().collection("restaurants");
		collAcc = FirebaseFirestore.getInstance().collection("accomodation");

		refresh.setOnClickListener(v -> refreshNews());

		return view;
	}

	private void refreshNews() {
		Query newQuery = collection.whereEqualTo("town", townName).orderBy("timestamp", Query.Direction.DESCENDING);

		FirestoreRecyclerOptions<NewsModel> options = new FirestoreRecyclerOptions.Builder<NewsModel>()
				.setQuery(newQuery, NewsModel.class)
				.build();

		adapter.updateOptions(options);
	}

	private void setupNews() {
		query = collection.whereEqualTo("town", townName);

		FirestoreRecyclerOptions<NewsModel> options = new FirestoreRecyclerOptions.Builder<NewsModel>()
				.setQuery(query, NewsModel.class)
				.build();

		adapter = new NewsAdapter(getContext(), options);

		newsList.setHasFixedSize(true);
		newsList.setLayoutManager(new LinearLayoutManager(getContext()));
		newsList.setAdapter(adapter);
	}

	private void initInstitutionRecyclerView() {
		Query query = collInstitution.whereEqualTo("town", townName).orderBy("rating", Query.Direction.DESCENDING)
				.limit(5);

		FirestoreRecyclerOptions<InstitutionModel> options = new FirestoreRecyclerOptions.Builder<InstitutionModel>()
				.setQuery(query, InstitutionModel.class)
				.build();

		institutionAdapter = new InstitutionAdapter(recyclerHorizontal, getContext(), options);
		LinearLayoutManager layoutMonument = new LinearLayoutManager(getContext());
		layoutMonument.setOrientation(RecyclerView.HORIZONTAL);

		institutions.setHasFixedSize(true);
		institutions.setLayoutManager(layoutMonument);
		institutions.setAdapter(institutionAdapter);

		institutionAdapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			Intent intent = new Intent(getContext(), InstitutionPreview.class);
			intent.putExtra("institution_id", documentSnapshot.getId());
			startActivity(intent);
		});
	}

	private void initMonumentRecyclerView() {
		Query monumentQuery = collMonument.whereEqualTo("town", townName).orderBy("rating", Query.Direction.DESCENDING)
				.limit(5);

		FirestoreRecyclerOptions<MonumentModel> monumentOptions = new FirestoreRecyclerOptions.Builder<MonumentModel>()
				.setQuery(monumentQuery, MonumentModel.class)
				.build();

		monumentAdapter = new MonumentAdapter(recyclerHorizontal, getContext(), monumentOptions);
		LinearLayoutManager layoutMonument = new LinearLayoutManager(getContext());
		layoutMonument.setOrientation(RecyclerView.HORIZONTAL);

		monuments.setHasFixedSize(true);
		monuments.setLayoutManager(layoutMonument);
		monuments.setAdapter(monumentAdapter);

		monumentAdapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			Intent intent = new Intent(getContext(), MonumentPreview.class);
			intent.putExtra("monument_id", documentSnapshot.getId());
			startActivity(intent);
		});
	}

	private void initNatureRecyclerView() {
		Query query = collNature.whereEqualTo("town", townName).orderBy("rating", Query.Direction.DESCENDING)
				.limit(5);

		FirestoreRecyclerOptions<NatureModel> options = new FirestoreRecyclerOptions.Builder<NatureModel>()
				.setQuery(query, NatureModel.class)
				.build();

		natureAdapter = new NatureAdapter(recyclerHorizontal, getContext(), options);
		LinearLayoutManager layoutMonument = new LinearLayoutManager(getContext());
		layoutMonument.setOrientation(RecyclerView.HORIZONTAL);

		nature.setHasFixedSize(true);
		nature.setLayoutManager(layoutMonument);
		nature.setAdapter(natureAdapter);

		natureAdapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			Intent intent = new Intent(getContext(), NaturePreview.class);
			intent.putExtra("nature_id", documentSnapshot.getId());
			startActivity(intent);
		});
	}

	private void initRestaurantRecyclerView() {
		Query query = collRestaurant.whereEqualTo("town", townName).orderBy("rating", Query.Direction.DESCENDING)
				.limit(5);

		FirestoreRecyclerOptions<RestaurantModel> options = new FirestoreRecyclerOptions.Builder<RestaurantModel>()
				.setQuery(query, RestaurantModel.class)
				.build();

		restaurantAdapter = new RestaurantAdapter(recyclerHorizontal, getContext(), options);
		LinearLayoutManager layoutMonument = new LinearLayoutManager(getContext());
		layoutMonument.setOrientation(RecyclerView.HORIZONTAL);

		restaurants.setHasFixedSize(true);
		restaurants.setLayoutManager(layoutMonument);
		restaurants.setAdapter(restaurantAdapter);

		restaurantAdapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			Intent intent = new Intent(getContext(), RestaurantPreview.class);
			intent.putExtra("restaurant_id", documentSnapshot.getId());
			startActivity(intent);
		});
	}

	private void initAccRecyclerView() {
		Query query = collAcc.whereEqualTo("town", townName).orderBy("rating", Query.Direction.DESCENDING)
				.limit(5);

		FirestoreRecyclerOptions<AccModel> options = new FirestoreRecyclerOptions.Builder<AccModel>()
				.setQuery(query, AccModel.class)
				.build();

		accAdapter = new AccAdapter(recyclerHorizontal, getContext(), options);
		LinearLayoutManager layoutMonument = new LinearLayoutManager(getContext());
		layoutMonument.setOrientation(RecyclerView.HORIZONTAL);

		acc.setHasFixedSize(true);
		acc.setLayoutManager(layoutMonument);
		acc.setAdapter(accAdapter);

		accAdapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			Intent intent = new Intent(getContext(), AccPreview.class);
			intent.putExtra("acc_id", documentSnapshot.getId());
			startActivity(intent);
		});
	}

	@Override
	public void onStart() {
		super.onStart();

		setupNews();
		initInstitutionRecyclerView();
		initMonumentRecyclerView();
		initNatureRecyclerView();
		initRestaurantRecyclerView();
		initAccRecyclerView();

		adapter.startListening();
		monumentAdapter.startListening();
		institutionAdapter.startListening();
		natureAdapter.startListening();
		restaurantAdapter.startListening();
		accAdapter.startListening();
	}

	@Override
	public void onResume() {
		super.onResume();

		initInstitutionRecyclerView();
		initMonumentRecyclerView();
		initNatureRecyclerView();
		initRestaurantRecyclerView();
		initAccRecyclerView();

		adapter.startListening();
		monumentAdapter.startListening();
		institutionAdapter.startListening();
		natureAdapter.startListening();
		restaurantAdapter.startListening();
		accAdapter.startListening();
	}

	@Override
	public void onStop() {
		super.onStop();

		adapter.stopListening();
		monumentAdapter.stopListening();
		institutionAdapter.stopListening();
		natureAdapter.stopListening();
		restaurantAdapter.stopListening();
		accAdapter.stopListening();
	}
}