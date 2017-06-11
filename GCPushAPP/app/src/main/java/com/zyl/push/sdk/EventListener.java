/**
 * probject:cim-android-sdk
 * @version 2.1.0
 * 
 * @author 3979434@qq.com
 */  
package com.zyl.push.sdk;

import android.net.NetworkInfo;

import com.zyl.push.sdk.model.GCMessage;


/**
 *CIM 主要事件接口
 */
public interface EventListener
{


    /**
     * 当收到服务端推送过来的消息时调用
     * @param message
     */
    public abstract void onMessageReceived(GCMessage message);

    /**
     * 当手机网络发生变化时调用
     * @param networkinfo
     */
    public abstract void onNetworkChanged(NetworkInfo networkinfo);
    
    
    /**
     * 当连接服务器成功时回调
     * @param hasAutoBind  : true 已经自动绑定账号到服务器了，不需要再手动调用bindAccount
     */
    public abstract void onConnectionSuccessed(boolean hasAutoBind);
    
    /**
     * 当断开服务器连接的时候回调
     */
    public abstract void onConnectionClosed();
    
    /**
     * 当服务器连接失败的时候回调
     * 
     */
    public abstract void onConnectionFailed(Exception e);
}

