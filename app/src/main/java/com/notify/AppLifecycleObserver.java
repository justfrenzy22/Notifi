package com.notify;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class AppLifecycleObserver implements LifecycleObserver {

    public static final String TAG = AppLifecycleObserver.class.getName();
    private Boolean isForeground = false;


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onEnterForeground() {
        isForeground = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        isForeground = false;
    }

    public Boolean IsForeground() {
        return this.isForeground;
    }
}
