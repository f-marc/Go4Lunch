package com.fleury.marc.go4lunch.views;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fleury.marc.go4lunch.R;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class ListViewHolder extends RecyclerView.ViewHolder {

    private TextView itemName;
    private TextView itemAddress;
    private TextView itemHours;
    private TextView itemDistance;
    private TextView itemPersons;
    private RatingBar itemRating;
    private ConstraintLayout constraintLayout;

    public ListViewHolder(View itemView) {
        super(itemView);

        itemName = itemView.findViewById(R.id.item_name);
        itemAddress = itemView.findViewById(R.id.item_address);
        itemHours = itemView.findViewById(R.id.item_hours);
        itemDistance = itemView.findViewById(R.id.item_distance);
        itemPersons = itemView.findViewById(R.id.item_person_number);
        itemRating = itemView.findViewById(R.id.item_rating);
        constraintLayout = itemView.findViewById(R.id.item_person_constraint);
    }

    public void updateWithPlace(String itemName, String itemAddress, String itemHours, String distance, int itemPersons, double itemRating){
        this.itemName.setText(itemName);
        String subAddress = itemAddress.substring(0, itemAddress.indexOf(","));
        this.itemAddress.setText(subAddress);
        this.itemHours.setText(itemHours);
        double rating = (itemRating / 1.7);
        this.itemRating.setRating((float) rating);
        this.itemDistance.setText(distance);
        if (itemPersons > 0 ) {
            constraintLayout.setVisibility(View.VISIBLE);
            this.itemPersons.setText("(" + itemPersons + ")");
        }
    }

}