package com.example.tourguideapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourguideapp.adapters.PlaceAdapter;
import com.example.tourguideapp.database.AppDatabase;
import com.example.tourguideapp.database.PlaceDao;
import com.example.tourguideapp.models.Place;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvPlaces;
    private PlaceAdapter adapter;
    private PlaceDao placeDao;

    private AutoCompleteTextView etSearch;

    private ChipGroup chipGroupCategory;
    private ChipGroup chipGroupType;

    private TextView tvResultsTitle;
    private View emptyState;

    private LiveData<List<Place>> currentLiveData;

    private List<Place> allPlacesList = new ArrayList<>();

    private ArrayAdapter<String> suggestionAdapter;

    private String selectedType = "Tourist Spot";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rvPlaces = findViewById(R.id.rvPlaces);
        etSearch = findViewById(R.id.etSearch);

        chipGroupCategory =
                findViewById(R.id.chipGroupCategory);

        chipGroupType =
                findViewById(R.id.chipGroupType);

        tvResultsTitle =
                findViewById(R.id.tvResultsTitle);

        emptyState =
                findViewById(R.id.emptyState);

        BottomNavigationView bottomNav =
                findViewById(R.id.bottomNavigation);

        // RecyclerView

        rvPlaces.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new PlaceAdapter(
                this,
                new ArrayList<>()
        );

        rvPlaces.setAdapter(adapter);

        // Database

        placeDao =
                AppDatabase
                        .getInstance(getApplicationContext())
                        .placeDao();

        // Search suggestions

        suggestionAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        new ArrayList<>()
                );

        etSearch.setAdapter(suggestionAdapter);

        // Get all places

        placeDao.getAllPlaces().observe(
                this,
                places -> {

                    if (places == null) {
                        return;
                    }

                    allPlacesList = places;

                    List<String> names = new ArrayList<>();

                    for (Place p : places) {

                        if (p.name != null &&
                                !p.name.trim().isEmpty()) {

                            names.add(p.name);
                        }
                    }

                    suggestionAdapter.clear();
                    suggestionAdapter.addAll(names);
                    suggestionAdapter.notifyDataSetChanged();

                    Log.d(
                            "TOUR_DEBUG",
                            "TOTAL DATABASE = " +
                                    places.size()
                    );
                }
        );

        // Search suggestion click

        etSearch.setOnItemClickListener(
                (parent, view, position, id) -> {

                    String selectedName =
                            (String) parent
                                    .getItemAtPosition(position);

                    for (Place place : allPlacesList) {

                        if (place.name != null &&
                                place.name.equals(selectedName)) {

                            Intent intent =
                                    new Intent(
                                            this,
                                            PlaceDetailActivity.class
                                    );

                            intent.putExtra(
                                    "place_id",
                                    place.id
                            );

                            startActivity(intent);

                            break;
                        }
                    }
                }
        );

        // Bottom navigation

        bottomNav.setOnItemSelectedListener(
                item -> {

                    int itemId = item.getItemId();
                    if (itemId == R.id.nav_home) {
                        return true;
                    } else if (itemId == R.id.nav_favorites) {
                        startActivity(new Intent(this, FavoritesActivity.class));
                        return true;
                    } else if (itemId == R.id.nav_profile) {
                        Toast.makeText(this, "Profile coming soon!", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false;
                }
        );

        // Default

        observePlaces(
                placeDao.getPlacesByType(
                        selectedType
                )
        );

        // Type chips

        chipGroupType.setOnCheckedStateChangeListener(
                (group, checkedIds) -> {

                    if (checkedIds.isEmpty()) {
                        return;
                    }

                    Chip chip =
                            findViewById(
                                    checkedIds.get(0)
                            );

                    String label =
                            chip.getText()
                                    .toString()
                                    .trim();

                    if (label.equalsIgnoreCase(
                            "Tourist Spots")) {

                        selectedType = "Tourist Spot";

                    } else if (
                            label.equalsIgnoreCase("Food")) {

                        selectedType = "Food";

                    } else if (
                            label.equalsIgnoreCase(
                                    "Petrol Pump")) {

                        selectedType = "Petrol Pump";

                    } else {

                        selectedType = "Stay";
                    }

                    chipGroupCategory.setVisibility(
                            selectedType.equals(
                                    "Tourist Spot"
                            )
                                    ? View.VISIBLE
                                    : View.GONE
                    );

                    etSearch.setText("");

                    observePlaces(
                            placeDao.getPlacesByType(
                                    selectedType
                            )
                    );
                }
        );

        // Category chips

        chipGroupCategory.setOnCheckedStateChangeListener(
                (group, checkedIds) -> {

                    if (checkedIds.isEmpty()) {

                        observePlaces(
                                placeDao.getPlacesByType(
                                        selectedType
                                )
                        );

                        return;
                    }

                    Chip chip =
                            findViewById(
                                    checkedIds.get(0)
                            );

                    String category =
                            chip.getText()
                                    .toString()
                                    .trim();

                    if (category.equalsIgnoreCase("All")) {

                        observePlaces(
                                placeDao.getPlacesByType(
                                        selectedType
                                )
                        );

                    } else {

                        observePlaces(
                                placeDao.getPlacesByTypeAndCategory(
                                        selectedType,
                                        category
                                )
                        );
                    }
                }
        );

        // Search

        etSearch.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        String query =
                                s.toString().trim();

                        if (query.isEmpty()) {

                            observePlaces(
                                    placeDao.getPlacesByType(
                                            selectedType
                                    )
                            );

                        } else {

                            observePlaces(
                                    placeDao.searchPlaces(
                                            query
                                    )
                            );
                        }
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                }
        );

        debugDatabase();
    }

    // ==========================================
    // OBSERVE DATA
    // ==========================================

    private void observePlaces(
            LiveData<List<Place>> liveData
    ) {

        if (currentLiveData != null) {
            currentLiveData.removeObservers(this);
        }

        currentLiveData = liveData;

        currentLiveData.observe(
                this,
                places -> {

                    if (places == null) {
                        adapter.updateList(
                                new ArrayList<>()
                        );

                        showEmptyState(true);

                        return;
                    }

                    adapter.updateList(places);

                    tvResultsTitle.setText(
                            "Places (" +
                                    places.size() +
                                    ")"
                    );

                    showEmptyState(
                            places.isEmpty()
                    );
                }
        );
    }

    // ==========================================
    // EMPTY STATE
    // ==========================================

    private void showEmptyState(
            boolean show
    ) {

        if (show) {

            emptyState.setVisibility(
                    View.VISIBLE
            );

            rvPlaces.setVisibility(
                    View.GONE
            );

        } else {

            emptyState.setVisibility(
                    View.GONE
            );

            rvPlaces.setVisibility(
                    View.VISIBLE
            );
        }
    }

    // ==========================================
    // DEBUG
    // ==========================================

    private void debugDatabase() {

        new Thread(() -> {

            int total =
                    placeDao.getPlaceCount();

            int tourist =
                    placeDao.getCountByType(
                            "Tourist Spot"
                    );

            int food =
                    placeDao.getCountByType(
                            "Food"
                    );

            int petrol =
                    placeDao.getCountByType(
                            "Petrol Pump"
                    );

            int stay =
                    placeDao.getCountByType(
                            "Stay"
                    );

            Log.d(
                    "TOUR_DEBUG",
                    "TOTAL = " + total
            );

            Log.d(
                    "TOUR_DEBUG",
                    "TOURIST = " + tourist
            );

            Log.d(
                    "TOUR_DEBUG",
                    "FOOD = " + food
            );

            Log.d(
                    "TOUR_DEBUG",
                    "PETROL = " + petrol
            );

            Log.d(
                    "TOUR_DEBUG",
                    "STAY = " + stay
            );

        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(
            Menu menu
    ) {

        getMenuInflater().inflate(
                R.menu.main_menu,
                menu
        );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(
            MenuItem item
    ) {

        int id = item.getItemId();
        if (id == R.id.action_favorites) {
            startActivity(new Intent(this, FavoritesActivity.class));
            return true;
        } else if (id == R.id.action_near_me) {
            startActivity(new Intent(this, NearMeActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
