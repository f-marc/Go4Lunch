package com.fleury.marc.go4lunch.utils;


import com.fleury.marc.go4lunch.models.User;

import java.util.Collections;
import java.util.List;

public class SortingList {

    public List<User> sortingAlphabetically(List<User> usersList) {
        Collections.sort(usersList, (o1, o2) -> o1.getUsername().compareToIgnoreCase(o2.getUsername()));
        return usersList;
    }

}
