package com.example.digitaltouristguide.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitaltouristguide.Models.TravelListPlaceModel;
import com.example.digitaltouristguide.Preview.EventPreview;
import com.example.digitaltouristguide.Preview.InstitutionPreview;
import com.example.digitaltouristguide.Preview.MonumentPreview;
import com.example.digitaltouristguide.Preview.NaturePreview;
import com.example.digitaltouristguide.Preview.RestaurantPreview;
import com.example.digitaltouristguide.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class TravelListPlacesAdapter extends FirestoreRecyclerAdapter<TravelListPlaceModel, TravelListPlacesAdapter.PlacesHolder > {
	Context context;
	OnItemClickListener listener;
	CollectionReference collection;
	DocumentReference document;

	public TravelListPlacesAdapter(Context context, @NonNull @NotNull FirestoreRecyclerOptions<TravelListPlaceModel> options) {
		super(options);
		this.context = context;
	}

	@Override
	protected void onBindViewHolder(@NonNull @NotNull TravelListPlacesAdapter.PlacesHolder holder, int position, @NonNull @NotNull TravelListPlaceModel model) {
		holder.name.setText(model.getName());

		boolean visited = model.getVisited();

		if (visited) {
			holder.container.setBackgroundColor(context.getResources().getColor(R.color.toolbar));
			holder.name.setTextColor(context.getResources().getColor(R.color.white));
		}
		else {
			holder.container.setBackgroundColor(Color.TRANSPARENT);
			holder.name.setTextColor(context.getResources().getColor(R.color.dark_green));
		}

		String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
		String listID = model.getListID();
		collection = FirebaseFirestore.getInstance()
				.collection("users").document(userID)
				.collection("TravelLists").document(listID)
				.collection("Places");

		holder.container.setOnClickListener(view -> {
			if (visited) {
				holder.container.setBackgroundColor(Color.TRANSPARENT);
				holder.name.setTextColor(context.getResources().getColor(R.color.dark_green));
				collection.document(model.getPlaceID()).update("visited", false);
			} else {
				holder.container.setBackgroundColor(context.getResources().getColor(R.color.toolbar));
				holder.name.setTextColor(context.getResources().getColor(R.color.white));
				collection.document(model.getPlaceID()).update("visited", true);
			}
		});

		holder.link.setOnClickListener(view -> {
			getLink(model.getCategory(), model.getPlaceID());
		});
	}

	public void deletePlace(int position) {
		getSnapshots().getSnapshot(position).getReference().delete();
	}

	private void getLink(String category, String id) {
		Intent intent = null;

		if (category.equals("institution")) {
			intent = new Intent(context, InstitutionPreview.class);
			intent.putExtra("institution_id", id);
		} else if (category.equals("monument")) {
			intent = new Intent(context, MonumentPreview.class);
			intent.putExtra("monument_id", id);
		} else if (category.equals("event")) {
			intent = new Intent(context, EventPreview.class);
			intent.putExtra("event_id", id);
		} else if (category.equals("nature")) {
			intent = new Intent(context, NaturePreview.class);
			intent.putExtra("nature_id", id);
		} else if (category.equals("restaurant")) {
			intent = new Intent(context, RestaurantPreview.class);
			intent.putExtra("restaurant_id", id);
		}

		context.startActivity(intent);
	}

	@NonNull
	@NotNull
	@Override
	public TravelListPlacesAdapter.PlacesHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_list_place, parent,false);
		return new PlacesHolder(v, listener);
	}

	public class PlacesHolder extends RecyclerView.ViewHolder {
		TextView name;
		ConstraintLayout container;
		ImageView link;

		public PlacesHolder(@NonNull @NotNull View view, OnItemClickListener listener) {
			super(view);

			name = view.findViewById(R.id.list_places_name);
			container = view.findViewById(R.id.list_places_container);
			link = view.findViewById(R.id.list_places_link);
		}
	}

	public interface OnItemClickListener {
		void onItemClick(Context context, DocumentSnapshot documentSnapshot, int position);
	}

	public void setOnItemClickListener (TravelListPlacesAdapter.OnItemClickListener listener) {
		this.listener = listener;
	}
}
