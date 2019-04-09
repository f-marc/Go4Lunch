package com.fleury.marc.go4lunch.views;

import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.fleury.marc.go4lunch.R;

import androidx.recyclerview.widget.RecyclerView;

public class WorkmatesViewHolder extends RecyclerView.ViewHolder {

    private ImageView itemImage;
    private TextView itemText;

    public WorkmatesViewHolder(View itemView) {
        super(itemView);

        itemImage = itemView.findViewById(R.id.workmates_item_image);
        itemText = itemView.findViewById(R.id.workmates_item_text);

    }

    public void updateWithWorkmates(String itemImage, String itemName, String itemRestaurant, RequestManager glide){

        if(itemRestaurant != null) {
            this.itemText.setText(itemName + " is eating at " + itemRestaurant);
        } else {
            this.itemText.setText(itemName + "hasn't decided yet");
            this.itemText.setTypeface(this.itemText.getTypeface(), Typeface.ITALIC);
        }
        glide.load(itemImage).into(this.itemImage);

    }

}