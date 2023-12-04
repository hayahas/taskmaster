package com.haya.taskmaster;


import android.Manifest;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.model.temporal.Temporal;

import com.amplifyframework.datastore.generated.model.TaskStateEnums;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.material.snackbar.Snackbar;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AddTaskActivity extends AppCompatActivity {


    public static final String TAG = "AddTaskActivity";
    static final int LOCATION_POLLING_INTERVAL = 5 * 1000;

    Button addTask;
    Spinner taskStateSpinner = null;
    Spinner teamsSpinner = null;
    CompletableFuture<List<Team>> teamFuture = new CompletableFuture<>();

    ImageView homeBtn;
    ActivityResultLauncher<Intent> activityResultLauncher;
    Button addImgButton;
    private String s3ImageKey = "";

    FusedLocationProviderClient locationProviderClient = null;

    Geocoder geocoder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tasks);

        activityResultLauncher = getImagePickerActivityLauncher();

        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        locationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        locationProviderClient.flushLocations();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationProviderClient.getLastLocation().addOnSuccessListener(location ->
        {
            if (location == null) {
                Log.e(TAG, "Location CallBack was null");
            }
            String currentLatitude = Double.toString(location.getLatitude());
            String currentLongitude = Double.toString(location.getLongitude());
            Log.i(TAG, "Our userLatitude: " + location.getLatitude());
            Log.i(TAG, "Our userLongitude: " + location.getLongitude());
        });

        locationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }

            @Override
            public boolean isCancellationRequested() {
                return false;
            }
        });

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
                    Log.i(TAG, "Repeating current location is: " + address);
                } catch (IOException ioe) {
                    Log.e(TAG, "Could not get subscribed location: " + ioe.getMessage(), ioe);
                }
            }
        };

        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());

        setUpSpinners();
        setUpSaveBtn();
        setupAddImgBtn();
        setUpDeleteImageButton();
        updateImageButtons();
        setupHomeBtn();

    }

    @Override
    protected void onResume() {
        super.onResume();

        setUpDeleteImageButton();

        Intent callingIntent = getIntent();

        if (callingIntent != null && callingIntent.getType() != null && callingIntent.getType().equals("text/plain")) {
            String callingText = callingIntent.getStringExtra(Intent.EXTRA_TEXT);
            String cleanText = cleanText(callingText);

            ((EditText) findViewById(R.id.editTextNewTaskTitle)).setText(cleanText);
        }

        if (callingIntent != null && callingIntent.getType() != null && callingIntent.getType().startsWith("image")) {
            Uri incomingImageFileUri = callingIntent.getParcelableExtra(Intent.EXTRA_STREAM);

            if (incomingImageFileUri != null) {
                InputStream incomingImageFileInputStream = null;

                try {
                    incomingImageFileInputStream = getContentResolver().openInputStream(incomingImageFileUri);

                    ImageView taskImage = findViewById(R.id.taskImgImageViewAddTaskPage);

                    if (taskImage != null) {

                        taskImage.setImageBitmap(BitmapFactory.decodeStream(incomingImageFileInputStream));
                    } else {
                        Log.e(TAG, "ImageView is null");
                    }
                } catch (FileNotFoundException fileNotFoundException) {
                    Log.e(TAG, " Could not get file stream from the URI " + fileNotFoundException.getMessage(), fileNotFoundException);
                }
            }
        }


    }

    private void setupHomeBtn(){

        homeBtn = (ImageView) findViewById(R.id.homeLogoAddTaskPage);
        homeBtn.setOnClickListener(b -> {

            Intent backToMain = new Intent(AddTaskActivity.this, MainActivity.class);
            startActivity(backToMain);

        });

    }
    private String cleanText(String text) {
        text = text.replaceAll("\\b(?:https?|ftp):\\/\\/\\S+\\b", "");
        text = text.replaceAll("\"", "");
        return text;
    }

    private void setUpSpinners() {

        taskStateSpinner = (Spinner) findViewById(R.id.newTaskStateSpinner);
        teamsSpinner = (Spinner) findViewById(R.id.newTeamsSpinner);
        taskStateSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                TaskStateEnums.values()
        ));


        Amplify.API.query(
                ModelQuery.list(Team.class),
                success -> {
                    Log.i(TAG, "AddTaskActivity() : Team Added Successfully");
                    ArrayList<String> teamNames = new ArrayList<>();
                    ArrayList<Team> teams = new ArrayList<>();
                    for (Team team : success.getData()) {
                        teams.add(team);
                        teamNames.add(team.getTeamName());
                    }
                    teamFuture.complete(teams);

                    runOnUiThread(() -> {
                        teamsSpinner.setAdapter(new ArrayAdapter<>(this, (
                                android.R.layout.simple_spinner_item),
                                teamNames
                        ));
                    });

                },
                failure -> {
                    Log.i(TAG, "AddTaskActivity : Failed to Add Team");
                    teamFuture.complete(null);
                }
        );
    }

    private void setUpSaveBtn() {
        addTask = (Button) findViewById(R.id.addNewTaskBtn);
        addTask.setOnClickListener(v -> {


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            String title = ((EditText) findViewById(R.id.editTextNewTaskTitle)).getText().toString();
            String description = ((EditText) findViewById(R.id.editTextNewTaskDescription)).getText().toString();
            String dateCreated = com.amazonaws.util.DateUtils.formatISO8601Date(new Date());
            String selectedTeamString = teamsSpinner.getSelectedItem().toString();


            List<Team> teams = null;

            try {
                teams = teamFuture.get();
            } catch (InterruptedException ie) {
                Log.e(TAG, " InterruptedException while getting teams");
            } catch (ExecutionException ee) {
                Log.e(TAG, " ExecutionException while getting teams");
            }

            Team selectedTeam = teams.stream().filter(c -> c.getTeamName().equals(selectedTeamString)).findAny().orElseThrow(RuntimeException::new);

            locationProviderClient.getLastLocation().addOnSuccessListener(location ->
                            {
                                if (location == null) {
                                    Log.e(TAG, "Location CallBack was null");
                                }
                                String currentLatitude = Double.toString(location.getLatitude());
                                String currentLongitude = Double.toString(location.getLongitude());
                                Log.i(TAG, "Our userLatitude: " + location.getLatitude());
                                Log.i(TAG, "Our userLongitude: " + location.getLongitude());
                                saveTask(title, description, s3ImageKey,currentLatitude, currentLongitude, selectedTeam);

                            }

                    ).addOnCanceledListener(() ->
                    {
                        Log.e(TAG, "Location request was Canceled");
                    })
                    .addOnFailureListener(failure ->
                    {
                        Log.e(TAG, "Location request failed, Error was: " + failure.getMessage(), failure.getCause());
                    })
                    .addOnCompleteListener(complete ->
                    {
                        Log.e(TAG, "Location request Completed");
                    });


        });

    }
    private void saveTask(String title,String description,String s3ImageKey,String latitude,String longitude, Team selectedTeam){


        Task newTask = Task.builder()
                .name(title)
                .taskLatitude(latitude)
                .taskLongitude(longitude)
                .description(description)
                .dateCreated(new Temporal.DateTime(new Date(), 0))
                .taskState((TaskStateEnums) taskStateSpinner.getSelectedItem())
                .taskTeam(selectedTeam)
                .taskImageS3Key(s3ImageKey)
                .build();



if(!s3ImageKey.isEmpty())
{
        Amplify.API.mutate(
                ModelMutation.create(newTask),
                successResponse -> {
                    Log.i(TAG, "AddTaskActivity.onCreate(): made a new task successfully");
                    runOnUiThread(() -> {
                        Snackbar.make(findViewById(R.id.addTaskActivity),"Task Added",Snackbar.LENGTH_SHORT).show(); });
                },
                failureResponse -> {Log.e(TAG, "AddTaskActivity.onCreate(): task adding failed" + failureResponse);}
        );



}else {
        Snackbar.make(findViewById(R.id.addTaskActivity),"Please add Image",Snackbar.LENGTH_SHORT).show();
}
    }
    private void setupAddImgBtn(){
        addImgButton = (Button) findViewById(R.id.addImageBtnAddTaskPage);

        addImgButton.setOnClickListener(b -> {
            launchImageSelectionIntent();
        });

    }
    private void launchImageSelectionIntent(){

        Intent imageFilePickingIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageFilePickingIntent.setType("*/*");
        imageFilePickingIntent.putExtra(Intent.EXTRA_MIME_TYPES,new String[]{"image/jpeg","image/png"});

        activityResultLauncher.launch(imageFilePickingIntent);

    }
    private ActivityResultLauncher<Intent> getImagePickerActivityLauncher(){
        ActivityResultLauncher<Intent> imagePickingActivityResultLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>()
                        {
                            @Override
                            public void onActivityResult(ActivityResult result)
                            {
                                Button addImageButton = findViewById(R.id.addImageBtnAddTaskPage);
                                if (result.getResultCode() == Activity.RESULT_OK)
                                {
                                    if (result.getData() != null)
                                    {
                                        Uri pickedImageFileUri = result.getData().getData();
                                        try
                                        {
                                            InputStream pickedImageInputStream = getContentResolver().openInputStream(pickedImageFileUri);
                                            String pickedImageFilename = getFileNameFromUri(pickedImageFileUri);
                                            Log.i(TAG, "Succeeded in getting input stream from file on phone! Filename is: " + pickedImageFilename);
                                            switchFromAddButtonToDeleteButton(addImageButton);
                                            uploadInputStreamToS3(pickedImageInputStream, pickedImageFilename,pickedImageFileUri);

                                        } catch (FileNotFoundException fnfe)
                                        {
                                            Log.e(TAG, "Could not get file from file picker! " + fnfe.getMessage(), fnfe);
                                        }
                                    }
                                }
                                else
                                {
                                    Log.e(TAG, "Activity result error in ActivityResultLauncher.onActivityResult");
                                }
                            }
                        }
                );

        return imagePickingActivityResultLauncher;
    }
    private void uploadInputStreamToS3(InputStream pickedImageInputStream, String pickedImageFilename,Uri pickedImageFileUri){
        Amplify.Storage.uploadInputStream(
                pickedImageFilename,
                pickedImageInputStream,
                success ->
                {
                    Log.i(TAG, "Succeeded in getting file uploaded to S3! Key is: " + success.getKey());
                    s3ImageKey = success.getKey();
//                    saveTask(success.getKey());
                    updateImageButtons();
                    ImageView taskImageView = findViewById(R.id.taskImgImageViewAddTaskPage);
                    InputStream pickedImageInputStreamCopy = null;
                    try
                    {
                        pickedImageInputStreamCopy = getContentResolver().openInputStream(pickedImageFileUri);
                    }
                    catch (FileNotFoundException fnfe)
                    {
                        Log.e(TAG, "Could not get file stream from URI! " + fnfe.getMessage(), fnfe);
                    }
                    taskImageView.setImageBitmap(BitmapFactory.decodeStream(pickedImageInputStreamCopy));

                },
                failure ->
                {
                    Log.e(TAG, "Failed to upload file to S3 with filename: " + pickedImageFilename + " with error: " + failure.getMessage());
                }
        );

    }
    private void setUpDeleteImageButton() {
        ImageView deleteImageButton = (ImageView) findViewById(R.id.deleteImageBtnLogo);
//        String s3ImageKey = this.s3ImageKey;
        deleteImageButton.setOnClickListener(v ->
        {
            Amplify.Storage.remove(
                    s3ImageKey ,
                    success ->
                    {
                        Log.i(TAG, "Succeeded in deleting file on S3! Key is: " + success.getKey());

                    },
                    failure ->
                    {
                        Log.e(TAG, "Failure in deleting file on S3 with key: " +  s3ImageKey  + " with error: " + failure.getMessage());
                    }
            );
            ImageView taskImageView = findViewById(R.id.taskImgImageViewAddTaskPage);
            taskImageView.setImageResource(android.R.color.transparent);
            saveTask("","","","","",null);
            switchFromDeleteButtonToAddButton(deleteImageButton);
        });
    }
    private void updateImageButtons() {
        Button addImageButton = findViewById(R.id.addImageBtnAddTaskPage);
        ImageView deleteImageButton = findViewById(R.id.deleteImageBtnLogo);
        runOnUiThread(() -> {
            if (s3ImageKey.isEmpty()) {
                deleteImageButton.setVisibility(View.INVISIBLE);
                addImageButton.setVisibility(View.VISIBLE);
            } else {
                deleteImageButton.setVisibility(View.VISIBLE);
                addImageButton.setVisibility(View.INVISIBLE);
            }
        });
    }
    private void switchFromDeleteButtonToAddButton(ImageView deleteImageButton) {
        Button addImageButton = findViewById(R.id.addImageBtnAddTaskPage);
        deleteImageButton.setVisibility(View.INVISIBLE);
        addImageButton.setVisibility(View.VISIBLE);
    }
    private void switchFromAddButtonToDeleteButton(Button addImageButton) {
        ImageView deleteImageButton = findViewById(R.id.deleteImageBtnLogo);
        deleteImageButton.setVisibility(View.VISIBLE);
        addImageButton.setVisibility(View.INVISIBLE);
    }

    // from stackoverflow
    @SuppressLint("Range")
    public String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }








}