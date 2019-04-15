package com.fleury.marc.go4lunch.views;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
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

        if(!TextUtils.isEmpty(itemRestaurant)) {
            this.itemText.setText(itemName + " is eating at " + itemRestaurant);
        } else {
            this.itemText.setText(itemName + " hasn't decided yet");
            this.itemText.setTextColor(Color.parseColor("#9D9D9D"));
            this.itemText.setTypeface(this.itemText.getTypeface(), Typeface.ITALIC);
        }
        glide.load(itemImage)
                .apply(RequestOptions.circleCropTransform())
                .into(this.itemImage);

    }

}