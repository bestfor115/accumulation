/**
 * DVBToolkit.java
 * TODO
 */
package com.ipanel.join.huawei.vod.dvb;

import java.util.HashMap;
import java.util.Map;

import com.ipanel.join.huawei.vod.dvb.PMT.Stream;

/**
 * @author Zengk
 * 2012-9-20 ÉÏÎç10:48:56
 * TODO
 */
public class DVBToolkit {

	public static final String QAM_MOD_8 	= "qam8";
	public static final String QAM_MOD_16 	= "qam16";
	public static final String QAM_MOD_32 	= "qam32";
	public static final String QAM_MOD_64 	= "qam64";
	public static final String QAM_MOD_128 	= "qam128";
	public static final String QAM_MOD_256 	= "qam256";

	public static final int QAM_8 = 0;
	public static final int QAM_16 = 1;
	public static final int QAM_32 = 2;
	public static final int QAM_64 = 3;
	public static final int QAM_128 = 4;
	public static final int QAM_256 = 5;

	public static final int VIDEO_MPEG1 		= 0x01;
	public static final int VIDEO_MPEG2 		= 0x02;
	public static final int VIDEO_H264			= 0x1B;
	public static final int VIDEO_AVS			= 0x42;
	public static final int VIDEO_MPEG4			= 0x10;
	public static final int VIDEO_VC1			= 0xEA;

	public static final int AUDIO_MPEG1 		= 0x03;
	public static final int AUDIO_MPEG2 		= 0x04;
	public static final int AUDIO_AC31			= 0x81;
	public static final int AUDIO_AC32			= 0x1B;
	public static final int AUDIO_AC3PLUS 		= 0x11;
	public static final int AUDIO_AAC1			= 0x0F;
	public static final int AUDIO_AAC2			= 0x80;
	public static final int AUDIO_DTS1			= 0x82;
	public static final int AUDIO_DTS2			= 0x86;
	public static final int AUDIO_DOLBY_TRUEHD	= 0x83;

	protected static Map<String, String> QAM;
	static {
		QAM = new HashMap<String, String>();
		QAM.put(String.valueOf(QAM_8), 		QAM_MOD_8);
		QAM.put(String.valueOf(QAM_16), 	QAM_MOD_16);
		QAM.put(String.valueOf(QAM_32), 	QAM_MOD_32);
		QAM.put(String.valueOf(QAM_64), 	QAM_MOD_64);
		QAM.put(String.valueOf(QAM_128), 	QAM_MOD_128);
		QAM.put(String.valueOf(QAM_256), 	QAM_MOD_256);
	}

	public static String switchModulation(int qam) {
		return QAM.get(String.valueOf(qam));
	}

	public static String checkVaildVideo(Stream s) {
		switch (s.streamType) {
		case VIDEO_MPEG1:
			return "mpeg1_video";
		case VIDEO_MPEG2:
			return "mpeg2_video";
		case VIDEO_H264:
			return "h264_video";
		case VIDEO_AVS:
			return "avs_video";
		case VIDEO_MPEG4:
			return "mpeg4_video";
		case VIDEO_VC1:
			return "vc1_video";
		default:
			return null;
		}
	}

	public static String chekcVaildAuido(Stream s) {
		switch (s.streamType) {
		case AUDIO_MPEG1:
			return "mpeg1_audio";
		case AUDIO_MPEG2:
			return "mpeg2_audio";
		case AUDIO_AC31:
			return "ac3_audio";
		case AUDIO_AC32:
			return "ac3_audio";
		case AUDIO_AC3PLUS:
			return "ac3plus_audio";
		case AUDIO_AAC1:
			return "aac_audio";
		case AUDIO_AAC2:
			return "aac_audio";
		case AUDIO_DTS1:
			return "dst_audio";
		case AUDIO_DTS2:
			return "dst_audio";
		case AUDIO_DOLBY_TRUEHD:
			return "dolby_turehd_audio";
		default:
			return null;
		}
	}
}