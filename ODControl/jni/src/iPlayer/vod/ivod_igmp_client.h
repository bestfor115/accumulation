/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:IGMP CLIENT实现
*********************************************************************/
#ifndef __IVOD_IGMP_CLIENT_H_
#define __IVOD_IGMP_CLIENT_H_

#ifdef __cplusplus
extern "C"{
#endif

#include "ivod_client_main.h"

unsigned int 
ivod_igmp_open(client_t *me, ivod_msg_func func);

int
ivod_igmp_proc(unsigned int me, unsigned int event, int p1, int p2);

int 
ivod_igmp_close(unsigned int me);

int
ivod_igmp_set_prop(unsigned int handle, int prop, int value);

int
ivod_igmp_get_prop(unsigned int handle, int prop, void *value);


#ifdef __cplusplus
}
#endif

#endif

