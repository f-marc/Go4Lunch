package com.fleury.marc.go4lunch.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;

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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient client;
    private double lng;
    private double lat;
    private String name;
    private String address;
    private LatLng placeLoc;

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

        //LatLng placeLoc = new LatLng(48.8534100, 2.3488000);

        googleMap.addMarker(new MarkerOptions().position(new LatLng(48.8534100, 2.3488000)));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(48.8544100, 2.3498000)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

    }

}
