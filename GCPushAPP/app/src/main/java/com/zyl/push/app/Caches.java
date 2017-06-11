package com.zyl.push.app;

import java.io.File;
import java.io.IOException;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.zyl.push.app.util.Utils;

public class Caches implements GCConstant{
    private static final String PREFERENCE_NAME = "auth-inf";

    public static final String TAG = "WaSu-Authen";
    private static SharedPreferences sp;
    private static File sdcardFile;

    public static File dataFolder;

    public static String mUserId;
    public static String mDeviceId;

    static {
        dataFolder = new File("/sdcard/dvb_dbase", "wasu_authenticator");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
            dataFolder.setReadable(true, false);
            dataFolder.setWritable(true, false);
            dataFolder.setExecutable(true, false);
        }
        Log.d(TAG, dataFolder.getAbsolutePath() + " can read=" + dataFolder.canRead());
        sdcardFile = new File(dataFolder, "wasu_authenticator_pref");
        try {
            if (!sdcardFile.exists())
                sdcardFile.createNewFile();
            sdcardFile.setReadable(true, false);
            sdcardFile.setWritable(true, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, sdcardFile.getAbsolutePath() + " can read=" + sdcardFile.canRead());
    }

    public static synchronized void loadData(Context ctx) {

        sp = ctx.getSharedPreferences(PREFERENCE_NAME, 0);
        if (!sp.contains("KEY_DEVICE_ID")) {
            Log.d(TAG, "No local user data, try to restore from: " + sdcardFile.getAbsolutePath());
            Utils.loadSharedPreferencesFromFile(sp, sdcardFile);
        }
        mDeviceId = sp.getString("KEY_DEVICE_ID", "");
        mUserId = sp.getString("KEY_USER_ID", "");
    }

    public static void saveData() {
        if (sp == null)
            return;
        sp.edit().putString("KEY_USER_ID", mUserId).putString("KEY_DEVICE_ID", mDeviceId).commit();

        Utils.saveSharedPreferencesToFile(sp, sdcardFile);
        sdcardFile.setReadable(true, false);
        sdcardFile.setWritable(true, false);
    }

    public static boolean hasUserData() {
        return !TextUtils.isEmpty(mDeviceId) && !TextUtils.isEmpty(mUserId);
    }
}
