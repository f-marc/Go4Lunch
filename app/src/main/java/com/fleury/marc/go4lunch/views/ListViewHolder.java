package com.fleury.marc.go4lunch.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fleury.marc.go4lunch.R;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import androidx.recyclerview.widget.RecyclerView;

public class ListViewHolder extends RecyclerView.ViewHolder {

    private TextView itemName;
    private TextView itemAddress;
    private TextView itemHours;
    private TextView itemDistance;
    private RatingBar itemRating;

    public ListViewHolder(View itemView) {
        super(itemView);

        itemName = itemView.findViewById(R.id.item_name);
        itemAddress = itemView.findViewById(R.id.item_address);
        itemHours = itemView.findViewById(R.id.item_hours);
        itemDistance = itemView.findViewById(R.id.item_distance);
        itemRating = itemView.findViewById(R.id.item_rating);
    }

    public void updateWithPlace(String itemName, String itemAddress, String itemHours, Float itemRating, String distance){
        this.itemName.setText(itemName);
        String subAddress = itemAddress.substring(0, itemAddress.indexOf(","));
        this.itemAddress.setText(subAddress);
        //this.itemHours.setText(itemHours);

        double rating = (itemRating / 1.7);
        this.itemRating.setRating((float) rating);

        this.itemDistance.setText(distance);
    }

}