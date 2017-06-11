/**
 * probject:cim
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */
package com.zyl.push.sdk;

import android.net.NetworkInfo;

import com.zyl.push.sdk.model.GCMessage;


/**
 * 消息入口，所有消息都会经过这里
 * 
 * @author 3979434
 * 
 */
public class DefaultEventReceiver extends EventBroadcastReceiver {
    @Override
    public void onMessageReceived(GCMessage message) {
        ListenerManager.notifyOnMessageReceived(message);
    }

    @Override
    public void onNetworkChanged(NetworkInfo info) {
        ListenerManager.notifyOnNetworkChanged(info);
    }

    @Override
    public void onConnectionSuccessed(boolean hasAutoBind) {
        ListenerManager.notifyOnConnectionSuccessed(hasAutoBind);
    }

    @Override
    public void onConnectionClosed() {
        ListenerManager.notifyOnConnectionClosed();
    }

    @Override
    public void onConnectionFailed(Exception arg0) {
        ListenerManager.notifyOnConnectionFailed(arg0);
    }

}
