package com.fleury.marc.go4lunch.controllers.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.fleury.marc.go4lunch.R;
import com.fleury.marc.go4lunch.api.UserHelper;
import com.fleury.marc.go4lunch.utils.AlarmReceiver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private SharedPreferences mPreferences;

    @BindView(R.id.settings_name_edit) EditText mEditText;
    @BindView(R.id.settings_name_button) Button mButton;
    @BindView(R.id.settings_switch_button) Switch mSwitch;
    @BindView(R.id.settings_switch_text) TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        mPreferences = this.getSharedPreferences("pref", MODE_PRIVATE);

        mButton.setOnClickListener(this::onClick);

        configureToolbar();
        configureEditText();
        updateSwitch();
        updateTextView();
    }

    public void onClick(View v) {
        if (v == mButton) { // Update user's name
            UserHelper.updateUsername(mEditText.getText().toString(), FirebaseAuth.getInstance().getUid());
        }
    }

    private void updateSwitch() {
        mSwitch.setChecked(mPreferences.getBoolean("switchCheck", false));

        mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                // Set the alarm to start at 12:00 p.m.
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, 12);
                // Set the alarm to repeat everyday
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);

                mPreferences.edit().putBoolean("switchCheck", true).apply();
            } else {
                alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                alarmMgr.cancel(alarmIntent);

                mPreferences.edit().putBoolean("switchCheck", false).apply();
            }
            updateTextView();
        });
    }

    private void updateTextView() {
        if(mPreferences.getBoolean("switchCheck", false)) {
            mTextView.setText(getString(R.string.notifications, getString(R.string.on)));
        } else {
            mTextView.setText(getString(R.string.notifications, getString(R.string.off)));
        }
    }

    //----------------------
    // CONFIGURATION
    //----------------------

    private void configureToolbar() {
        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.settings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void configureEditText() { // Update EditText with user's data
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
