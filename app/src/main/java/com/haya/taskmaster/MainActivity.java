package com.haya.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.haya.taskmaster.adapter.TasksRecyclerViewAdapter;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences ;

    public static final String TASK_TITLE_TAG="title";
    public static final String TASK_BODY_TAG="body";
    public static final String TASK_STATE_TAG="state";
    public static final String TASK_DATE_CREATED="date";
    public static final String TASK_TEAM_TAG="team";
    public final String TAG = "mainActivity";
    public static final String DATABASE_NAME="taskmaster";

    List <Task> tasks =null ;

    TasksRecyclerViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Button addTaskButton = (Button) findViewById(R.id.addTaskbtn);
        Button allTasksButton = (Button) findViewById(R.id.allTasksbtn);
        ImageView settingsButton = (ImageView) findViewById(R.id.settingImgBtn);

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Hello from the other side");

                Intent goToAddTask=new Intent(MainActivity.this,AddTaskActivity.class);
                startActivity(goToAddTask);
            }
        });

        allTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent goToAllTasks=new Intent(MainActivity.this,AllTasksActivity.class);
                startActivity(goToAllTasks);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToSettings=new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(goToSettings);
            }
        });



        tasks = new ArrayList<>();

//        Team team1=Team.builder()
//                .teamName("Frontend Team")
//                .email("frontendTeam@gmail.com")
//                .build();
//
//        Team team2=Team.builder()
//                .teamName("Backend Team")
//                .email("backendTeam@gmail.com")
//                .build();
//
//        Team team3=Team.builder()
//                .teamName("Database Team")
//                .email("databaseTeam@gmail.com")
//                .build();
//
//
//        Amplify.API.mutate(
//                ModelMutation.create(team1),
//                success -> Log.i(TAG,"MainActivity() : Team Created Successfully"),
//                failure -> Log.i(TAG,"MainActivity() : Team Creation Failed ")
//        );
//
//        Amplify.API.mutate(
//                ModelMutation.create(team2),
//                success -> Log.i(TAG,"MainActivity() : Team Created Successfully"),
//                failure -> Log.i(TAG,"MainActivity() : Team Creation Failed ")
//        );
//
//        Amplify.API.mutate(
//                ModelMutation.create(team3),
//                success -> Log.i(TAG,"MainActivity() : Team Created Successfully"),
//                failure -> Log.i(TAG,"MainActivity() : Team Creation Failed ")
//        );

        recyclerViewSetup();

    }

public void recyclerViewSetup() {
    RecyclerView tasksRecycler = (RecyclerView) findViewById(R.id.tasksRecycelerView);
    RecyclerView.LayoutManager layout = new LinearLayoutManager(this);
    tasksRecycler.setLayoutManager(layout);


    adapter = new TasksRecyclerViewAdapter(tasks, this);
    tasksRecycler.setAdapter(adapter);
}

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
                    Log.i(TAG,"Read Task Successfully");
                    tasks.clear();
                    for(Task databaseTask : success.getData()){
                        String teamName= "Backend Team";
//                        if(databaseTask.getTaskTeam().getTeamName().equals(teamName)){
                            tasks.add(databaseTask);
                        System.out.println(databaseTask.getName()+ "  " +  databaseTask.getTaskTeam());
//                        }

                    }
                    runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                    });
                },
                failure -> {
                    Log.i(TAG,"Read Task Failed");
                }
        );

    }
}