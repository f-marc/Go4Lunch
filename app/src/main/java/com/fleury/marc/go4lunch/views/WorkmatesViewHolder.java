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

    public void updateWithWorkmates(String itemImage, String itemRestaurant, String yesEating, String noEating, RequestManager glide){

        if(!TextUtils.isEmpty(itemRestaurant)) {
            this.itemText.setText(yesEating);
        } else {
            this.itemText.setText(noEating);
            this.itemText.setTextColor(Color.parseColor("#9D9D9D"));
            this.itemText.setTypeface(this.itemText.getTypeface(), Typeface.ITALIC);
        }
        if(!TextUtils.isEmpty(itemImage)){
            glide.load(itemImage)
                    .apply(RequestOptions.circleCropTransform())
                    .into(this.itemImage);
        } else {
            glide.load(R.drawable.default_person)
                    .apply(RequestOptions.circleCropTransform())
                    .into(this.itemImage);
        }
    }

}