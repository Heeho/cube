package ru.ltow.cube;

import android.app.Application;

public class App extends Application {
    public static PrefsManager prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = new PrefsManager(this);
    }
}