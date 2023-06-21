package com.example.digitaltouristguide.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitaltouristguide.Models.TravelListModel;
import com.example.digitaltouristguide.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

public class TravelListAdapter extends FirestoreRecyclerAdapter<TravelListModel, TravelListAdapter.ListHolder> {
	Context context;
	OnItemClickListener listener;

	public TravelListAdapter(Context context, @NonNull @NotNull FirestoreRecyclerOptions<TravelListModel> options) {
		super(options);
		this.context = context;
	}

	@Override
	protected void onBindViewHolder(@NonNull @NotNull TravelListAdapter.ListHolder holder, int position, @NonNull @NotNull TravelListModel model) {
		holder.title.setText(model.getTitle());
	}

	@NonNull
	@NotNull
	@Override
	public TravelListAdapter.ListHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_list, parent, false);
		return new ListHolder(v, listener);
	}

	public class ListHolder extends RecyclerView.ViewHolder {
		TextView title;

		public ListHolder(@NonNull @NotNull View view, OnItemClickListener listener) {
			super(view);

			title = view.findViewById(R.id.list_card_item);

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

	public void setOnItemClickListener (TravelListAdapter.OnItemClickListener listener) {
		this.listener = listener;
	}
}
