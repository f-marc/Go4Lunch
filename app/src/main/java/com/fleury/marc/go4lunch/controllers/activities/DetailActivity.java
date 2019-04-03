package com.fleury.marc.go4lunch.controllers.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fleury.marc.go4lunch.R;
import com.fleury.marc.go4lunch.api.UserHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class DetailActivity extends AppCompatActivity {

    private TextView name, address, likeText;
    private ImageView call, like, website;
    private RatingBar rating;

    private String detailName, detailAddress, detailId, detailNumber, detailWebsite;
    private Float detailRating;

    private String restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        name = findViewById(R.id.detail_name);
        address = findViewById(R.id.detail_address);
        likeText = findViewById(R.id.detail_like_text);
        rating = findViewById(R.id.detail_rating);
        call = findViewById(R.id.detail_call_image);
        like = findViewById(R.id.detail_like_image);
        website = findViewById(R.id.detail_website_image);

        call.setOnClickListener(this::onClick);
        like.setOnClickListener(this::onClick);
        website.setOnClickListener(this::onClick);

        detailName = getIntent().getExtras().getString("detailName");
        detailAddress = getIntent().getExtras().getString("detailAddress");
        detailId = getIntent().getExtras().getString("detailId");
        detailRating = getIntent().getExtras().getFloat("detailRating");

        if (!TextUtils.isEmpty(getIntent().getExtras().getString("detailNumber"))){
            detailNumber = getIntent().getExtras().getString("detailNumber");
        }
        if (!TextUtils.isEmpty(getIntent().getExtras().getString("detailWebsite"))){
            detailWebsite = getIntent().getExtras().getString("detailWebsite");
        }

        updateRestaurant();
        updateDetail();
    }

    private void updateRestaurant(){
        restaurant = "";
        UserHelper.getUser(FirebaseAuth.getInstance().getUid()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("TAG", "DocumentSnapshot data: " + document.getData().get("restaurant"));
                    if (document.getData().get("restaurant") != null) {
                        restaurant = document.getData().get("restaurant").toString();
                    }
                } else {
                    Log.d("TAG", "No such document");
                }
            } else {
                Log.d("TAG", "Get failed with ", task.getException());
            }
        });
        Log.d("TAGresto", restaurant);
    }

    private void updateDetail(){
        name.setText(detailName);

        String subAddress = detailAddress.substring(0, detailAddress.indexOf(","));
        address.setText(subAddress);

        double doubleRating = (detailRating / 1.7);
        rating.setRating((float) doubleRating);

        Log.d("TAGvalues", "restaurant = '" + restaurant + "' ; detailId = '" + detailId + "'");
        if (restaurant.equals(detailId)) {
            Log.d("TAGbug", "==");
            like.setImageResource(R.drawable.ic_star_yellow_30dp);
            likeText.setText(R.string.liked);
        } else {
            Log.d("TAGbug", "!=");
            like.setImageResource(R.drawable.ic_star_orange_30dp);
            likeText.setText(R.string.like);
        }
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
            if (restaurant.equals(detailId)){
                Log.d("TAGboucle", "==");
                UserHelper.updateRestaurant(null, FirebaseAuth.getInstance().getUid());
                like.setImageResource(R.drawable.ic_star_orange_30dp);
                likeText.setText(R.string.like);
                Toast.makeText(this, "UNLIKED !", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("TAGboucle", "!=");
                UserHelper.updateRestaurant(detailId, FirebaseAuth.getInstance().getUid());
                like.setImageResource(R.drawable.ic_star_yellow_30dp);
                likeText.setText(R.string.liked);
                Toast.makeText(this, "LIKED !", Toast.LENGTH_SHORT).show();
            }
            updateRestaurant();
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
