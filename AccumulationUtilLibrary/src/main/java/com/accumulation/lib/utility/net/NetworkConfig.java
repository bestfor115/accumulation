package com.accumulation.lib.utility.net;

import android.content.Context;

import com.accumulation.lib.utility.net.cookie.CookiesManager;

import java.io.File;
import java.util.List;

import okhttp3.Cache;
import okhttp3.CookieJar;
import okhttp3.Interceptor;
import retrofit2.Converter;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zhangyl on 2016/8/17.
 */
public abstract class NetworkConfig {

    public abstract Context getContext();

    public abstract String getBaseURL();

    public boolean isDevelopMode(){
        return true;
    }

    public Converter.Factory getConverterFactory() {
        return GsonConverterFactory.create();
    }

    public  int getConnectTimeout(){
        return 30;
    }

    public  int getWriteTimeout(){
        return 30;
    }

    public  int getReadTimeout(){
        return 30;
    }

    public CookieJar getCookieJar(){
        return new CookiesManager(getContext());
    }

    public List<Interceptor> getInterceptor(){
        return null;
    }

    public Cache getCache(){
        String cachePath=getContext().getApplicationContext().getCacheDir().getAbsolutePath();
        File cacheDirectory = new File(cachePath,"NetCache");
        return new Cache(cacheDirectory, 10 * 1024 * 1024);
    }

}
