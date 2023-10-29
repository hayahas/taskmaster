package com.haya.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;
import com.haya.taskmaster.database.TaskMasterDatabase;
import com.haya.taskmaster.models.Task;
import com.haya.taskmaster.models.TaskStateEnums;

import java.util.Date;

public class AddTaskActivity extends AppCompatActivity {

    TaskMasterDatabase taskMasterDatabase;
    public static final String DATABASE_NAME="taskmaster";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tasks);


        taskMasterDatabase = Room.databaseBuilder(
                        getApplicationContext(), TaskMasterDatabase.class, DATABASE_NAME
                )
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        Spinner taskState = (Spinner) findViewById(R.id.taskStateSpinner);

        taskState.setAdapter(new ArrayAdapter<>(
        this,
        android.R.layout.simple_spinner_item,
                TaskStateEnums.values()
                ));


        Button addTask = (Button) findViewById(R.id.addNewTaskBtn);

        addTask.setOnClickListener(v -> {
            Snackbar.make(findViewById(R.id.addTaskActivity),"Task Added",Snackbar.LENGTH_SHORT).show();

            Task newTask = new Task(
                    ((EditText) findViewById(R.id.editTextTaskTitle)).getText().toString(),
            ((EditText) findViewById(R.id.editTextTaskDescription)).getText().toString(),
            new Date(),
            TaskStateEnums.fromString(taskState.getSelectedItem().toString())

           );

            taskMasterDatabase.taskDAO().insertToTask(newTask);

        });



    }
}