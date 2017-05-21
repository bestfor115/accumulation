/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
����:Telecom vod�ͻ���ʵ��
*********************************************************************/
#include <netinet/in.h>
#include "ivod_rtsp_telecom.h"
#include "ivod_rtsp_session.h"
#include "ivod_client_time.h"
#include "iplayer_socket.h"
#include "iplayer_player.h"
#include "iplayer_vod_main.h"
#include "ivod_rtsp_session.h"
#include "ivod_rtsp_protocol.h"
#include "ivod_rtsp_client.h"

#define TELECOM_IPTV_USER_AGENT "User-Agent: CTC/2.0\r\n"
#define TELECOM_MARK "[IVOD][TELECOM]"

enum
{
	TELECOM_NAT_TYPE_DEFAULT,
	TELECOM_NAT_TYPE_ZTE,
	TELECOM_NAT_TYPE_UT,
	TELECOM_NAT_TYPE_UNDEFINED
}TELECOM_NAT_TYPE_e;


typedef struct
{
	int  type;                         //���˻�������,ȡֵTELECOM_NAT_TYPE_e
	unsigned int  heart_start_time;    //NAT����ʱ��
    unsigned int  heart_time;          //NAT����ʱ����
	char data[84];	                   //NAT��Ϣ
    
    int  flag_used:1;                  //�Ƿ�ʹ��
}telecom_nat_t;


typedef struct ivod_rtsp_telecom_s
{
    client_t *client_main;
    ivod_rtsp_client_t *rtsp_client;
    ivod_rtsp_session_t *rtsp_session;
    ivod_msg_func func;     //�ص�����

    char url[2048];         //�㲥��ַ
    int  type;              //��������liveTV��TSTV��VOD
    int  mode;              //����ģʽIP��DVB
    int  server_name;       //ǰ�˷�����
    int  speed;             //��ǰ�����ٶ�
    int player_id;                          //������id
    int sock_id;                            //����ͨ��socket��һ����udp���ͣ�tcp����socket����Ҫ����
    int current_op;                         //��ǰ��������

    int heart_event;                        //����
    int heart_time;                         //����ʱ����
    int heart_start_time;                   //������ʼʱ��

    IPAddr0 serverIP[1];                    //�㲥��ַ������ip��ַ
    IPAddr0 data_serverIP[1];               //����������ip��ַ
    int     data_serverPort;                //��������������socket�˿�

    char localIP[64];                       //����ip��ַ(�ַ���)
    int rtsp_clientPort;                    //����rtsp��������socket��Ӧ�Ķ˿�
    int data_clientPort;                    //�������ݽ��ն˿�

    telecom_nat_t  NAT[1];                  //NAT��Խ�ṹ
    int NAT_sockId;                         //����NAT�ĵڶ���socket

    unsigned int sessionId;                 //�Ự���

    unsigned int flag_first_play:1;         //��һ��play���
    unsigned int flag_livetv_to_tstv:1;     //ֱ��תʱ�Ʊ��
    unsigned int flag_tstv_to_livetv:1;     //ʱ��תֱ�����
    unsigned int flag_multicast_stream:1;   //ǰ���·����Ƿ����鲥��
    unsigned int flag_first_NAT_Send:1;     //��һ�η���NAT����
}ivod_rtsp_telecom_t;


static int 
ivod_rtsp_telecom_NAT_data(ivod_rtsp_telecom_t *me);


static int ivod_rtsp_telecom_response_describe(ivod_rtsp_telecom_t *me, ivod_rtsp_session_t *session)
{
    char *ptr = NULL;
    client_time_t time_info[1] = {0};

    
    FAILED_RETURNX(NULL == me, IPANEL_ERR); 

    /*��ȡserver*/
    ptr = ivod_rtsp_session_get_field(session, "Server", ':');
    if(ptr) 
    {
        if( strstr(ptr,"HMS_V") ) 
        {
        	me->server_name = IVOD_RTSP_SERVER_HUAWEI;
        } 
        else if( strstr(ptr,"ZMSS") || strstr(ptr,"ZXUSS")) 
        {
        	me->server_name = IVOD_RTSP_SERVER_ZTE;
        } 
        else 
        {
        	me->server_name = IVOD_RTSP_SERVER_NONE;
        }
    }

    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_GET, IVOD_RTSP_PROP_TIME_INFO, (int)time_info);

    /*��ȡRange�ֶ�*/
    ptr = ivod_rtsp_session_get_field(session, "a=range",':');
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
                //client_time_update_current_time(time_info, time_info->start_time);
                time_info->time_type = time_type;
            }

            if(strlen(to))
            {
                client_time_string_to_seconds(to, &time_info->end_time, &time_type);

                if(IVOD_TYPE_LiveTV == me->type || IVOD_TYPE_TSTV == me->type)
                {
                    //����ֻ�ܼ򵥸�ֵ�����ܵ���client_time_update_current_time����
                    time_info->present_time = time_info->end_time;
                }
                
                if(time_info->end_time > time_info->start_time)
                    time_info->duration = time_info->end_time - time_info->start_time;
            }

            if(strlen(from) || strlen(to))
            {
                /*֪ͨ����ʱ��*/
                me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_UPDATE_TIME_INFO, (int)time_info);                
            }
        }
        
    }
    
    return ivod_rtsp_session_proc(session, IVOD_RTSP_EVENT_SETUP, 0, 0);
}

static int ivod_rtsp_telecom_response_setup(ivod_rtsp_telecom_t *me, ivod_rtsp_session_t *session)
{
    char *ptr = NULL, *p_str = NULL;
    unsigned int index = 0, i = 0;
    char  key_word[64][32] = {0};
    char  key_value[64][32] = {0};
    iplayer_info_t info[1] = {0};
    int player_id = 0;
    IPAddr0 ipaddr[1] = {0};
    int stream_socket_type = ISOCKET_TYPE_TCP;
    client_time_t time_info[1] = {0};
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR); 

    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_GET, IVOD_RTSP_PROP_TIME_INFO, (int)time_info);
    
    /*��ȡsession id*/
    ptr = ivod_rtsp_session_get_field(session, "Session", ':');
    if(NULL == ptr)
    {
        INFO("%s[ivod_rtsp_telecom_response_setup] get Session failed\n", TELECOM_MARK);
        return IPANEL_ERR;
    }

    ivod_rtsp_session_set_session(session, ptr);
    me->sessionId = atoi(ptr);

    /*��ȡserver*/
    if(IVOD_RTSP_SERVER_NONE == me->server_name)
    {
        ptr = ivod_rtsp_session_get_field(session, "Server", ':');
        if(NULL == ptr)
        {
            INFO("%s[ivod_rtsp_telecom_response_setup] get Server failed\n", TELECOM_MARK);
            me->server_name = IVOD_RTSP_SERVER_NONE;
        }
        else
        {
            INFO("%s[ivod_rtsp_telecom_response_setup] get Server = %s\n", TELECOM_MARK,ptr);

            if( strstr(ptr,"HMS_V") ) 
            {
            	me->server_name = IVOD_RTSP_SERVER_HUAWEI;
            } 
            else if( strstr(ptr,"ZMSS") || strstr(ptr,"ZXUSS")) 
            {
            	me->server_name = IVOD_RTSP_SERVER_ZTE;
            } 
            else 
            {
            	me->server_name = IVOD_RTSP_SERVER_NONE;
            }            
        }
    }
    
    /*��ȡTransport��������*/
    ptr = ivod_rtsp_session_get_field(session, "Transport", ':');
    if(NULL == ptr)
    {
        INFO("%s[ivod_rtsp_telecom_response_setup] get Transport failed\n", TELECOM_MARK);
        return IPANEL_ERR;
    }

    if(strstr(ptr,"MP2T/RTP/UDP") || strstr(ptr,"MP2T/UDP"))
    {
        stream_socket_type = ISOCKET_TYPE_UDP;
        info->mode = IPLAYER_MODE_IP_TS;
    }
    else if(strstr(ptr,"MP2T/RTP/TCP") || strstr(ptr,"MP2T/TCP"))
    {
        stream_socket_type = ISOCKET_TYPE_TCP;
        info->mode = IPLAYER_MODE_IP_TS;
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
            INFO("%s[ivod_rtsp_telecom_response_setup] key_word = %s, key_value = %s\n", TELECOM_MARK,key_word[i], key_value[i]);
        }
    }    

    
    for( i = 0; i < sizeof(key_word)/sizeof(key_word[0]); i++) 
    {
        if(strlen(key_word[i]) == 0 || strlen(key_value[i]) == 0)
            continue;

        /*������ip��ַ*/
        if(strcmp(key_word[i], "source=") == 0)
        {
            if(IPANEL_IP_VERSION_6 == me->serverIP->version)
                ivod_common_ipv6_str_to_int(key_value[i], me->data_serverIP);
            else
                ivod_common_ipv4_str_to_int(key_value[i], me->data_serverIP);
            
            me->flag_first_NAT_Send = 1;
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

    /*��������*/
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
        /*֪ͨ����ʱ��*/
        me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_UPDATE_TIME_INFO, (int)time_info);
    }

    if(stream_socket_type == ISOCKET_TYPE_UDP && info->mode == IPLAYER_MODE_IP_TS)
    {
        int socket_version = IPANEL_IP_VERSION_4;

        if(me->serverIP->version > 0)
        {
            socket_version = me->serverIP->version;
        }
        
        /*����socket*/
        me->sock_id = isocket_open(ISOCKET_TYPE_UDP, socket_version, me->client_main);
        if(me->sock_id <= 0)
        {
            INFO("%s[ivod_rtsp_telecom_response_setup]socket open failed\n", TELECOM_MARK);
            goto OPEN_FAILED;
        }
        /*bind �˿�*/
        memset(ipaddr,0,sizeof(IPAddr0));
        ipaddr->version = socket_version;
        if(isocket_bind(me->sock_id, (unsigned int)ipaddr, me->data_clientPort) < 0)
        {
            INFO("%s[ivod_rtsp_telecom_response_setup]socket bind failed\n", TELECOM_MARK);
            goto OPEN_FAILED;
        }

        /*����NAT�ĵڶ���socket*/
        if(IVOD_TYPE_LiveTV == me->type)
        {
            me->NAT_sockId = isocket_open(ISOCKET_TYPE_UDP, socket_version, me->client_main);
            if(me->NAT_sockId <= 0)
            {
                INFO("%s[ivod_rtsp_telecom_response_setup]NAT socket open failed\n", TELECOM_MARK);
            }
            else
            {
                /*bind �˿�*/
                memset(ipaddr,0,sizeof(IPAddr0));
                ipaddr->version = socket_version;
                if(isocket_bind(me->NAT_sockId, (unsigned int)ipaddr, (me->data_clientPort+1)) < 0)
                {
                    INFO("%s[ivod_rtsp_telecom_response_setup]NAT socket bind failed\n", TELECOM_MARK);                        
                }
            }
        }
        
    }
   
    /*��ȡplayer_id*/
    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_GET, IVOD_RTSP_PROP_PLAYER_ID, (int)&player_id);
    if(player_id <= 0)
    {
        player_id = iplayer_open(me->client_main, info);
        if(player_id <= 0)
        {
            INFO("%s[ivod_rtsp_telecom_response_setup] player open failed\n", TELECOM_MARK);
            /*��PLAYERʧ��*/
            me->func(me->rtsp_client,IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_PLAYER_OPEN_FAILED, 0);
            goto OPEN_FAILED;
        }
        /*��player�ɹ�*/
        me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_PLAYER_OPEN_SUCCESS, player_id);
    }
    me->player_id = player_id; 

    /*������鲥������Ҫ�����鲥��*/
    if(IVOD_TYPE_LiveTV == me->type && IPANEL_IP_VERSION_4 == me->data_serverIP->version)
    {
        /*224.0.1.0��238.255.255.255��������Internet�ϵģ��鲥��ַ��Χ*/
        if(me->data_serverIP->addr.ipv4 >= 0xE0000100 && me->data_serverIP->addr.ipv4 <= 0xEFFFFFFF)
        {
            if(isocket_join_multicast(me->sock_id, (unsigned int)me->data_serverIP) < 0)
            {
                INFO("%s[ivod_rtsp_telecom_response_setup] join multicast failed\n", TELECOM_MARK);
                goto OPEN_FAILED;
            }

            me->flag_multicast_stream = 1;
        }
    }
    
    /*������Ӧ�ɹ�*/
    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_OPEN, IPANEL_OK);
    
	return IPANEL_OK;
    
OPEN_FAILED:
    
    /*����ִ��ʧ��*/
    if(me->sock_id > 0)
    {
        isocket_close(me->sock_id);
        me->sock_id = 0;
    }
    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_OPEN, IPANEL_ERR);
    
    return IPANEL_ERR;
}

static int ivod_rtsp_telecom_response_play(ivod_rtsp_telecom_t *me, ivod_rtsp_session_t *session)
{
    char *ptr = NULL;
    int speed = 0,seq = -1;
    active_info_t info[1] = {0};
    client_time_t time_info[1] = {0};
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_GET, IVOD_RTSP_PROP_TIME_INFO, (int)time_info);

    /*��ȡ����*/
    ptr = ivod_rtsp_session_get_field(session, "Scale", ':');
    if(ptr)
    {
        speed = atoi(ptr);
    }
    else
    {
        speed = me->speed;
    }
    
    /*��ȡrange*/
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
            		//���ǰ�˷��ص�ʱ��Ϊend�����ý���ʱ����µ�ǰʱ��
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
                /*֪ͨ����ʱ��*/
                me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_UPDATE_TIME_INFO, (int)time_info);                
            }
        }
        
    }
    
    /*��ȡRTP-Info*/
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
            INFO("%s[ivod_rtsp_telecom_response_play] RTP-Info seq = %d\n", TELECOM_MARK,seq);
		}

        //���¼������������ip��ַ�Ͷ˿�
        p_field = strstr(ptr,"rtsp://");
        if(p_field 
            && IPANEL_IP_VERSION_4 == me->serverIP->version 
            && IVOD_RTSP_SERVER_ZTE == me->server_name
            && IVOD_TYPE_LiveTV == me->type)
        {
            p_field = p_field + strlen("rtsp://");
            if(p_field)
            {
                str_end = strstr(p_field,":");
                if(str_end)
                {
                    strncpy(new_ip,p_field,(str_end-p_field)>sizeof(new_ip)?(sizeof(new_ip)-1):(str_end-p_field));
                    INFO("%s[ivod_rtsp_telecom_response_play]play response server = %s\n", TELECOM_MARK,new_ip);
                    
                    p_field = str_end + 1;
                    if(p_field)
                    {
                        str_end = strstr(p_field,"/");
                        if(str_end)
                        {
                            strncpy(new_port,p_field,(str_end-p_field)>sizeof(new_port)?(sizeof(new_port)-1):(str_end-p_field));
                            INFO("%s[ivod_rtsp_telecom_response_play]play response server port = %s\n", TELECOM_MARK,new_port);
                        }
                    }

                    if(IPANEL_IP_VERSION_6 == me->serverIP->version)
                        ivod_common_ipv6_str_to_int(new_ip, temp_ip);
                    else
                        ivod_common_ipv4_str_to_int(new_ip, temp_ip);

                    temp_port = atoi(new_port);
                    
                    //�ж��Ƿ�Ҫ��һ�δ�Խ��Ϣ
                    if(((me->data_serverIP->addr.ipv4 != temp_ip->addr.ipv4) || (temp_port != me->data_serverPort)) && (temp_port>0))
                    {
            			memcpy(me->data_serverIP,temp_ip,sizeof(IPAddr0));
                        me->data_serverPort = temp_port;
                        me->flag_first_NAT_Send = 1;
                    }

                    if(me->flag_first_NAT_Send)
                    {
                        int i = 0;
                        
                        //��װNAT��Ϣ
                        ivod_rtsp_telecom_NAT_data(me);
                        
                        //��������3��NAT��Ϣ
                        INFO("%s[ivod_rtsp_telecom_response_play]start send NAT data\n", TELECOM_MARK);
                        for(i=0;i<3;i++)
                        {
                            if(me->sock_id)
                                isocket_sendto((unsigned int)me->sock_id, me->NAT->data, sizeof(me->NAT->data), 0, (unsigned int)me->data_serverIP, me->data_serverPort);
                            if(me->NAT_sockId)
                                isocket_sendto((unsigned int)me->NAT_sockId, me->NAT->data, sizeof(me->NAT->data), 0, (unsigned int)me->data_serverIP, (me->data_serverPort+1));
                        }
                        INFO("%s[ivod_rtsp_telecom_response_play]end send NAT data\n", TELECOM_MARK);
                        
                        me->NAT->flag_used = 1;
                        me->NAT->heart_start_time = (unsigned int)time_ms();
                        me->NAT->heart_time = 300;
                        
                        me->flag_first_NAT_Send = 0;
                    }
                }
            }
        }            
    }

    /*���û�׼ts��seq*/
    iplayer_set_prop(me->player_id, IPLAYER_PARAM_RTP_SEQ, seq);
    /*֪ͨ���������ųɹ�*/
    iplayer_play(me->player_id, me->current_op, info);
    
    /*������Ӧ�ɹ�*/
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

static int ivod_rtsp_telecom_describe(void *handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    ivod_rtsp_telecom_t *me = (ivod_rtsp_telecom_t *)handle;
    int seq = 0;
    char buffer[512] = {0};
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    /*���seq*/
    seq = ivod_rtsp_session_add_cseq(session);

    /*���accept*/
	strcpy(buffer,"Accept:application/sdp\r\n");
	ivod_rtsp_session_add_field(session,buffer);

    /*���User Agent*/
    strcpy(buffer, TELECOM_IPTV_USER_AGENT);
	ivod_rtsp_session_add_field(session, buffer);

    /*���NAT��Ϣ*/
	if(IVOD_TYPE_TSTV == me->type)
	{
        //strcpy(buffer,"Timeshift: 1");
    	//ivod_rtsp_session_add_field(session, buffer);
    }
    else if(IVOD_TYPE_LiveTV == me->type)
    { 
        memset(buffer, 0, sizeof(buffer));
    	sprintf(buffer,"x-zmssFecCDN: yes\r\nx-NAT:%s:%d\r\nx-zmssRtxSdp: yes\r\nx-Index: \r\n", me->localIP, me->rtsp_clientPort);
    	ivod_rtsp_session_add_field(session, buffer);
    }
    
    /*���\r\n\r\n*/
	ivod_rtsp_session_add_field(session, END_STRING);

    /*֪ͨQOS��ʼ���ӷ�����*/
	implayer_qos_proc("proc", VOD_QOS_EVENT_CONNECT_URL, 0, 0);    

    return seq;
}

static int ivod_rtsp_telecom_setup(void *handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    ivod_rtsp_telecom_t *me = (ivod_rtsp_telecom_t *)handle;
    int seq = 0;
    char buffer[512] = {0};
    int port = 0;
	IPANEL_NETWORK_IF_PARAM localIP[1] = { 0 };
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    /*���seq*/
    seq = ivod_rtsp_session_add_cseq(session);
    /*���Transport*/
    
    port = ivod_common_get_dynamic_port();
    
    if(me->mode == IVOD_MODE_IP)
    {
#if 0
    	sprintf(buffer, "Transport: MP2T/RTP/UDP;unicast;destination=%s;client_port=%d-%d,MP2T/UDP;unicast;destination=%s;client_port=%d-%d,MP2T/TCP;unicast;destination=%s;interleaved=0-1,MP2T/RTP/TCP;unicast;destination=%s;interleaved=0-1\r\n",
    		me->localIP, port, port + 1, me->localIP, port, port + 1, me->localIP, me->localIP);
#else
    	sprintf(buffer, "Transport: MP2T/TCP;unicast;destination=%s;interleaved=0-1,MP2T/RTP/TCP;unicast;destination=%s;interleaved=0-1,MP2T/RTP/UDP;unicast;destination=%s;client_port=%d-%d,MP2T/UDP;unicast;destination=%s;client_port=%d-%d\r\n",
    			me->localIP, me->localIP, me->localIP, port, port + 1, me->localIP, port, port + 1);
#endif
    }
    else
        sprintf(buffer, "Transport:MP2T/DVBC/QAM\r\n");
    ivod_rtsp_session_add_field(session, buffer);
    /*���User Agent*/
    memset(buffer, 0, sizeof(buffer));
    strcpy(buffer, TELECOM_IPTV_USER_AGENT);
	ivod_rtsp_session_add_field(session, buffer);

    /*���NAT��Ϣ*/
	if(IVOD_TYPE_TSTV == me->type)
	{
        //strcpy(buffer,"Timeshift: 1");
    	//ivod_rtsp_session_add_field(session, buffer);
    }
    else if(IVOD_TYPE_LiveTV == me->type)
    {
        memset(buffer, 0, sizeof(buffer));
    	sprintf(buffer,"x-NAT:%s:%d\r\n", me->localIP, port);
    	ivod_rtsp_session_add_field(session, buffer);
    }
    else if(IVOD_TYPE_VOD == me->type)
	{
		if(IVOD_RTSP_SERVER_HUAWEI == me->server_name)
		{
	        memset(buffer, 0, sizeof(buffer));
	    	sprintf(buffer,"x-NAT:%s:%d\r\n", me->localIP, port);
	    	ivod_rtsp_session_add_field(session, buffer);
		}
	}
    
    /*���\r\n\r\n*/
	ivod_rtsp_session_add_field(session, END_STRING);

    return seq;  
}        

static int ivod_rtsp_telecom_play(void *handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    ivod_rtsp_telecom_t *me = (ivod_rtsp_telecom_t *)handle;
    char seek_from[64] = {0}, seek_to[64] = {0};
    char *ptr = NULL;
    int ret = IPANEL_OK, speed = 0, seq = 0;
    char range[128] = {0}, buffer[128] = {0};
    client_time_t time_info[1] = {0};
    
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_GET, IVOD_RTSP_PROP_TIME_INFO, (int)time_info);

    /*����speed�Լ�seek_time*/
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

            //�������˻�Ϊǰ����Ҫ��ͬ����
            if(IVOD_RTSP_SERVER_ZTE == me->server_name)
            {
                if(!me->flag_first_play)
                {
                    //������������ڽ���ʱ��ͷ����ͣ��Ȼ��resume
                    if(time_info->start_time > 0 && time_info->present_time <= time_info->start_time)
                    {
                        strcpy(range, "Range: npt=beginning-\r\n");
                        me->current_op = IPLAYER_OP_SEEK;
                    }
                    else
                    {
                        strcpy(range, "Range: npt=now-\r\n");
                        me->current_op = IPLAYER_OP_RESUME;
                    }
                }
                else
                {
                    strcpy(range, "Range: npt=now-\r\n");
                    me->current_op = IPLAYER_OP_RESUME;
                }
            }
            else
            {
                strcpy(range, "Range: npt=now-\r\n");
                me->current_op = IPLAYER_OP_RESUME;
            }
            break;
        case IVOD_RTSP_EVENT_SEEK:
            ptr = (char *)p1;            
            speed = 1;
            
            /*����seek_time*/
            client_time_parse_string_time(ptr, seek_from, seek_to);                

            /*ʱ��seek 100%תֱ��*/
            if(strchr(seek_from,'%') && atoi(seek_from) == 100 && me->type == IVOD_TYPE_TSTV)
            {
                char live_url[2048] = {0};

                /*��ȡֱ��url*/
                me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_GET, IVOD_RTSP_PROP_LIVE_URL, (int)live_url);                
                if(strncmp(live_url, "igmp://", strlen("igmp://")) == 0)
                {
                    /*igmp��ֱ����client����*/
                    me->flag_tstv_to_livetv = 1;
                    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_TSTV_TO_LIVE, 0);

                    return IPANEL_ERR;
                }
                else
                {
                    /*rtsp��ֱ������ֱ��ת��ֱ��*/
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
                /*rtsp��ֱ������ֱ��ת��ʱ��*/
                me->type = IVOD_TYPE_TSTV;
                me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_UPDATE_VOD_TYPE, me->type);                
                strcpy(range, "Range: npt=beginning-\r\n");
            }            
            else if(strchr(seek_from,'%') && me->type == IVOD_TYPE_VOD)
            {
                int time = atoi(seek_from);

                if(0/*!me->current_op && time==0*/)
                {
                    //���vod open֮��seek(0%)����
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

                    //���seek��ʱ���ڿ�ʼʱ���5s��ǰ��������ʼ�㿪ʼ���ţ���Ϊ����10s����һ����ʼ����ʱ��
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
            
            if(IVOD_RTSP_EVENT_BACKWARD == msg && IVOD_TYPE_LiveTV == me->type)
            {
                char tstv_url[2048] = {0};

                /*��ȡʱ��url*/
                me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_GET, IVOD_RTSP_PROP_TSTV_URL, (int)tstv_url);
                if(0 == strlen(tstv_url))
                {
                    /*��֧��ʱ��*/
                    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_BACKWARD, IPANEL_ERR);
                    return IPANEL_ERR;
                }

                me->type = IVOD_TYPE_TSTV;
                me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_UPDATE_VOD_TYPE, me->type);                
                
            }
            
            speed = p1;
            strcpy(range, "Range: npt=now-\r\n");
            me->current_op = IPLAYER_OP_FWBW;
            break;
        default:
            INFO("%s[ivod_rtsp_telecom_play] failed, event invalid\n",TELECOM_MARK);
            return IPANEL_ERR;
    }

    me->speed = speed;

    /*֪ͨplayer����Ӧ����*/
    iplayer_notify(me->player_id, me->current_op, speed);
    
    /*���seq*/
    seq = ivod_rtsp_session_add_cseq(session);
    /*���session id*/
    ivod_rtsp_session_add_session(session);
    /*���Scale*/
    sprintf(buffer, "Scale: %d.0\r\n", speed);
    ivod_rtsp_session_add_field(session, buffer);
    /*���Range*/
    ivod_rtsp_session_add_field(session, range);
    /*���User Agent*/
	ivod_rtsp_session_add_field(session, TELECOM_IPTV_USER_AGENT);
    /*���\r\n\r\n*/
	ivod_rtsp_session_add_field(session, END_STRING);

    /*֪ͨqos play����*/
	implayer_qos_proc("proc", VOD_QOS_EVENT_PLAY, speed, (IPLAYER_OP_SEEK == me->current_op)? 1 : 0);

    me->flag_first_play = 0;

    return seq;
}         

static int ivod_rtsp_telecom_pause(void *handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    ivod_rtsp_telecom_t *me = (ivod_rtsp_telecom_t *)handle;
    int seq = 0;
    char buffer[256] = {0};
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    /*֪ͨ������������ͣ*/
    iplayer_pause(me->player_id);

    /*ֱ��תʱ��*/
    if(IVOD_TYPE_LiveTV == me->type)
    {
        char tstv_url[2048] = {0};

        /*��ȡʱ��url*/
        me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_GET, IVOD_RTSP_PROP_TSTV_URL, (int)tstv_url);
        if(0 == strlen(tstv_url))
        {
            /*��֧��ʱ��*/
            me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_PAUSE, IPANEL_ERR);
            return IPANEL_ERR;
        }

        me->type = IVOD_TYPE_TSTV;
        me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_UPDATE_VOD_TYPE, me->type);                
        
    }
    
    
    /*���seq*/
    seq = ivod_rtsp_session_add_cseq(session);
    /*���session id*/
    ivod_rtsp_session_add_session(session);    
    /*���User Agent*/
    strcpy(buffer, TELECOM_IPTV_USER_AGENT);
	ivod_rtsp_session_add_field(session, buffer);
    /*���\r\n\r\n*/
	ivod_rtsp_session_add_field(session, END_STRING);

    me->current_op = IPLAYER_OP_PAUSE;

    /*֪ͨqos pause����*/
	implayer_qos_proc("proc",VOD_QOS_EVENT_PAUSE,0,0);

    return seq;
    
}        

static int ivod_rtsp_telecom_teardown(void *handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    ivod_rtsp_telecom_t *me = (ivod_rtsp_telecom_t *)handle;
    int seq = 0;
    char buffer[256] = {0};
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    
    /*���seq*/
    seq = ivod_rtsp_session_add_cseq(session);
    /*���session id*/
    ivod_rtsp_session_add_session(session);    
    /*���User Agent*/
    strcpy(buffer, TELECOM_IPTV_USER_AGENT);
	ivod_rtsp_session_add_field(session, buffer);
    /*���\r\n\r\n*/
	ivod_rtsp_session_add_field(session, END_STRING);

    me->current_op = IPLAYER_OP_CLOSE;

    return seq;
    
}       

static int ivod_rtsp_telecom_get_parameter(void *handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    ivod_rtsp_telecom_t *me = (ivod_rtsp_telecom_t *)handle;
    int seq = 0;
    char buffer[256] = {0};
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    
    /*���seq*/
    seq = ivod_rtsp_session_add_cseq(session);
    /*���session id*/
    ivod_rtsp_session_add_session(session);        
    /*���User Agent*/
    strcpy(buffer, TELECOM_IPTV_USER_AGENT);
	ivod_rtsp_session_add_field(session, buffer);
    
	if(me->type == IVOD_TYPE_TSTV)
	{
		memset(buffer, 0, sizeof(buffer));
		sprintf(buffer, "x-Timeshift_Range\r\n");
    	ivod_rtsp_session_add_field(session, buffer);
	}
    
    /*���\r\n\r\n*/
	ivod_rtsp_session_add_field(session, END_STRING);

    return seq;
}

static int ivod_rtsp_telecom_set_parameter(void *handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    return IPANEL_OK;
}

static int ivod_rtsp_telecom_option(void *handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    ivod_rtsp_telecom_t *me = (ivod_rtsp_telecom_t *)handle;
    int seq = 0;
    char buffer[256] = {0};
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    
    /*���seq*/
    seq = ivod_rtsp_session_add_cseq(session);
    /*���session id*/
    ivod_rtsp_session_add_session(session);   
    /*���User Agent*/
    strcpy(buffer, TELECOM_IPTV_USER_AGENT);
	ivod_rtsp_session_add_field(session, buffer);
    /*���\r\n\r\n*/
	ivod_rtsp_session_add_field(session, END_STRING);

    return seq;
}     

static int ivod_rtsp_telecom_validate(void *handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    return IPANEL_OK;
}     

static int ivod_rtsp_telecom_ping(void *handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    return IPANEL_OK;
}         

static int ivod_rtsp_telecom_response(void *handle, ivod_rtsp_session_t *session, int msg, int p1, int p2)
{
    ivod_rtsp_telecom_t *me = (ivod_rtsp_telecom_t *)handle;
    int seq = 0;
    char buffer[256] = {0};
    char ip[64] = {0};
    unsigned port = 0;
    int ret = IPANEL_OK;
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR); 
    
    INFO("%s[ivod_rtsp_telecom_response] msg=%d, p1=%d, p2=%d\n", TELECOM_MARK,msg,p1,p2); 
    
    switch(msg)
    {
        case IVOD_RTSP_ACK_OPEN:
            {
                IPAddr0 ipaddr[1] = {0};
                unsigned int temp_port = 0;

                if(p1 >= 0)
                {
                    /*����url����ȡ������ip��ַ*/
                    if(IPANEL_OK == ivod_common_parse_url(me->url,(unsigned int)ipaddr,&temp_port))
                    {
                        memcpy(me->serverIP,ipaddr,sizeof(IPAddr0));
                    }
                    else
                    {
                        /*Ĭ��ipv4�汾*/
                        memcpy(me->serverIP,0,sizeof(IPAddr0));
                        me->serverIP->version = IPANEL_IP_VERSION_4;
                    }

                    /*����p1ֵ����ȡrtsp��������socket�ı��ض˿�*/
                    if(IPANEL_IP_VERSION_4 == ipaddr->version)
                        isocket_getsockname((unsigned int)p1, (unsigned int)ipaddr, &me->rtsp_clientPort);

                    /*��ȡ����ip��ַ*/
                    if(IPANEL_IP_VERSION_6 != me->serverIP->version)
                    {
                        ivod_common_get_localIP(NULL, me->localIP, NULL);
                    }
                    else
                    {
                        ivod_common_get_localIP(NULL, NULL, me->localIP);
                    }

                    me->flag_first_play = 1;
    				ret = ivod_rtsp_session_proc(session, IVOD_RTSP_EVENT_DESCRIBE, p1, p2);
                }
                else
                {
                    /*���ӷ�����ʧ��*/
                    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_OPEN, -996);
                }
            }
            break;
        case IVOD_RTSP_ACK_DESCRIBE:
            ret = ivod_rtsp_telecom_response_describe(me, session);
            break;  
        case IVOD_RTSP_ACK_SETUP:
            ret = ivod_rtsp_telecom_response_setup(me, session);
            break;
        case IVOD_RTSP_ACK_PLAY:
            ret = ivod_rtsp_telecom_response_play(me, session);
            break;
        case IVOD_RTSP_ACK_PAUSE:
            {
                if(IPLAYER_OP_PAUSE == me->current_op)
                    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_PAUSE, 0);
            }
            break;
        case IVOD_RTSP_ACK_OPTION:
            break;
        case IVOD_RTSP_ACK_PING:
            break;
        case IVOD_RTSP_ACK_SET_PARAM:
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

                            /*֪ͨ����ʱ��*/
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
                            /*֪ͨ����ʱ��*/
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
            {
                char *p_notice = NULL;
                int notice = 0;
                
                p_notice = ivod_rtsp_session_get_field(session, "x-notice", ':');
    			if( !p_notice )
    			{
                    return IPANEL_ERR;
                }

    			notice = atoi(p_notice);

    			if ( notice == 2102 ) 
                {
                    //����5209��ҳ�棬ҳ�����ô�ͷ��ʼ����
    				me->flag_first_play = 1;
    			} 

                INFO("%s[ivod_rtsp_telecom_response] IVOD_RTSP_ACK_ANNOUNCE_RESPONSE notice=%d\n", TELECOM_MARK,notice); 

                //֪ͨqos announce����
                implayer_qos_proc("proc",VOD_QOS_EVENT_ERROR,notice,0);
               
                //֪ͨ�ϲ�announce��Ϣ
                me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_ANNOUNCE, notice);
            }
            break;
        case IVOD_RTSP_ACK_ERROR_RESPONSE:
            {
                if(302 == p1)
                {
 				    rtsp_request_func_t request[1] = {0};
                    char temp_url[2048] = {0};
    				char *location = ivod_rtsp_session_get_field(session, "Location", ':');
                    /*���302�ᵼ���м����qos���㲥������-,�°汾�ܱ�֤һ�ε㲥��һ��open���þͿ�����,����Ҫ�ٷ�302��ȥ*/
                #if 0
                    /*֪ͨqos 302��Ϣ*/
					implayer_qos_proc("proc", VOD_QOS_EVENT_ERROR, p1, p2);
                #endif
    				if (location) 
                    {
                        strcpy(temp_url,location);
                        
                        /*�ر�ԭ�е�seesion*/
                        if(me->rtsp_session)
                        {
                            ivod_rtsp_session_close(me->rtsp_session);
                            me->rtsp_session = NULL;
                        } 

                        /*�����µ�seesion*/
                        request->rtsp_describe      = ivod_rtsp_telecom_describe;
                        request->rtsp_setup         = ivod_rtsp_telecom_setup;
                        request->rtsp_play          = ivod_rtsp_telecom_play;
                        request->rtsp_pause         = ivod_rtsp_telecom_pause;
                        request->rtsp_close         = ivod_rtsp_telecom_teardown;
                        request->rtsp_get_parameter = ivod_rtsp_telecom_get_parameter;
                        request->rtsp_set_parameter = ivod_rtsp_telecom_set_parameter;
                        request->rtsp_options       = ivod_rtsp_telecom_option;	
                        request->rtsp_validate      = ivod_rtsp_telecom_validate;
                        request->rtsp_ping          = ivod_rtsp_telecom_ping;
                        request->rtsp_response      = ivod_rtsp_telecom_response;  

                        me->rtsp_session = ivod_rtsp_session_open(me->client_main, me, request);
                        if(NULL == me->rtsp_session)
                        {
                            INFO("%s[ivod_rtsp_telecom_response] rtsp_session_register failed\n", TELECOM_MARK);
                            me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_ERROR, p1);
                        }
                        else
                        {
                            ivod_rtsp_telecom_proc((unsigned int)me,IVOD_RTSP_EVENT_OPEN,(int)temp_url,me->type);
                        }
                        
    				}
                    else
                    {
                        me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_ERROR, p1);
                    }
                  
                }
                else if(p1 == -995 && (!(me->flag_tstv_to_livetv || me->flag_livetv_to_tstv)))
                {
                    /*����rtsp����ʧ��*/
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
                /*�رջỰ*/
                if(me->rtsp_session)
                {
                    ivod_rtsp_session_close(me->rtsp_session);
                    me->rtsp_session = NULL;
                }

                /*�ر�socket*/
                if(me->sock_id > 0)
                {
                    /*������鲥�����ﻹ��Ҫ���˳��鲥*/
                    if(me->flag_multicast_stream)
                    {
                        if(isocket_leave_multicast(me->sock_id, (unsigned int)&me->data_serverIP) < 0)
                        {
                            INFO("%s[ivod_rtsp_telecom_response] leave multicast failed\n", TELECOM_MARK);                   
                        } 
                        me->flag_multicast_stream = 0;

                        /*sleep 50ms ��֤�˳��鲥�������Ѿ�����ȥ��*/
                        usleep(50000);                       
                    }
                    
                    isocket_close(me->sock_id);
                    me->sock_id = 0;
                }  

                if(me->NAT_sockId)
                {
                    isocket_close(me->NAT_sockId);
                    me->NAT_sockId = 0;
                }                

                if(!(me->flag_tstv_to_livetv || me->flag_livetv_to_tstv))
                {                    
                    /*�ر�player*/
                    if(me->player_id)
                    {
                        if(iplayer_close(me->player_id) < 0)
                        {
                            INFO("%s[ivod_rtsp_telecom_response] close player failed\n", TELECOM_MARK); 
                            me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_PLAYER_CLOSE_FAILED, 0);                        
                        }
                        else
                        {
                            INFO("%s[ivod_rtsp_telecom_response] close player success\n", TELECOM_MARK); 
                            me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_MSG, IVOD_RTSP_MSG_PLAYER_CLOSE_SUCCESS, 0);
                        }

                        me->player_id = 0;
                    } 
                                                   
                    /*������Ӧ�ɹ�*/
                    me->func(me->rtsp_client, IVOD_RTSP_CALLBACK_TYPE_RESULT, IVOD_RTSP_EVENT_CLOSE, IPANEL_OK);
                }
            }
            break;
    }
    
    return ret;
}     

static int ivod_rtsp_telecom_callback_session_get(void *handle, unsigned int op, void *value)
{

    return IPANEL_ERR;    
}

static int ivod_rtsp_telecom_callback_session_message(void *handle, unsigned int msg, int value)
{
    return IPANEL_ERR;
}

unsigned int ivod_rtsp_telecom_open(client_t *client_main, ivod_rtsp_client_t *rtsp_client, int mode, ivod_msg_func func)
{
    rtsp_request_func_t request[1] = {0};
    ivod_rtsp_telecom_t *me = NULL;
    
    FAILED_RETURNX(NULL == client_main 
                   || NULL == rtsp_client
                   || NULL == func, (unsigned int)NULL);
    
    me = (ivod_rtsp_telecom_t *)calloc(1, sizeof(ivod_rtsp_telecom_t));
    if(NULL == me)
    {
        INFO("%s[ivod_rtsp_telecom_open] calloc failed\n", TELECOM_MARK);
        goto FAILED;
    }

    memset(me,0,sizeof(ivod_rtsp_telecom_t));
    me->client_main = client_main;
    me->rtsp_client = rtsp_client;    
    if(mode == IVOD_MODE_NONE)
        me->mode = IVOD_MODE_IP;
    else    
        me->mode = mode;

    me->func = func;

    request->rtsp_describe      = ivod_rtsp_telecom_describe;
    request->rtsp_setup         = ivod_rtsp_telecom_setup;
    request->rtsp_play          = ivod_rtsp_telecom_play;
    request->rtsp_pause         = ivod_rtsp_telecom_pause;
    request->rtsp_close         = ivod_rtsp_telecom_teardown;
    request->rtsp_get_parameter = ivod_rtsp_telecom_get_parameter;
    request->rtsp_set_parameter = ivod_rtsp_telecom_set_parameter;
    request->rtsp_options       = ivod_rtsp_telecom_option;	
    request->rtsp_validate      = ivod_rtsp_telecom_validate;
    request->rtsp_ping          = ivod_rtsp_telecom_ping;
    request->rtsp_response      = ivod_rtsp_telecom_response;  

    /*ע��һ��session*/
    me->rtsp_session = ivod_rtsp_session_open(me->client_main, me, request);
    if(NULL == me->rtsp_session)
    {
        INFO("%s[ivod_rtsp_telecom_open] rtsp_session_register failed\n", TELECOM_MARK);
        goto FAILED;
    }
    
    INFO("%s[ivod_rtsp_telecom_open] rtsp_telecom_open success\n", TELECOM_MARK);
    return (unsigned int)me;
    
FAILED:
    if(me)
    {
        if(me->rtsp_session)
        {
            ivod_rtsp_session_close(me->rtsp_session);
            me->rtsp_session = NULL;
        }
        memset(me,0,sizeof(ivod_rtsp_telecom_t));
        free(me);
        me = NULL;
    }
    
    return (unsigned int)NULL;
}

int ivod_rtsp_telecom_close(unsigned int handle)
{
    ivod_rtsp_telecom_t *me = (ivod_rtsp_telecom_t*)handle;

    /*�رջỰ*/
    if(me->rtsp_session)
    {
        ivod_rtsp_session_close(me->rtsp_session);
        me->rtsp_session = NULL;
    }

    /*�ر�socket*/
    if(me->sock_id > 0)
    {
        /*������鲥�����ﻹ��Ҫ���˳��鲥*/
        if(me->flag_multicast_stream)
        {
            if(isocket_leave_multicast(me->sock_id, (unsigned int)&me->data_serverIP) < 0)
            {
                INFO("%s[ivod_rtsp_telecom_close] leave multicast failed\n", TELECOM_MARK);                   
            } 
            me->flag_multicast_stream = 0;

            /*sleep 50ms ��֤�˳��鲥�������Ѿ�����ȥ��*/
            usleep(50000);
            
        }
                
        isocket_close(me->sock_id);
        me->sock_id = 0;
    }  

    if(me->NAT_sockId)
    {
        isocket_close(me->NAT_sockId);
        me->NAT_sockId = 0;
    }

    if(me)
    {    
        memset(me,0,sizeof(ivod_rtsp_telecom_t));
        free(me);
        me = NULL;
    }
    
    INFO("%s[ivod_rtsp_telecom_close] close success\n", TELECOM_MARK); 

    return IPANEL_OK;
}

int ivod_rtsp_telecom_proc(unsigned int handle, unsigned int event, int p1, int p2)
{
    int ret = IPANEL_ERR;
    ivod_rtsp_session_t *session = NULL;
    ivod_rtsp_telecom_t *me = (ivod_rtsp_telecom_t *)handle;
    
    FAILED_RETURNX(NULL == me || NULL == me->rtsp_session, IPANEL_ERR);

    session = me->rtsp_session;
    while(session)
    {
        /*����NAT����*/
        if(me->NAT->flag_used 
            && me->NAT->heart_start_time > 0
            && ((unsigned int)time_ms())-me->NAT->heart_start_time > me->NAT->heart_time)
        {
            isocket_sendto((unsigned int)me->sock_id, me->NAT->data, sizeof(me->NAT->data), 0, (unsigned int)me->data_serverIP, me->data_serverPort);
            if(me->NAT_sockId)
                isocket_sendto((unsigned int)me->NAT_sockId, me->NAT->data, sizeof(me->NAT->data), 0, (unsigned int)me->data_serverIP, (me->data_serverPort+1));
            me->NAT->heart_start_time = (unsigned int)time_ms();
        }
       
        /*��������*/
        if(me->heart_start_time > 0 && ((int)time_ms())-me->heart_start_time > me->heart_time)
        {
            ivod_rtsp_session_proc(session, me->heart_event, 0, 0);
            me->heart_start_time = (int)time_ms();
        }
        
        ret = ivod_rtsp_session_proc(session, event, p1, p2);
        if(ret == IPANEL_OK)/*ִ�гɹ�,�˳�*/
        {
            /*����Ǵ򿪲��ţ��ͱ��沥�ŵ�ַ����������*/
            if(IVOD_RTSP_EVENT_OPEN == event)
            {
                if(p1)
                {
                    memset(me->url,0,sizeof(me->url));
                    strcpy(me->url,(char*)p1);
                }

                me->type = p2;
                INFO("%s open type=%d,url=\n%s\n", TELECOM_MARK,p2,me->url);
            }
            break;
        }
        else
            session = ivod_rtsp_session_get_next(session);/*�е�VOD������session,����NGOD*/
    }
    
    return ret;
}

int ivod_rtsp_telecom_set_prop(unsigned int handle, int prop, int value)
{
    return IPANEL_OK;
}

int ivod_rtsp_telecom_get_prop(unsigned int handle, int prop, void *value)
{
    return IPANEL_OK;
}


/*����NAT��Ϣ*/
static int ivod_rtsp_telecom_NAT_data(ivod_rtsp_telecom_t *me)
{
    /*
        84�ֽڹ���:
        "ZXV10STB":8
        sessid :4
        localip:4
        pri_port:2
        rtsp_port:2
        0000:64
    */
    
    char zte_stun_head[] = "ZXV10STB";
    unsigned int sessid = 0;
    unsigned int local_ip = 0;
    unsigned short data_port = htons((unsigned short)me->data_clientPort);
	char buffer[84] = {0};
    unsigned short rtsp_port = htons((unsigned short)me->rtsp_clientPort);
    IPAddr0 ipaddr[1] = {0};
   
    INFO("%s[ivod_rtsp_telecom_NAT_data] construct data start!\n", TELECOM_MARK);
    
    sessid = htonl(me->sessionId);

    ivod_common_ipv4_str_to_int(me->localIP,ipaddr);
    local_ip = htonl(ipaddr->addr.ipv4);
    
    INFO("%s[ivod_rtsp_telecom_NAT_data]sessid = %u, local_ip = 0x%x, data_port = %d, local_rtsp_port = %d\n",
                TELECOM_MARK,sessid, local_ip, data_port, me->rtsp_clientPort);
    memcpy(buffer, zte_stun_head, 8);
    memcpy(&buffer[8], &sessid, 4);
    memcpy(&buffer[12], &local_ip, 4);
    memcpy(&buffer[16], &data_port, 2);
    memcpy(&buffer[18], &rtsp_port, 2);

    memset(me->NAT->data,0,84);
    memcpy(me->NAT->data,buffer,84);

	return 84;    
}



