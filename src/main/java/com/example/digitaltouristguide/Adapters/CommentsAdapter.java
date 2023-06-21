package com.example.digitaltouristguide.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitaltouristguide.Models.CommentModel;
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

public class CommentsAdapter extends FirestoreRecyclerAdapter<CommentModel, CommentsAdapter.CommentsHolder> {
	Context context;

	public CommentsAdapter(Context context, @NonNull @NotNull FirestoreRecyclerOptions<CommentModel> options) {
		super(options);
		this.context = context;
	}

	@Override
	protected void onBindViewHolder(@NonNull @NotNull CommentsAdapter.CommentsHolder holder, int position, @NonNull @NotNull CommentModel model) {
		FirebaseFirestore.getInstance().collection("users").document(model.getAuthor())
				.get().addOnSuccessListener(snapshot -> {
			holder.author.setText(snapshot.getString("name"));
		}).addOnFailureListener(e -> {
			Toast.makeText(context, "Pogreška: " + e, Toast.LENGTH_SHORT).show();
		});

		Date date = model.getTimestamp().toDate();
		String format = new SimpleDateFormat("dd.MM.yyyy.").format(date);

		holder.date.setText(format);
		holder.text.setText(model.getText());
		holder.text.resetState(true);

		StorageReference storageProfile = FirebaseStorage.getInstance().getReference("users/" + model.getAuthor() + "_profile");

		try {
			File file = File.createTempFile("tempfile", ".jpeg");
			storageProfile.getFile(file).addOnSuccessListener(taskSnapshot -> {
				Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
				holder.profileImage.setImageBitmap(bitmap);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		holder.author.setOnClickListener(view -> {
			Intent intent = new Intent(context, ProfileActivity.class);
			intent.putExtra("user_id", model.getAuthor());
			context.startActivity(intent);
		});

		holder.profileImage.setOnClickListener(view -> {
			Intent intent = new Intent(context, ProfileActivity.class);
			intent.putExtra("user_id", model.getAuthor());
			context.startActivity(intent);
		});
	}

	@NonNull
	@NotNull
	@Override
	public CommentsAdapter.CommentsHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_comment, parent, false);
		return new CommentsHolder(v);
	}

	public class CommentsHolder extends RecyclerView.ViewHolder {
		ShapeableImageView profileImage;
		TextView author, date;
		ExpandableTextView text;
		ImageView answer;

		public CommentsHolder(@NonNull @NotNull View view) {
			super(view);

			profileImage = view.findViewById(R.id.comment_profile_picture);
			author = view.findViewById(R.id.comment_author);
			date = view.findViewById(R.id.comment_timestamp);
			text = view.findViewById(R.id.comment_text);
		}
	}
}
