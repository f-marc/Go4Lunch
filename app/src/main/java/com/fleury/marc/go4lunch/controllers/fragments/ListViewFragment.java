package com.fleury.marc.go4lunch.controllers.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fleury.marc.go4lunch.controllers.activities.DetailActivity;
import com.fleury.marc.go4lunch.R;
import com.fleury.marc.go4lunch.adapters.ListViewAdapter;
import com.fleury.marc.go4lunch.utils.ItemClickSupport;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ListViewFragment extends Fragment {

    @BindView(R.id.fragment_list_view_recycler) RecyclerView recyclerView;

    private static final int LOC_REQ_CODE = 1;

    //private PlaceDetectionClient placeDetectionClient;
    private List<Place> placesList;
    private ListViewAdapter adapter;

    private double lat;
    private double lng;

    private static final int restaurantPlace = 79;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        ButterKnife.bind(this, view);

        //placeDetectionClient = Places.getPlaceDetectionClient(getActivity());

        SharedPreferences pref = getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);

        //lat = getDouble(pref, "locLat", 0.0);
        //lng = getDouble(pref, "locLng", 0.0);

        configureRecyclerView();
        configureOnClickRecyclerView();
        getCurrentPlaceItems();

        return view;
    }

    private void configureRecyclerView() {
        this.placesList = new ArrayList<>();
        this.adapter = new ListViewAdapter(getContext(), lat, lng);
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void configureOnClickRecyclerView(){ // Click on RecyclerView's item
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_list_view_item)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    Place place = adapter.getPlaces(position);
                    Intent detailActivityIntent = new Intent(getContext(), DetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("detailName", place.getName().toString());
                    bundle.putString("detailAddress", place.getAddress().toString());
                    //bundle.putFloat("detailRating", place.getRating());
                    bundle.putString("detailId", place.getId());
                    if (!TextUtils.isEmpty(place.getPhoneNumber())) {
                        bundle.putString("detailNumber", place.getPhoneNumber().toString());
                    }
                    if (place.getWebsiteUri() != null) {
                        bundle.putString("detailWebsite", place.getWebsiteUri().toString());
                    }
                    detailActivityIntent.putExtras(bundle);
                    startActivity(detailActivityIntent);
                });
    }

    private void getCurrentPlaceItems() {
        // Checking permissions
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOC_REQ_CODE);
        } else {
            //getCurrentPlaceData();

        }
    }

    /*@SuppressLint("MissingPermission")
    private void getCurrentPlaceData() {
        Task<PlaceLikelihoodBufferResponse> placeResult = placeDetectionClient.getCurrentPlace(null);
        placeResult.addOnFailureListener(task ->
                Log.i("List : onFailure", "Fail")
        );

        placeResult.addOnCompleteListener(task -> {
            PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
            for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                if (placeLikelihood.getPlace().getPlaceTypes().contains(restaurantPlace)) {
                    placesList.add(placeLikelihood.getPlace().freeze());
                }
            }
            likelyPlaces.release();
            adapter.setPlaces(placesList);
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOC_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                getCurrentPlaceData();
            }
        }
    }

    // Transform Long into Double
    private double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }*/

}




