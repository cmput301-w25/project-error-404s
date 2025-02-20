package com.example.mood_pulse;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.widget.Toolbar;

import com.example.mood_pulse.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate and set the content view first!
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Find and set the custom toolbar
        Toolbar customToolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(customToolbar);

        // Disable the default title display
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Set up the back button behavior
        ImageButton backButton = findViewById(R.id.back_button);
        if (backButton != null) {
            backButton.setOnClickListener(view -> onBackPressed());
        }

        // Bottom navigation setup
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search,
                R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_history)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // **Update Toolbar Title Dynamically**
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (toolbarTitle != null && destination.getLabel() != null) {
                toolbarTitle.setText(destination.getLabel());
            }

            // Show right-side buttons only on the home page (assuming home has id R.id.navigation_home)
            LinearLayout rightButtons = findViewById(R.id.right_buttons_container);
            if (destination.getId() == R.id.navigation_home) {
                rightButtons.setVisibility(View.VISIBLE);
            } else {
                rightButtons.setVisibility(View.GONE);
            }

        });


        View rootView = findViewById(android.R.id.content);
        rootView.setBackgroundColor(getResources().getColor(R.color.app_background));

    }
}
