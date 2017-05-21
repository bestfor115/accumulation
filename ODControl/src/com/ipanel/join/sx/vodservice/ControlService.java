/**
 * ControlService.java
 * TODO
 */
package com.ipanel.join.sx.vodservice;




import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ipanel.join.huawei.vod.control.MControl;
import com.ipanel.join.huawei.vod.control.MProtocol;
import com.ipanel.join.huawei.vod.control.Media;
import com.ipanel.join.huawei.vod.dvb.DVBToolkit;
import com.ipanel.join.huawei.vod.dvb.Filter;
import com.ipanel.join.huawei.vod.dvb.MpegStreamTypes;
import com.ipanel.join.huawei.vod.dvb.PAT;
import com.ipanel.join.huawei.vod.dvb.PAT.Program;
import com.ipanel.join.huawei.vod.dvb.PATFilter;
import com.ipanel.join.huawei.vod.dvb.PMT;
import com.ipanel.join.huawei.vod.dvb.PMT.Stream;
import com.ipanel.join.huawei.vod.dvb.PMTFilter;
import com.ipanel.join.huawei.vod.dvb.Table;
import com.ipanel.join.sx.vodcontrol.Logger;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.net.telecast.FrequencyInfo;
import android.net.telecast.NetworkInterface;
import android.net.telecast.ProgramInfo;
import android.net.telecast.StreamObserver;
import android.net.telecast.TransportManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author Zengk TODO
 */
/*
 * 实现的功能是通过解析协议获得FreqN跟ProgramN并返回，用于客户端的TeeveepManger决定VOD的播放资源,
 * 并提供控制器状态的回调方法
 */
public class ControlService extends Service implements Logger{

	/*播放器连接成功*/
	protected static final int PREPARE_SUCCESS = 1003;
	/*播放器连接失败*/
	protected static final int PREPARE_FAIL = 1004;
	/*视频播放成功 */
	protected static final int PLAY_SUCCESS = 1005;
	/*视频播放失败 */
	protected static final int PLAY_FAIL = 1006;
	/*视频关闭成功 */
	protected static final int CLOSE_SUCCESS = 1007;
	/*视频关闭失败 */
	protected static final int CLOSE_FAIL = 1008;
	/*视频播放开始 */
	protected static final int PLAY_BEGIN = 1011;
	/*视频播放结束 */
	protected static final int PlAY_END = 1012;
	/*播放器准备就绪，客户端可以接受freq数据和program数据*/
	protected static final int PROGRAM_PREPARE = 1013;
	/*视频快进*/
	protected static final int FastForward = 1014;
	/*视频快退*/
	protected static final int FastBackward = 1015;
	
	String UUID = "284c19b9-39aa-45e4-9956-9e15aa6e9168";
	StreamObserver mStreamobserver;
	TransportManager mTransportManager;

	MControl mControl;
	Media mMedia;

	PATFilter mPATFilter;
	PAT mPAT;
	PMTFilter mPMTFilter;
	PMT mPMT;
	
	String freqN;
	String programN;
	
	
	IControlCallback mCallback=null;
	ControlImpl mControlImpl;
	int flag=-1;
	
	public class ControlImpl extends IControl.Stub
	{
	
		@Override
		public void setCallback(IControlCallback callback) throws RemoteException
		{
			mCallback=callback;
		}
		
		@Override
		public void prepare(int type, String url) throws RemoteException {
			Log.d(TAG, "Control prepare: "+url);
			mControl.open(type+1, Uri.parse(url));
			flag=type;

			//For UI simulate test
			// Media m = new
			// Media("{frequency:443000000,symbolrate:68750,qam:3,service_id:160,pmt_pid:1}");
			// mControlCallback.notifyCallback(MProtocol.MPLAYER_PREPAREPLAY_SUCCESS,
			// 0, m);
		}
		
		@Override
		public void play() throws RemoteException {
			Log.d(TAG, "Control play");
			mControl.play();
		}

		@Override
		public void pause() throws RemoteException {
			Log.d(TAG, "Control pause");
			mControl.pause();
		}

		@Override
		public void fastForward() throws RemoteException {
			Log.d(TAG, "Control fast forward");
			mControl.fastForward();
		}

		@Override
		public void backForward() throws RemoteException {
			Log.d(TAG, "Control back forward");
			mControl.backForward();
		}
		@Override
		public void stop() throws RemoteException {
			Log.d(TAG, "Control stop");
			mControl.stop();
		}


		@Override
		public void close() throws RemoteException {
			Log.d(TAG, "Control close");
			mControl.close();
		}

	

		@Override
		public long getElapsed() throws RemoteException {
			Log.d(TAG, "Control get Elapsed");
			return mControl.getElapsed();
		}

		@Override
		public long getDuration() throws RemoteException {
			Log.d(TAG, "Control get duration");
			return mControl.getDuration();
		}
		

		@Override
		public void seekTo(long time) throws RemoteException {
			Log.d(TAG, "Control seek to "+ time);
			mControl.seek(time);
		}

		@Override
		public int getSpeed() throws RemoteException {
			return mControl.getSpeed();
		}

		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i("测试","onBind");
		return mControlImpl;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		mCallback=null;
		return super.onUnbind(intent);
	}


	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i("测试","onCreate");
		mControl = MControl.getInstance();
		mControl.setCallback(mControlCallback);
		Log.i("测试","getInstance()");
		mTransportManager = TransportManager
				.getInstance(getApplicationContext());
		mStreamobserver = mTransportManager.createObserver(UUID);
		mStreamobserver.setStreamStateListener(mStreamCallback);
		mPATFilter = new PATFilter(mTransportManager.createFilter(UUID, 0));
		mPATFilter.setOnFilterCallback(mFileCallback);
		mPMTFilter = new PMTFilter(mTransportManager.createFilter(UUID, 0));
		mPMTFilter.setOnFilterCallback(mFileCallback);
		mControlImpl=new ControlImpl();
//		///以下是固定数据测试
//		ProgramInfo mProgramInfo = new ProgramInfo();
//		mProgramInfo.setProgramNumber(3102);//设置节目号
//		
//		mProgramInfo.setAudioPID(81);
//		mProgramInfo.setAudioStreamType("mpeg2_audio");
//		mProgramInfo.setVideoPID(161);
//		mProgramInfo.setVideoStreamType("mpeg2_video");
//		mProgramInfo.setCARequired(false);
//		mProgramInfo.setPcrPID(161);//需要的节目号
//		String prog = mProgramInfo.toString();
//		programN=prog;
//		FrequencyInfo mMediaFreqI= new FrequencyInfo(NetworkInterface.DELIVERY_CABLE);
//		mMediaFreqI.setParameter(//设置固定
//				FrequencyInfo.SYMBOL_RATE, "6875000");
//		mMediaFreqI.setParameter(
//				FrequencyInfo.MODULATION, "QAM64");
//		mMediaFreqI.setParameter(
//				FrequencyInfo.FREQUENCY, "259000000");//这里三个值
//		freqN=mMediaFreqI.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	

	Filter.OnFilterCallback mFileCallback = new Filter.OnFilterCallback() {
		@SuppressWarnings({ "deprecation"})
		@Override
		public void onMessage(Filter f, int msg, Table t) {
			if (f == null)
				return;
			if(mMedia == null){
				f.stopFilter();
				return;
			}
			if (f instanceof PATFilter && f == mPATFilter) {
				if (t == null)
					return;
				if (t instanceof PAT) {
					mPAT = (PAT) t;
				}
			} else if (f instanceof PMTFilter && f == mPMTFilter) {
				if (t == null)
					return;
				if (t instanceof PMT) {
					mPMT = (PMT) t;
				}
			}
			
			if (mPATFilter == f && t instanceof PAT) {
				Log.d(TAG, "Find PAT: "+t);
				PAT pat = (PAT) t;
				Program p = null;//pat.getProgram(mMedia.getServiceId());//从media里获得服务id
				for(Program pg : pat.getProgramArray()){
					if(pg.getProgramePID() == mMedia.getPmtId()){
						p = pg;
						break;
					}
				}
				if (p == null) {
					Log.d(TAG, "Failed to find program");
					return;
				}
				Log.d(TAG, "find Progam program number:"+p.getProgramNumber()+" program pid:"+p.getProgramePID());
				mPATFilter.stopFilter();
				mPMTFilter.setPMTPid(p.getProgramePID());
				mPMTFilter.startFilter();

			} else if (mPMTFilter == f && t instanceof PMT) {
				Log.d(TAG, "Find PMT: "+t);
				mPMTFilter.stopFilter();
				PMT pmt = (PMT) t;
				Stream[] s = pmt.getStreamArray();
				ProgramInfo p = new ProgramInfo();
				if (s != null) {
					for (Stream stream : s) {
						String v = MpegStreamTypes.getMPEGStreamComponentTypeName(stream.getStreamStype());
						if (v == null || "".equals(v)) {
							String video = DVBToolkit.checkVaildVideo(stream);
							if (video != null) {
								p.setVideoStreamType(video);
								p.setVideoPID(stream.getElementaryPid());
								continue;
							}
							String audio = DVBToolkit.chekcVaildAuido(stream);
							if (audio != null) {
								p.setAudioStreamType(audio);
								p.setAudioPID(stream.getElementaryPid());
								continue;
							}			
						} else {
							if (MpegStreamTypes.isVideoStreamComponent(v)) {
								p.setVideoStreamType(v);
								p.setVideoPID(stream.getElementaryPid());
								continue;
							} else if (MpegStreamTypes.isAudioStreamComponent(v)) {
								p.setAudioStreamType(v);
								p.setAudioPID(stream.getElementaryPid());
								continue;
							}
						}
					}
				}
				//No CA required for VOD
				//p.setCARequired(true);
				p.setPcrPID(pmt.getPcrPID());
				p.setProgramNumber(pmt.getProgramNumber());
				programN=p.toString();
				
				Log.d(TAG, "Find progarm info: "+programN);
				final String freq=freqN;
				final String program=programN;
				onIMessageData(PROGRAM_PREPARE,program);
				mMedia = null;
				//这里获得节目表的字符串参数
			}
		}

	
	};

	StreamObserver.StreamStateListener mStreamCallback = new StreamObserver.StreamStateListener() {//流观察者，即通知这里改变数据

		/**
		 * 流转为不可达状态
		 * 
		 * @param o
		 *            对象
		 * @param freq
		 *            频率
		 */
		@Override
		public void onStreamAbsent(StreamObserver o, long freq) {
			Log.d(TAG, "Stream absent freq: " + freq);
			if (mMedia != null && mMedia.getFrequency() == freq) {
				if (mPATFilter != null)
					mPATFilter.stopFilter();
				if (mPMTFilter != null)
					mPMTFilter.stopFilter();
			}
		}

		public void onStreamPresent(StreamObserver o, long freq, int size,
				int prevSize) {
			Log.d(TAG, "Stream present freq: " + freq+", size"+size);
			if (mMedia == null)
				return;
			if (mMedia.getFrequency() == freq && size > 0) {
				mPATFilter.setFrequency(freq);
				mPMTFilter.setFrequency(freq);
				mPATFilter.startFilter();
			}
		}

		@Override
		public void onStreamPresent(StreamObserver o, long freq, int size) {
			Log.d(TAG, "Stream present freq: " + freq+", size"+size);
			if (mMedia == null)
				return;
			if (mMedia.getFrequency() == freq && size > 0) {
				mPATFilter.setFrequency(freq);
				mPMTFilter.setFrequency(freq);
				mPATFilter.startFilter();
			}			
		}

		@Override
		public void onStreamPresenting(StreamObserver o, long freq, int size) {
			// TODO Auto-generated method stub
			
		}
	};
	
	@Override
	public void onDestroy() {
		Log.d(TAG,"onDestroy");
		mCallback = null;
		pool.shutdownNow();
		mPATFilter.stopFilter();
		mPMTFilter.stopFilter();
		mStreamobserver.setStreamStateListener(null);
//		mStreamobserver.release();
		pool = null;
		mStreamobserver = null;
		super.onDestroy();
	}

	private ExecutorService pool = Executors.newSingleThreadExecutor();
	public  void onIMessageData(final int message,final String data) {//返回数据的消息
		if (mCallback == null)
			return;
		pool.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Log.d(TAG, String.format("Send message to UI runn %d, %s",message, data));
					if(mCallback != null)
						mCallback.notifyCallback(message,data);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		
		
	}
	public void onIMessage(final int message)//返回状态的消息
	{
	if (mCallback == null)
		return;

	pool.execute(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				if(mCallback != null)
					mCallback.notifyCallback(message,null);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	});
	
}

	private void getFreqN()
	{
		if (mMedia == null)
			return;

		FrequencyInfo mMediaFreqI= new FrequencyInfo(NetworkInterface.DELIVERY_CABLE);
		mMediaFreqI.clear();
		long freq=mMedia.getFrequency();
		mMediaFreqI.setFrequency(freq);
		String sFreq=String.valueOf(mMedia.getFrequency());
		mMediaFreqI.setParameter(FrequencyInfo.FREQUENCY,sFreq);
		String sModul=String.valueOf(mMedia.getModulation());
		mMediaFreqI.setParameter(FrequencyInfo.MODULATION, sModul);
		String sSymbo=String.valueOf(mMedia.getSymbolRate());
		mMediaFreqI.setParameter(FrequencyInfo.SYMBOL_RATE,sSymbo);//这就是播放节点的参数
//		mMediaFreqI.setParameter(//设置固定
//				FrequencyInfo.SYMBOL_RATE, "6875000");
//		mMediaFreqI.setParameter(
//				FrequencyInfo.MODULATION, "QAM64");
//		mMediaFreqI.setParameter(
//				FrequencyInfo.FREQUENCY, "259000000");//这里三个值
		freqN=mMediaFreqI.toString();
		
	}

	//可以先测试
	MControl.Callback mControlCallback = new MControl.Callback() {//播放回调

		@Override
		public void notifyCallback(int msg, int opt, Object s) {
			Log.d(TAG, String.format("Control callback msg %d, opt %d, Object %s",msg, opt, s));
			switch (msg) {
			case MProtocol.MPLAYER_PREPAREPLAY_SUCCESS:
				mControl.play();
				break;
			case MProtocol.MPLAYER_SET_MEDIA_INFO:
				mMedia=(Media) s;
				//回调消息-连接成功,客户端使用Handler获得回调消息通知线程执行对应方法
				getFreqN();
				onIMessageData(PREPARE_SUCCESS, freqN);
			    break;
		    case MProtocol.MPLAYER_CONNECT_FAILED:
		    	
					onIMessageData(PREPARE_FAIL,(String)s);//回调消息-连接失败
				
				break;
			case MProtocol.MPLAYER_PLAY_SUCCESS:
				
					onIMessage(PLAY_SUCCESS);//回调消息-播放成功
			    break;
			case MProtocol.MPLAYER_PLAY_FAILED:
					onIMessage(PLAY_FAIL);//回调消息-播放失败
			
				break;
				
	
			case MProtocol.MPLAYER_RELEASE_SUCCESS:
	
					onIMessage(CLOSE_SUCCESS);//回调消息-关闭成功

					break;
			case MProtocol.MPLAYER_RELEASE_FAIL:
					onIMessage(CLOSE_FAIL);//回调消息-关闭失败		
				break;
		
			case MProtocol.MPLAYER_PROGRAM_BEGIN:
				onIMessage(PLAY_BEGIN);//回调消息-播放开始
				break;
			case MProtocol.MPLAYER_PROGRAM_END:
				onIMessage(PlAY_END);//回调消息-播放结束
				break;
			default:
				break;
			}
		}
	};
}