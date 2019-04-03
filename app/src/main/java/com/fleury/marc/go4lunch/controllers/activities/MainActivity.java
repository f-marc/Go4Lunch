package com.fleury.marc.go4lunch.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.fleury.marc.go4lunch.controllers.fragments.ListViewFragment;
import com.fleury.marc.go4lunch.controllers.fragments.MapViewFragment;
import com.fleury.marc.go4lunch.controllers.fragments.WorkmatesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_map:
                        MapViewFragment mapFrag = new MapViewFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_frame_layout, mapFrag).commit();
                        return true;
                    case R.id.navigation_list:
                        ListViewFragment listFrag = new ListViewFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_frame_layout, listFrag).commit();
                        return true;
                    case R.id.navigation_workmates:
                        WorkmatesFragment workmatesFrag = new WorkmatesFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_frame_layout, workmatesFrag).commit();
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu and add it to the Toolbar
        getMenuInflater().inflate(R.menu.activity_main_toolbar, menu);
        return true;
    }

    //----------------------
    // CONFIGURATION
    //----------------------

    private void configureToolbar() {
        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("I'm Hungry!");
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

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            switch (id) {
                case R.id.nav_lunch:
                    Toast.makeText(getApplicationContext(), R.string.nav_lunch, Toast.LENGTH_LONG).show();
                    break;
                case R.id.nav_settings:
                    Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(settingsIntent);
                    finish();
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

    public void updateUserInfo(View headerView) {

        TextView userName = headerView.findViewById(R.id.nav_header_user_name);
        TextView userMail = headerView.findViewById(R.id.nav_header_user_mail);
        ImageView userImage = headerView.findViewById(R.id.nav_header_user_image);

        if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName() != null) {
            Log.i("UserName", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            userName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        }

        if (FirebaseAuth.getInstance().getCurrentUser().getEmail() != null) {
            Log.i("UserMail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
            userMail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }

        if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null) {
            Glide.with(this)
                    .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(userImage);
        }
    }

    //----------------------
    // TOOLBAR'S BUTTONS
    //----------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle actions on menu items
        switch (item.getItemId()) {
            case R.id.activity_main_search:
                Toast.makeText(getApplicationContext(), "Search Button", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
