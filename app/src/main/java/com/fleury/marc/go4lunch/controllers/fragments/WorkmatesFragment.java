package com.fleury.marc.go4lunch.controllers.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fleury.marc.go4lunch.R;
import com.fleury.marc.go4lunch.adapters.WorkmatesAdapter;
import com.fleury.marc.go4lunch.api.UserHelper;
import com.fleury.marc.go4lunch.controllers.activities.DetailActivity;
import com.fleury.marc.go4lunch.models.User;
import com.fleury.marc.go4lunch.utils.ItemClickSupport;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class WorkmatesFragment extends Fragment {

    @BindView(R.id.fragment_workmates_recycler) RecyclerView recyclerView;
    private List<User> usersList;
    private WorkmatesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        ButterKnife.bind(this, view);

        configureRecyclerView();
        configureOnClickRecyclerView();
        updateUsersList();

        return view;
    }

    private void configureRecyclerView() {
        this.usersList = new ArrayList<>();
        this.adapter = new WorkmatesAdapter(getContext(), Glide.with(this));
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void configureOnClickRecyclerView(){ // Click on RecyclerView's item
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_workmates_item)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    User user = adapter.getUsers(position);
                    if (user.getRestaurant() != null) {
                        Intent detailActivityIntent = new Intent(getContext(), DetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("detailId", user.getRestaurant());
                        detailActivityIntent.putExtras(bundle);
                        startActivity(detailActivityIntent);
                    }
                });
    }

    private void updateUsersList() { // Display all the users
        Query query = UserHelper.getUsersCollection();
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
