package com.example.uiapp.ui;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import com.google.android.gms.maps.model.LatLng;

public class LocationHelper {
    private final FusedLocationProviderClient fusedLocationClient;
    private final Geocoder geocoder;

    public LocationHelper(Context context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        geocoder = new Geocoder(context, Locale.getDefault());
    }

    public interface LocationCallback {
        void onLocationResult(String address);
        void onLocationError(String error);
    }

    public void getCurrentLocation(LocationCallback callback) {
        try {
            Task<android.location.Location> locationTask = fusedLocationClient.getLastLocation();

            locationTask.addOnSuccessListener(location -> {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    getAddressFromLocation(latitude, longitude, callback);
                } else {
                    callback.onLocationError("Location not available");
                }
            });
        } catch (SecurityException e) {
            callback.onLocationError("Location permission denied");
        }
    }

    private void getAddressFromLocation(double lat, double lng, LocationCallback callback) {
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressString = String.format(Locale.getDefault(),
                        // Dont know how specific we have to get with this
                        "%s, %s, %s",
                        address.getThoroughfare(),  // Street name
                        address.getLocality(),       // City
                        address.getCountryName()     // Country
                );
                callback.onLocationResult(addressString);
            } else {
                callback.onLocationError("No address found");
            }
        } catch (IOException e) {
            callback.onLocationError("Geocoder error: " + e.getMessage());
        }
    }

    // Implementing the map function
    public LatLng getLatLngFromAddress(String addressString) {
        try {
            List<Address> addresses = geocoder.getFromLocationName(addressString, 1);
            if (!addresses.isEmpty() && addresses.get(0) != null) {
                Address address = addresses.get(0);
                return new LatLng(
                        address.getLatitude(),
                        address.getLongitude()
                );
            }
        } catch (IOException e) {
            Log.e("LocationHelper", "Geocoding error: " + e.getMessage());
        }
        return null;
    }




}
