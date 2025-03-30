package com.example.uiapp.ui.notifications;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.uiapp.ui.LocationHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.uiapp.R;
import com.example.uiapp.model.MoodEntry;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.HashMap;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private ListenerRegistration moodListener;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private final Map<String, Float> moodColorMap = new HashMap<>();

    private ImageButton filterButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        initializeMoodColors();
        setupMapFragment(root);

        // Initialize filter button
        filterButton = root.findViewById(R.id.filter_button);
        if (filterButton != null) {
            filterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to MapFilter
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_bottom_nav);
                    navController.navigate(R.id.action_navigation_notifications_to_mapFilter);
                }
            });
        }

        return root;
    }

    private void initializeMoodColors() {
        moodColorMap.put("Happy", BitmapDescriptorFactory.HUE_GREEN);
        moodColorMap.put("Sad", BitmapDescriptorFactory.HUE_BLUE);
        moodColorMap.put("Fear", BitmapDescriptorFactory.HUE_VIOLET);
        moodColorMap.put("Disgust", BitmapDescriptorFactory.HUE_MAGENTA);
        moodColorMap.put("Anger", BitmapDescriptorFactory.HUE_RED);
        moodColorMap.put("Confused", BitmapDescriptorFactory.HUE_YELLOW);
        moodColorMap.put("Shame", BitmapDescriptorFactory.HUE_ROSE);
        moodColorMap.put("Surprised", BitmapDescriptorFactory.HUE_CYAN);
        moodColorMap.put("Tired", BitmapDescriptorFactory.HUE_AZURE);
        moodColorMap.put("Anxious", BitmapDescriptorFactory.HUE_MAGENTA);
        moodColorMap.put("Proud", BitmapDescriptorFactory.HUE_GREEN);
        moodColorMap.put("Bored", BitmapDescriptorFactory.HUE_ORANGE);
    }

    private void setupMapFragment(View root) {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

//    private void setupRealTimeUpdates() {
//        db = FirebaseFirestore.getInstance();
//
//        /** ------------ CHANGE THIS BASED ON HOW DATABASE IS FIGURED OUT TO BE -----------------------------  **/
//        /// //////////////////////////////////////////////////////////////////////////
//        moodListener = db.collection("MoodEvents")
//                .addSnapshotListener((value, error) -> {
//                    if (error != null) return;
//
//                    mMap.clear();
//                    for (QueryDocumentSnapshot doc : value) {
//                        MoodEntry entry = doc.toObject(MoodEntry.class);
//                        entry.setDocumentId(doc.getId());
//                        addCustomMarker(entry);
//                    }
//                });
//        /// //////////////////////////////////////////////////////////////////////////
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
            centerMapOnCurrentLocation();

        } else {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }

        // Uncomment when ready for Firestore
        // setupRealTimeUpdates();
    }

    private String formatDate(String dateTime) {
        // Keeping only the date here
        return dateTime.split(" \\| ")[0];
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    centerMapOnCurrentLocation();
                }
            }
        }
    }

    private void centerMapOnCurrentLocation() {
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            LatLng currentLatLng = new LatLng(
                                    location.getLatitude(),
                                    location.getLongitude()
                            );
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                        }
                    });
        } catch (SecurityException e) {
            Log.e("MapFragment", "Security Exception: " + e.getMessage());
        }
    }


//    private void addCustomMarker(MoodEntry entry) {
//        BitmapDescriptor icon = createEmojiMarker(entry.getMoodIcon(), entry.getDateTime());
//        LatLng location = new LocationHelper(requireContext()).getLatLngFromAddress(entry.getLocation());
//
//        Marker marker = mMap.addMarker(new MarkerOptions()
//                .position(location)
//                .title(entry.getMood())
//                .snippet(entry.getDateTime())
//                .icon(icon));
//
//        if (marker != null) {
//            marker.setTag(entry.getDocumentId());
//        }
//    }

    private BitmapDescriptor createEmojiMarker(int emojiResId, String date) {
        // Create custom marker with emoji and date
        View markerView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_marker, null);
        ImageView emoji = markerView.findViewById(R.id.emoji_image);
        TextView dateText = markerView.findViewById(R.id.date_text);

        emoji.setImageResource(emojiResId);
        dateText.setText(formatDate(date));

        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(markerView.getMeasuredWidth(),
                markerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerView.layout(0, 0, markerView.getMeasuredWidth(), markerView.getMeasuredHeight());
        markerView.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String documentId = (String) marker.getTag();
        navigateToMoodEntry(documentId);
        return true;
    }

    private void navigateToMoodEntry(String documentId) {
        Bundle args = new Bundle();
        args.putString("DOCUMENT_ID", documentId);

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_bottom_nav);
        navController.navigate(R.id.navigation_home, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (moodListener != null) moodListener.remove();
    }
}
