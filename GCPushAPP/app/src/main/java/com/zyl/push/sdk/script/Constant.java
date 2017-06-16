package com.zyl.push.sdk.script;

import android.view.KeyEvent;

public interface Constant {
    public static final int API_VERSION_BASE=0;
    public static final int ANDROID_PLATFORM=0;
    public static final int IOS_PLATFORM=1;
    public static final int KEYCODE_BACK = KeyEvent.KEYCODE_BACK;
    public static final int KEYCODE_HOME = KeyEvent.KEYCODE_HOME;
    public static final int KEYCODE_MENU = KeyEvent.KEYCODE_MENU;
    public static final int KEYCODE_SEARCH = 0;
    public static final String APP_KEY_WEIXIN = "gc://app/weixin";
    public static final String APP_KEY_QQ = "gc://app/qq";
    public static final String APP_KEY_WEIBO = "gc://app/weibo";
    public static final String APP_KEY_QQ_ZONE = "gc://app/qqzone";
    public static final int TOUCH_DIRECTION_HORIZONTAL=0;
    public static final int TOUCH_DIRECTION_VERTICAL=1;
    public static final int TOUCH_DIRECTION_LR=2;
    public static final int TOUCH_DIRECTION_RL=3;
    public static final int TOUCH_DIRECTION_TB=4;
    public static final int TOUCH_DIRECTION_BT=5;

}
