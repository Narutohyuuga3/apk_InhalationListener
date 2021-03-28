package com.example.flopfl.inhalationlistener.Data_Inhalation;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;
//Schnittstelle Inhalations-Database
@Dao
public interface TaskDao2 {

    @Query("SELECT * FROM Inhalationen")
    LiveData<List<InhalationEntry>> loadAllTasks();

    @Insert
    void insertTask(InhalationEntry taskEntry);


    @Delete
    void deleteTask(InhalationEntry taskEntry);

}
