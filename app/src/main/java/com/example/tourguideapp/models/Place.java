package com.example.tourguideapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "places")
public class Place {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String description;
    public String category;
    public String placeType;
    public String address;
    public String city;
    public String state;
    public double latitude;
    public double longitude;
    public String timings;
    public String entryFee;
    public float rating;
    public String imageName;
    public boolean favorite;

    public Place(
            String name,
            String description,
            String category,
            String placeType,
            String address,
            String city,
            String state,
            double latitude,
            double longitude,
            String timings,
            String entryFee,
            float rating,
            String imageName,
            boolean favorite
    ) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.placeType = placeType;
        this.address = address;
        this.city = city;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timings = timings;
        this.entryFee = entryFee;
        this.rating = rating;
        this.imageName = imageName;
        this.favorite = favorite;
    }
}
