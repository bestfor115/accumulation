/******************************************************************************/
/*    Copyright (c) 2005 iPanel Technologies, Ltd.                     */
/*    All rights reserved. You are not allowed to copy or distribute          */
/*    the code without permission.                                            */
/*    This is the demo implenment of the Porting APIs needed by iPanel        */
/*    MiddleWare.                                                             */
/*    Maybe you should modify it accorrding to Platform.                      */
/*                                                                            */
/*    $author huzh 2007/11/19                                                 */
/******************************************************************************/

#ifndef _IPANEL_MIDDLEWARE_PORTING_AUDIO_DECODER_API_FUNCTOTYPE_H_
#define _IPANEL_MIDDLEWARE_PORTING_AUDIO_DECODER_API_FUNCTOTYPE_H_

#include "ipanel_porting.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum
{
    IPANEL_AUDIO_CODEC_DEFAULT,               /* don’t assigned by iPanel Middleware*/
    IPANEL_AUDIO_CODEC_MPEG			= 0x3,    /* MPEG1/2, layer 1/2. */
    IPANEL_AUDIO_CODEC_MP3			= 0x4,    /* MPEG1/2, layer 3. */
    IPANEL_AUDIO_CODEC_AAC			= 0xF,    /* Advanced audio coding. Part of MPEG-4 */
    IPANEL_AUDIO_CODEC_AAC_PLUS		= 0x11,   /* AAC plus SBR. aka MPEG-4 High Efficiency (AAC-HE)*/
    IPANEL_AUDIO_CODEC_AC3			= 0x81,   /* Dolby Digital AC3 audio */
    IPANEL_AUDIO_CODEC_AC3_PLUS		= 0x6,    /* Dolby Digital Plus (AC3+ or DDP) audio */
    IPANEL_AUDIO_CODEC_WMAPRO		= 0x7,
    IPANEL_AUDIO_CODEC_WMASTD		= 0x8,
    IPANEL_AUDIO_CODEC_DTS			= 0x9,
    IPANEL_AUDIO_CODEC_DTSHD		= 0xa,
    IPANEL_AUDIO_CODEC_LPCMDVD		= 0xb,
    IPANEL_AUDIO_CODEC_LPCMHDDVD	= 0xc,
    IPANEL_AUDIO_CODEC_LPCMBLURAY	= 0xd,
    IPANEL_AUDIO_CODEC_PCM			= 0xe,
    IPANEL_AUDIO_CODEC_PCMWAV		= 0x20,
    IPANEL_AUDIO_CODEC_RA144        = 0x30,
	IPANEL_AUDIO_CODEC_RA288        = 0x31,
	IPANEL_AUDIO_CODEC_COOK         = 0x32,
	IPANEL_AUDIO_CODEC_SIPR         = 0x33,
	IPANEL_AUDIO_CODEC_ATRAC3       = 0x34,
	IPANEL_AUDIO_CODEC_VORBIS		= 0x40,
	IPANEL_AUDIO_CODEC_UNDEFINED
} IPANEL_ADEC_AUDIO_FORMAT_e;

typedef enum
{
    IPANEL_AUDIO_MODE_STEREO            = 0,   // 立体声
    IPANEL_AUDIO_MODE_LEFT_MONO         = 1,   // 左声道
    IPANEL_AUDIO_MODE_RIGHT_MONO        = 2,   // 右声道
    IPANEL_AUDIO_MODE_MIX_MONO          = 3,   // 左右声道混合
    IPANEL_AUDIO_MODE_STEREO_REVERSE    = 4    // 立体声，左右声道反转
} IPANEL_ADEC_CHANNEL_OUT_MODE_e;

typedef enum
{
    IPANEL_ADEC_SET_SOURCE       	= 1,
    IPANEL_ADEC_START           	= 2,
    IPANEL_ADEC_STOP              	= 3,
    IPANEL_ADEC_PAUSE             	= 4,
    IPANEL_ADEC_RESUME            	= 5,
    IPANEL_ADEC_CLEAR            	= 6,
    IPANEL_ADEC_SYNCHRONIZE       	= 7,
    IPANEL_ADEC_SET_CHANNEL_MODE   	= 8,
    IPANEL_ADEC_SET_MUTE           	= 9,
    IPANEL_ADEC_SET_PASS_THROUGH  	= 10,
    IPANEL_ADEC_SET_VOLUME        	= 11,
    IPANEL_ADEC_GET_BUFFER_RATE    	= 12,
    IPANEL_ADEC_SET_SYNC_OFFSET  	= 13,
    IPANEL_ADEC_GET_BUFFER        	= 14,
    IPANEL_ADEC_PUSH_STREAM       	= 15,
    IPANEL_ADEC_GET_BUFFER_CAP   	= 16,
    IPANEL_ADEC_SET_CONFIG_PARAMS 	= 17,
    IPANEL_ADEC_GET_CURRENT_PTS  	= 18,
	IPANEL_ADEC_SET_DEVICE_OUTPUT   = 19,
	IPANEL_ADEC_SET_CODEC_TYPE		= 20,
	IPANEL_ADEC_SET_LOCAL_CONFIG	= 21,
	IPANEL_ADEC_GET_RECV_FRAME_NUM	= 22,		//获取解码器从star开始到stop为止接收到的音频帧总个数
	IPANEL_ADEC_GET_BIT_RATE		= 23,		//获取解码码率
	IPANEL_ADEC_GET_MUTE			= 24,
	IPANEL_ADEC_UNDEFINED
} IPANEL_ADEC_IOCTL_e;

typedef struct
{
    UINT32_T valid;         //当前音频配置是否有效
    UINT32_T audio_pid;     //下发视频对应codec种类，与IPANEL_ADEC_START中附带参数相同
    UINT32_T codec_id;      //CODECID, 为对齐字节采用32BIT，将来可用于外部查询
    UINT16_T channelcount;  //声道数量
    UINT16_T samplesize;    //采样宽度
    UINT32_T samplerate;    //采样频率
    UINT32_T profile;       //一般适合AAC音频 用于区分版本
    UINT32_T private_len;   //一般用于WMA音频，其他暂时未知，可用于部分解码器私有数据兼容
    BYTE_T   private_data[512];
    UINT32_T stream_type;   //当前数据的种类，对应IPANEL_XMEM_PAYLOAD_TYPE_e，用于部分平台要详细区分注入数据的种类
}IPANEL_ADEC_PARAM;

/* audio decode */
/*打开一个解码单元*/
UINT32_T ipanel_porting_adec_open(VOID);

/*关闭指定的解码单元*/
INT32_T ipanel_porting_adec_close(UINT32_T decoder);

/*对decoder进行一个操作，或者用于设置和获取decoder设备的参数和属性*/
INT32_T ipanel_porting_adec_ioctl(UINT32_T decoder, IPANEL_ADEC_IOCTL_e op, VOID *arg);

#ifdef __cplusplus
}
#endif

#endif // _IPANEL_MIDDLEWARE_PORTING_AUDIO_DECODER_API_FUNCTOTYPE_H_

