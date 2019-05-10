package com.fleury.marc.go4lunch.models;

import java.util.ArrayList;

public class Restaurant {

    private String restaurant;
    private ArrayList<String> users;

    public Restaurant() {}

    public Restaurant(String restaurant, ArrayList<String> users) {
        this.restaurant = restaurant;
        this.users = users;
    }

    // --- GETTERS ---
    public String getRestaurant() { return restaurant; }
    public ArrayList<String> getUsers() { return users; }

    // --- SETTERS ---
    public void setRestaurant(String restaurant) { this.restaurant = restaurant; }
    public void setUsers(ArrayList<String> users) { this.users = users; }

}
