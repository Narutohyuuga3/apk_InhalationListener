package com.example.flopfl.inhalationlistener.Data_Inhalation;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import com.example.flopfl.inhalationlistener.Data.AppDatabase;
import com.example.flopfl.inhalationlistener.Data.MedikamentEntry;
import com.example.flopfl.inhalationlistener.Data_Inhalation.TaskDao2;

//Database für Inhalations-Protokoll
@Database(entities = {InhalationEntry.class}, version = 3, exportSchema = false)
public abstract class AppDatabase2 extends RoomDatabase {

    private static final String LOG_TAG = com.example.flopfl.inhalationlistener.Data_Inhalation.AppDatabase2.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "Inhalationen";
    private static com.example.flopfl.inhalationlistener.Data_Inhalation.AppDatabase2 sInstance;

    public static com.example.flopfl.inhalationlistener.Data_Inhalation.AppDatabase2 getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        com.example.flopfl.inhalationlistener.Data_Inhalation.AppDatabase2.class, com.example.flopfl.inhalationlistener.Data_Inhalation.AppDatabase2.DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract TaskDao2 taskDao2();

}