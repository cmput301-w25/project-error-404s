package com.example.uiapp;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.uiapp.databinding.ActivityBottomNavBinding;
import com.google.android.material.card.MaterialCardView;

public class BottomNavActivity extends AppCompatActivity {

    private ActivityBottomNavBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBottomNavBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        MaterialCardView navViewCard = findViewById(R.id.nav_view_card);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeModeFragment, R.id.navigation_dashboard, R.id.navigation_fragment_add, R.id.navigation_notifications, R.id.navigation_home)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_bottom_nav);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.editModeFragment) {
                navViewCard.setVisibility(View.GONE);
            } else {
                navViewCard.setVisibility(View.VISIBLE);
            }
        });
    }

}