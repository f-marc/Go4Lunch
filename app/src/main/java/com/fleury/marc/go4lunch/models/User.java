package com.fleury.marc.go4lunch.models;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    private String username;
    @Nullable private String mail;
    @Nullable private String restaurantId;
    @Nullable private String restaurant;
    @Nullable private String urlPicture;

    public User() { }

    public User(String uid, String mail, String username, String restaurantId, String restaurant, String urlPicture) {
        this.uid = uid;
        this.mail = mail;
        this.username = username;
        this.restaurantId = restaurantId;
        this.restaurant = restaurant;
        this.urlPicture = urlPicture;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUserMail() { return mail; }
    public String getUsername() { return username; }
    public String getRestaurantId() { return restaurantId; }
    public String getRestaurant() { return restaurant; }
    public String getUrlPicture() { return urlPicture; }

    // --- SETTERS ---
    public void setUid(String uid) { this.uid = uid; }
    public void setMail(String mail) { this.mail = mail; }
    public void setUsername(String username) { this.username = username; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }
    public void setRestaurant(String restaurant) { this.restaurant = restaurant; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }

}
