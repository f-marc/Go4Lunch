package com.fleury.marc.go4lunch.controllers.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

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
import com.fleury.marc.go4lunch.api.UserHelper;
import com.fleury.marc.go4lunch.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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

        if (!TextUtils.isEmpty(getIntent().getExtras().getString("detailNumber"))){
            detailNumber = getIntent().getExtras().getString("detailNumber");
        }
        if (!TextUtils.isEmpty(getIntent().getExtras().getString("detailWebsite"))){
            detailWebsite = getIntent().getExtras().getString("detailWebsite");
        }

        updateRestaurant();
        updateDetail();
        configureRecyclerView();
        updateUsersList();
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
                        updateStar();
                    }
                } else {
                    Log.d("TAG", "No such document");
                }
            } else {
                Log.d("TAG", "Get failed with ", task.getException());
            }
        });
    }

    private void updateDetail(){
        name.setText(detailName);

        String subAddress = detailAddress.substring(0, detailAddress.indexOf(","));
        address.setText(subAddress);

        double doubleRating = (detailRating / 1.7);
        rating.setRating((float) doubleRating);
    }

    private void updateStar() {
        if (restaurant.equals(detailId)) {
            like.setImageResource(R.drawable.ic_star_yellow_30dp);
            likeText.setText(R.string.liked);
        } else {
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
                UserHelper.updateRestaurant(null, FirebaseAuth.getInstance().getUid());
                like.setImageResource(R.drawable.ic_star_orange_30dp);
                likeText.setText(R.string.like);
                Toast.makeText(this, "UNLIKED !", Toast.LENGTH_SHORT).show();
            } else {
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

    private void configureRecyclerView() {
        this.usersList = new ArrayList<>();
        this.adapter = new DetailAdapter(this, Glide.with(this));
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void updateUsersList() {

        Query query = FirebaseFirestore.getInstance().collection(UserHelper.COLLECTION_NAME);

        query.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                // Handle error
                return;
            }

            // Convert query snapshot to a list of chats
            List<User> users = snapshot.toObjects(User.class);
            adapter.setUsers(users);
        });
    }

}
