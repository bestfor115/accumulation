package com.ipanel.join.huawei.vod.control;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.TimeZone;

import android.util.Log;

import com.ipanel.join.sx.vodcontrol.Logger;




public class MProtocol implements Logger{
    
	//Msg type value
	public static final int MPLAYER_PREPAREPLAY_SUCCESS = 0;
	public static final int MPLAYER_CONNECT_FAILED = 1;
	public static final int MPLAYER_PLAY_SUCCESS = 2;
	public static final int MPLAYER_PLAY_FAILED = 3;
	public static final int MPLAYER_PROGRAM_BEGIN = 4;
	public static final int MPLAYER_PROGRAM_END = 5;
	public static final int MPLAYER_RELEASE_SUCCESS = 7;
	public static final int MPLAYER_RELEASE_FAIL = 8;
	public static final int MPLAYER_OUT_OF_RANGE = 6;
	public static final int MPLAYER_SET_MEDIA_INFO = 9;
	
	//Property type vlaue
	public static final int MPLAYER_PROP_DURATION = 2;
	public static final int MPLAYER_PROP_ELAPSED = 5;	
	
	static {
		System.loadLibrary(JNISO);
	}
	
	int peer = 0;
	int type = 0;
	Callback mCallback;

	MProtocol(Callback callback) {
		mCallback = callback;
		peer = native_init(new WeakReference<MProtocol>(this));
		if (peer == 0)
			throw new RuntimeException("impl error");
	}
	
	protected void open(int type, String url) {
		this.type = type;
		native_open(type, url);
	}

	protected boolean close() {
		return native_close();
	}

	protected boolean play(int speed) {
		return native_play(speed);
	}

	protected boolean fastForward(int speed) {
		return play(speed);
	}
	
	protected boolean backForward(int speed) {
		return play(speed);
	}

	protected boolean seek(long position) {
		return native_seek(convertTime(position));
	}

	private String convertTime(long position) {
		if (type == 3) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(position * 1000);
			String tStr = String.format("%d%02d%02dT%02d%02d%02dZ",
					cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
					cal.get(Calendar.DAY_OF_MONTH),
					cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
					cal.get(Calendar.SECOND));
			Log.d(TAG, "convertTime: " + position + " --> " + tStr);
			return tStr;
		}
		return Long.toString(position);
	}

	protected boolean pause() {
		return native_pause();
	}

	protected boolean stop() {
		return native_stop(0);
	}
	
	protected long getProperties(int p) {
		String s = native_get(p);
		Log.d(TAG, String.format("Native get p %d, value %s", p, s));
		try {
			return Long.parseLong(s);
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		close();
		try {
			native_exit(peer);
		} catch (Exception e) {
		}
		super.finalize();
	};

	@SuppressWarnings("unchecked")
	private static final void native_callback(Object o, int msg, int opt, int flag, String s) {
		Log.d(TAG, "Native Callback msg "+msg);
		if (o instanceof WeakReference) {
			WeakReference<MProtocol> r = (WeakReference<MProtocol>) o;
			MProtocol p = r.get();
			Log.d(TAG, "Native Callback protocol "+p);
			if (p != null && p.mCallback != null) {
				p.mCallback.notifyCallback(msg, opt, s);
			}
		}
	}

	/**
	 * 实例化 播控协议
	 * 
	 * @param wo
	 * @return
	 */
	private native int native_init(WeakReference<MProtocol> wo);

	/**
	 * 销毁 播控协议
	 * 
	 * @param peer
	 */
	private native void native_exit(int peer);

	/**
	 * 打开一个播放链接
	 * 
	 * @param url
	 */
	private native void native_open(int type, String url);

	/**
	 * 播放
	 * 
	 * @return
	 */
	private native boolean native_play(int peer);

	/**
	 * 快进
	 * 
	 * @param speed
	 * @return
	 */
	private native boolean native_fastForward(int speed);

	/**
	 * 快退
	 * 
	 * @param speed
	 * @return
	 */
	private native boolean native_backForward(int speed);

	/**
	 * 暂停播放
	 * 
	 * @return
	 */
	private native boolean native_pause();

	/**
	 * Seek
	 * 
	 * @param seek
	 * @return
	 */
	private native boolean native_seek(String seek);

	/**
	 * 停止播放
	 * 
	 * @return
	 */
	private native boolean native_stop(int peer);

	/**
	 * 关闭播放
	 * 
	 * @return
	 */
	private native boolean native_close();

	/**
	 * 获取参数属性
	 * 
	 * @param porp
	 * @return
	 */
	private native String native_get(int porp);
	
	protected interface Callback {

		public void notifyCallback(int msg, int opt, String s);
	}

}
