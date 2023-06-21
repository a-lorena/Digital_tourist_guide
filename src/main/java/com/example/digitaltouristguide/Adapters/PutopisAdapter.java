package com.example.digitaltouristguide.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitaltouristguide.Models.PutopisModel;
import com.example.digitaltouristguide.R;
import com.example.digitaltouristguide.User.ProfileActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PutopisAdapter extends FirestoreRecyclerAdapter<PutopisModel, PutopisAdapter.PutopisHolder> {
	Context context;
	OnItemClickListener listener;

	public PutopisAdapter(Context context, @NonNull @NotNull FirestoreRecyclerOptions<PutopisModel> options) {
		super(options);
		this.context = context;
	}

	@Override
	protected void onBindViewHolder(@NonNull @NotNull PutopisAdapter.PutopisHolder holder, int position, @NonNull @NotNull PutopisModel model) {
		Date date = model.getTimestamp().toDate();
		String format = new SimpleDateFormat("dd.MM.yyyy.").format(date);

		DocumentReference document = FirebaseFirestore.getInstance().collection("users").document(model.getAuthor());
		document.get().addOnSuccessListener(snapshot -> holder.author.setText(snapshot.getString("name")));

		holder.title.setText(model.getTitle());
		holder.date.setText(format);
		holder.text.setText(model.getText());

		StorageReference storageProfile = FirebaseStorage.getInstance().getReference("users/" + model.getAuthor() + "_profile");

		try {
			File file = File.createTempFile("tempfile", ".jpeg");
			storageProfile.getFile(file).addOnSuccessListener(taskSnapshot -> {
				Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
				holder.profile.setImageBitmap(bitmap);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		holder.author.setOnClickListener(view -> {
			Intent intent = new Intent(context, ProfileActivity.class);
			intent.putExtra("user_id", model.getAuthor());
			context.startActivity(intent);
		});

		holder.profile.setOnClickListener(view -> {
			Intent intent = new Intent(context, ProfileActivity.class);
			intent.putExtra("user_id", model.getAuthor());
			context.startActivity(intent);
		});
	}

	@NonNull
	@NotNull
	@Override
	public PutopisAdapter.PutopisHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_putopis, parent, false);
		return new PutopisHolder(v, listener);
	}

	public class PutopisHolder extends RecyclerView.ViewHolder {
		ShapeableImageView profile;
		TextView title, author, date;
		TextView text;

		public PutopisHolder(@NonNull @NotNull View view, OnItemClickListener listener) {
			super(view);

			profile = view.findViewById(R.id.putopis_profile_picture);
			title = view.findViewById(R.id.putopis_title);
			author = view.findViewById(R.id.putopis_author);
			date = view.findViewById(R.id.putopis_date);
			text = view.findViewById(R.id.putopis_text);

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

	public void setOnItemClickListener (PutopisAdapter.OnItemClickListener listener) {
		this.listener = listener;
	}
}
