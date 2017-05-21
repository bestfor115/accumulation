/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:中间件多实例封装的实现
*********************************************************************/
#ifndef __IPLAYER_MAIN_H_
#define __IPLAYER_MAIN_H_

#ifdef __cplusplus
extern "C"{
#endif

#include "ipanel_porting.h"

/*VOD TYPE*/
#define IVOD_TYPE_VOD        (0)
#define IVOD_TYPE_LiveTV     (1)
#define IVOD_TYPE_TSTV       (2)
#define MAX_IVOD_TYPE_NUM    (3)


/*mplayer回调函数*/
typedef int (*implayer_cbf)(void *handle, unsigned int event, int p1, int p2);

/*QOS全局回调函数*/
typedef int (*qos_cbf)(void *option, int event, int p1, int p2);


typedef struct implayer_s implayer_t;
typedef struct implayer_mgr_s implayer_mgr_t;

enum
{
	IMPLAYER_PROP_UNDEFINED=0,

	IMPLAYER_PROP_STATUS,
	IMPLAYER_PROP_TYPE = 2,
        
	IMPLAYER_PROP_CURRENT_SPEED,
	IMPLAYER_PROP_REQUEST_SPEED = 4,
	
	IMPLAYER_PROP_URL,
	IMPLAYER_PROP_TSTV_URL = 6,
	
	IMPLAYER_PROP_DURATION,
	IMPLAYER_PROP_CURRENT_TIME = 8,
	IMPLAYER_PROP_START_TIME,
	IMPLAYER_PROP_END_TIME = 10,
	
    IMPLAYER_PROP_AUDIO_TRACKS,//=AVM_IOCTL_PLAYER_GET_AUDIO_TRACKS,		//获取所有音轨
	IMPLAYER_PROP_AUDIO_GET_CURR_TRACK = 12,//=AVM_IOCTL_PLAYER_GET_CURR_AUDIO_TRACK,	//获取当前使用音轨
    IMPLAYER_PROP_AUDIO_SET_CURR_TRACK,//=AVM_IOCTL_PLAYER_SET_CURR_AUDIO_TRACK,	//设置当前使用音轨
	IMPLAYER_PROP_AUDIO_GET_TRACK_SUPPORTING = 14,//=AVM_IOCTL_PLAYER_GET_AUDIO_TRACK_SUPPORTING //获取平台是否支持该音轨

	IMPLAYER_PROP_KEEP_LAST_FRAME,

    IMPLAYER_PROP_MINI_WINDOW = 16,           //是否小视屏窗口播放

    IMPLAYER_PROP_AUDIO_MODE,                 //设置音频的声道
    IMPLAYER_PROP_MUTE = 18,                  //设置获取静音状态
    IMPLAYER_PROP_VOLUME,                     //设置获取音量

    IMPLAYER_PROP_WIN_LOCATION = 20,          //视频窗口位置

    IMPLAYER_PROP_SUBTITLE_GET_LANGUAGES,     //获取TS字幕信息
	IMPLAYER_PROP_SUBTITLE_GET_CURR_LANG = 22,//获取当前使用的字幕
	IMPLAYER_PROP_SUBTITLE_SWITCH_LANG,       //根据序号切换字幕
    IMPLAYER_PROP_AUDIO_PTS,

    /**********************************************************************************************
     *以下列举为非所有项目使用属性枚举
     **********************************************************************************************/
    IMPLAYER_PROP_SET_QOS_FUNC = 9001,        //设置QOS回调函数
    
	IMPLAYER_PROP_UNKNOWN
};

/*视频窗口位置*/
typedef struct
{
    int x;
    int y;
    int w;
    int h;    
}implayer_rect;


/*创建mplayer管理器*/
implayer_mgr_t *
implayer_mgr_new(void);

/*销毁mplayer管理器*/
void 
implayer_mgr_delete(implayer_mgr_t *me);

/*创建一个mplayer实例*/
implayer_t *
implayer_create(void *handle, implayer_cbf cbf);

/*销毁一个mplayer实例*/
void 
implayer_destroy(implayer_t *me);

/*打开*/
int
implayer_open(implayer_t *me, const char *src, const char *type, int is_third);

/*关闭*/
int
implayer_close(implayer_t *me);

/*停止*/
int
implayer_stop(implayer_t *me, int keep_last_frame);

/*暂停*/
int
implayer_pause(implayer_t *me);

/*播放、快进、快退*/
int
implayer_play(implayer_t *me, int speed);

/*SEEK*/
int
implayer_seek(implayer_t *me, char *seek_time);

/*获取参数*/
int
implayer_get_property(implayer_t *me, int prop, void *value);

/*设置参数*/
int
implayer_set_property(implayer_t *me, int prop, int value);





/************************************************************************************
 *qos信息处理函数,此函数为全局函数,各模块直接调用
 ***********************************************************************************/
int
implayer_qos_proc(void *option, int event, int p1, int p2);

typedef enum tag_VOD_QOS_PROP_e
{
	VOD_QOS_PROP_BASE,
	VOD_QOS_MultiReqNumbers,	 //Int(16)加入组播组的总次数											
	VOD_QOS_MultiRRT,            //Int(16)组播请求的总响应时间
	VOD_QOS_MultiFailNumbers,	 //Int(16) 加入组播组失败（加入后没有收到组播数据）的总次数																											
	VOD_QOS_MultiFailInfo,		 //string(256) 加入组播组失败（加入后没有收到组播数据）的信息，要求包含失败产生时间、要加入的组播频道地址、失败原因代码格式：yyyymmddhhmm-组播地址-“errorcode”			

	VOD_QOS_VodReqNumbers,		 //Int(16) R	单播（含点播、回看和时移）申请的总次数																
	VOD_QOS_VodRRT,              //Int(16) R 单播请求的总时间
	VOD_QOS_VodFailNumbers,		 //Int(16) R	单播（含点播、回看和时移）申请失败的总次数						
	VOD_QOS_VodFailInfo,		 //string(256)	 单播（含点播、回看和时移）申请失败详细信息						                

	VOD_QOS_MultiAbendNumbers,	 //Int(16) 组播过程中因为缓冲区被取空导致无法再播放的次数。																								
	VOD_QOS_VODAbendNumbers,	 //Int(16) 单播过程中因为缓冲区被取空导致无法再播放的次数。																							
	VOD_QOS_MultiUPAbendNumbers, //Int(16)																																
	VOD_QOS_VODUPAbendNumbers,	 //Int(16)																																		  	
	VOD_QOS_HD_MultiAbendNumbers,//Int(16)
	VOD_QOS_HD_VODAbendNumbers,	 //Int(16)																									
	VOD_QOS_HD_MultiUPAbendNumbers,	//Int(16)																							
	VOD_QOS_HD_VODUPAbendNumbers,	//Int(16)																																

	VOD_QOS_PlayErrorNumbers,	//Int(16) 视频播放过程中（包括组播和单播）的数据出错，导致无法再播放的次数																																	  	
	VOD_QOS_PlayErrorInfo,		//Int(256) 视频播放过程中（包括组播和单播）的数据出错，导致无法再播放的详细信息厂商可自定义						

	VOD_QOS_MultiPacketsLostR1Nmb,	//Int(16)组播丢包率范围 1 发生次数																																		
	VOD_QOS_MultiPacketsLostR2Nmb,	//Int(16)组播丢包率范围 2 发生次数																															
	VOD_QOS_MultiPacketsLostR3Nmb,	//Int(16)组播丢包率范围 3 发生次数				
	VOD_QOS_MultiPacketsLostR4Nmb,	//Int(16)组播丢包率范围 4 发生次数																				
	VOD_QOS_MultiPacketsLostR5Nmb,	//Int(16)组播丢包率范围 5 发生次数		

	VOD_QOS_FECMultiPacketsLostR1Nmb,	//Int(16)组播在经过FEC 纠错之后的RTP丢包统计，组播丢包率范围1 发生次数																									
	VOD_QOS_FECMultiPacketsLostR2Nmb,	//Int(16)组播在经过FEC 纠错之后的RTP丢包统计，组播丢包率范围2 发生次数																							
	VOD_QOS_FECMultiPacketsLostR3Nmb,	//Int(16)组播在经过FEC 纠错之后的RTP丢包统计，组播丢包率范围3 发生次数																																
	VOD_QOS_FECMultiPacketsLostR4Nmb,	//Int(16)组播在经过FEC 纠错之后的RTP丢包统计，组播丢包率范围4 发生次数																																		  	
	VOD_QOS_FECMultiPacketsLostR5Nmb,	//Int(16)组播在经过FEC 纠错之后的RTP丢包统计，组播丢包率范围5 发生次数

	VOD_QOS_VODPacketsLostR1Nmb,	//Int(16)单播丢包率范围 1 发生次数																															
	VOD_QOS_VODPacketsLostR2Nmb,	//Int(16)单播丢包率范围 2 发生次数																						
	VOD_QOS_VODPacketsLostR3Nmb,	//Int(16)单播丢包率范围 3 发生次数																									
	VOD_QOS_VODPacketsLostR4Nmb,	//Int(16)单播丢包率范围 4 发生次数																	
	VOD_QOS_VODPacketsLostR5Nmb,	//Int(16)单播丢包率范围 5 发生次数					

	VOD_QOS_ARQVODPacketsLostR1Nmb,	//Int(16)单播在经过ARQ 重传恢复后的丢包统计单播丢包率范围1 发生次数																									
	VOD_QOS_ARQVODPacketsLostR2Nmb,	//Int(16)单播在经过ARQ 重传恢复后的丢包统计单播丢包率范围2 发生次数																							
	VOD_QOS_ARQVODPacketsLostR3Nmb,	//Int(16)单播在经过ARQ 重传恢复后的丢包统计单播丢包率范围3 发生次数																																
	VOD_QOS_ARQVODPacketsLostR4Nmb,	//Int(16)单播在经过ARQ 重传恢复后的丢包统计单播丢包率范围4 发生次数																																		  	
	VOD_QOS_ARQVODPacketsLostR5Nmb,	//Int(16)单播在经过ARQ 重传恢复后的丢包统计单播丢包率范围5 发生次数

	VOD_QOS_MultiBitRateR1Nmb,	//Int(16)			组播实时比特率在 BitRateR1范围内的次数																																	
	VOD_QOS_MultiBitRateR2Nmb,	//Int(16)			组播实时比特率在 BitRateR2范围内的次数																								
	VOD_QOS_MultiBitRateR3Nmb,	//Int(16)			组播实时比特率在 BitRateR3范围内的次数																																	
	VOD_QOS_MultiBitRateR4Nmb,	//Int(16)			组播实时比特率在 BitRateR4范围内的次数																																			  	
	VOD_QOS_MultiBitRateR5Nmb,	//Int(16)			组播实时比特率在 BitRateR5范围内的次数																

	VOD_QOS_VODBitRateR1Nmb,	//Int(16)			单播实时比特率在 BitRateR1  范围内的次数
	VOD_QOS_VODBitRateR2Nmb,	//Int(16)			单播实时比特率在 BitRateR2  范围内的次数
	VOD_QOS_VODBitRateR3Nmb,	//Int(16)			单播实时比特率在 BitRateR3范围内的次数
	VOD_QOS_VODBitRateR4Nmb,	//Int(16)			单播实时比特率在 BitRateR4  范围内的次数																								
	VOD_QOS_VODBitRateR5Nmb,	//Int(16)			单播实时比特率在 BitRateR5  范围内的次数																					

	VOD_QOS_HD_MultiPacketsLostR1Nmb,	//Int(16)高清组播原始接受到的RTP 数据包，在纠错之前的丢包统计，组播丢包率范围1 发生次数																									
	VOD_QOS_HD_MultiPacketsLostR2Nmb,	//Int(16)高清组播原始接受到的RTP 数据包，在纠错之前的丢包统计，组播丢包率范围2 发生次数																							
	VOD_QOS_HD_MultiPacketsLostR3Nmb,	//Int(16)高清组播原始接受到的RTP 数据包，在纠错之前的丢包统计，组播丢包率范围3 发生次数																																
	VOD_QOS_HD_MultiPacketsLostR4Nmb,	//Int(16)高清组播原始接受到的RTP 数据包，在纠错之前的丢包统计，组播丢包率范围4 发生次数																																		  	
	VOD_QOS_HD_MultiPacketsLostR5Nmb,	//Int(16)高清组播原始接受到的RTP 数据包，在纠错之前的丢包统计，组播丢包率范围5 发生次数

	VOD_QOS_HD_FECMultiPacketsLostR1Nmb,	//Int(16)高清组播在经过FEC 纠错之后的RTP 丢包统计，组播丢包率范围1发生次数																									
	VOD_QOS_HD_FECMultiPacketsLostR2Nmb,	//Int(16)高清组播在经过FEC 纠错之后的RTP 丢包统计，组播丢包率范围2发生次数																							
	VOD_QOS_HD_FECMultiPacketsLostR3Nmb,	//Int(16)高清组播在经过FEC 纠错之后的RTP 丢包统计，组播丢包率范围3发生次数																																
	VOD_QOS_HD_FECMultiPacketsLostR4Nmb,	//Int(16)高清组播在经过FEC 纠错之后的RTP 丢包统计，组播丢包率范围4发生次数																																		  	
	VOD_QOS_HD_FECMultiPacketsLostR5Nmb,	//Int(16)高清组播在经过FEC 纠错之后的RTP 丢包统计，组播丢包率范围5发生次数

	VOD_QOS_HD_VODPacketsLostR1Nmb,	//Int(16)高清单播原始接受到的RTP 包的丢包统计单播丢包率范围1 发生次数																									
	VOD_QOS_HD_VODPacketsLostR2Nmb,	//Int(16)高清单播原始接受到的RTP 包的丢包统计单播丢包率范围1 发生次数																							
	VOD_QOS_HD_VODPacketsLostR3Nmb,	//Int(16)高清单播原始接受到的RTP 包的丢包统计单播丢包率范围3 发生次数																																
	VOD_QOS_HD_VODPacketsLostR4Nmb,	//Int(16)高清单播原始接受到的RTP 包的丢包统计单播丢包率范围4 发生次数																																		  	
	VOD_QOS_HD_VODPacketsLostR5Nmb,	//Int(16)高清单播原始接受到的RTP 包的丢包统计单播丢包率范围5 发生次数

	VOD_QOS_HD_ARQVODPacketsLostR1Nmb,	//Int(16)高清单播在经过ARQ 重传恢复后的丢包统计单播丢包率范围1 发生次数																									
	VOD_QOS_HD_ARQVODPacketsLostR2Nmb,	//Int(16)高清单播在经过ARQ 重传恢复后的丢包统计单播丢包率范围2 发生次数																							
	VOD_QOS_HD_ARQVODPacketsLostR3Nmb,	//Int(16)高清单播在经过ARQ 重传恢复后的丢包统计单播丢包率范围3 发生次数																																
	VOD_QOS_HD_ARQVODPacketsLostR4Nmb,	//Int(16)高清单播在经过ARQ 重传恢复后的丢包统计单播丢包率范围4 发生次数																																		  	
	VOD_QOS_HD_ARQVODPacketsLostR5Nmb,	//Int(16)高清单播在经过ARQ 重传恢复后的丢包统计单播丢包率范围5 发生次数

	VOD_QOS_HD_MultiBitRateR1Nmb,   //Int(16)高清组播实时比特率在BitRateR1范围内的次数																									
	VOD_QOS_HD_MultiBitRateR2Nmb,   //Int(16)高清组播实时比特率在BitRateR2范围内的次数																							
	VOD_QOS_HD_MultiBitRateR3Nmb,	//Int(16)高清组播实时比特率在BitRateR3范围内的次数																																
	VOD_QOS_HD_MultiBitRateR4Nmb,	//Int(16)高清组播实时比特率在BitRateR4范围内的次数																																		  	
	VOD_QOS_HD_MultiBitRateR5Nmb,	//Int(16)高清组播实时比特率在BitRateR5范围内的次数

	VOD_QOS_HD_VODBitRateR1Nmb,	//Int(16)高清单播实时比特率在BitRateR1范围内的次数																									
	VOD_QOS_HD_VODBitRateR2Nmb,	//Int(16)高清单播实时比特率在BitRateR2范围内的次数																							
	VOD_QOS_HD_VODBitRateR3Nmb,	//Int(16)高清单播实时比特率在BitRateR3范围内的次数																																
	VOD_QOS_HD_VODBitRateR4Nmb,	//Int(16)高清单播实时比特率在BitRateR4范围内的次数																																		  	
	VOD_QOS_HD_VODBitRateR5Nmb,	//Int(16)高清单播实时比特率在BitRateR5范围内的次数

	VOD_QOS_BufferIncNmb,	//Int(16) 因缓存下溢，向服务器请求增加缓存的次数																									
	VOD_QOS_BufferDecNmb,	//Int(16) 因缓存上溢，向服务器请求减少缓存的次数																							

	VOD_QOS_FramesLostR1Nmb,	//Int(16) 丢帧数在 BitRateR1  范围内的次数																																																		
	VOD_QOS_FramesLostR2Nmb,	//Int(16) 丢帧数在 BitRateR2  范围内的次数																																									
	VOD_QOS_FramesLostR3Nmb,	//Int(16) 丢帧数在 BitRateR3  范围内的次数																																									
	VOD_QOS_FramesLostR4Nmb,	//Int(16) 丢帧数在 BitRateR4  范围内的次数																																
	VOD_QOS_FramesLostR5Nmb,	//Int(16) 丢帧数在 BitRateR5  范围内的次数																															

	VOD_QOS_NUM,

	/*江苏电信添加*/
	VOD_QOS_PACKETS_RECVIVED,   //收包总数
	VOD_QOS_PACKETS_LOST,       //丢包总数
	VOD_QOS_BIT_RATE,           //比特率
	VOD_QOS_UNDEFINED,
}VOD_QOS_PROP_e;

typedef enum tag_VOD_QOS_EVENT_e
{
	VOD_QOS_EVENT_OPEN	= 0x199,
	VOD_QOS_EVENT_CLOSE,
	VOD_QOS_EVENT_PLAY,
	VOD_QOS_EVENT_PAUSE,
	VOD_QOS_EVENT_ERROR,
	VOD_QOS_EVENT_CONNECT_URL,
	VOD_QOS_EVENT_WINDOW_STATUS,
	VOD_QOS_EVENT_MINI,
	VOD_QOS_EVENT_NORMAL,

	VOD_QOS_EVENT_RECV_TS_STREAM, 	//p1 buffer,p2 buffer_len
	VOD_QOS_EVENT_TS_CLEAR,			//ts流清理。需要重新开始清理。
	VOD_QOS_EVENT_BUFFER_EMPTY,		/*缓冲区取空*/
	VOD_QOS_EVENT_RTSP_REQUEST,
	VOD_QOS_EVENT_TIMER,
	VOD_QOS_EVENT_BUFFER_OVERFLOW,	/*缓冲区上溢*/
	VOD_QOS_EVENT_BUFFER_UNDERFLOW, /*缓冲区下溢*/
	VOD_QOS_EVENT_UNDEFINED
}VOD_QOS_EVENT_e;


/* 主要提供给evevt 传输一些信息.*/
typedef struct tag_vod_qos_info_t
{
	uint32			flag_multicast:1;
	uint32			flag_mini_window:1;
	/*记录类型,时移和单播频道都不需要统计RRT*/
	uint32			type;
	uint8			*buffer;
	int32	  		buffer_len;
	int32	  		header_len;
	uint16			video_pid;
	uint16			pcr_pid;
	uint32          new_recv_size;

	
	char			*url;
}vod_qos_info_t;



#ifdef __cplusplus
}
#endif

#endif

/************************************End Of File**********************************/

