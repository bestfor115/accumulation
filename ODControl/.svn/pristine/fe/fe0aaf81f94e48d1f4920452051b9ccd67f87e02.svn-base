/*********************************************************************
	Copyright (c) 2012 by iPanel Technologies, Ltd.
	All rights reserved. You are not allowed to copy or distribute
	the code without permission.
*********************************************************************/


#ifndef __TS_IPARSE_H__
#define __TS_IPARSE_H__

#include "ipanel_porting.h"

#define TS_IPARSE_PAT_PID  0x00
#define TS_IPARSE_PMT_PID  0x02

#define MAX_AV_CODEC_PRIVATE_LEN  512
#define MAX_TS_AUDIO_TRACKS_NUM   8
#define MAX_TS_SUBTITLE_TRACKS_NUM 8


#define TS_IPARSE_SECTION_LEN(ptr) (((ptr[1]&0x0f)<<8) | (ptr[2]&0xff) + 3)

typedef struct ts_video_info_t
{
    int valid;         //视频是否可用
    int video_pid;     //视频的pid
    int codec_id;      //视频的类型
    int framerate;     //视频的帧率
    int width;         //图片的宽度
    int height;        //图片的高度
    int private_len;   //私有数据长度
    unsigned char  private_data[MAX_AV_CODEC_PRIVATE_LEN];   //私有数据
}ts_video_info;


typedef struct ts_audio_info_t
{
    int valid;               //音频是否可用
    int audio_pid;           //音频的pid
    int codec_id;            //音频的类型
    int samplesize;          //采样宽度
    int samplerate;          //采样频率
    int profile;             //适合AAC音频用于区分版本
    unsigned char lang[32];  //表示语种
    unsigned char desc[32];  //表示压缩格式   
    int private_len;         //私有字段长度
    unsigned char private_data[MAX_AV_CODEC_PRIVATE_LEN];   //私有数据
}ts_audio_info;

typedef struct ts_subt_info_t
{
    int  subt_type;	     //类型 一般为SUBT_DVB
	int  subt_pid;	     //字幕pid
    unsigned char   lang[32];	     //表示语言
	unsigned char   desc[32];        //
}ts_subt_info;


typedef struct ts_av_info_t
{
	int         audio_channel_num;      //音轨数量
	int         audio_current_channel;  //当前音轨
	int         subt_channel_num;       //字幕数量
	int         subt_current_channel;   //当前字幕
	ts_video_info     vidinfo[1];
	ts_audio_info     audinfo[MAX_TS_AUDIO_TRACKS_NUM]; 
    ts_subt_info      subtinfo[MAX_TS_SUBTITLE_TRACKS_NUM];    
}ts_AV_info;


typedef struct ts_stream_info_t
{
	int pmt_pid;
    int pcr_pid;
	
	unsigned int current_pts;
    unsigned int base_pts;

    IPANEL_MEDIA_AUDIO_TRACKS   audio_track[1];
    IPANEL_MEDIA_SUBT_INFO      subt_track[1];
	ts_AV_info avinfo[1];
}ts_stream_info;


/*解析ts流音视频信息*/
int 
ts_iparse_stream_info(ts_stream_info *info,unsigned char *buffer,int len);


/*解析ts流PTS信息*/
int
ts_iparse_stream_pts(ts_stream_info *info,unsigned char *buffer,int len);


/*解析ts流视频的宽和高*/
int
ts_iparse_stream_video_picture(ts_stream_info *info,unsigned char *buffer,int len);



#endif //__TS_PARSER_H__
