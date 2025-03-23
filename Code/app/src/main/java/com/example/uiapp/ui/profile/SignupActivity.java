package com.example.uiapp.ui.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiapp.MainActivity;
import com.example.uiapp.R;
import com.example.uiapp.adapter.EmojiAdapter;
import com.example.uiapp.adapter.MoodAdapter;
import com.example.uiapp.model.MoodEntry;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
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

    private void switchToMainLayout() {
        // Change the content view to activity_main.xml
        setContentView(R.layout.activity_main);

        // Now reinitialize all UI elements from activity_main.xml.
        // For example, if activity_main.xml contains a Toolbar and a RecyclerView:
        @SuppressLint("WrongViewCast") Toolbar toolbar = findViewById(R.id.main); // Make sure the IDs match activity_main.xml
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // Initialize any adapters, listeners, etc.
        // For instance, set up your RecyclerView:
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<MoodEntry> MoodEntry;
        // MoodAdapter adapter = new MoodAdapter(this, MoodEntry, null, null);
        // recyclerView.setAdapter(adapter);


        // You might also want to perform any logic that MainActivity normally does.
    }


    private void goToMainActivity(String username) {
        getSharedPreferences("MoodPulsePrefs", MODE_PRIVATE)
                .edit()
                .putString("USERNAME", username)
                .apply();
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}
