package com.fleury.marc.go4lunch.views;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fleury.marc.go4lunch.BuildConfig;
import com.fleury.marc.go4lunch.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.DayOfWeek;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ListViewHolder extends RecyclerView.ViewHolder {

    private static final String apiKey = BuildConfig.GoogleMapsKey;
    private PlacesClient placesClient;
    private List<Period> periods2 = new ArrayList<>();

    private TextView itemName;
    private TextView itemAddress;
    private TextView itemOpening;
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
        itemOpening = itemView.findViewById(R.id.item_hours);
        itemDistance = itemView.findViewById(R.id.item_distance);
        itemPersons = itemView.findViewById(R.id.item_person_number);
        itemPhoto = itemView.findViewById(R.id.item_image);
        itemRating = itemView.findViewById(R.id.item_rating);
        constraintLayout = itemView.findViewById(R.id.item_person_constraint);
    }

    public void updateWithPlace(String itemName, String itemAddress, List<Period> periods1, String distance, int itemPersons, double itemRating, PhotoMetadata itemPhoto){

        this.itemName.setText(itemName);
        String subAddress = itemAddress.substring(0, itemAddress.indexOf(","));
        this.itemAddress.setText(subAddress);

        getOpening(periods1);

        double rating = (itemRating / 1.7);
        this.itemRating.setRating((float) rating);
        this.itemDistance.setText(distance);
        if (itemPersons > 0 ) {
            constraintLayout.setVisibility(View.VISIBLE);
            this.itemPersons.setText("(" + itemPersons + ")");
        }

        this.itemPhoto.setImageBitmap(null);
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

    private void getOpening(List<Period> periods) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        switch (day) {
            case Calendar.MONDAY:
                filterByDay(periods, DayOfWeek.MONDAY);
                break;
            case Calendar.TUESDAY:
                filterByDay(periods, DayOfWeek.TUESDAY);
                break;
            case Calendar.WEDNESDAY:
                filterByDay(periods, DayOfWeek.WEDNESDAY);
                break;
            case Calendar.THURSDAY:
                filterByDay(periods, DayOfWeek.THURSDAY);
                break;
            case Calendar.FRIDAY:
                filterByDay(periods, DayOfWeek.FRIDAY);
                break;
            case Calendar.SATURDAY:
                filterByDay(periods, DayOfWeek.SATURDAY);
                break;
            case Calendar.SUNDAY:
                filterByDay(periods, DayOfWeek.SUNDAY);
                break;
        }
        Log.i("testHours", "LIST : " + periods2);

        for (Period p : periods2) {
            Log.i("testHours", "open : " + p.getOpen().getTime().getHours());
            Log.i("testHours", "close : " + p.getClose().getTime().getHours());
            Log.i("testHours", "--------------------------------------------");
            if (p.getClose().getTime().getHours() < hour) {
                this.itemOpening.setText(R.string.closed);
                this.itemOpening.setTypeface(null, Typeface.ITALIC);
            } else if (p.getClose().getTime().getHours() - hour == 0) {
                if (p.getClose().getTime().getMinutes() < minute) {
                    this.itemOpening.setText(R.string.closed);
                    this.itemOpening.setTypeface(null, Typeface.ITALIC);
                } else {
                    this.itemOpening.setText(R.string.closing_soon);
                    this.itemOpening.setTypeface(null, Typeface.BOLD);
                    this.itemOpening.setTextColor(Color.RED);
                    //break;
                }
            } else {
                this.itemOpening.setText("Open until " + p.getClose().getTime().getHours() + "h" + p.getClose().getTime().getMinutes());
                this.itemOpening.setTypeface(null, Typeface.ITALIC);
                //break;
            }
        }
    }

    private void filterByDay(List<Period> periods, DayOfWeek day) {
        for (Period p : periods) {
            if (p.getOpen().getDay() == day && p.getClose().getDay() == day) {
                periods2.add(p);
            }
        }
    }

}