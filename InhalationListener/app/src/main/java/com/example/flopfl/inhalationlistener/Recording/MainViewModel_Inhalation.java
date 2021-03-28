package com.example.flopfl.inhalationlistener.Recording;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.flopfl.inhalationlistener.Data_Inhalation.AppDatabase2;
import com.example.flopfl.inhalationlistener.Data_Inhalation.InhalationEntry;

import java.util.List;
//ViewModel Inhalations-Protokoll cached die Datenbankeinträge
public class MainViewModel_Inhalation extends AndroidViewModel {

    private LiveData<List<InhalationEntry>> entries;

    public MainViewModel_Inhalation(@NonNull Application application) {
        super(application);
        AppDatabase2 dbs= AppDatabase2.getInstance(this.getApplication());
        entries =dbs.taskDao2().loadAllTasks();
    }

    public LiveData<List<InhalationEntry>> getEntries() {
        return entries;
    }
}
