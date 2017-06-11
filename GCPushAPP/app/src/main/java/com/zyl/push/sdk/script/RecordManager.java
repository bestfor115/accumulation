package com.zyl.push.sdk.script;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class RecordManager {
    private final static String TAG = "record";
    private volatile static RecordManager mInstance;
    private Context mContext;
    private Handler mHandler;
    private HandlerThread mThread;
    private final List<String> mEvents = new ArrayList<String>();
    private final Object mLock = new Object();
    private boolean mRecording = false;
    private DataOutputStream mOutStream = null;
    private Process mProcess = null;

    public static RecordManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (RecordManager.class) {
                if (mInstance == null) {
                    mInstance = new RecordManager(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    private RecordManager(Context context) {
        this.mContext = context;
        mThread = new HandlerThread("recorder");
        mThread.start();
        mHandler = new Handler(mThread.getLooper());
    }

    public void startRecord() {
        if (mRecording) {
            Log.e(TAG, "record has started");
            return;
        }
        mRecording = true;
        mHandler.postDelayed(mRecordRunnable, 1000);
    }

    private Runnable mRecordRunnable = new Runnable() {
        public void run() {
            Log.d(TAG, "start thread record");
            String command = "getevent -q -c 10000";
            mEvents.clear();
            BufferedReader resultReader = null;
            try {
                mProcess = Runtime.getRuntime().exec(ShellUtils.COMMAND_SU);
                mOutStream = new DataOutputStream(mProcess.getOutputStream());
                mOutStream.write(command.getBytes());
                mOutStream.writeBytes(ShellUtils.COMMAND_LINE_END);
                mOutStream.flush();
                mOutStream.writeBytes(ShellUtils.COMMAND_EXIT);
                mOutStream.flush();
                // new Thread(mIdleRunnable).start();
                resultReader = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
                String line = "";
                while (mRecording && (line = resultReader.readLine()) != null) {
                    appendEvent(line);
                }
                int c = mProcess.waitFor();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (mOutStream != null) {
                        mOutStream.close();
                        mOutStream = null;
                    }
                    if (resultReader != null) {
                        resultReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (mProcess != null) {
                    mProcess.destroy();
                }
            }
            Log.d(TAG, "end record thread");
        }
    };
    private Runnable mIdleRunnable = new Runnable() {

        @Override
        public void run() {
            while (mRecording) {
                if (mOutStream != null) {
                    try {
                        Log.d(TAG, "write a idle msg");
                        String command = "input keyevent -1";
                        mOutStream.write(command.getBytes());
                        mOutStream.writeBytes(ShellUtils.COMMAND_LINE_END);
                        mOutStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    public void stopRecord() {
        mRecording = false;
        synchronized (mLock) {
            mLock.notifyAll();
        }
        if (mProcess != null) {
            mProcess.destroy();
        }
    }

    @SuppressLint("NewApi")
    public void release() {
        mThread.quitSafely();
    }

    private void appendEvent(String raw) {
        String record = buildEventRecord(raw);
        Log.d(TAG, record);
        mEvents.add(record);
    }

    private String buildEventRecord(String raw) {
        String raws[] = raw.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < raws.length; i++) {
            String string = raws[i];
            sb.append(" ");
            if (i > 0) {
                sb.append(" ");
                sb.append(Long.parseLong(string, 16));
            }
        }
        return sb.toString();
    }

    public void playback() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                List<String> commands = new ArrayList<String>();
                for (String string : mEvents) {
                    commands.add(TouchEventCreater.getLineEvent(string));
                }
                ShellUtils.execCommand(commands, true);
            }
        });

    }
}
