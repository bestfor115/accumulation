package com.zyl.push.sdk.script;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by zhangyuliang on 2017/6/13.
 */

public class ScriptAccessibilityService extends AccessibilityService {

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d("XLZH:", "OnServiceConnected");
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d("XLZH:", event.toString());
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                //获取发生该事件的页面根view
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                //根据id查找需要点击的节点，返回的是一个List
                List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tendcloud.demo:id/pager");
                //获取List的第一个节点，并打印该节点的child数目及类名
                AccessibilityNodeInfo contentNodeInfo = list.get(0);
                Log.d("XLZH size:", String.valueOf(contentNodeInfo.getChildCount()));
                Log.d("XLZH class: ", String.valueOf(contentNodeInfo.getClassName()));

                //通过查找文本的方式获得节点
                if(contentNodeInfo.findAccessibilityNodeInfosByText("button_test1") != null){
                    Log.d("XLZH :", "first page");
                }
                if(contentNodeInfo.findAccessibilityNodeInfosByText("button1") != null){
                    Log.d("XLZH :", "second page");
                }
                if(contentNodeInfo.findAccessibilityNodeInfosByText("tvweb") != null){
                    Log.d("XLZH :", "third page");
                }
                break;
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
                AccessibilityNodeInfo node;
                for (int i = 0; i < stop_nodes.size(); i++) {
                    node = stop_nodes.get(i);
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
}
