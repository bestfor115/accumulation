/*********************************************************************
    Copyright (c) 2012 iPanel Technologies, Ltd.
    All rights reserved. You are not allowed to copy or distribute
    the code without permission.
 	History:	2012-9-5 pdc-develop7 Created
*********************************************************************/
#include "mplayer.h"
#include "iplayer_main.h"
#include "iplayer_vod_main.h"
#include "iplayer_common.h"
#include "./iPlayer/include/iplayer_common.h"

#define MPLAYER_MARK "[MPLAYER]"

typedef struct mplayer_s
{
    implayer_mgr_t *implayer_mgr;
    implayer_t  *implayer;
    void *vod_module;   
    mplayer_cbf func;
    int flag_open;
    
}mplayer_mgr_t;

static mplayer_mgr_t *s_mplayer_mgr = NULL;

static int mplayer_message(void *handle, int msg, int p1, int p2)
{
    mplayer_mgr_t *me = s_mplayer_mgr;    
    int event = -1;
    FAILED_RETURNX(NULL == me, MPLAYER_ERR);

    INFO("%s[mplayer_message] msg = %d, p1 = %d, p2 = %d\n", MPLAYER_MARK, msg, p1, p2);
    switch(p1)
    {
        case 5202:
            event = MPLAYER_PREPAREPLAY_SUCCESS;
            break;
        case 5203:
            event = MPLAYER_CONNECT_FAILED;
            break;
        case 5205:
            event = MPLAYER_PLAY_SUCCESS;
            break;
		case 5206:
			event = MPLAYER_PLAY_FAILED;
			break;
        case 5209:
            event = MPLAYER_PROGRAM_BEGIN;
            break;
        case 5210:
            event = MPLAYER_PROGRAM_END;
            break;
        case 5211:
            event = MPLAYER_RELEASE_SUCCESS;
            break;
        case 5212:
            event = MPLAYER_RELEASE_FAIL;
            break;
        case 5290:
            event = MPLAYER_SET_MEDIA_INFO;
            break;
        default:
            break;
    }
    if(event >= 0 && me->func != NULL)
    {
        me->func(me->implayer, event, p2, 0);
    }
    return MPLAYER_OK;    
}

void *mplayer_init(void)
{
    mplayer_mgr_t *me = NULL;

    INFO("%s[mplayer_init]\n", MPLAYER_MARK);    

    //android项目很容易异常退出的，所以关闭等操作可能就有时间没掉用，这里需要判断，不能总是初始化
    if(s_mplayer_mgr)
    {
        INFO("%s[mplayer_init]have exist a object\n", MPLAYER_MARK); 

        mplayer_close(NULL);
        
        return s_mplayer_mgr;
    }
    
    me = (mplayer_mgr_t *)calloc(1, sizeof(mplayer_mgr_t));
    if(NULL == me)
    {
        INFO("%s[mplayer_init] calloc mgr failed\n", MPLAYER_MARK);
        goto INIT_ERROR;
    }
    
    me->implayer_mgr = implayer_mgr_new();
    if(NULL == me->implayer_mgr)
    {
        INFO("%s[mplayer_init] calloc implayer mgr failed\n", MPLAYER_MARK);
        goto INIT_ERROR;
    }

    me->vod_module = ivod_module_new();
    if(NULL == me->vod_module)
    {
        INFO("%s[mplayer_init] calloc vod_module failed\n", MPLAYER_MARK);
        goto INIT_ERROR;             
    }

    INFO("%s[mplayer_init] success\n", MPLAYER_MARK);
    
    s_mplayer_mgr = me;
    return me;
    
INIT_ERROR:
    INFO("%s[mplayer_init] failed\n", MPLAYER_MARK);
    if(me)
    {
        if(me->vod_module != NULL)
        {
            ivod_module_delete(me->vod_module);
            me->vod_module = NULL;
        }
        if(me->implayer_mgr != NULL)
        {
            implayer_mgr_delete(me->implayer_mgr);
            me->implayer_mgr = NULL;
        }

        free(me);
        me = NULL;
    }    
    s_mplayer_mgr = NULL;
    return NULL;
}

/*****************************************************************
 * @brief 销毁mplayer模块
 
 * @param handle 初始化时得到的句柄
 *
 * @return 无
******************************************************************/
void mplayer_exit(void *handle)
{
    mplayer_mgr_t *me = (mplayer_mgr_t *)handle;

    INFO("%s[mplayer_exit]\n", MPLAYER_MARK);
    
    FAILED_RETURN(NULL == me);

    if(me->implayer != NULL)
    {
        implayer_destroy(me->implayer);
        me->implayer = NULL;
    }    
    if(me->vod_module != NULL)
    {
        ivod_module_delete(me->vod_module);
        me->vod_module = NULL;
    }
    if(me->implayer_mgr)
    {
        implayer_mgr_delete(me->implayer_mgr);
        me->implayer_mgr = NULL;
    }

    free(s_mplayer_mgr);
    me = NULL;
    s_mplayer_mgr = NULL;
    INFO("%s[mplayer_exit] success\n", MPLAYER_MARK);    
    return;
}

/*****************************************************************
 * @brief 切换当前vod类型以及方式,建议在open url之前都做这个切换
 
 * @param server vod服务器类型,类型名mplayer_server_e
 * @param mode vod播放模式,类型名mplayer_mode_e
 *
 * @return 成功:MPLAYER_OK 失败:MPLAYER_ERR
******************************************************************/
int mplayer_change_server(int server, int mode)
{
    mplayer_mgr_t *me = s_mplayer_mgr;

    FAILED_RETURNX(NULL == me, MPLAYER_ERR);

    INFO("%s[mplayer_change_server] server = %d, mode = %d\n", MPLAYER_MARK, server, mode);

    if(me->vod_module)
    {
        switch(server)
        {
        case MPLAYER_SERVER_ISMA:
            server = IVOD_NAME_ISMA;
            break;
        case MPLAYER_SERVER_DMX:
            server = IVOD_NAME_DMX;
            break;
        case MPLAYER_SERVER_TELECOM:
            server = IVOD_NAME_TELECOM;
            break;
        case MPLAYER_SERVER_CISCO_DMX:
            server = IVOD_NAME_NGOD;
            break;
        case MPLAYER_SERVER_IPANEL:
            server = IVOD_NAME_IPANEL;
            break;
        default:
            server = IVOD_NAME_NONE;
            break;
        }

        if(server > 0)
            ivod_module_set_property(me->vod_module, MODULE_IVOD_PROP_NAME, server);

        switch(mode)
        {
        case MPLAYER_MODE_IP:
            mode = IVOD_MODE_IP;
            break;
        case MPLAYER_MODE_DVB:
            mode = IVOD_MODE_DVB;
            break;
        default:
            mode = IVOD_MODE_DVB;
            break;
        }

        if(mode > 0)
            ivod_module_set_property(me->vod_module, MODULE_IVOD_PROP_MODE, mode);
    }

    INFO("%s[mplayer_change_server] success\n", MPLAYER_MARK);        

    return MPLAYER_OK;
}

/*****************************************************************
 * @brief 打开mplayer播放实例
 
 * @param url 播放路径,rtsp://
 * @param type mplayer播放类型,类型名mplayer_type_e
 * @param cbf 注册的函数指针
 *
 * @return player句柄
******************************************************************/
void *mplayer_open(const char *url, int type, mplayer_cbf cbf)
{
    mplayer_mgr_t *me = s_mplayer_mgr;
    char *str[3] = {"LiveTV", "VOD", "TSTV"};
    
    FAILED_RETURNX(NULL == me || NULL == url, NULL);

    INFO("%s[mplayer_open]\n", MPLAYER_MARK);

    if(me->implayer != NULL)
    {
        implayer_destroy(me->implayer);
        me->implayer = NULL;
    }
    me->implayer = implayer_create(me->implayer_mgr, (implayer_cbf)mplayer_message);
    if(NULL == me->implayer)
    {
        INFO("%s[mplayer_open] calloc implayer failed\n", MPLAYER_MARK);
        return NULL;        
    }
    me->func = cbf;
    
    implayer_open(me->implayer, url, (const char *)str[type-1], 0);

    me->flag_open = 1;
    
    INFO("%s[mplayer_open] success\n", MPLAYER_MARK);

    return me->implayer;
}

/*****************************************************************
 * @brief 关闭mplayer播放实例
 
 * @param player 播放句柄
 *
 * @return 成功:MPLAYER_OK 失败:MPLAYER_ERR
******************************************************************/
int mplayer_close(void *player)
{
    mplayer_mgr_t *me = s_mplayer_mgr;    

    FAILED_RETURNX(NULL == me || player != me->implayer, MPLAYER_ERR);

    INFO("%s[mplayer_close]\n", MPLAYER_MARK);

    if(me->implayer)
    {
        if(me->flag_open)
        {
            implayer_close(me->implayer); 
            me->flag_open = 0;

            usleep(50);
        }
        
        implayer_destroy(me->implayer);
        me->implayer = NULL;
    }
    
    INFO("%s[mplayer_close] success\n", MPLAYER_MARK);
    
    return MPLAYER_OK;
}

/*****************************************************************
 * @brief 停止播放
 
 * @param player 播放句柄
 * @param keep_last_frame 是否保留最后一帧 1:保留最后一帧 0:黑屏
 *
 * @return 成功:MPLAYER_OK 失败:MPLAYER_ERR
******************************************************************/
int mplayer_stop(void *player, int keep_last_frame)
{
    mplayer_mgr_t *me = s_mplayer_mgr;    

    FAILED_RETURNX(NULL == me || player != me->implayer, MPLAYER_ERR);

    INFO("%s[mplayer_stop] keep_last_frame = %d\n", MPLAYER_MARK, keep_last_frame);

    if(me->implayer && me->flag_open)
    {
        //implayer_stop(me->implayer, keep_last_frame);
        implayer_close(me->implayer); 
        me->flag_open = 0;

        usleep(50);
    }
    
    INFO("%s[mplayer_stop] success\n", MPLAYER_MARK);
    
    return MPLAYER_OK;
}

/*****************************************************************
 * @brief 暂停播放
 
 * @param player 播放句柄
 *
 * @return 成功:MPLAYER_OK 失败:MPLAYER_ERR
******************************************************************/
int mplayer_pause(void *player)
{
    mplayer_mgr_t *me = s_mplayer_mgr;    

    FAILED_RETURNX(NULL == me || player != me->implayer, MPLAYER_ERR);

    INFO("%s[mplayer_pause]\n", MPLAYER_MARK);

    if(me->implayer)
    {
        implayer_pause(me->implayer);
    }
    
    INFO("%s[mplayer_pause] success\n", MPLAYER_MARK);
    
    return MPLAYER_OK;
}

/*****************************************************************
 * @brief 指定速率播放
 
 * @param player 播放句柄
 * @param speed 速率
 *
 * @return 成功:MPLAYER_OK 失败:MPLAYER_ERR
******************************************************************/
int mplayer_play(void *player, int speed)
{
    mplayer_mgr_t *me = s_mplayer_mgr;    

    FAILED_RETURNX(NULL == me || player != me->implayer, MPLAYER_ERR);

    INFO("%s[mplayer_play] speed = %d\n", MPLAYER_MARK, speed);

    if(me->implayer)
    {
        implayer_play(me->implayer, speed);
    }
    
    INFO("%s[mplayer_play] success\n", MPLAYER_MARK);
    
    return MPLAYER_OK;
}

/*****************************************************************
 * @brief 指定位置播放
 
 * @param player 播放句柄
 * @param seek_time seek的时间
 *
 * @return 成功:MPLAYER_OK 失败:MPLAYER_ERR
******************************************************************/
int mplayer_seek(void *player, char *seek_time)
{
    mplayer_mgr_t *me = s_mplayer_mgr;    

    FAILED_RETURNX(NULL == me || player != me->implayer || NULL == seek_time, MPLAYER_ERR);

    INFO("%s[mplayer_play] seek_time = %s\n", MPLAYER_MARK, seek_time);

    if(me->implayer)
    {
        implayer_seek(me->implayer, seek_time);
    }
    
    INFO("%s[mplayer_play] success\n", MPLAYER_MARK);
    
    return MPLAYER_OK;
}

/*****************************************************************
 * @brief 获取属性信息
 
 * @param player 播放句柄
 * @param prop 属性类型,mplayer_prop_e
 * @param value 获取的值
 *
 * @return 成功:MPLAYER_OK 失败:MPLAYER_ERR
******************************************************************/
int mplayer_get_property(void *player, int prop, void *value)
{
    mplayer_mgr_t *me = s_mplayer_mgr; 
    int ret = MPLAYER_ERR;
    
    FAILED_RETURNX(NULL == me || player != me->implayer, MPLAYER_ERR);

    INFO("%s[mplayer_get_property] prop = %d\n", MPLAYER_MARK, prop);

    switch(prop)
    {
        case MPLAYER_PROP_TYPE:
            ret = implayer_get_property(me->implayer, IMPLAYER_PROP_TYPE, value);
            break;
    	case MPLAYER_PROP_URL:
            ret = implayer_get_property(me->implayer, IMPLAYER_PROP_URL, value);
            break;
    	case MPLAYER_PROP_DURATION:
            {
                int duration = 0;

                ret = implayer_get_property(me->implayer, IMPLAYER_PROP_DURATION, &duration);
                INFO("%s[mplayer_get_property] IMPLAYER_PROP_DURATION = %d\n", MPLAYER_MARK, duration);
                if(value != NULL)
                {
                    sprintf((char *)value, "%d", duration);
                }
    	    }
            break;
    	case MPLAYER_PROP_SPEED:
            {
                if(value)
                {
                    int speed = 0;
                    ret = implayer_get_property(me->implayer, IMPLAYER_PROP_CURRENT_SPEED, &speed);
                    sprintf((char *)value, "%d", speed);
                }
        	}
            break;
    	case MPLAYER_PROP_STATUS:
            ret = implayer_get_property(me->implayer, IMPLAYER_PROP_STATUS, value);
            break;
    	case MPLAYER_PROP_ELAPSED:
            {
                if(value)
                {
                    unsigned int current_time = 0;
                    ret = implayer_get_property(me->implayer, IMPLAYER_PROP_CURRENT_TIME, &current_time);
                    sprintf((char *)value, "%d", current_time);
                }
        	}
            break;
    	case MPLAYER_PROP_TSTV_STARTTIME:
            ret = implayer_get_property(me->implayer, IMPLAYER_PROP_START_TIME, value);
            break;
    	case MPLAYER_PROP_TSTV_PRESENTTIME:
            ret = implayer_get_property(me->implayer, IMPLAYER_PROP_CURRENT_TIME, value);
            break;
    	case MPLAYER_PROP_TSTV_ENDTIME:
            ret = implayer_get_property(me->implayer, IMPLAYER_PROP_END_TIME, value);
            break;
        case MPLAYER_PROP_PROGRESS:
            break;
    	case MPLAYER_PROP_TSTV_URL:
            ret = implayer_get_property(me->implayer, IMPLAYER_PROP_TSTV_URL, value);
            break;
        default:
            break;
    }
  
    return MPLAYER_OK;
}

/*****************************************************************
 * @brief 设置属性信息
 
 * @param prop 属性类型,mplayer_prop_e
 * @param value 设置的值
 *
 * @return 成功:MPLAYER_OK 失败:MPLAYER_ERR
******************************************************************/
int mplayer_set_property(void *player, int prop, int value)
{
    return MPLAYER_OK;
}

/*这两个函数并不是公开给安卓上层用的*/
int ipanel_dvb_play(mplayer_media_t *media)
{
    mplayer_mgr_t *me = s_mplayer_mgr;        
    int event = -1;
    
    FAILED_RETURNX(NULL == me, MPLAYER_ERR); 
    
    me->func(me->implayer, MPLAYER_SET_MEDIA_INFO, (int)media, 0);

    return MPLAYER_OK;
}

int ipanel_dvb_stop_play(void)
{
    mplayer_mgr_t *me = s_mplayer_mgr;        
    int event = -1;
    
    FAILED_RETURNX(NULL == me, MPLAYER_ERR); 
    
    me->func(me->implayer, MPLAYER_STOP_MEDIA, 0, 0);

    return MPLAYER_OK;    
}



