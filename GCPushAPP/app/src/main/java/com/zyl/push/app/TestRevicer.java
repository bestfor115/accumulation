package com.zyl.push.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zyl.push.sdk.script.ScriptManager;

/**
 * Created by zhangyuliang on 2017/6/14.
 */

public class TestRevicer extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        ScriptManager.getManager().execLuaScript();
    }
}
