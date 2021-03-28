package com.example.flopfl.inhalationlistener;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.flopfl.inhalationlistener.Data.AppDatabase;
import com.example.flopfl.inhalationlistener.Data.MedikamentEntry;

import java.util.List;
//Viewmodel Medikament-Protokoll cached die Datenbankeinträge
public class MainViewModel extends AndroidViewModel {

    private LiveData<List<MedikamentEntry>> entries;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase dbs= AppDatabase.getInstance(this.getApplication());
        entries =dbs.taskDao().loadAllTasks();
    }

    public LiveData<List<MedikamentEntry>> getEntries() {
        return entries;
    }
}
