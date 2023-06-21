package com.example.digitaltouristguide.Preview;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.digitaltouristguide.Adapters.RatingAdapter;
import com.example.digitaltouristguide.Models.RatingModel;
import com.example.digitaltouristguide.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class RatingsPreview extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView commentsList;
    ExtendedFloatingActionButton addComment;

    DocumentReference document;
    CollectionReference collection;
    RatingAdapter adapter;
    long commentsNumber, starsTotal, rating;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String fileName = "login";
    public static final String userType = "type";
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_ratings);

        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
        type = sharedPreferences.getString(userType, "user");

        String category = getIntent().getStringExtra("category");
        String locationID = getIntent().getStringExtra("location_id");
        document = FirebaseFirestore.getInstance().collection(category).document(locationID);
        collection = document.collection("Ratings");

        toolbar = findViewById(R.id.comments_preview_toolbar);
        commentsList = findViewById(R.id.comments_preview_comments);
        addComment = findViewById(R.id.comments_preview_add_comment);

        if (type.equals("admin")) {
            addComment.setVisibility(View.GONE);
        }

        getToolbarTitle();
        fetchData();

        addComment.setOnClickListener(view -> { showCommentDialog(); });
    }

    private void showCommentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.custom_dialog_rating, null);

        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        EditText comment = dialogView.findViewById(R.id.ratingComment);

        builder.setView(dialogView)
                .setCancelable(false)
                .setTitle("Nova recenzija")
                .setNegativeButton("Odustani", (dialogInterface, i) -> { })
                .setPositiveButton("Spremi", (dialogInterface, i) -> {
                    long ratingBarRating = (long) ratingBar.getRating();
                    String commentTXT = comment.getText().toString();

                    if (ratingBarRating == 0 || commentTXT.isEmpty()) {
                        Toast.makeText(this, "Ispunite sva polja.", Toast.LENGTH_SHORT).show();
                    } else {
                        saveComment(ratingBarRating, commentTXT);
                    }
                });

        builder.create().show();
    }

    private void saveComment(long stars, String comment) {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Spremanje...");
        pd.show();

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> rating = new HashMap<>();
        rating.put("comment", comment);
        rating.put("stars", stars);
        rating.put("author", userID);
        rating.put("time", Timestamp.now());
        
        document.collection("Ratings").add(rating).addOnSuccessListener(documentReference -> {
            pd.dismiss();
            Toast.makeText(this, "Ocjena spremljena.", Toast.LENGTH_SHORT).show();

            updateLocation(stars);
        }).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(this, "Pogreška: " + e, Toast.LENGTH_SHORT).show();
        });
    }

    private void updateLocation(long stars) {
        commentsNumber++;
        starsTotal += stars;
        rating = Math.round(starsTotal / commentsNumber);

        document.update(
            "comments", commentsNumber,
                "stars", starsTotal,
                "rating", rating
        );
    }

    private void fetchData() {
        document.get().addOnSuccessListener(snapshot -> {
            commentsNumber = snapshot.getLong("comments");
            starsTotal = snapshot.getLong("stars");
            rating = snapshot.getLong("rating");
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Pogreška: " + e, Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void getToolbarTitle() {
        document.get().addOnSuccessListener(snapshot -> {
            toolbar.setTitle(snapshot.getString("name"));
        }).addOnFailureListener(e -> {
            Toast.makeText(RatingsPreview.this, "Pogreška: " + e, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupRecyclerView() {
        Query query = collection.orderBy("time", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<RatingModel> options = new FirestoreRecyclerOptions.Builder<RatingModel>()
                .setQuery(query, RatingModel.class)
                .build();

        adapter = new RatingAdapter(this, options);
        commentsList.setHasFixedSize(true);
        commentsList.setLayoutManager(new LinearLayoutManager(this));
        commentsList.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        setupRecyclerView();
        adapter.startListening();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        setupRecyclerView();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        adapter.stopListening();
    }
}