package com.example.digitaltouristguide.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.digitaltouristguide.User.ProfileBookmarks;
import com.example.digitaltouristguide.User.ProfilePutopisi;

import org.jetbrains.annotations.NotNull;

public class FragmentUserAdapter extends FragmentStateAdapter {

	public FragmentUserAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
		super(fragmentManager, lifecycle);
	}

	@NonNull
	@NotNull
	@Override
	public Fragment createFragment(int position) {
		if (position == 1) return new ProfilePutopisi();
		return new ProfileBookmarks();
	}

	@Override
	public int getItemCount() {
		return 2;
	}
}
