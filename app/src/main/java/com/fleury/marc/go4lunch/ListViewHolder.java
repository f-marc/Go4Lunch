package com.fleury.marc.go4lunch;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ListViewHolder extends RecyclerView.ViewHolder {

    TextView itemName;
    TextView itemAddress;
    TextView itemHours;
    RatingBar itemRating;


    public ListViewHolder(View itemView) {
        super(itemView);

        itemName = itemView.findViewById(R.id.item_name);
        itemAddress = itemView.findViewById(R.id.item_address);
        itemHours = itemView.findViewById(R.id.item_hours);
        itemRating = itemView.findViewById(R.id.item_rating);
    }

    public void updateWithPlace(String itemName, String itemAddress, String itemHours, Float itemRating){
        this.itemName.setText(itemName);
        String subAddress = itemAddress.substring(0, itemAddress.indexOf(","));
        this.itemAddress.setText(subAddress);
        this.itemHours.setText(itemHours);

        if(itemRating > -1){
            double rating = (itemRating / 1.7);
            this.itemRating.setRating((float) rating);
        }else{
            this.itemRating.setVisibility(View.GONE);
        }
    }
}