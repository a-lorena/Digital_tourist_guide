package com.example.digitaltouristguide.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitaltouristguide.Adapters.FragmentUserAdapter;
import com.example.digitaltouristguide.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class MyProfile extends AppCompatActivity {
	ImageView headerImage, profileImage;
	TextView name;
	TabLayout tabLayout;
	ViewPager2 viewPager;

	FragmentUserAdapter fragmentAdapter;
	StorageReference storageHeader, storageProfile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_profile);

		headerImage = findViewById(R.id.profile_header_image);
		profileImage = findViewById(R.id.profile_profile_image);
		name = findViewById(R.id.profile_name);
		tabLayout = findViewById(R.id.profile_tabs);
		viewPager = findViewById(R.id.profile_pager);

		initTabs();
		getData();
	}

	private void getData() {
		String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
		DocumentReference document = FirebaseFirestore.getInstance().collection("users").document(userID);

		document.get().addOnSuccessListener(snapshot -> {
			getImage(snapshot);
			name.setText(snapshot.getString("name"));
		}).addOnFailureListener(e -> {
			Toast.makeText(this, "Pogreška: " + e, Toast.LENGTH_SHORT).show();
		});
	}

	private void getImage(DocumentSnapshot snapshot) {
		storageHeader = FirebaseStorage.getInstance().getReference("users/" + snapshot.getString("header"));
		storageProfile = FirebaseStorage.getInstance().getReference("users/" + snapshot.getString("profile"));

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

	private void initTabs() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentAdapter = new FragmentUserAdapter(fragmentManager, getLifecycle());
		viewPager.setAdapter(fragmentAdapter);

		tabLayout.addTab(tabLayout.newTab().setText("Planovi"));
		tabLayout.addTab(tabLayout.newTab().setText("Putopisi"));

		tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) { viewPager.setCurrentItem(tab.getPosition()); }
			@Override
			public void onTabUnselected(TabLayout.Tab tab) { }
			@Override
			public void onTabReselected(TabLayout.Tab tab) { }
		});

		viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
			@Override
			public void onPageSelected(int position) {
				tabLayout.selectTab(tabLayout.getTabAt(position));
			}
		});
	}
}