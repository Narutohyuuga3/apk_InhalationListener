package com.example.flopfl.inhalationlistener.Utility;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.flopfl.inhalationlistener.AppExecutors;
import com.example.flopfl.inhalationlistener.Data.AppDatabase;
import com.example.flopfl.inhalationlistener.Data.MedikamentEntry;
import com.example.flopfl.inhalationlistener.MainActivity;

import java.sql.Time;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
//BroadcastReceiver für Bluetooth-Verbindung
public class BluetoothReceiver extends BroadcastReceiver {

    /*  Überwachung ob Bluetooth Verbindung gestartet oder geschlossen
     *  Merken der verbundenen Produkte
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        switch (action) {
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                Log.d("headset_con2", "ACTION_ACL_CONNECTED");
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Set<String> BT_Device_names =new HashSet<String>();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                if(preferences.contains(MainActivity.DEVICELIST_IDENTIFIER)){
                    BT_Device_names=preferences.getStringSet(MainActivity.DEVICELIST_IDENTIFIER,MainActivity.DEVICELIST_DEFAULT_IDENTIFIER);
                }
                if(BT_Device_names.contains(bluetoothDevice.getName())){
                    editDataBaseNameList(bluetoothDevice.getName(),context);

                }
                else{

                    SharedPreferences.Editor editor = preferences.edit();
                    BT_Device_names.add(bluetoothDevice.getName());
                    editor.putStringSet(MainActivity.DEVICELIST_IDENTIFIER,BT_Device_names);
                    editor.apply();

                }
                break;


            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                Log.d("headset_con2", "ACTION_ACL_DISCONNECTED");
                BluetoothDevice bluetoothDevice2 = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                editDataBaseNameList(bluetoothDevice2.getName(),context);
                break;
        }
    }
    //  Update der "zuletzt Bewegt" Zeit in der Datenbank
    public void editDataBaseNameList(final String name,Context context){
        final AppDatabase mdb=AppDatabase.getInstance(context);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<MedikamentEntry> entries =mdb.taskDao().LoadTaksByName(name);
                if(entries.isEmpty()){
                }
                String time =getTime();
                for(MedikamentEntry entry :entries){
                    String update1=entry.getUpdatedAt();
                    String update2=entry.getUpdatedAt2();
                    if(!(update1.equals(time))){
                        if(!(update2.equals("--:--"))){
                            entry.setUpdatedAt3(update2);
                        }
                        entry.setUpdatedAt2(update1);
                        entry.setUpdatedAt(time);
                        mdb.taskDao().updateTask(entry);
                    }

                }
            }
        });
    }
    public String getTime(){
        int hours = new Time(System.currentTimeMillis()).getHours();
        int minutes=new Time(System.currentTimeMillis()).getMinutes();
        String h=""+hours;
        String m=""+minutes;
        if(hours<10){
            h="0"+hours;
        }
        if(minutes<10){
            m="0"+minutes;
        }

        String Time=(h+":"+m);
        return Time;
    }
}
