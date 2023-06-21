package com.example.digitaltouristguide.User;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.digitaltouristguide.Adapters.TravelListAdapter;
import com.example.digitaltouristguide.Models.TravelListModel;
import com.example.digitaltouristguide.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class ProfileBookmarks extends Fragment {

	RecyclerView list;
	ExtendedFloatingActionButton addBookmark;

	String userID;
	CollectionReference collection;
	TravelListAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_profile_bookmarks, container, false);

		userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
		collection = FirebaseFirestore.getInstance().collection("users")
				.document(userID).collection("TravelLists");

		list = view.findViewById(R.id.user_list_bookmarks);
		addBookmark = view.findViewById(R.id.user_make_list);

		addBookmark.setOnClickListener(v -> showDialog());

		return view;
	}

	private void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.custom_dialog_bookmark, null);
		EditText titleET = view.findViewById(R.id.new_list_name);

		builder.setView(view)
				.setCancelable(false)
				.setTitle("Nova lista")
				.setNegativeButton("Odustani", (dialogInterface, i) -> { })
				.setPositiveButton("Spremi", (dialogInterface, i) -> {
					String title = titleET.getText().toString();
					saveList(title);
				});
		builder.create().show();
	}

	private void saveList(String title) {
		ProgressDialog pd = new ProgressDialog(getActivity());
		pd.setMessage("Spremanje...");
		pd.show();

		Map<String, Object> list = new HashMap<>();
		list.put("title", title);

		collection.add(list).addOnSuccessListener(documentReference -> {
			pd.dismiss();
			Toast.makeText(getActivity(), "Lista dodana.", Toast.LENGTH_SHORT).show();
		}).addOnFailureListener(e -> {
			pd.dismiss();
			Toast.makeText(getActivity(), "Pogreška: " + e, Toast.LENGTH_SHORT).show();
		});
	}

	private void setupRecyclerView() {
		Query query = collection;

		FirestoreRecyclerOptions<TravelListModel> options = new FirestoreRecyclerOptions.Builder<TravelListModel>()
				.setQuery(query, TravelListModel.class)
				.build();

		adapter = new TravelListAdapter(getContext(), options);
		list.setHasFixedSize(true);
		list.setLayoutManager(new LinearLayoutManager(getContext()));
		list.setAdapter(adapter);

		adapter.setOnItemClickListener((context, documentSnapshot, position) -> {
			Intent intent = new Intent(getContext(), BookmarksPreview.class);
			intent.putExtra("list_id", documentSnapshot.getId());
			intent.putExtra("list_title", documentSnapshot.getString("title"));
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