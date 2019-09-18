package com.fleury.marc.go4lunch.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.fleury.marc.go4lunch.R;
import com.fleury.marc.go4lunch.api.UserHelper;
import com.fleury.marc.go4lunch.controllers.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.core.app.NotificationCompat;


public class AlarmReceiver extends BroadcastReceiver {

    String contentText;

    @Override
    public void onReceive(Context context, Intent intent){

        String contentTitle = context.getString(R.string.app_name);

        UserHelper.getUser(FirebaseAuth.getInstance().getUid()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    if (document.getData().get("restaurant") != null) {
                        contentText = document.getData().get("restaurant").toString();
                    } else {
                        contentText = context.getString(R.string.no_restaurant);
                    }
                    Log.i("testnotif1", "" + contentText);
                } else {
                    Log.e("TAG", "No such document");
                }
            } else {
                Log.e("TAG", "Get failed with ", task.getException());
            }
        });

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
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setPriority(Notification.PRIORITY_DEFAULT);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(contentIntent);

        notificationManager.notify(1, notificationBuilder.build());
        Log.i("testnotif2", "" + contentText);
    }

}
