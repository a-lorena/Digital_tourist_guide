package com.example.digitaltouristguide.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.digitaltouristguide.Adapters.AccAdapter;
import com.example.digitaltouristguide.Adapters.EventRecAdapter;
import com.example.digitaltouristguide.Adapters.InstitutionAdapter;
import com.example.digitaltouristguide.Adapters.MonumentAdapter;
import com.example.digitaltouristguide.Adapters.NatureAdapter;
import com.example.digitaltouristguide.Adapters.RestaurantAdapter;
import com.example.digitaltouristguide.Adapters.TownAdapter;
import com.example.digitaltouristguide.Models.AccModel;
import com.example.digitaltouristguide.Models.InstitutionModel;
import com.example.digitaltouristguide.Models.MonumentModel;
import com.example.digitaltouristguide.Models.NatureModel;
import com.example.digitaltouristguide.Models.RestaurantModel;
import com.example.digitaltouristguide.Models.TownModel;
import com.example.digitaltouristguide.Preview.AccPreview;
import com.example.digitaltouristguide.Preview.InstitutionPreview;
import com.example.digitaltouristguide.Preview.MonumentPreview;
import com.example.digitaltouristguide.Preview.NaturePreview;
import com.example.digitaltouristguide.Preview.RestaurantPreview;
import com.example.digitaltouristguide.Preview.TownPreview;
import com.example.digitaltouristguide.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class StartUser extends AppCompatActivity {

	ImageView allTowns;
	RecyclerView towns, institutions, monuments, events, nature, restaurants, acc;

	CollectionReference collTown, collInstitution, collMonument, collEvent, collNature, collRestaurant, collAcc;
	TownAdapter townAdapter;
	InstitutionAdapter institutionAdapter;
	MonumentAdapter monumentAdapter;
	EventRecAdapter eventAdapter;
	NatureAdapter natureAdapter;
	RestaurantAdapter restaurantAdapter;
	AccAdapter accAdapter;
	Query query;
	FirestoreRecyclerOptions<TownModel> options;
	private final static String recyclerHorizontal = "horizontal";
	public static final String fileName = "login";
	public static final String pickedTown = "id";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_user);

		allTowns = findViewById(R.id.all_towns);
		towns = findViewById(R.id.user_town_rec);
		institutions = findViewById(R.id.user_institutions_rec);
		monuments = findViewById(R.id.user_monument_rec);
		events = findViewById(R.id.user_event_rec);
		nature = findViewById(R.id.user_nature_rec);
		restaurants = findViewById(R.id.user_restaurants_rec);
		acc = findViewById(R.id.user_acc_rec);

		collTown = FirebaseFirestore.getInstance().collection("cities");
		collInstitution = FirebaseFirestore.getInstance().collection("institutions");
		collMonument = FirebaseFirestore.getInstance().collection("monuments");
		collEvent = FirebaseFirestore.getInstance().collection("events");
		collNature = FirebaseFirestore.getInstance().collection("nature");
		collRestaurant = FirebaseFirestore.getInstance().collection("restaurants");
		collAcc = FirebaseFirestore.getInstance().collection("accomodation");

		allTowns.setOnClickListener(view -> { startActivity(new Intent(this, TownUser.class)); });
	}

	private void initTownRecyclerView() {
		query = collTown.orderBy("rating", Query.Direction.DESCENDING)
				.limit(10);

		options = new FirestoreRecyclerOptions.Builder<TownModel>()
				.setQuery(query, TownModel.class)
				.build();

		townAdapter = new TownAdapter(recyclerHorizontal, this, options);
		LinearLayoutManager layoutTown = new LinearLayoutManager(this);
		layoutTown.setOrientation(RecyclerView.HORIZONTAL);

		towns.setHasFixedSize(true);
		towns.setLayoutManager(layoutTown);
		towns.setAdapter(townAdapter);

		townAdapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			Intent intent = new Intent(this, TownPreview.class);
			intent.putExtra("previewType", "pickTown");
			intent.putExtra("location_id", documentSnapshot.getId());
			startActivity(intent);
			finish();
		});
	}

	private void initInstitutionRecyclerView() {
		Query query = collInstitution.orderBy("rating", Query.Direction.DESCENDING)
				.limit(10);

		FirestoreRecyclerOptions<InstitutionModel> options = new FirestoreRecyclerOptions.Builder<InstitutionModel>()
				.setQuery(query, InstitutionModel.class)
				.build();

		institutionAdapter = new InstitutionAdapter(recyclerHorizontal, this, options);
		LinearLayoutManager layoutMonument = new LinearLayoutManager(this);
		layoutMonument.setOrientation(RecyclerView.HORIZONTAL);

		institutions.setHasFixedSize(true);
		institutions.setLayoutManager(layoutMonument);
		institutions.setAdapter(institutionAdapter);

		institutionAdapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			Intent intent = new Intent(this, InstitutionPreview.class);
			intent.putExtra("institution_id", documentSnapshot.getId());
			startActivity(intent);
			finish();
		});
	}

	private void initMonumentRecyclerView() {
		Query monumentQuery = collMonument.orderBy("rating", Query.Direction.DESCENDING)
				.limit(10);

		FirestoreRecyclerOptions<MonumentModel> monumentOptions = new FirestoreRecyclerOptions.Builder<MonumentModel>()
				.setQuery(monumentQuery, MonumentModel.class)
				.build();

		monumentAdapter = new MonumentAdapter(recyclerHorizontal, this, monumentOptions);
		LinearLayoutManager layoutMonument = new LinearLayoutManager(this);
		layoutMonument.setOrientation(RecyclerView.HORIZONTAL);

		monuments.setHasFixedSize(true);
		monuments.setLayoutManager(layoutMonument);
		monuments.setAdapter(monumentAdapter);

		monumentAdapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			Intent intent = new Intent(this, MonumentPreview.class);
			intent.putExtra("monument_id", documentSnapshot.getId());
			startActivity(intent);
			finish();
		});
	}

	/*private void initEventRecyclerView() {
		Query query = collEvent.orderBy("rating", Query.Direction.DESCENDING)
				.limit(10);

		FirestoreRecyclerOptions<EventModel> options = new FirestoreRecyclerOptions.Builder<EventModel>()
				.setQuery(query, EventModel.class)
				.build();

		eventAdapter = new EventRecAdapter(recyclerHorizontal, this, options);
		LinearLayoutManager layoutMonument = new LinearLayoutManager(this);
		layoutMonument.setOrientation(RecyclerView.HORIZONTAL);

		events.setHasFixedSize(true);
		events.setLayoutManager(layoutMonument);
		events.setAdapter(eventAdapter);

		eventAdapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			Intent intent = new Intent(this, EventPreview.class);
			intent.putExtra("event_id", documentSnapshot.getId());
			startActivity(intent);
			finish();
		});
	}*/

	private void initNatureRecyclerView() {
		Query query = collNature.orderBy("rating", Query.Direction.DESCENDING)
				.limit(10);

		FirestoreRecyclerOptions<NatureModel> options = new FirestoreRecyclerOptions.Builder<NatureModel>()
				.setQuery(query, NatureModel.class)
				.build();

		natureAdapter = new NatureAdapter(recyclerHorizontal, this, options);
		LinearLayoutManager layoutMonument = new LinearLayoutManager(this);
		layoutMonument.setOrientation(RecyclerView.HORIZONTAL);

		nature.setHasFixedSize(true);
		nature.setLayoutManager(layoutMonument);
		nature.setAdapter(natureAdapter);

		natureAdapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			Intent intent = new Intent(this, NaturePreview.class);
			intent.putExtra("nature_id", documentSnapshot.getId());
			startActivity(intent);
			finish();
		});
	}

	private void initRestaurantRecyclerView() {
		Query query = collRestaurant.orderBy("rating", Query.Direction.DESCENDING)
				.limit(10);

		FirestoreRecyclerOptions<RestaurantModel> options = new FirestoreRecyclerOptions.Builder<RestaurantModel>()
				.setQuery(query, RestaurantModel.class)
				.build();

		restaurantAdapter = new RestaurantAdapter(recyclerHorizontal, this, options);
		LinearLayoutManager layoutMonument = new LinearLayoutManager(this);
		layoutMonument.setOrientation(RecyclerView.HORIZONTAL);

		restaurants.setHasFixedSize(true);
		restaurants.setLayoutManager(layoutMonument);
		restaurants.setAdapter(restaurantAdapter);

		restaurantAdapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			Intent intent = new Intent(this, RestaurantPreview.class);
			intent.putExtra("restaurant_id", documentSnapshot.getId());
			startActivity(intent);
			finish();
		});
	}

	private void initAccRecyclerView() {
		Query query = collAcc.orderBy("rating", Query.Direction.DESCENDING)
				.limit(10);

		FirestoreRecyclerOptions<AccModel> options = new FirestoreRecyclerOptions.Builder<AccModel>()
				.setQuery(query, AccModel.class)
				.build();

		accAdapter = new AccAdapter(recyclerHorizontal, this, options);
		LinearLayoutManager layoutMonument = new LinearLayoutManager(this);
		layoutMonument.setOrientation(RecyclerView.HORIZONTAL);

		acc.setHasFixedSize(true);
		acc.setLayoutManager(layoutMonument);
		acc.setAdapter(accAdapter);

		accAdapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			Intent intent = new Intent(this, AccPreview.class);
			intent.putExtra("acc_id", documentSnapshot.getId());
			startActivity(intent);
			finish();
		});
	}

	@Override
	protected void onStart() {
		super.onStart();

		initTownRecyclerView();
		initInstitutionRecyclerView();
		initMonumentRecyclerView();
		//initEventRecyclerView();
		initNatureRecyclerView();
		initRestaurantRecyclerView();
		initAccRecyclerView();

		townAdapter.startListening();
		monumentAdapter.startListening();
		institutionAdapter.startListening();
		//eventAdapter.startListening();
		natureAdapter.startListening();
		restaurantAdapter.startListening();
		accAdapter.startListening();
	}

	@Override
	protected void onResume() {
		super.onResume();

		initTownRecyclerView();
		initInstitutionRecyclerView();
		initMonumentRecyclerView();
		//initEventRecyclerView();
		initNatureRecyclerView();
		initRestaurantRecyclerView();
		initAccRecyclerView();

		townAdapter.startListening();
		monumentAdapter.startListening();
		institutionAdapter.startListening();
		//eventAdapter.startListening();
		natureAdapter.startListening();
		restaurantAdapter.startListening();
		accAdapter.startListening();
	}

	@Override
	protected void onStop() {
		super.onStop();

		townAdapter.stopListening();
		monumentAdapter.stopListening();
		institutionAdapter.stopListening();
		//eventAdapter.stopListening();
		natureAdapter.stopListening();
		restaurantAdapter.stopListening();
		accAdapter.stopListening();
	}
}