package com.fleury.marc.go4lunch;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ListViewHolder extends RecyclerView.ViewHolder {

    private TextView itemName;
    private TextView itemAddress;
    private TextView itemHours;
    private TextView itemDistance;
    private RatingBar itemRating;
    private float[] result = new float[1];

    private SharedPreferences pref;

    public ListViewHolder(View itemView) {
        super(itemView);

        itemName = itemView.findViewById(R.id.item_name);
        itemAddress = itemView.findViewById(R.id.item_address);
        itemHours = itemView.findViewById(R.id.item_hours);
        itemDistance = itemView.findViewById(R.id.item_distance);
        itemRating = itemView.findViewById(R.id.item_rating);

        pref = itemName.getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
    }

    public void updateWithPlace(String itemName, String itemAddress, String itemHours, Float itemRating, double latitude, double longitude){
        this.itemName.setText(itemName);
        String subAddress = itemAddress.substring(0, itemAddress.indexOf(","));
        this.itemAddress.setText(subAddress);
        //this.itemHours.setText(itemHours);

        if(itemRating > -1){
            double rating = (itemRating / 1.7);
            this.itemRating.setRating((float) rating);
        } else{
            this.itemRating.setVisibility(View.GONE);
        }

        Location.distanceBetween(getDouble(pref, "locLat", 0.0), getDouble(pref, "locLng", 0.0), latitude, longitude, result);
        this.itemDistance.setText(result[0] + "m");
    }

    double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }
}