package com.haya.taskmaster.models;

import androidx.annotation.NonNull;

public enum TaskStateEnums {
    COMPLETE("Complete"),
    INPROGRESS("In Progress"),
    ASSIGNED("Assigned"),
    NEW("New");

    private final String taskStateText;

    TaskStateEnums(String taskStateText) {
        this.taskStateText = taskStateText;
    }

    public String getTaskText() {
        return taskStateText;
    }

    public static  TaskStateEnums fromString(String taskEntered){
        if(taskEntered == null) return null;
        for (TaskStateEnums task : TaskStateEnums.values()){
            if(task.taskStateText.equals(taskEntered))
                return task;
        }
          return null;
    }

    @NonNull
    @Override
    public String toString(){

        if(taskStateText == null) return "";

        return taskStateText;
    }

}

