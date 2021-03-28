package com.example.flopfl.inhalationlistener.Utility;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.flopfl.inhalationlistener.R;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
//JobService für start der Notification
public class Notification_Job extends JobService {

    @Override
    public boolean onStartJob(JobParameters job) {
        Log.d("notifies","NotificationJob inside");
        String name=job.getExtras().getString(getString(R.string.Job_Notification_key));
        Notifier.startNotification(this,name);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
