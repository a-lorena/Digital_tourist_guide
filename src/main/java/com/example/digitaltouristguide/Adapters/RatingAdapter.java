package com.example.digitaltouristguide.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitaltouristguide.Models.RatingModel;
import com.example.digitaltouristguide.R;
import com.example.digitaltouristguide.User.ProfileActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.embersoft.expandabletextview.ExpandableTextView;

public class RatingAdapter extends FirestoreRecyclerAdapter<RatingModel, RatingAdapter.RatingHolder> {
	Context context;

	public RatingAdapter(Context context, @NonNull @NotNull FirestoreRecyclerOptions<RatingModel> options) {
		super(options);
		this.context = context;
	}

	@Override
	protected void onBindViewHolder(@NonNull @NotNull RatingAdapter.RatingHolder holder, int position, @NonNull @NotNull RatingModel model) {
		FirebaseFirestore.getInstance().collection("users").document(model.getAuthor())
				.get().addOnSuccessListener(snapshot -> holder.author.setText(snapshot.getString("name")))
				.addOnFailureListener(e -> Toast.makeText(context, "Pogreška: " + e, Toast.LENGTH_SHORT).show());

		Date date = model.getTime().toDate();
		String format = new SimpleDateFormat("dd.MM.yyyy.").format(date);

		holder.stars.setRating(model.getStars());
		holder.timestamp.setText(format);
		holder.comment.setText(model.getComment());
		holder.comment.resetState(true);

		StorageReference storageProfile = FirebaseStorage.getInstance().getReference("users/" + model.getAuthor() + "_profile");

		try {
			File file = File.createTempFile("tempfile", ".jpeg");
			storageProfile.getFile(file).addOnSuccessListener(taskSnapshot -> {
				Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
				holder.picture.setImageBitmap(bitmap);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		holder.author.setOnClickListener(view -> {
			Intent intent = new Intent(context, ProfileActivity.class);
			intent.putExtra("user_id", model.getAuthor());
			context.startActivity(intent);
		});

		holder.picture.setOnClickListener(view -> {
			Intent intent = new Intent(context, ProfileActivity.class);
			intent.putExtra("user_id", model.getAuthor());
			context.startActivity(intent);
		});
	}

	@NonNull
	@NotNull
	@Override
	public RatingAdapter.RatingHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_rating, parent, false);
		return new RatingHolder(v);
	}

	public class RatingHolder extends RecyclerView.ViewHolder {
		ShapeableImageView picture;
		TextView author, timestamp;
		ExpandableTextView comment;
		RatingBar stars;

		public RatingHolder(@NonNull @NotNull View view) {
			super(view);

			picture = view.findViewById(R.id.rating_profile_picture);
			author = view.findViewById(R.id.rating_author);
			timestamp = view.findViewById(R.id.rating_timestamp);
			comment = view.findViewById(R.id.rating_comment);
			stars = view.findViewById(R.id.rating_stars);
		}
	}
}
