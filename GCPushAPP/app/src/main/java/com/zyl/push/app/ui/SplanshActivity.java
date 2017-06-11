/**
 * probject:cim
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */
package com.zyl.push.app.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.zyl.push.app.Caches;
import com.zyl.push.app.GCConstant;
import com.zyl.push.app.R;
import com.zyl.push.app.model.JDRegister;
import com.zyl.push.sdk.PushManager;
import com.zyl.push.sdk.constant.Constant;
import com.zyl.push.sdk.model.GCMessage;


public class SplanshActivity extends MonitorActivity {

    boolean initComplete = false;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // 连接服务端

        PushManager.connect(SplanshActivity.this, GCConstant.CIM_SERVER_HOST,
                GCConstant.CIM_SERVER_PORT);

        final View view = View.inflate(this, R.layout.activity_splansh, null);
        setContentView(view);
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(2000);
        view.startAnimation(aa);
    }

    @Override
    public void onConnectionSuccessed(boolean autoBind) {

        if (Caches.hasUserData()) {
            startLogin();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("提示")
                    .setNegativeButton("取消", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            PushManager.destroy(SplanshActivity.this);
                        }
                    }).setPositiveButton("确定", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent(SplanshActivity.this, RegisterActivity.class);
//                            startActivityForResult(intent, 1);
                        }
                    }).setMessage("手机还没登记，是否现在前去扫描注册?");
            builder.create().show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            JDRegister.Register register = (JDRegister.Register) data.getSerializableExtra("data");
            Caches.mUserId = register.userId;
            Caches.mDeviceId = register.deviceId;
            Caches.saveData();
            startLogin();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        PushManager.destroy(this);
    }

    public void onConnectionFailed(Exception e) {
        showToast("连接服务器失败，请检查当前设备是否能连接上服务器IP和端口");
    }

    public void startLogin() {
        showProgressDialog("提示", "正在登录，请稍候......");
        if (PushManager.getState(this) == PushManager.STATE_NORMAL) {
            PushManager.bindAccount(this, Caches.mDeviceId);
            return;
        }
        if (PushManager.getState(this) == PushManager.STATE_STOPED) {
            PushManager.connect(this, GCConstant.CIM_SERVER_HOST, GCConstant.CIM_SERVER_PORT);
        }
    }

    @Override
    public void onMessageReceived(GCMessage message) {
        super.onMessageReceived(message);
        if (Constant.MessageType.TYPE_CLIENT_BIND.equals(message.getType())) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("account", Caches.mDeviceId + "");
            startActivity(intent);
            this.finish();
        }
    }
}
