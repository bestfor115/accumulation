package com.zyl.push.sdk.script;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.accumulation.lib.utility.debug.Logger;

import java.util.List;

/**
 * Created by zhangyuliang on 2017/6/13.
 */

public class ScriptAccessibilityService extends AccessibilityService {

    public static final String TAP_WIDGET_ACTION="com.zyl.push.action.CLICK_WIDKET";
    public static final String SET_INPUT_METHOD_ACTION="com.zyl.push.action.SET_INPUT_METHOD";
    public static final String TAP_COLOR_ACTION="com.zyl.push.action.CLICK_COLOR";

    private String mForegroundPkg;
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Logger.d("ScriptAccessibilityService onServiceConnected");
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(TAP_WIDGET_ACTION);
        intentFilter.addAction(SET_INPUT_METHOD_ACTION);
        intentFilter.addAction(TAP_COLOR_ACTION);
        registerReceiver(new EventBroadcastReceiver(),intentFilter);
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Logger.d(event.toString());
        int eventType = event.getEventType();
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            mForegroundPkg = event.getPackageName().toString();
        }
    }

    @Override
    public void onInterrupt() {

    }

    class EventBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(TAP_WIDGET_ACTION.equals(intent.getAction())){
                String key=intent.getStringExtra("key");
                Logger.d("receive a click : "+key);
                if(!TextUtils.isEmpty(key)){
                    clicekElementByKeyword(key);
                }
            }else if(SET_INPUT_METHOD_ACTION.equals(intent.getAction())){
                int state=intent.getIntExtra("state",0);
                triggerInputMethod(state);
            }else if(TAP_COLOR_ACTION.equals(intent.getAction())){
                String color=intent.getStringExtra("color");
                calculateColorRange(Color.parseColor("color"));
            }
        }
    }
    private void calculateColorRange(int color){

    }
    private void triggerInputMethod(int state) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if(nodeInfo!=null){
            if(state==0){
                AccessibilityNodeInfo inputInfo=findFocusableNode(nodeInfo);
                if(inputInfo!=null){
                    inputInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }else{
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLEAR_FOCUS);
            }
        }
    }
    private AccessibilityNodeInfo findFocusableNode(AccessibilityNodeInfo node){
        if(node.isEditable()){
            return node;
        }else{
            int N=node.getChildCount();
            for (int i=0;i<N;i++){
                AccessibilityNodeInfo sub=findFocusableNode(node.getChild(i));
                if(sub!=null){
                    return sub;
                }
            }
        }
        return null;
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
