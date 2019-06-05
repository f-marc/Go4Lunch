package com.fleury.marc.go4lunch.controllers.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fleury.marc.go4lunch.R;
import com.fleury.marc.go4lunch.adapters.DetailAdapter;
import com.fleury.marc.go4lunch.api.RestaurantHelper;
import com.fleury.marc.go4lunch.api.UserHelper;
import com.fleury.marc.go4lunch.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.detail_name) TextView name;
    @BindView(R.id.detail_address) TextView address;
    @BindView(R.id.detail_like_text) TextView likeText;
    @BindView(R.id.detail_call_image) ImageView call;
    @BindView(R.id.detail_like_image) ImageView like;
    @BindView(R.id.detail_website_image) ImageView website;
    @BindView(R.id.detail_rating) RatingBar rating;

    private String detailName, detailAddress, detailId, detailNumber, detailWebsite;
    private Float detailRating;

    private String restaurant;

    @BindView(R.id.detail_recycler) RecyclerView recyclerView;
    private List<User> usersList;
    private DetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        call.setOnClickListener(this::onClick);
        like.setOnClickListener(this::onClick);
        website.setOnClickListener(this::onClick);

        detailName = getIntent().getExtras().getString("detailName");
        detailAddress = getIntent().getExtras().getString("detailAddress");
        detailId = getIntent().getExtras().getString("detailId");
        detailRating = getIntent().getExtras().getFloat("detailRating");

        if (!TextUtils.isEmpty(getIntent().getExtras().getString("detailNumber"))) {
            detailNumber = getIntent().getExtras().getString("detailNumber");
        }
        if (!TextUtils.isEmpty(getIntent().getExtras().getString("detailWebsite"))) {
            detailWebsite = getIntent().getExtras().getString("detailWebsite");
        }

        updateCurrentRestaurant();
        updateDetail();
        configureRecyclerView();
        updateUsersList();
    }

    private void updateCurrentRestaurant() { // Set "restaurant" to the actual value stored in Firebase
        restaurant = "";
        Log.i("snaptest0", "=" + restaurant);
        UserHelper.getUser(FirebaseAuth.getInstance().getUid()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    if (document.getData().get("restaurant") != null) {
                        restaurant = document.getData().get("restaurant").toString();
                        Log.i("snaptest1", "=" + restaurant);
                        updateStar();
                    }
                } else {
                    Log.e("TAG", "No such document");
                }
            } else {
                Log.e("TAG", "Get failed with ", task.getException());
            }
        });
    }

    private void updateDetail() { // Display restaurant details
        name.setText(detailName);

        String subAddress = detailAddress.substring(0, detailAddress.indexOf(","));
        address.setText(subAddress);

        double doubleRating = (detailRating / 1.7);
        rating.setRating((float) doubleRating);
    }

    private void updateStar() { // Update the star icon and text
        if (restaurant.equals(detailId)) {
            like.setImageResource(R.drawable.ic_star_yellow_30dp);
            likeText.setText(R.string.liked);
        } else {
            like.setImageResource(R.drawable.ic_star_orange_30dp);
            likeText.setText(R.string.like);
        }
    }

    private void updateRestaurantLike() {
        // Delete the old restaurant if the user already had one
        Log.i("snaptest2", "=" + restaurant);
        if (!TextUtils.isEmpty(restaurant)) {
            RestaurantHelper.getRestaurant(restaurant).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.i("snaptest3", "=" + restaurant);
                        DocumentReference docRef = RestaurantHelper.getRestaurantsDocument(restaurant);
                        docRef.update("users", FieldValue.arrayRemove(FirebaseAuth.getInstance().getUid()));
                    }
                    Log.i("snaptest4", "=" + restaurant);
                }
            });
            Log.i("snaptest5", "=" + restaurant);
        }
        // Create or update the restaurant to add the user in it
        RestaurantHelper.getRestaurant(detailId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) { // If the restaurant is already created : add the user in it
                    DocumentReference docRef = RestaurantHelper.getRestaurantsDocument(detailId);
                    docRef.update("users", FieldValue.arrayUnion(FirebaseAuth.getInstance().getUid()));
                } else { // If the restaurant isn't created : create it and add the user in it
                    ArrayList<String> users = new ArrayList<>();
                    users.add(FirebaseAuth.getInstance().getUid());
                    RestaurantHelper.createRestaurant(detailId, users);
                }
            }
        });
    }

    private void updateRestaurantUnlike() {
        // Remove the user of the restaurant
        DocumentReference docRef = RestaurantHelper.getRestaurantsDocument(detailId);
        docRef.update("users", FieldValue.arrayRemove(FirebaseAuth.getInstance().getUid()));
    }

    private void onClick(View v) {
        // CLICKED ON "CALL"
        if (v == call) {
            if (!TextUtils.isEmpty(detailNumber)) { // If there is a phone number: display it into user's call app
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:" + detailNumber));
                startActivity(phoneIntent);
            } else { // If there isn't a phone number: tell it to the user with a Toast
                Toast.makeText(this, R.string.no_phone_number, Toast.LENGTH_SHORT).show();
            }
        }
        // CLICKED ON "LIKE"
        else if (v == like) {
            if (restaurant.equals(detailId)) { // If the user dislikes
                UserHelper.updateRestaurant(null, FirebaseAuth.getInstance().getUid());
                UserHelper.updateRestaurantName(null, FirebaseAuth.getInstance().getUid());
                like.setImageResource(R.drawable.ic_star_orange_30dp);
                likeText.setText(R.string.like);
                updateRestaurantUnlike();
                Toast.makeText(this, "UNLIKED !", Toast.LENGTH_SHORT).show();
            } else { // If the user likes
                UserHelper.updateRestaurant(detailId, FirebaseAuth.getInstance().getUid());
                UserHelper.updateRestaurantName(detailName, FirebaseAuth.getInstance().getUid());
                like.setImageResource(R.drawable.ic_star_yellow_30dp);
                likeText.setText(R.string.liked);
                updateRestaurantLike();
                Toast.makeText(this, "LIKED !", Toast.LENGTH_SHORT).show();
            }
            updateCurrentRestaurant();
            updateUsersList();
        }
        // CLICKED ON "WEBSITE"
        else if (v == website) {
            if (!TextUtils.isEmpty(detailWebsite)) { // If there is a website: display it into user's browser
                if (!detailWebsite.startsWith("http://") && !detailWebsite.startsWith("https://")) {
                    detailWebsite = "http://" + detailWebsite;
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(detailWebsite));
                startActivity(browserIntent);
            } else { // If there isn't a website: tell it to the user with a Toast
                Toast.makeText(this, R.string.no_website, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void configureRecyclerView() {
        this.usersList = new ArrayList<>();
        this.adapter = new DetailAdapter(this, Glide.with(this));
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void updateUsersList() { // Display all the users in the restaurant
        Query query = UserHelper.getUsersCollection().whereEqualTo("restaurant", detailId);
        query.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e("SnapShotListener", "Error: ", e);
                return;
            }
            usersList = snapshot.toObjects(User.class);
            adapter.setUsers(usersList);
        });
    }
}
