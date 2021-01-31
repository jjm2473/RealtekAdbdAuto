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
    public void startAdbd() {
        ContentResolver contentResolver = getContentResolver();
        if (Settings.Global.getInt(contentResolver, Settings.Global.ADB_ENABLED, 0) > 0) {
            try {
                Class.forName("android.os.SystemProperties")
                        .getDeclaredMethod("set", String.class, String.class)
                        .invoke(null, "ctl.start", "adbd");
            } catch (Exception e) {
                Log.d("Reflect", "SystemProperties.set(\"ctl.start\", \"adbd\") failed!", e);
            }
        }
    }
}
