package com.example.digitaltouristguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.digitaltouristguide.Admin.MainAdmin;
import com.example.digitaltouristguide.User.HomeUser;
import com.example.digitaltouristguide.User.StartUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoadingUserActivity extends AppCompatActivity {

	SharedPreferences sharedPreferences;
	public static final String fileName = "login";
	public static final String userType = "type";
	public static final String pickedTown = "id";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading_user);

		sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);


		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

		if (user.getEmail().equals("admin@mail.com")) {
			startActivity(new Intent(LoadingUserActivity.this, MainAdmin.class));
		} else {
			if (!sharedPreferences.getString(pickedTown, "").isEmpty()) {
				startActivity(new Intent(LoadingUserActivity.this, HomeUser.class));
			} else {
				startActivity(new Intent(LoadingUserActivity.this, StartUser.class));
			}
		}
	}
}