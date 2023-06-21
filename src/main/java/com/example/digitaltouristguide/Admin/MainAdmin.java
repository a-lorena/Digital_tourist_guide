package com.example.digitaltouristguide.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitaltouristguide.LoginActivity;
import com.example.digitaltouristguide.R;
import com.example.digitaltouristguide.Adapters.TownAdapter;
import com.example.digitaltouristguide.Models.TownModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainAdmin extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

	Toolbar toolbar;
	DrawerLayout drawer;
	ActionBarDrawerToggle toggleDrawer;
	NavigationView navigation;
	TextView drawerUsername;
	ImageView profileImage, headerImage;

	EditText searchTown;
	RecyclerView townList;
	FloatingActionButton addTown;

	FirebaseFirestore db;
	CollectionReference collection;
	Query query;
	TownAdapter adapter;
	FirestoreRecyclerOptions<TownModel> options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_admin);

		toolbar = findViewById(R.id.main_admin_toolbar);
		setSupportActionBar(toolbar);

		drawer = findViewById(R.id.admin_main_drawer);
		navigation = findViewById(R.id.admin_main_nav);
		navigation.setNavigationItemSelectedListener(this);

		drawerUsername = navigation.getHeaderView(0).findViewById(R.id.drawer_username);
		profileImage = navigation.getHeaderView(0).findViewById(R.id.drawer_profile_image);
		headerImage = navigation.getHeaderView(0).findViewById(R.id.drawer_header_image);

		drawerUsername.setText("Admin");
		profileImage.setVisibility(View.INVISIBLE);

		toggleDrawer = new ActionBarDrawerToggle(this, drawer, toolbar,
				R.string.navigation_drawer_open, R.string.navigation_drawer_close);

		drawer.addDrawerListener(toggleDrawer);
		toggleDrawer.syncState();

		if (savedInstanceState == null) {
			toolbar.setTitle("Gradovi");
			getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
					new TownAdminFragment()).commit();
			navigation.setCheckedItem(R.id.town_nav_item);
		}

		/*searchTown = findViewById(R.id.search_town);
		townList = findViewById(R.id.town_list_admin);
		addTown = findViewById(R.id.add_town);*/

		db = FirebaseFirestore.getInstance();
		collection = db.collection("cities");

		/*searchTown.addTextChangedListener(new TextWatcher() {
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

		addTown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(MainAdmin.this, TownForm.class));
				finish();
			}
		});*/
	}

	/*private void setupRecyclerView() {
		query = collection;

		options = new FirestoreRecyclerOptions.Builder<TownModel>()
				.setQuery(query, TownModel.class)
				.build();

		adapter = new TownAdapter(this, options);
		townList.setHasFixedSize(true);
		townList.setLayoutManager(new LinearLayoutManager(this));
		townList.setAdapter(adapter);

		adapter.setOnItemClickListener(new TownAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(Context context, DocumentSnapshot documentSnapshot, int position) {
				String townID = documentSnapshot.getId();

				Intent intent = new Intent(MainAdmin.this, TownPreview.class);
				intent.putExtra("town_id", townID);
				startActivity(intent);
				finish();
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();

		setupRecyclerView();
		adapter.startListening();
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		setupRecyclerView();
		adapter.startListening();
	}

	@Override
	protected void onStop() {
		super.onStop();

		adapter.stopListening();
	}*/

	@Override
	public void onBackPressed() {
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.town_nav_item:
				toolbar.setTitle("Gradovi");
				getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
						new TownAdminFragment()).commit();
				break;

			case R.id.institution_nav_item:
				toolbar.setTitle("Kulturne ustanove");
				getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
						new InstitutionAdminFragment()).commit();
				break;

			case R.id.monuments_nav_item:
				toolbar.setTitle("Spomenici");
				getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
						new MonumentsAdminFragment()).commit();
				break;

			case R.id.events_nav_item:
				toolbar.setTitle("Događanja");
				getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
						new EventsAdminFragment()).commit();
				break;

			case R.id.nature_nav_item:
				toolbar.setTitle("Parkovi i plaže");
				getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
						new NatureAdminFragment()).commit();
				break;

			case R.id.restaurant_nav_item:
				toolbar.setTitle("Restorani i kafići");
				getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
						new RestaurantAdminFragment()).commit();
				break;

			case R.id.acc_nav_item:
				toolbar.setTitle("Smještaj i parking");
				getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
						new AccAdminFragment()).commit();
				break;




			case R.id.settings:
				Toast.makeText(this, "Item broj 4", Toast.LENGTH_SHORT).show();
				break;

			case R.id.logout:
				logoutUser();
				break;
		}

		drawer.closeDrawer(GravityCompat.START);

		return true;
	}

	private void logoutUser() {
		FirebaseAuth.getInstance().signOut();
		Toast.makeText(this, "Uspješno ste se odjavili.", Toast.LENGTH_SHORT).show();
		startActivity(new Intent(MainAdmin.this, LoginActivity.class));
		finish();
	}
}