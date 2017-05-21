/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:中间件多实例封装的实现
*********************************************************************/
#include "iplayer_main.h"
#include "iplayer_vod_main.h"
#include "iplayer_player.h"


/*逻辑TRUE*/
#define MPLAYER_TRUE (1)
/*逻辑FALSE*/
#define MPLAYER_FALSE (0)

/*最大MPLAYER实例个数*/
#define MAX_MPLAYER_NUM (8)
#define MPLAYER_MARK    "[IMPLAYER]"

#define IMPLAYER_MGR_LOCK(me) if(me) ipanel_porting_sem_wait(me->sem, IPANEL_WAIT_FOREVER)
#define IMPLAYER_MGR_UNLOCK(me) if(me) ipanel_porting_sem_release(me->sem)

/*mplayer 实例结构*/
struct implayer_s
{
	void *previous_level_handle;/*上一层句柄(例如:上一层是js调用，消息回调的时候，需要把该js实例句柄传递给回调函数)*/
    void *player_handle;    
	implayer_cbf msg_func;
    unsigned int flag_used:1;
    unsigned int flag_paused:1;
    int type;
};

/*mplayer 管理器结构*/
struct implayer_mgr_s
{
	implayer_t mplayer_list[MAX_MPLAYER_NUM];
    unsigned int       sem;
    iplayer_mgr_t       *player_mgr;
};

typedef enum
{
    IMPLAYER_TYPE_NONE = 0,
    IMPLAYER_TYPE_LOCAL_AV,
    IMPLAYER_TYPE_VOD,
    IMPLAYER_TYPE_THIRD
}implayer_type_e;

implayer_mgr_t *g_mplayer_mgr = NULL;
qos_cbf g_qos_cbf = NULL;

int implayer_qos_proc(void *option, int event, int p1, int p2)
{
    if(g_qos_cbf)
      return  g_qos_cbf(option,event,p1,p2);

    return IPANEL_OK;
}


/*mplayer注册给下层的回调函数*/
static int implayer_msg_func(void *handle, unsigned int msg, int p1, int p2)
{
    implayer_t *me = (implayer_t *)handle;

    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    if(me->msg_func)
        me->msg_func(me->previous_level_handle, msg, p1, p2);

	 implayer_qos_proc("proc",VOD_QOS_EVENT_ERROR,p1,p2);

	return IPANEL_OK;
}

/*根据type来初始化mplayer*/
static int implayer_init(implayer_t *me, int type)
{
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    /*原来已经打开过*/
    if(me->player_handle)
    {
        if(me->type != type)
        {
            /*销毁原有client*/
            switch(me->type)
            {
                case IMPLAYER_TYPE_LOCAL_AV:
                    break;
                case IMPLAYER_TYPE_VOD:
                    ivod_player_destroy(me->player_handle);
                    break;
                case IMPLAYER_TYPE_THIRD:
                    break;
                default:
                    break;
            }            
            me->player_handle = NULL;
            me->type = type;
            /*创建新client*/
            switch(type)
            {
                case IMPLAYER_TYPE_LOCAL_AV:
                    break;
                case IMPLAYER_TYPE_VOD:
                    me->player_handle = ivod_player_create(me, implayer_msg_func);
                    break;
                case IMPLAYER_TYPE_THIRD:
                    break;
                default:
                    break;
            }            
        }        
    }
    else
    {
         me->type = type;
        /*创建新client*/
        switch(type)
        {
            case IMPLAYER_TYPE_LOCAL_AV:
                break;
            case IMPLAYER_TYPE_VOD:
                me->player_handle = ivod_player_create(me, implayer_msg_func);
                break;
            case IMPLAYER_TYPE_THIRD:
                break;
            default:
                break;
        }          
    }
    return IPANEL_OK;
}

static int implayer_uninit(implayer_t *me)
{
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    
    if(me->player_handle)
    {
        switch(me->type)
        {
            case IMPLAYER_TYPE_LOCAL_AV:
                break;
            case IMPLAYER_TYPE_VOD:
                ivod_player_destroy(me->player_handle);
                break;
            case IMPLAYER_TYPE_THIRD:
                break;
            default:
                break;
        }            
    }
    return IPANEL_OK;
}

/*获取全局mplayer管理器*/
void *implayer_get_mgr_handle(void)
{
	return g_mplayer_mgr;
}

/*创建mplayer管理器*/
implayer_mgr_t *implayer_mgr_new(void)
{
    implayer_mgr_t *me = NULL;
    
    INFO("%s[implayer_mgr_new] start\n", MPLAYER_MARK);
    
	me = (implayer_mgr_t *)calloc(1, sizeof(implayer_mgr_t));
    if(NULL == me)
    {
        INFO("%s[implayer_mgr_new] calloc failed\n", MPLAYER_MARK);
        goto FAILED;
    }
    me->sem = ipanel_porting_sem_create("MPLY", 1, IPANEL_TASK_WAIT_FIFO);
    if(me->sem <= 0)
    {
        INFO("%s[implayer_mgr_new] sem create failed\n", MPLAYER_MARK);
        goto FAILED;
    }

    me->player_mgr = iplayer_mgr_new();
    if(NULL == me->player_mgr)
    {
        INFO("%s[implayer_mgr_new] player mgr create failed\n", MPLAYER_MARK);
    }
    
	g_mplayer_mgr = me;
    
    INFO("%s[implayer_mgr_new] success\n", MPLAYER_MARK);
    
    return me;
    
FAILED:
    if(me)
    {
        if(me->sem > 0)
        {
            ipanel_porting_sem_destroy(me->sem);
            me->sem = 0;
        }
        free(me);
        me = NULL;
    }    
    return NULL;
}

/*销毁mplayer管理器*/
void implayer_mgr_delete(implayer_mgr_t *me)
{
	int index = 0;
	implayer_t *instance = NULL;

    FAILED_RETURN(NULL == me);
    
    INFO("%s[implayer_mgr_delete]\n", MPLAYER_MARK);
    
	for(index = 0; index < MAX_MPLAYER_NUM; index++)
	{
		instance = &(me->mplayer_list[index]);
		if(instance->flag_used == 1) 
        {
			implayer_destroy(instance);
		}
	}
    
    if(me->player_mgr > 0)
    {
        iplayer_mgr_destory(me->player_mgr);
    }

    if(me->sem > 0)
	{
        ipanel_porting_sem_destroy(me->sem);
        me->sem = 0;
    }
    
    free(me);
    me = NULL;
    
    INFO("%s[implayer_mgr_delete] success\n", MPLAYER_MARK);

    return ;
}

/*创建一个mplayer实例*/
implayer_t *implayer_create(void *handle, implayer_cbf cbf)
{
	int index = 0;
	implayer_mgr_t *me = g_mplayer_mgr;
	implayer_t *instance = NULL;
	
	FAILED_RETURNX(NULL == me, NULL);
    
    INFO("%s[implayer_create] start\n", MPLAYER_MARK);
    
	for(index = 0; index < MAX_MPLAYER_NUM; index++)
	{
		if(me->mplayer_list[index].flag_used == 0)
		{
			instance = &me->mplayer_list[index];
			break;
		}
	}
	if(NULL == instance)
    	return NULL;

    IMPLAYER_MGR_LOCK(me);
    instance->flag_used = MPLAYER_TRUE;
	instance->msg_func = cbf;
	instance->previous_level_handle = handle;
    IMPLAYER_MGR_UNLOCK(me);
    
    INFO("%s[implayer_create] success\n", MPLAYER_MARK);
    
	return instance;
}

/*销毁一个mplayer实例*/
void implayer_destroy(implayer_t *me)
{
    INFO("%s[implayer_destroy]start\n", MPLAYER_MARK);
    
    if(me)
    {
        implayer_uninit(me);
        me->player_handle = NULL;
    	me->flag_paused = MPLAYER_FALSE;
    	me->flag_used = MPLAYER_FALSE;
        me->type = IMPLAYER_TYPE_NONE;
    	me->msg_func = NULL;
    }
    
    INFO("%s[implayer_destroy] success\n", MPLAYER_MARK);

    return;
}

/*打开*/
int implayer_open(implayer_t *me, const char *src, const char *type, int is_third)
{
	int ret = IPANEL_ERR, mplayer_type = 0;

	FAILED_RETURNX(NULL == me, IPANEL_ERR);

    INFO("%s[implayer_open] src = %s, type = %s, is_third = %d\n", MPLAYER_MARK, src, type, is_third);
    
	if(is_third)
	{
        mplayer_type = IMPLAYER_TYPE_THIRD;
	}
    else if(strncasecmp(src, "file://", 7) == 0
    		|| strncasecmp(src, "http://", 7) == 0
    		|| strncasecmp(src, "ui://", 5) == 0
    		|| strncasecmp(src, "mp3://", 6) == 0)
	{
        mplayer_type = IMPLAYER_TYPE_LOCAL_AV;
	}
	
	if(strncasecmp(src, "rtsp://", 7) == 0
		|| strncasecmp(src, "rtcp://", 7) == 0
		|| strncasecmp(src, "rtmp://", 7) == 0
		|| strncasecmp(src, "ssp://", 7) == 0
		|| strncasecmp(src, "igmp://", 7) == 0
		|| strncasecmp(src, "p2p://", 7) == 0)
	{
        mplayer_type = IMPLAYER_TYPE_VOD;
	}
    /*open前的初始化*/
    implayer_init(me, mplayer_type);
    switch(me->type)
    {
        case IMPLAYER_TYPE_LOCAL_AV:
            break;
        case IMPLAYER_TYPE_VOD:
            ret = ivod_player_open(me->player_handle, src, type);
            break;
        case IMPLAYER_TYPE_THIRD:
            break;
        default:
            break;
    }   
    
    INFO("%s[implayer_open] success\n", MPLAYER_MARK);
    
    return ret;
}

/*关闭*/
int implayer_close(implayer_t *me)
{
	int ret = IPANEL_ERR;
    
	FAILED_RETURNX(NULL == me,IPANEL_ERR);
    
    INFO("%s[implayer_close]\n", MPLAYER_MARK);

    switch(me->type)
    {
        case IMPLAYER_TYPE_LOCAL_AV:
            break;
        case IMPLAYER_TYPE_VOD:
            ret = ivod_player_close(me->player_handle);
            break;
        case IMPLAYER_TYPE_THIRD:
            break;
        default:
            break;
    }	

    INFO("%s[implayer_close]success\n", MPLAYER_MARK);

    return ret;
}

/*停止*/
int implayer_stop(implayer_t *me, int keep_last_frame)
{
	int ret = IPANEL_ERR;

    FAILED_RETURNX(NULL == me,IPANEL_ERR);

    INFO("%s[implayer_stop] keep_last_frame = %d\n", MPLAYER_MARK, keep_last_frame);
    
    switch(me->type)
    {
        case IMPLAYER_TYPE_LOCAL_AV:
            break;
        case IMPLAYER_TYPE_VOD:
            ret = ivod_player_stop(me->player_handle, keep_last_frame);
            break;
        case IMPLAYER_TYPE_THIRD:
            break;
        default:
            break;
    }	   	

    INFO("%s[implayer_stop]success\n", MPLAYER_MARK);

    return ret;
}

/*暂停*/
int implayer_pause(implayer_t *me)
{
	int ret = IPANEL_ERR;

    FAILED_RETURNX(NULL == me,IPANEL_ERR);

    INFO("%s[implayer_pause]\n", MPLAYER_MARK);

    switch(me->type)
    {
        case IMPLAYER_TYPE_LOCAL_AV:
            break;
        case IMPLAYER_TYPE_VOD:
            ret = ivod_player_pause(me->player_handle);
            break;
        case IMPLAYER_TYPE_THIRD:
            break;
        default:
            break;
    }	 
    
    INFO("%s[implayer_pause]success\n", MPLAYER_MARK);

    return ret;
}

/*播放、快进、快退*/
int implayer_play(implayer_t *me, int speed)
{	
	int ret = IPANEL_ERR;

    FAILED_RETURNX(NULL == me,IPANEL_ERR);

    INFO("%s[implayer_play]\n", MPLAYER_MARK);

    switch(me->type)
    {
        case IMPLAYER_TYPE_LOCAL_AV:
            break;
        case IMPLAYER_TYPE_VOD:
            ret = ivod_player_play(me->player_handle, speed);
            break;
        case IMPLAYER_TYPE_THIRD:
            break;
        default:
            break;
    }	   	

    INFO("%s[implayer_play]success\n", MPLAYER_MARK);    

    return ret;
}

/*SEEK*/
int implayer_seek(implayer_t *me, char *seek_time)
{
	int ret = IPANEL_ERR;

    FAILED_RETURNX(NULL == me,IPANEL_ERR);

    INFO("%s[implayer_seek] seek_time = %s\n", MPLAYER_MARK, seek_time);

    switch(me->type)
    {
        case IMPLAYER_TYPE_LOCAL_AV:
            break;
        case IMPLAYER_TYPE_VOD:
            ret = ivod_player_seek(me->player_handle, seek_time);
            break;
        case IMPLAYER_TYPE_THIRD:
            break;
        default:
            break;
    }	   	

    INFO("%s[implayer_seek]\n", MPLAYER_MARK);    

    return ret;
}

/*获取参数*/
int implayer_get_property(implayer_t *me, int prop, void *value)
{
    int ret = IPANEL_ERR;

    FAILED_RETURNX(NULL == me,IPANEL_ERR);

    INFO("%s[implayer_get_property]\n", MPLAYER_MARK);

    switch(me->type)
    {
        case IMPLAYER_TYPE_LOCAL_AV:
            break;
        case IMPLAYER_TYPE_VOD:
            ret = ivod_player_get_property(me->player_handle, prop, value);
            break;
        case IMPLAYER_TYPE_THIRD:
            break;
        default:
            break;
    }	
    
    INFO("%s[implayer_get_property] success\n", MPLAYER_MARK);

    return ret;
}

/*设置参数*/
int implayer_set_property(implayer_t *me, int prop, int value)
{
    int ret = IPANEL_ERR;

    FAILED_RETURNX(NULL == me,IPANEL_ERR);

    if(IMPLAYER_PROP_SET_QOS_FUNC == prop)
    {
        if(g_qos_cbf == NULL)
		{
			INFO("%s[implayer_set_property]set qos function success\n", MPLAYER_MARK);
            g_qos_cbf = (qos_cbf)value;
		}
        return IPANEL_OK;
    }

    switch(me->type)
    {
        case IMPLAYER_TYPE_LOCAL_AV:
            break;
        case IMPLAYER_TYPE_VOD:
            ret = ivod_player_set_property(me->player_handle, prop, value);
            break;
        case IMPLAYER_TYPE_THIRD:
            break;
        default:
            break;
    }	   	

    return ret;
}

/************************************End Of File**********************************/


