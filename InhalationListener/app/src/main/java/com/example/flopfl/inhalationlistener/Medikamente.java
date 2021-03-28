package com.example.flopfl.inhalationlistener;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.example.flopfl.inhalationlistener.Data.AppDatabase;
import com.example.flopfl.inhalationlistener.Data.MedikamentEntry;
import com.example.flopfl.inhalationlistener.Utility.Notification_Job_Dispatcher;

import java.util.List;
// Protokoll-Funktion
public class Medikamente extends AppCompatActivity implements TaskAdpater.ItemClickListener {

    private RecyclerView mRecyclerview;
    private TaskAdpater mAdapter;
    private AppDatabase mdb;

    //erstellung RecyclerView und zugehöriger Adapter
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medikamente);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerview=(RecyclerView)findViewById(R.id.recyclerViewTask);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));//übergibt layout in der RecView
        mAdapter=new TaskAdpater(this,this);//erzeugt den Taskadapter und übergibt seinen listener um nachher die einezelnen viewholder antippen zu können
        mRecyclerview.setAdapter(mAdapter);
        DividerItemDecoration deco=new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL);
        mRecyclerview.addItemDecoration(deco);

        //reagiert auf berührungen
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // bei swipen wird der Eintrag und die zugehörigen Jobs gelöscht
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                Notification_Job_Dispatcher.cancelJob(mAdapter.getEntries().get(viewHolder.getAdapterPosition()),getApplicationContext());
                AppExecutors.getInstance().diskIO().execute(new Runnable() { //singleThread executer der die erzeugten threads managen soll.
                    @Override
                    public void run() {
                        mdb.taskDao().deleteTask(mAdapter.getEntries().get(viewHolder.getAdapterPosition()));
                    }
                });
            }
        }).attachToRecyclerView(mRecyclerview);

        mdb=AppDatabase.getInstance(getApplicationContext());//Database singleton
        retrieve();
    }

    //Hinzufügen Knopf startet AddMedikament-Activity
    public void Add_On_Click(View view){
        Intent add =new Intent(this,AddMedikament.class);
        startActivity(add);
    }
    //wählen eines Eintrags öffnet diesen in der AddMedikament-Activity zum editieren
    @Override
    public void onItemClickListener(int itemID) {// geklicktes entry konfigurieren
        Intent intent =new Intent(this,AddMedikament.class);
        intent.putExtra(AddMedikament.EXTRA_TASK_ID,itemID);//übergabe itemID
        startActivity(intent);
    }

//beobachten der Einträge im ViewModel
    private void retrieve() {

                MainViewModel model= ViewModelProviders.of(this).get(MainViewModel.class);//erstellt einen proveider der viewmodels sichert solange die zugehörige Activity lebt.
                model.getEntries().observe(this, new Observer<List<MedikamentEntry>>() {// beobachtet die entries des Viewmodels: der Adapter bekommt die entryliste bei einer änderung.
                    @Override
                    public void onChanged(@Nullable List<MedikamentEntry> medikamentEntries) {
                        mAdapter.setTasks(medikamentEntries);
                    }
                });

    }
    @Override
    protected void onResume() {
        super.onResume();
    }
}
