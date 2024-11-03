package com.example.fitfeed.activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.fitfeed.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.fitfeed.databinding.ActivityGymSelectorMapsBinding;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.CircularBounds;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.SearchNearbyRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Activity to display nearby gyms and save selection
 */
public class GymSelectorMapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    private String selectedGym = "";
    private GoogleMap mMap;
    private ActivityGymSelectorMapsBinding binding;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private LatLng currentLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private final float ZOOM_LEVEL_INIT = 15.0f;
    private PlacesClient placesClient;

    private FloatingActionButton saveButton;
    private FloatingActionButton cancelButton;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted
            getDeviceLocation();
        }
    }

    /**
     * Handle a permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted
                getDeviceLocation();
            } else {
                // Denied
                Toast.makeText(this, R.string.location_permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGymSelectorMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Listener for save
        saveButton = this.findViewById(R.id.saveHomeGym);
        saveButton.setOnClickListener(v -> {
            if (!Objects.equals(selectedGym, "")) {
                SharedPreferences sharedPreferences = getSharedPreferences("com.example.fitfeed", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("home_gym", selectedGym);
                editor.apply();
            }
            finish();
        });

        // Listener for cancel
        cancelButton = this.findViewById(R.id.cancelHomeGym);
        cancelButton.setOnClickListener(v -> finish());

        // Get the SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.maps_api_key));
        }

        placesClient = Places.createClient(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        // Check location permission again before enabling user location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getDeviceLocation(); // Get the device location
        } else {
            checkLocationPermission();
        }
    }

    /**
     * Get the device's location and add markers on nearby gyms
     * Always called after permission is granted
     */
    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        mMap.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        // Get the current location and move the camera to it
                        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, ZOOM_LEVEL_INIT));

                        // Mark nearby gyms
                        findNearbyGyms(currentLocation, places -> {
                            for (Place place : places) {
                                if (place.getLocation() != null) { // Ensure Location is not null
                                    mMap.addMarker(new MarkerOptions()
                                            .position(place.getLocation())
                                            .title(place.getDisplayName()));
                                } else {
                                    Log.e("TAG", "Place " + place.getDisplayName() + " has null location");
                                }
                            }
                        });
                    } else { // No previous location
                        requestNewLocationData();
                    }
                })
                .addOnFailureListener(e -> {
                    // Failure to get location (no gps, maybe indoors, etc.)
                    Toast.makeText(this, R.string.error_getting_location + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    /**
     * Request location data if no previous location can be found
     */
    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        // TODO: Use newer Location builder instead of LocationRequest
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(4999);
        locationRequest.setNumUpdates(1);

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    /**
     * Callback for async location
     */
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            if (location != null) {
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, ZOOM_LEVEL_INIT));

                // Find nearby gyms based on the new location
                findNearbyGyms(currentLocation, places -> {
                    for (Place place : places) {
                        if (place.getLocation() != null) {
                            mMap.addMarker(new MarkerOptions()
                                    .position(place.getLocation())
                                    .title(place.getDisplayName()));
                        }
                    }
                });
            }
        }
    };

    /**
     * Callback for async places search
     */
    private interface PlacesCallback {
        void onPlacesFound(List<Place> places);
    }

    /**
     * Use places (new) api to find a list of 20 nearby gyms in a 10km radius
     */
    @SuppressLint("MissingPermission")
    private void findNearbyGyms(LatLng currentLocation, PlacesCallback callback) {
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.DISPLAY_NAME, Place.Field.LOCATION);
        final List<String> includedTypes = Arrays.asList("gym");
        CircularBounds circle = CircularBounds.newInstance(currentLocation, 10000);
        final SearchNearbyRequest searchNearbyRequest =
                SearchNearbyRequest.builder(circle, placeFields)
                        .setIncludedTypes(includedTypes)
                        .setMaxResultCount(20)
                        .build();

        placesClient.searchNearby(searchNearbyRequest)
                .addOnSuccessListener(response -> {
                    List<Place> places = response.getPlaces();
                    // Pass the list of places back through the callback
                    if (callback != null) {
                        callback.onPlacesFound(places);
                    }
                })
                .addOnFailureListener(exception -> {
                    Log.e("TAG", "Error finding places: " + exception.getMessage());
                });
    }

    /**
     * Select a marked gym
     */
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        selectedGym = marker.getTitle();
        return false;
    }
}