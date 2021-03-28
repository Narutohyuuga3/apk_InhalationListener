package com.example.flopfl.inhalationlistener;


import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.flopfl.inhalationlistener.Data.AppDatabase;
//custom Viewmodelfactory
public class AddViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final AppDatabase dbs;
    private final int ID;
    AddViewModelFactory(AppDatabase base, int id){
        dbs=base;
        ID=id;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new CustomViewModel(dbs, ID);
    }
}
