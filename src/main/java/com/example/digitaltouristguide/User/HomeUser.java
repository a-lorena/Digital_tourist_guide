package com.example.digitaltouristguide.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitaltouristguide.LoginActivity;
import com.example.digitaltouristguide.Preview.TownPreview;
import com.example.digitaltouristguide.PutopisiFragment;
import com.example.digitaltouristguide.R;
import com.example.digitaltouristguide.UserFragments.AccUserFragment;
import com.example.digitaltouristguide.UserFragments.EventsUserFragment;
import com.example.digitaltouristguide.UserFragments.InstitutionUserFragment;
import com.example.digitaltouristguide.UserFragments.MonumentUserFragment;
import com.example.digitaltouristguide.UserFragments.NatureUserFragment;
import com.example.digitaltouristguide.UserFragments.RestaurantUserFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class HomeUser extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

	Toolbar toolbar;
	DrawerLayout drawer;
	ActionBarDrawerToggle toggleDrawer;
	NavigationView navigation;
	TextView drawerUsername;
	ImageView profileImage, headerImage;

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;
	public static final String fileName = "login";
	public static final String userType = "type";
	public static final String pickedTownName = "pickedTownName";

	String[] categories = {"Ustanove", "Spomenici", "Parkovi i plaže", "Restorani i kafići"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_user);

		sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
		if (getIntent().hasExtra("category")) {
			Intent intent = new Intent(this, TownPreview.class);
			intent.putExtra("location_id", getIntent().getStringExtra("location_id"));
			startActivity(intent);
		}

		toolbar = findViewById(R.id.user_home_toolbar);
		setSupportActionBar(toolbar);
		toolbar.setTitle(sharedPreferences.getString(pickedTownName, ""));

		drawer = findViewById(R.id.user_home_drawer);
		navigation = findViewById(R.id.nav_view);
		navigation.setNavigationItemSelectedListener(this);

		drawerUsername = navigation.getHeaderView(0).findViewById(R.id.drawer_username);
		profileImage = navigation.getHeaderView(0).findViewById(R.id.drawer_profile_image);
		headerImage = navigation.getHeaderView(0).findViewById(R.id.drawer_header_image);
		fetchData();

		toggleDrawer = new ActionBarDrawerToggle(this, drawer, toolbar,
				R.string.navigation_drawer_open, R.string.navigation_drawer_close);

		drawer.addDrawerListener(toggleDrawer);
		toggleDrawer.syncState();

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
					new HomeFragment()).commit();
			navigation.setCheckedItem(R.id.town_nav_item);
		}
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.home_nav_item:
				toolbar.setTitle(sharedPreferences.getString(pickedTownName, ""));
				getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
						new HomeFragment()).commit();
				break;

			case R.id.institutions_nav_item:
				toolbar.setTitle("Kulturne ustanove");
				getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
						new InstitutionUserFragment()).commit();
				break;
				
			case R.id.monuments_nav_item:
				toolbar.setTitle("Spomenici");
				getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
						new MonumentUserFragment()).commit();
				break;

			case R.id.events_nav_item:
				toolbar.setTitle("Događanja");
				getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
						new EventsUserFragment()).commit();
				break;

			case R.id.nature_nav_item:
				toolbar.setTitle("Parkovi i plaže");
				getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
						new NatureUserFragment()).commit();
				break;

			case R.id.restaurant_nav_item:
				toolbar.setTitle("Restorani i kafići");
				getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
						new RestaurantUserFragment()).commit();
				break;

			case R.id.acc_nav_item:
				toolbar.setTitle("Smještaj i parking");
				getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
						new AccUserFragment()).commit();
				break;

			case R.id.putpoisi_nav_item:
				toolbar.setTitle("Putopisi");
				getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
						new PutopisiFragment()).commit();
				break;

			case R.id.profile:
				startActivity(new Intent(this, MyProfile.class));
				break;

			case R.id.walk_nav_item:
				showAlert();
				break;

			case R.id.settings:
				startActivity(new Intent(this, SettingsActivity.class));
				break;
				
			case R.id.logout:
				logoutUser();
				break;
		}

		drawer.closeDrawer(GravityCompat.START);

		return true;
	}

	private void showAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Odaberite kategoriju");

		builder.setItems(categories, (dialogInterface, i) -> {
			Intent intent = new Intent(HomeUser.this, WalkActivity.class);

			switch (i) {
				case 0:
					intent.putExtra("category", "institutions");
					break;

				case 1:
					intent.putExtra("category", "monuments");
					break;

				case 2:
					intent.putExtra("category", "nature");
					break;

				case 3:
					intent.putExtra("category", "restaurants");
					break;
			}

			startActivity(intent);
		});

		builder.show();
	}

	private void logoutUser() {
		FirebaseAuth.getInstance().signOut();

		editor = sharedPreferences.edit();
		editor.putString(userType, "");
		editor.commit();

		Toast.makeText(this, "Uspješno ste se odjavili.", Toast.LENGTH_SHORT).show();
		startActivity(new Intent(HomeUser.this, LoginActivity.class));
		finish();
	}

	@Override
	public void onBackPressed() {
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}

	private void fetchData() {
		String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
		DocumentReference document = FirebaseFirestore.getInstance().collection("users").document(userID);

		document.get().addOnSuccessListener(snapshot -> {
			drawerUsername.setText(snapshot.getString("name"));
			getImage(snapshot);
		});
	}

	private void getImage(DocumentSnapshot snapshot) {
		StorageReference storageHeader = FirebaseStorage.getInstance().getReference("users/" + snapshot.getString("header"));
		StorageReference storageProfile = FirebaseStorage.getInstance().getReference("users/" + snapshot.getString("profile"));

		try {
			File file = File.createTempFile("tempfile", ".jpeg");
			storageHeader.getFile(file).addOnSuccessListener(taskSnapshot -> {
				Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
				headerImage.setImageBitmap(bitmap);
			});
			storageProfile.getFile(file).addOnSuccessListener(taskSnapshot -> {
				Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
				profileImage.setImageBitmap(bitmap);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}