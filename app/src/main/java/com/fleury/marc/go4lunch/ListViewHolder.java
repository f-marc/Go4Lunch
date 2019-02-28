package com.fleury.marc.go4lunch;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_name) TextView itemName;
    @BindView(R.id.item_address) TextView itemAddress;
    @BindView(R.id.item_hours) TextView itemHours;


    public ListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateWithPlace(String itemName, String itemAddress, String itemHours){
        this.itemName.setText(itemName);
        this.itemAddress.setText(itemAddress);
        this.itemHours.setText(itemHours);
    }
}