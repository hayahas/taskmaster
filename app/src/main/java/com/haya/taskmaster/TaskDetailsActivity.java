package com.haya.taskmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;

import java.io.IOException;
import java.util.Locale;

public class TaskDetailsActivity extends AppCompatActivity {

    ImageView goToEditTask;
    ImageView backToHome;
    public static final String TAG ="TaskDetailsActivity";
    static final int LOCATION_POLLING_INTERVAL = 5 * 1000;
    FusedLocationProviderClient locationProviderClient = null;
     TextView locationTextView;
    Geocoder geocoder = null;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_details);

        locationTextView= (TextView) findViewById(R.id.location);
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(LOCATION_POLLING_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                try {
                    String address = geocoder.getFromLocation(
                                    locationResult.getLastLocation().getLatitude(),
                                    locationResult.getLastLocation().getLongitude(),
                                    1)
                            .get(0)
                            .getAddressLine(0);
                    runOnUiThread(() -> {
                        locationTextView.setText("Current Location: " + address);
                    });
                    Log.i(TAG, "Repeating current location is: " + address);
                } catch (IOException ioe) {
                    Log.e(TAG, "Could not get subscribed location: " + ioe.getMessage(), ioe);
                }
            }
        };

       sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        setupCallingIntentData();
        setupEditTaskBtn();
        setupBackToHomeBtn();


        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(LOCATION_POLLING_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        locationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        locationProviderClient.flushLocations();

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                try {
                    String address = geocoder.getFromLocation(
                                    locationResult.getLastLocation().getLatitude(),
                                    locationResult.getLastLocation().getLongitude(),
                                    1)
                            .get(0)
                            .getAddressLine(0);

                    runOnUiThread(() -> {
                        locationTextView.setText(address);
                    });

                    Log.i(TAG, "Repeating current location is: " + address);
                } catch (IOException ioe) {
                    Log.e(TAG, "Could not get subscribed location: " + ioe.getMessage(), ioe);
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());

    }

    private void setupCallingIntentData(){
        Intent callingIntent = getIntent();
        String taskTitle = null;
        String taskBody = null;
        String taskState = null;
        String taskDate = null;
//        String taskTeam = null;

        if(callingIntent != null){
            taskTitle = callingIntent.getStringExtra(MainActivity.TASK_TITLE_TAG);
            taskBody = callingIntent.getStringExtra(MainActivity.TASK_BODY_TAG);
            taskState = callingIntent.getStringExtra(MainActivity.TASK_STATE_TAG);
            taskDate = callingIntent.getStringExtra(MainActivity.TASK_DATE_CREATED);

//            taskTeam = callingIntent.getStringExtra(MainActivity.TASK_TEAM_TAG);
        }

        TextView taskSelected = (TextView) findViewById(R.id.taskTitleInput);
        TextView taskSelectedBody = (TextView) findViewById(R.id.taskBody);
        TextView taskSelectedState = (TextView) findViewById(R.id.taskState);
        TextView taskDateCreated = (TextView) findViewById(R.id.taskDate);

//        TextView taskTeamAssigned = (TextView) findViewById(R.id.taskTeam);


        if (taskTitle != null){
            taskSelected.setText(taskTitle);
            taskSelectedBody.setText(taskBody);
            taskSelectedState.setText(taskState);
            taskDateCreated.setText(taskDate);
//            taskTeamAssigned.setText(taskTeam);
        }else {
            taskSelected.setText("Task not specified");
        }




    }
    private void setupBackToHomeBtn(){
        backToHome= (ImageView) findViewById(R.id.homeLogoTaskDetails);
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backToHome=new Intent(TaskDetailsActivity.this,MainActivity.class);
                startActivity(backToHome);
            }
        });
    }

    private void setupEditTaskBtn(){
       goToEditTask = (ImageView) findViewById(R.id.editTaskFromDetails);
       goToEditTask.setOnClickListener(button -> {
           Intent callingIntent = getIntent();
           String taskTitle = null;
           String taskBody = null;
           String taskState = null;
           String taskDate = null;
           String taskId = null;


           if(callingIntent != null){
               taskTitle = callingIntent.getStringExtra(MainActivity.TASK_TITLE_TAG);
               taskBody = callingIntent.getStringExtra(MainActivity.TASK_BODY_TAG);
               taskState = callingIntent.getStringExtra(MainActivity.TASK_STATE_TAG);
               taskDate = callingIntent.getStringExtra(MainActivity.TASK_DATE_CREATED);
               taskId = callingIntent.getStringExtra(MainActivity.TASK_ID_TAG);

           }

           Intent goTEditTask = new Intent(TaskDetailsActivity.this,EditTaskActivity.class);
           goTEditTask.putExtra(MainActivity.TASK_TITLE_TAG,taskTitle);
           goTEditTask.putExtra(MainActivity.TASK_BODY_TAG,taskBody);
           goTEditTask.putExtra(MainActivity.TASK_STATE_TAG,taskState);
           goTEditTask.putExtra(MainActivity.TASK_ID_TAG,taskId);
           goTEditTask.putExtra(MainActivity.TASK_DATE_CREATED, taskDate);
           startActivity(goTEditTask);

       });
    }
}