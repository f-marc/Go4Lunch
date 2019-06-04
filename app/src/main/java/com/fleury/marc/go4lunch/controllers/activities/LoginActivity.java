package com.fleury.marc.go4lunch.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.fleury.marc.go4lunch.R;
import com.fleury.marc.go4lunch.api.UserHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    // Identifier for Sign-In Activity
    private static final int RC_SIGN_IN = 123;

    private String uid;
    private String mail;
    private String username;
    private String restaurant;
    private String restaurantName;
    private String urlPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.login_activity_button_login);

        loginButton.setOnClickListener(v ->
                this.startSignInActivity());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle SignIn Activity response on activity result
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    // Show Snack Bar with a message
    private void showSnackBar(String message){
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    // Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            // SUCCESS
            if (resultCode == RESULT_OK) {
                createUserInFirestore();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
            // ERRORS
            else {
                if (response == null) {
                    showSnackBar(getString(R.string.error_authentication_canceled));
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(getString(R.string.error_unknown_error));
                }
            }
        }
    }

    private void createUserInFirestore() { // Create user
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        restaurant = null;
        restaurantName = null;
        if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null) {
            urlPicture = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
        } else {
            urlPicture = null;
        }
        UserHelper.createUser(uid, mail, username, restaurant, restaurantName, urlPicture);
    }

    // Launch Sign-In Activity
    private void startSignInActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                               Arrays.asList(
                                       new AuthUI.IdpConfig.GoogleBuilder().build(), // GOOGLE
                                       new AuthUI.IdpConfig.FacebookBuilder().build(), // FACEBOOK
                                       new AuthUI.IdpConfig.TwitterBuilder().build(), // TWITTER
                                       new AuthUI.IdpConfig.EmailBuilder().build())) // EMAIL
                        .setIsSmartLockEnabled(false, true)
                        .setTheme(R.style.AuthUiTheme)
                        .setLogo(R.drawable.ic_local_dining_white_144dp)
                        .build(),
                RC_SIGN_IN);
    }
}
