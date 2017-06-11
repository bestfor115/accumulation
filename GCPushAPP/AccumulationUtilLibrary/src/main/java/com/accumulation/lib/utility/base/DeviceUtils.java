package com.accumulation.lib.utility.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.accumulation.lib.utility.debug.Logger;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by zhangyuliang on 2017/5/22.
 */

public class DeviceUtils {
    private final static String TAG = "DeviceUtils";
    private static final String DEFAULT_MAC_VALUE = "020000000000";

    private static String sMac;

    public static String getMAC(Context context){
        if(!isValidMac(sMac)){
            try {
                Enumeration<NetworkInterface> enu = NetworkInterface
                        .getNetworkInterfaces();
                while (enu.hasMoreElements()) {
                    NetworkInterface ni = enu.nextElement();
                    byte[] mac = ni.getHardwareAddress();
                    String macStr = null;
                    String ipStr = null;
                    if (mac != null) {
                        StringBuilder buf = new StringBuilder();
                        for (int idx = 0; idx < mac.length; idx++)
                            buf.append(String.format("%02X", mac[idx]));
                        macStr = buf.toString();
                    }
                    Enumeration<InetAddress> aenu = ni.getInetAddresses();
                    while (aenu.hasMoreElements()) {
                        InetAddress addr = aenu.nextElement();
                        if (!addr.isLoopbackAddress()) {
                            if(addr instanceof Inet4Address){
                                ipStr = addr.getHostAddress();
                            }
                        }
                    }
                    if(macStr != null && ni.getName()!=null&& ni.getName().toLowerCase().startsWith("eth")){
                        sMac = macStr.toLowerCase();
                        break;
                    }
                    if (macStr != null && ipStr != null) {
                        sMac = macStr.toLowerCase();
                    }
                }
                sMac = formatMAC(sMac);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        if(!isValidMac(sMac)){
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            @SuppressLint("HardwareIds") String macAddress = info.getMacAddress();
            sMac = formatMAC(macAddress);
        }
        if(!isValidMac(sMac)){
            sMac=SystemPropertieUtils.getSystemPropertie("persist.sys.wifi_mac",DEFAULT_MAC_VALUE);
            sMac = formatMAC(sMac);
        }
        if(TextUtils.isEmpty(sMac)){
            sMac=DEFAULT_MAC_VALUE;
        }
        Logger.d(TAG,"init MAC with :"+sMac);
        return sMac;
    }

    public static String getSerialNumber() {
        return Build.SERIAL;
    }

    public static String getProductName() {
        return Build.PRODUCT;
    }

    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }
    public static String getModel() {
        return Build.MODEL;
    }

    public static String getAndroidId(Context context) {
        return Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private static boolean isValidMac(String mac){
        if(TextUtils.isEmpty(mac)){
            return false;
        }
        if(DEFAULT_MAC_VALUE.equals(mac)){
            return false;
        }
        int N=mac.length();
        int zeroCount=0;
        for (int i=0;i<N;i++){
            if("0".equals(mac.substring(i,i+1))){
                zeroCount++;
            }
        }
        return zeroCount<10;
    }

    private static String formatMAC(String mac){
        if(!TextUtils.isEmpty(mac)){
            mac=mac.replaceAll(":","");
            mac=mac.replaceAll("-","");
            mac=mac.replaceAll(" ","");
            mac=mac.toLowerCase();
        }
        return mac;
    }
}
