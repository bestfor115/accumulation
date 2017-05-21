/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:对接第三方Android播放器的API接口
*********************************************************************/
#ifndef __IPANEL_MEDIAPROCESSOR_H_
#define __IPANEL_MEDIAPROCESSOR_H_

#ifdef __cplusplus
extern "C"{
#endif

#include "ipanel_adec.h"
#include "ipanel_vdec.h"



/*打开播放器的类型*/
typedef enum
{
    MEDIAPROCESSOR_TYPE_NONE,
    MEDIAPROCESSOR_TYPE_TS,
    MEDIAPROCESSOR_TYPE_ES
    
}MEDIAPROCESSOR_OPEN_TYPE;


/*获取和设置播放的参数*/
typedef enum
{
    MEDIAPROCESSOR_PROP_GET_VERSION = 630,     //获取底层模块实现的接口版本号
    MEDIAPROCESSOR_PROP_GET_PLAYMODE,          //获取播放模式
    MEDIAPROCESSOR_PROP_GET_VIDEOPIXELS,       //获取视频系统显示分辩率，不是当前影片的分辩率
    MEDIAPROCESSOR_PROP_GET_SOFTFIT,           //获取EPG是否由软件拉伸，如果由硬件拉伸，返回false;否则返回true
    MEDIAPROCESSOR_PROP_GET_AUDIOBALANCE,      //获取当前声道，1:左声道，2:右声道，3:双声道 
    MEDIAPROCESSOR_PROP_GET_MUTE = 635,        //获取静音
    MEDIAPROCESSOR_PROP_GET_VOLUME,            //获取音量大小

    MEDIAPROCESSOR_PROP_SET_AUDIOBALANCE,      //设置声道，1:左声道，2:右声道，3:双声道
    MEDIAPROCESSOR_PROP_SET_MUTE,              //设置静音
    MEDIAPROCESSOR_PROP_SET_VOLUME,            //设置音量大小
    MEDIAPROCESSOR_PROP_SET_RATIO = 640,       //设置视频显示比例
    MEDIAPROCESSOR_PROP_SET_VIDEOWINDOW,       //设置视频窗口位置
    MEDIAPROCESSOR_PROP_SET_VIDEOVISIBLE,      //设置视频是否可见，0表示不可见，1表示可见
    MEDIAPROCESSOR_PROP_SET_EPGSIZE = 643,     //设置 EPG 大小，标清固定 640*530，高清是 1280*720，在高清平台中有些页面还是 640*530，会随
                                               //时在这两种分辩率中切换，所以要做到根据不同分辩率来进行拉伸。当IsSoftFit 返回  true 时，该接口不用
                                               //实现
                                               
    MEDIAPROCESSOR_PROP_SET_SURFACE,           //用于Android 4.0将surface 传给播放器，从而显示视频画面。Surface是 Android图形机
                                               //制的结构，其定义详见Android 头文件

    MEDIAPROCESSOR_PROP_NONE
    
}MEDAIPROCESSOR_PROP_TYPE;


/*视频编码格式*/
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


/*音频编码格式*/
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


/*视频窗口位置*/
typedef struct
{
    int x;
    int y;
    int w;
    int h;
}mediaProcessor_rect;

/*打开底层一个播放器*/
int
ipanel_mediaProcessor_open(int type);

#if 0
/*设置音视频播放参数*/
int
ipanel_mediaProcessor_set_param(int handle,IPANEL_VDEC_PARAM *vParam,IPANEL_ADEC_PARAM *aParam);
#else
/*设置音视频全部播放参数*/
int
ipanel_mediaProcessor_set_param(int handle,IPANEL_VDEC_PARAM *vParam,IPANEL_ADEC_PARAM *aParam,int adec_param_len);
#endif

/*播放视频*/
int
ipanel_mediaProcessor_play(int handle);

/*暂停播放*/
int
ipanel_mediaProcessor_pause(int handle);

/*恢复播放*/
int
ipanel_mediaProcessor_resume(int handle);

/*seek播放*/
int
ipanel_mediaProcessor_seek(int handle);

/*快进快退播放*/
int
ipanel_mediaProcessor_fast(int handle,int rate);

/*快进快退转正常播放*/
int
ipanel_mediaProcessor_stopFast(int handle);

/*停止播放*/
int
ipanel_mediaProcessor_stop(int handle,int value);

/*关闭播放器*/
int
ipanel_mediaProcessor_close(int handle);

/*向底层解码器注入数据流*/
int
ipanel_mediaProcessor_push_stream(int handle,unsigned char* buffer,int len);

/*切换音轨信息*/
int 
ipanel_mediaProcessor_switch_AudioTrack(int handle, int pid);

/*获取参数*/
int
ipanel_mediaProcessor_get_property(int handle,int prop,void *value);

/*设置参数*/
int
ipanel_mediaProcessor_set_property(int handle,int prop,int value);



#ifdef __cplusplus
}
#endif

#endif

/************************************End Of File**********************************/

