package com.fleury.marc.go4lunch.views;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fleury.marc.go4lunch.BuildConfig;
import com.fleury.marc.go4lunch.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class ListViewHolder extends RecyclerView.ViewHolder {

    private static final String apiKey = BuildConfig.GoogleMapsKey;
    private PlacesClient placesClient;

    private TextView itemName;
    private TextView itemAddress;
    private TextView itemHours;
    private TextView itemDistance;
    private TextView itemPersons;
    private ImageView itemPhoto;
    private RatingBar itemRating;
    private ConstraintLayout constraintLayout;

    public ListViewHolder(View itemView) {
        super(itemView);

        Places.initialize(itemView.getContext(), apiKey);
        placesClient = Places.createClient(itemView.getContext());

        itemName = itemView.findViewById(R.id.item_name);
        itemAddress = itemView.findViewById(R.id.item_address);
        itemHours = itemView.findViewById(R.id.item_hours);
        itemDistance = itemView.findViewById(R.id.item_distance);
        itemPersons = itemView.findViewById(R.id.item_person_number);
        itemPhoto = itemView.findViewById(R.id.item_image);
        itemRating = itemView.findViewById(R.id.item_rating);
        constraintLayout = itemView.findViewById(R.id.item_person_constraint);
    }

    public void updateWithPlace(String itemName, String itemAddress, String itemHours, String distance, int itemPersons, double itemRating, PhotoMetadata itemPhoto){
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

        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(itemPhoto)
                .build();
        placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
            Bitmap bitmap = fetchPhotoResponse.getBitmap();
            this.itemPhoto.setImageBitmap(bitmap);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("photoBug", "Place not found: " + apiException.getStatusCode());
            }
        });
    }

}