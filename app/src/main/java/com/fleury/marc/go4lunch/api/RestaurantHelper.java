package com.fleury.marc.go4lunch.api;

import com.fleury.marc.go4lunch.models.Restaurant;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RestaurantHelper {

    public static final String COLLECTION_RESTAURANTS = "restaurants";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_RESTAURANTS);
    }

    // --- CREATE ---

    public static Task<Void> createRestaurant(String restaurant, ArrayList<String> users) {
        Restaurant restaurantToCreate = new Restaurant(restaurant, users);
        return RestaurantHelper.getRestaurantsCollection().document(restaurant).set(restaurantToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getRestaurant(String restaurant){
        return RestaurantHelper.getRestaurantsCollection().document(restaurant).get();
    }

    // --- UPDATE ---

    public static Task<Void> updateUsers(String restaurant, ArrayList<String> users) {
        return RestaurantHelper.getRestaurantsCollection().document(restaurant).update("users", users);
    }

    // --- DELETE ---

    public static Task<Void> deleteRestaurant(String restaurant) {
        return RestaurantHelper.getRestaurantsCollection().document(restaurant).delete();
    }
}
