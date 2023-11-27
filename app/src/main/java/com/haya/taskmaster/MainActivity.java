package com.haya.taskmaster;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.haya.taskmaster.adapter.TasksRecyclerViewAdapter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences ;
    public static final String TASK_ID_TAG="taskId";
    public static final String TASK_TITLE_TAG="title";
    public static final String TASK_BODY_TAG="body";
    public static final String TASK_STATE_TAG="state";
    public static final String TASK_DATE_CREATED="date";
    public final String TAG = "mainActivity";

    Button addTaskButton;
    Button allTasksButton;
    ImageView settingsButton;
    ImageView logoutButton;
    Button signupButton;
    Button loginButton;
    List <Task> tasks =null ;
    TasksRecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        tasks = new ArrayList<>();

        recyclerViewSetup();
//        addTeamsToDBQueries();
        setupAddTasksBtn();
        setupAllTasksBtn();
        setupSettingsBtn();
        setupLogoutBtn();
        setupSignupBtn();
        setupLoginBtn();
//        setupFileS3();
    }


    private void setupFileS3(){

        String emptyFileName = "emptyFileName";
        File emptyFile = new File(getApplicationContext().getFilesDir(), emptyFileName);

        try{
            BufferedWriter emptyFileBufferedWriter = new BufferedWriter(new FileWriter(emptyFile));
            emptyFileBufferedWriter.append("New file from Lab 37\n testing S3");
            emptyFileBufferedWriter.close();
        }catch(IOException ioe){
            Log.e(TAG, "MainActivity.setupFileS3() : Failed to write file Locally : " + emptyFileName);
        }

        String fileKey = "FirstS3File.txt";

        Amplify.Storage.uploadFile(
                fileKey,
                emptyFile,
                success ->{
                    Log.i(TAG, "MainActivity.setupFileS3() : S3 file uploaded successfully : " + success.getKey());
                },
                failure -> {
                    Log.e(TAG, "MainActivity.setupFileS3() : Failed to upload S3 file : " + failure.getMessage());
                }
        );
    }
    private void setupLoginBtn(){
        loginButton  = (Button) findViewById(R.id.loginBtnMainPage);
        loginButton.setOnClickListener(view -> {
            Intent goToSignup=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(goToSignup);
        });
    }
    private void setupSignupBtn(){
        signupButton  = (Button) findViewById(R.id.signupBtnMainPage);
        signupButton.setOnClickListener(view -> {
            Intent goToSignup=new Intent(MainActivity.this,SignupActivity.class);
            startActivity(goToSignup);
        });
    }
    private void setupLogoutBtn(){
        logoutButton = (ImageView) findViewById(R.id.logoutBtnLogo);
        logoutButton.setOnClickListener(v -> {

            Amplify.Auth.signOut(
                    () ->
                    {
                        Log.i(TAG,"Logout succeeded");
                        runOnUiThread(() ->
                        {
                            ((TextView)findViewById(R.id.usernameTasks)).setText("");
                        });
                        Intent goToLogInIntent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(goToLogInIntent);
                    },
                    failure ->
                    {
                        Log.i(TAG, "Logout failed");
                        runOnUiThread(() ->
                        {
                            Toast.makeText(MainActivity.this, "Log out failed", Toast.LENGTH_LONG);
                        });
                    }
            );
        });
    }
    private void setupSettingsBtn(){
        settingsButton  = (ImageView) findViewById(R.id.settingImgBtn);

        settingsButton.setOnClickListener(view -> {
            Intent goToSettings=new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(goToSettings);
        });


    }
    private void setupAllTasksBtn(){
        allTasksButton = (Button) findViewById(R.id.allTasksbtn);
        allTasksButton.setOnClickListener(view -> {

            Intent goToAllTasks=new Intent(MainActivity.this,AllTasksActivity.class);
            startActivity(goToAllTasks);
        });

    }
    private void setupAddTasksBtn(){
        addTaskButton = (Button) findViewById(R.id.addTaskbtn);
        addTaskButton.setOnClickListener(view -> {
            System.out.println("Hello from the other side");

            Intent goToAddTask=new Intent(MainActivity.this,AddTaskActivity.class);
            startActivity(goToAddTask);
        });
    }
    private void addTeamsToDBQueries(){
        Team team1=Team.builder()
                .teamName("Frontend Team")
                .email("frontendTeam@gmail.com")
                .build();


        Team team2=Team.builder()
                .teamName("Backend Team")
                .email("backendTeam@gmail.com")
                .build();

        Team team3=Team.builder()
                .teamName("Database Team")
                .email("databaseTeam@gmail.com")
                .build();


        Amplify.API.mutate(
                ModelMutation.create(team1),
                success -> Log.i(TAG,"MainActivity() : Team Created Successfully"),
                failure -> Log.i(TAG,"MainActivity() : Team Creation Failed ")
        );

        Amplify.API.mutate(
                ModelMutation.create(team2),
                success -> Log.i(TAG,"MainActivity() : Team Created Successfully"),
                failure -> Log.i(TAG,"MainActivity() : Team Creation Failed ")
        );

        Amplify.API.mutate(
                ModelMutation.create(team3),
                success -> Log.i(TAG,"MainActivity() : Team Created Successfully"),
                failure -> Log.i(TAG,"MainActivity() : Team Creation Failed ")
        );
    }
    private void recyclerViewSetup() {
        RecyclerView tasksRecycler = (RecyclerView) findViewById(R.id.tasksRecycelerView);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this);
        tasksRecycler.setLayoutManager(layout);


        adapter = new TasksRecyclerViewAdapter(tasks, this);
        tasksRecycler.setAdapter(adapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();

        String userTasks=sharedPreferences.getString(SettingsActivity.USERNAME_TAG,"My Tasks");
        ((TextView) findViewById(R.id.usernameTasks)).setText(getString(R.string.username_from_user_settings, userTasks));
        String userTeam=sharedPreferences.getString(SettingsActivity.USER_TEAM_TAG,"My Team");
        ((TextView) findViewById(R.id.userTeam)).setText(getString(R.string.user_team_from_user_settings, userTeam));

        Amplify.API.query(
                ModelQuery.list(Task.class),
                success -> {
                    Log.i(TAG,"MainActivity(): Read Task Successfully");
                    tasks.clear();
                    for(Task databaseTask : success.getData()){
                            if (databaseTask.getTaskTeam().getTeamName().equals(getString(R.string.user_team_from_user_settings, userTeam)))
                            {
                                tasks.add(databaseTask);
                            }
                                System.out.println(databaseTask.getName() + "  " +  databaseTask.getTaskTeam());
                    }
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                },
                failure -> Log.i(TAG,"Read Task Failed")
       );

        AuthUser authUser = Amplify.Auth.getCurrentUser();
        String username=" ";
        if (authUser == null){
            Button signupButton = (Button) findViewById(R.id.signupBtnMainPage);
            signupButton.setVisibility(View.VISIBLE);
            TextView textSignup = (TextView) findViewById(R.id.textSignup);
            textSignup.setVisibility(View.VISIBLE);
            Button loginButton = (Button) findViewById(R.id.loginBtnMainPage);
            loginButton.setVisibility(View.VISIBLE);
            TextView textLogin = (TextView) findViewById(R.id.textLogin);
            textLogin.setVisibility(View.VISIBLE);
            ImageView logoutButton = (ImageView) findViewById(R.id.logoutBtnLogo);
            logoutButton.setVisibility(View.INVISIBLE);
        }else{
            username = authUser.getUsername();
            Log.i(TAG, "Username is: "+ username);
            Button signupButton = (Button) findViewById(R.id.signupBtnMainPage);
            signupButton.setVisibility(View.INVISIBLE);
            TextView textSignup = (TextView) findViewById(R.id.textSignup);
            textSignup.setVisibility(View.INVISIBLE);
            Button loginButton = (Button) findViewById(R.id.loginBtnMainPage);
            loginButton.setVisibility(View.INVISIBLE);
            TextView textLogin = (TextView) findViewById(R.id.textLogin);
            textLogin.setVisibility(View.INVISIBLE);
            ImageView logoutButton = (ImageView) findViewById(R.id.logoutBtnLogo);
            logoutButton.setVisibility(View.VISIBLE);

            String username2 = username;
            Amplify.Auth.fetchUserAttributes(
                    success ->
                    {
                        Log.i(TAG, "Fetch user attributes succeeded for username: "+ username2);
                        for (AuthUserAttribute userAttribute: success){
                            if(userAttribute.getKey().getKeyString().equals("nickname")){
                                String user = userAttribute.getValue();
                                runOnUiThread(() ->
                                {
                                    ((TextView)findViewById(R.id.usernameTasks)).setText(user);
                                });
                            }
                        }
                    },
                    failure ->
                    {
                        Log.i(TAG, "Fetch user attributes failed: "+failure.toString());
                    }
            );
        }

    }
}