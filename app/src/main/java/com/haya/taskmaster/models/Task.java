package com.haya.taskmaster.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.haya.taskmaster.database.Converters;

import java.util.Date;


@Entity
public class Task {

    @PrimaryKey(autoGenerate = true)
    public Long id;
    private  String title;
    private  String body;
    java.util.Date dateCreated;
    TaskStateEnums taskState;

//    public Task(String title, String body,String state) {
//        this.title = title;
//        this.body = body;
//        this.state=state;
//    }

    public Task(String title, String body, Date dateCreated, TaskStateEnums taskState) {
        this.title = title;
        this.body = body;
        this.dateCreated = dateCreated;
        this.taskState = taskState;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public TaskStateEnums getTaskState() {
        return taskState;
    }

    public void setTaskState(TaskStateEnums taskState) {
        this.taskState = taskState;
    }


}
