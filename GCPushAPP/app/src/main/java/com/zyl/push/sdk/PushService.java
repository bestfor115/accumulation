/**
 * probject:cim-android-sdk
 * @version 2.1.0
 * 
 * @author 3979434@qq.com
 */
package com.zyl.push.sdk;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.zyl.push.sdk.constant.Constant;
import com.zyl.push.sdk.model.GCMessage;

/**
 * 与服务端连接服务
 * 
 */
public class PushService extends Service {
    final static String TAG = PushService.class.getSimpleName();
    protected final static int DEF_CIM_PORT = 28888;
    ConnectorManager mConnectorManager;
    WakeLock mWakeLock;

    @Override
    public void onCreate() {
        mConnectorManager = ConnectorManager.getManager(this.getApplicationContext());
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, PushService.class.getName());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            return START_STICKY;
        }

        String action = intent.getAction();
        if(Constant.DEBUG){
            Log.d(Constant.TAG,  String.format("service receiver a action %s", action));
        }
        if (PushManager.ACTION_CREATE_CIM_CONNECTION.equals(action)) {
            String host = CacheToolkit.getInstance(this).getString(
                    CacheToolkit.KEY_CIM_SERVIER_HOST);
            int port = CacheToolkit.getInstance(this).getInt(CacheToolkit.KEY_CIM_SERVIER_PORT);
            mConnectorManager.connect(host, port);
        }

        if (PushManager.ACTION_SEND_REQUEST_BODY.equals(action)) {
            mConnectorManager.send((GCMessage) intent.getSerializableExtra(PushManager.KEY_SEND_BODY));
        }

        if (PushManager.ACTION_CLOSE_CIM_CONNECTION.equals(action)) {
            mConnectorManager.closeSession();
        }

        if (PushManager.ACTION_DESTORY.equals(action)) {
            mConnectorManager.destroy();
            this.stopSelf();
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        if (PushManager.ACTION_ACTIVATE_PUSH_SERVICE.equals(action)) {
            if (!mConnectorManager.isConnected()) {
                boolean isManualStop = CacheToolkit.getInstance(this).getBoolean(
                        CacheToolkit.KEY_MANUAL_STOP);
                Log.d(TAG, "CIM.isConnected() == false, isManualStop == " + isManualStop);
                PushManager.connect(this);
            } else {
                Log.d(TAG, "CIM.isConnected() == true");
            }
        }

        try {
            if (!mWakeLock.isHeld()) {
                this.mWakeLock.acquire();
            }
        } catch (Exception e) {
        }
        return START_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        if (mWakeLock.isHeld()) {
            this.mWakeLock.release();
            mWakeLock = null;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
