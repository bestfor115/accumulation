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
    IPANEL_AUDIO_CODEC_DEFAULT,               /* don��t assigned by iPanel Middleware*/
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
    IPANEL_AUDIO_MODE_STEREO            = 0,   // ������
    IPANEL_AUDIO_MODE_LEFT_MONO         = 1,   // ������
    IPANEL_AUDIO_MODE_RIGHT_MONO        = 2,   // ������
    IPANEL_AUDIO_MODE_MIX_MONO          = 3,   // �����������
    IPANEL_AUDIO_MODE_STEREO_REVERSE    = 4    // ������������������ת
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
	IPANEL_ADEC_GET_RECV_FRAME_NUM	= 22,		//��ȡ��������star��ʼ��stopΪֹ���յ�����Ƶ֡�ܸ���
	IPANEL_ADEC_GET_BIT_RATE		= 23,		//��ȡ��������
	IPANEL_ADEC_GET_MUTE			= 24,
	IPANEL_ADEC_UNDEFINED
} IPANEL_ADEC_IOCTL_e;

typedef struct
{
    UINT32_T valid;         //��ǰ��Ƶ�����Ƿ���Ч
    UINT32_T audio_pid;     //�·���Ƶ��Ӧcodec���࣬��IPANEL_ADEC_START�и���������ͬ
    UINT32_T codec_id;      //CODECID, Ϊ�����ֽڲ���32BIT�������������ⲿ��ѯ
    UINT16_T channelcount;  //��������
    UINT16_T samplesize;    //�������
    UINT32_T samplerate;    //����Ƶ��
    UINT32_T profile;       //һ���ʺ�AAC��Ƶ �������ְ汾
    UINT32_T private_len;   //һ������WMA��Ƶ��������ʱδ֪�������ڲ��ֽ�����˽�����ݼ���
    BYTE_T   private_data[512];
    UINT32_T stream_type;   //��ǰ���ݵ����࣬��ӦIPANEL_XMEM_PAYLOAD_TYPE_e�����ڲ���ƽ̨Ҫ��ϸ����ע�����ݵ�����
}IPANEL_ADEC_PARAM;

/* audio decode */
/*��һ�����뵥Ԫ*/
UINT32_T ipanel_porting_adec_open(VOID);

/*�ر�ָ���Ľ��뵥Ԫ*/
INT32_T ipanel_porting_adec_close(UINT32_T decoder);

/*��decoder����һ�������������������úͻ�ȡdecoder�豸�Ĳ���������*/
INT32_T ipanel_porting_adec_ioctl(UINT32_T decoder, IPANEL_ADEC_IOCTL_e op, VOID *arg);

#ifdef __cplusplus
}
#endif

#endif // _IPANEL_MIDDLEWARE_PORTING_AUDIO_DECODER_API_FUNCTOTYPE_H_

