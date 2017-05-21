/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
����:�Խӵ�����Android��������API�ӿ�
*********************************************************************/
#ifndef __IPANEL_MEDIAPROCESSOR_H_
#define __IPANEL_MEDIAPROCESSOR_H_

#ifdef __cplusplus
extern "C"{
#endif

#include "ipanel_adec.h"
#include "ipanel_vdec.h"



/*�򿪲�����������*/
typedef enum
{
    MEDIAPROCESSOR_TYPE_NONE,
    MEDIAPROCESSOR_TYPE_TS,
    MEDIAPROCESSOR_TYPE_ES
    
}MEDIAPROCESSOR_OPEN_TYPE;


/*��ȡ�����ò��ŵĲ���*/
typedef enum
{
    MEDIAPROCESSOR_PROP_GET_VERSION = 630,     //��ȡ�ײ�ģ��ʵ�ֵĽӿڰ汾��
    MEDIAPROCESSOR_PROP_GET_PLAYMODE,          //��ȡ����ģʽ
    MEDIAPROCESSOR_PROP_GET_VIDEOPIXELS,       //��ȡ��Ƶϵͳ��ʾ�ֱ��ʣ����ǵ�ǰӰƬ�ķֱ���
    MEDIAPROCESSOR_PROP_GET_SOFTFIT,           //��ȡEPG�Ƿ���������죬�����Ӳ�����죬����false;���򷵻�true
    MEDIAPROCESSOR_PROP_GET_AUDIOBALANCE,      //��ȡ��ǰ������1:��������2:��������3:˫���� 
    MEDIAPROCESSOR_PROP_GET_MUTE = 635,        //��ȡ����
    MEDIAPROCESSOR_PROP_GET_VOLUME,            //��ȡ������С

    MEDIAPROCESSOR_PROP_SET_AUDIOBALANCE,      //����������1:��������2:��������3:˫����
    MEDIAPROCESSOR_PROP_SET_MUTE,              //���þ���
    MEDIAPROCESSOR_PROP_SET_VOLUME,            //����������С
    MEDIAPROCESSOR_PROP_SET_RATIO = 640,       //������Ƶ��ʾ����
    MEDIAPROCESSOR_PROP_SET_VIDEOWINDOW,       //������Ƶ����λ��
    MEDIAPROCESSOR_PROP_SET_VIDEOVISIBLE,      //������Ƶ�Ƿ�ɼ���0��ʾ���ɼ���1��ʾ�ɼ�
    MEDIAPROCESSOR_PROP_SET_EPGSIZE = 643,     //���� EPG ��С������̶� 640*530�������� 1280*720���ڸ���ƽ̨����Щҳ�滹�� 640*530������
                                               //ʱ�������ֱַ������л�������Ҫ�������ݲ�ͬ�ֱ������������졣��IsSoftFit ����  true ʱ���ýӿڲ���
                                               //ʵ��
                                               
    MEDIAPROCESSOR_PROP_SET_SURFACE,           //����Android 4.0��surface �������������Ӷ���ʾ��Ƶ���档Surface�� Androidͼ�λ�
                                               //�ƵĽṹ���䶨�����Android ͷ�ļ�

    MEDIAPROCESSOR_PROP_NONE
    
}MEDAIPROCESSOR_PROP_TYPE;


/*��Ƶ�����ʽ*/
typedef enum
{
	MEDIAPROCESSOR_VIDEO_TYPE_NONE,
	MEDIAPROCESSOR_VIDEO_TYPE_MPEG1,
	MEDIAPROCESSOR_VIDEO_TYPE_MPEG2,
	MEDIAPROCESSOR_VIDEO_TYPE_H264,
	MEDIAPROCESSOR_VIDEO_TYPE_AVS,
	MEDIAPROCESSOR_VIDEO_TYPE_WMV,
	MEDIAPROCESSOR_VIDEO_TYPE_MPEG4
}MEDIAPROCESSOR_VIDEO_TYPE;


/*��Ƶ�����ʽ*/
typedef enum
{
	MEDIAPROCESSOR_AUDIO_TYPE_NONE,
	MEDIAPROCESSOR_AUDIO_TYPE_MPEG1,
	MEDIAPROCESSOR_AUDIO_TYPE_MPEG2,
	MEDIAPROCESSOR_AUDIO_TYPE_MP3,
	MEDIAPROCESSOR_AUDIO_TYPE_AAC_1,        //0x0f
	MEDIAPROCESSOR_AUDIO_TYPE_AAC_2,        //0x80
	MEDIAPROCESSOR_AUDIO_TYPE_AAC_3,        //0x11
	MEDIAPROCESSOR_AUDIO_TYPE_AC3_1,        //0x1b
	MEDIAPROCESSOR_AUDIO_TYPE_AC3_2,        //0x81
	MEDIAPROCESSOR_AUDIO_TYPE_AC3_3,        //0x06
	
    MEDIAPROCESSOR_AUDIO_TYPE_DTS,	
	MEDIAPROCESSOR_AUDIO_TYPE_PCM,	
	MEDIAPROCESSOR_AUDIO_TYPE_LPCMBLURAY

}MEDIAPROCESSRO_AUDIO_TYPE;


/*��Ƶ����λ��*/
typedef struct
{
    int x;
    int y;
    int w;
    int h;
}mediaProcessor_rect;

/*�򿪵ײ�һ��������*/
int
ipanel_mediaProcessor_open(int type);

#if 0
/*��������Ƶ���Ų���*/
int
ipanel_mediaProcessor_set_param(int handle,IPANEL_VDEC_PARAM *vParam,IPANEL_ADEC_PARAM *aParam);
#else
/*��������Ƶȫ�����Ų���*/
int
ipanel_mediaProcessor_set_param(int handle,IPANEL_VDEC_PARAM *vParam,IPANEL_ADEC_PARAM *aParam,int adec_param_len);
#endif

/*������Ƶ*/
int
ipanel_mediaProcessor_play(int handle);

/*��ͣ����*/
int
ipanel_mediaProcessor_pause(int handle);

/*�ָ�����*/
int
ipanel_mediaProcessor_resume(int handle);

/*seek����*/
int
ipanel_mediaProcessor_seek(int handle);

/*������˲���*/
int
ipanel_mediaProcessor_fast(int handle,int rate);

/*�������ת��������*/
int
ipanel_mediaProcessor_stopFast(int handle);

/*ֹͣ����*/
int
ipanel_mediaProcessor_stop(int handle,int value);

/*�رղ�����*/
int
ipanel_mediaProcessor_close(int handle);

/*��ײ������ע��������*/
int
ipanel_mediaProcessor_push_stream(int handle,unsigned char* buffer,int len);

/*�л�������Ϣ*/
int 
ipanel_mediaProcessor_switch_AudioTrack(int handle, int pid);

/*��ȡ����*/
int
ipanel_mediaProcessor_get_property(int handle,int prop,void *value);

/*���ò���*/
int
ipanel_mediaProcessor_set_property(int handle,int prop,int value);



#ifdef __cplusplus
}
#endif

#endif

/************************************End Of File**********************************/

