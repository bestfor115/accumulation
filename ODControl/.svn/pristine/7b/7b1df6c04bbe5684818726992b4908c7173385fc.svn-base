/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:RTSP CLIENT MAIN实现
*********************************************************************/
#ifndef __IVOD_RTSP_CLIENT_H_
#define __IVOD_RTSP_CLIENT_H_

#ifdef __cplusplus
extern "C"{
#endif

#include "ivod_client_main.h"

typedef enum
{
	IVOD_RTSP_EVENT_NONE = 300,
	//request
	IVOD_RTSP_EVENT_OPEN,
	IVOD_RTSP_EVENT_DESCRIBE,
	IVOD_RTSP_EVENT_SETUP,
	IVOD_RTSP_EVENT_PLAY,
	IVOD_RTSP_EVENT_PAUSE = 305,
	IVOD_RTSP_EVENT_RESUME,
	IVOD_RTSP_EVENT_SEEK,
	IVOD_RTSP_EVENT_FORWARD,
	IVOD_RTSP_EVENT_BACKWARD,
	IVOD_RTSP_EVENT_STOP = 310,	
	IVOD_RTSP_EVENT_CLOSE,
	IVOD_RTSP_EVENT_OPTIONS,	
	IVOD_RTSP_EVENT_PING,	
	IVOD_RTSP_EVENT_SET_PARAM,
	IVOD_RTSP_EVENT_GET_PARAM = 315,	
	IVOD_RTSP_EVENT_VALIDATE,
	IVOD_RTSP_EVENT_ANNOUNCE,
	IVOD_RTSP_EVENT_ERROR,
	//response	
	IVOD_RTSP_ACK_OPEN,
	IVOD_RTSP_ACK_DESCRIBE = 320,
	IVOD_RTSP_ACK_SETUP,
	IVOD_RTSP_ACK_PLAY,	
	IVOD_RTSP_ACK_PAUSE,
	IVOD_RTSP_ACK_CLOSE,
	IVOD_RTSP_ACK_OPTION = 325,
	IVOD_RTSP_ACK_PING,
	IVOD_RTSP_ACK_SET_PARAM,
	IVOD_RTSP_ACK_GET_PARAM,	
	IVOD_RTSP_ACK_VADILATE,	
	IVOD_RTSP_ACK_ANNOUNCE_RESPONSE = 330,
	IVOD_RTSP_ACK_ERROR_RESPONSE,
	
    /*添加EVENT处理*/
    IVOD_RTSP_EVENT_TIMER = 332, 
    IVOD_RTSP_EVENT_SOCKET_MSG,
	IVOD_RTSP_EVENT_SOCKET_DATA
}ivod_rtsp_event_e;

typedef enum
{
    IVOD_RTSP_PROP_LIVE_URL = 20,
    IVOD_RTSP_PROP_TSTV_URL,
    IVOD_RTSP_PROP_PLAYER_ID = 22,
    IVOD_RTSP_PROP_TIME_INFO,
    IVOD_RTSP_PROP_TYPE
}ivod_rtsp_prop_e;

typedef enum
{
    IVOD_RTSP_MSG_PLAYER_OPEN_SUCCESS = 0,
    IVOD_RTSP_MSG_PLAYER_OPEN_FAILED,
    IVOD_RTSP_MSG_PLAYER_CLOSE_SUCCESS = 2,
    IVOD_RTSP_MSG_PLAYER_CLOSE_FAILED,

    IVOD_RTSP_MSG_LIVE_TO_TSTV = 4,
    IVOD_RTSP_MSG_TSTV_TO_LIVE,
    IVOD_RTSP_MSG_UPDATE_TIME_INFO = 6,
    IVOD_RTSP_MSG_UPDATE_VOD_TYPE,
    IVOD_RTSP_MSG_PLAYER_DVB_INFO = 8
    
}ivod_rtsp_msg_e;

typedef enum
{
    IVOD_RTSP_CALLBACK_TYPE_NONE = 0,
    IVOD_RTSP_CALLBACK_TYPE_GET = 701,
    IVOD_RTSP_CALLBACK_TYPE_MSG = 702,
    IVOD_RTSP_CALLBACK_TYPE_RESULT = 703
}ivod_rtsp_callback_type;

typedef struct ivod_rtsp_client_s ivod_rtsp_client_t;

    
unsigned int
ivod_rtsp_open(client_t *me, ivod_msg_func func);

int
ivod_rtsp_proc(unsigned int handle, unsigned int event, int p1, int p2);

int 
ivod_rtsp_close(unsigned int handle);

int
ivod_rtsp_set_prop(unsigned int handle, int prop, int value);

int
ivod_rtsp_get_prop(unsigned int handle, int prop, void *value);

#ifdef __cplusplus
}
#endif

#endif
