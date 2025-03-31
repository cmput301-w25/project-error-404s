package com.example.uiapp.ui.map;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.uiapp.MainActivity;
import com.example.uiapp.R;
import com.example.uiapp.adapter.CustomInfoWindowAdapter;
import com.example.uiapp.databinding.FragmentMapBinding;
import com.example.uiapp.model.MoodEntry;
import com.example.uiapp.utils.HelperClass;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles all the functionalities and activiteis that is required to set up the fragment that
 * display the map
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    private FragmentMapBinding binding;
    private MapViewModel mapViewModel;
    private ProgressDialog progressDialog;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentUserLocation;
    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.filterButton.setOnClickListener(view1 ->
                Navigation.findNavController(view1).navigate(R.id.action_mapFragment_to_mapFilter)
        );

        mapViewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Fetching data...");
        progressDialog.setCancelable(true);

        // Initialize Location Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Fetch user's current location
        fetchUserLocation();
    }

    @SuppressLint("MissingPermission")
    private void fetchUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentUserLocation = location;
                initializeMap();
            }
        });
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.e("aaa", "onMapReady: " );
        googleMap = map;
        observeMoodEntries();
    }

    @Override
    public void onResume() {
        super.onResume();
        showProgressDialog();
        mapViewModel.fetchMoodEntries();
    }

    private void observeMoodEntries() {
        mapViewModel.getFilteredMoodEntries().observe(getViewLifecycleOwner(), moodEntries -> {
            hideProgressDialog();
            if (googleMap == null || currentUserLocation == null) return;

            googleMap.clear(); // Clear previous markers
            int maxDistance = MainActivity.moodDistance; // Distance in km

            List<MoodEntry> filteredMoods = new ArrayList<>();
            for (MoodEntry moodEntry : moodEntries) {
                double moodLat = Double.parseDouble(moodEntry.getLocationLat());
                double moodLng = Double.parseDouble(moodEntry.getLocationLng());

                if (isWithinDistance(moodLat, moodLng, maxDistance)) {
                    filteredMoods.add(moodEntry);

                    LatLng location = new LatLng(moodLat, moodLng);
                    BitmapDescriptor markerIcon = getBitmapDescriptor(R.drawable.iv_custom_marker);
                    String title = "";
                    if (!moodEntry.getUsername().equals(HelperClass.users.getUsername())) {
                        title = moodEntry.getUsername();
                    }
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(location)
                            .title(title)
                            .icon(markerIcon);

                    Marker marker = googleMap.addMarker(markerOptions);
                    if (marker != null) {
                        marker.setTag(moodEntry.getMood());  // Store mood icon as tag
                    }
                }
            }

            // Move camera if there are moods
            if (!filteredMoods.isEmpty()) {
                googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(requireContext()));
                LatLng firstLocation = new LatLng(
                        Double.parseDouble(filteredMoods.get(0).getLocationLat()),
                        Double.parseDouble(filteredMoods.get(0).getLocationLng())
                );
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 12f));

            } else {
                // Show "No Mood Found" and display user’s location
                googleMap.setInfoWindowAdapter(null);
                BitmapDescriptor moodIcon = getBitmapDescriptor(R.drawable.iv_custom_marker);
                LatLng userLatLng = new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude());
                googleMap.addMarker(new MarkerOptions()
                        .position(userLatLng)
                        .title("Your Location")
                        .icon(moodIcon)
                );
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14f));
            }
        });
    }


    private boolean isWithinDistance(double lat, double lng, int maxDistanceKm) {
        float[] results = new float[1];
        Location.distanceBetween(
                currentUserLocation.getLatitude(), currentUserLocation.getLongitude(),
                lat, lng,
                results
        );
        float distanceInMeters = results[0];
        return (distanceInMeters / 1000) <= maxDistanceKm; // Convert to km
    }

    private BitmapDescriptor getBitmapDescriptor(int drawableId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(requireContext(), drawableId);
        if (vectorDrawable == null) {
            return BitmapDescriptorFactory.defaultMarker();
        }
        int width = vectorDrawable.getIntrinsicWidth();
        int height = vectorDrawable.getIntrinsicHeight();
        vectorDrawable.setBounds(0, 0, width, height);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void showProgressDialog() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchUserLocation();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapViewModel.clearFilter();
    }
}
