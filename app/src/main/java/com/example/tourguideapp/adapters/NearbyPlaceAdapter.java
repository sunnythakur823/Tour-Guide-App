package com.example.tourguideapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.tourguideapp.R;
import com.example.tourguideapp.models.NearbyPlace;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NearbyPlaceAdapter extends RecyclerView.Adapter<NearbyPlaceAdapter.ViewHolder> {

    private final Context context;
    private List<NearbyPlace> places;
    private final OnPlaceClickListener listener;

    public interface OnPlaceClickListener {
        void onPlaceClick(NearbyPlace place);
    }

    public NearbyPlaceAdapter(Context context, OnPlaceClickListener listener) {
        this.context = context;
        this.places = new ArrayList<>();
        this.listener = listener;
    }

    public void setPlaces(List<NearbyPlace> places) {
        this.places = places;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_nearby_place, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NearbyPlace place = places.get(position);
        holder.tvName.setText(place.getName());
        holder.tvAddress.setText(place.getAddress());
        
        String ratingText = String.format(Locale.getDefault(), "%.1f (%d reviews)", 
                place.getRating(), place.getRatingCount());
        holder.tvRating.setText(ratingText);
        
        holder.tvCategory.setText(place.getPrimaryType());
        
        float distanceKm = place.getDistance() / 1000f;
        holder.tvDistance.setText(String.format(Locale.getDefault(), "%.1f km away", distanceKm));

        String photoUrl = getPhotoUrl(place.getPhotoReference());
        Glide.with(context)
                .load(photoUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.ivPlaceImage);

        holder.itemView.setOnClickListener(v -> listener.onPlaceClick(place));
        holder.btnViewDetails.setOnClickListener(v -> listener.onPlaceClick(place));
        
        holder.btnViewOnMap.setOnClickListener(v -> {
            String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(%s)", 
                    place.getLatLng().latitude, place.getLatLng().longitude,
                    place.getLatLng().latitude, place.getLatLng().longitude,
                    Uri.encode(place.getName()));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            context.startActivity(intent);
        });

        holder.btnDirections.setOnClickListener(v -> {
            String uri = String.format(Locale.ENGLISH, "google.navigation:q=%f,%f", 
                    place.getLatLng().latitude, place.getLatLng().longitude);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            context.startActivity(intent);
        });
    }

    private String getPhotoUrl(String photoReference) {
        if (photoReference == null || photoReference.isEmpty()) return null;
        String apiKey = com.example.tourguideapp.BuildConfig.PLACES_API_KEY;
        return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" 
                + photoReference + "&key=" + apiKey;
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPlaceImage;
        TextView tvName, tvRating, tvCategory, tvAddress, tvDistance;
        MaterialButton btnViewDetails, btnViewOnMap, btnDirections;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPlaceImage = itemView.findViewById(R.id.ivPlaceImage);
            tvName = itemView.findViewById(R.id.tvPlaceName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
            btnViewOnMap = itemView.findViewById(R.id.btnViewOnMap);
            btnDirections = itemView.findViewById(R.id.btnDirections);
        }
    }
}
