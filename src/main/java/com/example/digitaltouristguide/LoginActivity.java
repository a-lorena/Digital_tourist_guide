package com.example.digitaltouristguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitaltouristguide.Admin.MainAdmin;
import com.example.digitaltouristguide.User.HomeUser;
import com.example.digitaltouristguide.User.StartUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

	EditText emailET, passwordET;
	Button loginButton;
	TextView registerLink;

	FirebaseAuth auth;

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;
	public static final String fileName = "login";
	public static final String userType = "type";
	public static final String pickedTown = "id";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		emailET = findViewById(R.id.email_input);
		passwordET = findViewById(R.id.password_input);
		loginButton = findViewById(R.id.login_button);
		registerLink = findViewById(R.id.register_link);

		auth = FirebaseAuth.getInstance();
		sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);

		loginButton.setOnClickListener(view -> {
			String emailTXT = emailET.getText().toString();
			String passwordTXT = passwordET.getText().toString();

			if (checkEmptyFields(emailTXT, passwordTXT)) {
				if (checkAdmin(emailTXT, passwordTXT)) {
					loginAdmin(emailTXT, passwordTXT);
				} else {
					loginUser(emailTXT, passwordTXT);
				}
			}
		});

		registerLink.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));
	}

	private void loginAdmin(String email, String password) {
		editor = sharedPreferences.edit();
		editor.putString(userType, "admin");
		editor.commit();

		auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
			Toast.makeText(LoginActivity.this, "Prijava uspješna.", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(LoginActivity.this, MainAdmin.class));
			finish();
		});
	}

	private void loginUser(String email, String password) {
		editor = sharedPreferences.edit();
		editor.putString(userType, "user");
		editor.commit();

		auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
			Toast.makeText(LoginActivity.this, "Prijava uspješna.", Toast.LENGTH_SHORT).show();

			if (!sharedPreferences.getString(pickedTown, "").isEmpty()) {
				startActivity(new Intent(this, HomeUser.class));
			} else {
				startActivity(new Intent(LoginActivity.this, StartUser.class));
			}

			finish();
		});
	}

	private boolean checkAdmin(String email, String password) {
		if (email.equals("admin@mail.com") && password.equals("admin0")) {
			return true;
		}

		return false;
	}

	private boolean checkEmptyFields(String email, String password) {
		if (email.isEmpty() || password.isEmpty()) {
			Toast.makeText(this, "Ispunite sva polja!", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();

		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

		if (user != null) {
			if (user != null) {
				startActivity(new Intent(LoginActivity.this, LoadingUserActivity.class));

			}

			/*if (user.getEmail().equals("admin@mail.com")) {
				startActivity(new Intent(LoginActivity.this, MainAdmin.class));
			} else {
				if (!sharedPreferences.getString(pickedTown, "").isEmpty()) {
					startActivity(new Intent(LoginActivity.this, HomeUser.class));
				} else {
					startActivity(new Intent(LoginActivity.this, StartUser.class));
				}
			}*/
		}
	}
}