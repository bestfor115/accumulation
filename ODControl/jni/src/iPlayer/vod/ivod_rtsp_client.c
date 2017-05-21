/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:RTSP CLIENT实现
*********************************************************************/
#include "iplayer_vod_main.h"
#include "iplayer_common.h"
#include "ivod_client_main.h"
#include "ivod_rtsp_client.h"
#include "ivod_rtsp_ipanel.h"
#include "ivod_rtsp_telecom.h"
#include "ivod_rtsp_isma.h"

#define RTSP_MARK "[IVOD][RTSP_CLIENT]"

struct ivod_rtsp_client_s
{
    client_t *client_main;
    int cur_client_event;  
    unsigned int ivod_handle;/*vod 句柄*/
    /*回调给上层应用*/
    ivod_msg_func func;
    /*各个vod的函数指针*/
    unsigned int (*ivod_open)(client_t *client_main, struct ivod_rtsp_client_s *rtsp_client, int mode, ivod_msg_func func);
    int  (*ivod_close)(unsigned int handle);
    int  (*ivod_proc)(unsigned int handle, unsigned int event, int p1, int p2);
    int  (*ivod_set_prop)(unsigned int handle, int prop, int value);
    int  (*ivod_get_prop)(unsigned int handle, int prop, void *value);
};


static void ivod_rtsp_callback_result(ivod_rtsp_client_t *rtsp_client, unsigned int event, int p1, int p2)
{
    ivod_rtsp_client_t *me = (ivod_rtsp_client_t *)rtsp_client; 
    FAILED_RETURN(NULL == me || NULL == me->client_main || NULL == me->func);
    switch(p1)
    {
        case IVOD_RTSP_EVENT_OPEN:
            me->func(me->client_main,event,CLIENT_EVENT_OPEN,p2);
            break;
        case IVOD_RTSP_EVENT_PLAY:
            me->func(me->client_main,event,CLIENT_EVENT_PLAY,p2);
            break;
        case IVOD_RTSP_EVENT_SEEK:
            me->func(me->client_main,event,CLIENT_EVENT_SEEK,p2);
            break;
        case IVOD_RTSP_EVENT_PAUSE:
            me->func(me->client_main,event,CLIENT_EVENT_PAUSE,p2);
            break;
        case IVOD_RTSP_EVENT_RESUME:
            me->func(me->client_main,event,CLIENT_EVENT_RESUME,p2);
            break;            
        case IVOD_RTSP_EVENT_FORWARD:
            me->func(me->client_main,event,CLIENT_EVENT_FORWARD,p2);
            break;
        case IVOD_RTSP_EVENT_BACKWARD:
            me->func(me->client_main,event,CLIENT_EVENT_BACKWARD,p2);
            break;
        case IVOD_RTSP_EVENT_STOP:
            me->func(me->client_main,event,CLIENT_EVENT_STOP,p2);
            break;            
        case IVOD_RTSP_EVENT_CLOSE:
            me->func(me->client_main,event,CLIENT_EVENT_CLOSE,p2);
            break;
        case IVOD_RTSP_EVENT_ANNOUNCE:
            me->func(me->client_main,event,CLIENT_EVENT_ANNOUNCE,p2);
            break;            
        case IVOD_RTSP_EVENT_ERROR:
            me->func(me->client_main,event,CLIENT_EVENT_ERROR,p2);
            break;
    }

    return;
}

static int ivod_rtsp_callback_message(ivod_rtsp_client_t *rtsp_client, unsigned int event, unsigned int msg, int value)
{
    int ret = IPANEL_OK;
    ivod_rtsp_client_t *me = (ivod_rtsp_client_t *)rtsp_client;  
    FAILED_RETURNX(NULL == me || NULL == me->client_main || NULL == me->func, IPANEL_ERR);
    
    switch(msg)
    {
        case IVOD_RTSP_MSG_PLAYER_OPEN_FAILED:
            me->func(me->client_main,event,CLIENT_MSG_PLAYER_OPEN_FAILED,0);
            break;
        case IVOD_RTSP_MSG_PLAYER_OPEN_SUCCESS:
            me->func(me->client_main,event,CLIENT_MSG_PLAYER_OPEN_SUCCESS,value);            
            break;
        case IVOD_RTSP_MSG_PLAYER_CLOSE_FAILED:
            break;
        case IVOD_RTSP_MSG_PLAYER_CLOSE_SUCCESS:
            me->func(me->client_main,event,CLIENT_MSG_PLAYER_CLOSE_SUCCESS,value);            
            break;
        case IVOD_RTSP_MSG_LIVE_TO_TSTV:
            me->func(me->client_main,event,CLIENT_MSG_LIVE_TO_TSTV,value);            
            break;
        case IVOD_RTSP_MSG_TSTV_TO_LIVE:
            me->func(me->client_main,event,CLIENT_MSG_TSTV_TO_LIVE,value);            
            break;
        case IVOD_RTSP_MSG_UPDATE_TIME_INFO:
            me->func(me->client_main,event,CLIENT_MSG_UPDATE_TIME_INFO,value);            
            break;
        case IVOD_RTSP_MSG_UPDATE_VOD_TYPE:
            me->func(me->client_main,event,CLIENT_MSG_UPDATE_VOD_TYPE,value);            
            break;
        case IVOD_RTSP_MSG_PLAYER_DVB_INFO:
            me->func(me->client_main,event,CLIENT_MSG_PLAYER_DVB_INFO,value);            
            break;       
        default:
            break;
    }
    
    return IPANEL_OK;
}

static int ivod_rtsp_callback_get(ivod_rtsp_client_t *rtsp_client, unsigned int event, unsigned int prop, int value)
{
    ivod_rtsp_client_t *me = (ivod_rtsp_client_t *)rtsp_client; 
    FAILED_RETURNX(NULL == me || NULL == me->client_main || NULL == me->func, IPANEL_ERR);
    
    switch(prop)
    {
        case IVOD_RTSP_PROP_LIVE_URL:
            me->func(me->client_main,event,CLIENT_INFO_LIVE_URL,value);
            break;
        case IVOD_RTSP_PROP_TSTV_URL:
            me->func(me->client_main,event,CLIENT_INFO_TSTV_URL,value);
            break;
        case IVOD_RTSP_PROP_PLAYER_ID:
            me->func(me->client_main,event,CLIENT_INFO_PLAYER_ID,value);
            break;
        case IVOD_RTSP_PROP_TIME_INFO:
            me->func(me->client_main,event,CLIENT_INFO_TIME_INFO,value);
            break;
        default:
            break;            
    }

    return IPANEL_OK;
}


static int ivod_rtsp_callback_proc(void *handle, unsigned int event,int p1, int p2)
{
    ivod_rtsp_client_t *me = (ivod_rtsp_client_t*)handle;
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    switch(event)
    {
        case IVOD_RTSP_CALLBACK_TYPE_GET:
            ivod_rtsp_callback_get(me,CLIENT_CALLBACK_TYPE_GET,p1,p2);
            break;
        case IVOD_RTSP_CALLBACK_TYPE_MSG:
            ivod_rtsp_callback_message(me,CLIENT_CALLBACK_TYPE_MSG,p1,p2);
            break;
        case IVOD_RTSP_CALLBACK_TYPE_RESULT:
            ivod_rtsp_callback_result(me,CLIENT_CALLBACK_TYPE_RESULT,p1,p2);
            break;
        default:
            break;
    }

    return IPANEL_OK;
}


unsigned int ivod_rtsp_open(client_t *handle, ivod_msg_func func)
{
    ivod_rtsp_client_t *me = NULL;
    void *vod_mgr_handle = NULL;
    unsigned int vod_name = 0, vod_mode = 0;
    
    FAILED_RETURNX(NULL == handle, (unsigned int)NULL);

    me = (ivod_rtsp_client_t *)calloc(1, sizeof(ivod_rtsp_client_t));
    if(NULL == me)
    {
        INFO("%s[ivod_rtsp_open] calloc mem failed\n", RTSP_MARK);
        goto FAILED;
    }    
    me->client_main = (client_t *)handle;
    if(func)
    {
        me->func = func;
    }
    else
    {
        INFO("%s[ivod_rtsp_open] failed, callback function is null\n", RTSP_MARK);
        goto FAILED;
    }
    /*从VOD公共处理模块获取vod类型*/
    vod_mgr_handle = ivod_module_get_handle();
    if(NULL == vod_mgr_handle)
    {
        INFO("%s[ivod_rtsp_open] get handle failed\n", RTSP_MARK);
        goto FAILED;
    }      
    ivod_module_get_property(vod_mgr_handle, MODULE_IVOD_PROP_NAME, &vod_name);
    /*获取vod模式*/
    ivod_module_get_property(vod_mgr_handle, MODULE_IVOD_PROP_MODE, &vod_mode);    
    switch(vod_name)
    {
        case IVOD_NAME_TELECOM:
            {
                me->ivod_open = ivod_rtsp_telecom_open;
                me->ivod_close = ivod_rtsp_telecom_close;
                me->ivod_proc = ivod_rtsp_telecom_proc;
                me->ivod_get_prop = ivod_rtsp_telecom_get_prop;
                me->ivod_set_prop = ivod_rtsp_telecom_set_prop;
            }
            break;
        case IVOD_NAME_NGOD:
            break;
        case IVOD_NAME_IPANEL:
            {
                me->ivod_open = ivod_rtsp_ipanel_open;
                me->ivod_close = ivod_rtsp_ipanel_close;
                me->ivod_proc = ivod_rtsp_ipanel_proc;
                me->ivod_get_prop = ivod_rtsp_ipanel_get_prop;
                me->ivod_set_prop = ivod_rtsp_ipanel_set_prop;
            }
            break;
        case IVOD_NAME_UNICOM:
            break;
        case IVOD_NAME_DMX:
            break;
        case IVOD_NAME_ISMA:
            {
                me->ivod_open = ivod_rtsp_isma_open;
                me->ivod_close = ivod_rtsp_isma_close;
                me->ivod_proc = ivod_rtsp_isma_proc;
                me->ivod_get_prop = ivod_rtsp_isma_get_prop;
                me->ivod_set_prop = ivod_rtsp_isma_set_prop;
            }
            break;
        default:
            break;
    }

    
    if(me->ivod_open)
    {
        me->ivod_handle = me->ivod_open(handle, me, vod_mode, ivod_rtsp_callback_proc);
        if(0 == me->ivod_handle)
        {
            INFO("%s[ivod_rtsp_open] open vod failed\n");
            goto FAILED;
        }
    }
    
    return (unsigned int)me;

 FAILED:
    if(me)
    {
        if(me->ivod_handle)
        {
            me->ivod_close(me->ivod_handle);
        }
        memset(me,0,sizeof(ivod_rtsp_client_t));
        free(me);
        me = NULL;
    }
    return (unsigned int )NULL;
}

int ivod_rtsp_proc(unsigned int handle, unsigned int event, int p1, int p2)
{
    ivod_rtsp_client_t *me = (ivod_rtsp_client_t *)handle;
    int ret = IPANEL_ERR, index = 0;
    int rtsp_event = 0;
    int client_event_list[] = { CLIENT_EVENT_OPEN,
                            CLIENT_EVENT_PLAY,
                            CLIENT_EVENT_SEEK,
                            CLIENT_EVENT_PAUSE,
                            CLIENT_EVENT_RESUME,
                            CLIENT_EVENT_FORWARD,
                            CLIENT_EVENT_BACKWARD,
                            CLIENT_EVENT_STOP,
                            CLIENT_EVENT_CLOSE,
                            CLIENT_EVENT_TIMER,
                            CLIENT_EVENT_SOCKET_MSG,
                            CLIENT_EVENT_SOCKET_DATA              
                        	};                   
    int rtsp_event_list[] = {   IVOD_RTSP_EVENT_OPEN,
                            IVOD_RTSP_EVENT_PLAY,
                            IVOD_RTSP_EVENT_SEEK,
                            IVOD_RTSP_EVENT_PAUSE,
                            IVOD_RTSP_EVENT_RESUME,
                            IVOD_RTSP_EVENT_FORWARD,
                            IVOD_RTSP_EVENT_BACKWARD,
                            IVOD_RTSP_EVENT_STOP,
                            IVOD_RTSP_EVENT_CLOSE,
                            IVOD_RTSP_EVENT_TIMER,
                            IVOD_RTSP_EVENT_SOCKET_MSG,
                        	IVOD_RTSP_EVENT_SOCKET_DATA  
                            };   

    FAILED_RETURNX(0 == handle || NULL == me->ivod_proc, IPANEL_ERR);
    /*这里将消息转换成RTSP的进行处理*/
    for(index = 0; index < sizeof(client_event_list)/sizeof(client_event_list[0]); index ++)
    {
        if((int)event == client_event_list[index])
        {
            rtsp_event = rtsp_event_list[index];
            break;
        }
    }    
    if(index == sizeof(client_event_list)/sizeof(client_event_list[0])
       && rtsp_event == 0)
    {
        /*命令映射失败*/
        INFO("%s[ivod_rtsp_proc] msg invalid, proc failed\n", RTSP_MARK);
        return IPANEL_ERR;
    }
    me->cur_client_event = event;
    ret = me->ivod_proc(me->ivod_handle, rtsp_event, p1, p2);

    return ret;
}

int ivod_rtsp_close(unsigned int handle)
{
    ivod_rtsp_client_t *me = (ivod_rtsp_client_t *)handle;
    FAILED_RETURNX(0 == me || NULL == me->ivod_close, IPANEL_ERR);
    
    if(me)
    {
        if(me->ivod_handle && me->ivod_close)
        {
            me->ivod_close(me->ivod_handle);
        }
        memset(me,0,sizeof(ivod_rtsp_client_t));
        free(me);
        me = NULL;
    }
    
    return IPANEL_OK;
}

/*提供给上一层CLIENT MAIN设置属性,也可以设置RTSP CLIENT私有的属性,注意属性值定义不能重了*/
int ivod_rtsp_set_prop(unsigned int handle, int prop, int value)
{
    ivod_rtsp_client_t *me = (ivod_rtsp_client_t *)handle;

    FAILED_RETURNX(NULL == me || !me->ivod_handle || NULL == me->ivod_set_prop, IPANEL_ERR);
    switch(prop)
    {
        default:
            break;
    }    
    return IPANEL_OK;
}

int ivod_rtsp_get_prop(unsigned int handle, int prop, void *value)
{
    ivod_rtsp_client_t *me = (ivod_rtsp_client_t *)handle;

    FAILED_RETURNX(NULL == me || !me->ivod_handle || NULL == me->ivod_get_prop, IPANEL_ERR);
    switch(prop)
    {
        default:
            break;
    }
    
    return IPANEL_OK;    
}



