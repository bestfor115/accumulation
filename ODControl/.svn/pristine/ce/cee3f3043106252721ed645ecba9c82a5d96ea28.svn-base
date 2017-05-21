/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:IGMP CLIENT实现
*********************************************************************/
#include "iplayer_socket.h"
#include "iplayer_player.h"
#include "iplayer_common.h"
#include "iplayer_main.h"
#include "ivod_client_main.h"
#include "ivod_igmp_client.h"

#define IGMP_MARK "[IVOD][IGMP CLIENT]"

typedef struct ivod_igmp_client_s
{
    client_t *client_main;
    ivod_msg_func func;
    int cur_client_event;
    int sock_id;
    int player_id;
    IPAddr0 multi_ip;
    int flag_livetv_to_tstv;
}ivod_igmp_client_t;

unsigned int ivod_igmp_open(client_t *client_main, ivod_msg_func func)
{
    ivod_igmp_client_t *me = NULL;
    
    FAILED_RETURNX(NULL == client_main, (unsigned int)NULL);

    INFO("%s[ivod_igmp_open] start\n", IGMP_MARK);

    me = (ivod_igmp_client_t *)calloc(1, sizeof(ivod_igmp_client_t));
    if(NULL == me)
    {
        INFO("%s[ivod_igmp_open] open igmp client failed\n", IGMP_MARK);
        goto FAILED;
    }
    
    me->client_main = client_main;
    if(func)
    {
        me->func = func;
    }
    else
    {
        INFO("%s[ivod_igmp_open] callback function null\n", IGMP_MARK);
        goto FAILED;
    }

    INFO("%s[ivod_igmp_open] success\n", IGMP_MARK);

    return (unsigned int)me;   
    
FAILED:     
    if(me)
    {
        free(me);
        me = NULL;
    }

    INFO("%s[ivod_igmp_open] failed\n", IGMP_MARK);

    return (unsigned int)NULL;
}

int ivod_igmp_proc(unsigned int handle, unsigned int event, int p1, int p2)
{
    ivod_igmp_client_t *me = (ivod_igmp_client_t *)handle;
    client_t *client = NULL;
    
    FAILED_RETURNX(NULL == me || NULL == me->client_main, IPANEL_ERR);

    client = me->client_main;
    
    switch(event)
    {
        case CLIENT_EVENT_OPEN:
            {
                char *url = (char *)p1;
                unsigned int port = 0;
                int player_id = 0;
                IPAddr0 ip[1] = {0};

                INFO("%s[ivod_igmp_proc][CLIENT_EVENT_OPEN]\n", IGMP_MARK);

                /*从url中解析出IP以及端口*/
                if(IPANEL_ERR == ivod_common_parse_url(url, (unsigned int)ip, &port))
                {
                    /*解析服务器ip地址和端口错误*/
                    INFO("%s[ivod_igmp_proc][CLIENT_EVENT_OPEN] parse url failed\n", IGMP_MARK);
                    me->func(client, CLIENT_CALLBACK_TYPE_RESULT, event, -991);
                    break;
                }
                
                /*判断IP地址合法性*/
                if( ((((ip->addr.ipv4>>24)&0xff) < 224 || ((ip->addr.ipv4>>24)&0xff) > 239) && IPANEL_IP_VERSION_4 == ip->version) || port == 0)
                {
                    /*打开失败*/
                    INFO("%s[ivod_igmp_proc][CLIENT_EVENT_OPEN] url invalid\n", IGMP_MARK);
                    me->func(client, CLIENT_CALLBACK_TYPE_RESULT, event, -991);
                    break;
                }
                else
                {
                    memset(&me->multi_ip,0,sizeof(me->multi_ip));
                    memcpy(&me->multi_ip,ip,sizeof(IPAddr0));
                    /*创建socket*/
                    me->sock_id = isocket_open(ISOCKET_TYPE_UDP, ip->version, client);
                    if(me->sock_id <= 0)
                    {
                        INFO("%s[ivod_igmp_proc][CLIENT_EVENT_OPEN] socket open failed\n", IGMP_MARK);
                        goto OPEN_FAILED;
                    }
                    /*bind 端口*/
                    memset(ip,0,sizeof(ip));
                    ip->version = me->multi_ip.version;
                    if(isocket_bind(me->sock_id, (unsigned int)ip, port) < 0)
                    {
                        INFO("%s[ivod_igmp_proc][CLIENT_EVENT_OPEN] socket bind failed\n", IGMP_MARK);                        
                        goto OPEN_FAILED;
                    }
                    /*获取当前player_id,可能本身就绑定了*/
                    me->func(client, CLIENT_CALLBACK_TYPE_GET, CLIENT_INFO_PLAYER_ID, (int)&player_id);
                    if(player_id <= 0)
                    {
                        iplayer_info_t info[1] = {0};
                        
                        info->mode = IPLAYER_MODE_IP_TS;
                        
                        /*创建player*/
                        player_id = iplayer_open(client, info);
                        if(player_id <= 0)
                        {
                            INFO("%s[ivod_igmp_proc][CLIENT_EVENT_OPEN] player_open failed\n", IGMP_MARK);
                            me->func(client, CLIENT_CALLBACK_TYPE_MSG, CLIENT_MSG_PLAYER_OPEN_FAILED, 0);
                            goto OPEN_FAILED;
                        }
                        else
                        {
                            /*打开player成功,player id整上去*/
                            me->func(client, CLIENT_CALLBACK_TYPE_MSG, CLIENT_MSG_PLAYER_OPEN_SUCCESS, player_id); 
                        }
                    }
                    
                    me->player_id = player_id;
                    
                    /*bind到直播电视*/
                    me->func(client, CLIENT_CALLBACK_TYPE_MSG, CLIENT_MSG_PLAYER_BIND, BIND_TYPE(IVOD_TYPE_LiveTV));
                    INFO("%s[ivod_igmp_proc][CLIENT_EVENT_OPEN] start join multicast socket_id = %d, time = %u\n", IGMP_MARK, me->sock_id, time_ms());
                    /*加入组播,成功的话,马上就有数据来了*/
                    if(isocket_join_multicast(me->sock_id, (unsigned int)&me->multi_ip) < 0)
                    {
                        INFO("%s[ivod_igmp_proc][CLIENT_EVENT_OPEN] join multicast failed\n", IGMP_MARK);
                        goto OPEN_FAILED;
                    }

                    /*通知QOS开始连接服务器*/
                    implayer_qos_proc("proc", VOD_QOS_EVENT_CONNECT_URL, 0, 0);
                    
                    /*通知结果*/
                    me->func(client, CLIENT_CALLBACK_TYPE_RESULT, event, IPANEL_OK);
                    INFO("%s[ivod_igmp_proc][CLIENT_EVENT_OPEN] success\n", IGMP_MARK);    
                    break;
                }                
            }        
        OPEN_FAILED:
            INFO("%s[ivod_igmp_proc][CLIENT_EVENT_OPEN] failed\n", IGMP_MARK);    
            if(me->sock_id > 0)
            {
                isocket_close(me->sock_id);
                me->sock_id = 0;
            }
            me->func(client, CLIENT_CALLBACK_TYPE_RESULT, event, IPANEL_ERR);
            break;                        
        case CLIENT_EVENT_PLAY:
			{
                INFO("%s[ivod_igmp_proc][CLIENT_EVENT_PLAY]\n", IGMP_MARK);
                if(me->player_id)
                {
                    iplayer_info_t info[1] = {0};
                    
                    /*通知player做相应操作*/
                    iplayer_notify(me->player_id, IPLAYER_OP_PLAY, 1);

    				INFO("%s[ivod_igmp_proc][CLIENT_EVENT_PLAY]\n", IGMP_MARK);
    				iplayer_play(me->player_id,IPLAYER_OP_PLAY,info);

                    /*通知QOS开始play*/
                    implayer_qos_proc("proc", VOD_QOS_EVENT_PLAY, 1, 0);
                    
    				me->func(client, CLIENT_CALLBACK_TYPE_RESULT, event, IPANEL_OK);
                }
                else
                {
    				me->func(client, CLIENT_CALLBACK_TYPE_RESULT, event, IPANEL_ERR);
                }
                INFO("%s[ivod_igmp_proc][CLIENT_EVENT_PLAY] success\n", IGMP_MARK);
			}
            break;
        case CLIENT_EVENT_RESUME:    
        case CLIENT_EVENT_FORWARD:
            /*这几个命令不支持,一律返回失败*/
            me->func(client, CLIENT_CALLBACK_TYPE_RESULT, event, IPANEL_ERR);            
            break;
        case CLIENT_EVENT_SEEK:            
        case CLIENT_EVENT_PAUSE:
        case CLIENT_EVENT_BACKWARD:
            {
                char tstv_url[2048] = {0};

                INFO("%s[ivod_igmp_proc] event = %d\n", IGMP_MARK, event);
                /*获取时移url*/
                me->func(client, CLIENT_CALLBACK_TYPE_GET, CLIENT_INFO_TSTV_URL, (int)tstv_url);
                if(0 == strlen(tstv_url))
                {
                    /*不支持时移*/
                    me->func(client, CLIENT_CALLBACK_TYPE_RESULT, event, IPANEL_ERR);                    
                }
                else
                {                    
                    /*支持时移、发送直播切时移命令*/
                    me->flag_livetv_to_tstv = 1;
                    me->func(client, CLIENT_CALLBACK_TYPE_MSG, CLIENT_MSG_LIVE_TO_TSTV, p1);
                    /*命令转到时移处理了,这里不反馈命令处理结果*/                  
                }
                INFO("%s[ivod_igmp_proc] event = %d success\n", IGMP_MARK, event);
            }
            break;
        case CLIENT_EVENT_STOP:
        case CLIENT_EVENT_CLOSE:
            {
                int bind = 0;
                INFO("%s[ivod_igmp_proc] event = %d\n", IGMP_MARK, event);
                /*关闭socket*/
                if(me->sock_id)
                {
                    INFO("%s[ivod_igmp_proc] start leave multicast socket_id = %d, time = %u\n", IGMP_MARK, me->sock_id, time_ms());
                    /*退出组播*/
                    if(isocket_leave_multicast(me->sock_id, (unsigned int)&me->multi_ip) < 0)
                    {
                        INFO("%s[ivod_igmp_proc] leave multicast failed\n", IGMP_MARK);                   
                    }
                    /*sleep 50ms 保证退出组播的命令已经发出去了*/
                    usleep(50000);
                    /*关闭socket*/
                    isocket_close(me->sock_id);
                    me->sock_id = 0;
                }

                /*非直播转时移情况下，关闭播放器*/
                if(!me->flag_livetv_to_tstv)
                {
                    /*检查是否bind*/
                    me->func(client, CLIENT_CALLBACK_TYPE_GET, CLIENT_INFO_BIND_TYPE, (int)&bind);
                    if(me->player_id && (bind&BIND_TYPE(IVOD_TYPE_LiveTV)))
                    {
                        /*关闭player*/
                        if(iplayer_close(me->player_id) < 0)
                        {
                            INFO("%s[ivod_igmp_proc] close player failed\n", IGMP_MARK); 
                            me->func(client, CLIENT_CALLBACK_TYPE_MSG, CLIENT_MSG_PLAYER_CLOSE_FAILED, 0);                        
                        }
                        else
                        {
                            INFO("%s[ivod_igmp_proc] close player success\n", IGMP_MARK); 
                            me->player_id = 0;
                            me->func(client, CLIENT_CALLBACK_TYPE_MSG, CLIENT_MSG_PLAYER_CLOSE_SUCCESS, 0);
                        }
                    }
                    /*成功*/
                    me->func(client, CLIENT_CALLBACK_TYPE_RESULT, event, IPANEL_OK);
                }
                INFO("%s[ivod_igmp_proc] event = %d success\n", IGMP_MARK, event);
            }
            break;
        /*IGMP是UDP协议,没有SOCKET交互消息,SOCKET部分消息可不处理*/    
        default:
            break;
    }
    
    return IPANEL_OK;
}

int ivod_igmp_close(unsigned int handle)
{
    ivod_igmp_client_t *me = (ivod_igmp_client_t *)handle;
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    
    INFO("%s[ivod_igmp_close] start time =%u\n", IGMP_MARK,time_ms()); 

    if(me)
    {
        if(me->sock_id)
        {
            INFO("%s[ivod_igmp_close] start leave multicast socket_id = %d, time = %u\n", IGMP_MARK, me->sock_id, time_ms());
            /*退出组播*/
            if(isocket_leave_multicast(me->sock_id, (unsigned int)&me->multi_ip) < 0)
            {
                INFO("%s[ivod_igmp_close] leave multicast failed\n", IGMP_MARK);                   
            }
            /*sleep 50ms 保证退组播的命令已经发出去了*/
            usleep(50000);
			/*关闭socket*/
            isocket_close(me->sock_id);
            me->sock_id = 0;
        }        
        
        memset(me,0,sizeof(ivod_igmp_client_t));
        free(me);
        me = NULL;
    }
    
    INFO("%s[ivod_igmp_close] end time =%u\n", IGMP_MARK,time_ms()); 
    
    return IPANEL_OK;
}

int ivod_igmp_set_prop(unsigned int handle, int prop, int value)
{
    INFO("%s[ivod_igmp_set_prop]\n", IGMP_MARK);    

    return IPANEL_OK;
}

int ivod_igmp_get_prop(unsigned int handle, int prop, void *value)
{
    INFO("%s[ivod_igmp_get_prop]\n", IGMP_MARK); 
    
    return IPANEL_OK;
}


