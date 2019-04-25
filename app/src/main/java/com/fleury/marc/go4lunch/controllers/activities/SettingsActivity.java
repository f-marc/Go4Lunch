package com.fleury.marc.go4lunch.controllers.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.fleury.marc.go4lunch.R;
import com.fleury.marc.go4lunch.api.UserHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    @BindView(R.id.settings_name_edit) EditText mEditText;
    @BindView(R.id.settings_name_button) Button mButton;
    @BindView(R.id.settings_switch_button) Switch mSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        mButton.setOnClickListener(this::onClick);
        mSwitch.setOnClickListener(this::onClick);

        configureToolbar();
        configureEditText();
    }

    public void onClick(View v) {
        if (v == mButton) {
            UserHelper.updateUsername(mEditText.getText().toString(), FirebaseAuth.getInstance().getUid());
        }

        if (v == mSwitch) {

        }
    }

    //----------------------
    // CONFIGURATION
    //----------------------

    private void configureToolbar() {
        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void configureEditText() {
        UserHelper.getUser(FirebaseAuth.getInstance().getUid()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("TAG", "DocumentSnapshot data: " + document.getData().get("username"));
                    if (document.getData().get("username") != null) {
                        mEditText.setText(document.getData().get("username").toString());
                    }
                } else {
                    Log.d("TAG", "No such document");
                }
            } else {
                Log.d("TAG", "Get failed with ", task.getException());
            }
        });
    }
}
