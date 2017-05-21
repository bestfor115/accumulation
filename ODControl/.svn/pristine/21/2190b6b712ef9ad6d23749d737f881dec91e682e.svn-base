/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:实现VOD初始化、VOD的全局数据处理(自发现数据等)、媒体播放的VOD部分封装
*********************************************************************/
#include "iplayer_vod_main.h"
#include "iplayer_main.h"
#include "iplayer_common.h"
#include "iplayer_socket.h"
#include "ivod_client_main.h"

#define VOD_MARK    "[IVOD]"

typedef struct ivod_mgr_s
{
    void *sock_mgr;
    client_mgr_t *client_mgr;
    unsigned int vod_name;    
	unsigned int vod_mode;
}ivod_mgr_t;

typedef struct ivod_player_s
{
    implayer_t *mplayer_main;
    implayer_cbf mplayer_callback_func;
    client_t *client_handle;
    int flag_paused:1;
}ivod_player_t;

static ivod_mgr_t *s_vod_mgr = NULL;

/*获得vod模块句柄*/
void *ivod_module_get_handle(void)
{
    return (void *)s_vod_mgr;
}

/*创建VOD模块*/
void *ivod_module_new(void)
{
    ivod_mgr_t *me = NULL;
    
    INFO("%s[ivod_module_new]\n", VOD_MARK);

    /*分配内存*/
    me = (ivod_mgr_t *)calloc(1, sizeof(ivod_mgr_t));
    if(NULL == me)
    {
        INFO("%s[ivod_module_new]calloc failed\n", VOD_MARK);
        goto FAILED;
    }
    /*创建socket mgr*/
    me->sock_mgr = isocket_mgr_new();
    if(NULL == me->sock_mgr)
    {
        INFO("%s[ivod_module_new]create socket mgr failed\n", VOD_MARK);
        goto FAILED;        
    }
    /*创建client mgr*/
    me->client_mgr = client_mgr_new();
    if(NULL == me->client_mgr)
    {
        INFO("%s[ivod_module_new]create client mgr failed\n", VOD_MARK);
        goto FAILED;        
    }
    
    ivod_module_init(me);
    
    s_vod_mgr = me;
    
    INFO("%s[ivod_module_new] success\n", VOD_MARK);

    return me;

FAILED:
    if(me)
    {
        if(me->client_mgr)
        {
            client_mgr_delete(me->client_mgr);
            me->client_mgr = NULL;
        }
        if(me->sock_mgr)
        {
            isocket_mgr_delete(me->sock_mgr);
            me->sock_mgr = NULL;
        }
        free(me);
        me = NULL;
    }
    INFO("%s[ivod_module_new] failed\n", VOD_MARK);
    return NULL;
}


/*初始化VOD类型*/
int ivod_module_init(void *handle)
{
    ivod_mgr_t *me = (ivod_mgr_t *)handle;

    FAILED_RETURN(NULL == me);

    INFO("%s[ivod_module_init]\n", VOD_MARK);

    /*这里需要从某个地方获取配置的vod类型，这里暂时写死*/
  
    me->vod_name = IVOD_NAME_ISMA;

}


/*销毁VOD模块*/
void ivod_module_delete(void *handle)
{
    ivod_mgr_t *me = (ivod_mgr_t *)handle;

    FAILED_RETURN(NULL == me);

    INFO("%s[ivod_module_delete]\n", VOD_MARK);

    if(me)
    {

        if(me->sock_mgr)
        {
            isocket_mgr_delete(me->sock_mgr);
            me->sock_mgr = NULL;
        }
         
        if(me->client_mgr)
        {
            client_mgr_delete(me->client_mgr);
            me->client_mgr = NULL;
        }

        free(me);
        me = NULL;
    }

    INFO("%s[ivod_module_delete] success\n", VOD_MARK);
    
    return ;
}

/*VOD模块全局消息处理*/
int ivod_module_proc(void *handle, int msg, unsigned int p1, unsigned int p2)
{
    INFO("%s[ivod_module_proc]\n", VOD_MARK);
    
	return IPANEL_OK;
}

/*VOD模块全局参数获取*/
int ivod_module_get_property(void *handle, int name, unsigned int *value)
{
    ivod_mgr_t *me = (ivod_mgr_t *)handle;
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    INFO("%s[ivod_module_get_property] name = %d\n", VOD_MARK,name);

    switch(name)
    {
        case MODULE_IVOD_PROP_NAME:
            if(value)
            {
                if(me->vod_name != 0)
                    *value = me->vod_name;
                else
                    *value = IVOD_NAME_ISMA;
            }
            break;
		case MODULE_IVOD_PROP_MODE:
            if(value)
            {
                if(me->vod_mode != 0)
                    *value = me->vod_mode;
                else
                    *value = IVOD_MODE_DVB;
            }
        default:
            break;
    }

    INFO("%s[ivod_module_get_property] success\n", VOD_MARK);    

    return IPANEL_OK;
}

/*VOD模块全局参数设置*/
int ivod_module_set_property(void *handle, int name, unsigned int value)
{
    ivod_mgr_t *me = (ivod_mgr_t *)handle;
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    INFO("%s[ivod_module_set_property]\n", VOD_MARK);

    switch(name)
    {
        case MODULE_IVOD_PROP_NAME:
            if(value)
                me->vod_name = value;
            break;
		case MODULE_IVOD_PROP_MODE:
			if(value)
				me->vod_mode = value;
        default:
            break;
    }    

    INFO("%s[ivod_module_set_property] success\n", VOD_MARK);   

    return IPANEL_OK;
}

/*消息回调函数*/
static int ivod_player_msg_func(void *handle, unsigned int msg, int p1, int p2)
{
    ivod_player_t *me = (ivod_player_t *)handle;

    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    
    INFO("%s[ivod_player_msg_func] msg = %d, p1 = %d, p2 = %d\n", VOD_MARK, msg, p1, p2);
    /*VOD这一层有可能需要对消息过滤一下再处理*/
    if(me->mplayer_callback_func)
        me->mplayer_callback_func(me->mplayer_main, msg, p1, p2);
    else
        return IPANEL_ERR;
    
    INFO("%s[ivod_player_msg_func] success\n", VOD_MARK);
    
	return IPANEL_OK;
}

/*创建vod播放实例*/
void *ivod_player_create(implayer_t *mplayer_main, implayer_cbf cbf)
{
    ivod_player_t *me = NULL;
    
    FAILED_RETURNX(NULL == mplayer_main, NULL);

    INFO("%s[ivod_player_create]\n", VOD_MARK);

    me = (ivod_player_t *)calloc(1, sizeof(ivod_player_t));
    if(NULL == me)
    {
        INFO("%s[ivod_player_create] calloc failed\n", VOD_MARK);
        goto FAILED;
    }
    me->mplayer_main = mplayer_main;
    me->mplayer_callback_func = cbf;
    /*创建client*/
    me->client_handle = client_create(me, ivod_player_msg_func);
    if(NULL == me->client_handle)
    {
        INFO("%s[ivod_player_create]create client failed\n", VOD_MARK);        
        goto FAILED;
    }    

    INFO("%s[ivod_player_create] success\n", VOD_MARK);

    return me;
    
FAILED:
    if(me)
    {
        if(me->client_handle)
        {
            client_destroy(me->client_handle);
            me->client_handle = NULL;
        }
        free(me);
        me = NULL;
    }
    INFO("%s[ivod_player_create] failed\n", VOD_MARK);
    
    return NULL;
}

/*销毁vod播放实例*/
int ivod_player_destroy(void *handle)
{
    ivod_player_t *me = (ivod_player_t *)handle;

    INFO("%s[ivod_player_destroy]\n", VOD_MARK);

    if(me)
    {
        if(me->client_handle)
        {
            client_destroy(me->client_handle);
            me->client_handle = NULL;
        }
        free(me);
        me = NULL;
    }

    INFO("%s[ivod_player_destroy] success\n", VOD_MARK);

    return IPANEL_OK;
}

/*打开*/
int ivod_player_open(void *handle, const char *src, const char *type)
{
    ivod_player_t *me = (ivod_player_t *)handle;
    int ret = IPANEL_ERR;
    
    FAILED_RETURNX(NULL == me 
                || NULL == me->client_handle
                || NULL == src
                || NULL == type, IPANEL_ERR);
    INFO("%s[ivod_player_open] src = %s, type = %s\n", VOD_MARK, src, type);
    /*发送命令*/
    ret = client_proc((client_t *)me->client_handle, CLIENT_EVENT_OPEN, (int)src, (int)type);

    INFO("%s[ivod_player_open] success\n", VOD_MARK);

    return ret; 
}

/*关闭*/
int ivod_player_close(void *handle)
{
    ivod_player_t *me = (ivod_player_t *)handle;
    int ret = IPANEL_ERR;
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    INFO("%s[ivod_player_close]\n", VOD_MARK);

    ret = client_proc((client_t *)me->client_handle, CLIENT_EVENT_CLOSE, 0, 0);

    INFO("%s[ivod_player_close] success\n", VOD_MARK);

    return ret;
}

/*停止*/
int ivod_player_stop(void *handle, int keep_last_frame)
{
    ivod_player_t *me = (ivod_player_t *)handle;
    int ret = IPANEL_ERR;
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    INFO("%s[ivod_player_stop] keep_last_frame = %d\n", VOD_MARK, keep_last_frame);

    ret = client_proc((client_t *)me->client_handle, CLIENT_EVENT_STOP, keep_last_frame, 0);    

    INFO("%s[ivod_player_stop] success\n", VOD_MARK);

    return ret;
}

/*暂停*/
int ivod_player_pause(void *handle)
{
    int ret = IPANEL_ERR;
    ivod_player_t *me = (ivod_player_t *)handle;
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    INFO("%s[ivod_player_pause]\n", VOD_MARK);

    me->flag_paused = 1;

    ret = client_proc((client_t *)me->client_handle, CLIENT_EVENT_PAUSE, 0, 0);    

    INFO("%s[ivod_player_pause] success\n", VOD_MARK);

    return ret;
}

/*播放、快进、快退*/
int ivod_player_play(void *handle, int speed)
{
    int ret = IPANEL_ERR;
    client_event_name_e name = CLIENT_EVENT_NONE;
    ivod_player_t *me = (ivod_player_t *)handle;
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    INFO("%s[ivod_player_play] speed = %d\n", VOD_MARK, speed);

    if(speed == 1)
        if(me->flag_paused)
            name = CLIENT_EVENT_RESUME;
        else
            name = CLIENT_EVENT_PLAY;
    else if(speed > 1)
        name = CLIENT_EVENT_FORWARD;
    else if(speed < 0)
        name = CLIENT_EVENT_BACKWARD;
    me->flag_paused = 0;

    ret = client_proc((client_t *)me->client_handle, name, speed, 0);

    INFO("%s[ivod_player_play] success\n", VOD_MARK);

    return ret;
}

/*SEEK*/
int ivod_player_seek(void *handle, char *seek_time)
{
    int ret = IPANEL_ERR;
    ivod_player_t *me = (ivod_player_t *)handle;
    
    FAILED_RETURNX(NULL == me || NULL == seek_time, IPANEL_ERR);

    INFO("%s[ivod_player_seek] seek_time = %s\n", VOD_MARK, seek_time);

    ret = client_proc((client_t *)me->client_handle, CLIENT_EVENT_SEEK, (int)seek_time, 0);

    INFO("%s[ivod_player_seek] success\n", VOD_MARK);

    return ret;
}

/*获取参数*/
int ivod_player_get_property(void *handle, int prop, void *value)
{
    int ret = IPANEL_ERR;
    ivod_player_t *me = (ivod_player_t *)handle;
    
    FAILED_RETURNX(NULL == me || NULL == value, IPANEL_ERR);

    INFO("%s[ivod_player_get_property] prop = %d\n", VOD_MARK, prop);

    ret = client_get_property((client_t *)me->client_handle, prop, value);

    INFO("%s[ivod_player_get_property] success\n", VOD_MARK);

    return ret;
}

/*设置参数*/
int ivod_player_set_property(void *handle, int prop, int value)
{
    int ret = IPANEL_ERR;
    ivod_player_t *me = (ivod_player_t *)handle;
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    ret = client_set_property((client_t *)me->client_handle, prop, value);

    return ret;
}

/************************************End Of File**********************************/


