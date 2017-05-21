package com.accumulation.lib.utility.net;

import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;


/**
 * Created by zhangyl on 2016/8/17.
 */
public class NetworkClient {

    private Retrofit mRetrofit;
    private OkHttpClient mOkHttpClient;
    private NetworkConfig mConfig;

    private static final NetworkClient mInstance = new NetworkClient();

    public static NetworkClient getClient() {
        return mInstance;
    }

    public void init(NetworkConfig config){
        if (mRetrofit!=null){
            throw new IllegalStateException("network client has inited");
        }
        if (config == null) throw new NullPointerException("config == null");

        this.mConfig=config;

        OkHttpClient.Builder builder=new OkHttpClient.Builder()
                .connectTimeout(config.getConnectTimeout(),TimeUnit.SECONDS)
                .writeTimeout(config.getWriteTimeout(),TimeUnit.SECONDS)
                .readTimeout(config.getReadTimeout(),TimeUnit.SECONDS);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        if(config.isDevelopMode()){
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        builder.addInterceptor(loggingInterceptor);

        List<Interceptor> interceptors=config.getInterceptor();
        if(interceptors!=null&&interceptors.size()>0){
            for (Interceptor interceptor:interceptors) {
                builder.addInterceptor(interceptor);
            }
        }
        CookieJar cookieJar=config.getCookieJar();
        if(cookieJar!=null){
            builder.cookieJar(cookieJar);
        }
        Cache cache=config.getCache();
        if(cache!=null){
            builder.cache(cache);
        }
        mOkHttpClient=builder.build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(config.getBaseURL() + "")
                .addConverterFactory(config.getConverterFactory())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(mOkHttpClient)
                .build();
    }

    private Retrofit getRetrofit() {
        if(mRetrofit==null){
            throw new IllegalStateException("you should call init firstly");
        }else{
            return mRetrofit;
        }
    }

    public <T> T create(final Class<T> service) {
        return getRetrofit().create(service);
    }

    public  OkHttpClient.Builder cloneOkHttpClientBuilder(OkHttpClient.Builder builder){
        return mOkHttpClient.newBuilder();
    }

}
