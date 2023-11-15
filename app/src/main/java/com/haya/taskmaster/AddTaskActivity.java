package com.haya.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.temporal.Temporal;

import com.amplifyframework.datastore.generated.model.TaskStateEnums;
import com.google.android.material.snackbar.Snackbar;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AddTaskActivity extends AppCompatActivity {


    public static final String TAG = "AddTaskActivity";
    Button addTask;
    Spinner taskStateSpinner=null;
    Spinner teamsSpinner= null;

    CompletableFuture<List<Team>> teamFuture = new CompletableFuture<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tasks);

        setUpSpinners();
        setUpSaveBtn();

    }

    public void setUpSpinners(){

        taskStateSpinner = (Spinner) findViewById(R.id.taskStateSpinner);
        teamsSpinner = (Spinner) findViewById(R.id.teamsSpinner);
        taskStateSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                TaskStateEnums.values()
        ));


        Amplify.API.query(
                ModelQuery.list(Team.class),
                success ->{
                    Log.i(TAG,"AddTaskActivity() : Team Added Successfully");
                    ArrayList<String> teamNames = new ArrayList<>();
                    ArrayList<Team> teams = new ArrayList<>();
                      for(Team team : success.getData()){
                          teams.add(team);
                          teamNames.add(team.getTeamName());
                      }
                      teamFuture.complete(teams);

                      runOnUiThread(() -> {
                          teamsSpinner.setAdapter(new ArrayAdapter<>(this,(
                                  android.R.layout.simple_spinner_item),
                          teamNames
                          ));
                      });

                },
                failure -> {
                    Log.i(TAG,"AddTaskActivity : Failed to Add Team");
                    teamFuture.complete(null);
                }
        );
    }
    public void setUpSaveBtn(){

        addTask = (Button) findViewById(R.id.addNewTaskBtn);


        addTask.setOnClickListener(v -> {


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
            String selectedTeamString = teamsSpinner.getSelectedItem().toString();


            List<Team> teams=null;

            try {
                teams=teamFuture.get();
            }catch (InterruptedException ie){
                Log.e(TAG, " InterruptedException while getting contacts");
            }catch (ExecutionException ee){
                Log.e(TAG," ExecutionException while getting contacts");
            }

            Team selectedTeam = teams.stream().filter(c -> c.getTeamName().equals(selectedTeamString)).findAny().orElseThrow(RuntimeException::new);


            Task newTask = Task.builder()
                    .name(title)
                    .description(description)
                    .dateCreated(new Temporal.DateTime(new Date(), 0))
                    .taskState((TaskStateEnums) taskStateSpinner.getSelectedItem())
                    .taskTeam(selectedTeam).build();

            Amplify.API.mutate(
                    ModelMutation.create(newTask),
                    successResponse -> Log.i(TAG, "AddTaskActivity.onCreate(): made a new task successfully"),
                    failureResponse -> Log.e(TAG, "AddTaskActivity.onCreate(): task adding failed" + failureResponse)
            );

            Snackbar.make(findViewById(R.id.addTaskActivity),"Task Added",Snackbar.LENGTH_SHORT).show();
        });



    }



}