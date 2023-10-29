package com.haya.taskmaster.database;

import androidx.room.TypeConverter;

import java.util.Date;

public class Converters {

    @TypeConverter
    public Date toDate(Long date){
    return date == null?null : new Date(date);
    }

    @TypeConverter
    public Long fromDate(Date date){
    return date == null?null : date.getTime();
    }
}
