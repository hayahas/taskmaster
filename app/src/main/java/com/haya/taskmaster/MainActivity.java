package com.haya.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addTaskButton = (Button) findViewById(R.id.addTaskbtn);
        Button allTasksButton = (Button) findViewById(R.id.allTasksbtn);

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
    }
}