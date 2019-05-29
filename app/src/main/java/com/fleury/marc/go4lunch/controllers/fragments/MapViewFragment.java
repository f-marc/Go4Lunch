package com.fleury.marc.go4lunch.controllers.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fleury.marc.go4lunch.api.RestaurantHelper;
import com.fleury.marc.go4lunch.api.UserHelper;
import com.fleury.marc.go4lunch.controllers.activities.DetailActivity;
import com.fleury.marc.go4lunch.R;
import com.fleury.marc.go4lunch.models.Restaurant;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.compat.PlaceDetectionClient;
import com.google.android.libraries.places.compat.PlaceLikelihood;
import com.google.android.libraries.places.compat.PlaceLikelihoodBufferResponse;
import com.google.android.libraries.places.compat.Places;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.TAG;


public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient client;
    private LatLng placeLoc;
    private PlaceDetectionClient placeDetectionClient;
    private Task<PlaceLikelihoodBufferResponse> placeResult;

    private double latitude = 0.0;
    private double longitude = 0.0;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static final int LOC_REQ_CODE = 1;
    private static final int restaurantPlace = 79;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ButterKnife.bind(this, view);

        pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        editor = pref.edit();

        client = LocationServices.getFusedLocationProviderClient(getActivity());
        placeDetectionClient = Places.getPlaceDetectionClient(getActivity());

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // SET THE MAP STYLE
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.style_json));
            if (!success) {
                Log.e("MapStyle", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapStyle", "Can't find style. Error: ", e);
        }

        // SET THE CAMERA TO USER'S POSITION
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOC_REQ_CODE);
        } else {
            client.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    putDouble(editor, "locLat", latitude).apply();
                    putDouble(editor, "locLng", longitude).apply();
                    placeLoc = new LatLng(latitude, longitude);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(placeLoc));
                }
            });
            mMap.setMyLocationEnabled(true);
        }

        placeResult = placeDetectionClient.getCurrentPlace(null);
        placeResult.addOnFailureListener(task ->
                Log.e("Map : onFailure", "Fail")
        );

        // PLACING MARKERS ON THE MAP
        placeResult.addOnCompleteListener(task -> {
            PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
            for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                // If the place is a restaurant
                if (placeLikelihood.getPlace().getPlaceTypes().contains(restaurantPlace)) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(placeLikelihood.getPlace().getLatLng())
                            .title(placeLikelihood.getPlace().getName().toString())).setTag(placeLikelihood);
                    // If the restaurant is selected by a user
                    RestaurantHelper.getRestaurantsCollection().get().addOnCompleteListener(taskR -> {
                        if (taskR.isSuccessful()) {
                            List<String> list = new ArrayList<>();
                            for (QueryDocumentSnapshot document : taskR.getResult()) {
                                list.add(document.getId());
                            }
                            if (list.contains(placeLikelihood.getPlace().getId())){
                                googleMap.addMarker(new MarkerOptions()
                                        .position(placeLikelihood.getPlace().getLatLng())
                                        .title(placeLikelihood.getPlace().getName().toString())
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).setTag(placeLikelihood);
                            }
                        } else {
                            Log.d("TaskError", "Error getting documents: ", task.getException());
                        }
                    });
                }
            }
            likelyPlaces.release();
        });

        // CLICK ON MARKER
        googleMap.setOnMarkerClickListener(marker -> {
            PlaceLikelihood resto = (PlaceLikelihood) marker.getTag();
            Intent detailActivityIntent = new Intent(getContext(), DetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("detailName", resto.getPlace().getName().toString());
            bundle.putString("detailAddress", resto.getPlace().getAddress().toString());
            bundle.putFloat("detailRating", resto.getPlace().getRating());
            bundle.putString("detailId", resto.getPlace().getId());
            if (!TextUtils.isEmpty(resto.getPlace().getPhoneNumber())) {
                bundle.putString("detailNumber", resto.getPlace().getPhoneNumber().toString());
            }
            if (resto.getPlace().getWebsiteUri() != null) {
                bundle.putString("detailWebsite", resto.getPlace().getWebsiteUri().toString());
            }
            detailActivityIntent.putExtras(bundle);
            startActivity(detailActivityIntent);
            return true;
        });
    }

    // Transform Double into Long
    SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }
}
