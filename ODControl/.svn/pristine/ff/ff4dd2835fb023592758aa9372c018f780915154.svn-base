/*********************************************************************
    Copyright (c) 2012 iPanel Technologies, Ltd.
    All rights reserved. You are not allowed to copy or distribute
    the code without permission.
 	History:	2012-9-5 pdc-develop7 Created
*********************************************************************/
#ifndef _MPLAYER_H__
#define _MPLAYER_H__

#ifdef __cplusplus
extern "C" {
#endif

/*宏定义*/
#define MPLAYER_OK  (0)
#define MPLAYER_ERR (-1)

/*服务器类型枚举*/
typedef enum
{
    MPLAYER_SERVER_NONE         = 0,
    MPLAYER_SERVER_SEACHANGE    = 1, /*思迁VOD*/
    MPLAYER_SERVER_ISMA         = 2, /*思华VOD*/
    MPLAYER_SERVER_DMX          = 3, /*DMX协议VOD*/
    MPLAYER_SERVER_TELECOM      = 4, /*电信VOD*/
    MPLAYER_SERVER_UT           = 5, /*UT VOD*/
    MPLAYER_SERVER_CISCO_DMX    = 6, /*NGOD协议VOD*/
    MPLAYER_SERVER_IPANEL       = 7  /*IPANEL VOD*/
}mplayer_server_e;

/*播放模式枚举*/
typedef enum
{
    MPLAYER_MODE_NONE   = 0,
    MPLAYER_MODE_IP     = 1,    /*IP模式*/
    MPLAYER_MODE_DVB    = 2     /*DVB模式*/
}mplayer_mode_e;

/*播放类型枚举*/
typedef enum
{
    MPLAYER_TYPE_NONE   = 0,
    MPLAYER_TYPE_LiveTV = 1,    /*直播*/
    MPLAYER_TYPE_VOD    = 2,    /*点播*/
    MPLAYER_TYPE_TSTV   = 3     /*时移*/    
}mplayer_type_e;

/*消息枚举*/
typedef enum 
{
	MPLAYER_PREPAREPLAY_SUCCESS = 0,/*准备播放成功,上层应用收到此消息可以调用play*/
	MPLAYER_CONNECT_FAILED      = 1,/*连接服务器失败,建立会话失败或者服务器返回超时*/
	MPLAYER_PLAY_SUCCESS        = 2,/*播放媒体成功*/
	MPLAYER_PLAY_FAILED	        = 3,/*播放媒体失败*/
	MPLAYER_PROGRAM_BEGIN       = 4,/*时移频道或VOD影片播放到了点播开始的位置*/
	MPLAYER_PROGRAM_END         = 5,/*时移频道或VOD影片播放到了点播结束的位置*/
	MPLAYER_OUT_OF_RANGE        = 6,/*时移或VOD影片请求超过过时间范围*/
	MPLAYER_RELEASE_SUCCESS     = 7,/*关闭媒体成功*/
	MPLAYER_RELEASE_FAIL        = 8,/*关闭媒体失败*/
	MPLAYER_SET_MEDIA_INFO      = 9, /*设置媒体播放参数,p1为mplayer_media_t*/
	MPLAYER_STOP_MEDIA
}mplayer_msg_e;

/*播放属性枚举*/
typedef enum
{
	MPLAYER_PROP_TYPE           	= 0,
	MPLAYER_PROP_URL            	= 1,
	MPLAYER_PROP_DURATION       	= 2,
	MPLAYER_PROP_SPEED          	= 3,
	MPLAYER_PROP_STATUS         	= 4,
	MPLAYER_PROP_ELAPSED        	= 5,
	MPLAYER_PROP_TSTV_STARTTIME     = 6,
	MPLAYER_PROP_TSTV_PRESENTTIME   = 7,
	MPLAYER_PROP_TSTV_ENDTIME       = 8,
	MPLAYER_PROP_PROGRESS       	= 9,
	MPLAYER_PROP_TSTV_URL
}mplayer_prop_e;

/*播放参数信息*/
typedef struct mplayer_media_s
{
	unsigned int    frequency;		/*频点信息，hex: 0x0FFF0000*/
	unsigned int	symbolrate;		/*hex: 0x00068750*/
	unsigned char	qam;			/*1:16-QAM 2:32-QAM 3:64-QAM 4:128-QAM 5:256=QAM*/
	unsigned short	service_id;		/*前端描述的ServiceId*/
	unsigned short	pmt_pid;	    /* PMT PID(一些CA要从PMT的PID开始, ECM/EMM的PID无用!) */
}mplayer_media_t;

/*****************************************************************
 * @brief 消息回调函数,用于抛消息给上层应用
 
 * @param player mplayer_open打开的播放句柄
 * @param msg 回调具体消息类型,枚举型mplayer_msg_e
 * @param p1 消息类型的参数
 * @param p2 消息类型的参数
 *
 * @return 无
******************************************************************/
typedef void (*mplayer_cbf)(void *player, int msg, int p1, int p2);

/*****************************************************************
 * @brief 初始化mplayer模块,一般系统启动时调用
 
 * @param 无
 *
 * @return 模块句柄 
******************************************************************/
void *mplayer_init(void);

/*****************************************************************
 * @brief 销毁mplayer模块
 
 * @param handle 初始化时得到的句柄
 *
 * @return 无
******************************************************************/
void mplayer_exit(void *handle);

/*****************************************************************
 * @brief 切换当前vod类型以及方式,建议在open url之前都做这个切换
 
 * @param server vod服务器类型,类型名mplayer_server_e
 * @param mode vod播放模式,类型名mplayer_mode_e
 *
 * @return 成功:MPLAYER_OK 失败:MPLAYER_ERR
******************************************************************/
int mplayer_change_server(int server, int mode);

/*****************************************************************
 * @brief 打开mplayer播放实例
 
 * @param url 播放路径,rtsp://
 * @param type mplayer播放类型,类型名mplayer_type_e
 * @param cbf 注册的函数指针
 *
 * @return player句柄
******************************************************************/
void *mplayer_open(const char *url, int type, mplayer_cbf cbf);

/*****************************************************************
 * @brief 关闭mplayer播放实例
 
 * @param player 播放句柄
 *
 * @return 成功:MPLAYER_OK 失败:MPLAYER_ERR
******************************************************************/
int mplayer_close(void *player);

/*****************************************************************
 * @brief 停止播放
 
 * @param player 播放句柄
 * @param keep_last_frame 是否保留最后一帧 1:保留最后一帧 0:黑屏
 *
 * @return 成功:MPLAYER_OK 失败:MPLAYER_ERR
******************************************************************/
int mplayer_stop(void *player, int keep_last_frame);

/*****************************************************************
 * @brief 暂停播放
 
 * @param player 播放句柄
 *
 * @return 成功:MPLAYER_OK 失败:MPLAYER_ERR
******************************************************************/
int mplayer_pause(void *player);

/*****************************************************************
 * @brief 指定速率播放
 
 * @param player 播放句柄
 * @param speed 速率
 *
 * @return 成功:MPLAYER_OK 失败:MPLAYER_ERR
******************************************************************/
int mplayer_play(void *player, int speed);

/*****************************************************************
 * @brief 指定位置播放
 
 * @param player 播放句柄
 * @param seek_time seek的时间
 *
 * @return 成功:MPLAYER_OK 失败:MPLAYER_ERR
******************************************************************/
int mplayer_seek(void *player, char *seek_time);

/*****************************************************************
 * @brief 获取属性信息
 
 * @param player 播放句柄
 * @param prop 属性类型,mplayer_prop_e
 * @param value 获取的值
 *
 * @return 成功:MPLAYER_OK 失败:MPLAYER_ERR
******************************************************************/
int mplayer_get_property(void *player, int prop, void *value);

/*****************************************************************
 * @brief 设置属性信息
 
 * @param prop 属性类型,mplayer_prop_e
 * @param value 设置的值
 *
 * @return 成功:MPLAYER_OK 失败:MPLAYER_ERR
******************************************************************/
int mplayer_set_property(void *player, int prop, int value);

/*这两个函数实际上不是公开给android用的,应该是android上层应用公开给iPlayer用的-暂时先这么用一下吧*/
int ipanel_dvb_play(mplayer_media_t *media);

int ipanel_dvb_stop_play(void);


#ifdef __cplusplus
}
#endif

#endif
