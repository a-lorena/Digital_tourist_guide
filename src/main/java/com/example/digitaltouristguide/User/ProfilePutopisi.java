package com.example.digitaltouristguide.User;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.digitaltouristguide.Adapters.PutopisAdapter;
import com.example.digitaltouristguide.Forms.PutopisForm;
import com.example.digitaltouristguide.Models.PutopisModel;
import com.example.digitaltouristguide.Preview.PutopisPreview;
import com.example.digitaltouristguide.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ProfilePutopisi extends Fragment {

	RecyclerView list;
	ExtendedFloatingActionButton addPutopis;

	String userID;
	CollectionReference collection;
	PutopisAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_profile_putopisi, container, false);

		userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
		collection = FirebaseFirestore.getInstance().collection("Putopisi");
		//collection = FirebaseFirestore.getInstance().collection("users").document(userID).collection("Putopisi");

		list = view.findViewById(R.id.user_list_putopisi);
		addPutopis = view.findViewById(R.id.user_make_putopis);

		addPutopis.setOnClickListener(view1 -> {
			Intent intent = new Intent(getContext(), PutopisForm.class);
			startActivity(intent);
		});

		return view;
	}

	private void setupRecyclerView() {
		Query query = collection.whereEqualTo("author", userID)
				.orderBy("timestamp", Query.Direction.DESCENDING);

		FirestoreRecyclerOptions<PutopisModel> options = new FirestoreRecyclerOptions.Builder<PutopisModel>()
				.setQuery(query, PutopisModel.class)
				.build();

		adapter = new PutopisAdapter(getContext(), options);
		list.setHasFixedSize(true);
		list.setLayoutManager(new LinearLayoutManager(getContext()));
		list.setAdapter(adapter);

		adapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			Intent intent = new Intent(getContext(), PutopisPreview.class);
			intent.putExtra("putopis_id", documentSnapshot.getId());
			intent.putExtra("putopis_author", documentSnapshot.getString("author"));
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