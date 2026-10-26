package com.example.tourguideapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourguideapp.PlaceDetailActivity;
import com.example.tourguideapp.R;
import com.example.tourguideapp.models.Place;

import java.util.ArrayList;
import java.util.List;

public class PlaceAdapter
        extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private final Context context;
    private final List<Place> placeList;

    public PlaceAdapter(
            Context context,
            List<Place> places
    ) {

        this.context = context;
        this.placeList =
                places != null
                        ? places
                        : new ArrayList<>();
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.item_place,
                                parent,
                                false
                        );

        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PlaceViewHolder holder,
            int position
    ) {

        Place place =
                placeList.get(position);

        holder.tvName.setText(
                place.name != null
                        ? place.name
                        : ""
        );

        holder.tvDescription.setText(
                place.description != null
                        ? place.description
                        : ""
        );

        holder.tvCategory.setText(
                place.category != null
                        ? place.category
                        : ""
        );

        holder.tvAddress.setText(
                place.address != null
                        ? place.address
                        : ""
        );

        holder.itemView.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            context,
                            PlaceDetailActivity.class
                    );

            intent.putExtra(
                    "place_id",
                    place.id
            );

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {

        return placeList.size();
    }

    public void updateList(
            List<Place> newList
    ) {

        placeList.clear();

        if (newList != null) {
            placeList.addAll(newList);
        }

        notifyDataSetChanged();
    }

    static class PlaceViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvDescription;
        TextView tvCategory;
        TextView tvAddress;

        public PlaceViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            tvName =
                    itemView.findViewById(
                            R.id.tvPlaceName
                    );

            tvDescription =
                    itemView.findViewById(
                            R.id.tvPlaceDescription
                    );

            tvCategory =
                    itemView.findViewById(
                            R.id.tvPlaceCategory
                    );

            tvAddress =
                    itemView.findViewById(
                            R.id.tvPlaceAddress
                    );
        }
    }
}
