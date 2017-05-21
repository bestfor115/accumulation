/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:RTSP SESSION实现
*********************************************************************/
#include "iplayer_common.h"
#include "ivod_client_main.h"
#include "ivod_rtsp_session.h"
#include "ivod_rtsp_protocol.h"
#include "iplayer_socket.h"
#include "iplayer_player.h"
#include "iplayer_vod_main.h"
#include "ivod_rtsp_session.h"
#include "ivod_rtsp_protocol.h"
#include "ivod_rtsp_client.h"

#define SESSION_MARK "[IVOD][SESSION]"

struct ivod_rtsp_session_s
{
    char url[2048];
    int socket_id;
    client_t *client_main;
    void *vod_handle;
    rtsp_request_func_t request[1];
    ivod_rtsp_protocol_t *protocol;
    ivod_rtsp_event_e    heart_event;

    int connect_server_time;          //连接服务器开始时间
    struct ivod_rtsp_session_s *next;

    int flag_send_setup:1;            //标记是否已经发了setup命令，用于在close判断是否要发命令到服务器
    int flag_connected:1;             //连接服务器是否成功
};

static int ivod_rtsp_session_proc_event(ivod_rtsp_session_t *me, unsigned int event, int p1, int p2)
{
    return IPANEL_ERR;
}

static int ivod_rtsp_session_event_callback(void *me, unsigned int event, int p1, int p2)
{
    return ivod_rtsp_session_proc((ivod_rtsp_session_t*)me,event,p1,p2);
}

ivod_rtsp_session_t *ivod_rtsp_session_open(client_t *client_main, void *vod_handle, rtsp_request_func_t *request)
{
    ivod_rtsp_session_t *me = NULL;

    FAILED_RETURNX(NULL == client_main 
                || NULL == vod_handle 
                || NULL == request, NULL);
    me = (ivod_rtsp_session_t *)calloc(1, sizeof(ivod_rtsp_session_t));
    if(NULL == me)
    {
        INFO("%s[ivod_rtsp_session_open] calloc failed\n", SESSION_MARK);
        goto FAILED;
    }

    me->client_main = client_main;
    me->vod_handle = vod_handle;
    me->connect_server_time = 0;
    me->flag_send_setup = 0;
    me->flag_connected = 0;
    
    memcpy(me->request, request, sizeof(rtsp_request_func_t));
    
    me->protocol = ivod_rtsp_protocol_open((void *)client_main, (void *)me, ivod_rtsp_session_event_callback);
    if(NULL == me->protocol)
    {
        INFO("%s[ivod_rtsp_session_open] open failed\n", SESSION_MARK);
        goto FAILED;
    }
    
    return me;

FAILED:
    if(me)
    {
        if(me->protocol)
        {
            ivod_rtsp_protocol_close(me->protocol);
            me->protocol = NULL;
        }

        free(me);
        me = NULL;
    }
    return NULL;
}

int ivod_rtsp_session_close(ivod_rtsp_session_t *me)
{
    if(me)
    {
        if(me->protocol)
        {
            ivod_rtsp_protocol_close(me->protocol);
            me->protocol = NULL;
        }
        memset(me,0,sizeof(ivod_rtsp_session_t));
        free(me);
        me = NULL;
    }
    
    return IPANEL_ERR;
}

int ivod_rtsp_session_proc(ivod_rtsp_session_t *me, unsigned int event, int p1, int p2)
{
    int ret = IPANEL_ERR;
    char *url = NULL;
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR); 

    /*判断socket是否创建，如果没有创建不能发命令*/
    if(event < IVOD_RTSP_ACK_OPEN && event != IVOD_RTSP_EVENT_OPEN && me->socket_id <= 0)
    {
        if(IVOD_RTSP_EVENT_CLOSE == event)
        {
            if(me->request->rtsp_response)
            {
                ret = me->request->rtsp_response(me->vod_handle, me, IVOD_RTSP_ACK_CLOSE, 0, 0);
            }
        }
        else
        {
            /*向页面发送错误消息*/
            ivod_rtsp_session_proc(me, IVOD_RTSP_ACK_ERROR_RESPONSE, 1, 0); 
        }
        return ret;
    }
    
    switch(event)
    {
        case IVOD_RTSP_EVENT_OPEN:
            url = (char *)p1;

            INFO("%s[ivod_rtsp_session_proc] RTSP_EVENT_OPEN is start\n",SESSION_MARK);
            
            if(me->socket_id > 0)
            {
                ivod_rtsp_protocol_disconnect(me->protocol);
                me->socket_id = 0;
            }
            me->socket_id = ivod_rtsp_protocol_connect(me->protocol, url);
            if(me->socket_id <= 0)
            {
                INFO("%s[ivod_rtsp_session_proc]connect server failed\n",SESSION_MARK);
                ivod_rtsp_session_proc(me, IVOD_RTSP_ACK_OPEN, -1, 0);                
                ret = IPANEL_ERR;
                break;
            }
            strncpy(me->url, url, sizeof(me->url)); 
            me->connect_server_time = (int)time_ms();

            ret = IPANEL_OK;
            INFO("%s[ivod_rtsp_session_proc]RTSP_EVENT_OPEN is end\n",SESSION_MARK);
            break;
        case IVOD_RTSP_EVENT_CLOSE:
            if(me->request->rtsp_close 
              && me->request->rtsp_close(me->vod_handle, me, event, p1, 0) > 0
              && me->flag_send_setup)
            {
                ret = ivod_rtsp_protocol_teardown(me->protocol, me->url);
                /*回调消息给上一层*/                
            }
            
            if(me->request->rtsp_response)
            {
                ret = me->request->rtsp_response(me->vod_handle, me, IVOD_RTSP_ACK_CLOSE, 0, 0);
            }
            break;
        case IVOD_RTSP_EVENT_DESCRIBE:
            if(me->request->rtsp_describe
              && me->request->rtsp_describe(me->vod_handle, me, event, p1, 0) > 0)
            {
                ret = ivod_rtsp_protocol_describe(me->protocol, me->url);
            }            
            break;
        case IVOD_RTSP_EVENT_SETUP:
            if(me->request->rtsp_setup
              && me->request->rtsp_setup(me->vod_handle, me, event, p1, 0) > 0)
            {
                ret = ivod_rtsp_protocol_setup(me->protocol, me->url);
                me->flag_send_setup = 1;
            }
            break;
        case IVOD_RTSP_EVENT_PLAY:
        case IVOD_RTSP_EVENT_RESUME:
        case IVOD_RTSP_EVENT_SEEK:
        case IVOD_RTSP_EVENT_BACKWARD:
        case IVOD_RTSP_EVENT_FORWARD:
            if(me->request->rtsp_play
              && me->request->rtsp_play(me->vod_handle, me, event, p1, 0) > 0)
            {
                ret = ivod_rtsp_protocol_play(me->protocol, me->url);
            }            
            break;
        case IVOD_RTSP_EVENT_PAUSE:
            if(me->request->rtsp_pause
              && me->request->rtsp_pause(me->vod_handle, me, event, p1, 0) > 0)
            {
                ret = ivod_rtsp_protocol_pause(me->protocol, me->url);
            } 
            break;
        case IVOD_RTSP_EVENT_VALIDATE:
            if(me->request->rtsp_validate
              && me->request->rtsp_validate(me->vod_handle, me, event, p1, 0) > 0)
            {
                ret = ivod_rtsp_protocol_validate(me->protocol, me->url);
            }     
            break;
        case IVOD_RTSP_EVENT_SET_PARAM:
            if(me->request->rtsp_set_parameter
              && me->request->rtsp_set_parameter(me->vod_handle, me, event, p1, 0) > 0)
            {
                ret = ivod_rtsp_protocol_setparameter(me->protocol, me->url);
            }     
            break;  
        case IVOD_RTSP_EVENT_GET_PARAM:
            if(me->request->rtsp_get_parameter
              && me->request->rtsp_get_parameter(me->vod_handle, me, event, p1, 0) > 0)
            {
                ret = ivod_rtsp_protocol_getparameter(me->protocol, me->url);
            }     
            break;            
        case IVOD_RTSP_EVENT_OPTIONS:
            if(me->request->rtsp_options
              && me->request->rtsp_options(me->vod_handle, me, event, p1, 0) > 0)
            {
                ret = ivod_rtsp_protocol_options(me->protocol, "*");
            }     
            break;  
        case IVOD_RTSP_EVENT_PING:
            if(me->request->rtsp_ping
              && me->request->rtsp_ping(me->vod_handle, me, event, p1, 0) > 0)
            {
                ret = ivod_rtsp_protocol_ping(me->protocol, me->url);
            }     
            break;
    	case IVOD_RTSP_ACK_CLOSE:
    	case IVOD_RTSP_ACK_ERROR_RESPONSE:
    	case IVOD_RTSP_ACK_ANNOUNCE_RESPONSE:
    	case IVOD_RTSP_ACK_OPTION:		
    	case IVOD_RTSP_ACK_DESCRIBE:
    	case IVOD_RTSP_ACK_SETUP:
    	case IVOD_RTSP_ACK_PLAY:	
    	case IVOD_RTSP_ACK_PAUSE:
    	case IVOD_RTSP_ACK_OPEN:		
    	case IVOD_RTSP_ACK_SET_PARAM:
    	case IVOD_RTSP_ACK_GET_PARAM:
        case IVOD_RTSP_ACK_VADILATE:  
            if(me->request->rtsp_response)
                ret = me->request->rtsp_response(me->vod_handle, me, event, p1, 0);
            break;
        case IVOD_RTSP_EVENT_TIMER:
            if(me->flag_connected)
                ret = ivod_rtsp_protocol_proc(me->protocol, IVOD_RTSP_EVENT_TIMER, 0, 0);
            else
            {
                //连接服务器5000ms，发送连接失败消息
                if(me->connect_server_time && (int)time_ms() - me->connect_server_time >= 5000)
                {
                    ivod_rtsp_session_proc(me, IVOD_RTSP_ACK_OPEN, -1, 0); 
                    me->connect_server_time = 0;
                }
            }
            break;
        case IVOD_RTSP_EVENT_SOCKET_MSG:
            /*只有socket id相等才处理*/
            if(p1 == me->socket_id)
            {
                if(p2 == SOCKET_CONNECT_SUCCESS)
                {
                    //传递rtsp交互命令socket传递到vod
                    me->flag_connected = 1;
                    ret = ivod_rtsp_session_proc(me, IVOD_RTSP_ACK_OPEN, p1, 0);
                }
                else if(p2 == SOCKET_CONNECT_FAILED)
                {
                    ret = ivod_rtsp_session_proc(me, IVOD_RTSP_ACK_OPEN, -1, 0);
                }
                else
                {
                    ret = ivod_rtsp_session_proc(me, IVOD_RTSP_ACK_ERROR_RESPONSE, 0, 0);
                }                    
            }            
            break;
        case IVOD_RTSP_EVENT_SOCKET_DATA:
            /*这里可能要将p2转换成rtsp_protocol_proc的参数*/
            ret = ivod_rtsp_protocol_proc(me->protocol, event, p1, p2);
            break;
        default:
            break;
    }    

    return ret;
}

ivod_rtsp_session_t *ivod_rtsp_session_get_next(ivod_rtsp_session_t *session)
{
    FAILED_RETURNX(NULL == session, NULL);

    return session->next;
}

char *ivod_rtsp_session_get_field(ivod_rtsp_session_t *me, char *field, char flag)
{
    FAILED_RETURNX(NULL == me, NULL);
    return ivod_rtsp_protocol_get_field(me->protocol, field, flag);
}

char **ivod_rtsp_session_get_field_address(ivod_rtsp_session_t *me, char *field)
{
    FAILED_RETURNX(NULL == me, NULL);
    return ivod_rtsp_protocol_get_field_address(me->protocol, field);    
}

int ivod_rtsp_session_add_field(ivod_rtsp_session_t *me, char *field)
{
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    return ivod_rtsp_protocol_add_field(me->protocol, field);
}

int ivod_rtsp_session_add_cseq(ivod_rtsp_session_t *me)
{    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    return ivod_rtsp_protocol_add_cseq(me->protocol);
}

int ivod_rtsp_session_set_session(ivod_rtsp_session_t *me, char *session_id)
{
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    return ivod_rtsp_protocol_set_session(me->protocol, session_id);
}

int ivod_rtsp_session_add_session(ivod_rtsp_session_t *me)
{
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    return ivod_rtsp_protocol_add_session(me->protocol);
}


int ivod_rtsp_session_set_url(ivod_rtsp_session_t *me, char *url)
{
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    memset(me->url,0,sizeof(me->url));
    strcpy(me->url, url); 
    return IPANEL_OK;
}


int ivod_rtsp_session_get_url(ivod_rtsp_session_t *me, char *url)
{
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    if(url)
    {
        strcpy(url,me->url); 
        return IPANEL_OK;
    }

    return IPANEL_ERR;
}

