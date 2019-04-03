package com.fleury.marc.go4lunch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fleury.marc.go4lunch.R;
import com.fleury.marc.go4lunch.views.ListViewHolder;
import com.google.android.libraries.places.compat.Place;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewHolder> {

    private List<Place> placesList;
    private Context context;

    private double lat;
    private double lng;

    public ListViewAdapter(Context ctx, double lat, double lng) {
        context = ctx;
        this.lat = lat;
        this.lng = lng;
    }

    public void setPlaces(List<Place> list) {
        this.placesList = list;
        notifyDataSetChanged();
    }

    public Place getPlaces(int position){
        return this.placesList.get(position);
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list_view_item, parent, false);

        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListViewHolder viewHolder, int position) {
        final int itemPos = position;
        final Place place = placesList.get(position);

        String name = place.getName().toString();
        String address = place.getAddress().toString();
        String phone = place.getPhoneNumber().toString();
        Float rating = place.getRating();
        double latitude = place.getLatLng().latitude;
        double longitude = place.getLatLng().longitude;

        viewHolder.updateWithPlace(name, address, phone, rating, lat, lng, latitude, longitude);
    }

    @Override
    public int getItemCount() {
        if(placesList == null){
            return 0;
        }
        return placesList.size();
    }

}