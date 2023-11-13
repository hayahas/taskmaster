package com.haya.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class TaskDetailsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_details);

       Button backToHome= (Button) findViewById(R.id.backToHomeFromDetailsBtn);

       sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

        Intent callingIntent = getIntent();
        String taskTitle = null;
        String taskBody = null;
        String taskState = null;
        String taskDate = null;
//
//        DateFormat dateCreatedIso8061InputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
//        dateCreatedIso8061InputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//        DateFormat dateCreatedOutputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        dateCreatedOutputFormat.setTimeZone(TimeZone.getDefault());
//        String dateCreatedString = "";
//
//        try {
//            {
//                Date dateCreatedJavaDate = dateCreatedIso8061InputFormat.parse(product.getDateCreated().format());
//                if (dateCreatedJavaDate != null){
//                    dateCreatedString = dateCreatedOutputFormat.format(dateCreatedJavaDate);
//                }
//            }
//        }catch (ParseException e){
//            throw new RuntimeException(e);
//        }
        if(callingIntent != null){
            taskTitle = callingIntent.getStringExtra(MainActivity.TASK_TITLE_TAG);
            taskBody = callingIntent.getStringExtra(MainActivity.TASK_BODY_TAG);
            taskState = callingIntent.getStringExtra(MainActivity.TASK_STATE_TAG);
            taskDate = callingIntent.getStringExtra(MainActivity.TASK_DATE_CREATED);
        }

        TextView taskSelected = (TextView) findViewById(R.id.taskTitleInput);
        TextView taskSelectedBody = (TextView) findViewById(R.id.taskBody);
        TextView taskSelectedState = (TextView) findViewById(R.id.taskState);
        TextView taskDateCreated = (TextView) findViewById(R.id.taskDate);


        if (taskTitle != null){
            taskSelected.setText(taskTitle);
            taskSelectedBody.setText(taskBody);
            taskSelectedState.setText(taskState);
            taskDateCreated.setText(taskDate);
        }else {
            taskSelected.setText("Task not specified");
        }

        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backToHome=new Intent(TaskDetailsActivity.this,MainActivity.class);
                startActivity(backToHome);
            }
        });


    }

}