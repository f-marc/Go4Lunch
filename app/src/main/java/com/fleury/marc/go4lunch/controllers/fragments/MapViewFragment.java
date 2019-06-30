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

import com.fleury.marc.go4lunch.BuildConfig;
import com.fleury.marc.go4lunch.api.RestaurantHelper;
import com.fleury.marc.go4lunch.controllers.activities.DetailActivity;
import com.fleury.marc.go4lunch.R;
import com.google.android.gms.common.api.ApiException;
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
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private static final String apiKey = BuildConfig.GoogleMapsKey;

    private GoogleMap mMap;
    private FusedLocationProviderClient client;
    private LatLng placeLoc;
    private PlacesClient placesClient;
    private List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.TYPES, Place.Field.LAT_LNG,
            Place.Field.ADDRESS, Place.Field.RATING, Place.Field.PHOTO_METADATAS);
    private FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();

    private double latitude = 0.0;
    private double longitude = 0.0;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static final int LOC_REQ_CODE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ButterKnife.bind(this, view);

        Places.initialize(getActivity().getApplicationContext(), apiKey);
        placesClient = Places.createClient(getContext());

        pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        editor = pref.edit();

        client = LocationServices.getFusedLocationProviderClient(getActivity());

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
        if (ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, LOC_REQ_CODE);
        }

        // PLACING MARKERS ON THE MAP
        if (ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.i("placeTest", "Autorisation OK");
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.i("placeTest", "Task Successful");
                    FindCurrentPlaceResponse response = task.getResult();
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        if (placeLikelihood.getPlace().getTypes().contains(Place.Type.RESTAURANT)) {
                            Log.i("placeTest", String.format("Place '%s' /// Type '%s' /// ID '%s' /// likelihood: %f",
                                    placeLikelihood.getPlace().getName(),
                                    placeLikelihood.getPlace().getTypes(),
                                    placeLikelihood.getPlace().getId(),
                                    placeLikelihood.getLikelihood()));
                            googleMap.addMarker(new MarkerOptions()
                                    .position(placeLikelihood.getPlace().getLatLng())
                                    .title(placeLikelihood.getPlace().getName())).setTag(placeLikelihood);
                            // If the restaurant is selected by a user
                            RestaurantHelper.getRestaurant(placeLikelihood.getPlace().getId()).addOnCompleteListener(taskR -> {
                                if (taskR.isSuccessful()) {
                                    DocumentSnapshot document = taskR.getResult();
                                    List<String> list = new ArrayList<>();
                                    try {
                                        list = (List<String>) document.getData().get("users");
                                    } catch (NullPointerException e) {
                                        Log.e("GetDataError", "Error" + e);
                                    }
                                    if (!list.isEmpty()) {
                                        googleMap.addMarker(new MarkerOptions()
                                                .position(placeLikelihood.getPlace().getLatLng())
                                                .title(placeLikelihood.getPlace().getName())
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).setTag(placeLikelihood);
                                    }
                                } else {
                                    Log.d("TaskError", "Error getting documents: ", taskR.getException());
                                }
                            });
                        } else {
                            Log.i("placeTest", "Pas de resto");
                        }
                    }
                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e("placeTest", "Place not found: " + apiException.getStatusCode());
                    }
                }
            });
        } else {
            Log.i("placeTest", "Pas d'autorisation");
            ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, LOC_REQ_CODE);
        }

        // CLICK ON MARKER
        googleMap.setOnMarkerClickListener(marker -> {
            PlaceLikelihood resto = (PlaceLikelihood) marker.getTag();
            Intent detailActivityIntent = new Intent(getContext(), DetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("detailId", resto.getPlace().getId());
            detailActivityIntent.putExtras(bundle);
            startActivity(detailActivityIntent);
            return true;
        });
    }

    // Transform Double into Long
    private SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }
}
