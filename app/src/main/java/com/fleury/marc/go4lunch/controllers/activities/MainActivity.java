package com.fleury.marc.go4lunch.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fleury.marc.go4lunch.R;
import com.fleury.marc.go4lunch.api.UserHelper;
import com.fleury.marc.go4lunch.controllers.fragments.ListViewFragment;
import com.fleury.marc.go4lunch.controllers.fragments.MapViewFragment;
import com.fleury.marc.go4lunch.controllers.fragments.WorkmatesFragment;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View headerView;
    private int navigationPage;

    // BOTTOM NAVIGATION
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_map:
                        MapViewFragment mapFrag = new MapViewFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_frame_layout, mapFrag).commit();
                        this.navigationPage = 1;
                        invalidateOptionsMenu();
                        return true;
                    case R.id.navigation_list:
                        ListViewFragment listFrag = new ListViewFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_frame_layout, listFrag).commit();
                        this.navigationPage = 2;
                        invalidateOptionsMenu();
                        return true;
                    case R.id.navigation_workmates:
                        WorkmatesFragment workmatesFrag = new WorkmatesFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_frame_layout, workmatesFrag).commit();
                        this.navigationPage = 3;
                        invalidateOptionsMenu();
                        return true;
                }
                return false;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        MapViewFragment mapFrag = new MapViewFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_frame_layout, mapFrag).commit();

        configureToolbar();
        configureDrawerLayout();
        configureNavigationView();
        autocomplete();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the navigationPage and add it to the Toolbar
        getMenuInflater().inflate(R.menu.activity_main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (this.navigationPage == 3) { // If fragment = workmates : don't display the "search" item
            menu.findItem(R.id.activity_main_search).setVisible(false);
        } else {
            menu.findItem(R.id.activity_main_search).setVisible(true);
        }
        return true;
    }

    //----------------------
    // CONFIGURATION
    //----------------------

    private void configureToolbar() {
        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_Bar));
    }

    private void configureDrawerLayout() {
        this.drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView() {
        navigationView = findViewById(R.id.activity_main_nav_view);
        headerView = navigationView.getHeaderView(0);

        updateUserInfo(headerView);

        // NAVIGATION DRAWER
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            switch (id) {
                case R.id.nav_lunch:
                    // ------------------------- ICI --------------------------------
                    UserHelper.getUser(FirebaseAuth.getInstance().getUid()).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                if (document.getData().get("restaurant") != null) {
                                    Intent detailActivityIntent = new Intent(MainActivity.this, DetailActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("detailId", (String) document.getData().get("restaurant"));
                                    detailActivityIntent.putExtras(bundle);
                                    startActivity(detailActivityIntent);
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.no_restaurant, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Log.d("TAG", "No such document");
                                Toast.makeText(getApplicationContext(), R.string.no_restaurant, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.d("TAG", "Get failed with ", task.getException());
                        }
                    });
                    break;
                case R.id.nav_settings:
                    Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(settingsIntent);
                    break;
                case R.id.nav_logout:
                    FirebaseAuth.getInstance().signOut();
                    Intent signOutIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(signOutIntent);
                    finish();
                    break;
                default:
                    break;
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    public void updateUserInfo(View headerView) { // Display user name/mail/image in the Navigation Header

        TextView userName = headerView.findViewById(R.id.nav_header_user_name);
        TextView userMail = headerView.findViewById(R.id.nav_header_user_mail);
        ImageView userImage = headerView.findViewById(R.id.nav_header_user_image);

        if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName() != null) {
            userName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        }

        if (FirebaseAuth.getInstance().getCurrentUser().getEmail() != null) {
            userMail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }

        if(!TextUtils.isEmpty(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString())){
            Glide.with(this)
                    .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(userImage);
        } else { // Display a default image if the user doesn't have one
            Glide.with(this)
                    .load(R.drawable.default_person)
                    .apply(RequestOptions.circleCropTransform())
                    .into(userImage);
        }
    }

    //----------------------
    // TOOLBAR'S BUTTONS
    //----------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle actions on navigationPage items
        switch (item.getItemId()) {
            case R.id.activity_main_search:
                Toast.makeText(getApplicationContext(), "Search Button", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void autocomplete() {

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("TESTAUTO", "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TESTAUTO", "An error occurred: " + status);
            }
        });
    }
}
