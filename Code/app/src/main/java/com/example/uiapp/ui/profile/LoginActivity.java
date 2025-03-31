package com.example.uiapp.ui.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uiapp.MainActivity;
import com.example.uiapp.R;
import com.example.uiapp.databinding.ActivityLoginBinding;
import com.example.uiapp.databinding.ActivitySignupBinding;
import com.example.uiapp.model.UserModel;
import com.example.uiapp.utils.HelperClass;
import com.example.uiapp.utils.SharedPrefHelper;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    private FirebaseFirestore db;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Fetching data...");
        progressDialog.setCancelable(false);

        binding.loginBtn.setOnClickListener(v -> {
            String username = binding.editTextUsername.getText().toString().trim();
            String password = binding.editTextTextPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
            } else {
                userLogin(username, password);
            }
        });

        binding.llSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

    }

    private void userLogin(String username, String password) {
        progressDialog.show();
        DocumentReference userRef = db.collection("Users").document(username);

        userRef.get().addOnSuccessListener(document -> {
            if (!document.exists()) {
                progressDialog.dismiss();
                Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
            } else {
                String storedPassword = document.getString("password");
                if (storedPassword != null && storedPassword.equals(password)) {
                    UserModel loggedInUser = document.toObject(UserModel.class);
                    HelperClass.users = loggedInUser;
                    saveUserToPreferences(loggedInUser);
                    goToMainActivity(username);
                } else {
                    Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void saveUserToPreferences(UserModel user) {
        SharedPrefHelper.saveUser(this, user);
    }

    private void goToMainActivity(String username) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finishAffinity();
    }

}