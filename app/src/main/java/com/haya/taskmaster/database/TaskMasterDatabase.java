package com.haya.taskmaster.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.haya.taskmaster.dao.TaskDAO;
import com.haya.taskmaster.models.Task;

@Database(entities = {Task.class}, version = 2)
@TypeConverters(Converters.class)
public abstract class TaskMasterDatabase extends RoomDatabase {
    public abstract TaskDAO taskDAO();
}
