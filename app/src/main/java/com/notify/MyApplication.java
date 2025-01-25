package com.notify;

import com.google.firebase.FirebaseApp;
import android.app.Application;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ProcessLifecycleOwner;

public class MyApplication extends Application {
    private AppLifecycleObserver appLifecycleObserver;

    @Override
    public void onCreate() {
        super.onCreate();



        FirebaseApp.initializeApp(this);
        appLifecycleObserver = new AppLifecycleObserver();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);
    }

    public Boolean IsAppForeground () {
        return appLifecycleObserver.IsForeground();
    }


}