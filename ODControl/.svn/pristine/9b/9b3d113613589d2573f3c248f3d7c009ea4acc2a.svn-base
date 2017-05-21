/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:PLAYER管理
*********************************************************************/
#ifndef __IPLAYER_PLAYER_H_
#define __IPLAYER_PLAYER_H_

#include "iplayer_main.h"
#include "ivod_client_main.h"
#include "iplayer_mem.h"


#ifdef __cplusplus
extern "C"{
#endif


#define IPLAYER_TS_TRACK     (0)
#define IPLAYER_AUDIO_TRACK  (0)
#define IPLAYER_VIDEO_TRACK  (1)
#define IPLAYER_TRACK_NUM    (2)
#define IPLAYER_MAX_SDP_LEN  (1024)

typedef int (*iplayer_cbf)(void *handle, unsigned int event, int p1, int p2);
typedef struct iplayer_s iplayer_t;
typedef struct iplayer_mgr_s iplayer_mgr_t;

/*播放模式*/
typedef enum
{
    IPLAYER_MODE_NONE = 0,
    IPLAYER_MODE_IP_TS,
    IPLAYER_MODE_IP_ES,
    IPLAYER_MODE_QAM_TS = 3,
    IPLAYER_MODE_QAM_ES
}iplayer_mode_e;

/*操作方式*/
typedef enum
{
    IPLAYER_OP_NONE = 0,
    IPLAYER_OP_OPEN,
    IPLAYER_OP_PLAY,
    IPLAYER_OP_SEEK,
    IPLAYER_OP_PAUSE = 4,
    IPLAYER_OP_RESUME,
    IPLAYER_OP_FWBW,
    IPLAYER_OP_NOTIFY,
    IPLAYER_OP_STOP = 8,
    IPLAYER_OP_CLOSE,
    IPLAYER_OP_SET_PROP,
    IPLAYER_OP_GET_PROP = 11,
    IPLAYER_OP_GET_BLOCK,
    IPLAYER_OP_APPEND_DATA,
    IPLAYER_OP_TIME
}iplayer_op_e;


/*播放器参数信息*/
typedef enum
{
    IPLAYER_PARAM_RTP_SEQ = 0,                     /*RTP包的seq*/

    IPLAYER_PARAM_MEDIA_INFO,                      /*音视频信息，包括音轨信息*/

    IPLAYER_PARAM_AUDIO_TRACKS,                    /*音频的音轨信息*/
    IPLAYER_PARAM_AUDIO_TRACK_ID = 3,              /*音频的当前音轨*/
    IPLAYER_PARAM_AUDIO_CHANGE_TRACK_ID,           /*切换音轨信息*/
    IPLAYER_PARAM_VIDEO_PTS,                       /*视频的pts*/
    
    IPLAYER_PARAM_VOD_BUFFER_SIZE = 6,             /*中间件播放器缓存大小*/
    IPLAYER_PARAM_VOD_BUFFER_RATE,                 /*中间件播放器缓存使用率*/
    IPLAYER_PARAM_PORTING_VIDEO_BUFFER_SIZE,       /*底层视频buffer总大小*/
    IPLAYER_PARAM_PORTING_VIDEO_BUFFER_RATE = 9,   /*底层视频buffer使用率*/
    IPLAYER_PARAM_PORTING_AUDIO_BUFFER_SIZE,       /*底层音频buffer总大小*/
    IPLAYER_PARAM_PORTING_AUDIO_BUFFER_RATE,       /*底层音频buffer使用率*/

    IPLAYER_PARAM_AUDIO_MODE = 12,                 /*设置声道模式(左声道、右声道)*/
    IPLAYER_PARAM_MUTE,                            /*设置获取静音状态*/
    IPLAYER_PARAM_VOLUME,                          /*播放器的音量*/

    IPLAYER_PARAM_WIN_LOCATION = 15,               /*设置视频窗口大小*/

    IPLAYER_PARAM_SUBTITLE_GET_LANGUAGES,          /*获取TS字幕信息*/
	IPLAYER_PARAM_SUBTITLE_GET_CURR_LANG = 17,     /*获取当前使用的字幕*/
	IPLAYER_PARAM_SUBTITLE_SWITCH_LANG,            /*根据序号切换字幕*/    

    IPLAYER_PARAM_NONE
}iplayer_param_e;

/*视频窗口位置*/
typedef struct
{
    int x;
    int y;
    int w;
    int h;
}iplayer_rect;


/*ES方式信息*/
typedef struct es_info_s
{
    int     payload_type[IPLAYER_TRACK_NUM];    /*payload_type*/
    int     sample_rate[IPLAYER_TRACK_NUM];     /*采样率*/
    char    param[IPLAYER_TRACK_NUM][IPLAYER_MAX_SDP_LEN];      /*SDP的参数信息*/
}es_info_t;

/*DVB方式信息*/
typedef struct dvb_info_s
{
	unsigned int	frequency;	    /*频点信息，hex: 0x0FFF0000*/
	unsigned int	symbolrate;		/*hex: 0x00068750*/
	unsigned char	qam;			/*1:16-QAM 2:32-QAM 3:64-QAM 4:128-QAM 5:256=QAM*/
	unsigned short	service_id;		/*前端描述的ServiceId*/
	unsigned short	pmt_pid;		/*PMT PID(一些CA要从PMT的PID开始, ECM/EMM的PID无用!)*/
	unsigned short	video_pid;		/*video pid*/
	unsigned short	audio_pid;		/*audio pid*/
	unsigned short	pcr_pid;		/*pcr pid*/
	unsigned short	audio_dec;		/*音频解码:DvbAudioCodingType*/
	unsigned short	video_dec;	    /*视频解码:DvbVedioCodingType*/    
}dvb_info_t;

/*打开player时传递的播放信息*/
typedef struct iplayer_info_s
{
    int             mode;
    dvb_info_t      dvb_info;
    es_info_t       es_info;  
}iplayer_info_t;

/*play ok的码流信息*/
typedef struct active_info_s
{
	int             channel_no[IPLAYER_TRACK_NUM];     /*channel id*/
	unsigned int    rtp_seq_num[IPLAYER_TRACK_NUM];    /*起始seq number*/
	unsigned int 	rtp_time_stamp[IPLAYER_TRACK_NUM]; /*起始time stamp*/
	unsigned int    ssrc[IPLAYER_TRACK_NUM];           /*ssrc*/
}active_info_t;

/*创建player管理器*/
iplayer_mgr_t *
iplayer_mgr_new(void);

/*销毁player管理器*/
int
iplayer_mgr_destory(iplayer_mgr_t *handle);

/*打开player播放实例*/
int
iplayer_open(client_t *client_main, iplayer_info_t *info);

/*关闭player播放实例*/
int
iplayer_close(int player_id);

/*停止player播放实例*/
int
iplayer_stop(int player_id, int keep_last_frame);

/*暂停*/
int
iplayer_pause(int player_id);

/*恢复*/
int
iplayer_resume(int player_id);

/*播放前的notify*/
int
iplayer_notify(int player_id, int op, int speed);

/*播放*/
int
iplayer_play(int player_id, int op, active_info_t *info);

/*获取可用内存块*/
imem_node_t*
iplayer_get_unused_block(int player_id, int size);

/*添加到player的数据*/
int
iplayer_append_data(int player_id, int value);

/*设置播放器参数*/
int
iplayer_set_prop(int player_id, int op, int value);

/*获取播放器参数*/
int
iplayer_get_prop(int player_id, int op, int value);


#ifdef __cplusplus
}
#endif

#endif
/************************************End Of File**********************************/



