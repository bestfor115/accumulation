/*******************************************************************************
/*    Copyright (c) 2008 iPanel Technologies, Ltd.                     */
/*    All rights reserved. You are not allowed to copy or distribute          */
/*    the code without permission.                                            */
/*    This is the demo implenment of the Porting APIs needed by iPanel        */
/*    MiddleWare.                                                             */
/*    Maybe you should modify it accorrding to Platform.                      */
/*                                                                            */
/*   History:                                                                  */
/*   Version     Date        Author      Modification                         */
/*   --------    --------    --------    --------                             */
/*   1.0         2010-10-20   andy        Create                              */
/******************************************************************************/

#ifndef _IPANEL_VANE_PORTING_MEDIAPALYER_API_FUNCTOTYPE_H_
#define _IPANEL_VANE_PORTING_MEDIAPALYER_API_FUNCTOTYPE_H_

#include "ipanel_porting.h"

#ifdef __cplusplus
extern "C" {
#endif

#define AUDIO_TRACK_DESC_MAX_LEN 32 //音轨描述最大字符长度

#define AUDIO_TRACKS_MAX_LEN     8  //最大支持读取音轨个数

#define SUBT_DESC_MAX_LEN        48

#define SUBT_LANG_MAX_NUM        8

typedef enum
{
    IPANEL_MEDIA_TRANS_NET_HFC      = 0,
    IPANEL_MEDIA_TRANS_NET_ETHERNET = 1
}IPANEL_MEDIA_TRANS_NET_e;

typedef enum 
{
    IPANEL_MEDIA_GET_DURATION               = 0,
    IPANEL_MEDIA_GET_STATUS                 = 1,
    IPANEL_MEDIA_GET_RATE                   = 2, 
    IPANEL_MEDIA_GET_POSITION               = 3,
    IPANEL_MEDIA_GET_STARTTIME              = 4,
    IPANEL_MEDIA_GET_ENDTIIME               = 5,
    IPANEL_MEDIA_GET_TRANS_NET              = 6,
    IPANEL_MEDIA_PUSH_STREAM                = 7,
    IPANEL_MEDIA_CLEAR_BUFFER               = 8,
    IPANEL_MEDIA_GET_SERVICE                = 9,
    IPANEL_MEDIA_ADEC_DEVICE_OUTPUT         = 10, //音频解码器
    IPANEL_MEDIA_VDEC_DEVICE_OUTPUT         = 11, //视频解码器
    IPANEL_MEDIA_GET_AUDIO_TRACKS           = 12, //获取所有音轨
    IPANEL_MEDIA_GET_CURR_AUDIO_TRACK       = 13, //获取当前使用音轨
    IPANEL_MEDIA_GET_TYPE                   = 14, //获取当前播放类型
    IPANEL_MEDIA_GET_SOURCE_URL             = 15, //获取播放地址
    IPANEL_MEDIA_GET_TSTV_URL               = 16, //获取时移播放地址
    IPANEL_MEDIA_SET_TSTV_URL               = 102, //设置时移播放地址
    IPANEL_MEDIA_SET_QOS_FUNC               = 103, //设置QOS回调函数
    IPANEL_MEDIA_GET_AUDIO_PTS              = 104, /*获取音频PTS 45K为单位的*/
    // adec的控制
    IPANEL_MEDIA_ADEC_SET_CHANNEL_MODE      = 1000,
    IPANEL_MEDIA_ADEC_SET_MUTE              = 1001,
    IPANEL_MEDIA_ADEC_SET_PASS_THROUGH      = 1002,
    IPANEL_MEDIA_ADEC_SET_VOLUME            = 1003,
    IPANEL_MEDIA_ADEC_SET_CURR_AUDIO_TRACK  = 1004, //设置当前使用音轨
    IPANEL_MEDIA_ADEC_SET_CODEC_TYPE        = 1005, //设置mediaplay的音频解码格式
    // vdec的控制
    IPANEL_MEDIA_VDEC_SET_CODEC_TYPE        = 1800, //设置mediaplay的视频解码格式
    //
    IPANEL_MEDIA_PLAY_I_FRAME               = 2000,
    IPANEL_MEDIA_STOP_I_FRAME               = 2001,
    IPANEL_MEDIA_KEEP_LAST_FRAME            = 2002,
    // display的控制等。
    IPANEL_MEDIA_DIS_SELECT_DEV             = 3001,
    IPANEL_MEDIA_DIS_ENABLE_DEV             = 3002,
    IPANEL_MEDIA_DIS_SET_MODE               = 3003,
    IPANEL_MEDIA_DIS_SET_VISABLE            = 3004,
    IPANEL_MEDIA_DIS_SET_ASPECT_RATIO       = 3005,
    IPANEL_MEDIA_DIS_SET_WIN_LOCATION       = 3006,
    IPANEL_MEDIA_DIS_SET_WIN_TRANSPARENT    = 3007,
    IPANEL_MEDIA_DIS_SET_CONTRAST           = 3008,
    IPANEL_MEDIA_DIS_SET_HUE                = 3009,
    IPANEL_MEDIA_DIS_SET_BRIGHTNESS         = 3010,
    IPANEL_MEDIA_DIS_SET_SATURATION         = 3011,
    IPANEL_MEDIA_DIS_SET_SHARPNESS          = 3012,
    IPANEL_MEDIA_DIS_SET_HD_RES             = 3013,
    IPANEL_MEDIA_DIS_SET_IFRAME_LOCATION    = 3014,

    //subtitle的控制
    IPANEL_MEDIA_SUBTITLE_SET_PID           = 4001,/*设置subtitle对应的PID*/
    IPANEL_MEDIA_SUBTITLE_GET_PES           = 4002,/*获取当前时间的PES字幕数据包*/
	IPANEL_MEDIA_SUBTITLE_GET_LANGUAGES     = 4003,/*获取TS字幕信息*/
	IPANEL_MEDIA_SUBTITLE_GET_CURR_LANG     = 4004,/*获取当前使用的字幕*/
	IPANEL_MEDIA_SUBTITLE_SWITCH_LANG       = 4005,/*根据序号切换字幕*/

    IPANEL_MEDIA_UNKNOWN
} IPANEL_MEDIA_PLAYER_IOCTL_e;

typedef enum 
{
    IPANEL_MEDIA_RUNNING    = 0,
    IPANEL_MEDIA_STOPPED    = 1,
    IPANEL_MEDIA_PAUSED     = 2,
    IPANEL_MEDIA_REWIND     = 3,
    IPANEL_MEDIA_FORWARD    = 4,
    IPANEL_MEDIA_SLOW       = 5
} MEDIA_STATUS_TYPE_e; // 播放器状态

typedef struct _MEDIA_STATUS MEDIA_STATUS;
struct _MEDIA_STATUS {
    int      param;
    UINT32_T statu;
};

typedef struct
{
    INT32_T trackID;                             //音轨ID
    BYTE_T  trackdesc[AUDIO_TRACK_DESC_MAX_LEN]; //音轨描述，一般为音轨压缩格式
	BYTE_T  langdesc[AUDIO_TRACK_DESC_MAX_LEN]; //音轨语言描述，表示语种
	UINT32_T flag_support; /*标记平台是否支持该音轨*/
	INT32_T audio_pid;		//音轨的音频pid，没有或获取不到返回-1，一般只有TS/PS才会有该属性
}IPANEL_MEDIA_AUDIO_TRACKINFO; //单个音轨信息

typedef struct
{
    INT32_T                       len;                          //音轨数目
    IPANEL_MEDIA_AUDIO_TRACKINFO  tracks[AUDIO_TRACKS_MAX_LEN]; //音轨信息数组
} IPANEL_MEDIA_AUDIO_TRACKS; //所有音轨信息

typedef struct
{
	INT32_T trackID;                             //字幕ID
    BYTE_T subtdesc[SUBT_DESC_MAX_LEN]; 
	BYTE_T langdesc[SUBT_DESC_MAX_LEN]; //字幕语言
	INT32_T subt_pid;	//字幕的pid，没有或获取不到返回-1，一般只有TS/PS才会有该属性 
}IPANEL_MEDIA_SUBT_DESC; 

typedef struct 
{
    INT32_T                len;                      //字幕数目
    IPANEL_MEDIA_SUBT_DESC subts[SUBT_LANG_MAX_NUM];
}IPANEL_MEDIA_SUBT_INFO;

typedef void (*IPANEL_PLAYER_EVENT_NOTIFY)(UINT32_T player, INT32_T event, void *param);

UINT32_T ipanel_mediaplayer_open(CONST CHAR_T *des, IPANEL_PLAYER_EVENT_NOTIFY cbk);
INT32_T ipanel_mediaplayer_close(UINT32_T player);
INT32_T ipanel_mediaplayer_play(UINT32_T player,CONST BYTE_T *mrl,CONST BYTE_T *des);
INT32_T ipanel_mediaplayer_stop(UINT32_T player);
INT32_T ipanel_mediaplayer_pause(UINT32_T player);
INT32_T ipanel_mediaplayer_resume(UINT32_T player);
INT32_T ipanel_mediaplayer_slow(UINT32_T player, INT32_T rate);
INT32_T ipanel_mediaplayer_forward(UINT32_T player,INT32_T rate);
INT32_T ipanel_mediaplayer_rewind(UINT32_T player, INT32_T rate);
INT32_T ipanel_mediaplayer_seek(UINT32_T player, BYTE_T *pos);
INT32_T ipanel_mediaplayer_ioctl(UINT32_T player ,INT32_T op,UINT32_T *param);
INT32_T ipanel_mediaplayer_start_record(UINT32_T player, CONST BYTE_T *mrl, CONST BYTE_T *device);
INT32_T ipanel_mediaplayer_stop_record(UINT32_T player);

#ifdef __cplusplus
}
#endif

#endif

