package com.fleury.marc.go4lunch;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ListViewHolder extends RecyclerView.ViewHolder {

    TextView itemName;
    TextView itemAddress;
    TextView itemHours;


    public ListViewHolder(View itemView) {
        super(itemView);
        //ButterKnife.bind(this, itemView);

        itemName = itemView.findViewById(R.id.item_name);
        itemAddress = itemView.findViewById(R.id.item_address);
        itemHours = itemView.findViewById(R.id.item_hours);
    }

    public void updateWithPlace(String itemName, String itemAddress, String itemHours){
        this.itemName.setText(itemName);
        this.itemAddress.setText(itemAddress);
        this.itemHours.setText(itemHours);
    }
}