package com.example.flopfl.inhalationlistener.Utility;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.flopfl.inhalationlistener.MainActivity;
import com.example.flopfl.inhalationlistener.R;

//erstellt Benachrichtigung
public class Notifier {

    private static final int NOTIFICATION_ID = 1138;

    private static final int PENDING_INTENT_ID = 3417;

    private static final String NOTIFICATION_CHANNEL_ID = "reminder_notification_channel";

//erstellt Benachrichtigungschannel und Benachrichtigung. Startet Benachrichtigung, diese kann um bis zu Minute verspätet sein wegen dem Timewindow des Jobs
    public static void startNotification(Context context,String Name){

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        CharSequence name = context.getString(R.string.Notification_Channel_Name);
        String description = context.getString(R.string.Notification_Channel_Desc);
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel channel = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_pic)
                .setContentTitle(Name)
                .setContentText("Das Medikament "+Name+" einnehmen")
                .setContentIntent(getPendIntent(context))
                .setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }
//der PendingIntent öffnet beim betätigen der Benachrichtigung die Mainactivity Klasse.
    private static PendingIntent getPendIntent(Context context){
        Intent intent= new Intent(context,MainActivity.class);
        intent.putExtra(context.getString(R.string.Medikamente_Extra_Key),context.getString(R.string.Medikamente_Extra_txt));
        return PendingIntent.getActivity(
                context,
                PENDING_INTENT_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

    }


}
