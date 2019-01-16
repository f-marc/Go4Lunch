package com.fleury.marc.go4lunch;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import butterknife.BindView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.activity_main_nav_view) NavigationView navigationView;
    @BindView(R.id.message) TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_map:
                    mTextMessage.setText(R.string.title_map);
                    return true;
                case R.id.navigation_list:
                    mTextMessage.setText(R.string.title_list);
                    return true;
                case R.id.navigation_workmates:
                    mTextMessage.setText(R.string.title_workmates);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        configureToolbar();
        configureDrawerLayout();
        configureNavigationView();
    }

    //@Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle Navigation Item Click
        int id = item.getItemId();

        switch (id){
            case R.id.nav_lunch:
                Toast.makeText(getApplicationContext(), R.string.nav_lunch, Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_settings:
                Toast.makeText(getApplicationContext(), R.string.nav_settings, Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_logout:
                Toast.makeText(getApplicationContext(), R.string.nav_logout, Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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

    private void configureToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("I'm Hungry!");
    }

    private void configureDrawerLayout(){
        this.drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView(){
        //navigationView.setNavigationItemSelectedListener(this);

    }

    //----------------------
    // TOOLBAR'S BUTTONS
    //----------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //3 - Handle actions on menu items
        switch (item.getItemId()) {
            case R.id.activity_main_search:
                Toast.makeText(getApplicationContext(), "Search Button", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
