/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:SOCKET管理
*********************************************************************/
#ifndef __ISOCKET_H_
#define __ISOCKET_H_

#ifdef __cplusplus
extern "C"{
#endif

#include "ivod_client_main.h"


typedef struct isocket_mgr_s isocket_mgr_t;
typedef struct isocket_s isocket_t;


// socket类型
enum
{
    ISOCKET_TYPE_TCP = 0,         
    ISOCKET_TYPE_UDP,
    ISOCKET_TYPE_TCP_ACCEPT
};

// socket event事件
enum {
    ISOCKET_EVENT_CONNECT=1,
    ISOCKET_EVENT_CLOSE,
    ISOCKET_EVENT_READ,
    ISOCKET_EVENT_WRITE,
	ISOCKET_EVENT_LISTEN,
    ISOCKET_EVENT_ACCEPT,
    ISOCKET_EVENT_FAILED,	// only for SOCKET连接失败(由于页面要捕获socket消息,细分SOCKET事件) 
	ISOCKET_EVENT_ERROR,	// generic errors 
	ISOCKET_EVENT_REFRESH,  //要优先级低的socket去做关闭操作
    ISOCKET_EVENT_UNKNOWN
};

enum{
    ISOCKET_INTER_NULL,         //不复用
	ISOCKET_INTER_RTP,	       //被rtp复用
	ISOCKET_INTER_TS,           //被ts复用
	ISOCKET_INTER_HTTP          //被ts复用
};

/*创建socket管理器*/
void *
isocket_mgr_new(void);

/*销毁socket管理器*/
void
isocket_mgr_delete(void *handle);

/*open*/
int 
isocket_open(int type, int protocol, client_t *handle);

/*close*/
int
isocket_close(unsigned int socket_id);

/*bind*/
int
isocket_bind(unsigned int socket_id, unsigned int addr, int port);

/*connect*/
int
isocket_connect(unsigned int socket_id, unsigned int addr, int port);

/*send*/
int 
isocket_send(unsigned int socket_id, char *buf, unsigned int buf_len, int flags);

/*sendto*/
int 
isocket_sendto(unsigned int socket_id, char *buf, unsigned int buf_len, int flags, unsigned int addr, int port);

/*recv*/
int
isocket_recv(unsigned int socket_id, char *buf, unsigned int buf_len, int flags);

/*recvfrom*/
int
isocket_recvfrom(unsigned int socket_id, char *buf, unsigned int buf_len, int flags, unsigned int addr, int *port);

/*join multicast*/
int 
isocket_join_multicast(unsigned int socket_id, unsigned int addr);

/*leave multicast*/
int 
isocket_leave_multicast(unsigned int socket_id, unsigned int addr);

/*socket set interleave*/
int 
isocket_set_interleave(unsigned int socket_id, unsigned char interleave_type);

/*socket getsockname*/
int
isocket_getsockname(unsigned int socket_id, unsigned int addr, int *port);

#ifdef __cplusplus
}
#endif

#endif
