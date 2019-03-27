package com.fleury.marc.go4lunch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {

    private TextView name;
    private TextView address;
    private RatingBar rating;
    private ImageView call;
    private ImageView like;
    private ImageView website;

    private String detailName;
    private String detailAddress;
    private String detailNumber;
    private String detailWebsite;
    private Float detailRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        name = findViewById(R.id.detail_name);
        address = findViewById(R.id.detail_address);
        rating = findViewById(R.id.detail_rating);
        call = findViewById(R.id.detail_call_image);
        like = findViewById(R.id.detail_like_image);
        website = findViewById(R.id.detail_website_image);

        call.setOnClickListener(this::onClick);
        like.setOnClickListener(this::onClick);
        website.setOnClickListener(this::onClick);

        detailName = getIntent().getExtras().getString("detailName");
        detailAddress = getIntent().getExtras().getString("detailAddress");
        detailRating = getIntent().getExtras().getFloat("detailRating");

        if (getIntent().getExtras().getString("detailNumber") != null){
            detailNumber = getIntent().getExtras().getString("detailNumber");
        }
        if (getIntent().getExtras().getString("detailWebsite") != null){
            detailWebsite = getIntent().getExtras().getString("detailWebsite");
        }

        updateDetail();
    }

    private void updateDetail(){
        name.setText(detailName);

        String subAddress = detailAddress.substring(0, detailAddress.indexOf(","));
        address.setText(subAddress);

        double doubleRating = (detailRating / 1.7);
        rating.setRating((float) doubleRating);
    }


    public void onClick(View v) {
        if(v == call){
            if (getIntent().getExtras().getString("detailNumber") != null){
                Toast.makeText(this, detailNumber, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Aucun numéro de téléphone", Toast.LENGTH_SHORT).show();
            }
        }
        else if(v == like){
            Toast.makeText(this, "LIKE !", Toast.LENGTH_SHORT).show();
        }
        else if(v == website){
            if (getIntent().getExtras().getString("detailWebsite") != null){
                Toast.makeText(this, detailWebsite, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Aucun site internet", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
