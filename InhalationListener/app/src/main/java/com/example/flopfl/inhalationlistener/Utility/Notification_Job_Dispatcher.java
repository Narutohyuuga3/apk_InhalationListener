package com.example.flopfl.inhalationlistener.Utility;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.flopfl.inhalationlistener.Data.MedikamentEntry;
import com.example.flopfl.inhalationlistener.R;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
//Dispatcher für TimeSetterJob, NotifictionJob
//Canceln von Jobs
public class Notification_Job_Dispatcher {
//plant einen NotificationJob alle 7 Tage
    public static void scheduleNotification(@NonNull final Context context,@NonNull final String Name,@NonNull final String Tag) {
        int timer =7*3600*24;

        FirebaseJobDispatcher dispatcher =FBJDSingleton.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(context.getString(R.string.Job_Notification_key),Name);
        Log.d("notifies","scheduleNotificaiotn"+Tag);
        Log.d("notifies","sschedulenotifi context"+context);
        Notifier.startNotification(context,Name);
        Job notificationJob = dispatcher.newJobBuilder()
                .setService(Notification_Job.class)
                .setExtras(bundle)
                .setTag(Tag)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(timer-10,timer+10))
                .setReplaceCurrent(true)
                /* Once the Job is ready, call the builder's build method to return the Job */
                .build();

        dispatcher.mustSchedule(notificationJob);

    }

    //plant einen Setter Job für den eingetragenen Zeitpunkt um durch diesen einen Notification Job zu starten
    public static void scheduleTimeSetter(@NonNull final Context context,@NonNull final int Time,@NonNull final String Name,@NonNull final String Tag){
        FirebaseJobDispatcher dispatcher =FBJDSingleton.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(context.getString(R.string.Job_Notification_key),Name);
        bundle.putString(context.getString(R.string.Job_Notifi_Tag_Key),Tag);
        Log.d("notifies","scheduleTimesetter time:  "+Time+"  Tag:   "+Tag);
        Log.d("notifies","sschedulesetter context"+context);
        Job notificationJob = dispatcher.newJobBuilder()
                .setService(TimeSetter_Job.class)
                .setExtras(bundle)
                .setTag(Tag)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(false)
                .setTrigger(Trigger.executionWindow(Time-10,Time+10))
                .setReplaceCurrent(true)
                /* Once the Job is ready, call the builder's build method to return the Job */
                .build();

        dispatcher.mustSchedule(notificationJob);
    }
//Löscht alle mit dem Tag des Eintrags verbundenen Jobs
    public static void cancelJob(MedikamentEntry entry, Context context){
        FirebaseJobDispatcher dispatcher =FBJDSingleton.getInstance(context);
        if(entry.isNotification()){
            if(entry.isMonday()){
                if(entry.isMorning()){
                    dispatcher.cancel("first"+1+entry.getTag());
                }
                if(entry.isEvening()){
                    dispatcher.cancel("second"+1+entry.getTag());
                }
            }
            if(entry.isTuesday()){
                if(entry.isMorning()){
                    dispatcher.cancel("first"+2+entry.getTag());
                }
                if(entry.isEvening()){
                    dispatcher.cancel("second"+2+entry.getTag());
                }
            }
            if(entry.isWednesday()){
                if(entry.isMorning()){
                    dispatcher.cancel("first"+3+entry.getTag());
                }
                if(entry.isEvening()){
                    dispatcher.cancel("second"+3+entry.getTag());
                }
            }
            if(entry.isThursday()){
                if(entry.isMorning()){
                    dispatcher.cancel("first"+4+entry.getTag());
                }
                if(entry.isEvening()){
                    dispatcher.cancel("second"+4+entry.getTag());
                }
            }
            if(entry.isFriday()){
                if(entry.isMorning()){
                    dispatcher.cancel("first"+5+entry.getTag());
                }
                if(entry.isEvening()){
                    dispatcher.cancel("second"+5+entry.getTag());
                }
            }
            if(entry.isSaturday()){
                if(entry.isMorning()){
                    dispatcher.cancel("first"+6+entry.getTag());
                }
                if(entry.isEvening()){
                    dispatcher.cancel("second"+6+entry.getTag());
                }
            }
            if(entry.isSunday()){
                if(entry.isMorning()){
                    dispatcher.cancel("first"+7+entry.getTag());
                }
                if(entry.isEvening()){
                    dispatcher.cancel("second"+7+entry.getTag());
                }
            }
        }

    }
}
