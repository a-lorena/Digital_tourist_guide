package com.example.digitaltouristguide.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitaltouristguide.Models.NewsModel;
import com.example.digitaltouristguide.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NewsAdapter extends FirestoreRecyclerAdapter<NewsModel, NewsAdapter.NewsHolder> {
	Context context;

	public NewsAdapter(Context context, @NonNull @NotNull FirestoreRecyclerOptions<NewsModel> options) {
		super(options);
		this.context = context;
	}

	@Override
	protected void onBindViewHolder(@NonNull @NotNull NewsAdapter.NewsHolder holder, int position, @NonNull @NotNull NewsModel model) {
		Date date = model.getTimestamp().toDate();
		String formatDate = new SimpleDateFormat("dd.MM.yyyy.").format(date);
		Date time = model.getTimestamp().toDate();
		String formatTime = new SimpleDateFormat("HH:mm").format(time);
		String timestamp = formatDate + " " + formatTime;

		holder.timestamp.setText(timestamp);
		holder.text.setText(model.getText());
	}

	@NonNull
	@NotNull
	@Override
	public NewsAdapter.NewsHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_news, parent, false);
		return new NewsHolder(v);
	}

	public class NewsHolder extends RecyclerView.ViewHolder {
		TextView timestamp, text;

		public NewsHolder(@NonNull @NotNull View view) {
			super(view);

			timestamp = view.findViewById(R.id.news_date);
			text = view.findViewById(R.id.news_text);
		}
	}
}
