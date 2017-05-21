/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:iPanel vod客户端实现
*********************************************************************/
#include "iplayer_socket.h"
#include "iplayer_player.h"
#include "iplayer_vod_main.h"
#include "ivod_rtsp_client.h"
#include "ivod_rtsp_ipanel.h"
#include "ivod_rtsp_session.h"
#include "ivod_client_time.h"

#define IPANEL_VOD_USER_AGENT "User-Agent: iPanel V1.0\r\n"
#define IPANEL_MARK "[IVOD][IPANEL]"


typedef struct ivod_rtsp_ipanel_s
{
    client_t *client_main;
    ivod_rtsp_client_t *rtsp_client;
    ivod_rtsp_session_t *rtsp_session;
    ivod_msg_func func;     //回调函数

    char url[2048];         //点播地址
    int  type;              //播放类型liveTV、TSTV、VOD
    int  mode;              //播放模式IP或DVB
    int  server_name;       //前端服务器
    int  speed;             //当前播放速度
    int player_id;                          //播放器id
    int sock_id;                            //数据通道socket，一般是udp类型，tcp类型socket不需要创建
    int current_op;                         //当前操作命令

    int heart_event;                        //心跳
    int heart_time;                         //心跳时间间隔
    int heart_start_time;                   //心跳起始时间

    IPAddr0 serverIP[1];                    //服务器ip地址

    unsigned int flag_first_play:1;         //第一次play标记
}ivod_rtsp_ipanel_t;

static int ivod_rtsp_ipanel_response_describe(ivod_rtsp_ipanel_t *me, ivod_rtsp_session_t *session)
{
    FAILED_RETURNX(NULL == me, IPANEL_ERR); 

    return ivod_rtsp_session_proc(session, IVOD_RTSP_EVENT_SETUP, 0, 0);
}

static int ivod_rtsp_ipanel_response_setup(ivod_rtsp_ipanel_t *me, ivod_rtsp_session_t *session)
{
    char *ptr = NULL, *p_str = NULL;
    unsigned int index = 0, i = 0;
    char  key_word[64][32] = {0};
    char  key_value[64][32] = {0};
    iplayer_info_t info[1] = {0};
    int player_id = 0;
    int type = 0,client_port = 0;
    IPAddr0 ipaddr[1] = {0};
    int stream_socket_type = ISOCKET_TYPE_TCP;
    client_time_t time_info[1] = {0};
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR); 

    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_GET, IVOD_RTSP_PROP_TIME_INFO, (int)time_info);
    
    /*获取session id*/
    ptr = ivod_rtsp_session_get_field(session, "Session", ':');
    if(NULL == ptr)
    {
        INFO("%s[ivod_rtsp_ipanel_response_setup] get Session failed\n", IPANEL_MARK);
        return IPANEL_ERR;
    }

    ivod_rtsp_session_set_session(session, ptr);

    /*获取server*/
    ptr = ivod_rtsp_session_get_field(session, "Server", ':');
    if(NULL == ptr)
    {
        INFO("%s[ivod_rtsp_ipanel_response_setup] get Server failed\n", IPANEL_MARK);
        me->server_name = IVOD_RTSP_SERVER_NONE;
    }
    else
    {
        INFO("%s[ivod_rtsp_ipanel_response_setup] get Server = %s\n", IPANEL_MARK,ptr);
        if(strstr(ptr,"Ipanelserver"))
            me->server_name = IVOD_RTSP_SERVER_IPANEL;
        else
            me->server_name = IVOD_RTSP_SERVER_NONE;
    }
    
    /*获取Transport参数解析*/
    ptr = ivod_rtsp_session_get_field(session, "Transport", ':');
    if(NULL == ptr)
    {
        INFO("%s[ivod_rtsp_ipanel_response_setup] get Transport failed\n", IPANEL_MARK);
        return IPANEL_ERR;
    }

    if(strstr(ptr,"MP2T/AVP/UDP"))
    {
        stream_socket_type = ISOCKET_TYPE_UDP;
        info->mode = IPLAYER_MODE_IP_TS;
    }
    else
    {
        /*预留扩展*/
    }
        
    strcpy(key_word[index++],"program-number=");
    strcpy(key_word[index++],"frequency=");
    strcpy(key_word[index++],"duration=");
    strcpy(key_word[index++],"source=");
    strcpy(key_word[index++],"port=");
    strcpy(key_word[index++],"destination=");

    for( i = 0; i < index; i++) 
    {
        char *end = NULL;
        p_str = strstr(ptr, key_word[i]);
        if(p_str) 
        {
            p_str = p_str + strlen(key_word[i]);
            end = strchr(p_str,';');
            if(end == NULL) 
                end = strchr(p_str,'\r');
            if(end == NULL) 
                continue;
            memcpy(key_value[i], p_str, end - p_str);
            INFO("%s[ivod_rtsp_ipanel_response_setup] key_word = %s, key_value = %s\n", IPANEL_MARK,key_word[i], key_value[i]);
        }
    }    

    
    for( i = 0; i < sizeof(key_word)/sizeof(key_word[0]); i++) 
    {
        if(strlen(key_word[i]) == 0 || strlen(key_value[i]) == 0)
            continue;
        /*获取Frequency*/
        if(strcmp(key_word[i], "qam_frq=") == 0) 
        {
            char buf[64] = {0};
            sprintf(buf, "%s00", key_value[i]);            
            info->dvb_info.frequency = strtoul(buf, NULL, 16);
        }
        /*获取Symbolrate*/
        else if (strcmp(key_word[i], "Symbolrate=") == 0 )
        {
            info->dvb_info.symbolrate = atoi(key_value[i])*10;
        }
        /*获取QAM*/
        else if (strcmp(key_word[i], "qam=") == 0 )
        {
            info->dvb_info.qam = atoi(key_value[i]);
        }
        /*获取pmt_pid*/
        else if (strcmp(key_word[i], "pmt_pid=") == 0 )
        {
            info->dvb_info.pmt_pid = atoi(key_value[i]);				
        }
        else if (strcmp(key_word[i], "stb_mode=") == 0 )
        {
            if(strcmp(key_value[i], "qam") == 0 ||strcmp(key_value[i], "QAM") == 0)
                info->mode = IPLAYER_MODE_QAM_TS;
            else
                info->mode = IPLAYER_MODE_IP_TS;
        }
        else if(strcmp(key_word[i], "program-number=") == 0)
        {
            info->dvb_info.service_id = atoi(key_value[i]);
        }
        else if(strcmp(key_word[i], "frequency=") == 0)
        {
            char buf[64] = {0};
            
            sprintf(buf, "%d0000", atoi(key_value[i]));
			info->dvb_info.frequency  = strtoul(buf, NULL, 16);
        }
        else if(strcmp(key_word[i], "duration=") == 0)
        {
            time_info->duration = atoi(key_value[i]);
        }
        else if(strcmp(key_word[i], "source=") == 0)
        {
          
        }
        else if(strcmp(key_word[i], "destination=") == 0)
        {
        
        }
        else if(strcmp(key_word[i], "port=") == 0)
        {
            client_port = atoi(key_value[i]);
        }
    }

    /*设置心跳*/
    me->heart_event = IVOD_RTSP_EVENT_GET_PARAM;
    me->heart_time = 10000;
    me->heart_start_time = (int)time_ms();

    
    if(info->mode == IPLAYER_MODE_NONE)
    {
        if(me->mode == IVOD_MODE_DVB)
            info->mode = IPLAYER_MODE_QAM_TS;
        else
            info->mode = IPLAYER_MODE_IP_TS;
    }

    if(info->mode == IPLAYER_MODE_IP_TS)
    {
        time_info->flag_support_pts = 1;
        /*通知更新时间*/
        me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_UPDATE_TIME_INFO, (int)time_info);
    }

    if(stream_socket_type == ISOCKET_TYPE_UDP && info->mode == IPLAYER_MODE_IP_TS)
    {
        int socket_version = IPANEL_IP_VERSION_4;

        if(me->serverIP->version > 0)
        {
            socket_version = me->serverIP->version;
        }
        
        /*创建socket*/
        me->sock_id = isocket_open(ISOCKET_TYPE_UDP, socket_version, me->client_main);
        if(me->sock_id <= 0)
        {
            INFO("%s[ivod_rtsp_ipanel_response_setup]socket open failed\n", IPANEL_MARK);
            goto OPEN_FAILED;
        }
        /*bind 端口*/
        memset(ipaddr,0,sizeof(IPAddr0));
        ipaddr->version = socket_version;
        if(isocket_bind(me->sock_id, (unsigned int)ipaddr, client_port) < 0)
        {
            INFO("%s[ivod_rtsp_ipanel_response_setup]socket bind failed\n", IPANEL_MARK);
            goto OPEN_FAILED;
        }
    }
   
    /*获取player_id*/
    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_GET, IVOD_RTSP_PROP_PLAYER_ID, (int)&player_id);
    if(player_id <= 0)
    {
        player_id = iplayer_open(me->client_main, info);
        if(player_id <= 0)
        {
            INFO("%s[ivod_rtsp_ipanel_response_setup] player open failed\n", IPANEL_MARK);
            /*打开PLAYER失败*/
            me->func(me->rtsp_client,IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_PLAYER_OPEN_FAILED, 0);
            goto OPEN_FAILED;
        }
        /*打开player成功*/
        me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_PLAYER_OPEN_SUCCESS, player_id);
    }
    me->player_id = player_id;    

    /*命令响应成功*/
    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_OPEN, IPANEL_OK);
    
	return IPANEL_OK;
    
OPEN_FAILED:
    
    /*命令执行失败*/
    if(me->sock_id > 0)
    {
        isocket_close(me->sock_id);
        me->sock_id = 0;
    }
    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_OPEN, IPANEL_ERR);
    
    return IPANEL_ERR;
}

static int ivod_rtsp_ipanel_response_play(ivod_rtsp_ipanel_t *me, ivod_rtsp_session_t *session)
{
    char *ptr = NULL;
    int speed = 0;
    active_info_t info[1] = {0};
    client_time_t time_info[1] = {0};
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_GET, IVOD_RTSP_PROP_TIME_INFO, (int)time_info);

    /*获取速率*/
    ptr = ivod_rtsp_session_get_field(session, "Scale", ':');
    if(ptr)
    {
        speed = atoi(ptr);
    }
    else
    {
        speed = me->speed;
    }
    
    /*获取range*/
    ptr = ivod_rtsp_session_get_field(session, "Range", ':');
    if(NULL == ptr)
        ptr = ivod_rtsp_session_get_field(session, "range", ':');
    
    if(ptr)
    {
        if(strstr(ptr, "npt="))
        {            
            ptr = strstr(ptr, "npt=") + strlen("npt=");
        }
        else if(strstr(ptr, "clock="))/*clock*/
        {            
            ptr = strstr(ptr, "clock=") + strlen("clock=");
        }

        if(ptr)
        {
            int time_type = CLIENT_TIME_TYPE_NONE;
            char from[64] = {0}, to[64] = {0};
            
            client_time_parse_string_time(ptr, from, to);

            if(strlen(from))
            {
                client_time_string_to_seconds(from, &time_info->start_time, &time_type);
                client_time_update_current_time(time_info, time_info->start_time);
                time_info->time_type = time_type;
            }

            if(strlen(to))
            {
                client_time_string_to_seconds(to, &time_info->end_time, &time_type);
                if(time_info->end_time > time_info->start_time)
                    time_info->duration = time_info->end_time - time_info->start_time;
            }

            if(strlen(from) || strlen(to))
            {
                /*通知更新时间*/
                me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_UPDATE_TIME_INFO, (int)time_info);                
            }
        }
        
    }
    /*获取RTP-Info*/
    ptr = ivod_rtsp_session_get_field(session, "RTP-Info", ':');
    if(ptr)
    {
        /*解析rtp info*/
        
    }
    
    /*通知播放器播放成功*/
    iplayer_play(me->player_id, me->current_op, info);
    
    /*命令响应成功*/
    if(IPLAYER_OP_PLAY == me->current_op)
        me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_PLAY, speed);
    else if(IPLAYER_OP_RESUME == me->current_op)
        me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_RESUME, speed);
    else if(IPLAYER_OP_SEEK == me->current_op)
        me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_SEEK, speed);
    else if(IPLAYER_OP_FWBW == me->current_op)
    {
        if(speed > 1)
            me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_FORWARD, speed);
        else if(speed < 0)
            me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_BACKWARD, speed*(-1));
    }
    
    return IPANEL_OK;
}

static int ivod_rtsp_ipanel_describe(void *ipanel_handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    ivod_rtsp_ipanel_t *me = (ivod_rtsp_ipanel_t *)ipanel_handle;
    int seq = 0;
    char buffer[256] = {0};
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    /*添加seq*/
    seq = ivod_rtsp_session_add_cseq(session);
    /*添加User Agent*/
    strcpy(buffer, IPANEL_VOD_USER_AGENT);
	ivod_rtsp_session_add_field(session, buffer);
    /*添加\r\n\r\n*/
	ivod_rtsp_session_add_field(session, END_STRING);

    return seq;
}

static int ivod_rtsp_ipanel_setup(void *ipanel_handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    ivod_rtsp_ipanel_t *me = (ivod_rtsp_ipanel_t *)ipanel_handle;
    int seq = 0;
    char buffer[256] = {0};
    char ip[64] = {0};
    int port = 0;
	IPANEL_NETWORK_IF_PARAM localIP[1] = { 0 };
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    /*添加seq*/
    seq = ivod_rtsp_session_add_cseq(session);
    /*添加Transport*/
    if(IPANEL_IP_VERSION_6 != me->serverIP->version)
    {
        ivod_common_get_localIP(NULL, ip, NULL);
    }
    else
    {
        ivod_common_get_localIP(NULL, NULL, ip);
    }
    
    port = ivod_common_get_dynamic_port();
    
    if(me->mode == IVOD_MODE_IP)
        sprintf(buffer, "Transport: MP2T/AVP/UDP;unicast;destination=%s;port=%d\r\n", ip, port);
    else
        sprintf(buffer, "Transport:MP2T/DVBC/QAM\r\n");
    ivod_rtsp_session_add_field(session, buffer);
    /*添加User Agent*/
    memset(buffer, 0, sizeof(buffer));
    strcpy(buffer, IPANEL_VOD_USER_AGENT);
	ivod_rtsp_session_add_field(session, buffer);
    /*添加\r\n\r\n*/
	ivod_rtsp_session_add_field(session, END_STRING);

    return seq;  
}        

static int ivod_rtsp_ipanel_play(void *ipanel_handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    ivod_rtsp_ipanel_t *me = (ivod_rtsp_ipanel_t *)ipanel_handle;
    char seek_from[64] = {0}, seek_to[64] = {0};
    char *ptr = NULL;
    int ret = IPANEL_OK, speed = 0, seq = 0;
    char range[128] = {0}, buffer[128] = {0};
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    /*计算speed以及seek_time*/
    switch(msg)
    {
        case IVOD_RTSP_EVENT_PLAY:
            speed = 1;
            if(me->flag_first_play)
                strcpy(range, "Range: npt=beginning-\r\n");
            else
                strcpy(range, "Range: npt=now-\r\n");
            me->current_op = IPLAYER_OP_PLAY;
            break;
        case IVOD_RTSP_EVENT_RESUME:
            speed = 1;
            if(me->flag_first_play)
                strcpy(range, "Range: npt=beginning-\r\n");
            else
                strcpy(range, "Range: npt=now-\r\n");
            me->current_op = IPLAYER_OP_RESUME;
            break;
        case IVOD_RTSP_EVENT_SEEK:
            ptr = (char *)p1;
            
            speed = 1;
            /*解析seek_time*/
            client_time_parse_string_time(ptr, seek_from, seek_to);                
            sprintf(range, "Range: npt=%s-%s\r\n", seek_from, seek_to);
            me->current_op = IPLAYER_OP_SEEK;
            break;
        case IVOD_RTSP_EVENT_FORWARD:
        case IVOD_RTSP_EVENT_BACKWARD:   
            speed = p1;
            strcpy(range, "Range: npt=now-\r\n");
            me->current_op = IPLAYER_OP_FWBW;
            break;
        default:
            INFO("%s[ivod_rtsp_ipanel_play] failed, event invalid\n",IPANEL_MARK);
            return IPANEL_ERR;
    }

    me->speed = speed;

    /*通知player做相应操作*/
    iplayer_notify(me->player_id, me->current_op, speed);
    
    /*添加seq*/
    seq = ivod_rtsp_session_add_cseq(session);
    /*添加session id*/
    ivod_rtsp_session_add_session(session);
    /*添加Scale*/
    sprintf(buffer, "Scale: %d.0\r\n", speed);
    ivod_rtsp_session_add_field(session, buffer);
    /*添加Range*/
    ivod_rtsp_session_add_field(session, range);
    /*添加User Agent*/
	ivod_rtsp_session_add_field(session, IPANEL_VOD_USER_AGENT);
    /*添加\r\n\r\n*/
	ivod_rtsp_session_add_field(session, END_STRING);

    me->flag_first_play = 0;

    return seq;
}         

static int ivod_rtsp_ipanel_pause(void *ipanel_handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    ivod_rtsp_ipanel_t *me = (ivod_rtsp_ipanel_t *)ipanel_handle;
    int seq = 0;
    char buffer[256] = {0};
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    /*通知播放器播放暂停*/
    iplayer_pause(me->player_id);
    
    /*添加seq*/
    seq = ivod_rtsp_session_add_cseq(session);
    /*添加User Agent*/
    strcpy(buffer, IPANEL_VOD_USER_AGENT);
	ivod_rtsp_session_add_field(session, buffer);
    /*添加\r\n\r\n*/
	ivod_rtsp_session_add_field(session, END_STRING);

    me->current_op = IPLAYER_OP_PAUSE;

    return seq;
    
}        

static int ivod_rtsp_ipanel_teardown(void *ipanel_handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    ivod_rtsp_ipanel_t *me = (ivod_rtsp_ipanel_t *)ipanel_handle;
    int seq = 0;
    char buffer[256] = {0};
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    
    /*添加seq*/
    seq = ivod_rtsp_session_add_cseq(session);
    /*添加User Agent*/
    strcpy(buffer, IPANEL_VOD_USER_AGENT);
	ivod_rtsp_session_add_field(session, buffer);
    /*添加\r\n\r\n*/
	ivod_rtsp_session_add_field(session, END_STRING);

    me->current_op = IPLAYER_OP_CLOSE;

    return seq;
    
}       

static int ivod_rtsp_ipanel_get_parameter(void *ipanel_handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    ivod_rtsp_ipanel_t *me = (ivod_rtsp_ipanel_t *)ipanel_handle;
    int seq = 0;
    char buffer[256] = {0};
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    
    /*添加seq*/
    seq = ivod_rtsp_session_add_cseq(session);
    /*添加User Agent*/
    strcpy(buffer, IPANEL_VOD_USER_AGENT);
	ivod_rtsp_session_add_field(session, buffer);
    /*添加\r\n\r\n*/
	ivod_rtsp_session_add_field(session, END_STRING);

    return seq;
}

static int ivod_rtsp_ipanel_set_parameter(void *ipanel_handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    return IPANEL_OK;
}

static int ivod_rtsp_ipanel_option(void *ipanel_handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    ivod_rtsp_ipanel_t *me = (ivod_rtsp_ipanel_t *)ipanel_handle;
    int seq = 0;
    char buffer[256] = {0};
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    
    /*添加seq*/
    seq = ivod_rtsp_session_add_cseq(session);
    /*添加User Agent*/
    strcpy(buffer, IPANEL_VOD_USER_AGENT);
	ivod_rtsp_session_add_field(session, buffer);
    /*添加\r\n\r\n*/
	ivod_rtsp_session_add_field(session, END_STRING);

    return seq;
}     

static int ivod_rtsp_ipanel_validate(void *ipanel_handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    return IPANEL_OK;
}     

static int ivod_rtsp_ipanel_ping(void *ipanel_handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    return IPANEL_OK;
}         

static int ivod_rtsp_ipanel_response(void *ipanel_handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    ivod_rtsp_ipanel_t *me = (ivod_rtsp_ipanel_t *)ipanel_handle;
    int seq = 0;
    char buffer[256] = {0};
    char ip[64] = {0};
    unsigned port = 0;
    int ret = IPANEL_OK;
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR); 
    
    INFO("%s[ivod_rtsp_ipanel_response] msg=%d, p1=%d, p2=%d\n", IPANEL_MARK,msg,p1,p2); 
    
    switch(msg)
    {
        case IVOD_RTSP_ACK_OPEN:
            {
                IPAddr0 ipaddr[1] = {0};
                unsigned int temp_port = 0;

                /*解析url，获取服务器ip地址*/
                if(IPANEL_OK == ivod_common_parse_url(me->url,(unsigned int)ipaddr,&temp_port))
                {
                    memcpy(me->serverIP,ipaddr,sizeof(IPAddr0));
                }
                else
                {
                    /*默认ipv4版本*/
                    memcpy(me->serverIP,0,sizeof(IPAddr0));
                    me->serverIP->version = IPANEL_IP_VERSION_4;
                }

                me->flag_first_play = 1;
				ret = ivod_rtsp_session_proc(session, IVOD_RTSP_EVENT_DESCRIBE, p1, p2);
            }
            break;
        case IVOD_RTSP_ACK_DESCRIBE:
            ret = ivod_rtsp_ipanel_response_describe(me, session);
            break;  
        case IVOD_RTSP_ACK_SETUP:
            ret = ivod_rtsp_ipanel_response_setup(me, session);
            break;
        case IVOD_RTSP_ACK_PLAY:
            ret = ivod_rtsp_ipanel_response_play(me, session);
            break;
        case IVOD_RTSP_ACK_GET_PARAM:
            {
                char *ptr = NULL;
                client_time_t time_info[1] = {0};
                me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_GET, IVOD_RTSP_PROP_TIME_INFO, (int)time_info);
                ptr = ivod_rtsp_session_get_field(session, "Position", ':');
                if(NULL == ptr)
                    ptr = ivod_rtsp_session_get_field(session, "position", ':');

                if(ptr)
                {
                    int time_type = CLIENT_TIME_TYPE_NONE;
                    char from[64] = {0}, to[64] = {0};
                    unsigned int temp_time = 0;
                    
                    client_time_parse_string_time(ptr, from, to);

                    if(strlen(from))
                    {
                        client_time_string_to_seconds(from, &temp_time, &time_type);
                        client_time_update_current_time(time_info, temp_time);
                        time_info->time_type = time_type;

                        /*通知更新时间*/
                        me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_UPDATE_TIME_INFO, (int)time_info);                
                    }
                }             
            }
            break;
        case IVOD_RTSP_ACK_CLOSE:
            {
                /*关闭会话*/
                if(me->rtsp_session)
                {
                    ivod_rtsp_session_close(me->rtsp_session);
                    me->rtsp_session = NULL;
                }

                /*关闭socket*/
                if(me->sock_id > 0)
                {
                    isocket_close(me->sock_id);
                    me->sock_id = 0;
                }        
                               
                /*命令响应成功*/
                me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_CLOSE, IPANEL_OK);
            }
            break;
    }
    
    return ret;
}     

static int ivod_rtsp_ipanel_callback_session_get(void *handle, unsigned int op, void *value)
{

    return IPANEL_ERR;    
}

static int ivod_rtsp_ipanel_callback_session_message(void *handle, unsigned int msg, int value)
{
    return IPANEL_ERR;
}

unsigned int ivod_rtsp_ipanel_open(client_t *client_main, ivod_rtsp_client_t *rtsp_client, int mode, ivod_msg_func func)
{
    rtsp_request_func_t request[1] = {0};
    ivod_rtsp_ipanel_t *me = NULL;
    
    FAILED_RETURNX(NULL == client_main 
                   || NULL == rtsp_client
                   || NULL == func, (unsigned int)NULL);
    
    me = (ivod_rtsp_ipanel_t *)calloc(1, sizeof(ivod_rtsp_ipanel_t));
    if(NULL == me)
    {
        INFO("%s[ivod_rtsp_ipanel_open] calloc failed\n", IPANEL_MARK);
        goto FAILED;
    }

    memset(me,0,sizeof(ivod_rtsp_ipanel_t));
    me->client_main = client_main;
    me->rtsp_client = rtsp_client;    
    if(mode == IVOD_MODE_NONE)
        me->mode = IVOD_MODE_IP;
    else    
        me->mode = mode;

    me->func = func;

    request->rtsp_describe      = ivod_rtsp_ipanel_describe;
    request->rtsp_setup         = ivod_rtsp_ipanel_setup;
    request->rtsp_play          = ivod_rtsp_ipanel_play;
    request->rtsp_pause         = ivod_rtsp_ipanel_pause;
    request->rtsp_close         = ivod_rtsp_ipanel_teardown;
    request->rtsp_get_parameter = ivod_rtsp_ipanel_get_parameter;
    request->rtsp_set_parameter = ivod_rtsp_ipanel_set_parameter;
    request->rtsp_options       = ivod_rtsp_ipanel_option;	
    request->rtsp_validate      = ivod_rtsp_ipanel_validate;
    request->rtsp_ping          = ivod_rtsp_ipanel_ping;
    request->rtsp_response      = ivod_rtsp_ipanel_response;  

    /*注册一个session*/
    me->rtsp_session = ivod_rtsp_session_open(me->client_main, me, request);
    if(NULL == me->rtsp_session)
    {
        INFO("%s[ivod_rtsp_ipanel_open] rtsp_session_register failed\n", IPANEL_MARK);
        goto FAILED;
    }
    
    INFO("%s[ivod_rtsp_ipanel_open] rtsp_ipanel_open success\n", IPANEL_MARK);
    return (unsigned int)me;
    
FAILED:
    if(me)
    {
        if(me->rtsp_session)
        {
            ivod_rtsp_session_close(me->rtsp_session);
            me->rtsp_session = NULL;
        }
        memset(me,0,sizeof(ivod_rtsp_ipanel_t));
        free(me);
        me = NULL;
    }
    
    return (unsigned int)NULL;
}

int ivod_rtsp_ipanel_close(unsigned int handle)
{
    ivod_rtsp_ipanel_t *me = (ivod_rtsp_ipanel_t*)handle;

    if(me)
    {

        /*ipanel vod的播放器可以在这里关闭，其他vod涉及直播和时移切换的可以不关闭播放器*/       
        if(me->player_id)
        {
            /*关闭player*/
            if(iplayer_close(me->player_id) < 0)
            {
                INFO("%s[ivod_rtsp_ipanel_close] close player failed\n", IPANEL_MARK);
                me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_PLAYER_CLOSE_FAILED, 0);                        
            }
            else
            {
                INFO("%s[ivod_rtsp_ipanel_close] close player success\n", IPANEL_MARK);
                me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_PLAYER_CLOSE_SUCCESS, 0);
            }

            me->player_id = 0;
        }
     
        memset(me,0,sizeof(ivod_rtsp_ipanel_t));
        free(me);
        me = NULL;
    }
    
    INFO("%s[ivod_rtsp_ipanel_close] close success\n", IPANEL_MARK);

    return IPANEL_OK;
}

int ivod_rtsp_ipanel_proc(unsigned int handle, unsigned int event, int p1, int p2)
{
    int ret = IPANEL_ERR;
    ivod_rtsp_session_t *session = NULL;
    ivod_rtsp_ipanel_t *me = (ivod_rtsp_ipanel_t *)handle;
    
    FAILED_RETURNX(NULL == me || NULL == me->rtsp_session, IPANEL_ERR);

    session = me->rtsp_session;
    while(session)
    {
        /*处理心跳*/
        if(me->heart_start_time > 0 && ((int)time_ms())-me->heart_start_time > me->heart_time)
        {
            ivod_rtsp_session_proc(session, me->heart_event, 0, 0);
            me->heart_start_time = (int)time_ms();
        }
        
        ret = ivod_rtsp_session_proc(session, event, p1, p2);
        if(ret == IPANEL_OK)/*执行成功,退出*/
        {
            /*如果是打开播放，就保存播放地址及播放类型*/
            if(IVOD_RTSP_EVENT_OPEN == event)
            {
                if(p1)
                {
                    memset(me->url,0,sizeof(me->url));
                    strcpy(me->url,(char*)p1);
                }

                me->type = p2;
                INFO("%s open type=%d,url=\n%s\n", IPANEL_MARK,p2,me->url);
            }
            break;
        }
        else
            session = ivod_rtsp_session_get_next(session);/*有的VOD有两个session,比如NGOD*/
    }
    
    return ret;
}

int ivod_rtsp_ipanel_set_prop(unsigned int handle, int prop, int value)
{
    return IPANEL_OK;
}

int ivod_rtsp_ipanel_get_prop(unsigned int handle, int prop, void *value)
{
    return IPANEL_OK;
}


