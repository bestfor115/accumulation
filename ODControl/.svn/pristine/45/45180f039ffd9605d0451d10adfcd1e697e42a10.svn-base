/*********************************************************************
Copyright (c) 2013 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:isma vod客户端实现
*********************************************************************/
#include <netinet/in.h>
#include "ivod_rtsp_isma.h"
#include "ivod_rtsp_session.h"
#include "ivod_client_time.h"
#include "iplayer_socket.h"
#include "iplayer_player.h"
#include "iplayer_vod_main.h"
#include "ivod_rtsp_session.h"
#include "ivod_rtsp_protocol.h"
#include "ivod_rtsp_client.h"
#include "mplayer.h"


#define ISMA_USER_AGENT "User-Agent: iPanel VOD Player (Android Version.1)\r\n"
#define ISMA_MARK "[IVOD][ISMA]"


typedef struct ivod_rtsp_isma_s
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

    IPAddr0 serverIP[1];                    //点播地址服务器ip地址
    IPAddr0 data_serverIP[1];               //推流服务器ip地址
    int     data_serverPort;                //推流服务器推流socket端口

    char localIP[64];                       //本机ip地址(字符串)
    int rtsp_clientPort;                    //本地rtsp交互命令socket对应的端口
    int data_clientPort;                    //本地数据接收端口

    char param_key[16][16];                 //通过播放地址下发的参数名称
    char param_value[16][32];               //通过播放地址下发的参数值
    int  param_num;                         //通过播放地址下发的参数数量


    int             video_trackID;          //视频trackID
    int             audio_trackID;          //音频trackID
    int             current_setup_track;    //当前发送的setup是音频，或者视频
    iplayer_info_t  play_info[1];           //播放音视频参数

    unsigned int flag_first_play:1;         //第一次play标记
    unsigned int flag_livetv_to_tstv:1;     //直播转时移标记
    unsigned int flag_tstv_to_livetv:1;     //时移转直播标记
    unsigned int flag_multicast_stream:1;   //前端下发的是否是组播流
    unsigned int flag_send_dvb_info:1;      //QAM TS方式，是否发了dvb播放信息

    /*************************************************************************************
     *shanxi start 陕西项目特殊标志
     *************************************************************************************/
    unsigned int flag_shanxi_program:1;     //陕西项目标志，其他项目可以去掉

    /*************************************************************************************
     *shanxi  end
     *************************************************************************************/
    
}ivod_rtsp_isma_t;


/*************************************************************************************
 *函数声明
 *************************************************************************************/

/*解析URL中参数*/
static int 
ivod_rtsp_isma_parse_param(unsigned int handle, char *url);


/*************************************************************************************
 *函数定义
 *************************************************************************************/

static int ivod_rtsp_isma_response_describe(ivod_rtsp_isma_t *me, ivod_rtsp_session_t *session)
{
    char *ptr = NULL;
    char **sdp_param = NULL;
    client_time_t time_info[1] = {0};

    
    FAILED_RETURNX(NULL == me, IPANEL_ERR); 

    /*获取server*/
    ptr = ivod_rtsp_session_get_field(session, "Server", ':');
    if(ptr) 
    {
        if( 0/*strstr(ptr,"HMS_V")*/ ) 
        {
        	me->server_name = IVOD_RTSP_SERVER_HUAWEI;
        } 
        else 
        {
        	me->server_name = IVOD_RTSP_SERVER_ISMA;
        }
    }

    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_GET, IVOD_RTSP_PROP_TIME_INFO, (int)time_info);


    //获取sdp并解析
    if(me->type == IVOD_TYPE_TSTV && me->flag_shanxi_program)
    {
        /*******************************************************************************************
         *SDP规范以版本号(v=)开始，其他信息都排列在它的后面，但陕西的服务器就是不规范，只能特殊处理
         ******************************************************************************************/
        sdp_param = ivod_rtsp_session_get_field_address(session, "a=");
    }
    else
        sdp_param = ivod_rtsp_session_get_field_address(session, "v=");
    
    if(sdp_param)
    {
        int need_add_field = 0,current_media_type = 0;
        
        while(*sdp_param)
        {
            ptr = *sdp_param;
            INFO("%s[ivod_rtsp_isma_response_describe]%s\n", ISMA_MARK,ptr);
            
            if(strstr(ptr, "a=range:"))
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
                        //client_time_update_current_time(time_info, time_info->start_time);

                        //这里更新起始时间的更新时间，后续时移，回看播放到头时，用起始时间和更新时间计算时移和回看的当前起始时间
                        time_info->last_update_start_time = (unsigned int)time_ms();
                        time_info->time_type = time_type;
                    }

                    if(strlen(to))
                    {
                        client_time_string_to_seconds(to, &time_info->end_time, &time_type);

                        if(IVOD_TYPE_LiveTV == me->type || IVOD_TYPE_TSTV == me->type)
                        {
                            //这里只能简单赋值，不能调用client_time_update_current_time更新
                            time_info->present_time = time_info->end_time;
                        }
                        
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
            else if(strstr(ptr, "x-Frequency:"))
            {
				ptr =strstr(ptr, "x-Frequency:") + strlen("x-Frequency:");
				me->play_info->dvb_info.frequency = (unsigned int)(atoi(ptr));                
            }
            else if(strstr(ptr, "x-ProgramNumber:"))
            {
				ptr =strstr(ptr, "x-ProgramNumber:") + strlen("x-ProgramNumber:");
				me->play_info->dvb_info.pmt_pid = (unsigned int)(atoi(ptr));                
            }
            else if(strstr(ptr, "x-pid:"))
            {
				ptr =strstr(ptr, "x-pid:") + strlen("x-pid:");
				me->play_info->dvb_info.pmt_pid = (unsigned int)(atoi(ptr));                
            }
            else if(strstr(ptr, "m=video"))
            {
                need_add_field  = 1;
                current_media_type = IPLAYER_VIDEO_TRACK;

                if(strstr(ptr, "MP2T"))
                {
                    //TS流
                }
            }            
            else if(strstr(ptr, "trackID="))
            {
				ptr =strstr(ptr, "trackID=") + strlen("trackID=");

                if(IPLAYER_AUDIO_TRACK == current_media_type)
    				me->audio_trackID = atoi(ptr);
                else
    				me->video_trackID = atoi(ptr);
            }
            else if(strstr(ptr, "track="))
            {
				ptr =strstr(ptr, "track=") + strlen("track=");

                if(IPLAYER_AUDIO_TRACK == current_media_type)
    				me->audio_trackID = atoi(ptr);
                else
    				me->video_trackID = atoi(ptr);
            }
            else if(strstr(ptr, "streamid="))
            {
				ptr =strstr(ptr, "streamid=") + strlen("streamid=");

                if(IPLAYER_AUDIO_TRACK == current_media_type)
    				me->audio_trackID = atoi(ptr);
                else
    				me->video_trackID = atoi(ptr);
            } 
            else if(strstr(ptr, "a=rtpmap:"))
            {
				ptr =strstr(ptr, "a=rtpmap:") + strlen("a=rtpmap:");
                me->play_info->es_info.payload_type[current_media_type] = atoi(ptr);

                if(strstr(ptr, "MP4V-ES/"))
                {
    				ptr =strstr(ptr, "MP4V-ES/") + strlen("MP4V-ES/");
                    me->play_info->es_info.sample_rate[current_media_type] = atoi(ptr);
                }
                else if(strstr(ptr, "H264/"))
                {
    				ptr =strstr(ptr, "H264/") + strlen("H264/");
                    me->play_info->es_info.sample_rate[current_media_type] = atoi(ptr);                    
                }
                else if(strstr(ptr, "mpeg4-generic/"))
                {
    				ptr =strstr(ptr, "mpeg4-generic/") + strlen("mpeg4-generic/");
                    me->play_info->es_info.sample_rate[current_media_type] = atoi(ptr);                    
                }
                else if(strstr(ptr, "MPA/"))
                {
    				ptr =strstr(ptr, "MPA/") + strlen("MPA/");
                    me->play_info->es_info.sample_rate[current_media_type] = atoi(ptr);                    
                }
                else if(strstr(ptr, "MP4A-LATM/"))
                {
    				ptr =strstr(ptr, "MP4A-LATM/") + strlen("MP4A-LATM/");
                    me->play_info->es_info.sample_rate[current_media_type] = atoi(ptr);                    
                }                                    
            }
            else if(strstr(ptr, "m=audio"))
            {
                need_add_field  = 1;
                current_media_type = IPLAYER_AUDIO_TRACK;
            }
            
            if(need_add_field)
            {
                strcat(me->play_info->es_info.param[current_media_type],*sdp_param);
                strcat(me->play_info->es_info.param[current_media_type],"\r\n");
            }
            
            sdp_param++;
        }
    }

    //处理url，将url中问号之后的参数去掉
    if(strstr(me->url,"?"))
    {
        char *p = strstr(me->url,"?");
        *p = '\0';
        ivod_rtsp_session_set_url(session, me->url);
    }
    
    //从视频开始发送setup命令
    if(me->video_trackID >= 0)
        me->current_setup_track = IPLAYER_VIDEO_TRACK;
    
    return ivod_rtsp_session_proc(session, IVOD_RTSP_EVENT_SETUP, 0, 0);
}

static int ivod_rtsp_isma_response_setup(ivod_rtsp_isma_t *me, ivod_rtsp_session_t *session)
{
    char *ptr = NULL, *p_str = NULL;
    unsigned int index = 0, i = 0;
    char  key_word[64][32] = {0};
    char  key_value[64][32] = {0};
    int player_id = 0;
    IPAddr0 ipaddr[1] = {0};
    int stream_socket_type = ISOCKET_TYPE_TCP;
    client_time_t time_info[1] = {0};
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR); 

    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_GET, IVOD_RTSP_PROP_TIME_INFO, (int)time_info);
    
    /*获取session id*/
    ptr = ivod_rtsp_session_get_field(session, "Session", ':');
    if(NULL == ptr)
    {
        INFO("%s[ivod_rtsp_isma_response_setup] get Session failed\n", ISMA_MARK);
        return IPANEL_ERR;
    }

    ivod_rtsp_session_set_session(session, ptr);

    /*获取server*/
    if(IVOD_RTSP_SERVER_NONE == me->server_name)
    {
        ptr = ivod_rtsp_session_get_field(session, "Server", ':');
        if(NULL == ptr)
        {
            INFO("%s[ivod_rtsp_isma_response_setup] get Server failed\n", ISMA_MARK);
            me->server_name = IVOD_RTSP_SERVER_NONE;
        }
        else
        {
            INFO("%s[ivod_rtsp_isma_response_setup] get Server = %s\n", ISMA_MARK,ptr);

            if( 0/*strstr(ptr,"HMS_V")*/ ) 
            {
            	me->server_name = IVOD_RTSP_SERVER_HUAWEI;
            } 
            else 
            {
            	me->server_name = IVOD_RTSP_SERVER_ISMA;
            }            
        }
    }
    
    /*获取Transport参数解析*/
    ptr = ivod_rtsp_session_get_field(session, "Transport", ':');
    if(NULL == ptr)
    {
        INFO("%s[ivod_rtsp_isma_response_setup] get Transport failed\n", ISMA_MARK);
        return IPANEL_ERR;
    }

    if(strstr(ptr,"MP2T/RTP/UDP") || strstr(ptr,"MP2T/UDP"))
    {
        stream_socket_type = ISOCKET_TYPE_UDP;
        me->play_info->mode = IPLAYER_MODE_IP_TS;
    }
    else if(strstr(ptr,"MP2T/RTP/TCP") || strstr(ptr,"MP2T/TCP"))
    {
        stream_socket_type = ISOCKET_TYPE_TCP;
        me->play_info->mode = IPLAYER_MODE_IP_TS;
    }
    else if(strstr(ptr,"MP2T"))
    {
        me->play_info->mode = IPLAYER_MODE_QAM_TS;
    }

    strcpy(key_word[index++],"source=");
    strcpy(key_word[index++],"server_port=");
    strcpy(key_word[index++],"client_port=");
    strcpy(key_word[index++],"destination=");

    for( i = 0; i < index; i++) 
    {
        char *end = NULL;
        p_str = strstr(ptr, key_word[i]);
        if(p_str) 
        {
            p_str = p_str + strlen(key_word[i]);
            end = strchr(p_str,';');
            if(end) 
                memcpy(key_value[i], p_str, end - p_str);
            else
                strcpy(key_value[i],p_str);
            INFO("%s[ivod_rtsp_isma_response_setup] key_word = %s, key_value = %s\n", ISMA_MARK,key_word[i], key_value[i]);
        }
    }    

    
    for( i = 0; i < sizeof(key_word)/sizeof(key_word[0]); i++) 
    {
        if(strlen(key_word[i]) == 0 || strlen(key_value[i]) == 0)
            continue;

        /*服务器ip地址*/
        if(strcmp(key_word[i], "source=") == 0)
        {
            if(IPANEL_IP_VERSION_6 == me->serverIP->version)
                ivod_common_ipv6_str_to_int(key_value[i], me->data_serverIP);
            else
                ivod_common_ipv4_str_to_int(key_value[i], me->data_serverIP);
            
        }
        else if(strcmp(key_word[i], "server_port=") == 0)
        {
            me->data_serverPort = atoi(key_value[i]);
        }
        else if(strcmp(key_word[i], "client_port=") == 0)
        {
            me->data_clientPort = atoi(key_value[i]);
        }
        else if(strcmp(key_word[i], "destination=") == 0)
        {
            ;
        }
    }

    /*设置心跳*/
    if(me->type == IVOD_TYPE_TSTV && me->play_info->mode == IPLAYER_MODE_QAM_TS)
        me->heart_event = IVOD_RTSP_EVENT_GET_PARAM;
    else
        me->heart_event = IVOD_RTSP_EVENT_OPTIONS;
    me->heart_time = 10000;
    me->heart_start_time = (int)time_ms();

    
    if(me->play_info->mode == IPLAYER_MODE_NONE)
    {
        if(me->mode == IVOD_MODE_DVB)
            me->play_info->mode = IPLAYER_MODE_QAM_TS;
        else
            me->play_info->mode = IPLAYER_MODE_IP_TS;
    }

    if(me->play_info->mode == IPLAYER_MODE_IP_TS)
    {
        time_info->flag_support_pts = 1;
        /*通知更新时间*/
        me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_UPDATE_TIME_INFO, (int)time_info);
    }

    if(stream_socket_type == ISOCKET_TYPE_UDP && me->play_info->mode == IPLAYER_MODE_IP_TS)
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
            INFO("%s[ivod_rtsp_isma_response_setup]socket open failed\n", ISMA_MARK);
            goto OPEN_FAILED;
        }
        /*bind 端口*/
        memset(ipaddr,0,sizeof(IPAddr0));
        ipaddr->version = socket_version;
        if(isocket_bind(me->sock_id, (unsigned int)ipaddr, me->data_clientPort) < 0)
        {
            INFO("%s[ivod_rtsp_isma_response_setup]socket bind failed\n", ISMA_MARK);
            goto OPEN_FAILED;
        }        
    }
   
    /*获取player_id*/
    if(me->play_info->mode == IPLAYER_MODE_IP_TS)
    {
        me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_GET, IVOD_RTSP_PROP_PLAYER_ID, (int)&player_id);
        if(player_id <= 0)
        {
            player_id = iplayer_open(me->client_main, me->play_info);
            if(player_id <= 0)
            {
                INFO("%s[ivod_rtsp_isma_response_setup] player open failed\n", ISMA_MARK);
                /*打开PLAYER失败*/
                me->func(me->rtsp_client,IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_PLAYER_OPEN_FAILED, 0);
                goto OPEN_FAILED;
            }
            /*打开player成功*/
            me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_PLAYER_OPEN_SUCCESS, player_id);
        }
        me->player_id = player_id; 
    }

    /*如果是组播这里需要加入组播组*/
    if(IVOD_TYPE_LiveTV == me->type && IPANEL_IP_VERSION_4 == me->data_serverIP->version)
    {
        /*224.0.1.0—238.255.255.255可以用与Internet上的，组播地址范围*/
        if(me->data_serverIP->addr.ipv4 >= 0xE0000100 && me->data_serverIP->addr.ipv4 <= 0xEFFFFFFF)
        {
            if(isocket_join_multicast(me->sock_id, (unsigned int)me->data_serverIP) < 0)
            {
                INFO("%s[ivod_rtsp_isma_response_setup] join multicast failed\n", ISMA_MARK);
                goto OPEN_FAILED;
            }

            me->flag_multicast_stream = 1;
        }
    }
    
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

static int ivod_rtsp_isma_response_play(ivod_rtsp_isma_t *me, ivod_rtsp_session_t *session)
{
    char *ptr = NULL;
    int speed = 0,seq = -1;
    active_info_t info[1] = {0};
    client_time_t time_info[1] = {0};
    mplayer_media_t dvb_media[1] = {0};
    
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
            unsigned int current_time = 0;
            
            client_time_parse_string_time(ptr, from, to);

            if(strlen(from))
            {
            	if(!strcmp(from,"end"))
            	{
            		//如果前端返回的时间为end，则用结束时间更新当前时间
					client_time_update_current_time(time_info, time_info->end_time);
            	}
            	else
            	{
					client_time_string_to_seconds(from, &current_time, &time_type);
					client_time_update_current_time(time_info, current_time);
					time_info->time_type = time_type;
            	}
            }

            if(strlen(to) && time_info->end_time == 0)
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
        char *str_end = NULL,*p_field = NULL;
        char new_ip[64] = {0},new_port[10] = {0};
        IPAddr0 temp_ip[1] = {0};
        int temp_port = 0;
        
		str_end = strstr(ptr,"seq=");
		if( str_end ) 
        {
            str_end = str_end + strlen("seq=");
			seq = atoi(str_end);
            INFO("%s[ivod_rtsp_isma_response_play] RTP-Info seq = %d\n", ISMA_MARK,seq);
		}                    
    }

    if(me->play_info->mode == IPLAYER_MODE_IP_TS)
    {
        /*设置基准ts包seq*/
        iplayer_set_prop(me->player_id, IPLAYER_PARAM_RTP_SEQ, seq);
        /*通知播放器播放成功*/
        iplayer_play(me->player_id, me->current_op, info);
    }
    else if(me->play_info->mode == IPLAYER_MODE_QAM_TS)
    {
        dvb_media->frequency = me->play_info->dvb_info.frequency*1000;
        dvb_media->pmt_pid = me->play_info->dvb_info.pmt_pid;
        dvb_media->symbolrate = 68750;
        dvb_media->qam = 3;
    }
    
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

    if(!me->flag_send_dvb_info)
    {
        me->flag_send_dvb_info = 1;
        me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_PLAYER_DVB_INFO, (int)dvb_media);
    }
    
    return IPANEL_OK;
}

static int ivod_rtsp_isma_describe(void *handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    ivod_rtsp_isma_t *me = (ivod_rtsp_isma_t *)handle;
    int seq = 0;
    char buffer[512] = {0};
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    /*添加seq*/
    seq = ivod_rtsp_session_add_cseq(session);

    /*添加accept*/
	strcpy(buffer,"Accept:application/sdp\r\n");
	ivod_rtsp_session_add_field(session,buffer);

    /*添加User Agent*/
    strcpy(buffer, ISMA_USER_AGENT);
	ivod_rtsp_session_add_field(session, buffer);

    /*添加区域码*/
    if(me->mode == IVOD_MODE_DVB)
    {
        
        memset(buffer,0,sizeof(buffer));
    	sprintf(buffer,"x-RegionID: %s\r\n","0x1130100");
    	ivod_rtsp_session_add_field(session,buffer);  
    	      
    } 

    //陕西项目特殊字段，其他项目可以去掉，保证代码可读性
	if(me->flag_shanxi_program) 
    {
        int i = 0;
        
		for( i = 0 ; i < me->param_num; i++ ) 
        {
			memset(buffer,0,sizeof(buffer));
            
			if( strcasecmp(me->param_key[i],"STBCapability") == 0 ) {
				sprintf(buffer,"x-STBCapability:%s\r\n",me->param_value[i]);
			} else if( strcasecmp(me->param_key[i],"STBmac") == 0 ) {
				sprintf(buffer,"x-STBMac:%s\r\n",me->param_value[i]);
			} else if( strcasecmp(me->param_key[i],"Category") == 0 ) {
				sprintf(buffer,"x-Category:%s\r\n",me->param_value[i]);
			} else if( strcasecmp(me->param_key[i],"Provider") == 0 ) {
				sprintf(buffer,"x-Provider:%s\r\n",me->param_value[i]);
			} else if( strcasecmp(me->param_key[i],"PurchaseType") == 0 ) {
				sprintf(buffer,"x-PurchaseType:%s\r\n",me->param_value[i]);
			} else if( strcasecmp(me->param_key[i],"ServiceCode") == 0 ) {
				sprintf(buffer,"x-ServiceCode:%s\r\n",me->param_value[i]);
			} else if( strcasecmp(me->param_key[i],"ServiceName") == 0 ) {
				sprintf(buffer,"x-ServiceName:%s\r\n",me->param_value[i]);
			} 
			
			if(strlen(buffer) > 0)
            	ivod_rtsp_session_add_field(session, buffer);
		}
	}    
    
    /*添加\r\n\r\n*/
	ivod_rtsp_session_add_field(session, END_STRING);

    /*通知QOS开始连接服务器*/
	implayer_qos_proc("proc", VOD_QOS_EVENT_CONNECT_URL, 0, 0);    

    return seq;
}

static int ivod_rtsp_isma_setup(void *handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    ivod_rtsp_isma_t *me = (ivod_rtsp_isma_t *)handle;
    int seq = 0;
    char buffer[512] = {0};
    int port = 0;
	IPANEL_NETWORK_IF_PARAM localIP[1] = { 0 };
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    
    /*添加seq*/
    seq = ivod_rtsp_session_add_cseq(session);
    /*添加Transport*/
    
    port = ivod_common_get_dynamic_port();
    
    if(me->mode == IVOD_MODE_IP)
    	sprintf(buffer, "Transport: MP2T/RTP/UDP;unicast;destination=%s;client_port=%d-%d,MP2T/UDP;unicast;destination=%s;client_port=%d-%d,MP2T/TCP;unicast;destination=%s;interleaved=0-1,MP2T/RTP/TCP;unicast;destination=%s;interleaved=0-1\r\n",
    		me->localIP, port, port + 1, me->localIP, port, port + 1, me->localIP, me->localIP);
    else
        sprintf(buffer, "Transport: MP2T\r\n");
    ivod_rtsp_session_add_field(session, buffer);
    
    /*添加User Agent*/
    memset(buffer, 0, sizeof(buffer));
    strcpy(buffer, ISMA_USER_AGENT);
	ivod_rtsp_session_add_field(session, buffer);

    /*添加区域码*/
    if(me->mode == IVOD_MODE_DVB)
    {        
        memset(buffer,0,sizeof(buffer));
    	sprintf(buffer,"x-RegionID: %s\r\n","0x1130100");
    	ivod_rtsp_session_add_field(session,buffer);      	      
    }    
   
    /*添加\r\n\r\n*/
	ivod_rtsp_session_add_field(session, END_STRING);

    return seq;  
}        

static int ivod_rtsp_isma_play(void *handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    ivod_rtsp_isma_t *me = (ivod_rtsp_isma_t *)handle;
    char seek_from[64] = {0}, seek_to[64] = {0};
    char *ptr = NULL;
    int ret = IPANEL_OK, speed = 0, seq = 0;
    char range[128] = {0}, buffer[128] = {0};
    client_time_t time_info[1] = {0};
    
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_GET, IVOD_RTSP_PROP_TIME_INFO, (int)time_info);

    /*计算speed以及seek_time*/
    switch(msg)
    {
        case IVOD_RTSP_EVENT_PLAY:
            speed = 1;

            if(me->type == IVOD_TYPE_TSTV)
            {
                UTC_time_t utc_time[1] = {0};
                char str[20] = {0};
                unsigned int current_t = 0;
                
                if(me->flag_first_play)
                {
                    current_t = time_info->end_time;
                }
                else
                {
                    client_time_calculate_current_time(time_info, me->speed, &current_t);
                }
                client_time_seconds_to_UTC(current_t, utc_time);
                client_time_UTC_time_print(utc_time, str);                        
                sprintf(range, "Range: clock=%s-\r\n", str);                
            }
            else if(me->type == IVOD_TYPE_LiveTV)
            {
                //待定
            }
            else if(me->type == IVOD_TYPE_VOD)
            {
                if(me->flag_first_play)
                    strcpy(range, "Range: npt=beginning-\r\n");
                else
                    strcpy(range, "Range: npt=now-\r\n");               
            }            
            me->current_op = IPLAYER_OP_PLAY;
            break;
        case IVOD_RTSP_EVENT_RESUME:
            speed = 1;

            if(me->type == IVOD_TYPE_TSTV)
            {
                UTC_time_t utc_time[1] = {0};
                char str[20] = {0};
                unsigned int current_t = 0;
                unsigned int temp_t = 0;

                client_time_calculate_current_time(time_info, me->speed, &current_t);
                
                //时移比较特殊，暂停时间太长，时移的时间就变了，所以需要重新计算时间
                temp_t = time_info->start_time + (time_ms() - time_info->last_update_start_time)/1000;
                if(temp_t > 0 && current_t <= temp_t + 1)
                    current_t = temp_t + 1;
                client_time_seconds_to_UTC(current_t, utc_time);
                client_time_UTC_time_print(utc_time, str);                        
                sprintf(range, "Range: clock=%s-\r\n", str);                
            }
            else if(me->type == IVOD_TYPE_LiveTV)
            {
                //思华规范，暂停恢复播放，不需要range字段，VOD就不需要Range字段了
            }
            else if(me->type == IVOD_TYPE_VOD)
            {
                //思华规范，暂停恢复播放，不需要range字段，VOD就不需要Range字段了
            }

            me->current_op = IPLAYER_OP_RESUME;            
            break;
        case IVOD_RTSP_EVENT_SEEK:
            ptr = (char *)p1;            
            speed = 1;
            
            /*解析seek_time*/
            client_time_parse_string_time(ptr, seek_from, seek_to);                

            /*时移seek 100%转直播*/
            if(strchr(seek_from,'%') && atoi(seek_from) == 100 && me->type == IVOD_TYPE_TSTV)
            {
                char live_url[2048] = {0};

                /*获取直播url*/
                me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_GET, IVOD_RTSP_PROP_LIVE_URL, (int)live_url);                
                if(strncmp(live_url, "igmp://", strlen("igmp://")) == 0)
                {
                    /*igmp的直播由client处理*/
                    me->flag_tstv_to_livetv = 1;
                    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_TSTV_TO_LIVE, 0);

                    return IPANEL_ERR;
                }
                else
                {
                    /*rtsp的直播库里直接转到直播*/
                    me->type = IVOD_TYPE_LiveTV;
                    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_UPDATE_VOD_TYPE, me->type); 
                    strcpy(range, "Range: npt=end-\r\n");
                }
            }          
            else if(strchr(seek_from,'%') && atoi(seek_from) == 0 && me->type == IVOD_TYPE_TSTV)
            {
                strcpy(range, "Range: npt=beginning-\r\n");
            }
            else if(strchr(seek_from,'%') && atoi(seek_from) == 0 && me->type == IVOD_TYPE_LiveTV)
            {
                /*rtsp的直播库里直接转到时移*/
                me->type = IVOD_TYPE_TSTV;
                me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_UPDATE_VOD_TYPE, me->type);                
                strcpy(range, "Range: npt=beginning-\r\n");
            }            
            else if(strchr(seek_from,'%') && me->type == IVOD_TYPE_VOD)
            {
                int time = atoi(seek_from);

                if(!me->current_op && time==0)
                {
                    //规避vod open之后seek(0%)播放
                    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_SEEK, 1);
                    return IPANEL_ERR;
                }
                else
                {
                    if(time>=0 && time<=100)
                    {                    
                        time = (int)(time_info->duration * time / 100);
                        sprintf(range, "Range: npt=%d-\r\n", time);                    
                    }
                    else
                    {
                        strcpy(range, "Range: npt=0-\r\n");
                    }
                }
            }
            else if(atoi(seek_from) == 0 && me->type == IVOD_TYPE_LiveTV)
            {
                strcpy(range, "Range: npt=end-\r\n");
            }              
            else
            {
                unsigned int seek_time = 0;
                int          seek_time_type = CLIENT_TIME_TYPE_NONE;

                client_time_string_to_seconds(seek_from,&seek_time,&seek_time_type); 

                if(CLIENT_TIME_TYPE_NPT == seek_time_type)
                {
                    sprintf(range, "Range: npt=%d-\r\n", seek_time);
                }
                else if(CLIENT_TIME_TYPE_UTC == seek_time_type)
                {
                    UTC_time_t utc_time[1] = {0};
                    char str[20] = {0};

                    //如果seek的时间在开始时间加5s以前，都从起始点开始播放，因为心跳10s更新一次起始播放时间
                    if(seek_time < time_info->start_time + 5)
                    {
                        sprintf(range, "Range: npt=beginning-\r\n");
                    }
                    else
                    {
                        client_time_seconds_to_UTC(seek_time, utc_time);
                        client_time_UTC_time_print(utc_time, str);
                        
                        sprintf(range, "Range: clock=%s-\r\n", str);
                    }
                }
            }
            
            me->current_op = IPLAYER_OP_SEEK;
            
            break;
        case IVOD_RTSP_EVENT_FORWARD:
        case IVOD_RTSP_EVENT_BACKWARD: 
            if(me->type == IVOD_TYPE_TSTV)
            {
                UTC_time_t utc_time[1] = {0};
                char str[20] = {0};
                unsigned int current_t = 0;
                unsigned int temp_t = 0;

                client_time_calculate_current_time(time_info, me->speed, &current_t);
                
                //时移比较特殊，暂停时间太长，时移的时间就变了，所以需要重新计算时间
                temp_t = time_info->start_time + (time_ms() - time_info->last_update_start_time)/1000;
                if(temp_t > 0 && current_t <= temp_t + 1)
                    current_t = temp_t + 1;  

                client_time_seconds_to_UTC(current_t, utc_time);
                client_time_UTC_time_print(utc_time, str);                        
                sprintf(range, "Range: clock=%s-\r\n", str);                
            }
            else if(me->type == IVOD_TYPE_LiveTV)
            {
                if(IVOD_RTSP_EVENT_BACKWARD == msg)
                {
                    char tstv_url[2048] = {0};

                    /*获取时移url*/
                    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_GET, IVOD_RTSP_PROP_TSTV_URL, (int)tstv_url);
                    if(0 == strlen(tstv_url))
                    {
                        /*不支持时移*/
                        me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_BACKWARD, IPANEL_ERR);
                        return IPANEL_ERR;
                    }

                    me->type = IVOD_TYPE_TSTV;
                    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_UPDATE_VOD_TYPE, me->type);                
                    
                }
                strcpy(range, "Range: npt=now-\r\n");
            }
            else if(me->type == IVOD_TYPE_VOD)
            {
                strcpy(range, "Range: npt=now-\r\n");
            }
            
            speed = p1;
            me->current_op = IPLAYER_OP_FWBW;
            break;
        default:
            INFO("%s[ivod_rtsp_isma_play] failed, event invalid\n",ISMA_MARK);
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
    if(strlen(range) > 0)
        ivod_rtsp_session_add_field(session, range);
    /*添加User Agent*/
	ivod_rtsp_session_add_field(session, ISMA_USER_AGENT);
    
    /*添加区域码*/
    if(me->mode == IVOD_MODE_DVB)
    {        
        memset(buffer,0,sizeof(buffer));
    	sprintf(buffer,"x-RegionID: %s\r\n","0x1130100");
    	ivod_rtsp_session_add_field(session,buffer);      	      
    }    
    
    /*添加\r\n\r\n*/
	ivod_rtsp_session_add_field(session, END_STRING);

    /*通知qos play操作*/
	implayer_qos_proc("proc", VOD_QOS_EVENT_PLAY, speed, (IPLAYER_OP_SEEK == me->current_op)? 1 : 0);

    me->flag_first_play = 0;

    return seq;
}         

static int ivod_rtsp_isma_pause(void *handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    ivod_rtsp_isma_t *me = (ivod_rtsp_isma_t *)handle;
    int seq = 0;
    char buffer[256] = {0};
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    /*通知播放器播放暂停*/
    iplayer_pause(me->player_id);

    /*直播转时移*/
    if(IVOD_TYPE_LiveTV == me->type)
    {
        char tstv_url[2048] = {0};

        /*获取时移url*/
        me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_GET, IVOD_RTSP_PROP_TSTV_URL, (int)tstv_url);
        if(0 == strlen(tstv_url))
        {
            /*不支持时移*/
            me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_PAUSE, IPANEL_ERR);
            return IPANEL_ERR;
        }

        me->type = IVOD_TYPE_TSTV;
        me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_UPDATE_VOD_TYPE, me->type);                
        
    }
    
    
    /*添加seq*/
    seq = ivod_rtsp_session_add_cseq(session);
    /*添加session id*/
    ivod_rtsp_session_add_session(session);    
    /*添加User Agent*/
    strcpy(buffer, ISMA_USER_AGENT);
	ivod_rtsp_session_add_field(session, buffer);
    /*添加\r\n\r\n*/
	ivod_rtsp_session_add_field(session, END_STRING);

    me->current_op = IPLAYER_OP_PAUSE;

    /*通知qos pause操作*/
	implayer_qos_proc("proc",VOD_QOS_EVENT_PAUSE,0,0);

    return seq;
    
}        

static int ivod_rtsp_isma_teardown(void *handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    ivod_rtsp_isma_t *me = (ivod_rtsp_isma_t *)handle;
    int seq = 0;
    char buffer[256] = {0};
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    
    /*添加seq*/
    seq = ivod_rtsp_session_add_cseq(session);
    /*添加session id*/
    ivod_rtsp_session_add_session(session);    
    /*添加User Agent*/
    strcpy(buffer, ISMA_USER_AGENT);
	ivod_rtsp_session_add_field(session, buffer);
    /*添加\r\n\r\n*/
	ivod_rtsp_session_add_field(session, END_STRING);

    me->current_op = IPLAYER_OP_CLOSE;

    return seq;
    
}       

static int ivod_rtsp_isma_get_parameter(void *handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    ivod_rtsp_isma_t *me = (ivod_rtsp_isma_t *)handle;
    int seq = 0;
    char buffer[256] = {0};
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    
    /*添加seq*/
    seq = ivod_rtsp_session_add_cseq(session);
    /*添加session id*/
    ivod_rtsp_session_add_session(session);        
    /*添加User Agent*/
    strcpy(buffer, ISMA_USER_AGENT);
	ivod_rtsp_session_add_field(session, buffer);
    
	if(me->type == IVOD_TYPE_TSTV)
	{
		memset(buffer, 0, sizeof(buffer));
        if(me->play_info->mode == IPLAYER_MODE_QAM_TS)
    		sprintf(buffer, "x-Timeshift_Range:0\r\n");
        else
    		sprintf(buffer, "x-Timeshift_Range\r\nx-Timeshift_Current\r\n");
    	ivod_rtsp_session_add_field(session, buffer);
	}   
    
    /*添加\r\n\r\n*/
	ivod_rtsp_session_add_field(session, END_STRING);

    return seq;
}

static int ivod_rtsp_isma_set_parameter(void *handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    return IPANEL_OK;
}

static int ivod_rtsp_isma_option(void *handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    ivod_rtsp_isma_t *me = (ivod_rtsp_isma_t *)handle;
    int seq = 0;
    char buffer[256] = {0};
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    
    /*添加seq*/
    seq = ivod_rtsp_session_add_cseq(session);
    /*添加session id*/
    ivod_rtsp_session_add_session(session);   
    /*添加User Agent*/
    strcpy(buffer, ISMA_USER_AGENT);
	ivod_rtsp_session_add_field(session, buffer);

    /*添加区域码*/
    if(me->mode == IVOD_MODE_DVB)
    {        
        memset(buffer,0,sizeof(buffer));
    	sprintf(buffer,"x-RegionID: %s\r\n","0x1130100");
    	ivod_rtsp_session_add_field(session,buffer);      	      
    } 
    
    /*添加\r\n\r\n*/
	ivod_rtsp_session_add_field(session, END_STRING);

    return seq;
}     

static int ivod_rtsp_isma_validate(void *handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    return IPANEL_OK;
}     

static int ivod_rtsp_isma_ping(void *handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    return IPANEL_OK;
}         

static int ivod_rtsp_isma_response(void *handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    ivod_rtsp_isma_t *me = (ivod_rtsp_isma_t *)handle;
    int seq = 0;
    char buffer[256] = {0};
    char ip[64] = {0};
    unsigned port = 0;
    int ret = IPANEL_OK;
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR); 
    
    INFO("%s[ivod_rtsp_isma_response] msg=%d, p1=%d, p2=%d\n", ISMA_MARK,msg,p1,p2); 
    
    switch(msg)
    {
        case IVOD_RTSP_ACK_OPEN:
            {
                IPAddr0 ipaddr[1] = {0};
                unsigned int temp_port = 0;

                if(p1 >= 0)
                {
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

                    /*根据p1值，获取rtsp交互命令socket的本地端口*/
                    if(IPANEL_IP_VERSION_4 == ipaddr->version)
                        isocket_getsockname((unsigned int)p1, (unsigned int)ipaddr, &me->rtsp_clientPort);

                    /*获取本机ip地址*/
                    if(IPANEL_IP_VERSION_6 != me->serverIP->version)
                    {
                        ivod_common_get_localIP(NULL, me->localIP, NULL);
                    }
                    else
                    {
                        ivod_common_get_localIP(NULL, NULL, me->localIP);
                    }

                    //针对shanxi地区播放地址特殊处理，其他地区可以删除，保证代码可读性
                    {                        
                        if(strstr(me->url,"&ipanel_shangxi=1"))
                        {
                            char *p = strstr(me->url,"&ipanel_shangxi=1");

                            memset(me->param_key,0,sizeof(me->param_key));
                            memset(me->param_value,0,sizeof(me->param_value));
                            me->param_num = 0;
                            
                            me->flag_shanxi_program = 1;
                            ivod_rtsp_isma_parse_param((unsigned int)me,me->url);
                            *p = '\0';
                            ivod_rtsp_session_set_url(session, me->url);
                        }
                    }
                    

                    me->flag_first_play = 1;
    				ret = ivod_rtsp_session_proc(session, IVOD_RTSP_EVENT_DESCRIBE, p1, p2);
                }
                else
                {
                    /*连接服务器失败*/
                    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_OPEN, -996);
                }
            }
            break;
        case IVOD_RTSP_ACK_DESCRIBE:
            ret = ivod_rtsp_isma_response_describe(me, session);
            break;  
        case IVOD_RTSP_ACK_SETUP:
            ret = ivod_rtsp_isma_response_setup(me, session);
            break;
        case IVOD_RTSP_ACK_PLAY:
            ret = ivod_rtsp_isma_response_play(me, session);
            break;
        case IVOD_RTSP_ACK_PAUSE:
            {
                if(IPLAYER_OP_PAUSE == me->current_op)
                {
                    client_time_t time_info[1] = {0};
                    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_GET, IVOD_RTSP_PROP_TIME_INFO, (int)time_info);
                    time_info->last_pause_time = (unsigned int)time_ms();
                    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_UPDATE_TIME_INFO, (int)time_info);                
                   
                    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_PAUSE, 0);
                }
            }
            break;
        case IVOD_RTSP_ACK_OPTION:
            break;
        case IVOD_RTSP_ACK_PING:
            break;
        case IVOD_RTSP_ACK_SET_PARAM:
            {
                char *p_reason = NULL;
                int notice = 0;
                
                p_reason = ivod_rtsp_session_get_field(session, "x-Reason", ':');
    			if( !p_reason )
    			{
                    return IPANEL_ERR;
                }

                if(strstr(p_reason,"BOS") || strstr(p_reason,"bos"))
                {
                    //播放到头
        			notice = 2102;
                }
                else if(strstr(p_reason,"EOS") || strstr(p_reason,"eos"))
                {
                    //播放到尾
        			notice = 2101;
                }

    			if ( notice == 2102 ) 
                {
                    //发送5209给页面，页面会调用从头开始播放
    				me->flag_first_play = 1;
    			} 

                INFO("%s[ivod_rtsp_isma_response] IVOD_RTSP_ACK_SET_PARAM reason=%s\n", ISMA_MARK,p_reason); 

                //通知qos announce错误
                implayer_qos_proc("proc",VOD_QOS_EVENT_ERROR,notice,0);
               
                //通知上层announce消息
                me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_ANNOUNCE, notice);
            }            
            break;
        case IVOD_RTSP_ACK_GET_PARAM:
            {
                char *ptr = NULL;
                client_time_t time_info[1] = {0};
                int flag_update_time = 0;
                
                me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_GET, IVOD_RTSP_PROP_TIME_INFO, (int)time_info);
                ptr = ivod_rtsp_session_get_field(session, "x-Timeshift_Current", ':');

                if(ptr)
                {
                    if(strstr(ptr, "clock="))
                    {            
                        ptr = strstr(ptr, "clock=") + strlen("clock=");
                    } 

                    if(ptr)
                    {
                        int time_type = CLIENT_TIME_TYPE_NONE;
                        char from[64] = {0}, to[64] = {0};
                        unsigned int temp_time = 0;
                        
                        client_time_parse_string_time(ptr, from, to);

                        if(strlen(from))
                        {
                            client_time_string_to_seconds(from, &temp_time, &time_type);

                            if(time_info->last_update_time > 0)
                            {
                                client_time_update_current_time(time_info, temp_time);
                                time_info->time_type = time_type;
                            }
                            else
                            {
                                time_info->present_time = temp_time;
                            }

                            /*通知更新时间*/
                            flag_update_time = 1;
                        }
                    }
                }

                ptr = ivod_rtsp_session_get_field(session, "x-Timeshift_Range", ':');                
                if(ptr)
                {
                    if(strstr(ptr, "clock="))
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
                            time_info->last_update_start_time = (unsigned int)time_ms();
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
                            flag_update_time = 1;
                        }
                    }
                    
                }

                if(flag_update_time)
                    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_UPDATE_TIME_INFO, (int)time_info);                
            }
            break;
        case IVOD_RTSP_ACK_VADILATE:
            break;
        case IVOD_RTSP_ACK_ANNOUNCE_RESPONSE:
            break;
        case IVOD_RTSP_ACK_ERROR_RESPONSE:
            {
                if(302 == p1)
                {
 				    rtsp_request_func_t request[1] = {0};
                    char temp_url[2048] = {0};
    				char *location = ivod_rtsp_session_get_field(session, "Location", ':');
    				if (location) 
                    {
                        strcpy(temp_url,location);
                        
                        /*关闭原有的seesion*/
                        if(me->rtsp_session)
                        {
                            ivod_rtsp_session_close(me->rtsp_session);
                            me->rtsp_session = NULL;
                        } 

                        /*创建新的seesion*/
                        request->rtsp_describe      = ivod_rtsp_isma_describe;
                        request->rtsp_setup         = ivod_rtsp_isma_setup;
                        request->rtsp_play          = ivod_rtsp_isma_play;
                        request->rtsp_pause         = ivod_rtsp_isma_pause;
                        request->rtsp_close         = ivod_rtsp_isma_teardown;
                        request->rtsp_get_parameter = ivod_rtsp_isma_get_parameter;
                        request->rtsp_set_parameter = ivod_rtsp_isma_set_parameter;
                        request->rtsp_options       = ivod_rtsp_isma_option;	
                        request->rtsp_validate      = ivod_rtsp_isma_validate;
                        request->rtsp_ping          = ivod_rtsp_isma_ping;
                        request->rtsp_response      = ivod_rtsp_isma_response;  

                        me->rtsp_session = ivod_rtsp_session_open(me->client_main, me, request);
                        if(NULL == me->rtsp_session)
                        {
                            INFO("%s[ivod_rtsp_isma_response] rtsp_session_register failed\n", ISMA_MARK);
                            me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_ERROR, p1);
                        }
                        else
                        {
                            ivod_rtsp_isma_proc((unsigned int)me,IVOD_RTSP_EVENT_OPEN,(int)temp_url,me->type);
                        }
                        
    				}
                    else
                    {
                        me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_ERROR, p1);
                    }
                  
                }
                else if(p1 == -995 && (!(me->flag_tstv_to_livetv || me->flag_livetv_to_tstv)))
                {
                    /*发送rtsp命令失败*/
                    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_ERROR, p1);
                }
                else if(p1 > 0)
                {
                    implayer_qos_proc("proc",VOD_QOS_EVENT_ERROR,p1,p2);
                    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_ERROR, p1);
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
                    /*如果是组播，这里还需要先退出组播*/
                    if(me->flag_multicast_stream)
                    {
                        if(isocket_leave_multicast(me->sock_id, (unsigned int)&me->data_serverIP) < 0)
                        {
                            INFO("%s[ivod_rtsp_isma_response] leave multicast failed\n", ISMA_MARK);                   
                        } 
                        me->flag_multicast_stream = 0;

                        /*sleep 50ms 保证退出组播的命令已经发出去了*/
                        usleep(50000);                       
                    }
                    
                    isocket_close(me->sock_id);
                    me->sock_id = 0;
                }  

                if(!(me->flag_tstv_to_livetv || me->flag_livetv_to_tstv))
                {                    
                    /*关闭player*/
                    if(me->player_id)
                    {
                        if(iplayer_close(me->player_id) < 0)
                        {
                            INFO("%s[ivod_rtsp_isma_response] close player failed\n", ISMA_MARK); 
                            me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_PLAYER_CLOSE_FAILED, 0);                        
                        }
                        else
                        {
                            INFO("%s[ivod_rtsp_isma_response] close player success\n", ISMA_MARK); 
                            me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_PLAYER_CLOSE_SUCCESS, 0);
                        }

                        me->player_id = 0;
                    } 
                                                   
                    /*命令响应成功*/
                    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_CLOSE, IPANEL_OK);
                }
            }
            break;
    }
    
    return ret;
}     

static int ivod_rtsp_isma_callback_session_get(void *handle, unsigned int op, void *value)
{

    return IPANEL_ERR;    
}

static int ivod_rtsp_isma_callback_session_message(void *handle, unsigned int msg, int value)
{
    return IPANEL_ERR;
}

unsigned int ivod_rtsp_isma_open(client_t *client_main, ivod_rtsp_client_t *rtsp_client, int mode, ivod_msg_func func)
{
    rtsp_request_func_t request[1] = {0};
    ivod_rtsp_isma_t *me = NULL;
    
    FAILED_RETURNX(NULL == client_main 
                   || NULL == rtsp_client
                   || NULL == func, (unsigned int)NULL);
    
    me = (ivod_rtsp_isma_t *)calloc(1, sizeof(ivod_rtsp_isma_t));
    if(NULL == me)
    {
        INFO("%s[ivod_rtsp_isma_open] calloc failed\n", ISMA_MARK);
        goto FAILED;
    }

    memset(me,0,sizeof(ivod_rtsp_isma_t));
    me->client_main = client_main;
    me->rtsp_client = rtsp_client;    
    if(mode == IVOD_MODE_NONE)
        me->mode = IVOD_MODE_DVB;
    else    
        me->mode = mode;

    me->func = func;
    
    me->video_trackID = -1;
    me->audio_trackID = -1;
    me->current_setup_track = -1;

    request->rtsp_describe      = ivod_rtsp_isma_describe;
    request->rtsp_setup         = ivod_rtsp_isma_setup;
    request->rtsp_play          = ivod_rtsp_isma_play;
    request->rtsp_pause         = ivod_rtsp_isma_pause;
    request->rtsp_close         = ivod_rtsp_isma_teardown;
    request->rtsp_get_parameter = ivod_rtsp_isma_get_parameter;
    request->rtsp_set_parameter = ivod_rtsp_isma_set_parameter;
    request->rtsp_options       = ivod_rtsp_isma_option;	
    request->rtsp_validate      = ivod_rtsp_isma_validate;
    request->rtsp_ping          = ivod_rtsp_isma_ping;
    request->rtsp_response      = ivod_rtsp_isma_response;  

    /*注册一个session*/
    me->rtsp_session = ivod_rtsp_session_open(me->client_main, me, request);
    if(NULL == me->rtsp_session)
    {
        INFO("%s[ivod_rtsp_isma_open] rtsp_session_register failed\n", ISMA_MARK);
        goto FAILED;
    }
    
    INFO("%s[ivod_rtsp_isma_open] rtsp_isma_open success\n", ISMA_MARK);
    return (unsigned int)me;
    
FAILED:
    if(me)
    {
        if(me->rtsp_session)
        {
            ivod_rtsp_session_close(me->rtsp_session);
            me->rtsp_session = NULL;
        }
        memset(me,0,sizeof(ivod_rtsp_isma_t));
        free(me);
        me = NULL;
    }
    
    return (unsigned int)NULL;
}

int ivod_rtsp_isma_close(unsigned int handle)
{
    ivod_rtsp_isma_t *me = (ivod_rtsp_isma_t*)handle;

    /*关闭会话*/
    if(me->rtsp_session)
    {
        ivod_rtsp_session_close(me->rtsp_session);
        me->rtsp_session = NULL;
    }

    /*关闭socket*/
    if(me->sock_id > 0)
    {
        /*如果是组播，这里还需要先退出组播*/
        if(me->flag_multicast_stream)
        {
            if(isocket_leave_multicast(me->sock_id, (unsigned int)&me->data_serverIP) < 0)
            {
                INFO("%s[ivod_rtsp_isma_close] leave multicast failed\n", ISMA_MARK);                   
            } 
            me->flag_multicast_stream = 0;

            /*sleep 50ms 保证退出组播的命令已经发出去了*/
            usleep(50000);
            
        }
                
        isocket_close(me->sock_id);
        me->sock_id = 0;
    }  

    if(me)
    {    
        memset(me,0,sizeof(ivod_rtsp_isma_t));
        free(me);
        me = NULL;
    }
    
    INFO("%s[ivod_rtsp_isma_close] close success\n", ISMA_MARK); 

    return IPANEL_OK;
}

int ivod_rtsp_isma_proc(unsigned int handle, unsigned int event, int p1, int p2)
{
    int ret = IPANEL_ERR;
    ivod_rtsp_session_t *session = NULL;
    ivod_rtsp_isma_t *me = (ivod_rtsp_isma_t *)handle;
    
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
                INFO("%s[ivod_rtsp_isma_proc] open type=%d,url=\n%s\n", ISMA_MARK,p2,me->url);
            }
            break;
        }
        else
            session = ivod_rtsp_session_get_next(session);/*有的VOD有两个session,比如NGOD*/
    }
    
    return ret;
}

int ivod_rtsp_isma_set_prop(unsigned int handle, int prop, int value)
{
    return IPANEL_OK;
}

int ivod_rtsp_isma_get_prop(unsigned int handle, int prop, void *value)
{
    return IPANEL_OK;
}


/*解析URL中参数*/
static int ivod_rtsp_isma_parse_param(unsigned int handle, char *url)
{
	char  *p_url = NULL,*p_start = NULL,*p_end = NULL;
    ivod_rtsp_isma_t *me = (ivod_rtsp_isma_t *)handle;
    
    FAILED_RETURNX(NULL == me || NULL == url, IPANEL_ERR);

	
	p_url = strchr( url,'?' );

	FAILED_RETURNX(!p_url,IPANEL_ERR);
	
	p_start = p_url+1;
	
	while( 1 )
	{
		char *temp = NULL;
		int value_len = 0,str_len = 0;

		p_end = strchr(p_start,'&');
			
		temp = strchr(p_start,'=');
	
		if( temp ) 
        {
			str_len = temp - p_start;

			str_len = str_len >= sizeof(me->param_key[0]) ? sizeof(me->param_key[0])-1 : str_len;

            if(str_len > 0)
    			strncpy(me->param_key[me->param_num],p_start,str_len);
			
			value_len = p_end ? ( p_end - temp - 1 ) : sizeof(me->param_value[me->param_num]);

			value_len = value_len >= sizeof(me->param_value[me->param_num]) ? sizeof(me->param_value[me->param_num])-1 : value_len;

            if(value_len > 0)
    			strncpy(me->param_value[me->param_num],temp+1,value_len);
			
			INFO("%s[ivod_rtsp_isma_parse_param]param_key: %s, param_value: %s.\n",ISMA_MARK,me->param_key[me->param_num],me->param_value[me->param_num]);

			me->param_num++;
		} 
        else 
        {
			break;
		}
		
		if( p_end == NULL ) 
        {
			break;
		}

		p_start = p_end + 1;
	}

	return IPANEL_OK;
}



