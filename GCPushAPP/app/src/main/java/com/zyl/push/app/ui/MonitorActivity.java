/**
 * probject:cim
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */
package com.zyl.push.app.ui;

import android.app.Activity;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.zyl.push.sdk.EventListener;
import com.zyl.push.sdk.ListenerManager;
import com.zyl.push.sdk.model.GCMessage;


public abstract class MonitorActivity extends Activity implements EventListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListenerManager.registerMessageListener(this, this);

    }

    @Override
    public void finish() {
        super.finish();
        ListenerManager.removeMessageListener(this);

    }

    @Override
    public void onRestart() {
        super.onRestart();
        ListenerManager.registerMessageListener(this, this);
    }

    @Override
    public void onMessageReceived(GCMessage message) {
        // TODO Auto-generated method stub
        
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

    public void showProgressDialog(String title, String content) {
        Toast.makeText(this, title+ ":" + content, Toast.LENGTH_SHORT).show();
    }

    public void hideProgressDialog() {
    }

    public void showToast(String hint) {
        Toast.makeText(this, hint, Toast.LENGTH_SHORT).show();
    }
}
