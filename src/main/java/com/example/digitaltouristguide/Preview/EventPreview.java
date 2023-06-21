package com.example.digitaltouristguide.Preview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.digitaltouristguide.Adapters.RatingAdapter;
import com.example.digitaltouristguide.Edit.EventEdit;
import com.example.digitaltouristguide.Models.RatingModel;
import com.example.digitaltouristguide.R;
import com.example.digitaltouristguide.Service.AlertReceiver;
import com.example.digitaltouristguide.Service.DatePickerFragment;
import com.example.digitaltouristguide.Service.TimePickerFragment;
import com.example.digitaltouristguide.User.PickBookmarksList;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import ru.embersoft.expandabletextview.ExpandableTextView;

public class EventPreview extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

	Toolbar toolbar;
	ImageView image;
	ExpandableTextView time, location, description;
	TextView showAllComments, descText;
	RecyclerView commentsList;

	DocumentReference document;
	CollectionReference collection;
	RatingAdapter adapter;
	String eventID;
	static String eventName, eventDateStart, eventStart, openID;

	Calendar alarmCalendar;
	AlertReceiver alertReceiver;

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;
	public static final String fileName = "login";
	public static final String userType = "type";
	String type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview_event);

		sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
		type = sharedPreferences.getString(userType, "user");

		eventID = getIntent().getStringExtra("event_id");
		document = FirebaseFirestore.getInstance().collection("events").document(eventID);
		collection = document.collection("Ratings");

		toolbar = findViewById(R.id.event_preview_toolbar);
		image = findViewById(R.id.event_preview_image);
		time = findViewById(R.id.event_preview_time);
		location = findViewById(R.id.event_preview_location);
		descText = findViewById(R.id.text_description);
		description = findViewById(R.id.event_preview_description);
		commentsList = findViewById(R.id.event_preview_comments);
		showAllComments = findViewById(R.id.event_show_all_comments);

		setSupportActionBar(toolbar);
		fetchData();

		showAllComments.setOnClickListener(view -> {
			Intent intent = new Intent(EventPreview.this, RatingsPreview.class);
			intent.putExtra("location_id", eventID);
			intent.putExtra("category", "events");
			startActivity(intent);
		});
	}

	private void fetchData() {
		document.get().addOnSuccessListener(snapshot -> {
			eventName = snapshot.getString("name");
			eventDateStart = snapshot.getString("dateStart");
			eventStart = snapshot.getString("start");
			openID = snapshot.getId();
			fetchImage(snapshot.getString("imageName"));

			String period = snapshot.getString("dateStart");

			if (!snapshot.getString("dateEnd").isEmpty()) {
				period += " - " + snapshot.getString("dateEnd");
			}
			if (!snapshot.getString("start").isEmpty()) {
				period += "\n" + snapshot.getString("start");
				if (!snapshot.getString("end").isEmpty()) {
					period += " - " + snapshot.getString("end");
				}
			}

			toolbar.setTitle(snapshot.getString("name"));
			toolbar.setSubtitle(snapshot.getString("town"));
			time.setText(period);
			location.setText(snapshot.getString("location"));

			if (snapshot.getString("description").isEmpty()) {
				descText.setVisibility(View.GONE);
				description.setVisibility(View.GONE);
			} else {
				description.setText(snapshot.getString("description"));
				description.resetState(true);
			}

		}).addOnFailureListener(e -> {
			Toast.makeText(this, "Pogreška: " + e, Toast.LENGTH_SHORT).show();
		});
	}

	private void fetchImage(String imageName) {
		StorageReference storage = FirebaseStorage.getInstance().getReference("images_events/" + imageName);

		try {
			File file = File.createTempFile("tempfile", ".jpeg");
			storage.getFile(file).addOnSuccessListener(taskSnapshot -> {
				Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
				image.setImageBitmap(bitmap);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setupRecycler() {
		Query query = collection.orderBy("time", Query.Direction.DESCENDING).limit(3);
		FirestoreRecyclerOptions<RatingModel> options = new FirestoreRecyclerOptions.Builder<RatingModel>()
				.setQuery(query, RatingModel.class)
				.build();

		adapter = new RatingAdapter(this, options);
		commentsList.setHasFixedSize(true);
		commentsList.setLayoutManager(new LinearLayoutManager(this));
		commentsList.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (type.equals("admin")) {
			getMenuInflater().inflate(R.menu.toolbar_menu_admin, menu);
		} else {
			getMenuInflater().inflate(R.menu.toolbar_menu_user_event, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int itemID = item.getItemId();

		switch (itemID) {
			case R.id.toolbar_edit_action:
				Intent intent = new Intent(EventPreview.this, EventEdit.class);
				intent.putExtra("event_id", eventID);
				startActivity(intent);
				return true;

			case R.id.toolbar_delete_action:
				showAlert();
				return true;

			case R.id.toolbar_reminder_action:
				alertReceiver = new AlertReceiver();
				alarmCalendar = Calendar.getInstance();
				pickDate();
				return true;

			case R.id.toolbar_bookmark_action:
				Intent intentBookmark = new Intent(EventPreview.this, PickBookmarksList.class);
				intentBookmark.putExtra("location_id", eventID);
				intentBookmark.putExtra("location_name", eventName);
				intentBookmark.putExtra("location_category", "event");
				startActivity(intentBookmark);
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void showAlert() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Pozor!")
				.setMessage("Jeste li sigurni da želite obrisati " + eventName + "?")
				.setPositiveButton("DA", (dialogInterface, i) -> deleteEvent())
				.setNegativeButton("NE", (dialogInterface, i) -> {
				});

		alert.create().show();
	}

	private void deleteEvent() {
		ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage("Brisanje...");
		pd.show();

		document.delete().addOnSuccessListener(unused -> {
			pd.dismiss();
			Toast.makeText(EventPreview.this, "Događaj uspješno obrisan.", Toast.LENGTH_SHORT).show();
			finish();

		}).addOnFailureListener(e -> {
			pd.dismiss();
			Toast.makeText(this, "Brisanje neuspješno. Pokušajte kasnije.", Toast.LENGTH_SHORT).show();
			finish();
		});
	}

	private void pickDate() {
		DialogFragment datePicker = new DatePickerFragment();
		datePicker.show(getSupportFragmentManager(), "Date picker");
	}

	@Override
	public void onDateSet(DatePicker datePicker, int year, int month, int day) {
		alarmCalendar.set(Calendar.YEAR, year);
		alarmCalendar.set(Calendar.MONTH, month);
		alarmCalendar.set(Calendar.DAY_OF_MONTH, day);

		pickTime();
	}

	private void pickTime() {
		DialogFragment timePicker = new TimePickerFragment();
		timePicker.show(getSupportFragmentManager(), "Time picker");
	}

	@Override
	public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
		alarmCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		alarmCalendar.set(Calendar.MINUTE, minute);
		alarmCalendar.set(Calendar.SECOND, 0);

		setAlarm(alarmCalendar);
	}

	private void setAlarm(Calendar calendar) {
		AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		/*
		Intent openIntent = new Intent(this, OpenReminder.class);
		PendingIntent pd = PendingIntent.getActivity(this, 0, openIntent, 0);*/

		Intent intent = new Intent(this, AlertReceiver.class);
		intent.putExtra("alarm_title", "Imate nadolazeći događaj!");
		intent.putExtra("alarm_message", eventName + " započinje " + eventDateStart + " u " + eventStart + " sati.");
		intent.putExtra("open_id", openID);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
		Toast.makeText(this, "Podsjetnik postavljen za " + eventName + ".", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onStart() {
		super.onStart();

		setupRecycler();
		adapter.startListening();
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		setupRecycler();
		adapter.startListening();
	}

	@Override
	protected void onStop() {
		super.onStop();

		adapter.stopListening();
	}
}