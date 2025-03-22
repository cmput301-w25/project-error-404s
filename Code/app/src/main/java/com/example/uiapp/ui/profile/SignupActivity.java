package com.example.uiapp.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uiapp.MainActivity;
import com.example.uiapp.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextPassword;
    private Button LoginBTN, SignupBTN;
    private TextView toggleText;
    private FirebaseFirestore db;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        SignupBTN = findViewById(R.id.SignupBtn);
        LoginBTN = findViewById(R.id.LoginBtn);

        db = FirebaseFirestore.getInstance();

        SignupBTN.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
            } else {
                userCheck(username, password);
            }
        });

        LoginBTN.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
            } else {
                userLogin(username, password);
            }
        });

    }

    private void userCheck(String username, String password) {
        DocumentReference usrRef = db.collection("users").document(username);
        usrRef.get().addOnSuccessListener(document -> {
            if (document.exists()) {
                Toast.makeText(this, "Username exists W's", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "registerUser", Toast.LENGTH_SHORT).show();
                registerUser(username, password);
            }
        });
    }
    private void registerUser(String username, String password){
        Map<String, Object> userData = new HashMap<>();
        userData.put("password", password);
        db.collection("users").document(username).set(userData);
        goToMainActivity(username);
    }

    private void userLogin(String username, String password){
        DocumentReference usrRef = db.collection("users").document(username);
        usrRef.get().addOnSuccessListener(document -> {
            if (!document.exists()) {
                Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
            } else {
                String storedPassword = document.getString("password");
                if (storedPassword != null && storedPassword.equals(password)) {
                    goToMainActivity(username);
                } else {
                    Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goToMainActivity(String username) {
        // Save username to SharedPreferences
        getSharedPreferences("MoodPulsePrefs", MODE_PRIVATE)
                .edit()
                .putString("USERNAME", username)
                .apply();

        startActivity(new Intent(this, MainActivity.class));
        finish(); // Close SignupActivity
    }

}
