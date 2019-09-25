package com.fleury.marc.go4lunch.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.fleury.marc.go4lunch.R;
import com.fleury.marc.go4lunch.api.RestaurantHelper;
import com.fleury.marc.go4lunch.api.UserHelper;
import com.fleury.marc.go4lunch.controllers.activities.MainActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AlarmReceiver extends BroadcastReceiver {

    private PlacesClient placesClient;
    private List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

    String contentText;
    String restaurant;
    String restaurantName;
    String restaurantAddress;

    @Override
    public void onReceive(Context context, Intent intent){

        UserHelper.getUser(FirebaseAuth.getInstance().getUid()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document1 = task.getResult();
                if (document1.exists()) {
                    if (document1.getData().get("restaurant") != null) {
                        restaurant = document1.getData().get("restaurant").toString();
                    } else {
                        restaurant = "";
                        contentText = context.getString(R.string.no_restaurant);
                    }
                        placesClient = Places.createClient(context);
                        FetchPlaceRequest request = FetchPlaceRequest.builder(restaurant, placeFields).build();
                        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                            Place place = response.getPlace();
                            restaurantName = place.getName();
                            restaurantAddress = place.getAddress();
                            String subAddress = restaurantAddress.substring(0, restaurantAddress.indexOf(","));

                            contentText = restaurantName + " – " + subAddress;

                            triggerNotification(context, contentText);

                        }).addOnFailureListener((exception) -> {
                            if (exception instanceof ApiException) {
                                ApiException apiException = (ApiException) exception;
                                Log.e("ApiException", "Place not found: " + apiException.getStatusCode());
                            }
                        });
                } else {
                    Log.e("TAG", "No such document");
                }
            } else {
                Log.e("TAG", "Get failed with ", task.getException());
            }
        });
    }

    private void triggerNotification(Context context, String contentText){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_restaurant_menu_black_48dp)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(contentText)
                .setPriority(Notification.PRIORITY_DEFAULT);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(contentIntent);

        notificationManager.notify(1, notificationBuilder.build());
    }
}
