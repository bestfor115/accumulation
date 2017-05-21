/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:RTSP SESSION实现
*********************************************************************/
#ifndef __IVOD_RTSP_SESSION_H_
#define __IVOD_RTSP_SESSION_H_

#ifdef __cplusplus
extern "C"{
#endif

#define END_STRING "\r\n"

typedef struct ivod_rtsp_session_s ivod_rtsp_session_t;

typedef int (*rtsp_session_request)(void *mgr, ivod_rtsp_session_t *session, int msg, int p1, int p2);

typedef struct rtsp_request_func_s
{
	rtsp_session_request    rtsp_describe;
	rtsp_session_request    rtsp_setup;
	rtsp_session_request    rtsp_play;
	rtsp_session_request    rtsp_pause;
	rtsp_session_request    rtsp_close;
	rtsp_session_request    rtsp_get_parameter;
	rtsp_session_request    rtsp_set_parameter;
	rtsp_session_request    rtsp_options;	
	rtsp_session_request    rtsp_validate;
	rtsp_session_request    rtsp_ping;
	rtsp_session_request    rtsp_response;      
}rtsp_request_func_t;


ivod_rtsp_session_t *
ivod_rtsp_session_open(client_t *client_main, void *vod_handle, rtsp_request_func_t *request);

int
ivod_rtsp_session_close(ivod_rtsp_session_t *me);

int
ivod_rtsp_session_proc(ivod_rtsp_session_t *me, unsigned int event, int p1, int p2);

ivod_rtsp_session_t *
ivod_rtsp_session_get_next(ivod_rtsp_session_t *session);

char *
ivod_rtsp_session_get_field(ivod_rtsp_session_t *me, char *field, char flag);

char **
ivod_rtsp_session_get_field_address(ivod_rtsp_session_t *me, char *field);

int 
ivod_rtsp_session_add_field(ivod_rtsp_session_t *me, char *field);

int 
ivod_rtsp_session_add_cseq(ivod_rtsp_session_t *me);

int 
ivod_rtsp_session_set_session(ivod_rtsp_session_t *me, char *session_id);

int 
ivod_rtsp_session_add_session(ivod_rtsp_session_t *me);

int 
ivod_rtsp_session_set_url(ivod_rtsp_session_t *me, char *url);

int 
ivod_rtsp_session_get_url(ivod_rtsp_session_t *me, char *url);




#ifdef __cplusplus
}
#endif

#endif
