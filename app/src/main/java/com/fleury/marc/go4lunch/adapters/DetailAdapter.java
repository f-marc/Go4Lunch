package com.fleury.marc.go4lunch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fleury.marc.go4lunch.R;
import com.fleury.marc.go4lunch.views.DetailViewHolder;
import com.google.android.libraries.places.compat.Place;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class DetailAdapter extends RecyclerView.Adapter<DetailViewHolder> {

    private List<Place> placesList;
    private Context context;

    public DetailAdapter(Context ctx) {
        context = ctx;
    }

    public void setPlaces(List<Place> list) {
        this.placesList = list;
        notifyDataSetChanged();
    }

    public Place getPlaces(int position){
        return this.placesList.get(position);
    }

    @Override
    public DetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_detail_item, parent, false);

        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DetailViewHolder viewHolder, int position) {
        final int itemPos = position;
        final Place place = placesList.get(position);

        String name = place.getName().toString();
        String address = place.getAddress().toString();
        String phone = place.getPhoneNumber().toString();
        Float rating = place.getRating();
        double latitude = place.getLatLng().latitude;
        double longitude = place.getLatLng().longitude;

        //viewHolder.updateWithPlace(name, address, phone, rating, latitude, longitude);
    }

    @Override
    public int getItemCount() {
        if(placesList == null){
            return 0;
        }
        return placesList.size();
    }

}