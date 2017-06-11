package com.accumulation.lib.utility.base;

import java.lang.reflect.Method;

/**
 * Created by zhangyuliang on 2017/5/22.
 */

public class SystemPropertieUtils {
    public static String getSystemPropertie(String key,String defaultValue) {
        Object value ;
        try {
            Class systemPropertiesCls = Class
                    .forName("android.os.SystemProperties");
            Method mMethod = systemPropertiesCls.getMethod("get", String.class);
            value = mMethod.invoke(null, key);
        } catch (Throwable e) {
            return defaultValue;
        }
        return value+"";
    }
    public static void setSystemPropertie(String key,String value) {
        try {
            Class systemPropertiesCls = Class
                    .forName("android.os.SystemProperties");
            Method mMethod = systemPropertiesCls.getMethod("set", String.class);
            mMethod.invoke(null, key);
        } catch (Throwable e) {
        }
    }
}
