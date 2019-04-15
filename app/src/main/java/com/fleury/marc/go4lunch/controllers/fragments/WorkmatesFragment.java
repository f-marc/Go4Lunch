package com.fleury.marc.go4lunch.controllers.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.fleury.marc.go4lunch.R;
import com.fleury.marc.go4lunch.adapters.WorkmatesAdapter;
import com.fleury.marc.go4lunch.api.UserHelper;
import com.fleury.marc.go4lunch.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class WorkmatesFragment extends Fragment {

    public static WorkmatesFragment newInstance() {
        return new WorkmatesFragment();
    }

    @BindView(R.id.fragment_workmates_recycler) RecyclerView recyclerView;
    private List<User> usersList;
    private WorkmatesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        ButterKnife.bind(this, view);

        configureRecyclerView();
        updateUsersList();

        return view;
    }

    private void configureRecyclerView() {
        this.usersList = new ArrayList<>();
        this.adapter = new WorkmatesAdapter(getContext(), Glide.with(this));
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void updateUsersList() {
        Query query = FirebaseFirestore.getInstance().collection(UserHelper.COLLECTION_NAME).whereGreaterThan("restaurant", "");
        query.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                // Handle error
                return;
            }
            List<User> users = snapshot.toObjects(User.class);
            adapter.setUsers(users);
        });
    }
}
