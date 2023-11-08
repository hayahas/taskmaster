package com.haya.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.temporal.Temporal;

import com.amplifyframework.datastore.generated.model.TaskStateEnums;
import com.google.android.material.snackbar.Snackbar;


import java.util.Date;

public class AddTaskActivity extends AppCompatActivity {


    public static final String TAG = "AddTaskActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tasks);




        Spinner taskState = (Spinner) findViewById(R.id.taskStateSpinner);

        taskState.setAdapter(new ArrayAdapter<>(
        this,
        android.R.layout.simple_spinner_item,
                TaskStateEnums.values()
                ));


        Button addTask = (Button) findViewById(R.id.addNewTaskBtn);

        addTask.setOnClickListener(v -> {
            Snackbar.make(findViewById(R.id.addTaskActivity),"Task Added",Snackbar.LENGTH_SHORT).show();

//            Task newTask = new Task(
//                    ((EditText) findViewById(R.id.editTextTaskTitle)).getText().toString(),
//            ((EditText) findViewById(R.id.editTextTaskDescription)).getText().toString(),
//            new Date(),
//            TaskStateEnums.fromString(taskState.getSelectedItem().toString())
//
//           );

//            taskMasterDatabase.taskDAO().insertToTask(newTask);

            String title = ((EditText) findViewById(R.id.editTextTaskTitle)).getText().toString();
            String description = ((EditText) findViewById(R.id.editTextTaskDescription)).getText().toString();
            String dateCreated=  com.amazonaws.util.DateUtils.formatISO8601Date(new Date());

            Task newTask = Task.builder()
                    .name(title)
                    .description(description)
                    .dateCreated(new Temporal.DateTime(new Date(), 0))
                    .taskState((TaskStateEnums) taskState.getSelectedItem()).build();

            Amplify.API.mutate(
                    ModelMutation.create(newTask),
                    successResponse -> Log.i(TAG, "AddTaskActivity.onCreate(): made a new task successfully"),
                    failureResponse -> Log.e(TAG, "AddTaskActivity.onCreate(): task adding failed" + failureResponse)
            );
        });




    }
}