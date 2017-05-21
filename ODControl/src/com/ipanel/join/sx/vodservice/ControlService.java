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
 * ʵ�ֵĹ�����ͨ������Э����FreqN��ProgramN�����أ����ڿͻ��˵�TeeveepManger����VOD�Ĳ�����Դ,
 * ���ṩ������״̬�Ļص�����
 */
public class ControlService extends Service implements Logger{

	/*���������ӳɹ�*/
	protected static final int PREPARE_SUCCESS = 1003;
	/*����������ʧ��*/
	protected static final int PREPARE_FAIL = 1004;
	/*��Ƶ���ųɹ� */
	protected static final int PLAY_SUCCESS = 1005;
	/*��Ƶ����ʧ�� */
	protected static final int PLAY_FAIL = 1006;
	/*��Ƶ�رճɹ� */
	protected static final int CLOSE_SUCCESS = 1007;
	/*��Ƶ�ر�ʧ�� */
	protected static final int CLOSE_FAIL = 1008;
	/*��Ƶ���ſ�ʼ */
	protected static final int PLAY_BEGIN = 1011;
	/*��Ƶ���Ž��� */
	protected static final int PlAY_END = 1012;
	/*������׼���������ͻ��˿��Խ���freq���ݺ�program����*/
	protected static final int PROGRAM_PREPARE = 1013;
	/*��Ƶ���*/
	protected static final int FastForward = 1014;
	/*��Ƶ����*/
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
		Log.i("����","onBind");
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
		Log.i("����","onCreate");
		mControl = MControl.getInstance();
		mControl.setCallback(mControlCallback);
		Log.i("����","getInstance()");
		mTransportManager = TransportManager
				.getInstance(getApplicationContext());
		mStreamobserver = mTransportManager.createObserver(UUID);
		mStreamobserver.setStreamStateListener(mStreamCallback);
		mPATFilter = new PATFilter(mTransportManager.createFilter(UUID, 0));
		mPATFilter.setOnFilterCallback(mFileCallback);
		mPMTFilter = new PMTFilter(mTransportManager.createFilter(UUID, 0));
		mPMTFilter.setOnFilterCallback(mFileCallback);
		mControlImpl=new ControlImpl();
//		///�����ǹ̶����ݲ���
//		ProgramInfo mProgramInfo = new ProgramInfo();
//		mProgramInfo.setProgramNumber(3102);//���ý�Ŀ��
//		
//		mProgramInfo.setAudioPID(81);
//		mProgramInfo.setAudioStreamType("mpeg2_audio");
//		mProgramInfo.setVideoPID(161);
//		mProgramInfo.setVideoStreamType("mpeg2_video");
//		mProgramInfo.setCARequired(false);
//		mProgramInfo.setPcrPID(161);//��Ҫ�Ľ�Ŀ��
//		String prog = mProgramInfo.toString();
//		programN=prog;
//		FrequencyInfo mMediaFreqI= new FrequencyInfo(NetworkInterface.DELIVERY_CABLE);
//		mMediaFreqI.setParameter(//���ù̶�
//				FrequencyInfo.SYMBOL_RATE, "6875000");
//		mMediaFreqI.setParameter(
//				FrequencyInfo.MODULATION, "QAM64");
//		mMediaFreqI.setParameter(
//				FrequencyInfo.FREQUENCY, "259000000");//��������ֵ
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
				Program p = null;//pat.getProgram(mMedia.getServiceId());//��media���÷���id
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
				//�����ý�Ŀ����ַ�������
			}
		}

	
	};

	StreamObserver.StreamStateListener mStreamCallback = new StreamObserver.StreamStateListener() {//���۲��ߣ���֪ͨ����ı�����

		/**
		 * ��תΪ���ɴ�״̬
		 * 
		 * @param o
		 *            ����
		 * @param freq
		 *            Ƶ��
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
	public  void onIMessageData(final int message,final String data) {//�������ݵ���Ϣ
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
	public void onIMessage(final int message)//����״̬����Ϣ
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
		mMediaFreqI.setParameter(FrequencyInfo.SYMBOL_RATE,sSymbo);//����ǲ��Žڵ�Ĳ���
//		mMediaFreqI.setParameter(//���ù̶�
//				FrequencyInfo.SYMBOL_RATE, "6875000");
//		mMediaFreqI.setParameter(
//				FrequencyInfo.MODULATION, "QAM64");
//		mMediaFreqI.setParameter(
//				FrequencyInfo.FREQUENCY, "259000000");//��������ֵ
		freqN=mMediaFreqI.toString();
		
	}

	//�����Ȳ���
	MControl.Callback mControlCallback = new MControl.Callback() {//���Żص�

		@Override
		public void notifyCallback(int msg, int opt, Object s) {
			Log.d(TAG, String.format("Control callback msg %d, opt %d, Object %s",msg, opt, s));
			switch (msg) {
			case MProtocol.MPLAYER_PREPAREPLAY_SUCCESS:
				mControl.play();
				break;
			case MProtocol.MPLAYER_SET_MEDIA_INFO:
				mMedia=(Media) s;
				//�ص���Ϣ-���ӳɹ�,�ͻ���ʹ��Handler��ûص���Ϣ֪ͨ�߳�ִ�ж�Ӧ����
				getFreqN();
				onIMessageData(PREPARE_SUCCESS, freqN);
			    break;
		    case MProtocol.MPLAYER_CONNECT_FAILED:
		    	
					onIMessageData(PREPARE_FAIL,(String)s);//�ص���Ϣ-����ʧ��
				
				break;
			case MProtocol.MPLAYER_PLAY_SUCCESS:
				
					onIMessage(PLAY_SUCCESS);//�ص���Ϣ-���ųɹ�
			    break;
			case MProtocol.MPLAYER_PLAY_FAILED:
					onIMessage(PLAY_FAIL);//�ص���Ϣ-����ʧ��
			
				break;
				
	
			case MProtocol.MPLAYER_RELEASE_SUCCESS:
	
					onIMessage(CLOSE_SUCCESS);//�ص���Ϣ-�رճɹ�

					break;
			case MProtocol.MPLAYER_RELEASE_FAIL:
					onIMessage(CLOSE_FAIL);//�ص���Ϣ-�ر�ʧ��		
				break;
		
			case MProtocol.MPLAYER_PROGRAM_BEGIN:
				onIMessage(PLAY_BEGIN);//�ص���Ϣ-���ſ�ʼ
				break;
			case MProtocol.MPLAYER_PROGRAM_END:
				onIMessage(PlAY_END);//�ص���Ϣ-���Ž���
				break;
			default:
				break;
			}
		}
	};
}