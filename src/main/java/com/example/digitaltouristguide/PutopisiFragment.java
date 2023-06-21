package com.example.digitaltouristguide;

import android.content.Intent;
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

import com.example.digitaltouristguide.Adapters.PutopisAdapter;
import com.example.digitaltouristguide.Forms.PutopisForm;
import com.example.digitaltouristguide.Models.PutopisModel;
import com.example.digitaltouristguide.Preview.PutopisPreview;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PutopisiFragment extends Fragment {

	EditText search;
	RecyclerView list;
	FloatingActionButton addPutopis;

	CollectionReference collection;
	PutopisAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_putopisi, container, false);

		collection = FirebaseFirestore.getInstance().collection("Putopisi");

		search = view.findViewById(R.id.putopisi_search);
		list = view.findViewById(R.id.putopisi_list);
		addPutopis = view.findViewById(R.id.add_putopis_button);

		search.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
			@Override
			public void afterTextChanged(Editable editable) {
				Query query;

				if (editable.toString().isEmpty()) {
					query = collection;
				} else {
					query = collection.whereEqualTo("town", editable.toString());
				}

				FirestoreRecyclerOptions<PutopisModel> options = new FirestoreRecyclerOptions.Builder<PutopisModel>()
						.setQuery(query, PutopisModel.class)
						.build();

				adapter.updateOptions(options);
			}
		});

		addPutopis.setOnClickListener(v -> {
			Intent intent = new Intent(getContext(), PutopisForm.class);
			startActivity(intent);
		});

		return view;
	}

	private void setupRecycler() {
		FirestoreRecyclerOptions<PutopisModel> options = new FirestoreRecyclerOptions.Builder<PutopisModel>()
				.setQuery(collection, PutopisModel.class)
				.build();

		adapter = new PutopisAdapter(getContext(), options);
		list.setHasFixedSize(true);
		list.setLayoutManager(new LinearLayoutManager(getContext()));
		list.setAdapter(adapter);

		adapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			Intent intent = new Intent(getContext(), PutopisPreview.class);
			intent.putExtra("putopis_id", documentSnapshot.getId());
			intent.putExtra("putopis_title", documentSnapshot.getString("title"));
			intent.putExtra("putopis_author", documentSnapshot.getString("author"));
			startActivity(intent);
		});
	}

	@Override
	public void onStart() {
		super.onStart();

		setupRecycler();
		adapter.startListening();
	}

	@Override
	public void onResume() {
		super.onResume();

		setupRecycler();
		adapter.startListening();
	}

	@Override
	public void onStop() {
		super.onStop();

		adapter.stopListening();
	}
}