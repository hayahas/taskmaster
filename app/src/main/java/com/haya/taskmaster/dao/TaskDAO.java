package com.haya.taskmaster.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.haya.taskmaster.models.Task;

import java.util.List;

@Dao
public interface TaskDAO {

    @Query("select * from Task")
    public List<Task> findAll();

    @Insert
    public void insertToTask(Task task);

    @Delete
    public void deleteTask(Task task);

    @Query("select * from Task where id =:id")
    Task findById(Long id);
}
