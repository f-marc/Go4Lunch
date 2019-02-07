package com.fleury.marc.go4lunch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    //FOR DATA
    //Identifier for Sign-In Activity
    private static final int RC_SIGN_IN = 123;
    @BindView(R.id.login_activity_coordinator_layout) CoordinatorLayout coordinatorLayout;


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
        //Handle SignIn Activity response on activity result
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    // --------------------
    // ACTIONS
    // --------------------

    /*@OnClick(R.id.login_activity_button_login)
    public void onClickLoginButton() {
        //Launch Sign-In Activity when user clicked on Login Button
        this.startSignInActivity();
        //Toast.makeText(getApplicationContext(), "Test clique boutton", Toast.LENGTH_LONG).show();
    }*/

    // --------------------
    // UI
    // --------------------

    //Show Snack Bar with a message
    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message){
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    // --------------------
    // UTILS
    // --------------------

    //Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else { // ERRORS
                if (response == null) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_authentication_canceled));
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_unknown_error));
                }
            }
        }
    }

    // --------------------
    // NAVIGATION
    // --------------------

    //Launch Sign-In Activity
    private void startSignInActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                               Arrays.asList(
                                       new AuthUI.IdpConfig.EmailBuilder().build(), //EMAIL
                                       new AuthUI.IdpConfig.FacebookBuilder().build(), //FACEBOOK
                                       new AuthUI.IdpConfig.TwitterBuilder().build(), //TWITTER
                                       new AuthUI.IdpConfig.GoogleBuilder().build())) //GOOGLE
                        .setIsSmartLockEnabled(false, true)
                        //.setLogo(R.drawable.ic_logo_auth)
                        .build(),
                RC_SIGN_IN);
    }

}
