/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:RTSP PROTOCOL实现
*********************************************************************/
#include "iplayer_socket.h"
#include "iplayer_player.h"
#include "iplayer_vod_main.h"
#include "ivod_rtsp_session.h"
#include "ivod_rtsp_protocol.h"
#include "ivod_rtsp_client.h"

/************************************************************************************************
 *宏定义
 ************************************************************************************************/

#define PTL_MARK   "[IVOD][PROTOCOL]"
#define PROTOCOL_MAX_BUFFER_LEN    (4*1024)        //RTSP模块的数据缓冲区大小（包括接收与发送)
#define PROTOCOL_RTSP_HEADLEN      (4)             //接收RTSP包的头部长度
#define PROTOCOL_MAX_RECVPKT_LEN   (2*1024)        //最大可接收的数据包
#define PROTOCOL_MAX_SENDPKT_LEN   (PROTOCOL_MAX_BUFFER_LEN - PROTOCOL_MAX_RECVPKT_LEN) //最大可发送的数据包

#define PROTOCOL_MAX_FIELDS 256
#define PROTOCOL_MAX_COMMANDS 64

#define PROTOCOL_RTSP_STATUS_UNKNOWN        999
#define PROTOCOL_RTSP_STATUS_SET_PARAMETER  10
#define PROTOCOL_RTSP_STATUS_ANNOUNCE	    11
#define PROTOCOL_RTSP_STATUS_OPTIONS   	    12
#define PROTOCOL_RTSP_STATUS_REDIRECT	    13

#define PROTOCOL_RTSP_STATUS_OK             200
#define PROTOCOL_RTSP_STATUS_MOVED          301
#define PROTOCOL_RTSP_MOVED_TEMPORARILY     302


#define PROTOCOL_ZERO(a, b) memset((a), 0, (b))
#define PROTOCOL_BUFFER_LEN    4084


/************************************************************************************************
 *结构体定义
 ************************************************************************************************/

/*RTSP应答缓存区相关操作*/
typedef enum
{
    IVOD_REPLY_BUFFER_INIT = 256,
    IVOD_REPLY_BUFFER_ADD_DATA,
    IVOD_REPLY_BUFFER_GET_DATA    
}ivod_reply_buffer_op_e;


/* RTSP交互命令名称*/
typedef enum
{
	IVOD_RTSP_COMMAND_IDLE = 0,

 	IVOD_RTSP_COMMAND_DESCRIBE,
	IVOD_RTSP_COMMAND_SETUP = 2,
	IVOD_RTSP_COMMAND_PLAY,
	IVOD_RTSP_COMMAND_PAUSE = 4,
	IVOD_RTSP_COMMAND_CLOSE,
	IVOD_RTSP_COMMAND_OPTION = 6,
	IVOD_RTSP_COMMAND_PING,
	IVOD_RTSP_COMMAND_SET_PARAM = 8,                    	
	IVOD_RTSP_COMMAND_GET_PARAM,
	IVOD_RTSP_COMMAND_VALIDATE = 10,
	IVOD_RTSP_COMMAND_ANNOUNCE,
	IVOD_RTSP_COMMAND_OTHER_STATE,   
	IVOD_RTSP_COMMAND_UNKNOWN
}ivod_rtsp_command_e;


/* 交互命令存储buffer */
typedef struct
{
    char *data;         //发送命令和接收消息的缓冲区,也是发送命令的起始地址
    int lastSendLen;             //剩余未发送命令数据长度
    int sendLen;                 //已发送命令数据长度
    char *reply;        //指向data中应答起始地址,固定不变
    char *reply_offSet; //应答处理的偏移地址
    int recvLen;                 //接收的交互命令应答数据长度
} ivod_command_buffer_t;

typedef struct ivod_rtsp_command_s 
{
    int  port;                           //服务器端口
    unsigned int current_op;             //当前交互命令
    unsigned int cseq;                   //发送命令的序列号
    unsigned int answer_seq;             //应答命令的序列号
    char *session;                       //和服务器的会话id
    char *answers[PROTOCOL_MAX_FIELDS];             //接收的应答按行存储队列
    char *request[PROTOCOL_MAX_FIELDS];             //待发送的命令内容队列
    ivod_rtsp_command_e opList[PROTOCOL_MAX_COMMANDS];   //发送的交互命令队列
    unsigned int cseqList[PROTOCOL_MAX_COMMANDS];        //发送的交互命令序号队列
    unsigned int  opTimeList[PROTOCOL_MAX_COMMANDS];     //交互命令发送时间队列
    ivod_command_buffer_t cmd_buffer[1];
}ivod_rtsp_command_t;

struct ivod_rtsp_protocol_s
{
    ivod_msg_func rtsp_callback;                     //回调函数
    client_t *client_main;                           //client句柄
    ivod_rtsp_session_t *rtsp_session;               //session句柄
    ivod_rtsp_command_t *rtsp_command;               //rtsp交互命令信息，包括发送的命令及接收的应答
    
	char response_buffer[PROTOCOL_BUFFER_LEN*3];     //rtsp应答临时缓存区，可以存很多个应答
	char data_buffer[PROTOCOL_BUFFER_LEN];           //备份已经解析的rtsp应答
	int response_len;                                //rtsp应答临时缓冲区数据量
    int sock_id;                                     //发送和接收交互命令的socket
};


/************************************************************************************************
 *函数声明
 ************************************************************************************************/

static int 
ivod_rtsp_protocol_send_data(ivod_rtsp_protocol_t *me);

static void 
ivod_rtsp_protocol_clear_answers(ivod_rtsp_protocol_t *me);

static void 
ivod_rtsp_protocol_clear_request(ivod_rtsp_protocol_t *me);

static int 
ivod_rtsp_protocol_parse_reply( ivod_rtsp_protocol_t *me );

static void 
ivod_rtsp_protocol_printf_command(ivod_rtsp_command_e op);

static char*
ivod_rtsp_protocol_get_line_data( ivod_rtsp_protocol_t *me );

static int 
ivod_rtsp_protocol_get_answers( ivod_rtsp_protocol_t *me );

static int 
ivod_rtsp_protocol_get_reply_code( ivod_rtsp_protocol_t *me, const char *str );

static int 
ivod_rtsp_protocol_get_rtsp_reply_length(char *buffer, int len);

static ivod_rtsp_command_e 
ivod_rtsp_protocol_get_rtsp_command( ivod_rtsp_protocol_t *me, unsigned int cseq );

static int 
ivod_rtsp_protocol_save_rtsp_command( ivod_rtsp_protocol_t *me, ivod_rtsp_command_e op, unsigned int cseq );

static int 
ivod_rtsp_protocol_send_request( ivod_rtsp_protocol_t *me, const char *type, const char *url);

static int 
ivod_rtsp_protocol_return_rtsp_reply(ivod_rtsp_protocol_t *me, int msg, int wparam, int lparam );

static int 
ivod_rtsp_protocol_reply_buffer_operate(ivod_rtsp_protocol_t *me, int msg, int p1, int p2);

static int 
ivod_rtsp_protocol_construct_message(ivod_rtsp_protocol_t *me, const char *sendData, int needSendDataLen);


const static char ivod_rtsp_version[] = "RTSP/1.0\r\n";

/************************************************************************************************
 *函数定义
 ************************************************************************************************/

/*打开*/
ivod_rtsp_protocol_t *ivod_rtsp_protocol_open(void *client_main_handle, void *rtsp_session_handle, ivod_msg_func cbf)
{
    ivod_rtsp_protocol_t *me = (ivod_rtsp_protocol_t *)calloc(1, sizeof(ivod_rtsp_protocol_t));
    if(NULL == me)
    {
        INFO("%s[ivod_rtsp_protocol_open] calloc failed\n",PTL_MARK);
        goto FAILED;
    }
    
    PROTOCOL_ZERO(me, sizeof(ivod_rtsp_protocol_t));
    
    me->rtsp_command = (ivod_rtsp_command_t *)calloc(1, sizeof(ivod_rtsp_command_t));
    if(NULL == me->rtsp_command)
    {
        INFO("%s[ivod_rtsp_protocol_open] calloc private failed\n",PTL_MARK);
        goto FAILED;
    }
    me->client_main = (client_t *)client_main_handle;
    me->rtsp_session = (ivod_rtsp_session_t *)rtsp_session_handle;
	me->rtsp_callback = cbf;
    
    return me;
    
FAILED:
    if(me)
    {
        if(me->rtsp_command)
        {
            free(me->rtsp_command);
            me->rtsp_command = NULL;
        }
        free(me);
    }
    return NULL;
}

/*关闭*/
int ivod_rtsp_protocol_close(ivod_rtsp_protocol_t *me)
{
    if(me)
    {
	    if(me->rtsp_command)
    	{
    		if( me->sock_id > 0)
    		{
                ivod_rtsp_protocol_disconnect(me);
    		}
            
    		if( me->rtsp_command->session )
            {
    			free( me->rtsp_command->session );
    			me->rtsp_command->session = NULL;
    		}
            
    		if( me->rtsp_command->cmd_buffer->data)
            {
    			free(me->rtsp_command->cmd_buffer->data);
    			me->rtsp_command->cmd_buffer->data = NULL;
    		}
                       
    		ivod_rtsp_protocol_clear_answers( me );
    		ivod_rtsp_protocol_clear_request( me );
    		free( me->rtsp_command );
    		me->rtsp_command = NULL;
    	}        
	    free(me);
    }
	return IPANEL_OK;
}

/*proc*/
int ivod_rtsp_protocol_proc(ivod_rtsp_protocol_t *me, unsigned int msg, int p1, int p2)
{
    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    if(msg == IVOD_RTSP_EVENT_TIMER)
    {
        /*TIMER判断response buffer是否有数据可以解析*/
        if(strlen(me->response_buffer) > 0 && me->response_len > 0 )
        {
            ivod_rtsp_protocol_parse_reply(me);
        }
    }
    else if(msg == IVOD_RTSP_EVENT_SOCKET_DATA)
    {
        /*添加到response buffer里面*/
        ivod_rtsp_protocol_reply_buffer_operate(me, IVOD_REPLY_BUFFER_ADD_DATA, (int)p1, p2);
    }
    
    return IPANEL_OK;
}

/*connect*/
int ivod_rtsp_protocol_connect(ivod_rtsp_protocol_t *me, char *url)
{
    ivod_rtsp_command_t *cmd = NULL;
    unsigned int port = 0;
    int sockID = 0, i = 0;
    IPAddr0 ip[1] = {0};
    
    FAILED_RETURNX(NULL == me || NULL == url, IPANEL_ERR);

    cmd = me->rtsp_command;
    
    FAILED_RETURNX(NULL == cmd, IPANEL_ERR);
    
	cmd->cmd_buffer->data = (char*)calloc(1, PROTOCOL_MAX_BUFFER_LEN);
	if(NULL == cmd->cmd_buffer->data)
	{
		free(me->rtsp_command);
		me->rtsp_command = NULL;
        return IPANEL_ERR;
	}
    
	// 将接收与发送的消息缓冲区清零
	PROTOCOL_ZERO(cmd->cmd_buffer->data, PROTOCOL_MAX_BUFFER_LEN);
    
	cmd->cmd_buffer->reply = cmd->cmd_buffer->data + PROTOCOL_MAX_SENDPKT_LEN;
    // 应答消息的偏移地址复位
	cmd->cmd_buffer->reply_offSet = cmd->cmd_buffer->reply; 
	cmd->cmd_buffer->recvLen = 0;
	cmd->cmd_buffer->sendLen = 0;
    cmd->cmd_buffer->lastSendLen = 0;

    //解析ip地址及端口
    INFO("%s[ivod_rtsp_protocol_connect] start parse url\n",PTL_MARK);   
    if(IPANEL_ERR == ivod_common_parse_url(url, (unsigned int)ip, &port))
    {
        INFO("%s[ivod_rtsp_protocol_connect] parse url error!!\n",PTL_MARK); 
        return IPANEL_ERR;
    }
       
    for ( i=0; i<PROTOCOL_MAX_FIELDS; i++ )
    {
        cmd->answers[i]=NULL;
        cmd->request[i]=NULL;
    }
    
    cmd->port = port;
    cmd->cseq = 0;
    cmd->session = NULL;
	cmd->current_op = IVOD_RTSP_COMMAND_IDLE;


    ivod_rtsp_protocol_reply_buffer_operate(me, IVOD_REPLY_BUFFER_INIT, 0, 0);
    sockID = isocket_open(ISOCKET_TYPE_TCP, ip->version, me->client_main);
    if(sockID <= 0)
    {
        INFO("%s[ivod_rtsp_protocol_connect] socket open failed\n",PTL_MARK);
        return IPANEL_ERR;
    }
    isocket_connect((unsigned int)sockID, (unsigned int)ip, (int)port);
    me->sock_id = sockID;

    return sockID;
}

/*disconnect*/
int ivod_rtsp_protocol_disconnect(ivod_rtsp_protocol_t *me)
{
    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    if(me->sock_id)
    {
        isocket_close(me->sock_id);
        me->sock_id = 0;
    }
    
    return IPANEL_OK;
}

/*describe*/
int ivod_rtsp_protocol_describe(ivod_rtsp_protocol_t *me, const char *what )
{
    ivod_rtsp_protocol_send_request( me, "DESCRIBE", what );

	me->rtsp_command->current_op = IVOD_RTSP_COMMAND_DESCRIBE;
	ivod_rtsp_protocol_save_rtsp_command(me,(ivod_rtsp_command_e)me->rtsp_command->current_op,me->rtsp_command->cseq);
	return IPANEL_OK;
}

/*options*/
int ivod_rtsp_protocol_options(ivod_rtsp_protocol_t *me, const char *what )
{
    ivod_rtsp_protocol_send_request( me, "OPTIONS", what );

	me->rtsp_command->current_op = IVOD_RTSP_COMMAND_OPTION;
	ivod_rtsp_protocol_save_rtsp_command(me,(ivod_rtsp_command_e)me->rtsp_command->current_op,me->rtsp_command->cseq);
	return IPANEL_OK;
}

/*setup*/
int ivod_rtsp_protocol_setup(ivod_rtsp_protocol_t *me, const char *what )
{
    ivod_rtsp_protocol_send_request( me, "SETUP", what );

	me->rtsp_command->current_op = IVOD_RTSP_COMMAND_SETUP;
	ivod_rtsp_protocol_save_rtsp_command(me,(ivod_rtsp_command_e)me->rtsp_command->current_op,me->rtsp_command->cseq);
	return IPANEL_OK;
}

/*setparameter*/
int ivod_rtsp_protocol_setparameter(ivod_rtsp_protocol_t *me, const char *what )
{
    ivod_rtsp_protocol_send_request( me, "SET_PARAMETER", what );

	me->rtsp_command->current_op = IVOD_RTSP_COMMAND_SET_PARAM;
	ivod_rtsp_protocol_save_rtsp_command(me,(ivod_rtsp_command_e)me->rtsp_command->current_op,me->rtsp_command->cseq);
	return IPANEL_OK;
}

/*getparameter*/
int ivod_rtsp_protocol_getparameter(ivod_rtsp_protocol_t *me, const char *what )
{
    ivod_rtsp_protocol_send_request( me, "GET_PARAMETER", what );

	me->rtsp_command->current_op = IVOD_RTSP_COMMAND_GET_PARAM;
	ivod_rtsp_protocol_save_rtsp_command(me,(ivod_rtsp_command_e)me->rtsp_command->current_op,me->rtsp_command->cseq);
	return IPANEL_OK;
}

/*validate*/
int ivod_rtsp_protocol_validate(ivod_rtsp_protocol_t *me, const char *what )
{	
    ivod_rtsp_protocol_send_request( me, "VALIDATE", what );

	me->rtsp_command->current_op = IVOD_RTSP_COMMAND_VALIDATE;
	ivod_rtsp_protocol_save_rtsp_command(me,(ivod_rtsp_command_e)me->rtsp_command->current_op,me->rtsp_command->cseq);	
	return IPANEL_OK;
}

/*play*/
int ivod_rtsp_protocol_play(ivod_rtsp_protocol_t *me, const char *what )
{
    ivod_rtsp_protocol_send_request( me, "PLAY", what );

	me->rtsp_command->current_op = IVOD_RTSP_COMMAND_PLAY;
	ivod_rtsp_protocol_save_rtsp_command(me,(ivod_rtsp_command_e)me->rtsp_command->current_op,me->rtsp_command->cseq);
	return IPANEL_OK;
}

/*pause*/
int ivod_rtsp_protocol_pause(ivod_rtsp_protocol_t *me, const char *what )
{
    ivod_rtsp_protocol_send_request( me, "PAUSE", what );

	me->rtsp_command->current_op = IVOD_RTSP_COMMAND_PAUSE;
	ivod_rtsp_protocol_save_rtsp_command(me,(ivod_rtsp_command_e)me->rtsp_command->current_op,me->rtsp_command->cseq);
	return IPANEL_OK;
}

/*teardown*/
int ivod_rtsp_protocol_teardown(ivod_rtsp_protocol_t *me, const char *what )
{
    ivod_rtsp_protocol_send_request( me, "TEARDOWN", what );
	me->rtsp_command->current_op = IVOD_RTSP_COMMAND_CLOSE;
	ivod_rtsp_protocol_save_rtsp_command(me,(ivod_rtsp_command_e)me->rtsp_command->current_op,me->rtsp_command->cseq);
	return IPANEL_OK;
}

/*ping*/
int ivod_rtsp_protocol_ping(ivod_rtsp_protocol_t *me, const char *what )
{	
    ivod_rtsp_protocol_send_request( me, "PING", what );

	me->rtsp_command->current_op = IVOD_RTSP_COMMAND_PING;
	ivod_rtsp_protocol_save_rtsp_command(me,(ivod_rtsp_command_e)me->rtsp_command->current_op,me->rtsp_command->cseq);
	return IPANEL_OK;
}

/*SEND OK*/
int ivod_rtsp_protocol_send_ok( ivod_rtsp_protocol_t *me, int cseq, char *session, char *ondemand_session)
{
	char field[512] = { 0 };
    int str_len = 0;

    str_len = strlen("RTSP/1.0 200 OK\r\n");
    ivod_rtsp_protocol_construct_message(me, "RTSP/1.0 200 OK\r\n", str_len);
    
    sprintf( field, "CSeq: %u\r\n", cseq );
    str_len = strlen(field);
    ivod_rtsp_protocol_construct_message(me, field, str_len);

    PROTOCOL_ZERO(field, sizeof(field));
	sprintf(field,"Session: %s\r\n",session);    
    str_len = strlen(field);
    ivod_rtsp_protocol_construct_message(me, field, str_len);

    if( ondemand_session && strlen(ondemand_session) > 0 ) 
    {
        PROTOCOL_ZERO(field, sizeof(field));
    	sprintf(field,"OnDemandSessionId: %s\r\n",ondemand_session);                
        str_len = strlen(field);
        ivod_rtsp_protocol_construct_message(me, field, str_len);
    }
        
    str_len = strlen("\r\n");
    ivod_rtsp_protocol_construct_message(me, "\r\n", str_len);

	ivod_rtsp_protocol_send_data(me);

    return IPANEL_OK;
}

/*get field*/
char *ivod_rtsp_protocol_get_field(ivod_rtsp_protocol_t *me, char *field, char flag)
{
    char **answer = NULL;
    char *ptr = NULL;
    
	FAILED_RETURNX(NULL == me || NULL == me->rtsp_command || NULL == field, NULL);

    answer = me->rtsp_command->answers;

    while(*answer)
    {
        if(!strncasecmp( *answer, field, strlen(field)))
        {
            ptr = strchr(*answer, flag);
            if(ptr)
            {
                ptr++;
                while( *ptr == ' ' ) ptr++;
                return ptr;
            }
        }
        answer++;
    }

    return ptr;   
}

/*获取field所在字段的地址指针*/
char **ivod_rtsp_protocol_get_field_address(ivod_rtsp_protocol_t *me, char *field)
{
    char **answer = NULL;
    
	FAILED_RETURNX(NULL == me || NULL == me->rtsp_command || NULL == field, NULL);

    answer = me->rtsp_command->answers;

    while(*answer)
    {
        if(!strncasecmp( *answer, field, strlen(field)))
        {
            return answer;    
        }
        answer++;
    }

    return answer;    
}


/*将发送的命令内容放入命令队列中*/
int ivod_rtsp_protocol_add_field(ivod_rtsp_protocol_t *me, char *field)
{
    int i = 0;
    FAILED_RETURNX(NULL == me || NULL == me->rtsp_command ||NULL == field, IPANEL_ERR);
    
    while(me->rtsp_command->request[i]) i++;

    me->rtsp_command->request[i] = strdup(field);

    return IPANEL_OK; 
}


/*add seq*/
int ivod_rtsp_protocol_add_cseq(ivod_rtsp_protocol_t *me)
{
	char buffer[512] = { 0 };

	FAILED_RETURNX(NULL == me || NULL == me->rtsp_command, IPANEL_ERR);

	me->rtsp_command->cseq++;
	sprintf(buffer, "CSeq: %u\r\n", me->rtsp_command->cseq);
	ivod_rtsp_protocol_add_field(me, buffer);	

	return me->rtsp_command->cseq;
}


/*set session id*/
int ivod_rtsp_protocol_set_session(ivod_rtsp_protocol_t *me, char *session_id)
{
	FAILED_RETURNX(NULL == me, IPANEL_ERR);

    if(me->rtsp_command->session) 
        free(me->rtsp_command->session);
    me->rtsp_command->session = strdup(session_id);
    
    return IPANEL_OK;
}


/*add session*/
int ivod_rtsp_protocol_add_session(ivod_rtsp_protocol_t *me)
{
	char buffer[512] = { 0 };

	FAILED_RETURNX(NULL == me || NULL == me->rtsp_command, IPANEL_ERR);
    
    if(me->rtsp_command->session)
    {
        sprintf(buffer, "Session: %s\r\n", me->rtsp_command->session);
    	ivod_rtsp_protocol_add_field(me, buffer);	
        return IPANEL_OK;
    }
    else
        return IPANEL_ERR;
}



/*构造消息并发送*/
static int ivod_rtsp_protocol_send_request( ivod_rtsp_protocol_t *me, const char *type, const char *url)
{
    char **payload = me->rtsp_command->request;
    char *buffer=NULL;
    int ret = 0,str_len = 0;
    unsigned int cseqNum = 0;

    buffer = (char*)calloc(sizeof(char), strlen(type)+strlen(url)+sizeof(ivod_rtsp_version) + 2 );
    if(NULL == buffer)
        return IPANEL_ERR;
    
    sprintf( buffer, "%s %s %s", type, url, ivod_rtsp_version );
    str_len = strlen(buffer);
    ivod_rtsp_protocol_construct_message(me, buffer, str_len);
    free( buffer );
	buffer=NULL;

    if(payload)
    {
        while( *payload )
        {
            //"CSeq"
            if(strlen(*payload) > 4 && 0==memcmp(*payload, "CSeq", 4))
            {
                char *tmpPtr = NULL;
                tmpPtr = (*payload)+4;
                while(*tmpPtr)
                {
                    if(*tmpPtr >= '0' && *tmpPtr <= '9')
                        break;

                    tmpPtr++;
                }

                while(*tmpPtr >= '0' && *tmpPtr <= '9')
                {
                    cseqNum = cseqNum*10 + (*tmpPtr) - '0';
                    tmpPtr++;
                }
            }
            
            if(*(payload+1) == NULL )
            {
                if(cseqNum == 0)
                {
                    char tmdata[64] = {0};
                    me->rtsp_command->cseq++;
                    sprintf(tmdata, "CSeq: %u\r\n", me->rtsp_command->cseq);
                    str_len = strlen(tmdata);
                    ivod_rtsp_protocol_construct_message(me, tmdata, str_len);
                }
                else
                    me->rtsp_command->cseq = cseqNum;
            }

            str_len = strlen(*payload);
            ivod_rtsp_protocol_construct_message(me, *payload, str_len);
            
            payload++;
        }
    }

    //将暂存发送的命令内容队列清空
	ivod_rtsp_protocol_clear_request( me );

	ret = ivod_rtsp_protocol_send_data(me);
	INFO("%s[ivod_rtsp_protocol_send_request] send length = %d\n", PTL_MARK, ret);
	return ret;
}




/*组装待发送的消息*/
static int ivod_rtsp_protocol_construct_message(ivod_rtsp_protocol_t *me, const char *sendData, int needSendDataLen)
{
	ivod_command_buffer_t *cmd_buffer = me->rtsp_command->cmd_buffer;
	char *sendbuffer = cmd_buffer->data;
	int len = cmd_buffer->lastSendLen;

	if(len + needSendDataLen > PROTOCOL_MAX_SENDPKT_LEN -1)
    {
		INFO("%s[construct_message] failed construct_message\n",PTL_MARK);
		return IPANEL_ERR;
	}
	memcpy((sendbuffer+len), sendData, needSendDataLen);
	len += needSendDataLen;
	*(sendbuffer+len) = '\0';
	cmd_buffer->lastSendLen = len;
	return needSendDataLen;
}




/*通过socket将交互命令发到服务器*/
static int ivod_rtsp_protocol_send_data(ivod_rtsp_protocol_t *me)
{
	ivod_command_buffer_t *cmd_buffer = me->rtsp_command->cmd_buffer;
	char *sendStr = cmd_buffer->data;
	int needSendlen = 0,sendLen = 0;
	int ret = 0,now = (int)time_ms();

	needSendlen = cmd_buffer->lastSendLen;

	if(needSendlen < 0)
    {
		INFO("%s[ivod_rtsp_protocol_send_data] no ready data to send\n", PTL_MARK);
		return 0;
	}
    
	sendLen = cmd_buffer->sendLen;
    
    INFO("%s[ivod_rtsp_protocol_send_data]send RTSP (tm: %d) ==>\n%s\r\n", PTL_MARK, now, sendStr);	

    while(needSendlen > 0)
    {
    	ret = isocket_send(me->sock_id, sendStr + sendLen, needSendlen, 0);

		if (ret < 0)
		{
			cmd_buffer->sendLen = 0;
			cmd_buffer->lastSendLen = 0;
			INFO("%s[ivod_rtsp_protocol_send_data] rtsp Send Failed\n", PTL_MARK);
			ivod_rtsp_protocol_return_rtsp_reply(me, IVOD_RTSP_COMMAND_OTHER_STATE, -995, 0);
			return -1;
		}
		else if(ret == 0){
			return 0;
		}
		else{
			sendLen += ret;
			needSendlen -= ret;
			cmd_buffer->sendLen = sendLen;
			cmd_buffer->lastSendLen = needSendlen;
		}
	}
	cmd_buffer->sendLen = 0;
	cmd_buffer->lastSendLen = 0;
    
    implayer_qos_proc("logmsg", (int)sendStr, (int)"vod", 1);
    	
	return sendLen;
}



/*从应答临时缓冲区中获取一个完整的RTSP应答*/
static int ivod_rtsp_protocol_recv_data( ivod_rtsp_protocol_t *me )
{
	ivod_command_buffer_t *cmd_buffer = me->rtsp_command->cmd_buffer;
	int iRemLen = 0;
	int iLen = 0,ret = 0,now = (int)time_ms();

	iRemLen = PROTOCOL_MAX_RECVPKT_LEN - cmd_buffer->recvLen;
    INFO("%s[ivod_rtsp_protocol_recv_data] iRemLen = %d\n", PTL_MARK, iRemLen);

	iLen = ivod_rtsp_protocol_reply_buffer_operate(me, IVOD_REPLY_BUFFER_GET_DATA,(int)(&cmd_buffer->reply[cmd_buffer->recvLen]),iRemLen);    
	if( iLen > 0 ) 
    {
        *(cmd_buffer->reply+cmd_buffer->recvLen+iLen) = 0;        
	} 
    else
    {
        *(cmd_buffer->reply+cmd_buffer->recvLen) = 0;        
	}
    
	if (iLen <= 0)
	{
		return iLen;
	}

    INFO("%s[ivod_rtsp_protocol_recv_data]recv RTSP (tm: %d) ==>\n%s\r\n", PTL_MARK, now, (cmd_buffer->reply+cmd_buffer->recvLen));	

	cmd_buffer->recvLen += iLen;

	if (cmd_buffer->recvLen < PROTOCOL_RTSP_HEADLEN)
	{
		return 0; // 接收未完成, 正常返回
	}
	cmd_buffer->reply[cmd_buffer->recvLen] = '\0';//截断消息，避免上次遗留

    INFO("%s[ivod_rtsp_protocol_recv_data] end iLen=%d!\n", PTL_MARK, iLen);

    implayer_qos_proc("logmsg", (int)cmd_buffer->reply, (int)"vod", 1);
    
	return iLen;
}



/*接收应答并解析处理*/
static int ivod_rtsp_protocol_parse_reply( ivod_rtsp_protocol_t *me )
{
	ivod_command_buffer_t  *cmd_buffer = NULL;
	int ret = 0,current_op = IVOD_RTSP_COMMAND_IDLE;


	FAILED_RETURNX( !me || !me->rtsp_command, IPANEL_ERR );
	
    INFO("%s[ivod_rtsp_protocol_parse_reply] start!\n", PTL_MARK);

    cmd_buffer = me->rtsp_command->cmd_buffer;

	ret = ivod_rtsp_protocol_recv_data( me );

    if( ret > 0 ) 
    {
		INFO("%s[ivod_rtsp_protocol_parse_reply] recv data len %d\n", PTL_MARK, ret);
	} 
    else if( ret == 0 )
    {
		return 0;
	}
    else
	{
		ivod_rtsp_protocol_return_rtsp_reply(me, IVOD_RTSP_COMMAND_UNKNOWN, ret, 0);
		return -1;
	}   

    //将应答按行处理存储，并获取服务器返回的错误码包括200
	ret = ivod_rtsp_protocol_get_answers(me);

	cmd_buffer->reply_offSet = cmd_buffer->reply;
	cmd_buffer->recvLen= 0;             

	if(ret == PROTOCOL_RTSP_STATUS_OK)
    {
    	INFO("%s[ivod_rtsp_protocol_parse_reply] server reply is 200 OK\n", PTL_MARK);
    	current_op = (int)ivod_rtsp_protocol_get_rtsp_command(me,me->rtsp_command->answer_seq);
		ivod_rtsp_protocol_return_rtsp_reply(me, current_op, 0,0);
    }
	else if(ret == PROTOCOL_RTSP_STATUS_ANNOUNCE)
    {            
		ivod_rtsp_protocol_return_rtsp_reply(me, IVOD_RTSP_COMMAND_ANNOUNCE, 0, 0);
	}
	else if(ret == PROTOCOL_RTSP_STATUS_OPTIONS)
    {   
		ivod_rtsp_protocol_return_rtsp_reply(me, IVOD_RTSP_COMMAND_VALIDATE, 0, 0);
	}
	else if(ret == PROTOCOL_RTSP_STATUS_SET_PARAMETER)
    {
		ivod_rtsp_protocol_return_rtsp_reply(me, IVOD_RTSP_COMMAND_SET_PARAM, 0, 0);
	}
	else if(ret == PROTOCOL_RTSP_STATUS_REDIRECT)
    {
		ivod_rtsp_protocol_return_rtsp_reply(me, IVOD_RTSP_COMMAND_VALIDATE, 0, 0);
	}
	else if(ret == PROTOCOL_RTSP_STATUS_MOVED)
    {
		ivod_rtsp_protocol_return_rtsp_reply(me, IVOD_RTSP_COMMAND_VALIDATE, 0, 0);
	}		
	else if(ret == PROTOCOL_RTSP_STATUS_UNKNOWN)
    {
		ivod_rtsp_protocol_return_rtsp_reply(me, IVOD_RTSP_COMMAND_UNKNOWN, 0, 0);
	}
	else
    {
        ivod_rtsp_protocol_return_rtsp_reply(me, IVOD_RTSP_COMMAND_OTHER_STATE, ret, 0);
	}

	return 0;
}


/*清空交互命令请求内容*/
static void ivod_rtsp_protocol_clear_request(ivod_rtsp_protocol_t *me)
{
	int i = 0;

    if( !me || !me->rtsp_command ) 
        return;

	for(i=0;i< PROTOCOL_MAX_FIELDS;i++ ) 
    {
		if(me->rtsp_command->request[i]) 
        {
			free(me->rtsp_command->request[i]);
			me->rtsp_command->request[i] = NULL;
		}
	}

}


/*清空交互命令应答内容*/
static void ivod_rtsp_protocol_clear_answers(ivod_rtsp_protocol_t *me)
{
	int i = 0;

    if( !me || !me->rtsp_command ) 
        return;

	for(i=0;i< PROTOCOL_MAX_FIELDS;i++ ) 
    {
		if(me->rtsp_command->answers[i]) 
        {
			free(me->rtsp_command->answers[i]);
			me->rtsp_command->answers[i] = NULL;
		}
	}
}



/*将应答解析成每一行存储*/
static int ivod_rtsp_protocol_get_answers( ivod_rtsp_protocol_t *me )
{
	ivod_command_buffer_t *cmd_buffer = me->rtsp_command->cmd_buffer;
    char *answer = NULL;
	ivod_rtsp_command_t *cmd_info = me->rtsp_command;
    char **answer_ptr = cmd_info->answers;
    int code = 0, nLineCount = 0, nCurrLine = 0;
	int ans_count = 0;

    while(*answer_ptr)
    {
        answer_ptr++;
    }

    //获取应答中第一行数据
    answer = ivod_rtsp_protocol_get_line_data( me );
	if( !answer ) return 0;

    //获取服务器返回的错误码，包括200
	code = ivod_rtsp_protocol_get_reply_code(me, answer);

	if(code == PROTOCOL_RTSP_STATUS_UNKNOWN)
    {
		return code;
	}
    
	if (answer)
	{
		nLineCount ++;
		nCurrLine ++;
	}

    //统计总行数
	{
		char *pBegin = cmd_buffer->reply;
		char *pEnd = cmd_buffer->reply + cmd_buffer->recvLen;

		while (pBegin < pEnd)
		{
			if (*pBegin == '\n')
				nLineCount ++;
			pBegin ++;
		}
	}

    //将应答按行分开存储
	while (ans_count < PROTOCOL_MAX_FIELDS)
	{
		answer = ivod_rtsp_protocol_get_line_data( me );
		nCurrLine ++;
        
		if (nCurrLine >= nLineCount)
		{
			*answer_ptr = NULL;

			if (answer)
			{
				free(answer);
				answer = NULL;
			}
			return code;
		}
		if (answer == NULL)
			return 0;

		if(strlen(answer) == 0)
		{
			if (nCurrLine < nLineCount)
			{
				free(answer);
				answer = NULL;
				continue;
			}

		}
		else
		{
			if(!strncasecmp(answer, "Cseq:", strlen("Cseq:")))
			{
				char *pStr = answer + strlen("Cseq:");

                if(pStr)
                {
                    while( *pStr == ' ' ) pStr++;
                }

				cmd_info->answer_seq = atoi(pStr);
				if( cmd_info->cseq != cmd_info->answer_seq )
				{
					if(code != PROTOCOL_RTSP_STATUS_ANNOUNCE && code != PROTOCOL_RTSP_STATUS_SET_PARAMETER && code != PROTOCOL_RTSP_STATUS_REDIRECT )
					{
						INFO("%s[ivod_rtsp_protocol_get_answers] warning: Cseq mismatch. got %u, assumed %u\r\n",
									PTL_MARK, cmd_info->answer_seq, cmd_info->cseq);
					}
				}
			}

			*answer_ptr = answer;
			answer_ptr++;
			ans_count++;
		}//if
	}//while

    return code;
}



/*获取应答中一行数据*/
static char *ivod_rtsp_protocol_get_line_data( ivod_rtsp_protocol_t *me )
{
  ivod_command_buffer_t *cmd_buffer	= me->rtsp_command->cmd_buffer;
  char *str = NULL;
  char	*p = cmd_buffer->reply_offSet;
  int iRemDataLen = cmd_buffer->recvLen - (cmd_buffer->reply_offSet - cmd_buffer->reply);
  int iLen = 0;

  //顺序扫描,一次扫描一行
  while (iLen <= iRemDataLen)
  {
	  iLen++;
	  if (*p == '\n')
	  {
		  str = (char*)calloc(iLen+5, sizeof(char));
		  if(str)
		  {
			memcpy(str, cmd_buffer->reply_offSet, iLen);

			if (*(str+iLen-2) == '\r' || *(str+iLen-2) == '\n')
				*(str+iLen - 2) = 0;
			else
				*(str+iLen - 1) = 0;
		  }
		  cmd_buffer->reply_offSet += iLen;
		  break;
	  }
	  p++;
  }

  return str;
}



/*获取服务器应答错误码*/
static int ivod_rtsp_protocol_get_reply_code( ivod_rtsp_protocol_t *me, const char *str )
{
    char psz_buffer[4];
    int i_code = PROTOCOL_RTSP_STATUS_UNKNOWN;

    if(!strncmp(str, "RTSP/1.0", sizeof("RTSP/1.0") - 1))
    {
        memcpy(psz_buffer, str + sizeof("RTSP/1.0"), 3);
        psz_buffer[3] = 0;
        i_code = atoi( psz_buffer );
    }
    else if(!strncmp( str, "SET_PARAMETER", strlen("SET_PARAMETER")))
    {
        return (int)PROTOCOL_RTSP_STATUS_SET_PARAMETER;
    }
	else if(!strncmp( str, "REDIRECT", strlen("REDIRECT")))
    {
        return (int)PROTOCOL_RTSP_STATUS_REDIRECT;
    }
	else if(!strncmp( str, "ANNOUNCE", strlen("ANNOUNCE")))
    {
        return (int)PROTOCOL_RTSP_STATUS_ANNOUNCE;
    }
	else if(!strncmp( str, "OPTIONS", strlen("OPTIONS")))
    {
        return (int)PROTOCOL_RTSP_STATUS_OPTIONS;
    }
	else if(!strncmp(str, "Moved", strlen("Moved")))
	{
		return (int)PROTOCOL_RTSP_STATUS_MOVED;
	}

    return i_code;
}



/*根据cseq获取发送的命令*/
static ivod_rtsp_command_e ivod_rtsp_protocol_get_rtsp_command( ivod_rtsp_protocol_t *me, unsigned int cseq )
{
	ivod_rtsp_command_e op = IVOD_RTSP_COMMAND_IDLE;
	int i = 0;
	if (cseq == 0)
	{
		op = (ivod_rtsp_command_e)me->rtsp_command->current_op;
		return op;
	}

	for (i=0;i<PROTOCOL_MAX_COMMANDS;i++)
	{
		if (me->rtsp_command->cseqList[i]==cseq)
		{
			op = me->rtsp_command->opList[i];
			me->rtsp_command->cseqList[i] = 0;
			me->rtsp_command->opList[i] = IVOD_RTSP_COMMAND_IDLE;
			me->rtsp_command->opTimeList[i] = 0;

			goto OK_EXIT;
		}
	}
    
	//如果在队列中没有找到相关命令，则返回当前的命令
	op = (ivod_rtsp_command_e)me->rtsp_command->current_op;
OK_EXIT: 
    ivod_rtsp_protocol_printf_command(op);
	return op;
}



//获取一个完整RTSP应答的数据长度
static int ivod_rtsp_protocol_get_rtsp_reply_length(char *buffer, int len)
{
	int content_len = -1;
	char *p_content = NULL,*p_tmp = NULL;
	char len_buf[16] = {0};   
	char temp_buffer[PROTOCOL_MAX_RECVPKT_LEN] = {0};

    PROTOCOL_ZERO(len_buf, sizeof(len_buf));
    p_tmp = len_buf;

    if(!buffer || len < 0 ) 
        return -1;

    p_content = strstr(buffer,"\r\n\r\n");

    if( p_content ) 
    {
        memcpy(temp_buffer,buffer,p_content - buffer);
        p_content = strstr(temp_buffer,"Content-Length");
        if( p_content == NULL ) 
        {
            p_content = strstr(temp_buffer,"Content-length");
        }
    }

    //获取SDP的数据长度
    if(p_content != NULL )
    {
        p_content += strlen("Content-Length");

        //寻找Content-Length字段参数值              
        for(;;)
        {
            if ((*p_content >= '0' && *p_content <= '9')
              || *p_content == '\r'
              || *p_content == '\n')
                break;
            else
                p_content++;
        }

        //如果找到Content-Length字段参数值
        if (*p_content != '\r' && *p_content != '\n')
        {
            for(;;)
            {
                if (*p_content >= '0' && *p_content <= '9')
                {
                    *p_tmp = *p_content;
                    p_tmp++;
                    p_content++;
                }
                else
                {
                    *p_tmp = '\0';
                    content_len = atoi(len_buf);
					break;
                }
            }
        }
        else
        {
            content_len = -1;
        }
    }

	if( content_len > 0 ) 
    {
        //如果rtsp应答中包括SDP
		p_tmp = strstr(buffer,"\r\n\r\n")+4;
		if( p_tmp ) 
        {
			if(p_tmp - buffer + content_len <= len ) 
            {
				return p_tmp - buffer + content_len;
			} 
            else 
            {
				return -1;
			}
		} 
        else
        {
			return -1;
        }
	} 
    else 
    {
        //如果不包括SDP
		p_tmp = strstr(buffer,"\r\n\r\n");

		if( !p_tmp ) 
            return -1;
		else 
        {
			return p_tmp-buffer+4;
		}
	}

	return -1;
}



static void ivod_rtsp_protocol_printf_command(ivod_rtsp_command_e op)
{
    switch(op)
    {
        case IVOD_RTSP_COMMAND_IDLE:
            INFO("%s[ivod_rtsp_protocol_printf_command] op= IVOD_RTSP_COMMAND_IDLE\n", PTL_MARK);
            break;
        case IVOD_RTSP_COMMAND_CLOSE:
            INFO("%s[ivod_rtsp_protocol_printf_command] op= IVOD_RTSP_COMMAND_CLOSE\n", PTL_MARK);
            break;
        case IVOD_RTSP_COMMAND_DESCRIBE:
            INFO("%s[ivod_rtsp_protocol_printf_command] op= IVOD_RTSP_COMMAND_DESCRIBE\n", PTL_MARK);
            break;            
        case IVOD_RTSP_COMMAND_SETUP:
            INFO("%s[ivod_rtsp_protocol_printf_command] op= IVOD_RTSP_COMMAND_SETUP\n", PTL_MARK);
            break;
        case IVOD_RTSP_COMMAND_PLAY:
            INFO("%s[ivod_rtsp_protocol_printf_command] op= IVOD_RTSP_COMMAND_PLAY\n", PTL_MARK);
            break;
        case IVOD_RTSP_COMMAND_OPTION:
            INFO("%s[ivod_rtsp_protocol_printf_command] op= IVOD_RTSP_COMMAND_OPTION\n", PTL_MARK);
            break;
        case IVOD_RTSP_COMMAND_PAUSE:
            INFO("%s[ivod_rtsp_protocol_printf_command] op= IVOD_RTSP_COMMAND_PAUSE\n", PTL_MARK);
            break;
        case IVOD_RTSP_COMMAND_GET_PARAM:
            INFO("%s[ivod_rtsp_protocol_printf_command] op= IVOD_RTSP_COMMAND_GET_PARAM\n", PTL_MARK);
            break;
        case IVOD_RTSP_COMMAND_SET_PARAM:
            INFO("%s[ivod_rtsp_protocol_printf_command] op= IVOD_RTSP_COMMAND_SET_PARAM\n", PTL_MARK);
            break;
        case IVOD_RTSP_COMMAND_VALIDATE:
            INFO("%s[ivod_rtsp_protocol_printf_command] op= IVOD_RTSP_COMMAND_VALIDATE\n", PTL_MARK);
            break;
        case IVOD_RTSP_COMMAND_OTHER_STATE:
            INFO("%s[ivod_rtsp_protocol_printf_command] op= IVOD_RTSP_COMMAND_OTHER_STATE\n", PTL_MARK);
            break;
        case IVOD_RTSP_COMMAND_UNKNOWN:
        default:
            INFO("%s[ivod_rtsp_protocol_printf_command] op= IVOD_RTSP_COMMAND_UNKNOWN\n", PTL_MARK);
            break;
    }
}



/*保存发送的RTSP命令*/
static int ivod_rtsp_protocol_save_rtsp_command( ivod_rtsp_protocol_t *me, ivod_rtsp_command_e op, unsigned int cseq )
{
	int i;

	if (cseq==0) return IPANEL_ERR;
    
    ivod_rtsp_protocol_printf_command(op);
    
	for (i=0;i<PROTOCOL_MAX_COMMANDS;i++)
	{
		if (me->rtsp_command->cseqList[i] == 0 
			|| ( me->rtsp_command->opTimeList[i] > 0 
				&& time_ms() - me->rtsp_command->opTimeList[i] > 600000)
				)
		{
			me->rtsp_command->cseqList[i] = cseq;
			me->rtsp_command->opList[i] = op;
			me->rtsp_command->opTimeList[i] = (unsigned int)time_ms();
			return IPANEL_OK;
		}
	}
    
	return IPANEL_ERR;
}



/*RTSP应答临时buffer的数据处理*/
static int ivod_rtsp_protocol_reply_buffer_operate(ivod_rtsp_protocol_t *me, int msg, int p1, int p2)
{
    int ret = IPANEL_OK;
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    switch(msg)
    {
        case IVOD_REPLY_BUFFER_INIT:
            {                    
                memset(me->response_buffer, 0, sizeof(me->response_buffer));
            	me->response_len = 0;
            }
            break;
        case IVOD_REPLY_BUFFER_ADD_DATA:
            if( p1 && p2 && me->response_buffer && me->response_len+p2 < sizeof(me->response_buffer)) 
            {
    			memcpy(me->response_buffer + me->response_len, (char*)p1, p2);
    			me->response_len += p2;
    		} 
            else 
            {
                INFO("%s[reply_buffer_operate] reply buffer overflow,add failed\n", PTL_MARK);
                ret = IPANEL_ERR;
            }
            break;
        case IVOD_REPLY_BUFFER_GET_DATA:
            {
    			FAILED_RETURNX(p1 == 0 || me->response_len < 0, IPANEL_ERR);
       			ret = ivod_rtsp_protocol_get_rtsp_reply_length(me->response_buffer, me->response_len);
                if( ret > 0 && p2 >= ret && p1 ) 
                {
            		INFO("%s[reply_buffer_operate] before response_len = %d\n", PTL_MARK, me->response_len);
                    memcpy((char*)p1, me->response_buffer, ret);
                    memset(me->data_buffer, 0, PROTOCOL_BUFFER_LEN);
    				memcpy(me->data_buffer, me->response_buffer, ret);
                    
    				me->response_len -= ret;
    				me->response_len = me->response_len > 0 ? me->response_len : 0;
    				memmove(me->response_buffer, me->response_buffer + ret, me->response_len);
    				memset(me->response_buffer + me->response_len, 0, sizeof(me->response_buffer) - me->response_len);
            		INFO("%s[reply_buffer_operate] after response_len = %d\n", PTL_MARK, me->response_len);
                }
		    }
            break;
        default:
            break;
    }
    return ret;    
}


/*将交互命令应答通过回调返回*/
static int ivod_rtsp_protocol_return_rtsp_reply(ivod_rtsp_protocol_t *me, int msg, int wparam, int lparam )
{
	int event = IVOD_RTSP_EVENT_NONE;
	int i = 0, status = 0, ret = 0,speed = 0;
	int event_list[] = {    
                    	IVOD_RTSP_COMMAND_DESCRIBE,
                    	IVOD_RTSP_COMMAND_SETUP,
                    	IVOD_RTSP_COMMAND_PLAY,
                    	IVOD_RTSP_COMMAND_PAUSE,
                    	IVOD_RTSP_COMMAND_CLOSE,
                    	IVOD_RTSP_COMMAND_OPTION,
                    	IVOD_RTSP_COMMAND_PING,
                    	IVOD_RTSP_COMMAND_SET_PARAM,                    	
                    	IVOD_RTSP_COMMAND_GET_PARAM,
                    	IVOD_RTSP_COMMAND_VALIDATE,
                        IVOD_RTSP_COMMAND_ANNOUNCE,                    	
                    	IVOD_RTSP_COMMAND_OTHER_STATE
                        };
	int rtsp_event_list[] = {
                    	IVOD_RTSP_ACK_DESCRIBE,
                    	IVOD_RTSP_ACK_SETUP,
                    	IVOD_RTSP_ACK_PLAY,	
                    	IVOD_RTSP_ACK_PAUSE,
                    	IVOD_RTSP_ACK_CLOSE,
                    	IVOD_RTSP_ACK_OPTION,
                    	IVOD_RTSP_ACK_PING,
                    	IVOD_RTSP_ACK_SET_PARAM,
                    	IVOD_RTSP_ACK_GET_PARAM,	
                    	IVOD_RTSP_ACK_VADILATE,
                    	IVOD_RTSP_ACK_ANNOUNCE_RESPONSE,
                    	IVOD_RTSP_ACK_ERROR_RESPONSE
							};	
	FAILED_RETURNX(NULL == me, IPANEL_ERR);
    
	for( i = 0; i < sizeof( event_list ) / sizeof(event_list[0]); i++ ) 
    {
		if( msg == event_list[i] ) 
        {
			event = rtsp_event_list[i];
			break;
		}
	}

	if( event != IVOD_RTSP_EVENT_NONE ) 
    {
		ret = me->rtsp_callback(me->rtsp_session, event, wparam, lparam);	
	}
    
	ivod_rtsp_protocol_clear_answers(me);
    
	return IPANEL_ERR;
}


