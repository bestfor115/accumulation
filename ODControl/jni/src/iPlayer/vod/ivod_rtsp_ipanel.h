/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:iPanel vod客户端实现
*********************************************************************/
#ifndef __IVOD_RTSP_IPANEL_H_
#define __IVOD_RTSP_IPANEL_H_

#include "ivod_rtsp_client.h"
#include "ivod_client_main.h"

#ifdef __cplusplus
extern "C"{
#endif

unsigned int 
ivod_rtsp_ipanel_open(client_t *client_main, ivod_rtsp_client_t *rtsp_client, int mode, ivod_msg_func func);

int 
ivod_rtsp_ipanel_close(unsigned int handle);

int
ivod_rtsp_ipanel_proc(unsigned int handle, unsigned int event, int p1, int p2);

int
ivod_rtsp_ipanel_set_prop(unsigned int handle, int prop, int value);

int
ivod_rtsp_ipanel_get_prop(unsigned int handle, int prop, void *value);

#ifdef __cplusplus
}
#endif

#endif
