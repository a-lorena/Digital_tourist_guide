package com.example.digitaltouristguide.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitaltouristguide.Constants;
import com.example.digitaltouristguide.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class SettingsActivity extends AppCompatActivity {

	SwitchCompat newTownSwitch, newTownLocationSwitch, newAllLocationsSwitch, newCommentSwitch;
	TextView pickTown, editData;
	String townName, userID;

	private boolean isCheckedNewTownNotification = true;
	private boolean isCheckedTownLocationNotification = true;
	private boolean isCheckedAllLocationsNotification = true;
	private boolean isCheckedCommentNotification = true;
	private SharedPreferences sharedPreferencesTown, sharedPreferencesLocation;
	private SharedPreferences.Editor editor;

	private static final String enabledMessage = "Obavijesti su uključene.";
	private static final String disabledMessage = "Obavijesti su isključene.";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		newTownSwitch = findViewById(R.id.new_town_switch);
		//newAllLocationsSwitch = findViewById(R.id.new_all_locations_switch);
		newTownLocationSwitch = findViewById(R.id.new_town_location_switch);
		newCommentSwitch = findViewById(R.id.new_comment_switch);
		pickTown = findViewById(R.id.settings_pick_town);
		editData = findViewById(R.id.settings_edit_profile);

		sharedPreferencesTown = getSharedPreferences("SETTINGS_SP", MODE_PRIVATE);
		sharedPreferencesLocation = getSharedPreferences("login", MODE_PRIVATE);
		townName = sharedPreferencesLocation.getString("pickedTownName", "");
		userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

		isCheckedNewTownNotification = sharedPreferencesTown.getBoolean("" + Constants.NEW_TOWN_TOPIC, true);
		//isCheckedAllLocationsNotification = sharedPreferencesLocation.getBoolean("" + Constants.NEW_ALL_LOCATIONS_TOPIC, true);
		isCheckedTownLocationNotification = sharedPreferencesLocation.getBoolean("" + townName, true);
		isCheckedCommentNotification = sharedPreferencesTown.getBoolean("" + userID, true);

		newTownSwitch.setChecked(isCheckedNewTownNotification);
		//newAllLocationsSwitch.setChecked(isCheckedAllLocationsNotification);
		newTownLocationSwitch.setChecked(isCheckedTownLocationNotification);
		newCommentSwitch.setChecked(isCheckedCommentNotification);

		newTownSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
			editor = sharedPreferencesTown.edit();
			editor.putBoolean("" + Constants.NEW_TOWN_TOPIC, isChecked);
			editor.apply();

			if (isChecked) { subscribeToNewTown();
			} else { unsubscribeFromNewTown(); }
		});

		/*newAllLocationsSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
			editor = sharedPreferencesTown.edit();
			editor.putBoolean("" + Constants.NEW_ALL_LOCATIONS_TOPIC, isChecked);
			editor.apply();

			if (isChecked) { subscribeToNewAllLocations();
			} else { unsubscribeFromNewAllLocations(); }
		});*/

		newTownLocationSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
			editor = sharedPreferencesLocation.edit();
			editor.putBoolean("" + townName, isChecked);
			editor.apply();

			if (isChecked) { subscribeToNewTownLocation();
			} else { unsubscribeFromNewTownLocation(); }
		});

		newCommentSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
			editor = sharedPreferencesTown.edit();
			editor.putBoolean("" + userID, isChecked);
			editor.apply();

			if (isChecked) {
				subscribeToNewComments();
			} else {
				unsubscribeFromNewComments();
			}
		});

		pickTown.setOnClickListener(view -> { startActivity(new Intent(SettingsActivity.this, StartUser.class)); });
		editData.setOnClickListener(view -> { startActivity(new Intent(SettingsActivity.this, SettingsEditData.class)); });
	}

	private void subscribeToNewTown() {
		FirebaseMessaging.getInstance().subscribeToTopic(Constants.NEW_TOWN_TOPIC)
				.addOnSuccessListener(unused -> {
					Toast.makeText(this, "" + enabledMessage, Toast.LENGTH_SHORT).show();
				}).addOnFailureListener(e -> {
					Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
				});
	}

	private void unsubscribeFromNewTown() {
		FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.NEW_TOWN_TOPIC)
				.addOnSuccessListener(unused -> {
					Toast.makeText(this, "" + disabledMessage, Toast.LENGTH_SHORT).show();
				}).addOnFailureListener(e -> {
					Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
				});
	}

	/*private void subscribeToNewAllLocations() {
		FirebaseMessaging.getInstance().subscribeToTopic(Constants.NEW_ALL_LOCATIONS_TOPIC)
				.addOnSuccessListener(unused -> {
					Toast.makeText(this, "" + enabledMessage, Toast.LENGTH_SHORT).show();
				}).addOnFailureListener(e -> {
			Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
		});
	}

	private void unsubscribeFromNewAllLocations() {
		FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.NEW_ALL_LOCATIONS_TOPIC)
				.addOnSuccessListener(unused -> {
					Toast.makeText(this, "" + disabledMessage, Toast.LENGTH_SHORT).show();
				}).addOnFailureListener(e -> {
			Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
		});
	}*/

	private void subscribeToNewTownLocation() {
		FirebaseMessaging.getInstance().subscribeToTopic(townName)
				.addOnSuccessListener(unused -> {
					Toast.makeText(this, "" + enabledMessage, Toast.LENGTH_SHORT).show();
				}).addOnFailureListener(e -> {
			Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
		});
	}

	private void unsubscribeFromNewTownLocation() {
		FirebaseMessaging.getInstance().unsubscribeFromTopic(townName)
				.addOnSuccessListener(unused -> {
					Toast.makeText(this, "" + disabledMessage, Toast.LENGTH_SHORT).show();
				}).addOnFailureListener(e -> {
			Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
		});
	}

	private void subscribeToNewComments() {
		FirebaseMessaging.getInstance().subscribeToTopic(userID)
				.addOnSuccessListener(unused -> {
					Toast.makeText(this, "" + enabledMessage, Toast.LENGTH_SHORT).show();
				}).addOnFailureListener(e -> {
			Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
		});
	}

	private void unsubscribeFromNewComments() {
		FirebaseMessaging.getInstance().unsubscribeFromTopic(userID)
				.addOnSuccessListener(unused -> {
					Toast.makeText(this, "" + disabledMessage, Toast.LENGTH_SHORT).show();
				}).addOnFailureListener(e -> {
			Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
		});
	}
}