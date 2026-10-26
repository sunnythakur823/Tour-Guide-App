package com.example.tourguideapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourguideapp.adapters.NearbyPlaceAdapter;
import com.example.tourguideapp.models.NearbyPlace;
import com.example.tourguideapp.utils.LocationHelper;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.CircularBounds;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.SearchNearbyRequest;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NearMeActivity extends AppCompatActivity implements NearbyPlaceAdapter.OnPlaceClickListener {

    private static final String TAG = "NEAR_ME";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private LocationHelper locationHelper;
    private PlacesClient placesClient;
    private NearbyPlaceAdapter adapter;

    private TextView tvLocationStatus;
    private ProgressBar progressBar;
    private RecyclerView rvNearbyPlaces;
    private View emptyState;
    private ChipGroup chipGroupCategories;
    private Spinner spinnerRadius;

    private Location currentUserLocation;
    private String selectedType = "tourist_attraction";
    private int selectedRadiusMeters = 10000; // Default 10km

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_me);

        initUI();
        
        if (performDiagnostics()) {
            initPlaces();
            locationHelper = new LocationHelper(this);
            checkLocationPermission();
        }
    }

    private boolean performDiagnostics() {
        String apiKey = BuildConfig.PLACES_API_KEY;
        
        if (apiKey == null || apiKey.isEmpty()) {
            showError("API Error: API Key is missing in secrets.properties.");
            return false;
        }
        
        if (apiKey.equals("PASTE_MY_API_KEY_HERE") || apiKey.equals("DEFAULT_API_KEY")) {
            showError("API Error: Please replace the placeholder API Key in secrets.properties.");
            return false;
        }
        
        Log.d(TAG, "Diagnostics passed. API Key found (length: " + apiKey.length() + ")");
        return true;
    }

    private void initUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvLocationStatus = findViewById(R.id.tvLocationStatus);
        progressBar = findViewById(R.id.progressBar);
        rvNearbyPlaces = findViewById(R.id.rvNearbyPlaces);
        emptyState = findViewById(R.id.emptyState);
        chipGroupCategories = findViewById(R.id.chipGroupCategories);
        spinnerRadius = findViewById(R.id.spinnerRadius);

        rvNearbyPlaces.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NearbyPlaceAdapter(this, this);
        rvNearbyPlaces.setAdapter(adapter);

        chipGroupCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            Chip chip = findViewById(checkedIds.get(0));
            updateCategory(chip.getText().toString());
        });

        spinnerRadius.setSelection(2); // 10 km default
        spinnerRadius.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String radiusStr = parent.getItemAtPosition(position).toString().split(" ")[0];
                selectedRadiusMeters = Integer.parseInt(radiusStr) * 1000;
                Log.d(TAG, "Radius changed to: " + selectedRadiusMeters + "m");
                fetchNearbyPlaces();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void initPlaces() {
        try {
            if (!Places.isInitialized()) {
                // Use the NEW Places API initialization
                Places.initializeWithNewPlacesApiEnabled(getApplicationContext(), BuildConfig.PLACES_API_KEY);
                Log.d(TAG, "Places SDK (New) initialized.");
            }
            placesClient = Places.createClient(this);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Places SDK: " + e.getMessage());
            showError("Failed to initialize Google Places SDK.");
        }
    }

    private void checkLocationPermission() {
        if (locationHelper.hasLocationPermissions()) {
            Log.d(TAG, "Location permissions granted.");
            getLocation();
        } else {
            Log.d(TAG, "Requesting location permissions.");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getLocation() {
        if (!locationHelper.isLocationEnabled()) {
            Log.w(TAG, "GPS is disabled.");
            locationHelper.checkAndEnableLocation(this, 1002);
            return;
        }

        tvLocationStatus.setText("Location: Searching for GPS...");
        locationHelper.getLastLocation(new LocationHelper.LocationCallback() {
            @Override
            public void onSuccess(Location location) {
                currentUserLocation = location;
                Log.d(TAG, "Current location: " + location.getLatitude() + ", " + location.getLongitude());
                tvLocationStatus.setText(String.format("Location: %.4f, %.4f",
                        location.getLatitude(), location.getLongitude()));
                fetchNearbyPlaces();
            }

            @Override
            public void onFailure(String message) {
                Log.e(TAG, "Location failure: " + message);
                tvLocationStatus.setText("Location: Unavailable");
                Toast.makeText(NearMeActivity.this, "Location Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCategory(String category) {
        Log.d(TAG, "Category selected: " + category);
        switch (category) {
            case "Attractions": selectedType = "tourist_attraction"; break;
            case "Hotels": selectedType = "lodging"; break;
            case "Restaurants": selectedType = "restaurant"; break;
            case "Cafes": selectedType = "cafe"; break;
            case "Hospitals": selectedType = "hospital"; break;
            case "Shopping": selectedType = "shopping_mall"; break;
            case "Stations": selectedType = "train_station"; break;
            case "Airports": selectedType = "airport"; break;
            case "ATMs": selectedType = "atm"; break;
            case "Banks": selectedType = "bank"; break;
            case "Petrol Pumps": selectedType = "gas_station"; break;
            case "Parks": selectedType = "park"; break;
            case "Temples": selectedType = "hindu_temple"; break;
            case "Museums": selectedType = "museum"; break;
            case "Colleges": selectedType = "university"; break;
            case "Bus Stations": selectedType = "bus_station"; break;
            case "Historical": selectedType = "tourist_attraction"; break;
            default: selectedType = "point_of_interest"; break;
        }
        fetchNearbyPlaces();
    }

    private void fetchNearbyPlaces() {
        if (currentUserLocation == null || placesClient == null) return;

        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);

        LatLng center = new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude());
        CircularBounds circle = CircularBounds.newInstance(center, selectedRadiusMeters);

        final List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.RATING,
                Place.Field.USER_RATINGS_TOTAL, Place.Field.PHOTO_METADATAS, Place.Field.LAT_LNG, Place.Field.TYPES
        );

        SearchNearbyRequest request = SearchNearbyRequest.builder(circle, placeFields)
                .setIncludedTypes(Collections.singletonList(selectedType))
                .setMaxResultCount(20)
                .build();

        Log.d(TAG, "Fetching nearby: " + selectedType + " within " + selectedRadiusMeters + "m");

        placesClient.searchNearby(request).addOnSuccessListener((response) -> {
            List<NearbyPlace> nearbyPlaces = new ArrayList<>();
            
            for (Place place : response.getPlaces()) {
                NearbyPlace nearMePlace = new NearbyPlace(
                        place.getId(), place.getName(), place.getAddress(),
                        place.getRating() != null ? place.getRating() : 0.0,
                        place.getUserRatingsTotal() != null ? place.getUserRatingsTotal() : 0,
                        null, // Photo reference not directly available in New SDK without fetching metadata
                        place.getLatLng(),
                        place.getTypes() != null ? place.getTypes().stream().map(Enum::name).collect(Collectors.toList()) : null
                );

                float[] distanceResults = new float[1];
                Location.distanceBetween(currentUserLocation.getLatitude(), currentUserLocation.getLongitude(),
                        place.getLatLng().latitude, place.getLatLng().longitude, distanceResults);
                nearMePlace.setDistance(distanceResults[0]);

                nearbyPlaces.add(nearMePlace);
            }

            Log.d(TAG, "Found " + nearbyPlaces.size() + " places.");
            Collections.sort(nearbyPlaces, (p1, p2) -> Float.compare(p1.getDistance(), p2.getDistance()));

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                adapter.setPlaces(nearbyPlaces);
                if (nearbyPlaces.isEmpty()) {
                    emptyState.setVisibility(View.VISIBLE);
                }
            });

        }).addOnFailureListener((exception) -> {
            progressBar.setVisibility(View.GONE);
            Log.e(TAG, "Search failed: " + exception.getMessage());
            
            String msg = exception.getMessage();
            if (msg != null && msg.contains("9012")) {
                showError("Google Places API Key not valid. Check your Cloud Console and restrictions.");
            } else {
                showError("Search failed. Check your internet or API configuration.");
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        tvLocationStatus.setText(message);
    }

    @Override
    public void onPlaceClick(NearbyPlace place) {
        Intent intent = new Intent(this, PlaceDetailActivity.class);
        intent.putExtra("place_id_str", place.getId());
        intent.putExtra("is_google_place", true);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                showError("Location permission is required to find nearby places.");
            }
        }
    }
}
