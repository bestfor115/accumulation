package com.zyl.push.sdk.script;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.Context;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.zyl.push.sdk.constant.Constant;
import com.zyl.push.sdk.EventListener;
import com.zyl.push.sdk.FileTransportManager;
import com.zyl.push.sdk.ListenerManager;
import com.zyl.push.sdk.model.GCMessage;
import com.zyl.push.sdk.model.GCScript;

import dalvik.system.DexClassLoader;

public class ScriptManager implements EventListener {
    private static String TAG="ScriptManager";
    private static volatile ScriptManager mInstance;
    private Context mContext;
    private ExecutorService mExecutor;
    private Handler mMainHandler;
    private boolean mExecing=false;
    private  MScriptProtocol mProtocol;
    public static ScriptManager getManager() {
        if (mInstance == null) {
            synchronized (ScriptManager.class) {
                if (mInstance == null) {
                    mInstance = new ScriptManager();
                }
            }
        }
        return mInstance;
    }
    private ScriptManager(){
        mExecutor = Executors.newCachedThreadPool();
        mMainHandler=new Handler(Looper.getMainLooper());
        mProtocol=new MScriptProtocol();
        mProtocol.bindImpl(ScriptFactory.getScriptable());
    }
    public void bindContext(Context context){
        mContext=context.getApplicationContext();
        ListenerManager.registerMessageListener(this,mContext);
    }
    public Context getContext(){
    	return mContext;
    }
    public Handler getMainHandler(){
    	return mMainHandler;
    }
    public void execLuaScript(final GCScript script){
    	Log.d(TAG, "load a lua script "+script.getUrl());
        mExecing=false;
        if(script!=null&&script.getUrl()!=null&&!mExecing){
            mExecing=true;
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
            		final File jarFile = new File(mContext.getDir("extlua", 0), "ext" + script.getUrl().hashCode() + ".lua");
            		if (true||!(jarFile.exists() && jarFile.length() > 0)) {
            			try {
            				HttpURLConnection conn = (HttpURLConnection) new URL(script.getUrl()).openConnection();
    						Log.d(TAG, "Connect to: " + conn.getURL().toString());
    						conn.setConnectTimeout(5000);
    						conn.connect();
    						int resCode = conn.getResponseCode();
    						Log.d(TAG, "Response " + resCode + " for " + conn.getURL().toString());
    						if (resCode >= 200 && resCode < 300) {
    							InputStream is = conn.getInputStream();
//    							InputStream is = mContext.getResources().getAssets().open("t1.lua");
    							OutputStream os = new FileOutputStream(jarFile);
    							ExtJarLoader.streamCopy(is, os);
        						Log.d(TAG, "save a script to : "+jarFile.getPath());
    						}
						} catch (Exception e) {
							e.printStackTrace();
							jarFile.delete();
						}
            		}
            		mProtocol.exec(jarFile.getPath());
                }
            });
        }else{
            Log.d(TAG, "give up exec a script");
        }
    }
    public void execLuaScript(){
    	Log.d(TAG, "load a test script ");
            mExecing=true;
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
            		final File jarFile = new File(mContext.getDir("extlua", 0), "ext" + "000".hashCode() + ".lua");
					try {
	            		InputStream is = mContext.getResources().getAssets().open("t1.lua");
						OutputStream os = new FileOutputStream(jarFile);
						ExtJarLoader.streamCopy(is, os);
				    	Log.d(TAG, "test script path :"+jarFile.getPath());

	            		mProtocol.exec(jarFile.getPath());
					} catch (Exception e) {
						e.printStackTrace();
						jarFile.delete();
					}
                }
            });
    }
    public void execUpload(final String account){
    	Log.d(TAG, "load a upload script ");
            mExecing=true;
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
            		final File jarFile = new File(mContext.getDir("extlua", 0), "ext" + "000".hashCode() + ".lua");
					try {
	            		InputStream is = mContext.getResources().getAssets().open("t1.lua");
						OutputStream os = new FileOutputStream(jarFile);
						ExtJarLoader.streamCopy(is, os);
				    	Log.d(TAG, "test script path :"+jarFile.getPath());
//				    	FileTransportManager.getManager(getContext()).requestFileTask(account,"09adf25d84ab46fb967710549d8836ef548",jarFile.getPath());

				    	FileTransportManager.getManager(getContext()).requestFileTask(account,GCMessage.MESSAGE_TARGET_SYSTEM,jarFile.getPath());
					} catch (Exception e) {
						e.printStackTrace();
						jarFile.delete();
					}
                }
            });
    }
    public void execScript(final GCScript script){
    	Log.d(TAG, "load a script "+script.getUrl());
    	Log.d(TAG, "load a script "+mExecing);
        mExecing=false;
        if(script!=null&&script.getUrl()!=null&&!mExecing){
            mExecing=true;
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    ExtJarLoader.loadExtJar(mContext, script.getUrl(), new ExtJarLoader.ExtJarLoadListener() {
                        @Override
                        public void onLoaded(DexClassLoader classLoader) {
                        	if(classLoader==null){
                        		Log.e(TAG, "classLoader is null");
                        		return ;
                        	}
                        	Log.d(TAG, "load a script ");
                            Executable executable=null;
                            try {
                                Class c = classLoader.loadClass(script.getLoadClass());
                                executable = (Executable) c.newInstance();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            if(executable!=null){
                            	executable.bindImpl(ScriptFactory.getScriptable());
                                for (int i = 0; i < script.getCount(); i++) {
                                    String result=executable.exec(script.getParamter());
                                	Log.d(TAG, "exec a script "+result);
                                }
                            }
                        }
                    });
                }
            });
        }else{
            Log.d(TAG, "give up exec a script");
        }
    }
	@Override
	public void onMessageReceived(GCMessage message) {
        if(Constant.MessageType.TYPE_PUSH_SCRIPT.equals(message.getType())){
        	GCScript script=new GCScript();
        	script.setLoadClass(message.getExtra("loadClass"));
        	try {
				script.setUrl(URLDecoder.decode(message.getExtra("url"),"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        	script.setCount(1);
//        	script.setCount(Integer.parseInt(message.getExtra("count")));
        	script.setParamter(message.getExtra("paramter").split("\\|"));
            ScriptManager.getManager().execScript(script);
            return ;
        }
        if(Constant.MessageType.TYPE_PUSH_LUA_SCRIPT.equals(message.getType())){
        	GCScript script=new GCScript();
        	script.setCount(1);
        	script.setParamter(message.getExtra("paramter").split("\\|"));
        	try {
				script.setUrl(URLDecoder.decode(message.getExtra("url"),"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
            ScriptManager.getManager().execLuaScript(script);
            return ;
        }
	}
	@Override
	public void onNetworkChanged(NetworkInfo networkinfo) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onConnectionSuccessed(boolean hasAutoBind) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onConnectionClosed() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onConnectionFailed(Exception e) {
		// TODO Auto-generated method stub
		
	}
}
