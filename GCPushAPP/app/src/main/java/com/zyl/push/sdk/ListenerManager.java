/**
 * probject:cim-android-sdk
 * @version 2.1.0
 * 
 * @author 3979434@qq.com
 */ 
package com.zyl.push.sdk;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.zyl.push.sdk.constant.Constant;
import com.zyl.push.sdk.model.GCMessage;


/**
 * CIM 消息监听器管理
 */
public class ListenerManager  {

    private static ArrayList<EventListener> mListeners = new ArrayList<EventListener>();
    
    

    public static void registerMessageListener(EventListener listener,Context mcontext) {

        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
            // 按照接收顺序倒序
            Collections.sort(mListeners, new CIMMessageReceiveComparator(mcontext));
        }
    }

    
    public static void removeMessageListener(EventListener listener) {
        for (int i = 0; i < mListeners.size(); i++) {
            if (listener.getClass() == mListeners.get(i).getClass()) {
                mListeners.remove(i);
            }
        }
    }
    
    
    public static void notifyOnNetworkChanged(NetworkInfo info) {
        for (EventListener listener : mListeners) {
            listener.onNetworkChanged(info);
        }
    }
    
    
    public static void notifyOnConnectionSuccessed(boolean hasAutoBind) {
        for (EventListener listener : mListeners) {
            listener.onConnectionSuccessed(hasAutoBind);
        }
    }
    public static void notifyOnMessageReceived(GCMessage message) {
        for (EventListener listener : mListeners) {
            listener.onMessageReceived(message);
        }
    }
    
    public static void notifyOnConnectionClosed() {
        for (EventListener listener : mListeners) {
            listener.onConnectionClosed();
        }
    }

    
    public static void notifyOnConnectionFailed(Exception e) {
        for (EventListener listener : mListeners) {
            listener.onConnectionFailed(e);
        }
    }
    
    public static void destory() {
        mListeners.clear();
    }
    
    public static void logListenersName() {
        for (EventListener listener : mListeners) {
            Log.i(EventListener.class.getSimpleName(),"#######" + listener.getClass().getName() + "#######" );
        }
    }
    
    /**
     * 消息接收activity的接收顺序排序，CIM_RECEIVE_ORDER倒序
     */
   static class CIMMessageReceiveComparator  implements Comparator<EventListener>{

        Context mcontext;
        public CIMMessageReceiveComparator(Context ctx)
        {
            mcontext = ctx;
        }
        @Override
        public int compare(EventListener arg1, EventListener arg2) {
             
            Integer order1  = Constant.CIM_DEFAULT_MESSAGE_ORDER;
            Integer order2  = Constant.CIM_DEFAULT_MESSAGE_ORDER;
            ActivityInfo info;
            if (arg1 instanceof Activity  ) {
                
                try {
                     info = mcontext.getPackageManager() .getActivityInfo(((Activity)(arg1)).getComponentName(), PackageManager.GET_META_DATA);
                     if(info.metaData!=null)
                     {
                         order1 = info.metaData.getInt("CIM_RECEIVE_ORDER");
                     }
                 } catch (Exception e) {}
            }
            if (arg1 instanceof Activity ) {
                try {
                     info = mcontext.getPackageManager() .getActivityInfo(((Activity)(arg2)).getComponentName(), PackageManager.GET_META_DATA);
                     if(info.metaData!=null)
                     {
                         order2 = info.metaData.getInt("CIM_RECEIVE_ORDER");
                     }
                 } catch (Exception e) {}
            }
            return order2.compareTo(order1);
        }
         

    }

}