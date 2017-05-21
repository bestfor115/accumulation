package com.accumulation.lib.tool.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeaderValueParser;
import org.apache.http.message.HeaderValueParser;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class HttpUtils {
	
	/** 检查是否有网络 */
	public static boolean isNetworkAvailable(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			return info.isAvailable();
		}
		return false;
	}

	/** 检查是否是WIFI */
	public static boolean isWifi(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_WIFI)
				return true;
		}
		return false;
	}

	/** 检查是否是移动网络 */
	public static boolean isMobile(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE)
				return true;
		}
		return false;
	}

	private static NetworkInfo getNetworkInfo(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo();
	}
	
	public static String getCharSet(URLConnection connection) throws IOException {
		String contentType = connection.getContentType();
		if (contentType != null) {
			HeaderValueParser parser = new BasicHeaderValueParser();
			HeaderElement[] values = BasicHeaderValueParser.parseElements(contentType, parser);
			if (values.length > 0) {
				NameValuePair param = values[0].getParameterByName("charset");
				if (param != null) {
					return param.getValue();
				}
			}
		}
		if (connection instanceof HttpURLConnection) {
			return "UTF-8";
		} else {
			throw new IOException("Unabled to determine character encoding");
		}
	}
	
	/**
	 * 
	 * @param host
	 * @param timeOut in milliseconds
	 * @return
	 */
	public static boolean checkDNSResolve(String host, int timeOut){
		Future<InetAddress> future = sPool.submit(new DNSChecker(host));
		try {
			return future.get(timeOut, TimeUnit.MILLISECONDS) != null;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			future.cancel(true);
		}
		return false;
	}
	
	private static ExecutorService sPool = Executors.newCachedThreadPool();
	
	public static class DNSChecker implements Callable<InetAddress>{
		private String host;
		
		public DNSChecker(String host ){
			this.host = host;
		}

		@Override
		public InetAddress call() throws Exception {
			try{
				return InetAddress.getByName(host);
			}catch(UnknownHostException e){
				e.printStackTrace();
			}
			return null;
		}
		
	}
	
	public static InputStream getInputStreamFromURL(String urlstr) {
		HttpURLConnection connection;
		URL url;
		InputStream stream = null;
		try {
			url = new URL(urlstr);
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			stream = connection.getInputStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return stream;
	}
}
