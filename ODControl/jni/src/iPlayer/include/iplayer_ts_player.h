/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
����:TS PLAYER
*********************************************************************/
#ifndef __TS_IPLAYER_H_
#define __TS_IPLAYER_H_

#ifdef __cplusplus
extern "C"{
#endif

#include "iplayer_player.h"


typedef struct ts_iplayer_s ts_iplayer_t;



/*��ts������*/
int
ts_iplayer_open(iplayer_t *handle, iplayer_info_t *info, iplayer_cbf cbf);


/*ts player����������*/
int
ts_iplayer_proc(int player_id, unsigned int op, int p1,int p2);



#ifdef __cplusplus
}
#endif

#endif
/************************************End Of File**********************************/



