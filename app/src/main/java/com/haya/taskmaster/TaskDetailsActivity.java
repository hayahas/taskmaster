package com.haya.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

        if(callingIntent != null){
            taskTitle = callingIntent.getStringExtra(MainActivity.TASK_TITLE_TAG);
        }

        TextView taskSelected = (TextView) findViewById(R.id.taskTitleInput);

        if (taskTitle != null){
            taskSelected.setText(taskTitle);
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