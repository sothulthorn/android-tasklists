package com.example.tasklists;

//add database entities
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = { MainData.class }, version = 1, exportSchema = false)

public abstract class RoomDB extends RoomDatabase {
    //create database instance
    private static RoomDB database;

    //define db name
    private static String DATABASE_NAME="database";

    public  synchronized static RoomDB getInstance(Context context){
        if(database == null){
            //when db is null
            //initialize db
            database= Room.databaseBuilder(context.getApplicationContext(),RoomDB.class,DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }

        //return db
        return database;
    }

    //create Dao

    public abstract MainDao mainDao();
}
