/**
 * MPlayerControl.java
 * TODO
 */
package com.ipanel.join.huawei.vod.control;


import com.ipanel.join.huawei.vod.control.MProtocol;
import com.ipanel.join.sx.vodcontrol.Logger;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * @author Zengk
 * 2012-9-6 上午11:09:11
 * TODO
 */
public class MControl implements Logger {

	static MControl instance;
	public synchronized static MControl getInstance() {
		if (instance == null) {
			instance = new MControl();
		}
		return instance;
	}

	public static final int FAST_MAX_SPEED = 32;
	public static final int FAST_MIN_SPEED = 2;
	public static final int BACK_MAX_SPEED = -32;
	public static final int BACK_MIN_SPEED = -2;
	public static final int NOL_SPEED = 1;

	int speed = 1;
	MProtocol mMProtocol;

	Handler mHandler;
	Looper mLooper;

	Callback mCallback;
	
	MControl() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				mHandler = new Handler();
				mLooper = Looper.myLooper();
				Looper.loop();
			}
		}).start();
		mMProtocol = new MProtocol(onProtocolCallback);
	}
	
	public void setCallback(Callback callback) {
		mCallback = callback;
	}

	public void open(final int type, final Uri uri) {
		postRunnable(new Runnable() {
			@Override
			public void run() {
				Log.d(TAG, "MControl.Open.Uri = " + uri.toString());
				mMProtocol.open(type, uri.toString());
			}
		});
	}

	public void stop() {
		postRunnable(new Runnable() {
			@Override
			public void run() {
				Log.d(TAG, "MControl.Stop");
				mMProtocol.stop();
			}
		});
	}

	public void close() {
		postRunnable(new Runnable() {
			@Override
			public void run() {
				Log.d(TAG, "MControl.Close");
				mMProtocol.close();
				Log.d(TAG, "MControl.Close.Protocol.close");
			}
		});
	}

	public void play() {
		postRunnable(new Runnable() {
			@Override
			public void run() {
				Log.d(TAG, "MControl.Play");
				speed = NOL_SPEED;
				mMProtocol.play(speed);
			}
		});
	}

	public void pause() {
		postRunnable(new Runnable() {
			@Override
			public void run() {
				Log.d(TAG, "MControl.Pause");
				mMProtocol.pause();
			}
		});
	}

	public void seek(final long time) {
		postRunnable(new Runnable() {
			@Override
			public void run() {
				Log.d(TAG, "MControl.Seed.Time = " + time);
				mMProtocol.seek(time);
			}
		});
	}

	public void fastForward() {
		postRunnable(new Runnable() {
			@Override
			public void run() {
				if (speed >= FAST_MAX_SPEED)
					return;
				Log.d(TAG, "MControl.fastForward.Speed Old = " + speed);
				if (speed < FAST_MIN_SPEED) {
					speed = FAST_MIN_SPEED;
				} else {
					speed <<= 1;
				}
				Log.d(TAG, "MControl.fastForward.Speed New = " + speed);
				mMProtocol.fastForward(speed);
			}
		});
	}

	public void backForward() {
		postRunnable(new Runnable() {
			@Override
			public void run() {
				if (speed <= BACK_MAX_SPEED)
					return;
				Log.d(TAG, "MControl.backForward.Speed Old = " + speed);
				if (speed > BACK_MIN_SPEED) {
					speed = BACK_MIN_SPEED;
				} else {
					speed <<= 1;
				}
				Log.d(TAG, "MControl.backForward.Speed New = " + speed);
				mMProtocol.backForward(speed);
			}
		});
	}
	
	public int getSpeed(){
		return speed;
	}

	/**
	 * 返回影片的长度，单位毫秒
	 * 
	 * @return
	 */
	public long getDuration() {
		return mMProtocol.getProperties(MProtocol.MPLAYER_PROP_DURATION);
	}

	/**
	 * 返回影片的当前播放进度，单位毫秒
	 * 
	 * @return
	 */
	public long getElapsed() {
		return mMProtocol.getProperties(MProtocol.MPLAYER_PROP_ELAPSED);
	}

	private synchronized void postRunnable(Runnable r) {
		while (mHandler == null) {
		}
		mHandler.post(r);
	}

	public interface Callback {

		public void notifyCallback(int msg, int opt, Object s);
	}

	MProtocol.Callback onProtocolCallback = new MProtocol.Callback() {
		@Override
		public void notifyCallback(int msg, int opt, String s) {
			Log.d(TAG, "protocl callback "+ msg);
			if (msg == MProtocol.MPLAYER_SET_MEDIA_INFO) {//如果准备好了，那么就是穿Media回去
				if (mCallback != null) {
					mCallback.notifyCallback(msg, opt, new Media(s));//这里重写传给
				}
			} else {
				if (mCallback != null) {
					mCallback.notifyCallback(msg, opt, s);//否则传的是Media的json字符串
				}
			}
		}
	};
}