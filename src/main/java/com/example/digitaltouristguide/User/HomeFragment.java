package com.example.digitaltouristguide.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.digitaltouristguide.Adapters.FragmentHomeAdapter;
import com.example.digitaltouristguide.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {

	TabLayout tabLayout;
	ViewPager2 viewPager;
	FragmentHomeAdapter fragmentAdapter;

	DocumentReference document;

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;
	public static final String fileName = "login";
	public static final String pickedTown = "id";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);

		sharedPreferences = this.requireActivity().getSharedPreferences(fileName, Context.MODE_PRIVATE);
		String townID = sharedPreferences.getString(pickedTown, "");
		document = FirebaseFirestore.getInstance().collection("cities").document(townID);

		tabLayout = view.findViewById(R.id.home_tabs);
		viewPager = view.findViewById(R.id.home_pager);

		initTabs();

		return view;
	}

	private void initTabs() {
		FragmentManager fragmentManager = this.requireActivity().getSupportFragmentManager();
		fragmentAdapter = new FragmentHomeAdapter(fragmentManager, getLifecycle());
		viewPager.setAdapter(fragmentAdapter);
		viewPager.setUserInputEnabled(false);

		tabLayout.addTab(tabLayout.newTab().setText("Novosti"));
		tabLayout.addTab(tabLayout.newTab().setText("Informacije"));

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