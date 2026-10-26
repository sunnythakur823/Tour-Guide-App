package com.example.tourguideapp.models;

import com.google.android.gms.maps.model.LatLng;
import java.util.List;

public class NearbyPlace {
    private String id;
    private String name;
    private String address;
    private double rating;
    private int ratingCount;
    private String photoReference;
    private LatLng latLng;
    private List<String> types;
    private float distance;

    public NearbyPlace(String id, String name, String address, double rating, int ratingCount,
                       String photoReference, LatLng latLng, List<String> types) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.photoReference = photoReference;
        this.latLng = latLng;
        this.types = types;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public double getRating() { return rating; }
    public int getRatingCount() { return ratingCount; }
    public String getPhotoReference() { return photoReference; }
    public LatLng getLatLng() { return latLng; }
    public List<String> getTypes() { return types; }
    
    public float getDistance() { return distance; }
    public void setDistance(float distance) { this.distance = distance; }

    public String getPrimaryType() {
        if (types == null || types.isEmpty()) return "Place";
        String type = types.get(0).replace("_", " ");
        if (type.length() > 0) {
            return type.substring(0, 1).toUpperCase() + type.substring(1);
        }
        return type;
    }
}
