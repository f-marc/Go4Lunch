package com.fleury.marc.go4lunch.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fleury.marc.go4lunch.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.PlaceDetectionClient;
import com.google.android.libraries.places.compat.PlaceFilter;
import com.google.android.libraries.places.compat.PlaceLikelihood;
import com.google.android.libraries.places.compat.PlaceLikelihoodBufferResponse;
import com.google.android.libraries.places.compat.Places;

import java.util.ArrayList;
import java.util.List;

public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient client;
    private LatLng placeLoc;
    private PlaceDetectionClient placeDetectionClient;
    private Task<PlaceLikelihoodBufferResponse> placeResult;

    PlaceFilter filter;

    private double lng;
    private double lat;
    private String name;
    private String address;

    private double latitude = 0.0;
    private double longitude = 0.0;

    private static final int LOC_REQ_CODE = 1;

    public static MapViewFragment newInstance() {
        return new MapViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ButterKnife.bind(this, view);

        client = LocationServices.getFusedLocationProviderClient(getContext());
        placeDetectionClient = Places.getPlaceDetectionClient(getActivity());

        if(getArguments() != null){
            lng = getArguments().getDouble("lng");
            lat = getArguments().getDouble("lat");
            name = getArguments().getString("name");
            address = getArguments().getString("address");
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOC_REQ_CODE);
        } else {
            client.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                if(location != null){
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    placeLoc = new LatLng(latitude, longitude);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(placeLoc));
                }
            });
            mMap.setMyLocationEnabled(true);
        }


        placeResult = placeDetectionClient.getCurrentPlace(null);
        placeResult.addOnFailureListener(task ->
                Log.i("Map : onFailure", "Fail")
        );

        placeResult.addOnCompleteListener(task -> {
            PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
            for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                //Log.i("Map : onComplete", String.format("Place '%s' has likelihood: %g", placeLikelihood.getPlace().getName(), placeLikelihood.getLikelihood()));
                Log.i("Map : onComplete", String.format("Place '%s' has likelihood: %g", placeLikelihood.getPlace().getPlaceTypes(), placeLikelihood.getLikelihood()));
                //googleMap.addMarker(new MarkerOptions().position(placeLikelihood.getPlace().getLatLng()));
                if (placeLikelihood.getPlace().getPlaceTypes().contains(79)) {
                    googleMap.addMarker(new MarkerOptions().position(placeLikelihood.getPlace().getLatLng()));
                }
            }
            likelyPlaces.release();
        });

        //googleMap.addMarker(new MarkerOptions().position(new LatLng(48.8534100, 2.3488000)));
        //googleMap.addMarker(new MarkerOptions().position(new LatLng(48.8544100, 2.3498000)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

    }

}
