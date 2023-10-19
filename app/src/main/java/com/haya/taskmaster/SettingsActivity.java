package com.haya.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

import java.util.prefs.PreferencesFactory;

public class SettingsActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    public static final String USERNAME_TAG="username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);


        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        Button saveSettingsButton = (Button) findViewById(R.id.settingsSaveBtn);

        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor edit= sharedPreferences.edit();
                String usernameText = ((EditText) findViewById(R.id.usernameInput)).getText().toString();
                edit.putString(USERNAME_TAG,usernameText);
                edit.apply();

                Snackbar.make(findViewById(R.id.settingsActivity),"Username Saved",Snackbar.LENGTH_SHORT).show();

            }
        });



    }
}