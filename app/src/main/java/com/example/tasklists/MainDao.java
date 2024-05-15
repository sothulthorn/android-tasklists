package com.example.tasklists;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface MainDao {
    //insert query

    @Insert(onConflict = REPLACE)
    void insert(MainData mainData);

    //delete
    @Delete
    void delete(MainData mainData);

    //delete all
    @Delete
    void reset(List<MainData> mainData);

    //update
    @Query("UPDATE table_name SET text = :updateText, date = :updateDate, time = :updateTime WHERE ID = :sID")
    void update(int sID, String updateText, String updateDate, String updateTime);


    //get all data
    @Query("SELECT * FROM table_name")
    List<MainData> getAll();
}
