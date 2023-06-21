package com.example.digitaltouristguide.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitaltouristguide.Models.InstitutionModel;
import com.example.digitaltouristguide.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class InstitutionAdapter extends FirestoreRecyclerAdapter<InstitutionModel, InstitutionAdapter.InstitutionHolder> {
	Context context;
	OnItemClickListener listener;
	String recyclerOrientation;

	public InstitutionAdapter(String recyclerOrientation, Context context, @NonNull FirestoreRecyclerOptions<InstitutionModel> options) {
		super(options);
		this.context = context;
		this.recyclerOrientation = recyclerOrientation;
	}

	@Override
	protected void onBindViewHolder(@NonNull InstitutionAdapter.InstitutionHolder holder, int position, @NonNull InstitutionModel model) {
		String imageName = model.getImageName();
		StorageReference storage = FirebaseStorage.getInstance().getReference("images_institutions/" + imageName);

		try {
			File file = File.createTempFile("tempfile", ".jpeg");
			storage.getFile(file).addOnSuccessListener(taskSnapshot -> {
				Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
				holder.image.setImageBitmap(bitmap);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		holder.name.setText(model.getName());
		holder.rating.setRating(model.getRating());
	}

	@NonNull
	@Override
	public InstitutionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v;

		if (recyclerOrientation.equals("vertical")) {
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_location, parent, false);
		} else {
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_rec, parent, false);
		}
		return new InstitutionHolder(v, listener);
	}

	public class InstitutionHolder extends RecyclerView.ViewHolder {
		ImageView image;
		TextView name;
		RatingBar rating;

		public InstitutionHolder(@NonNull View view, OnItemClickListener listener) {
			super(view);

			image = view.findViewById(R.id.location_image);
			name = view.findViewById(R.id.location_name);
			rating = view.findViewById(R.id.location_rating);

			view.setOnClickListener(v -> {
				int position = getAdapterPosition();
				if (position != RecyclerView.NO_POSITION && listener != null) {
					listener.onItemClick(context, getSnapshots().getSnapshot(position), position);
				}
			});
		}
	}

	public interface OnItemClickListener {
		void onItemClick(Context context, DocumentSnapshot documentSnapshot, int position);
	}

	public void setOnItemClickListener (InstitutionAdapter.OnItemClickListener listener) {
		this.listener = listener;
	}
}
