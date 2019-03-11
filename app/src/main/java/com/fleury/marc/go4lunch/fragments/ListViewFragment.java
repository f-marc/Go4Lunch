package com.fleury.marc.go4lunch.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fleury.marc.go4lunch.R;
import com.fleury.marc.go4lunch.ListViewAdapter;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.PlaceDetectionClient;
import com.google.android.libraries.places.compat.PlaceLikelihood;
import com.google.android.libraries.places.compat.PlaceLikelihoodBufferResponse;
import com.google.android.libraries.places.compat.Places;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ListViewFragment extends Fragment {

    @BindView(R.id.fragment_list_view_recycler) RecyclerView recyclerView;

    private static final int LOC_REQ_CODE = 1;

    private PlaceDetectionClient placeDetectionClient;
    private List<Place> placesList;
    private ListViewAdapter adapter;

    Task<PlaceLikelihoodBufferResponse> placeResult;

    public static ListViewFragment newInstance() {
        return new ListViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        ButterKnife.bind(this, view);

        placeDetectionClient = Places.getPlaceDetectionClient(getActivity());

        configureRecyclerView();
        getCurrentPlaceItems();

        return view;
    }

    private void configureRecyclerView(){

        this.placesList = new ArrayList<>();
        this.adapter = new ListViewAdapter(this.placesList, getContext());
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void getCurrentPlaceItems() {
        if (isLocationAccessPermitted()) {
            getCurrentPlaceData();
        } else {
            requestLocationAccessPermission();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentPlaceData() {
        placeResult = placeDetectionClient.getCurrentPlace(null);
        placeResult.addOnFailureListener(task ->
                Log.i("testFail", "result"));
        Log.i("testLog", "1");

        placeResult.addOnCompleteListener(task -> {

            PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
            for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                Log.i("test0", String.format("Place '%s' has likelihood: %g",
                        placeLikelihood.getPlace().getName(), placeLikelihood.getLikelihood()));
                placesList.add(placeLikelihood.getPlace().freeze());
            }
            likelyPlaces.release();
        });
    }

    private boolean isLocationAccessPermitted() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    private void requestLocationAccessPermission() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOC_REQ_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOC_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                getCurrentPlaceData();
            }
        }
    }

}




