package com.accumulation.lib.utility.net.cookie;

import android.content.Context;

import java.util.List;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by zhangyl on 2016/8/17.
 */
public class CookiesManager implements CookieJar {
    private final PersistentCookieStore cookieStore;

    private Context mContext;

    public CookiesManager(Context context){
        this.mContext=context;
        cookieStore = new PersistentCookieStore(mContext.getApplicationContext());
    }
    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (cookies != null && cookies.size() > 0) {
            for (Cookie item : cookies) {
                cookieStore.add(url, item);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies = cookieStore.get(url);
        return cookies;
    }
}
