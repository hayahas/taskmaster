package com.haya.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences ;

    public static final String TASK_TITLE_TAG="title";
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

        TextView task1=(TextView) findViewById(R.id.task1);
        TextView task2=(TextView) findViewById(R.id.task2);
        TextView task3=(TextView) findViewById(R.id.task3);

        task1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor edit= sharedPreferences.edit();
                String taskTitle = ((TextView) findViewById(R.id.task1)).getText().toString();
                edit.putString(TASK_TITLE_TAG,taskTitle);
                edit.apply();
                Intent goToTask1Details=new Intent(MainActivity.this,TaskDetailsActivity.class);
                goToTask1Details.putExtra(TASK_TITLE_TAG,taskTitle);
                startActivity(goToTask1Details);
            }
        });

        task2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor edit= sharedPreferences.edit();
                String taskTitle = ((TextView) findViewById(R.id.task2)).getText().toString();
                edit.putString(TASK_TITLE_TAG,taskTitle);
                edit.apply();
                Intent goToTask2Details=new Intent(MainActivity.this,TaskDetailsActivity.class);
                goToTask2Details.putExtra(TASK_TITLE_TAG,taskTitle);
                startActivity(goToTask2Details);
            }
        });

        task3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor edit= sharedPreferences.edit();
                String taskTitle = ((TextView) findViewById(R.id.task3)).getText().toString();
                edit.putString(TASK_TITLE_TAG,taskTitle);
                edit.apply();
                Intent goToTask3Details=new Intent(MainActivity.this,TaskDetailsActivity.class);
                goToTask3Details.putExtra(TASK_TITLE_TAG,taskTitle);
                startActivity(goToTask3Details);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        String userTasks=sharedPreferences.getString(SettingsActivity.USERNAME_TAG,"My Tasks");
        ((TextView) findViewById(R.id.usernameTasks)).setText(getString(R.string.username_from_user_settings, userTasks));
    }
}