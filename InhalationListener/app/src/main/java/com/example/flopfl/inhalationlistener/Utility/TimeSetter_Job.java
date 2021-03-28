package com.example.flopfl.inhalationlistener.Utility;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.flopfl.inhalationlistener.R;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
//Job der einen Notifiction Job startet
public class TimeSetter_Job extends JobService {

    private AsyncTask task;
    @Override
    public boolean onStartJob(final JobParameters job) {
        task = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                String name=job.getExtras().getString(getString(R.string.Job_Notification_key));
                String Tag=job.getExtras().getString(getString(R.string.Job_Notifi_Tag_Key));
                Context context = TimeSetter_Job.this;
                Notification_Job_Dispatcher.scheduleNotification(context,name,Tag);
                Log.d("notifies","Timesetterjob inside");
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {

                jobFinished(job, false);
            }
        };

        task.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (task != null) task.cancel(true);
        return true;
    }

}
