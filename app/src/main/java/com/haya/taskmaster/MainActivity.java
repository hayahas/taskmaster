package com.haya.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haya.taskmaster.adapter.TasksRecyclerViewAdapter;
import com.haya.taskmaster.database.TaskMasterDatabase;
import com.haya.taskmaster.models.Task;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences ;

    public static final String TASK_TITLE_TAG="title";
    public static final String TASK_BODY_TAG="body";
    public static final String TASK_STATE_TAG="state";
    public static final String DATABASE_NAME="taskmaster";
    TaskMasterDatabase taskMasterDatabase;

    List <Task> tasks =null ;
//
    TasksRecyclerViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Button addTaskButton = (Button) findViewById(R.id.addTaskbtn);
        Button allTasksButton = (Button) findViewById(R.id.allTasksbtn);
        ImageView settingsButton = (ImageView) findViewById(R.id.settingImgBtn);

        taskMasterDatabase = Room.databaseBuilder(
                        getApplicationContext(), TaskMasterDatabase.class, DATABASE_NAME
                )
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        tasks=taskMasterDatabase.taskDAO().findAll();
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

//tasksSetup();

recyclerViewSetup();

    }

public void recyclerViewSetup() {
    RecyclerView tasksRecycler = (RecyclerView) findViewById(R.id.tasksRecycelerView);
    RecyclerView.LayoutManager layout = new LinearLayoutManager(this);
    tasksRecycler.setLayoutManager(layout);


//    List<Task> tasks = new ArrayList<>();

//    tasks.add(new Task("Task 1", "Install Android Studio", "Complete"));
//    tasks.add(new Task("Task 2", "Install Emulator", "Complete"));
//    tasks.add(new Task("Task 3", "Setup first Android app", "Complete"));
//    tasks.add(new Task("Task 4", "Name the new project Task Master", "Assigned"));
//    tasks.add(new Task("Task 5", "User can Add tasks", "Assigned"));
//    tasks.add(new Task("Task 6", "User can display all tasks", "In progress"));
//    tasks.add(new Task("Task 7", "User can display all tasks in recycler View", "In Progress"));
//    tasks.add(new Task("Task 8", "User tasks added to database", "new"));
//    tasks.add(new Task("Task 9", ".....", "new"));
//    tasks.add(new Task("Task 10", "anythingggg", "new"));
//    tasks.add(new Task("Task 11", "print Hello world", "new"));
//    tasks.add(new Task("Task 12", "App ly changes", "new"));

    adapter = new TasksRecyclerViewAdapter(tasks, this);
    tasksRecycler.setAdapter(adapter);
}


//    public void tasksSetup(){
//        TextView task1=(TextView) findViewById(R.id.task1);
//        TextView task2=(TextView) findViewById(R.id.task2);
//        TextView task3=(TextView) findViewById(R.id.task3);
//
//        task1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SharedPreferences.Editor edit= sharedPreferences.edit();
//                String taskTitle = ((TextView) findViewById(R.id.task1)).getText().toString();
//                edit.putString(TASK_TITLE_TAG,taskTitle);
//                edit.apply();
//                Intent goToTask1Details=new Intent(MainActivity.this,TaskDetailsActivity.class);
//                goToTask1Details.putExtra(TASK_TITLE_TAG,taskTitle);
//                startActivity(goToTask1Details);
//            }
//        });
//
//        task2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SharedPreferences.Editor edit= sharedPreferences.edit();
//                String taskTitle = ((TextView) findViewById(R.id.task2)).getText().toString();
//                edit.putString(TASK_TITLE_TAG,taskTitle);
//                edit.apply();
//                Intent goToTask2Details=new Intent(MainActivity.this,TaskDetailsActivity.class);
//                goToTask2Details.putExtra(TASK_TITLE_TAG,taskTitle);
//                startActivity(goToTask2Details);
//            }
//        });
//
//        task3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SharedPreferences.Editor edit= sharedPreferences.edit();
//                String taskTitle = ((TextView) findViewById(R.id.task3)).getText().toString();
//                edit.putString(TASK_TITLE_TAG,taskTitle);
//                edit.apply();
//                Intent goToTask3Details=new Intent(MainActivity.this,TaskDetailsActivity.class);
//                goToTask3Details.putExtra(TASK_TITLE_TAG,taskTitle);
//                startActivity(goToTask3Details);
//            }
//        });
//    }


    @Override
    protected void onResume() {
        super.onResume();

        String userTasks=sharedPreferences.getString(SettingsActivity.USERNAME_TAG,"My Tasks");
        ((TextView) findViewById(R.id.usernameTasks)).setText(getString(R.string.username_from_user_settings, userTasks));

        tasks.clear();
        tasks.addAll(taskMasterDatabase.taskDAO().findAll());
        adapter.notifyDataSetChanged();
    }
}