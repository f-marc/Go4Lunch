package com.fleury.marc.go4lunch;

import com.fleury.marc.go4lunch.models.User;
import com.fleury.marc.go4lunch.utils.SortingList;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class SortingTest {

    @Test
    public void getOpeningTest() {

        List<User> usersList = new ArrayList<>();
        List<User> usersListSorted = new ArrayList<>();

        User user1 = mock(User.class);
        User user2 = mock(User.class);
        User user3 = mock(User.class);
        User user4 = mock(User.class);

        when(user1.getUsername()).thenReturn("Marc");
        when(user2.getUsername()).thenReturn("Sandra");
        when(user3.getUsername()).thenReturn("Benjamin");
        when(user4.getUsername()).thenReturn("Catherine");

        usersList.add(user1);
        usersList.add(user2);
        usersList.add(user3);
        usersList.add(user4);

        usersListSorted.add(user3);
        usersListSorted.add(user4);
        usersListSorted.add(user1);
        usersListSorted.add(user2);

        SortingList sortingList = new SortingList();

        assertEquals(usersListSorted, sortingList.sortingAlphabetically(usersList));
    }
}
