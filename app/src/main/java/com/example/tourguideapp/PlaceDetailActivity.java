package com.example.tourguideapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.tourguideapp.database.AppDatabase;
import com.example.tourguideapp.database.PlaceDao;
import com.example.tourguideapp.models.Place;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place.Field;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class PlaceDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private PlaceDao placeDao;
    private Place currentPlace;
    private com.google.android.libraries.places.api.model.Place googlePlace;
    
    private int placeId;
    private String placeIdStr;
    private boolean isGooglePlace;

    private ImageView ivDetailImage;
    private TextView tvName, tvCategory, tvDescription, tvTimings, tvFee, tvRating, tvFoodType;
    private ImageButton btnFavorite;
    private MapView mapView;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        initUI();

        placeDao = AppDatabase.getInstance(getApplicationContext()).placeDao();
        
        Intent intent = getIntent();
        isGooglePlace = intent.getBooleanExtra("is_google_place", false);
        
        if (isGooglePlace) {
            placeIdStr = intent.getStringExtra("place_id_str");
            loadGooglePlaceDetails();
        } else {
            placeId = intent.getIntExtra("place_id", -1);
            loadLocalPlaceDetails();
        }

        btnFavorite.setOnClickListener(v -> toggleFavorite());
        findViewById(R.id.btnOpenMap).setOnClickListener(v -> openInMaps());
        findViewById(R.id.btnDirections).setOnClickListener(v -> openDirections());
    }

    private void initUI() {
        Toolbar toolbar = findViewById(R.id.detailToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        ivDetailImage = findViewById(R.id.ivDetailImage);
        tvName = findViewById(R.id.tvDetailName);
        tvCategory = findViewById(R.id.tvDetailCategory);
        tvDescription = findViewById(R.id.tvDetailDescription);
        tvTimings = findViewById(R.id.tvDetailTimings);
        tvFee = findViewById(R.id.tvDetailFee);
        tvRating = findViewById(R.id.tvDetailRating);
        tvFoodType = findViewById(R.id.tvDetailFoodType);
        btnFavorite = findViewById(R.id.btnFavorite);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(null);
        mapView.getMapAsync(this);
    }

    private void loadLocalPlaceDetails() {
        Executors.newSingleThreadExecutor().execute(() -> {
            currentPlace = placeDao.getPlaceByIdSync(placeId);
            runOnUiThread(() -> {
                if (currentPlace == null) {
                    Toast.makeText(this, "Error loading place", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                tvName.setText(currentPlace.name);
                tvCategory.setText(currentPlace.category);
                tvDescription.setText(currentPlace.description);
                tvTimings.setText("Timings: " + currentPlace.timings);
                tvFee.setText("Entry: " + currentPlace.entryFee);
                tvRating.setText(String.format("Rating: %.1f ★", currentPlace.rating));
                tvFoodType.setVisibility(View.GONE);

                int imageRes = getResources().getIdentifier(
                        currentPlace.imageName, "drawable", getPackageName());
                
                Glide.with(this)
                        .load(imageRes != 0 ? imageRes : android.R.drawable.ic_menu_gallery)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(ivDetailImage);

                updateFavoriteIcon();
                placeMapMarker();
            });
        });
    }

    private void loadGooglePlaceDetails() {
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(getApplicationContext(), BuildConfig.PLACES_API_KEY);
        }
        PlacesClient placesClient = Places.createClient(this);

        List<Field> placeFields = Arrays.asList(
                Field.ID, Field.NAME, Field.ADDRESS, Field.RATING, Field.PHOTO_METADATAS, Field.LAT_LNG);

        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeIdStr, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            googlePlace = response.getPlace();
            
            tvName.setText(googlePlace.getName());
            tvCategory.setText("Nearby Place");
            tvDescription.setText(googlePlace.getAddress());
            tvTimings.setVisibility(View.GONE);
            tvFee.setVisibility(View.GONE);
            tvRating.setText(String.format("Rating: %.1f ★", googlePlace.getRating()));
            tvFoodType.setVisibility(View.GONE);
            btnFavorite.setVisibility(View.GONE); // Simple implementation: hide favorites for Google Places

            if (googlePlace.getPhotoMetadatas() != null && !googlePlace.getPhotoMetadatas().isEmpty()) {
                com.google.android.libraries.places.api.model.PhotoMetadata photoMetadata = googlePlace.getPhotoMetadatas().get(0);
                
                com.google.android.libraries.places.api.net.FetchPhotoRequest photoRequest = com.google.android.libraries.places.api.net.FetchPhotoRequest.builder(photoMetadata)
                        .setMaxWidth(800)
                        .build();
                
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                    ivDetailImage.setImageBitmap(fetchPhotoResponse.getBitmap());
                });
            }

            placeMapMarker();

        }).addOnFailureListener((exception) -> {
            Toast.makeText(this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        placeMapMarker();
    }

    private void placeMapMarker() {
        if (googleMap == null) return;
        
        LatLng location = null;
        String name = "";

        if (isGooglePlace && googlePlace != null) {
            location = googlePlace.getLatLng();
            name = googlePlace.getName();
        } else if (currentPlace != null) {
            location = new LatLng(currentPlace.latitude, currentPlace.longitude);
            name = currentPlace.name;
        }

        if (location != null) {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(location).title(name));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
        }
    }

    private void toggleFavorite() {
        if (currentPlace == null) return;
        currentPlace.favorite = !currentPlace.favorite;
        Executors.newSingleThreadExecutor().execute(() -> {
            placeDao.update(currentPlace);
            runOnUiThread(this::updateFavoriteIcon);
        });
    }

    private void updateFavoriteIcon() {
        if (currentPlace == null) return;
        btnFavorite.setImageResource(currentPlace.favorite
                ? android.R.drawable.btn_star_big_on
                : android.R.drawable.btn_star_big_off);
    }

    private void openInMaps() {
        LatLng latLng = null;
        String name = "";

        if (isGooglePlace && googlePlace != null) {
            latLng = googlePlace.getLatLng();
            name = googlePlace.getName();
        } else if (currentPlace != null) {
            latLng = new LatLng(currentPlace.latitude, currentPlace.longitude);
            name = currentPlace.name;
        }

        if (latLng != null) {
            String uri = "geo:" + latLng.latitude + "," + latLng.longitude + "?q=" + Uri.encode(name);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
        }
    }

    private void openDirections() {
        LatLng latLng = null;
        if (isGooglePlace && googlePlace != null) {
            latLng = googlePlace.getLatLng();
        } else if (currentPlace != null) {
            latLng = new LatLng(currentPlace.latitude, currentPlace.longitude);
        }

        if (latLng != null) {
            String uri = String.format("google.navigation:q=%f,%f", latLng.latitude, latLng.longitude);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + latLng.latitude + "," + latLng.longitude)));
            }
        }
    }

    @Override protected void onResume() { super.onResume(); mapView.onResume(); }
    @Override protected void onStart() { super.onStart(); mapView.onStart(); }
    @Override protected void onStop() { super.onStop(); mapView.onStop(); }
    @Override protected void onPause() { super.onPause(); mapView.onPause(); }
    @Override protected void onDestroy() { super.onDestroy(); mapView.onDestroy(); }
    @Override public void onLowMemory() { super.onLowMemory(); mapView.onLowMemory(); }
}
