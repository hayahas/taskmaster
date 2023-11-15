package com.haya.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.prefs.PreferencesFactory;

public class SettingsActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    public static final String USERNAME_TAG="username";
    public static final String USER_TEAM_TAG="team";
    public static final String TAG="settings";

    CompletableFuture<List<Team>> teamFuture = new CompletableFuture<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);


        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        Button saveSettingsButton = (Button) findViewById(R.id.settingsSaveBtn);
        Spinner userTeamsSpinner = (Spinner) findViewById(R.id.settingTeamSpinner);

        Amplify.API.query(
                ModelQuery.list(Team.class),
                success ->{
                    Log.i(TAG,"SettingsActivity() : Assigned to Team Successfully");
                    ArrayList<String> teamNames = new ArrayList<>();
                    ArrayList<Team> teams = new ArrayList<>();
                    for(Team team : success.getData()){
                        teams.add(team);
                        teamNames.add(team.getTeamName());
                    }
                    teamFuture.complete(teams);

                    runOnUiThread(() -> {
                        userTeamsSpinner.setAdapter(new ArrayAdapter<>(this,(
                                android.R.layout.simple_spinner_item),
                                teamNames
                        ));
                    });

                },
                failure -> {
                    Log.i(TAG,"AddTaskActivity : Failed to  Assign to Team");
                    teamFuture.complete(null);
                }
        );


        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor edit= sharedPreferences.edit();
                String usernameText = ((EditText) findViewById(R.id.usernameInput)).getText().toString();
                String userTeam = userTeamsSpinner.getSelectedItem().toString();
                edit.putString(USERNAME_TAG,usernameText);
                edit.putString(USER_TEAM_TAG,userTeam);
                edit.apply();

                Snackbar.make(findViewById(R.id.settingsActivity),"Username Saved",Snackbar.LENGTH_SHORT).show();

            }
        });



    }
}