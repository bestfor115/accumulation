/**
 * probject:cim-android-sdk
 * @version 2.1.0
 * 
 * @author 3979434@qq.com
 */
package com.zyl.push.sdk;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import com.zyl.push.sdk.constant.Constant;
import com.zyl.push.sdk.exception.SessionDisableException;
import com.zyl.push.sdk.model.GCMessage;

/**
 * 消息入口，所有消息都会经过这里
 */
abstract class EventBroadcastReceiver extends BroadcastReceiver implements EventListener {

    public Context mContext;

    @Override
    public void onReceive(Context ctx, Intent it) {

        mContext = ctx;
        if(Constant.DEBUG){
            Log.e(Constant.TAG, String.format("event braodacast receiver a action %s", it.getAction()));
        }
        /*
         * 操作事件广播，用于提高service存活率
         */
        if (it.getAction().equals(Intent.ACTION_USER_PRESENT)
                || it.getAction().equals(Intent.ACTION_POWER_CONNECTED)
                || it.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
            startPushService();
        }

        /*
         * 设备网络状态变化事件
         */
        if (it.getAction().equals(ConnectorManager.ACTION_NETWORK_CHANGED)) {
            ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            onDevicesNetworkChanged(info);
        }

        /*
         * cim断开服务器事件
         */
        if (it.getAction().equals(ConnectorManager.ACTION_CONNECTION_CLOSED)) {
            onInnerConnectionClosed();
        }

        /*
         * cim连接服务器失败事件
         */
        if (it.getAction().equals(ConnectorManager.ACTION_CONNECTION_FAILED)) {
            onInnerConnectionFailed((Exception) it.getSerializableExtra("exception"));
        }

        /*
         * cim连接服务器成功事件
         */
        if (it.getAction().equals(ConnectorManager.ACTION_CONNECTION_SUCCESSED)) {
            onInnerConnectionSuccessed();
        }

        /*
         * 收到推送消息事件
         */
        if (it.getAction().equals(ConnectorManager.ACTION_MESSAGE_RECEIVED)) {
            onInnerMessageReceived((GCMessage) it.getSerializableExtra("message"));
        }

        /*
         * 获取sendbody发送失败事件
         */
        if (it.getAction().equals(ConnectorManager.ACTION_SENT_FAILED)) {
            onSentFailed((Exception) it.getSerializableExtra("exception"),
                    (GCMessage) it.getSerializableExtra("message"));
        }

        /*
         * 获取sendbody发送成功事件
         */
        if (it.getAction().equals(ConnectorManager.ACTION_SENT_SUCCESSED)) {
            onSentSucceed((GCMessage) it.getSerializableExtra("message"));
        }

        /*
         * 获取cim数据传输异常事件
         */
        if (it.getAction().equals(ConnectorManager.ACTION_UNCAUGHT_EXCEPTION)) {
            onUncaughtException((Exception) it.getSerializableExtra("exception"));
        }

        /*
         * 重新连接，如果断开的话
         */
        if (it.getAction().equals(ConnectorManager.ACTION_CONNECTION_RECOVERY)) {
            PushManager.connect(mContext);
        }
    }

    private void startPushService() {
        Intent intent = new Intent(mContext, PushService.class);
        intent.setAction(PushManager.ACTION_ACTIVATE_PUSH_SERVICE);
        mContext.startService(intent);
    }

    private void onInnerConnectionClosed() {
        CacheToolkit.getInstance(mContext).putBoolean(CacheToolkit.KEY_CIM_CONNECTION_STATE, false);
        if (ConnectorManager.netWorkAvailable(mContext)) {
            PushManager.connect(mContext);
        }

        onConnectionClosed();
    }

    Handler connectionHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message message) {
            PushManager.connect(mContext);
        }
    };

    private void onInnerConnectionFailed(Exception e) {

        if (ConnectorManager.netWorkAvailable(mContext)) {
            connectionHandler.sendEmptyMessageDelayed(0, Constant.RECONN_INTERVAL_TIME);
        }

        onConnectionFailed(e);
    }

    private void onInnerConnectionSuccessed() {
        CacheToolkit.getInstance(mContext).putBoolean(CacheToolkit.KEY_CIM_CONNECTION_STATE, true);

        boolean autoBind = PushManager.autoBindAccount(mContext);
        onConnectionSuccessed(autoBind);
    }

    private void onUncaughtException(Throwable arg0) {
    }

    private void onDevicesNetworkChanged(NetworkInfo info) {

        if (info != null) {
            PushManager.connect(mContext);
        }

        onNetworkChanged(info);
    }

    private void onInnerMessageReceived(GCMessage message) {
        if (Constant.MessageType.TYPE_999.equals(message.getHeader().getType())) {
            CacheToolkit.getInstance(mContext).putBoolean(CacheToolkit.KEY_MANUAL_STOP, true);
        }

        onMessageReceived(message);
    }

    private void onSentFailed(Exception e, GCMessage message) {

        // 与服务端端开链接，重新连接
        if (e instanceof SessionDisableException) {
            PushManager.connect(mContext);
        } else {
            // 发送失败 重新发送
            PushManager.sendRequest(mContext, message);
        }

    }

    private void onSentSucceed(GCMessage message) {
        //可以保存历史记录
    }

    @Override
    public abstract void onMessageReceived(GCMessage message);

    public abstract void onNetworkChanged(NetworkInfo info);

    public abstract void onConnectionFailed(Exception e);

}
