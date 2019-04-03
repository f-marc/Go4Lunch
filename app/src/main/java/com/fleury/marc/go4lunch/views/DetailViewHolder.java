package com.fleury.marc.go4lunch.views;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fleury.marc.go4lunch.R;

import androidx.recyclerview.widget.RecyclerView;

public class DetailViewHolder extends RecyclerView.ViewHolder {

    private ImageView itemImage;
    private TextView itemName;

    public DetailViewHolder(View itemView) {
        super(itemView);

        itemImage = itemView.findViewById(R.id.detail_item_image);
        itemName = itemView.findViewById(R.id.detail_item_name);

    }

    public void updateWithUser(String itemImage, String itemName){

        this.itemName.setText(itemName + " is joining!");
    }

}