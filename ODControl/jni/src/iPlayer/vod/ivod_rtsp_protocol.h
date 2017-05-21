/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:RTSP PROTOCOL实现
*********************************************************************/
#ifndef __IVOD_RTSP_PROTOCOL_H_
#define __IVOD_RTSP_PROTOCOL_H_

#ifdef __cplusplus
extern "C"{
#endif

#include "iplayer_common.h"

typedef struct ivod_rtsp_protocol_s ivod_rtsp_protocol_t;

/*打开*/
ivod_rtsp_protocol_t *
ivod_rtsp_protocol_open(void *client_main_handle, void *session_handle, ivod_msg_func cbf);

/*关闭*/
int
ivod_rtsp_protocol_close(ivod_rtsp_protocol_t *me);

/*proc*/
int
ivod_rtsp_protocol_proc(ivod_rtsp_protocol_t *me, unsigned int msg, int p1, int p2);

/*connect*/
int 
ivod_rtsp_protocol_connect(ivod_rtsp_protocol_t *me, char *url);

/*disconnect*/
int
ivod_rtsp_protocol_disconnect(ivod_rtsp_protocol_t *me);

/*describe*/
int
ivod_rtsp_protocol_describe(ivod_rtsp_protocol_t *me, const char *what );

/*options*/
int 
ivod_rtsp_protocol_options(ivod_rtsp_protocol_t *me, const char *what );

/*setup*/
int 
ivod_rtsp_protocol_setup(ivod_rtsp_protocol_t *me, const char *what );

/*setparameter*/
int 
ivod_rtsp_protocol_setparameter(ivod_rtsp_protocol_t *me, const char *what );

/*getparameter*/
int 
ivod_rtsp_protocol_getparameter(ivod_rtsp_protocol_t *me, const char *what );

/*validate*/
int 
ivod_rtsp_protocol_validate(ivod_rtsp_protocol_t *me, const char *what );

/*play*/
int 
ivod_rtsp_protocol_play(ivod_rtsp_protocol_t *me, const char *what );

/*pause*/
int 
ivod_rtsp_protocol_pause(ivod_rtsp_protocol_t *me, const char *what );

/*teardown*/
int 
ivod_rtsp_protocol_teardown(ivod_rtsp_protocol_t *me, const char *what );

/*ping*/
int 
ivod_rtsp_protocol_ping(ivod_rtsp_protocol_t *me, const char *what );

/*send ok*/
int
ivod_rtsp_protocol_send_ok( ivod_rtsp_protocol_t *me, int cseq, char *session, char *ondemand_session);

/*get field*/
char *
ivod_rtsp_protocol_get_field(ivod_rtsp_protocol_t *me, char *field, char flag);

/*get field address*/
char **
ivod_rtsp_protocol_get_field_address(ivod_rtsp_protocol_t *me, char *field);

/*add field*/
int 
ivod_rtsp_protocol_add_field(ivod_rtsp_protocol_t *me, char *field);

/*add seq*/
int 
ivod_rtsp_protocol_add_cseq(ivod_rtsp_protocol_t *me);

/*set session id*/
int 
ivod_rtsp_protocol_set_session(ivod_rtsp_protocol_t *me, char *session_id);

/*add session*/
int 
ivod_rtsp_protocol_add_session(ivod_rtsp_protocol_t *me);







#ifdef __cplusplus
}
#endif

#endif

