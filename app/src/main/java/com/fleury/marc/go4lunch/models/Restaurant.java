package com.fleury.marc.go4lunch.models;

import java.util.ArrayList;

public class Restaurant {

    private ArrayList<String> users;

    public Restaurant() {}

    public Restaurant(ArrayList<String> users) {
        this.users = users;
    }

    // --- GETTERS ---
    public ArrayList<String> getUsers() { return users; }

    // --- SETTERS ---
    public void setUsers(ArrayList<String> users) { this.users = users; }

}
