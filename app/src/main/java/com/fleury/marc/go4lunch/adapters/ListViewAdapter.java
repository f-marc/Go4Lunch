package com.fleury.marc.go4lunch.adapters;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fleury.marc.go4lunch.R;
import com.fleury.marc.go4lunch.api.RestaurantHelper;
import com.fleury.marc.go4lunch.views.ListViewHolder;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.firestore.DocumentSnapshot;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewHolder> {

    private List<Place> placesList;
    private Context context;

    private int persons;
    private double lat;
    private double lng;
    private float[] result1 = new float[1];
    private float[] result2 = new float[1];

    public ListViewAdapter(Context context, double lat, double lng) {
        this.context = context;
        this.lat = lat;
        this.lng = lng;
    }

    public void setPlaces(List<Place> placeList) {
        this.placesList = placeList;
        sortingByDistance(placesList);
        notifyDataSetChanged();
    }

    private void sortingByDistance(List<Place> placesList) {
        Collections.sort(placesList, (o1, o2) -> {
            double latitude1 = o1.getLatLng().latitude;
            double longitude1 = o1.getLatLng().longitude;
            Location.distanceBetween(lat, lng, latitude1, longitude1, result1);
            double latitude2 = o2.getLatLng().latitude;
            double longitude2 = o2.getLatLng().longitude;
            Location.distanceBetween(lat, lng, latitude2, longitude2, result2);
            if (result1[0] > result2[0]) {
                return 1;
            } else if (result2[0] > result1[0]) {
                return -1;
            }
            return 0;
        });
    }

    public Place getPlaces(int position) {
        return this.placesList.get(position);
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list_view_item, parent, false);

        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListViewHolder viewHolder, int position) {
        final Place place = placesList.get(position);

        String name = place.getName();
        String address = place.getAddress();
        Double rating = place.getRating();
        PhotoMetadata photo = place.getPhotoMetadatas().get(0);
        double latitude = place.getLatLng().latitude;
        double longitude = place.getLatLng().longitude;

        Location.distanceBetween(lat, lng, latitude, longitude, result1);
        DecimalFormat df = new DecimalFormat("#");
        df.setRoundingMode(RoundingMode.HALF_UP);
        String distance = context.getString(R.string.distance, df.format(result1[0]));

        RestaurantHelper.getRestaurant(place.getId()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                List<String> list;
                try {
                    list = (List<String>) document.getData().get("users");
                    if (list != null) {
                        persons = list.size();
                    }
                } catch (NullPointerException e) {
                    Log.e("GetDataError", "Error" + e);
                }
            } else {
                Log.d("TaskError", "Error getting documents: ", task.getException());
            }
            viewHolder.updateWithPlace(name, address, place.getOpeningHours().getPeriods() ,distance, persons, rating, photo);
        });
    }

    @Override
    public int getItemCount() {
        if (placesList == null) {
            return 0;
        }
        return placesList.size();
    }

}