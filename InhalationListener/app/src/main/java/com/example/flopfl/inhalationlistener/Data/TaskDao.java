package com.example.flopfl.inhalationlistener.Data;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;


//Schnittstelle Database
@Dao
public interface TaskDao {
    @Query("SELECT * FROM Medikamente")
    LiveData<List<MedikamentEntry>> loadAllTasks();

    @Insert
    void insertTask(MedikamentEntry taskEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(MedikamentEntry taskEntry);

    @Delete
    void deleteTask(MedikamentEntry taskEntry);

    @Query("SELECT * FROM Medikamente WHERE ID=:id")
    LiveData<MedikamentEntry> LoadTaksByID(int id);

    @Query("SELECT * FROM Medikamente WHERE deviceName=:DeviceName")
    List<MedikamentEntry> LoadTaksByName(String DeviceName);
}
