package com.haya.taskmaster;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStateEnums;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class EditTaskActivity extends AppCompatActivity {

    private final static String TAG = "EditTaskActivity";
    CompletableFuture<Task> taskCompletableFuture = null ;
    CompletableFuture<List<Team>> teamsCompletableFuture = null ;
     Task updatedTask ;
    ImageView backToHome;
    ImageView textToSpeech;
    private Spinner newTaskStateSpinner = null;
    private  Spinner newTaskTeamSpinner = null;
    private EditText nameEditText;
    private EditText descriptionEditText;

    private MediaPlayer mp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_task);



        taskCompletableFuture = new CompletableFuture<>();
        teamsCompletableFuture = new CompletableFuture<>();

        mp = new MediaPlayer();

        setupBackToHomeBtn();
        setupTextToSpeechBtn();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupUI();
        setUpDeleteBtn();
        setUpSaveBtn();
    }



    private void setupBackToHomeBtn(){
        backToHome= (ImageView) findViewById(R.id.homeLogoEditTask);
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backToHome=new Intent(EditTaskActivity.this,MainActivity.class);
                startActivity(backToHome);
            }
        });
    }
    private void setupUI() {

        Intent callingIntent = getIntent();
        String taskId = null;

        if (callingIntent != null) {
            taskId = callingIntent.getStringExtra(MainActivity.TASK_ID_TAG);
        }

        String finalTaskId = taskId;

        Amplify.API.query(
                ModelQuery.list(Task.class),
                success -> {
                    Log.i(TAG, "Read tasks Successfully");

                    for (Task databaseTask : success.getData()) {
                        if (databaseTask.getId().equals(finalTaskId)) {
                            taskCompletableFuture.complete(databaseTask);
                        }
                    }

                    runOnUiThread(() -> {

                    });
                },
                failure -> Log.i(TAG, "Couldn't read tasks successfully")
        );
        try {
           updatedTask = taskCompletableFuture.get();

        } catch (InterruptedException i){
            Log.e(TAG, "InterruptedException in reading tasks ");
            Thread.currentThread().interrupt();
        }catch(ExecutionException e) {
            Log.e(TAG, "ExecutionException in reading tasks ");
        }

        nameEditText = findViewById(R.id.editTextNewTaskTitle);
        nameEditText.setText(updatedTask.getName());

        descriptionEditText = findViewById(R.id.editTextNewTaskDescription);
        descriptionEditText.setText(updatedTask.getDescription());
        setUpSpinners();

    }


    public void setUpSpinners(){

        newTaskStateSpinner = (Spinner) findViewById(R.id.newTaskStateSpinner);

        newTaskTeamSpinner = (Spinner) findViewById(R.id.newTeamsSpinner);

        Amplify.API.query(
                ModelQuery.list(Team.class),
                success -> {
                    Log.i(TAG,"Teams Read Successfully");

                    ArrayList<String> teamNames = new ArrayList<>();
                    ArrayList<Team> teams = new ArrayList<>();

                    for(Team team : success.getData()){
                        teams.add(team);
                        teamNames.add(team.getTeamName());
                    }

                    teamsCompletableFuture.complete(teams);

                    runOnUiThread(() -> {
                        newTaskTeamSpinner.setAdapter(new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                teamNames)
                        );
                        newTaskTeamSpinner.setSelection(getSpinnerIndex(newTaskTeamSpinner,updatedTask.getTaskTeam().getTeamName()));
                    });

                },
                failure ->{

                    teamsCompletableFuture.complete(null);
                    Log.e(TAG,"Failed to read Teams");
                }
        );

        newTaskStateSpinner.setAdapter(new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        TaskStateEnums.values()));

        newTaskStateSpinner.setSelection(getSpinnerIndex(newTaskStateSpinner,updatedTask.getTaskState().toString()));

    }

    public int getSpinnerIndex(Spinner mySpinner, String stringToCheck){
        for(int i=0;i< mySpinner.getCount();i++){
            if(mySpinner.getItemAtPosition(i).toString().equalsIgnoreCase(stringToCheck)){
                return i;
            }
        }
        return 0;
    }

    public void setUpSaveBtn(){

        Button saveChangesBtn = (Button) findViewById(R.id.saveTaskUpdateBtn);

        saveChangesBtn.setOnClickListener(v ->{

            List<Team> teams = null;
            String getUpdatedTeam = newTaskTeamSpinner.getSelectedItem().toString();

            try{
                teams=teamsCompletableFuture.get();
            } catch (InterruptedException ie){
                Log.e(TAG, "InterruptedException while getting task");
                Thread.currentThread().interrupt();
            } catch (ExecutionException ee) {
                Log.e(TAG, "ExecutionException while getting task");
            }


            Team newTeamSelected = teams.stream().filter(c -> c.getTeamName().equals(getUpdatedTeam)).findAny().orElseThrow(RuntimeException::new);


            Task newTask = Task.builder()
                            .name(nameEditText.getText().toString())
                                    .id(updatedTask.getId())
                               .taskState(taskStateEnumsFromString(newTaskStateSpinner.getSelectedItem().toString()))
                               .taskTeam(newTeamSelected).description(descriptionEditText.getText().toString())
                            .dateCreated(updatedTask.getDateCreated()).build();

            Amplify.API.mutate(
                    ModelMutation.update(newTask),
                    success -> {
                        Log.i(TAG,"Task updated Successfully");
                        Snackbar.make(findViewById(R.id.editTaskActivity), "Task updated!", Snackbar.LENGTH_SHORT).show();

                    },
                    failure -> Log.i(TAG,"Failed to update task" + failure)
            );

        });
    }

    public static TaskStateEnums taskStateEnumsFromString(String selectedEnum){
        for(TaskStateEnums taskStateEnum : TaskStateEnums.values()){
            if(taskStateEnum.toString().equals(selectedEnum)){
                return taskStateEnum;
            }
        }
        return null;
    }

    public void setUpDeleteBtn(){

        Button deleteTaskBtn = (Button) findViewById(R.id.deleteTaskBtn);

        deleteTaskBtn.setOnClickListener(v -> Amplify.API.mutate(
                ModelMutation.delete(updatedTask),
                success -> {
                    Log.i(TAG,"Task Deleted Successfully");
                    Intent goToMain = new Intent(EditTaskActivity.this,MainActivity.class);
                    startActivity(goToMain);
                },
                failure -> Log.i(TAG,"Failed to Delete task" + failure)
        ));
    }

    private void setupTextToSpeechBtn(){

        textToSpeech = (ImageView) findViewById(R.id.textToSpeechLogoEditPage);

        textToSpeech.setOnClickListener(b -> {
            String taskTitle = ((EditText) findViewById(R.id.editTextNewTaskTitle)).getText().toString();

            Amplify.Predictions.convertTextToSpeech(
                    taskTitle,
                    success -> playAudio(success.getAudioData()),
                    failure -> Log.e(TAG, "Failed to convert Text to Speech",failure)
            );

        });
    }

    private void playAudio(InputStream data) {
        File mp3File = new File(getCacheDir(), "audio.mp3");

        try (OutputStream out = new FileOutputStream(mp3File)) {
            byte[] buffer = new byte[8 * 1_024];
            int bytesRead;
            while ((bytesRead = data.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            mp.reset();
            mp.setOnPreparedListener(MediaPlayer::start);
            mp.setDataSource(new FileInputStream(mp3File).getFD());
            mp.prepareAsync();
        } catch (IOException error) {
            Log.e("MyAmplifyApp", "Error writing audio file", error);
        }
    }

}
