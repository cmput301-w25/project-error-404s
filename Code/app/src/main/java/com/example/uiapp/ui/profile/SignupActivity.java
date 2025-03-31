package com.example.uiapp.ui.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.uiapp.databinding.ActivitySignupBinding;
import com.example.uiapp.model.MoodEntry;
import com.example.uiapp.model.UserModel;
import com.example.uiapp.utils.HelperClass;
import com.example.uiapp.utils.SharedPrefHelper;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SignupActivity extends AppCompatActivity {
    ActivitySignupBinding binding;
    private FirebaseFirestore db;
    ProgressDialog progressDialog;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Fetching data...");
        progressDialog.setCancelable(false);
        db = FirebaseFirestore.getInstance();

        binding.SignupBtn.setOnClickListener(v -> {
            String username = binding.editTextUsername.getText().toString().trim();
            String password = binding.editTextTextPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
            } else {
                userCheck(username, password);
            }
        });

        binding.llLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });

    }

    private void userCheck(String username, String password) {
        progressDialog.show();
        DocumentReference usrRef = db.collection("Users").document(username);
        usrRef.get().addOnSuccessListener(document -> {
            if (document.exists()) {
                progressDialog.dismiss();
                Toast.makeText(this, "Username exists W's", Toast.LENGTH_SHORT).show();
            } else {
                registerUser(username, password);
            }
        }).addOnFailureListener(command -> {
            progressDialog.dismiss();
            Toast.makeText(this, command.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }

    private void registerUser(String username, String password) {
        UserModel newUser = new UserModel(username, password);
        db.collection("Users").document(username)
                .set(newUser)
                .addOnSuccessListener(aVoid -> {
                    HelperClass.users = newUser;
                    saveUserToPreferences(newUser);
                    progressDialog.dismiss();
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    goToMainActivity(username);
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveUserToPreferences(UserModel user) {
        SharedPrefHelper.saveUser(this, user);
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
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}
