/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.jjm.autoadb;

import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;

import androidx.leanback.preference.LeanbackPreferenceFragment;
import androidx.leanback.preference.LeanbackSettingsFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceScreen;

public class SettingsFragment extends LeanbackSettingsFragment {

    private static final String ADBD_KEY = "enable_adbd";

    @Override
    public void onPreferenceStartInitialScreen() {
        startPreferenceFragment(buildPreferenceFragment(R.xml.prefs, null));
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragment preferenceFragment,
                                             Preference preference) {
        return false;
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragment preferenceFragment,
                                           PreferenceScreen preferenceScreen) {
        PreferenceFragment frag = buildPreferenceFragment(R.xml.prefs, preferenceScreen.getKey());
        startPreferenceFragment(frag);
        return true;
    }

    private PreferenceFragment buildPreferenceFragment(int preferenceResId, String root) {
        PreferenceFragment fragment = new PrefFragment();
        Bundle args = new Bundle();
        args.putInt("preferenceResource", preferenceResId);
        args.putString("root", root);
        fragment.setArguments(args);
        return fragment;
    }

    private static class AdbStatusDataStore extends PreferenceDataStore {
        private App app;
        private ContentResolver contentResolver;
        public AdbStatusDataStore(App app) {
            this.app = app;
            this.contentResolver = app.getContentResolver();
        }

        @Override
        public void putBoolean(String key, boolean value) {
           if (ADBD_KEY.equals(key)) {
               Settings.Global.putInt(contentResolver, Settings.Global.ADB_ENABLED, value ? 1 : 0);
               app.startStopAdbd(value);
           } else {
               throw new UnsupportedOperationException("Unknown key " + key);
           }
        }

        @Override
        public boolean getBoolean(String key, boolean defValue) {
            if (ADBD_KEY.equals(key)) {
                return Settings.Global.getInt(contentResolver, Settings.Global.ADB_ENABLED, 0) > 0;
            } else {
                throw new UnsupportedOperationException("Unknown key " + key);
            }
        }
    }

    public static class PrefFragment extends LeanbackPreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            String root = getArguments().getString("root", null);
            int prefResId = getArguments().getInt("preferenceResource");
            if (root == null) {
                AdbStatusDataStore adbStatusDataStore = new AdbStatusDataStore((App)getContext().getApplicationContext());
                getPreferenceManager().setPreferenceDataStore(adbStatusDataStore);
                addPreferencesFromResource(prefResId);
            } else {
                setPreferencesFromResource(prefResId, root);
            }
        }
    }
}
