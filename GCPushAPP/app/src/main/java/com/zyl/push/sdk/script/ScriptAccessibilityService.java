package com.zyl.push.sdk.script;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.accumulation.lib.utility.debug.Logger;

import java.util.List;

/**
 * Created by zhangyuliang on 2017/6/13.
 */

public class ScriptAccessibilityService extends AccessibilityService {

    private String mForegroundPkg;
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Logger.d("ScriptAccessibilityService onServiceConnected");
        registerReceiver(new EventBroadcastReceiver(),new IntentFilter("com.zyl.push.action.CLICK_WIDKET"));
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Logger.d(event.toString());
        int eventType = event.getEventType();
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            mForegroundPkg = event.getPackageName().toString();
        }
    }

    @Override
    public void onInterrupt() {

    }

    private void click(AccessibilityEvent event, String text, String widgetType) {
        // 事件页面节点信息不为空
        if (event.getSource() != null) {
            // 根据Text搜索所有符合条件的节点, 模糊搜索方式; 还可以通过ID来精确搜索findAccessibilityNodeInfosByViewId
            List<AccessibilityNodeInfo> stop_nodes = event.getSource().findAccessibilityNodeInfosByText(text);
            // 遍历节点
            if (stop_nodes != null && !stop_nodes.isEmpty()) {
                for (int i = 0; i < stop_nodes.size(); i++) {
                    AccessibilityNodeInfo node = stop_nodes.get(i);
                    // 判断按钮类型
                    if (node.getClassName().equals(widgetType)) {
                        // 可用则模拟点击
                        if (node.isEnabled()) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
        }
    }

    class EventBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String key=intent.getStringExtra("key");
            Logger.d("receive a click : "+key);
            if(!TextUtils.isEmpty(key)){
                clicekElementByKeyword(key);
            }
        }
    }
    private void clicekElementByKeyword(String key) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            //为了演示,直接查看了红包控件的id
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(key);
            nodeInfo.recycle();
            Logger.d("widget count :"+list.size());
            for (AccessibilityNodeInfo item : list) {
                AccessibilityNodeInfo node=calculateClickable(item);
                if(node!=null){
                    Logger.d("find a node : "+node.getClassName());
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
    }
    private AccessibilityNodeInfo calculateClickable(AccessibilityNodeInfo node){
        if(node!=null){
            if(!node.isClickable()){
                return calculateClickable(node.getParent());
            }else{
                return node;
            }
        }
        return null;
    }
}
