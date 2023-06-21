package com.example.digitaltouristguide;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.digitaltouristguide.Admin.MainAdmin;
import com.example.digitaltouristguide.User.HomeUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

	EditText name, email, password, passwordRepeat;
	Button register;

	FirebaseAuth auth;
	FirebaseFirestore db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);

		name = findViewById(R.id.name_register);
		email = findViewById(R.id.email_register);
		password = findViewById(R.id.password_register);
		passwordRepeat = findViewById(R.id.password_repeat);
		register = findViewById(R.id.register_button);

		auth = FirebaseAuth.getInstance();
		db = FirebaseFirestore.getInstance();

		register.setOnClickListener(view -> { getInput(); });
	}

	private void startApp(String email) {
		if (email.equals("admin@mail.com")) {
			startActivity(new Intent(RegistrationActivity.this, MainAdmin.class));
			finish();
		} else {
			startActivity(new Intent(RegistrationActivity.this, HomeUser.class));
			finish();
		}
	}

	private void registerUser(String name, String email, String password) {
		ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage("Registracija u tijeku...");
		pd.show();

		Map<String, Object> user = new HashMap<>();
		user.put("name", name);
		user.put("email", email);
		user.put("profile", "");
		user.put("header", "");

		auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
			String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

			db.collection("users").document(userID).set(user).addOnSuccessListener(unused -> {
				pd.dismiss();
				Toast.makeText(RegistrationActivity.this, "Registracija uspješna!", Toast.LENGTH_SHORT).show();
				startApp(email);
			});
		}).addOnFailureListener(e -> {
			pd.dismiss();
			Toast.makeText(RegistrationActivity.this, "error: " + e, Toast.LENGTH_SHORT).show();
		});
	}

	private boolean checkEmptyField(String name, String email, String password, String passwordRepeat) {
		if (name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordRepeat.isEmpty()) {
			Toast.makeText(this, "Ispunite sva polja!", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (!password.equals(passwordRepeat)) {
			Toast.makeText(this, "Lozinke nisu jednake!", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	private void getInput() {
		String nameTXT = name.getText().toString();
		String emailTXT = email.getText().toString();
		String passwordTXT = password.getText().toString();
		String passwordRepeatTXT = passwordRepeat.getText().toString();

		if (checkEmptyField(nameTXT, emailTXT, passwordTXT, passwordRepeatTXT)) {
			registerUser(nameTXT, emailTXT, passwordTXT);
		}
	}
}