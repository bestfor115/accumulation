package com.accumulation.lib.tool.net.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceManager {
    static SharedPreferences sp;

    public static void init(Context context, String name) {
        sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    private static final String KEY_CACHED_USERNAME = "jchat_cached_username";

    public static void setCachedUsername(String username) {
        if (null != sp) {
            sp.edit().putString(KEY_CACHED_USERNAME, username).commit();
        }
    }

    public static String getCachedUsername() {
        if (null != sp) {
            return sp.getString(KEY_CACHED_USERNAME, null);
        }
        return null;
    }
    
    private static final String KEY_CACHED_USERID = "jchat_cached_userid";

    public static void setCachedUserId(String username) {
        if (null != sp) {
            sp.edit().putString(KEY_CACHED_USERID, username).commit();
        }
    }

    public static String getCachedUserId() {
        if (null != sp) {
            return sp.getString(KEY_CACHED_USERID, null);
        }
        return null;
    }

    private static final String KEY_CACHED_AVATAR_PATH = "jchat_cached_avatar_path";

    public static void setCachedAvatarPath(String path) {
        if (null != sp) {
            sp.edit().putString(KEY_CACHED_AVATAR_PATH, path).commit();
        }
    }

    public static String getCachedAvatarPath() {
        if (null != sp) {
            return sp.getString(KEY_CACHED_AVATAR_PATH, null);
        }
        return null;
    }


}
