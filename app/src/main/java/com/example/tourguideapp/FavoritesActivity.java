package com.example.tourguideapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tourguideapp.adapters.PlaceAdapter;
import com.example.tourguideapp.database.AppDatabase;
import com.example.tourguideapp.database.PlaceDao;
import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView rvFavorites;
    private PlaceAdapter adapter;
    private PlaceDao placeDao;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = findViewById(R.id.favToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        tvEmpty = findViewById(R.id.tvEmptyFavorites);
        rvFavorites = findViewById(R.id.rvFavorites);
        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new PlaceAdapter(this, new ArrayList<>());
        rvFavorites.setAdapter(adapter);

        placeDao = AppDatabase.getInstance(getApplicationContext()).placeDao();

        placeDao.getFavoritePlaces().observe(this, places -> {
            if (places == null || places.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                rvFavorites.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                rvFavorites.setVisibility(View.VISIBLE);
                adapter.updateList(places);
            }
        });
    }
}
