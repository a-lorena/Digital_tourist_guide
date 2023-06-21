package com.example.digitaltouristguide.Preview;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.digitaltouristguide.Adapters.CommentsAdapter;
import com.example.digitaltouristguide.Edit.PutopisEdit;
import com.example.digitaltouristguide.Models.CommentModel;
import com.example.digitaltouristguide.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ru.embersoft.expandabletextview.ExpandableTextView;

public class PutopisPreview extends AppCompatActivity {

	Toolbar toolbar;
	ExpandableTextView text;
	ImageView photo;
	EditText comment;
	ImageView sendComment;
	RecyclerView commentsList;

	DocumentReference documentUser, documentAll;
	CollectionReference collectionComments;
	FirebaseFirestore db;
	CommentsAdapter adapter;

	String putopisID, putopisTitle, putopisAuthor, userID, username;
	RequestQueue requestQueue;
	String URL = "https://fcm.googleapis.com/fcm/send";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview_putopis);

		putopisID = getIntent().getStringExtra("putopis_id");
		putopisTitle = getIntent().getStringExtra("putopis_title");
		putopisAuthor = getIntent().getStringExtra("putopis_author");
		userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
		db = FirebaseFirestore.getInstance();

		documentAll = db.collection("Putopisi").document(putopisID);
		collectionComments = documentAll.collection("Comments");
		requestQueue = Volley.newRequestQueue(this);

		toolbar = findViewById(R.id.putopis_preview_toolbar);
		text = findViewById(R.id.putopis_preview_text);
		photo = findViewById(R.id.putopis_preview_photo);
		comment = findViewById(R.id.putopis_preview_input_comment);
		sendComment = findViewById(R.id.putopis_preview_send_comment);
		commentsList = findViewById(R.id.putopis_preview_comments_list);

		setSupportActionBar(toolbar);
		sendComment.setOnClickListener(view -> getComment());
	}

	private void getComment() {
		String commentTXT = comment.getText().toString();
		String commentAuthor = userID;

		if (commentTXT.isEmpty()) {
			Toast.makeText(this, "Ne možete poslati prazan komentar!", Toast.LENGTH_SHORT).show();
		} else {
			sendComment(commentTXT, commentAuthor);
			comment.setText("");
		}
	}

	private void sendComment(String text, String commentAuthor) {
		Map<String, Object> comment = new HashMap<>();
		comment.put("text", text);
		comment.put("author", commentAuthor);
		comment.put("timestamp", Timestamp.now());

		documentAll.collection("Comments").add(comment).addOnSuccessListener(documentReference -> {
			if (!commentAuthor.equals(putopisAuthor)) {
				sendNotification(commentAuthor, text);
			}
		}).addOnFailureListener(e -> Log.d("SAVE_FAILURE", e.toString()));
	}

	private void sendNotification(String commentAuthor, String text) {
		JSONObject json = new JSONObject();

		try {
			json.put("to", "/topics/" + putopisAuthor);

			JSONObject notificationObj = new JSONObject();
			notificationObj.put("title", username + " komentira " + putopisTitle);
			notificationObj.put("body", text);

			JSONObject extraData = new JSONObject();
			extraData.put("putopisID", putopisID);
			extraData.put("putopis_author", putopisAuthor);
			extraData.put("category", "Putopis");

			json.put("notification", notificationObj);
			json.put("data", extraData);

			JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, json,
					response -> {
					}, error -> {
			}) {
				@Override
				public Map<String, String> getHeaders() throws AuthFailureError {
					Map<String, String> header = new HashMap<>();
					header.put("content-type", "application/json");
					header.put("authorization", "key=");

					return header;
				}
			};

			requestQueue.add(request);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void fetchData() {
		documentAll.get().addOnSuccessListener(snapshot -> {
			getImage(snapshot);
			toolbar.setTitle(snapshot.getString("title"));
			text.setText(snapshot.getString("text"));
			text.resetState(true);
		}).addOnFailureListener(e -> Toast.makeText(PutopisPreview.this, "Pogreška: " + e, Toast.LENGTH_SHORT).show());

		db.collection("users").document(userID).get().addOnSuccessListener(snapshot -> username = snapshot.getString("name"));
	}

	private void getImage(DocumentSnapshot snapshot) {
		StorageReference storage = FirebaseStorage.getInstance().getReference("images_putopisi/" + snapshot.getString("imageName"));

		try {
			File file = File.createTempFile("tempfile", ".jpeg");
			storage.getFile(file).addOnSuccessListener(taskSnapshot -> {
				Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
				photo.setImageBitmap(bitmap);
				photo.setVisibility(View.VISIBLE);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (putopisAuthor.equals(userID)) {
			getMenuInflater().inflate(R.menu.toolbar_menu_admin, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int itemID = item.getItemId();

		switch (itemID) {
			case R.id.toolbar_edit_action:
				Intent intent = new Intent(PutopisPreview.this, PutopisEdit.class);
				intent.putExtra("putopis_id", putopisID);
				startActivity(intent);
				return true;

			case R.id.toolbar_delete_action:
				showAlert();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void showAlert() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Pozor!")
				.setMessage("Jeste li sigurni da želite obrisati putopis?")
				.setPositiveButton("DA", (dialogInterface, i) -> deletePutopis())
				.setNegativeButton("NE", (dialogInterface, i) -> {
				});

		alert.create().show();
	}

	private void deletePutopis() {
		ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage("Brisanje...");
		pd.show();

		documentAll.delete().addOnSuccessListener(unused -> {
			pd.dismiss();
			Toast.makeText(PutopisPreview.this, "Putopis obrisan.", Toast.LENGTH_SHORT).show();

		}).addOnFailureListener(e -> {
			pd.dismiss();
			Toast.makeText(PutopisPreview.this, "Brisanje neuspješno. Pokušajte kasnije.", Toast.LENGTH_SHORT).show();
		});

		finish();
	}

	private void setupRecycler() {
		Query query = collectionComments.orderBy("timestamp", Query.Direction.DESCENDING);

		FirestoreRecyclerOptions<CommentModel> options = new FirestoreRecyclerOptions.Builder<CommentModel>()
				.setQuery(query, CommentModel.class)
				.build();

		adapter = new CommentsAdapter(this, options);
		commentsList.setHasFixedSize(true);
		commentsList.setLayoutManager(new LinearLayoutManager(this));
		commentsList.setAdapter(adapter);
	}

	@Override
	protected void onStart() {
		super.onStart();

		fetchData();
		setupRecycler();
		adapter.startListening();
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		fetchData();
		setupRecycler();
		adapter.startListening();
	}

	@Override
	protected void onStop() {
		super.onStop();

		adapter.stopListening();
	}
}