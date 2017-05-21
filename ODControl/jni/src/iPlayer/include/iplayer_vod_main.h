/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:VOD子模块公开的函数
*********************************************************************/
#ifndef __IPALYER_VOD_MAIN_H_
#define __IPALYER_VOD_MAIN_H_

#include "iplayer_main.h"
#include "ipanel_porting.h"

#ifdef __cplusplus
extern "C"{
#endif

typedef enum
{
    IVOD_MODE_NONE,
    IVOD_MODE_IP,
    IVOD_MODE_DVB
}ivod_mode_e;

typedef enum
{
    MODULE_IVOD_RROP_NONE,
    MODULE_IVOD_PROP_NAME,
    MODULE_IVOD_PROP_MODE
}ivod_prop_e;

/*创建VOD模块*/
void *
ivod_module_new(void);

/*初始化VOD类型*/
int
ivod_module_init(void *handle);

/*销毁VOD模块*/
void 
ivod_module_delete(void *handle);

/*VOD模块全局消息处理*/
int
ivod_module_proc(void *handle, int msg, unsigned int p1, unsigned int p2);

/*VOD模块全局参数获取*/
int 
ivod_module_get_property(void *handle, int name, unsigned int *value);

/*VOD模块全局参数设置*/
int 
ivod_module_set_property(void *handle, int name, unsigned int value);

/*mplayer创建vod播放器*/
void *
ivod_player_create(implayer_t *me, implayer_cbf cbf);

/*销毁*/
int 
ivod_player_destroy(void *handle);

/*打开*/
int
ivod_player_open(void *me, const char *src, const char *type);

/*关闭*/
int
ivod_player_close(void *me);

/*停止*/
int
ivod_player_stop(void *me, int keep_last_frame);

/*暂停*/
int
ivod_player_pause(void *me);

/*播放、快进、快退*/
int
ivod_player_play(void *me, int speed);

/*SEEK*/
int
ivod_player_seek(void *me, char *seek_time);

/*获取参数*/
int
ivod_player_get_property(void *me, int prop, void *value);

/*设置参数*/
int
ivod_player_set_property(void *me, int prop, int value);

#ifdef __cplusplus
}
#endif

#endif
/************************************End Of File**********************************/

