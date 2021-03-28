package com.example.flopfl.inhalationlistener;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flopfl.inhalationlistener.Data.AppDatabase;
import com.example.flopfl.inhalationlistener.Data.MedikamentEntry;
import com.example.flopfl.inhalationlistener.Utility.Notification_Job_Dispatcher;

import java.sql.Time;
import java.util.Calendar;
import java.util.Set;
//Activity zum Hinzufügen von Medikamente
public class AddMedikament extends AppCompatActivity {
    public static final String EXTRA_TASK_ID = "extraTaskId";
    // Extra for the task ID to be received after rotation
    public static final String INSTANCE_TASK_ID = "instanceTaskId";
    private static final int DEFAULT_TASK_ID = -1;
    EditText mDescrption_Edit;
    CheckBox mMonday;
    CheckBox mTuesday;
    CheckBox mWednesday;
    CheckBox mThursday;
    CheckBox mFriday;
    CheckBox mSaturday;
    CheckBox mSunday;
    CheckBox mNotification;
    CheckBox mMorning;
    CheckBox mEvening;
    NumberPicker Timer1_h;
    NumberPicker Timer1_m;
    NumberPicker Timer2_h;
    NumberPicker Timer2_m;
    NumberPicker mDevice;
    AppDatabase mdb;
    private int mEntryId = DEFAULT_TASK_ID;
    private int Tag;
    private int changingTag;
//Wird "hinzufügen" betätigt wird ein standart UI gestartet
//Wird ein Eintrag angetippt wird das UI entsprechend mit dem Eintrag befüllt
//Job Tags werden gemerkt bzw. erstellt
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medikament);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.contains(MainActivity.Job_Tag_Key)){
            Tag=prefs.getInt(MainActivity.Job_Tag_Key,0);
        }
        initViews();
        mdb=AppDatabase.getInstance(getApplicationContext());
        Intent intent =getIntent();
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mEntryId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            if(mEntryId==DEFAULT_TASK_ID) {
                mEntryId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);
                AddViewModelFactory fac= new AddViewModelFactory(mdb,mEntryId);
                final CustomViewModel model= ViewModelProviders.of(this,fac).get(CustomViewModel.class);
                model.getEntry().observe(this, new Observer<MedikamentEntry>() {
                    @Override
                    public void onChanged(@Nullable MedikamentEntry entry) {
                        model.getEntry().removeObserver(this);
                        populateUI(entry);
                    }
                });
            }

        }
    }
//befüllt mit dem gewählten Eintrag das UI
    public void populateUI(MedikamentEntry entry){
        if (entry == null) {
            return;
        }

        mDescrption_Edit.setText(entry.getDescription());
        mMonday.setChecked(entry.isMonday());
        mTuesday.setChecked(entry.isTuesday());
        mWednesday.setChecked(entry.isWednesday());
        mThursday.setChecked(entry.isThursday());
        mFriday.setChecked(entry.isFriday());
        mSaturday.setChecked(entry.isSaturday());
        mSunday.setChecked(entry.isSunday());
        mMorning.setChecked(entry.isMorning());
        mEvening.setChecked(entry.isEvening());
        mNotification.setChecked(entry.isNotification());
        Timer1_h.setValue(Integer.valueOf(extractHour(entry.getTime1())));
        Timer1_m.setValue(Integer.valueOf(extractMin(entry.getTime1())));
        Timer2_h.setValue(Integer.valueOf(extractHour(entry.getTime2())));
        Timer2_m.setValue(Integer.valueOf(extractMin(entry.getTime2())));
        changingTag=entry.getTag();
        int i=0;
        for(String k:mDevice.getDisplayedValues()){
            if(k.equals(entry.getDeviceName())){
                mDevice.setValue(i);
                break;
            }
            i++;
        }



    }
    //standart UI
    public void initViews(){
        mDescrption_Edit=(EditText) findViewById(R.id.Name_EditText);
        mMonday=(CheckBox) findViewById(R.id.checkbox_Mo);
        mTuesday=(CheckBox) findViewById(R.id.checkbox_Di);
        mWednesday=(CheckBox) findViewById(R.id.checkbox_Mi);
        mThursday=(CheckBox) findViewById(R.id.checkbox_Do);
        mFriday=(CheckBox) findViewById(R.id.checkbox_Fr);
        mSaturday=(CheckBox) findViewById(R.id.checkbox_Sa);
        mSunday=(CheckBox) findViewById(R.id.checkbox_So);
        mMorning=(CheckBox)findViewById(R.id.notifyMorning);
        mEvening=(CheckBox)findViewById(R.id.notifyEvening);
        mNotification=(CheckBox)findViewById(R.id.checkbox_Notification);
        Timer1_h=(NumberPicker)findViewById(R.id.Timer1_hours_NP);
        Timer1_m=(NumberPicker)findViewById(R.id.Timer1_min_NP);
        Timer2_h=(NumberPicker)findViewById(R.id.Timer2_hours_NP);
        Timer2_m=(NumberPicker)findViewById(R.id.Timer2_min_NP);
        Timer1_h.setMaxValue(23);
        Timer1_h.setMinValue(0);
        Timer1_h.setValue(8);
        Timer1_m.setMaxValue(60);
        Timer1_m.setMinValue(0);
        Timer1_m.setValue(0);
        Timer2_h.setMaxValue(23);
        Timer2_h.setMinValue(0);
        Timer2_h.setValue(18);
        Timer2_m.setMaxValue(60);
        Timer2_m.setMinValue(0);
        Timer2_m.setValue(0);
        Timer1_h.setWrapSelectorWheel(false);
        Timer1_m.setWrapSelectorWheel(false);
        Timer2_m.setWrapSelectorWheel(false);
        Timer2_h.setWrapSelectorWheel(false);
        mDevice=(NumberPicker)findViewById(R.id.Device_picker);
        mDevice.setMinValue(0);
        String[] values= {"HIFEER","Green", "Blue", "Yellow", "Magenta"};

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.contains(MainActivity.DEVICELIST_IDENTIFIER)){
            if(!(preferences.getStringSet(MainActivity.DEVICELIST_IDENTIFIER,MainActivity.DEVICELIST_DEFAULT_IDENTIFIER).isEmpty())){
            Set<String> DeviceNames=preferences.getStringSet(MainActivity.DEVICELIST_IDENTIFIER,MainActivity.DEVICELIST_DEFAULT_IDENTIFIER);
            mDevice.setMaxValue(DeviceNames.size()-1);
            String[] names = DeviceNames.toArray(new String[DeviceNames.size()]);
            mDevice.setDisplayedValues(names);
            }
            else{ ;
                mDevice.setMaxValue(values.length-1);
                mDevice.setDisplayedValues(values);
            }
        }else { ;
            mDevice.setMaxValue(values.length-1);
            mDevice.setDisplayedValues(values);
        }

   }

    //Speichert den erstellten Eintrag
    //erstellt die zugehörigen Jobs für Benachrichtigungen falls selektiert
    public void Save_On_Click(View view){
        String description=mDescrption_Edit.getText().toString();
        boolean M=mMonday.isChecked();
        boolean Tu=mTuesday.isChecked();
        boolean W=mWednesday.isChecked();
        boolean Th=mThursday.isChecked();
        boolean F=mFriday.isChecked();
        boolean Sa=mSaturday.isChecked();
        boolean So=mSunday.isChecked();
        boolean No=mNotification.isChecked();
        boolean morning=mMorning.isChecked();
        boolean evening=mEvening.isChecked();
        String T1h=""+Timer1_h.getValue();
        String T1m=""+Timer1_m.getValue();
        String T2h=""+Timer2_h.getValue();
        String T2m=""+Timer2_m.getValue();
        String Device=""+mDevice.getDisplayedValues()[mDevice.getValue()];
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
        final MedikamentEntry entry;
        int timenow=hours*3600+minutes*60;
        int timeset1;
        int timeset2;
        int time1=Integer.parseInt(T1h)*3600+Integer.parseInt(T1m)*60;
        int time2=Integer.parseInt(T2h)*3600+Integer.parseInt(T2m)*60;
        timeset1=time1-timenow;
        timeset2=time2-timenow;
        Calendar calendar = Calendar.getInstance();
        int day = getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
        if(mEntryId==DEFAULT_TASK_ID){
            Tag++;
            SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor edit =prefs.edit();
            edit.putInt(MainActivity.Job_Tag_Key,Tag);
            edit.apply();
            entry=new MedikamentEntry(description,M,Tu,W,Th,F,Sa,So,No,makeTime(T1h,T1m),makeTime(T2h,T2m),Time,"--:--","--:--",Device,Tag,morning,evening);
            if(No){
                int setDay;
                if(M){
                    setDay=1;
                    dispatching(morning,evening,setDay,day,timeset1,timeset2,description,""+Tag);
                }
                if(Tu){
                    setDay=2;
                    dispatching(morning,evening,setDay,day,timeset1,timeset2,description,""+Tag);
                }
                if(W){
                    setDay=3;
                    dispatching(morning,evening,setDay,day,timeset1,timeset2,description,""+Tag);
                }
                if(Th){
                    setDay=4;
                    dispatching(morning,evening,setDay,day,timeset1,timeset2,description,""+Tag);
                }
                if(F){
                    setDay=5;
                    dispatching(morning,evening,setDay,day,timeset1,timeset2,description,""+Tag);
                }
                if(Sa){
                    setDay=6;
                    dispatching(morning,evening,setDay,day,timeset1,timeset2,description,""+Tag);
                }
                if(So){
                    setDay=7;
                    dispatching(morning,evening,setDay,day,timeset1,timeset2,description,""+Tag);
                }

            }

        }
        else {
            entry = new MedikamentEntry(description, M, Tu, W, Th, F, Sa, So, No, makeTime(T1h, T1m), makeTime(T2h, T2m), Time, "--:--", "--:--", Device, changingTag, morning, evening);
            if (No) {
                int setDay;
                if (M) {
                    setDay = 1;
                    dispatching(morning, evening, setDay, day, timeset1, timeset2, description, "" + changingTag);
                }
                if (Tu) {
                    setDay = 2;
                    dispatching(morning, evening, setDay, day, timeset1, timeset2, description, "" + changingTag);
                }
                if (W) {
                    setDay = 3;
                    dispatching(morning, evening, setDay, day, timeset1, timeset2, description, "" + changingTag);
                }
                if (Th) {
                    setDay = 4;
                    dispatching(morning, evening, setDay, day, timeset1, timeset2, description, "" + changingTag);
                }
                if (F) {
                    setDay = 5;
                    dispatching(morning, evening, setDay, day, timeset1, timeset2, description, "" + changingTag);
                }
                if (Sa) {
                    setDay = 6;
                    dispatching(morning, evening, setDay, day, timeset1, timeset2, description, "" + changingTag);
                }
                if (So) {
                    setDay = 7;
                    dispatching(morning, evening, setDay, day, timeset1, timeset2, description, "" + changingTag);
                }

            }
        }
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(mEntryId==DEFAULT_TASK_ID){
                    mdb.taskDao().insertTask(entry);
                }
                else{
                    entry.setId(mEntryId);
                    mdb.taskDao().updateTask(entry);
                }
                finish();
            }
        });

    }

//Zeitrechnungen für die benötigte Zeit bis Termin

    public void dispatching(boolean morning,boolean evening,int setDay,int day,int timeset1,int timeset2,String description,String tag){
        if(morning){
            int timer1=DispatchJobsTime(setDay,day)+timeset1;
            if(timer1<0){
                timer1=24*7*3600-timer1;
            }

            Notification_Job_Dispatcher.scheduleTimeSetter(this,timer1,description,"first"+setDay+tag);
        }
        if(evening){
            int timer2=DispatchJobsTime(setDay,day)+timeset2;
            if(timer2<0){
                timer2=24*7*3600-timer2;
            }
            Notification_Job_Dispatcher.scheduleTimeSetter(this,timer2,description,"second"+setDay+tag);
        }
    }

    public int DispatchJobsTime(int Setday,int day){
        int DayDiff=Setday-day;
        int Daytimebonus=0;
        if(DayDiff>0){
            Daytimebonus=24*3600*DayDiff;
        }
        if(DayDiff<0){
            Daytimebonus=(7+DayDiff)*24*3600;
        }
        return Daytimebonus;
    }

    public int getDayOfWeek(int day) {
        switch (day) {
            case Calendar.SUNDAY:
                return 7;
            case Calendar.MONDAY:
                return 1;
            case Calendar.TUESDAY:
                return 2;
            case Calendar.WEDNESDAY:
                return 3;
            case Calendar.THURSDAY:
                return 4;
            case Calendar.FRIDAY:
                return 5;
            case Calendar.SATURDAY:
                return 6;
            default: return 0;
        }
    }

    public String makeTime(String h, String m){
       if(h.length()==1){
            h="0"+h;
        }
        if(m.length()==1){
            m="0"+m;
        }
        String time= h+":"+m;
        return time;
    }
    public String extractHour(String s){
        String[] arg=s.split(":");
        return arg[0];
    }
    public String extractMin(String s){
        String[] arg=s.split(":");
        return arg[1];
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_TASK_ID, mEntryId);
        super.onSaveInstanceState(outState);
    }
}
