/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:CLIENT管理
*********************************************************************/
#ifndef __IVOD_CLIENT_MAIN_H_
#define __IVOD_CLIENT_MAIN_H_

#include "ipanel_porting.h"
#include "iplayer_mem.h"
#include "iplayer_common.h"

#ifdef __cplusplus
extern "C"{
#endif

typedef struct client_info_s client_info_t;
typedef struct client_mgr_s client_mgr_t;
typedef struct client_s client_t;


/*client回调类型*/
typedef enum
{
    CLIENT_CALLBACK_TYPE_NONE = 0,
    CLIENT_CALLBACK_TYPE_GET = 701,         /*获取信息消息*/
    CLIENT_CALLBACK_TYPE_MSG = 702,         /*通知消息*/
    CLIENT_CALLBACK_TYPE_RESULT = 703       /*执行结果*/
}client_callback_type_e;

/*client内部消息*/    
typedef enum
{
    CLIENT_MSG_PLAYER_OPEN_SUCCESS = 0,     /*打开解码器成功*/
    CLIENT_MSG_PLAYER_OPEN_FAILED,          /*打开解码器失败*/ 
    CLIENT_MSG_PLAYER_CLOSE_SUCCESS = 2,    /*关闭解码器成功*/
    CLIENT_MSG_PLAYER_CLOSE_FAILED,         /*关闭解码器失败*/     
    CLIENT_MSG_PLAYER_BIND = 4,             /*绑定解码器*/
    CLIENT_MSG_PLAYER_UNBIND,               /*解绑定解码器*/
    CLIENT_MSG_LIVE_TO_TSTV = 6,            /*直播切时移*/
    CLIENT_MSG_TSTV_TO_LIVE,                /*时移切直播*/
    CLIENT_MSG_UPDATE_TIME_INFO = 8,        /*更新时间信息*/
    CLIENT_MSG_UPDATE_VOD_TYPE,             /*更新播放的类型VOD\TSTV\LIVETV*/
    CLIENT_MSG_PLAYER_DVB_INFO = 10         /*DVB播放信息*/
}client_msg_e;

/*client提供给socket调用的event*/
typedef enum
{
    SOCKET_CONNECT_SUCCESS,
    SOCKET_CONNECT_FAILED,
    SOCKET_EXCEPTION_ERROR
}client_socket_msg_e;

/*EVENT*/
typedef enum
{
    CLIENT_EVENT_NONE = 0,
    CLIENT_EVENT_OPEN,               /*p2为-991表示组播地址错误；-996表示rtsp地址错误*/
    CLIENT_EVENT_PLAY,
    CLIENT_EVENT_SEEK,
    CLIENT_EVENT_PAUSE,
    CLIENT_EVENT_RESUME = 5,
    CLIENT_EVENT_FORWARD,
    CLIENT_EVENT_BACKWARD,
    CLIENT_EVENT_STOP,
    CLIENT_EVENT_CLOSE,
    CLIENT_EVENT_ANNOUNCE = 10,
    CLIENT_EVENT_ERROR,              /*播放过程中出现错误,p2为-995表示发送rtsp命令失败*/
    /*TIMER是虚拟出来的*/
    CLIENT_EVENT_TIMER,
    /*SOCKET过来的命令*/
    CLIENT_EVENT_SOCKET_MSG = 13,    /*SOCKET消息处理*/
    CLIENT_EVENT_SOCKET_DATA,        /*SOCKET数据处理*/
    CLIENT_EVENT_SOCKET_MEDIA        /*音视频数据*/
}client_event_name_e;

/*状态*/
typedef enum
{
	CLIENT_STATUS_NONE = 0,
	CLIENT_STATUS_OPEN, 
    CLIENT_STATUS_NORMAL,
	CLIENT_STATUS_BACKWARD = 3,
	CLIENT_STATUS_FORWARD,
	CLIENT_STATUS_PAUSE,
   	CLIENT_STATUS_CLOSED = 6,
	CLIENT_STATUS_STOP
}client_status_e;

/*info名称*/
typedef enum
{
    CLIENT_INFO_URL = 256,
    CLIENT_INFO_LIVE_URL,
    CLIENT_INFO_TSTV_URL,
    CLIENT_INFO_IP,
    CLIENT_INFO_PORT = 260,
    CLIENT_INFO_SPEED,
    CLIENT_INFO_TYPE,
    CLIENT_INFO_STATUS,
    CLIENT_INFO_PLAYER_ID,
    CLIENT_INFO_TIME_INFO = 265,
    CLIENT_INFO_BIND_TYPE,

    CLIENT_INFO_DURATION,
    CLIENT_INFO_CURRENT_TIME,
    CLIENT_INFO_START_TIME,
    CLIENT_INFO_END_TIME = 270,
    
}client_info_name_e;

/*创建client管理器*/
client_mgr_t *
client_mgr_new(void);

/*销毁client管理器*/
void
client_mgr_delete(client_mgr_t *handle);

/*创建client实例*/
client_t *
client_create(void *handle, ivod_msg_func cbf);

/*销毁client实例*/
int 
client_destroy(client_t *handle);

/*client发送消息*/
int 
client_proc(client_t *me, client_event_name_e event_name, int p1, int p2);

/*对外提供的设置属性接口-线程安全*/
int
client_set_property(client_t *me, int prop, int value);

/*对外提供的获取属性接口-线程安全*/
int 
client_get_property(client_t *me, int prop, void *value);

/*获取client 可用内存块*/
imem_node_t *
client_get_unused_block(client_t *me, int size);

/*处理socket event*/
int 
client_proc_socket(client_t *me, client_event_name_e event, int p1, int p2);

/*client回调函数,处理播放器、igmp和rtsp终端回调消息*/
int 
client_callback_proc(void *handle, unsigned int event,int p1, int p2);



#ifdef __cplusplus
}
#endif

#endif
