package com.accumulation.lib.utility.net;

import android.content.Context;
import com.accumulation.lib.utility.base.NetUtils;
import com.accumulation.lib.utility.debug.Logger;
import com.accumulation.lib.utility.matcher.StringUtils;

import java.io.IOException;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhangyl on 2016/8/17.
 */
public class ValidIntercepter implements Interceptor {
    private Context mContext;
    private boolean mIsDevelop;

    public ValidIntercepter(Context context,boolean isDevelop){
        this.mContext=context;
        this.mIsDevelop=isDevelop;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        if(!NetUtils.isConnected(mContext)){
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)//无网络时只从缓存中读取
                    .build();
        }

        Response response = chain.proceed(request);
        if(NetUtils.isConnected(mContext)){
            int maxAge = 60 * 60; // 有网络时 设置缓存超时时间1个小时
            response.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build();
        } else {
            int maxStale = 60 * 60 * 24 * 28; // 无网络时，设置超时为4周
            response.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build();
        }
        return response;
    }
}
