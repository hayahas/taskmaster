package com.haya.taskmaster.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.haya.taskmaster.EditTaskActivity;
import com.haya.taskmaster.MainActivity;
import com.haya.taskmaster.R;
import com.haya.taskmaster.TaskDetailsActivity;

import com.amplifyframework.datastore.generated.model.Task;


import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Locale;

public class TasksRecyclerViewAdapter extends RecyclerView.Adapter<TasksRecyclerViewAdapter.taskViewHolder> {

    List<Task> tasks ;
    Context callingView;




    public TasksRecyclerViewAdapter(List<Task> tasks,Context callingView) {
        this.tasks = tasks;
        this.callingView=callingView;
    }

    @NonNull
    @Override
    public taskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View tasksFragment = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_recycler_fragment,parent,false);
        return new taskViewHolder(tasksFragment);
    }


    @Override
    public void onBindViewHolder(@NonNull taskViewHolder holder, int position) {
        TextView taskFragment = (TextView) holder.itemView.findViewById(R.id.taskFragmentTextView);
        Task task = tasks.get(position);
        String taskId= task.getId();
        String taskTitle= task.getName();
        String taskBody= task.getDescription();
        String taskState= String.valueOf(tasks.get(position).getTaskState());
        String taskTeam= String.valueOf(tasks.get(position).getTaskTeam());
        String taskDate = "";
        DateFormat dateCreatedIso8061InputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        dateCreatedIso8061InputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateFormat dateCreatedOutputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateCreatedOutputFormat.setTimeZone(TimeZone.getDefault());
        String location = "Cannot Access Location";

        try {
            {
                Date dateCreatedJavaDate = dateCreatedIso8061InputFormat.parse(task.getDateCreated().format());
                if (dateCreatedJavaDate != null){
                    taskDate = dateCreatedOutputFormat.format(dateCreatedJavaDate);
                }
            }
        }catch (ParseException e){
            throw new RuntimeException(e);
        }

        taskFragment.setText(taskTitle);

        View taskViewHolder = holder.itemView;
        String finalDateCreatedString = taskDate;

        taskViewHolder.setOnClickListener(v -> {
            Intent goToDetailsFromRecycler = new Intent(callingView, TaskDetailsActivity.class);
            goToDetailsFromRecycler.putExtra(MainActivity.TASK_TITLE_TAG,taskTitle);
            goToDetailsFromRecycler.putExtra(MainActivity.TASK_BODY_TAG,taskBody);
            goToDetailsFromRecycler.putExtra(MainActivity.TASK_STATE_TAG,taskState);
            goToDetailsFromRecycler.putExtra(MainActivity.TASK_ID_TAG,taskId);
            goToDetailsFromRecycler.putExtra(MainActivity.TASK_DATE_CREATED, finalDateCreatedString);
            goToDetailsFromRecycler.putExtra(MainActivity.LOCATION_TAG, location);
//            goToDetailsFromRecycler.putExtra(MainActivity.TASK_TEAM_TAG,taskTeam);
            callingView.startActivity(goToDetailsFromRecycler);
        });
    }

    @Override
    public int getItemCount() {

        return tasks.size();

   }

    public static class taskViewHolder extends RecyclerView.ViewHolder {

        public taskViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}
