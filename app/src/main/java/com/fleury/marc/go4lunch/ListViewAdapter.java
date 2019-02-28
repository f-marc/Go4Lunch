package com.fleury.marc.go4lunch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.libraries.places.compat.Place;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewHolder> {

    private List<Place> placesList;
    private Context context;

    public ListViewAdapter(List<Place> list, Context ctx) {
        placesList = list;
        context = ctx;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_view, parent, false);


        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListViewHolder viewHolder, int position) {
        final int itemPos = position;
        final Place place = placesList.get(position);

        String name = place.getName().toString();
        String address = place.getAddress().toString();
        String phone = place.getPhoneNumber().toString();

        /*if(place.getWebsiteUri() != null){
            viewHolder.website.setText(place.getWebsiteUri().toString());
        }

        if(place.getRating() > -1){
            viewHolder.ratingBar.setNumStars((int)place.getRating());
        }else{
            viewHolder.ratingBar.setVisibility(View.GONE);
        }*/

        viewHolder.updateWithPlace(name, address, phone);

    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

}