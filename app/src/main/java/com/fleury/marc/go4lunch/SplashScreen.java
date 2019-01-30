package com.fleury.marc.go4lunch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIMER = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {
            if(FirebaseAuth.getInstance().getCurrentUser() != null){
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIMER);

    }
}
