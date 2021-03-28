package com.example.flopfl.inhalationlistener;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.flopfl.inhalationlistener.Data.AppDatabase;
import com.example.flopfl.inhalationlistener.Data.MedikamentEntry;
//ViewModel für einzelne Einträge
public class CustomViewModel extends ViewModel {
    LiveData<MedikamentEntry> entry;

    CustomViewModel(AppDatabase base,int id){
        entry=base.taskDao().LoadTaksByID(id);
    }

    public LiveData<MedikamentEntry> getEntry(){
        return entry;
    }
}
