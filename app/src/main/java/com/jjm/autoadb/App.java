package com.jjm.autoadb;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ContentResolver;
import android.provider.Settings;
import android.util.Log;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressLint({"PrivateApi", "DiscouragedPrivateApi"})
    public void startStopAdbd(boolean start) {
        try {
            Class.forName("android.os.SystemProperties")
                    .getDeclaredMethod("set", String.class, String.class)
                    .invoke(null, start ? "ctl.start" : "ctl.stop",
                            "adbd");
        } catch (Exception e) {
            Log.d("Reflect", "SystemProperties.set(\"ctl.start/ctl.stop\", \"adbd\") failed!", e);
        }
    }

    public void startAdbd() {
        ContentResolver contentResolver = getContentResolver();
        if (Settings.Global.getInt(contentResolver, Settings.Global.ADB_ENABLED, 0) > 0)
            startStopAdbd(true);
    }
}
