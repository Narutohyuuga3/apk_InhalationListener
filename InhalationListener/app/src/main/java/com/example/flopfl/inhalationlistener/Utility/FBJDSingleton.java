package com.example.flopfl.inhalationlistener.Utility;

import android.content.Context;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
//FirebaseJobDispatcher als singelton
public class FBJDSingleton {
    private static FirebaseJobDispatcher firebaseJobDispatcher;

    private FBJDSingleton() {}

    public static FirebaseJobDispatcher getInstance(Context context) {
        if (firebaseJobDispatcher == null) {
            firebaseJobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        }
        return firebaseJobDispatcher;
    }
}