package com.haya.taskmaster.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.haya.taskmaster.MainActivity;
import com.haya.taskmaster.R;
import com.haya.taskmaster.TaskDetailsActivity;
import com.haya.taskmaster.models.Task;

import java.util.ArrayList;
import java.util.List;

public class TasksRecyclerViewAdapter extends RecyclerView.Adapter<TasksRecyclerViewAdapter.taskViewHolder> {

    List<Task> tasks = new ArrayList<>();
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
//        String taskTitle= tasks.get(position).getTitle();
//        String taskBody= tasks.get(position).getBody();
//        String taskState= String.valueOf(tasks.get(position).getTaskState());
//        taskFragment.setText(taskTitle);

        View taskViewHolder = holder.itemView;
        taskViewHolder.setOnClickListener(v -> {
            Intent goToDetailsFromRecycler = new Intent(callingView, TaskDetailsActivity.class);
//            goToDetailsFromRecycler.putExtra(MainActivity.TASK_TITLE_TAG,taskTitle);
//            goToDetailsFromRecycler.putExtra(MainActivity.TASK_BODY_TAG,taskBody);
//            goToDetailsFromRecycler.putExtra(MainActivity.TASK_STATE_TAG,taskState);
            callingView.startActivity(goToDetailsFromRecycler);
        });
    }

    @Override
    public int getItemCount() {

//        return tasks.size();

        return 4;
    }

    public static class taskViewHolder extends RecyclerView.ViewHolder {

        public taskViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}
