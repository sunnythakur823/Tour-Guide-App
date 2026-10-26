package com.example.tourguideapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.tourguideapp.models.Place;

import java.util.List;

@Dao
public interface PlaceDao {

    // Get all places
    @Query("SELECT * FROM places ORDER BY name ASC")
    LiveData<List<Place>> getAllPlaces();

    // Get places by main type
    @Query("SELECT * FROM places WHERE placeType = :type ORDER BY name ASC")
    LiveData<List<Place>> getPlacesByType(String type);

    // Get places by type + category
    @Query("SELECT * FROM places WHERE placeType = :type AND category = :category ORDER BY name ASC")
    LiveData<List<Place>> getPlacesByTypeAndCategory(
            String type,
            String category
    );

    // Search all places
    @Query("SELECT * FROM places " +
            "WHERE name LIKE '%' || :query || '%' " +
            "OR description LIKE '%' || :query || '%' " +
            "OR category LIKE '%' || :query || '%' " +
            "OR placeType LIKE '%' || :query || '%' " +
            "OR address LIKE '%' || :query || '%' " +
            "OR city LIKE '%' || :query || '%' " +
            "OR state LIKE '%' || :query || '%' " +
            "ORDER BY name ASC")
    LiveData<List<Place>> searchPlaces(String query);

    // Get single place
    @Query("SELECT * FROM places WHERE id = :id LIMIT 1")
    LiveData<Place> getPlaceById(int id);

    @Query("SELECT * FROM places WHERE id = :id LIMIT 1")
    Place getPlaceByIdSync(int id);

    // Get favorites
    @Query("SELECT * FROM places WHERE favorite = 1 ORDER BY name ASC")
    LiveData<List<Place>> getFavoritePlaces();

    // Insert one place
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Place place);

    // Insert multiple places
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Place> places);

    // Update place
    @Update
    void update(Place place);

    // Delete place
    @Delete
    void delete(Place place);

    // Toggle favorite
    @Query("UPDATE places SET favorite = :favorite WHERE id = :placeId")
    void updateFavorite(int placeId, boolean favorite);

    // Count all records
    @Query("SELECT COUNT(*) FROM places")
    int getPlaceCount();

    // Debug: count by type
    @Query("SELECT COUNT(*) FROM places WHERE placeType = :type")
    int getCountByType(String type);

    // Debug: count by category
    @Query("SELECT COUNT(*) FROM places WHERE placeType = :type AND category = :category")
    int getCountByTypeAndCategory(String type, String category);
}
