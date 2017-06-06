package com.accumulation.lib.utility.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import com.accumulation.lib.utility.debug.Logger;

import java.util.List;

/**
 * Created by tom.chen on 2016/11/28.
 */

public class LocationUtils {
    public static final String TAG = "LocationU";

    private LocationCallBack mCallBack;

    private volatile static LocationUtils uniqueInstance;

    private LocationManager mLocationManager;

    private Context mContext;

    private LocationUtils(Context context) {
        mContext = context;
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public static LocationUtils getInstance(Context context) {
        if (uniqueInstance == null) {
            synchronized (LocationUtils.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new LocationUtils(context);
                }
            }
        }
        return uniqueInstance;
    }

    public void fetchLocation(LocationCallBack callback) {
        mCallBack = callback;
        List<String> providers = mLocationManager.getProviders(false);
        for (String locationProvider : providers) {
            if (locationProvider != null) {
                if(!checkPermission(mContext)){
                    break;
                }
                Location location = mLocationManager.getLastKnownLocation(locationProvider);
                if (location != null) {
                    mCallBack.onLastLocationChanged(location);
                    break;
                } else {
                    Logger.e(TAG, ".initLocationInfo() lastKnowLocation is empty ");
                }
            }
        }
        try {
            int locationMode = Settings.Secure.getInt(mContext.getContentResolver(), Settings.Secure.LOCATION_MODE);
            Logger.d(TAG, ".initLocation() locationMode:" + locationMode);
            if (locationMode != Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {
                Settings.Secure.putInt(mContext.getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Logger.d(TAG, ".initLocation() set Settings error ");
        }
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new MyLocationListener());
    }

    private boolean checkPermission(Context context){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Logger.d(TAG, "onLocationChanged: ");
            if (location != null) {
                Logger.d(TAG, "onLocationChanged: Longitude:" + location.getLongitude() + "|Latitude:" + location.getLatitude());
                mCallBack.onLocationChanged(location);
            }
            if(checkPermission(mContext)){
                mLocationManager.removeUpdates(this);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            mCallBack.onStatusChanged(provider,status,extras);
        }

        @Override
        public void onProviderEnabled(String provider) {
            mCallBack.onProviderEnabled(provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            mCallBack.onProviderDisabled(provider);
        }
    }
    interface LocationCallBack extends LocationListener {
        void onLocationChanged(Location location);
        void onLastLocationChanged(Location location);
    }
    public static abstract class SimpleLocationCallBack implements LocationCallBack{

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }

}
