package com.fleury.marc.go4lunch.controllers.fragments;

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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fleury.marc.go4lunch.BuildConfig;
import com.fleury.marc.go4lunch.controllers.activities.DetailActivity;
import com.fleury.marc.go4lunch.R;
import com.fleury.marc.go4lunch.adapters.ListViewAdapter;
import com.fleury.marc.go4lunch.utils.ItemClickSupport;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ListViewFragment extends Fragment {

    @BindView(R.id.fragment_list_view_recycler) RecyclerView recyclerView;

    private static final String apiKey = BuildConfig.GoogleMapsKey;
    private static final int LOC_REQ_CODE = 1;

    private PlacesClient placesClient;
    private List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.TYPES);
    private List<Place.Field> placeFields2 = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.TYPES, Place.Field.LAT_LNG,
            Place.Field.ADDRESS, Place.Field.RATING, Place.Field.PHOTO_METADATAS, Place.Field.OPENING_HOURS);
    private FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();

    private List<Place> placesList;
    private ListViewAdapter adapter;

    private double lat;
    private double lng;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        ButterKnife.bind(this, view);

        Places.initialize(getActivity().getApplicationContext(), apiKey);
        placesClient = Places.createClient(getContext());

        SharedPreferences pref = getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);

        lat = getDouble(pref, "locLat", 0.0);
        lng = getDouble(pref, "locLng", 0.0);

        configureRecyclerView();
        configureOnClickRecyclerView();
        getCurrentPlaceData();

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
                    bundle.putString("detailId", place.getId());
                    detailActivityIntent.putExtras(bundle);
                    startActivity(detailActivityIntent);
                });
    }

    private void getCurrentPlaceData() {
        if (ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FindCurrentPlaceResponse response = task.getResult();
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        if (placeLikelihood.getPlace().getTypes().contains(Place.Type.RESTAURANT)) {
                            FetchPlaceRequest request2 = FetchPlaceRequest.builder(placeLikelihood.getPlace().getId(), placeFields2).build();
                            placesClient.fetchPlace(request2).addOnSuccessListener((response2) -> {
                                Place place = response2.getPlace();
                                placesList.add(place);
                                adapter.setPlaces(placesList);
                            }).addOnFailureListener((exception) -> {
                                if (exception instanceof ApiException) {
                                    ApiException apiException = (ApiException) exception;
                                    Log.e("placeTest", "Place not found: " + apiException.getStatusCode());
                                }
                            });
                        }
                    }
                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e("ApiException", "Place not found: " + apiException.getStatusCode());
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, LOC_REQ_CODE);
        }
    }

    // Transform Long into Double
    private double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

}




