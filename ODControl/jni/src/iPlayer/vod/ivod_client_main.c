/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:CLIENT管理
*********************************************************************/
#include "ivod_client_main.h"
#include "ivod_rtsp_client.h"
#include "ivod_igmp_client.h"
#include "ivod_client_time.h"
#include "iplayer_main.h"
#include "iplayer_player.h"

/****************************************
一个client线程管理多个client实例
而在client实例内部又可能会创建多个client
****************************************/
#define MAX_CLIENT_NUM  (8)
#define MAX_URL_LEN     (2048)
#define MAX_TIME_LEN    (64)
#define MAX_COMMAND_EVENT_NUM   (16)


#define CLIENT_MARK "[IVOD][CLIENT]"
#define CLIENT_MGR_LOCK(me) if(me) ipanel_porting_sem_wait(me->sem, IPANEL_WAIT_FOREVER)
#define CLIENT_MGR_UNLOCK(me) if(me) ipanel_porting_sem_release(me->sem)
#define CLIENT_LOCK(me) if(me) ipanel_porting_sem_wait(me->client_sem, IPANEL_WAIT_FOREVER)
#define CLIENT_UNLOCK(me) if(me) ipanel_porting_sem_release(me->client_sem)

static client_mgr_t *s_client_mgr = NULL;

/*client 消息处理工作状态*/
typedef enum
{
    CLIENT_EVENT_PROC_IDLE = 0,
    CLIENT_EVENT_PROC_BUSY,
}client_event_proc_status_e;

/*client mgr线程工作状态*/
typedef enum
{
    CLIENT_THREAD_STATE_IDLE   = 0,
    CLIENT_THREAD_STATE_WORKING,        
    CLIENT_THREAD_STATE_STOP
}client_thread_state_e;

/*event结构*/
typedef struct client_event_s
{
    unsigned int event_name;
    char url[MAX_URL_LEN];  
    char type[16];    
    int speed;
    int keep_last_frame;
    char seek_time[MAX_TIME_LEN];
}client_event_t;

/*event队列结构管理*/
typedef struct client_event_mgr_s
{
    client_event_t *event_mem;
    unsigned int event_num;
    unsigned int event_front;
    unsigned int event_rear;
}client_event_mgr_t;

struct client_info_s
{
    char url[MAX_URL_LEN];
    char live_url[MAX_URL_LEN];
    char tstv_url[MAX_URL_LEN];
    IPAddr0 ip[1];
    int port;
    int speed;
    int type;
    int status;
    int player_id;
    int bind_type;
    client_time_t time_info[1];
};

/*client结构*/
struct client_s
{
    unsigned int        client_sem;
    void                *msg_handle;
    ivod_msg_func       cbf;
    client_event_mgr_t  *event_mgr;
    int                 cur_type;
    unsigned int        cur_event_name;    
    int                 client_proc_status;  
    client_info_t       client_info[1];
    iplayer_rect        client_video_location[1];    //视频窗口位置
    int                 client_audio_volume;         //媒体音量
    unsigned int        flag_set_location:1;         //是否设置窗口位置，主要用于播放器打开之后判断
    unsigned int        flag_set_volume:1;           //是否设置音量，主要用于播放器打开之后判断
    unsigned int        flag_used:1;
    unsigned int        flag_live_to_tstv:1;
    unsigned int        flag_tstv_to_live:1;    
    struct client_s     *next;    
    unsigned int        client_handle[MAX_IVOD_TYPE_NUM];
    unsigned int        (*client_open[MAX_IVOD_TYPE_NUM])(struct client_s *me, ivod_msg_func func);
    int                 (*client_proc[MAX_IVOD_TYPE_NUM])(unsigned int handle, unsigned int event, int p1, int p2);
    int                 (*client_close[MAX_IVOD_TYPE_NUM])(unsigned int handle);
    int                 (*client_set_prop[MAX_IVOD_TYPE_NUM])(unsigned int handle, int prop, int value);
    int                 (*client_get_prop[MAX_IVOD_TYPE_NUM])(unsigned int handle, int prop, void *value);
};

/*client链表结构管理*/
struct client_mgr_s
{
    unsigned int thread_fd;
    unsigned int sem;
    client_t *client_mem;
    client_t *client_head;
    client_thread_state_e thread_state;
};

static client_event_mgr_t *client_event_create(int num);
static int client_event_destroy(client_event_mgr_t *me);
static int client_event_post(client_event_mgr_t *me, client_event_t *event);
static client_event_t *client_event_read(client_event_mgr_t *me);
static int client_event_clear(client_event_mgr_t *me);
static int client_proc_event(client_t *me, client_event_t *event);
static void client_mgr_proc(void *param);
static int client_info_set_property(client_info_t *me, int prop, int value);
static int client_info_get_property(client_info_t *me, int prop, void *value);

static void client_callback_result(client_t *me, unsigned int event, int value);
static int client_callback_message(client_t *me, unsigned int msg, int value);
static int client_callback_get(client_t *me, unsigned int prop, void *value);

/*创建client管理器*/
client_mgr_t *client_mgr_new(void)
{
    client_mgr_t *me = NULL;
    
    INFO("%s[client_mgr_new]\n", CLIENT_MARK);
    /*分配mgr内存*/
    me = (client_mgr_t *)calloc(1, sizeof(client_mgr_t));
    if(NULL == me)
    {
        INFO("%s[client_mgr_new]calloc client mgr failed\n", CLIENT_MARK);
        goto FAILED;
    }
    
    memset(me,0,sizeof(client_mgr_t));
    me->thread_state = CLIENT_THREAD_STATE_IDLE;

    
    /*分配client总内存*/
    me->client_mem = (client_t *)calloc(1, MAX_CLIENT_NUM*sizeof(client_t));
    if(NULL == me->client_mem)
    {
        INFO("%s[client_mgr_new]calloc client mem failed\n", CLIENT_MARK);
        goto FAILED;
    }
    /*创建信号量*/
    me->sem = ipanel_porting_sem_create("CINT", 1, IPANEL_TASK_WAIT_FIFO);
    if(me->sem <= 0)
    {
        INFO("%s[client_mgr_new]sem create failed\n", CLIENT_MARK);
        goto FAILED;        
    }
    /*创建线程*/
    me->thread_fd = ipanel_porting_task_create("CINT", client_mgr_proc, me, 8, 64*1024);
    if(me->thread_fd <= 0)
    {
        INFO("%s[client_mgr_new]task create failed\n", CLIENT_MARK);
        goto FAILED;        
    }
    me->thread_state = CLIENT_THREAD_STATE_WORKING;
    s_client_mgr = me;

    INFO("%s[client_mgr_new] success\n", CLIENT_MARK);

    return me;
    
 FAILED:
    if(me)
    {
        if(me->thread_fd > 0)
        {
            ipanel_porting_task_destroy(me->thread_fd);
            me->thread_fd = 0;
        }
        if(me->sem > 0)
        {
            ipanel_porting_sem_destroy(me->sem);
            me->sem = 0;
        }
        if(me->client_mem)
        {
            free(me->client_mem);
            me->client_mem = NULL;
        }
        free(me);
        me = NULL;
    }
    s_client_mgr = NULL;
    
    return NULL;    
}

/*销毁client管理器*/
void client_mgr_delete(client_mgr_t *handle)
{
    client_mgr_t *me = handle;
    client_t *client = NULL;
    FAILED_RETURN(NULL == me);
    

    INFO("%s[client_mgr_delete]client mgr delete start(thread_state = %d)!!\n", CLIENT_MARK,me->thread_state);

    if( CLIENT_THREAD_STATE_WORKING == me->thread_state )
    {
        me->thread_state = CLIENT_THREAD_STATE_IDLE;
        
        while(CLIENT_THREAD_STATE_STOP!= me->thread_state)
        {
            /*等待client线程结束*/
            ipanel_porting_task_sleep(3);
        }
    }

    INFO("%s[client_mgr_delete]destory client thread!!\n", CLIENT_MARK);
    if(me->thread_fd > 0)
    {
        ipanel_porting_task_destroy(me->thread_fd);
        me->thread_fd = 0;
    }

    /*销毁链表中的client*/
    client = me->client_head;
    while(client)
    {
        if(client->flag_used)
        {
            client_destroy(client);
        }
        else
        {
            /*异常情况直接退出*/
            INFO("%s[client_mgr_delete]client list happened exception!!\n", CLIENT_MARK);
            break;
        }
        client = me->client_head;
    }
    
    INFO("%s[client_mgr_delete]free client mem!!\n", CLIENT_MARK);
    if(me->client_mem)
    {
        free(me->client_mem);
        me->client_mem = NULL;
    }
    
    if(me->sem > 0)
    {
        ipanel_porting_sem_destroy(me->sem);
        me->sem = 0;
    }
    
    if(me)
    {
        free(me);
    }
    s_client_mgr = NULL;

    INFO("%s[client_mgr_delete] success\n", CLIENT_MARK);
    
    return;
}


/*client mgr 线程*/
static void client_mgr_proc(void *param)
{
    client_mgr_t *me = (client_mgr_t *)param;
    client_t *client = NULL;
    client_event_t *event = NULL;
    client_event_t  timer_event[1] = {0};
    
    FAILED_RETURN(NULL == me);

    timer_event->event_name = CLIENT_EVENT_TIMER;
    me->thread_state = CLIENT_THREAD_STATE_WORKING;
    
    while(CLIENT_THREAD_STATE_WORKING == me->thread_state)
    {
        CLIENT_MGR_LOCK(me);
        /*遍历client链*/
        client = me->client_head;
        if(NULL == client)
            goto SLEEP;
        /*client命令执行*/
        while(client)
        {
            /*client消息处理*/
            CLIENT_LOCK(client);

            if(client->client_proc_status == CLIENT_EVENT_PROC_IDLE)
            {
                /*空闲的时候从event队列里面取消息执行*/
                event = client_event_read(client->event_mgr);
                if(event)
                {
                    client->cur_event_name = event->event_name;
                    client->client_proc_status = CLIENT_EVENT_PROC_BUSY;
                    client_proc_event(client, event);          
                }
                else
                {                    
                    /*TIMER 消息*/
                    client_proc_event(client, timer_event);                            
                }
            }
            else
            {                
                /*TIMER 消息*/
                client_proc_event(client, timer_event);                
            }
            
            CLIENT_UNLOCK(client);
            /*下一个client*/
            client = client->next;
        }        
     SLEEP:
        CLIENT_MGR_UNLOCK(me);
        ipanel_porting_task_sleep(10);
    } 
    
    INFO("%s[client_mgr_proc] client thread stop working\n", CLIENT_MARK);
    me->thread_state = CLIENT_THREAD_STATE_STOP;
    
    return ;
}


/*创建client实例*/
client_t *client_create(void *handle, ivod_msg_func cbf)
{
    client_mgr_t *me = s_client_mgr;
    client_t *client = NULL;
    int index = 0;
    char buf[16] = {0};
    
    FAILED_RETURNX(NULL == me || NULL == me->client_mem, NULL);

    INFO("%s[client_create] start(time:%d)\n", CLIENT_MARK,time_ms());

    CLIENT_MGR_LOCK(me);
    client = (client_t *)me->client_mem;
    /*查询*/
    for(index = 0; index < MAX_CLIENT_NUM; index ++)
    {
        if(!client->flag_used)
        {
            memset(client, 0, sizeof(client_t));
            /*创建sem*/
            sprintf(buf, "CL%02d", index);
            client->client_sem = ipanel_porting_sem_create(buf, 1, IPANEL_TASK_WAIT_FIFO);
            if(client->client_sem <= 0)
                goto FAILED;
            /*分配event队列*/
            client->event_mgr = client_event_create(MAX_COMMAND_EVENT_NUM);
            if(NULL == client->event_mgr)
                goto FAILED;
            /*回调函数*/
            client->msg_handle = handle;
            client->cbf = cbf;
            client->flag_used = 1;
            /*添加到client链表里面*/
            if(NULL == me->client_head)
            {
                /*head为空,client即为head*/
                me->client_head = client;
            }
            else
            {
                /*head不为空,添加到尾端*/
                client_t *ptr = me->client_head;
                while(ptr->next)
                    ptr = ptr->next;
                ptr->next = client;
            }
			break;
        }
        client ++;
    }
    if(index == MAX_CLIENT_NUM)
    {
        client = NULL;
        goto FAILED;
    }
    
    CLIENT_MGR_UNLOCK(me);
    
    INFO("%s[client_create] success\n", CLIENT_MARK);

    return client;
    
FAILED:
    INFO("%s[client_create] failed\n", CLIENT_MARK);
    if(client)
    {
        if(client->event_mgr)
        {
            client_event_destroy(client->event_mgr);
            client->event_mgr = NULL;
        }

        if(client->client_sem)
        {
            ipanel_porting_sem_destroy(client->client_sem);
            client->client_sem = 0;
        }
        
        memset(client, 0, sizeof(client_t));
    }
    CLIENT_MGR_UNLOCK(me);
    return NULL;
}

/*销毁client实例*/
int client_destroy(client_t *handle)
{
    client_mgr_t *me = s_client_mgr;
    client_t *client = handle;
    client_t *ptr = NULL;
    
    FAILED_RETURNX(NULL == me || NULL == me->client_mem || NULL == me->client_head || NULL == client, IPANEL_ERR);
    
    INFO("%s[client_destroy] start(time:%d)\n", CLIENT_MARK,time_ms());

    CLIENT_MGR_LOCK(me);
    
    /*从链表删除*/
    ptr = me->client_head;

    if(ptr == client)
    {
        me->client_head = client->next;
        INFO("%s[client_destroy] client_head is client\n", CLIENT_MARK);
    }
    else
    {
        while(ptr->next != client)
            ptr = ptr->next;

        if(ptr->next == client)
        {
            INFO("%s[client_destroy] find client in client_head\n", CLIENT_MARK);
            ptr->next = client->next;
        }
    }

    /*销毁client*/
    if(client)
    {
        if(client->event_mgr)
        {
            client_event_destroy(client->event_mgr);
            client->event_mgr = NULL;
        }
       
        if(client->client_sem)
        {
            ipanel_porting_sem_destroy(client->client_sem);
            client->client_sem = 0;
        }

        //如果播放器还没有关闭需要关闭
        if(client->client_info->player_id)
            iplayer_close(client->client_info->player_id);
        
        
        memset(client, 0, sizeof(client_t));
        client = NULL;
    }
    
    CLIENT_MGR_UNLOCK(me);
    INFO("%s[client_destroy] success\n", CLIENT_MARK);
    
    return IPANEL_ERR;
}

/*对外接口:往client里面发送消息*/
int client_proc(client_t *me, client_event_name_e event_name, int p1, int p2)
{
    client_event_t event[1] = {0};
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    
    INFO("%s[client_proc] event_name = %d\n", CLIENT_MARK, event_name);

    event->event_name = event_name;
    /*各种event的分别处理*/
    switch(event_name)
    {
        case CLIENT_EVENT_OPEN:
            {
                char *url = (char *)p1;
                char *type = (char *)p2;
                if(url)
                    strncpy(event->url, url, sizeof(event->url));
                if(type)
                    strncpy(event->type, type, sizeof(event->type));
                
                client_info_set_property(me->client_info, CLIENT_INFO_STATUS, CLIENT_STATUS_OPEN);
            }
            break;
        case CLIENT_EVENT_PLAY:
            break;
        case CLIENT_EVENT_SEEK:
            {
                if(p1)
                {
                    strcpy(event->seek_time,(char*)p1);
                }
            }
            break;
        case CLIENT_EVENT_PAUSE:
            break;
        case CLIENT_EVENT_RESUME:
            break;
        case CLIENT_EVENT_FORWARD:
            {
                if(p1)
                {
                    event->speed = p1;
                }
            }
            break;
        case CLIENT_EVENT_BACKWARD:
            {
                if(p1)
                {
                    event->speed = p1;
                }
            }
            break;
        case CLIENT_EVENT_STOP:
            break;
        case CLIENT_EVENT_CLOSE:
            me->cur_event_name = event_name;
            break;
        default:
            break;
    }
    /*post*/
    CLIENT_LOCK(me);

    if(CLIENT_EVENT_CLOSE == event_name)
    {
        int i = 0;

        //线程状态改成忙碌
        me->cur_event_name = event->event_name;
        me->client_proc_status = CLIENT_EVENT_PROC_BUSY;        
        
        //close消息立即清空消息队列，执行close操作
        client_event_clear(me->event_mgr);

        for(i=0;i<MAX_IVOD_TYPE_NUM;i++)
        {
            if(me->client_handle[i])
            {
                me->cur_type = i;
                me->client_proc[i](me->client_handle[i], CLIENT_EVENT_CLOSE, 0, 0);
            }
        }
    }
    else
    {
        client_event_post(me->event_mgr, event);
    }
    CLIENT_UNLOCK(me);

    INFO("%s[client_proc] success\n", CLIENT_MARK);
    
    return IPANEL_OK;
}

/*对外提供的设置属性接口-需要保证线程安全*/
int client_set_property(client_t *me, int prop, int value)
{
   
    FAILED_RETURNX(NULL == me || NULL == me->client_info, IPANEL_ERR);

    INFO("%s[client_set_property] prop = %d\n", CLIENT_MARK, prop);

    CLIENT_LOCK(me);
    /*主要分两部分*/
    /*全局info信息-调用client_info_set_property*/
    /*其它属性设置-调用各client的set_property:me->client_set_prop*/

    switch(prop)
    {
	case IMPLAYER_PROP_TSTV_URL:
        client_info_set_property(me->client_info, CLIENT_INFO_TSTV_URL, value);
        break;
    case IMPLAYER_PROP_AUDIO_SET_CURR_TRACK:
        {
            //切换音轨直接设置到播放器
            int player_id = me->client_info->player_id;
            if(player_id)
                iplayer_set_prop(player_id, IPLAYER_PARAM_AUDIO_CHANGE_TRACK_ID, value);
            
        }
        break;
	case IMPLAYER_PROP_KEEP_LAST_FRAME:
        {
            //设置是否保留最后一帧，直接设置到播放器
        }
        break;
    case IMPLAYER_PROP_MINI_WINDOW: 
        {
            //是否小视屏播放，直接设置到播放器
        }
        break;
    case IMPLAYER_PROP_AUDIO_MODE:
        {
            //设置声道模式(左声道、右声道等)，直接设置到播放器
            int player_id = me->client_info->player_id;
            if(player_id)
                iplayer_set_prop(player_id, IPLAYER_PARAM_AUDIO_MODE, value);
        }
        break;
    case IMPLAYER_PROP_MUTE:
        {
            //静音直接设置到播放器
            int player_id = me->client_info->player_id;
            if(player_id)
                iplayer_set_prop(player_id, IPLAYER_PARAM_MUTE, value);
        }
        break;
    case IMPLAYER_PROP_VOLUME:
        {
            //音量直接设置到播放器
            int player_id = me->client_info->player_id;
            if(player_id)
                iplayer_set_prop(player_id, IPLAYER_PARAM_VOLUME, value);
            else if(value >=0 && value <=100)
            {
                //播放器还未创建，保存音量，等播放器创建好了再设置
                me->client_audio_volume = value;
                me->flag_set_volume = 1;
            }
        }
        break;
    case IMPLAYER_PROP_WIN_LOCATION:
        {
            //视频位置直接设置到播放器
            implayer_rect *rect = (implayer_rect*)value;            
            int player_id = me->client_info->player_id;

            if(rect)
            {
                me->client_video_location->x = rect->x;
                me->client_video_location->y = rect->y;
                me->client_video_location->w = rect->w;
                me->client_video_location->h = rect->h;               
            }
            
            if(player_id && rect)
            {                
                iplayer_set_prop(player_id, IPLAYER_PARAM_WIN_LOCATION, (int)me->client_video_location);
                me->flag_set_location = 0;
            }
            else if(!player_id && rect)
            {
                //播放器还没有创建，暂存视频窗口位置，播放器打开时再设置
                me->flag_set_location = 1;
            }
        }
        break;
	case IMPLAYER_PROP_SUBTITLE_SWITCH_LANG:
        {
            //切换字幕直接设置到播放器
            int player_id = me->client_info->player_id;
            if(player_id)
                iplayer_set_prop(player_id, IPLAYER_PARAM_SUBTITLE_SWITCH_LANG, value);
            
        }
        break;
    default:
        me->client_set_prop[me->cur_type](me->client_handle[me->cur_type],prop,value);
        break;
    }
    
    CLIENT_UNLOCK(me);

    INFO("%s[client_set_property] success\n", CLIENT_MARK);    

    return IPANEL_OK;
}

/*对外提供的获取属性接口-需要保证线程安全*/
int client_get_property(client_t *me, int prop, void *value)
{
    FAILED_RETURNX(NULL == me || NULL == me->client_info, IPANEL_ERR);

    INFO("%s[client_get_property] prop = %d\n", CLIENT_MARK, prop);

    CLIENT_LOCK(me);
    /*主要分两部分*/
    /*全局info信息->调用client_info_set_property*/
    /*其它属性设置,调用各client的get_property:me->client_get_prop*/
    switch(prop)
    {
	case IMPLAYER_PROP_STATUS:
        client_info_get_property(me->client_info, CLIENT_INFO_STATUS, value);        
        break;
	case IMPLAYER_PROP_TYPE:
        client_info_get_property(me->client_info, CLIENT_INFO_TYPE, value);        
        break;
	case IMPLAYER_PROP_CURRENT_SPEED:
        client_info_get_property(me->client_info, CLIENT_INFO_SPEED, value);        
        break;
	case IMPLAYER_PROP_REQUEST_SPEED:
        break;
	case IMPLAYER_PROP_URL:
        client_info_get_property(me->client_info, CLIENT_INFO_URL, value);        
        break;
	case IMPLAYER_PROP_TSTV_URL:
        client_info_get_property(me->client_info, CLIENT_INFO_TSTV_URL, value);        
        break;
	case IMPLAYER_PROP_DURATION:
        client_info_get_property(me->client_info, CLIENT_INFO_DURATION, value);        
        break;
	case IMPLAYER_PROP_CURRENT_TIME:
        client_info_get_property(me->client_info, CLIENT_INFO_CURRENT_TIME, value);        
        break;
	case IMPLAYER_PROP_START_TIME:
        client_info_get_property(me->client_info, CLIENT_INFO_START_TIME, value);        
        break;
	case IMPLAYER_PROP_END_TIME:
        client_info_get_property(me->client_info, CLIENT_INFO_END_TIME, value);        
        break;
    case IMPLAYER_PROP_AUDIO_TRACKS:
        {
            //直接从播放器获取
            int player_id = me->client_info->player_id;
            if(player_id)
                iplayer_get_prop(player_id, IPLAYER_PARAM_AUDIO_TRACKS, (int)value);
            
        }        
        break;
	case IMPLAYER_PROP_AUDIO_GET_CURR_TRACK:
        {
            //直接从播放器获取
            int player_id = me->client_info->player_id;
            if(player_id)
                iplayer_get_prop(player_id, IPLAYER_PARAM_AUDIO_TRACK_ID, (int)value);            
        }        
        break;
	case IMPLAYER_PROP_AUDIO_GET_TRACK_SUPPORTING:
        {
            //直接从播放器获取
        }        
        break;
    case IMPLAYER_PROP_MINI_WINDOW:
        {
            //直接从播放器获取
        }
        break;
    case IMPLAYER_PROP_SUBTITLE_GET_LANGUAGES:
        {
            //直接从播放器获取
            int player_id = me->client_info->player_id;
            if(player_id)
                iplayer_get_prop(player_id, IPLAYER_PARAM_SUBTITLE_GET_LANGUAGES, (int)value);
            
        }        
        break;
	case IMPLAYER_PROP_SUBTITLE_GET_CURR_LANG:
        {
            //直接从播放器获取
            int player_id = me->client_info->player_id;
            if(player_id)
                iplayer_get_prop(player_id, IPLAYER_PARAM_SUBTITLE_GET_CURR_LANG, (int)value);            
        }        
        break;
    case IMPLAYER_PROP_AUDIO_PTS:
        {
            /*用视频pts代替*/
            int player_id = me->client_info->player_id;
            if(player_id)
                iplayer_get_prop(player_id, IPLAYER_PARAM_VIDEO_PTS, (int)value);                        
        }
        break;
    default:
        me->client_get_prop[me->cur_type](me->client_handle[me->cur_type],prop,value);
        break;
    }

    
    CLIENT_UNLOCK(me);

    INFO("%s[client_get_property] success\n", CLIENT_MARK);
   
    return IPANEL_OK;
}
/*对外提供给socket线程使用*/
int client_proc_socket(client_t *me, client_event_name_e event, int p1, int p2)
{
    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    //INFO("%s[client_proc_socket] event = %d\n", CLIENT_MARK, event);

    if(event == CLIENT_EVENT_SOCKET_MEDIA)
    {
        int player_id = me->client_info->player_id;
        /*直接发送给player*/
        if(player_id)
            iplayer_append_data(player_id, p1);
    }
    else
    {
        /*如果正在执行关闭操作,可以不处理socket应答*/
        if(CLIENT_EVENT_CLOSE == me->cur_event_name)
            return IPANEL_OK;
        
        /*区分处理*/
        CLIENT_LOCK(me);
        /*暂时先通知到cur type,不满足要求再考虑将socket id也在client main中维护*/
        me->client_proc[me->cur_type](me->client_handle[me->cur_type], event, p1, p2);
        CLIENT_UNLOCK(me);
    }

    //INFO("%s[client_proc_socket] success\n", CLIENT_MARK);
    
    return IPANEL_OK;
}
/*对外提供给socket线程使用*/
imem_node_t *client_get_unused_block(client_t *me, int size)
{
    FAILED_RETURNX(NULL == me, NULL);

    //INFO("%s[client_get_unused_block] size = %d\n", CLIENT_MARK, size);

    if(me->client_info->player_id)
        return iplayer_get_unused_block(me->client_info->player_id, size);
    else
        return NULL;
}


/*创建event队列*/
static client_event_mgr_t *client_event_create(int num)
{
    client_event_mgr_t *me = NULL;
    
    FAILED_RETURNX(num == 0, NULL);

    INFO("%s[client_event_create] num = %d\n", CLIENT_MARK, num);

    me = (client_event_mgr_t *)calloc(1, sizeof(client_event_mgr_t));
    if(NULL == me)
    {
        INFO("%s[client_event_mgr_t]create event mgr failed\n", CLIENT_MARK);
        goto FAILED;
    }

    me->event_mem = (client_event_t *)calloc(1, num*sizeof(client_event_t));
    if(NULL == me)
    {
        INFO("%s[client_event_mgr_t]create event mem failed\n", CLIENT_MARK);
        goto FAILED;
    }
    me->event_num = num;
    me->event_front = 0;
    me->event_rear = 0;

    INFO("%s[client_event_create] success\n", CLIENT_MARK);

    return me;    
FAILED:
    if(me)
    {
        if(me->event_mem)
        {
            free(me->event_mem);
            me->event_mem = NULL;
        }
        free(me);
    }

    INFO("%s[client_event_create] failed\n", CLIENT_MARK);
    
    return NULL;
}

/*销毁event队列*/
static int client_event_destroy(client_event_mgr_t *me)
{
    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    INFO("%s[client_event_destroy]\n", CLIENT_MARK);

    if(me)
    {
        if(me->event_mem)
        {
            free(me->event_mem);
            me->event_mem = NULL;
        }
        free(me);
    }   

    INFO("%s[client_event_destroy] success\n", CLIENT_MARK);

    return IPANEL_OK;
}

/*post event*/
static int client_event_post(client_event_mgr_t *me, client_event_t *event)
{
    FAILED_RETURNX(NULL == me || NULL == event || 0 == me->event_num, IPANEL_ERR);

    INFO("%s[client_event_post]\n", CLIENT_MARK);
   
	if((me->event_rear + 1)%me->event_num == me->event_front)
	{
		INFO("%s[client_event_post] post failed\n", CLIENT_MARK);
		me->event_front = me->event_rear = 0;
	}
    
    memcpy(me->event_mem + me->event_rear, event, sizeof(client_event_t));
	me->event_rear = (me->event_rear + 1)%me->event_num;

    INFO("%s[client_event_post] success\n", CLIENT_MARK);

    return IPANEL_OK;
    
}

/*read event*/
static client_event_t *client_event_read(client_event_mgr_t *me)
{
    client_event_t *event = NULL;

    FAILED_RETURNX(NULL == me || 0 == me->event_num, NULL);

    if(me->event_rear == me->event_front)
        return NULL;

    INFO("%s[client_event_read]\n", CLIENT_MARK);

    event = me->event_mem + me->event_front;
    me->event_front = (me->event_front + 1)%me->event_num;
    
    INFO("%s[client_event_read] success\n", CLIENT_MARK);

    return event;
}

/*clear event*/
static int client_event_clear(client_event_mgr_t *me)
{
	FAILED_RETURNX(NULL == me || 0 == me->event_num, IPANEL_ERR);

    INFO("%s[client_event_clear]\n", CLIENT_MARK);

    me->event_front = me->event_rear = 0;

    INFO("%s[client_event_clear] success\n", CLIENT_MARK);    

    return IPANEL_OK;
}

static int client_info_set_property(client_info_t *me, int prop, int value)
{
    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    INFO("%s[client_info_set_property] prop = %d\n", CLIENT_MARK, prop);

    switch(prop)
    {
        case CLIENT_INFO_URL:
            if(value)
            {
                memset(me->url,0,sizeof(me->url));
                strncpy(me->url, (char *)value, sizeof(me->url));
            }
            break;
        case CLIENT_INFO_LIVE_URL:
            if(value)
            {
                memset(me->live_url,0,sizeof(me->live_url));
                strncpy(me->live_url, (char *)value, sizeof(me->live_url));
            }
            break;
        case CLIENT_INFO_TSTV_URL:
            if(value)
            {
                memset(me->tstv_url,0,sizeof(me->tstv_url));
                strncpy(me->tstv_url, (char *)value, sizeof(me->tstv_url));
            }
            break;
        case CLIENT_INFO_IP:
            if(value)
                memcpy(me->ip,(IPAddr0*)value,sizeof(IPAddr0));
            break;
        case CLIENT_INFO_PORT:
            if(value)
                me->port = value;
            break;
        case CLIENT_INFO_SPEED:
            if(value)
                me->speed = value;
            break;
        case CLIENT_INFO_TYPE:
            if(value)
                me->type = value;
            break;
        case CLIENT_INFO_STATUS:
            if(value)
                me->status = value;
            break;
        case CLIENT_INFO_PLAYER_ID:
            if(value >= 0)
                me->player_id = value;
            break;
        case CLIENT_INFO_TIME_INFO:
            if(value)
            {
                memcpy(me->time_info,(client_time_t*)value,sizeof(client_time_t));
            }
            break;
        case CLIENT_INFO_BIND_TYPE:
            if(value)
            {
                if(me->bind_type == 0)
                    me->bind_type = 1;                
                me->bind_type |= value;
            }
            break;
        default:
            break;
    }

    INFO("%s[client_info_set_property] success\n", CLIENT_MARK);    

    return IPANEL_OK;
}

/*全局获取info信息,本线程使用,不需LOCK*/
static int client_info_get_property(client_info_t *me, int prop, void *value)
{
    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    INFO("%s[client_info_get_property] prop = %d\n", CLIENT_MARK, prop);    
    
    switch(prop)
    {
        case CLIENT_INFO_URL:
            if(value)
            {
                strcpy((char*)value ,me->url);
            }
            break;
        case CLIENT_INFO_LIVE_URL:
            if(value)
            {
                strcpy((char*)value ,me->live_url);
            }
            break;
        case CLIENT_INFO_TSTV_URL:
            if(value)
            {
                strcpy((char*)value ,me->tstv_url);
            }
            break;
        case CLIENT_INFO_IP:
            if(value)
                *(IPAddr0**)value = me->ip;
            break;
        case CLIENT_INFO_PORT:
            if(value)
                *(int *)value = me->port;
            break;
        case CLIENT_INFO_SPEED:
            if(value)
            {
                if(CLIENT_STATUS_PAUSE == me->status)
                    *(int*)value = 0;
                else
                    *(int*)value = me->speed;
            }
            break;
        case CLIENT_INFO_TYPE:
            if(value)
                *(int *)value = me->type;
            break;
        case CLIENT_INFO_STATUS:
            if(value)
            {
                switch(me->status)
                {
                case CLIENT_STATUS_OPEN:
        			strcpy((char*)value, "open");
        			break;                    
                case CLIENT_STATUS_NORMAL:
        			strcpy((char*)value, "play");
        			break;
                case CLIENT_STATUS_BACKWARD:
        			strcpy((char*)value,"backward");
        			break;
                case CLIENT_STATUS_FORWARD:
        			strcpy((char*)value,"forward");
        			break;                    
                case CLIENT_STATUS_PAUSE:
        			strcpy((char*)value,"pause");
        			break;                    
                case CLIENT_STATUS_CLOSED:
        			strcpy((char*)value,"closed");
        			break;                    
                case CLIENT_STATUS_STOP: 
        			strcpy((char*)value,"stop");
        			break;
        		default:
        			strcpy((char*)value,"none");
        			break;                    
                }
                INFO("%s[client_info_get_property] status = %s\n", CLIENT_MARK, (char*)value);    
            }
            break;
        case CLIENT_INFO_PLAYER_ID:
            if(value)
                *(int *)value = me->player_id;
            break;
        case CLIENT_INFO_TIME_INFO:
            if(value)
            {
                memcpy((client_time_t*)value,me->time_info,sizeof(client_time_t));
            }
            break;            
        case CLIENT_INFO_BIND_TYPE:
            if(value)
                *(int *)value = me->bind_type;
            break;
        case CLIENT_INFO_DURATION:
            if(value)
            {
                *(int *)value = me->time_info->duration;
            }
            break;
        case CLIENT_INFO_CURRENT_TIME:
            if(value)
            {
                unsigned int video_pts = 0;
                unsigned int current_time = 0;
                UTC_time_t utc_time[1] = {0};                

                if(me->time_info->flag_support_pts)
                {
                    //先获取pts,并更新pts
                    if(me->player_id)
                        iplayer_get_prop(me->player_id, IPLAYER_PARAM_VIDEO_PTS, (int)&video_pts); 

                    if(video_pts > 0)
                    {
                        if(me->time_info->base_pts == 0)
                            me->time_info->base_pts = video_pts;
                        me->time_info->current_pts = video_pts;
                    }
                }

                //计算当前时间
                client_time_calculate_current_time(me->time_info, me->speed, &current_time);

                *(unsigned int*)value = current_time;
            #if 0
                if(CLIENT_TIME_TYPE_NPT == me->time_info->time_type)
                {
                    sprintf((char*)value,"%d",current_time);
                }
                else if(CLIENT_TIME_TYPE_UTC == me->time_info->time_type)
                {
                    client_time_seconds_to_UTC(current_time, utc_time);
                    client_time_UTC_time_print(utc_time, (char*)value);
                }
                INFO("%s[client_info_get_property] current_time=%s\n", CLIENT_MARK, (char*)value); 
            #endif
            }
            break;
        case CLIENT_INFO_START_TIME:
            if(value)
            {
                *(unsigned int*)value = me->time_info->start_time;
            #if 0
                UTC_time_t utc_time[1] = {0};                
                if(CLIENT_TIME_TYPE_NPT == me->time_info->time_type)
                {
                    sprintf((char*)value,"%d",me->time_info->start_time);
                }
                else if(CLIENT_TIME_TYPE_UTC == me->time_info->time_type)
                {
                    client_time_seconds_to_UTC(me->time_info->start_time, utc_time);
                    client_time_UTC_time_print(utc_time, (char*)value);
                }
                INFO("%s[client_info_get_property] start_time=%s\n", CLIENT_MARK, (char*)value);
            #endif
            }
            break;
        case CLIENT_INFO_END_TIME:
            if(value)
            {
                *(unsigned int*)value = me->time_info->end_time;
            #if 0
                UTC_time_t utc_time[1] = {0};                
                if(CLIENT_TIME_TYPE_NPT == me->time_info->time_type)
                {
                    sprintf((char*)value,"%d",me->time_info->end_time);
                }
                else if(CLIENT_TIME_TYPE_UTC == me->time_info->time_type)
                {
                    client_time_seconds_to_UTC(me->time_info->end_time, utc_time);
                    client_time_UTC_time_print(utc_time, (char*)value);
                }
                INFO("%s[client_info_get_property] current_time=%s\n", CLIENT_MARK, (char*)value); 
            #endif
            }
            break;
        default:
            break;
    }

    INFO("%s[client_info_get_property] success\n", CLIENT_MARK);    
    
    return IPANEL_OK;
}

/*client消息处理*/
static int client_proc_event(client_t *me, client_event_t *event)
{
    int type = 0;
	vod_qos_info_t qos_info[1] = {0};
    
    FAILED_RETURNX(NULL == me || NULL == event, IPANEL_ERR);

    //INFO("%s[client_proc_event] \n", CLIENT_MARK);

    switch(event->event_name)
    {
        case CLIENT_EVENT_OPEN:        
            if(strncmp(event->type, "LiveTV", strlen("LiveTV")) == 0)
            {
                client_info_set_property(me->client_info, CLIENT_INFO_LIVE_URL, (int)event->url);
                type = IVOD_TYPE_LiveTV;                
            }
            else if(strncmp(event->type, "TSTV", strlen("TSTV")) == 0)
            {
                //client_info_set_property(me->client_info, CLIENT_INFO_TSTV_URL, (int)event->url);                
                type = IVOD_TYPE_TSTV;
            }
            else
                type = IVOD_TYPE_VOD;

            /*如果已经存在了client,要先close掉*/
            if(me->client_handle[type] && me->client_close[type])
                me->client_close[type](me->client_handle[type]);

            INFO("%s[client_proc_event]open url=%s(time:%d)\n", CLIENT_MARK,event->url,time_ms());
            
            if(strncmp(event->url, "igmp://", strlen("igmp://")) == 0
               || strncmp(event->url, "udp://", strlen("udp://")) == 0)
            {
                me->client_open[type] = ivod_igmp_open;
                me->client_proc[type] = ivod_igmp_proc;
                me->client_close[type] = ivod_igmp_close;
                me->client_set_prop[type] = ivod_igmp_set_prop;
                me->client_get_prop[type] = ivod_igmp_get_prop;

                /*初始化QOS*/
        		qos_info->flag_multicast = 1;
        		qos_info->flag_mini_window = 0;
        		qos_info->url = event->url;

                if(IVOD_TYPE_VOD == type)
            		qos_info->type = 8;
                else if(IVOD_TYPE_TSTV == type)
            		qos_info->type = 9;
                else if(IVOD_TYPE_LiveTV)
            		qos_info->type = 10;
                
        		implayer_qos_proc("proc", VOD_QOS_EVENT_OPEN, (int32)qos_info,0);
                implayer_qos_proc("logmsg", (int)event->url, (int)"igmp", 1);
                
            }
            else if(strncmp(event->url, "rtsp://", strlen("rtsp://")) == 0)
            {
                me->client_open[type] = ivod_rtsp_open;
                me->client_proc[type] = ivod_rtsp_proc;
                me->client_close[type] = ivod_rtsp_close;
                me->client_set_prop[type] = ivod_rtsp_set_prop;
                me->client_get_prop[type] = ivod_rtsp_get_prop;

                /*初始化QOS*/
        		qos_info->flag_multicast = 0;
        		qos_info->flag_mini_window = 0;
        		qos_info->url = event->url;

                if(IVOD_TYPE_VOD == type)
            		qos_info->type = 8;
                else if(IVOD_TYPE_TSTV == type)
            		qos_info->type = 9;
                else if(IVOD_TYPE_LiveTV)
            		qos_info->type = 10;
                
        		implayer_qos_proc("proc", VOD_QOS_EVENT_OPEN, (int32)qos_info,0);                
            }
        #if 0
            else if(strncmp(event->url, "rtmp://", strlen("rtmp://")) == 0)
            {
                me->client_open[type] = rtmp_open;
                me->client_proc[type] = rtmp_proc;
                me->client_close[type] = rtmp_close;
                me->client_set_prop[type] = rtmp_set_prop;
                me->client_get_prop[type] = rtmp_get_prop;
            }
            else if(strncmp(event->url, "dsmcc://", strlen("dsmcc://")) == 0
                || strncmp(event->url, "ssp://", strlen("ssp://")) == 0
                || strncmp(event->url, "dmx://", strlen("dmx://")) == 0)
            {
                me->client_open[type] = dmx_open;
                me->client_proc[type] = dmx_proc;
                me->client_close[type] = dmx_close;
                me->client_set_prop[type] = dmx_set_prop;
                me->client_get_prop[type] = dmx_get_prop;
            }
            else if(strncmp(event->url, "pptv://", strlen("pptv://")) == 0)
            {
                me->client_open[type] = p2p_open;
                me->client_proc[type] = p2p_proc;
                me->client_close[type] = p2p_close;
                me->client_set_prop[type] = p2p_set_prop;
                me->client_get_prop[type] = p2p_get_prop;
            }
        #endif
            else
            {
                INFO("%s[client_proc_event]open failed,unknown protocol\n", CLIENT_MARK);
                break;
            }      
            /*open句柄*/ 
            me->client_handle[type] = me->client_open[type](me, client_callback_proc);

            /*打开媒体播放*/
            if(me->client_handle[type])
                me->client_proc[type](me->client_handle[type], CLIENT_EVENT_OPEN, (int)event->url, type);
            else
            {
                INFO("%s[client_proc_event] client open failed\n", CLIENT_MARK);
                break;
            }            
            me->cur_type = type;
            client_info_set_property(me->client_info, CLIENT_INFO_URL, (int)event->url);
            /*如果不是切换中的话设置type,切换中要等切换结果回调上来才能设置*/
            if(!me->flag_live_to_tstv && !me->flag_tstv_to_live)
                client_info_set_property(me->client_info, CLIENT_INFO_TYPE, type);
            break;
        case CLIENT_EVENT_PLAY:  
        case CLIENT_EVENT_PAUSE:
        case CLIENT_EVENT_RESUME:
        case CLIENT_EVENT_CLOSE:
            /*首先关闭qos*/
            if(CLIENT_EVENT_CLOSE == event->event_name)
    		    implayer_qos_proc("proc",VOD_QOS_EVENT_CLOSE,0,0);
            
            if(me->client_handle[me->cur_type])
                me->client_proc[me->cur_type](me->client_handle[me->cur_type], event->event_name, 0, 0);
            break;
        case CLIENT_EVENT_SEEK:
            if(me->client_handle[me->cur_type])
                me->client_proc[me->cur_type](me->client_handle[me->cur_type], CLIENT_EVENT_SEEK, (int)event->seek_time, 0);
            break;
        case CLIENT_EVENT_FORWARD:
        case CLIENT_EVENT_BACKWARD:
            if(me->client_handle[me->cur_type])
                me->client_proc[me->cur_type](me->client_handle[me->cur_type], event->event_name, event->speed, 0);          
            break;
        case CLIENT_EVENT_STOP:
            if(me->client_handle[me->cur_type])
                me->client_proc[me->cur_type](me->client_handle[me->cur_type], CLIENT_EVENT_STOP, event->keep_last_frame, 0);            
            break;
        case CLIENT_EVENT_TIMER: 
            {
                unsigned int video_pts = 0;

                //更新初始pts
                if(me->client_info->time_info->flag_support_pts && me->client_info->time_info->base_pts == 0)
                {
                    if(me->client_info->player_id)
                        iplayer_get_prop(me->client_info->player_id, IPLAYER_PARAM_VIDEO_PTS, (int)&video_pts); 
                    if(video_pts > 0)
                        me->client_info->time_info->base_pts = video_pts;
                }
                
                if(me->client_handle[me->cur_type])
                    me->client_proc[me->cur_type](me->client_handle[me->cur_type], CLIENT_EVENT_TIMER, 0, 0);
            }
            break;
        default:
            break;
    }

    //INFO("%s[client_proc_event] success\n", CLIENT_MARK);

    return IPANEL_OK;
}

/*以下是回调函数 提供给下层使用*/
int client_callback_proc(void *handle, unsigned int event,int p1, int p2)
{
    client_t *me = (client_t*)handle;
    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    switch(event)
    {
        case CLIENT_CALLBACK_TYPE_GET:
            client_callback_get(me, p1, (void*)p2);
            break;
        case CLIENT_CALLBACK_TYPE_MSG:
            client_callback_message(me, p1, p2);
            break;
        case CLIENT_CALLBACK_TYPE_RESULT:
            client_callback_result(me, p1, p2);
            break;
        default:
            break;
    }

    //以下为播放器回调的消息
    switch(event)
    {
        case EIS_VOD_PLAY_ABNORMAL:
            {
                if(p1 == 2)
                {
                    //连接服务器之后就没有接收到数据流,需要区分igmp，还是rtsp
                    if(strncmp(me->client_info->url, "rtsp://", strlen("rtsp://")) == 0)
                        me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_RTSP_CMD_ERROR, 4);
                    else if(strncmp(me->client_info->url, "igmp://", strlen("igmp://")) == 0)
                        me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_MEMBERSHIP_ERROR, 1);
                    me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_CONNECT_FAILED, 0);
                }
                else if(p1 == 3)
                {
                    //播放过程中严重断流
                    me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_PLAY_ABNORMAL, p1);
                    me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_PLAY_FAILED, 0);
                }
                else
                {
                    me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_PLAY_FAILED, 0);
                }
            }
            break;
        case EIS_VOD_PLAY_ABNORMAL_RESUME:
            me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_PLAY_SUCCESS, 0);
            break;
        default:
            break;
    }

    return IPANEL_OK;
}


static void client_callback_result(client_t *me, unsigned int event, int value)
{
    FAILED_RETURN(NULL == me);

    INFO("%s[client_callback_result] event = %d, value = %d\n", CLIENT_MARK, event, value);      

    /*value:>=0 表示执行成功 value:-1 表示执行失败*/
    switch(event/*me->cur_event_name*/)
    {
        case CLIENT_EVENT_OPEN:
            /*判断是否成功-修改状态 - 回调5202等消息*/                        
            if(value >= 0 )
            {
                /*切换成功，修改状态*/
                if(me->flag_live_to_tstv)
                {
                    /*时移打开成功*/
                    client_info_set_property(me->client_info, CLIENT_INFO_TYPE, IVOD_TYPE_TSTV);
                    /*解绑player*/
                    client_info_set_property(me->client_info, CLIENT_INFO_BIND_TYPE, ~ BIND_TYPE(IVOD_TYPE_LiveTV));
                    /*关闭直播*/
                    if(me->client_handle[IVOD_TYPE_LiveTV])
                    {
                        me->client_proc[IVOD_TYPE_LiveTV](me->client_handle[IVOD_TYPE_LiveTV], CLIENT_EVENT_CLOSE, 0, 0);
                        me->client_close[IVOD_TYPE_LiveTV](me->client_handle[IVOD_TYPE_LiveTV]);
                        me->client_handle[IVOD_TYPE_LiveTV] = 0;
                    }
                    else
                    {
                        INFO("%s[client_callback_result] LiveTV already closed\n", CLIENT_MARK);
                    }
                    me->flag_live_to_tstv = 0;
                    me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_PREPAREPLAY_SUCCESS, 0);
                }
                else if(me->flag_tstv_to_live)
                {
                    /*直播打开成功*/
                    client_info_set_property(me->client_info, CLIENT_INFO_TYPE, IVOD_TYPE_LiveTV);                    
                    /*解绑player*/
                    client_info_set_property(me->client_info, CLIENT_INFO_BIND_TYPE, ~ BIND_TYPE(IVOD_TYPE_TSTV));
                    /*关闭时移*/                    
                    if(me->client_handle[IVOD_TYPE_TSTV])
                    {
                        me->client_proc[IVOD_TYPE_TSTV](me->client_handle[IVOD_TYPE_TSTV], CLIENT_EVENT_CLOSE, 0, 0);                        
                        me->client_close[IVOD_TYPE_TSTV](me->client_handle[IVOD_TYPE_TSTV]);
                        me->client_handle[IVOD_TYPE_TSTV] = 0;
                    }
                    else
                    {
                        INFO("%s[client_callback_result] TSTV already closed\n", CLIENT_MARK);
                    }            
                    me->flag_tstv_to_live = 0;
                    me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_PREPAREPLAY_SUCCESS, 0);
                }
                else
                {
                    /*正常OPEN*/
                    client_info_set_property(me->client_info, CLIENT_INFO_STATUS, CLIENT_STATUS_OPEN);
                    me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_PREPAREPLAY_SUCCESS, 0);
                }
            }
            else if(value == -991)
            {
                /*-991表示组播播放地址错误*/
                me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_MEMBERSHIP_ERROR, 1);                            
            }
            else if(value == -996)
            {
                /*-996表示RTSP地址错误，或连接服务器失败*/
                me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_RTSP_CMD_ERROR, 4);

                /*如果刚在直播转时移过程中发生了错误，需要把时移关闭*/
                if(me->flag_live_to_tstv)
                {
                    //清空消息队列
                    client_event_clear(me->event_mgr);

                    //关闭时移
                    if(me->client_handle[IVOD_TYPE_TSTV])
                    {
                        me->client_close[IVOD_TYPE_TSTV](me->client_handle[IVOD_TYPE_TSTV]);
                        me->client_handle[IVOD_TYPE_TSTV] = 0;
                    }                

                    //还原相关参数为直播状态
                    {
                        char live_url[2048] = {0};
                        client_info_get_property(me->client_info, CLIENT_INFO_LIVE_URL, (void *)live_url);
                        client_info_set_property(me->client_info, CLIENT_INFO_URL, (int)live_url);
                    }
                    me->cur_type = IVOD_TYPE_LiveTV;
                    me->flag_live_to_tstv = 0;
                }
                
            }
            else
            {
                me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_CONNECT_FAILED, 0);                            
            }
            break;
        case CLIENT_EVENT_PLAY:
        case CLIENT_EVENT_SEEK:
        case CLIENT_EVENT_RESUME:
            if(value >= 0)
            {
                client_info_set_property(me->client_info, CLIENT_INFO_STATUS, CLIENT_STATUS_NORMAL);
                client_info_set_property(me->client_info, CLIENT_INFO_SPEED, 1);
                me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_PLAY_SUCCESS, 0);
            }
            else
            {
                me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_PLAY_FAILED, 0);
            }
            break;
        case CLIENT_EVENT_PAUSE:
            if(value >= 0)
            {
                client_info_set_property(me->client_info, CLIENT_INFO_STATUS, CLIENT_STATUS_PAUSE);
                //client_info_set_property(me->client_info, CLIENT_INFO_SPEED, 0);
                me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_PLAY_SUCCESS, 0);
            }
            else
            {
                me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_PLAY_FAILED, 0);
            }
            break;    
        case CLIENT_EVENT_FORWARD:
            if(value >= 0)
            {
                client_info_set_property(me->client_info, CLIENT_INFO_STATUS, CLIENT_STATUS_FORWARD);
                client_info_set_property(me->client_info, CLIENT_INFO_SPEED, value);
                me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_PLAY_SUCCESS, 0);
            }
            else
            {
                me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_PLAY_FAILED, 0);
            }
            break;
        case CLIENT_EVENT_BACKWARD:
            if(value >= 0)
            {
                client_info_set_property(me->client_info, CLIENT_INFO_STATUS, CLIENT_STATUS_BACKWARD);
                client_info_set_property(me->client_info, CLIENT_INFO_SPEED, value*(-1));
                me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_PLAY_SUCCESS, 0);
            }
            else
            {
                me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_PLAY_FAILED, 0);
            }                       
            break;
        case CLIENT_EVENT_STOP:
            if(value >= 0)
            {
                client_info_set_property(me->client_info, CLIENT_INFO_STATUS, CLIENT_STATUS_STOP);
                client_info_set_property(me->client_info, CLIENT_INFO_SPEED, 0);
                me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_PLAY_SUCCESS, 0);
            }
            else
            {
                me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_PLAY_FAILED, 0);
            }                               
            break;
        case CLIENT_EVENT_CLOSE:
            /*判断是否成功-修改状态 调用close*/
            if(value >= 0)
            {
                client_info_set_property(me->client_info, CLIENT_INFO_STATUS, CLIENT_STATUS_CLOSED);
                client_info_set_property(me->client_info, CLIENT_INFO_SPEED, 0);                
                me->client_close[me->cur_type](me->client_handle[me->cur_type]);
                me->client_handle[me->cur_type] = 0;
                me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_RELEASE_SUCCESS, 0);
            }
            else
            {
                me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_RELEASE_FAILED, 0);
            }            
            break;
        case CLIENT_EVENT_ANNOUNCE:
            {
                if ( value == 2102 ) 
                {
                    //播放到头，发送5209给页面
                	me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV,EIS_VOD_PROGRAM_BEGIN, 0);
                } 
                else if ( value == 2101 ) 
                {
                    //播放到尾，发送5210给页面
                	me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV,EIS_VOD_PROGRAM_END, 0);
                } 
                else 
                {
                	me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV,EIS_VOD_USER_EXCEPTION, value);
                }                
            }
            break;
        case CLIENT_EVENT_ERROR:
            /*播放过程中发生错误，直接发送错误消息给页面,关闭播放*/
            if(value == -995)
            {
                /*发送RTSP命令失败*/
                me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_RTSP_CMD_ERROR, 2);                            
            }
            else
            {                
                me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_USER_EXCEPTION, value);
            }

            /*如果刚在直播转时移过程中发生了错误，需要把时移关闭*/
            if(me->flag_live_to_tstv)
            {
                //清空消息队列
                client_event_clear(me->event_mgr);

                //关闭时移
                if(me->client_handle[IVOD_TYPE_TSTV])
                {
                    me->client_close[IVOD_TYPE_TSTV](me->client_handle[IVOD_TYPE_TSTV]);
                    me->client_handle[IVOD_TYPE_TSTV] = 0;
                }                

                //还原相关参数为直播状态
                {
                    char live_url[2048] = {0};
                    client_info_get_property(me->client_info, CLIENT_INFO_LIVE_URL, (void *)live_url);
                    client_info_set_property(me->client_info, CLIENT_INFO_URL, (int)live_url);

                    //如果是组播转时移，时移播放不了，还需要发送5252消息
                    if(strncmp(live_url, "igmp://", strlen("igmp://")) == 0 && value > 400)
                        me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_MEMBERSHIP_ERROR, 2);                            
                }
                me->cur_type = IVOD_TYPE_LiveTV;
                me->flag_live_to_tstv = 0;
            }

            break;
        default:
            break;
    }
    
    me->client_proc_status = CLIENT_EVENT_PROC_IDLE;
    me->cur_event_name = CLIENT_EVENT_NONE; 

    INFO("%s[client_callback_result] success\n", CLIENT_MARK);
}

static int client_callback_message(client_t *me, unsigned int msg, int value)
{
    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    INFO("%s[client_callback_message] msg = %d, value = %d\n", CLIENT_MARK, msg, value);

    switch(msg)
    {
        case CLIENT_MSG_PLAYER_OPEN_SUCCESS:    /*打开解码器成功*/
            {
                client_info_set_property(me->client_info, CLIENT_INFO_PLAYER_ID, value);

                //检查是否要设置视频窗口位置
                if(me->flag_set_location && value)
                {
                    iplayer_set_prop(value, IPLAYER_PARAM_WIN_LOCATION, (int)me->client_video_location);
                    me->flag_set_location = 0;
                }

                //检查是否设置媒体音量
                if(me->flag_set_volume && value)
                {
                    iplayer_set_prop(value, IPLAYER_PARAM_VOLUME, me->client_audio_volume);
                    me->flag_set_volume = 0;
                }
            }
            break;
        case CLIENT_MSG_PLAYER_OPEN_FAILED:     /*打开解码器失败*/            
            break;
        case CLIENT_MSG_PLAYER_CLOSE_SUCCESS:   /*关闭解码器成功*/
            client_info_set_property(me->client_info, CLIENT_INFO_PLAYER_ID, 0);
            break;
        case CLIENT_MSG_PLAYER_CLOSE_FAILED:    /*关闭解码器失败*/     
            break;
        case CLIENT_MSG_PLAYER_BIND:            /*绑定解码器*/
            client_info_set_property(me->client_info, CLIENT_INFO_BIND_TYPE, value);
            break;
        case CLIENT_MSG_PLAYER_UNBIND:          /*解绑定解码器*/
            break;
        case CLIENT_MSG_LIVE_TO_TSTV:           /*直播切时移*/
            {                
                client_event_t event[1] = {0};
                char tstv_url[2048] = {0};

                /*获取时移url*/
                client_info_get_property(me->client_info, CLIENT_INFO_TSTV_URL, (void *)tstv_url);
                if(0 == strlen(tstv_url))
                {
                    INFO("%s[client_message] CLIENT_MSG_LIVE_TO_TSTV failed, tstv url is null\n",CLIENT_MARK);
                    break;
                }
                /*先做一个简单的清除所有event/实际应用的时候可以考虑从头开始插入*/
                client_event_clear(me->event_mgr);
                /*open tstv*/  
                event->event_name = CLIENT_EVENT_OPEN;
                strcpy(event->type, "TSTV");
                strcpy(event->url, tstv_url);
                client_event_post(me->event_mgr, event);

                /*暂停、或者快退*/
                event->event_name = me->cur_event_name;
                if(me->cur_event_name == CLIENT_EVENT_BACKWARD)
                    event->speed = -2;
                else if(me->cur_event_name == CLIENT_EVENT_SEEK)
                    strcpy(event->seek_time,(char*)value);
                
                client_event_post(me->event_mgr, event);

                /*直播切时移*/
                me->flag_live_to_tstv = 1;
                /*线程状态改成空闲*/
                me->client_proc_status = CLIENT_EVENT_PROC_IDLE;
                
            }
            break;
        case CLIENT_MSG_TSTV_TO_LIVE:           /*时移切直播*/
            {
                client_event_t event[1] = {0};
                char live_url[2048] = {0};

                /*获取直播url*/
                client_info_get_property(me->client_info, CLIENT_INFO_LIVE_URL, (void *)live_url);
                if(0 == strlen(live_url))
                {
                    INFO("%s[client_message] CLIENT_MSG_TSTV_TO_LIVE failed, live url is null\n",CLIENT_MARK);
                    break;
                }
                /*先做一个简单的清除所有event/实际应用的时候可以考虑从头开始插入*/
                client_event_clear(me->event_mgr);
                /*open tstv*/  
                event->event_name = CLIENT_EVENT_OPEN;
                strcpy(event->type, "LiveTV");
                strcpy(event->url, live_url);
                client_event_post(me->event_mgr, event);

                /*进入直播正常播放*/
                event->event_name = CLIENT_EVENT_PLAY;
                client_event_post(me->event_mgr, event);

                /*时移切直播*/
                me->flag_tstv_to_live = 1;
                /*线程状态改成空闲*/
                me->client_proc_status = CLIENT_EVENT_PROC_IDLE;                
            }
            break;
        case CLIENT_MSG_UPDATE_TIME_INFO:       /*更新时间信息*/
            client_info_set_property(me->client_info, CLIENT_INFO_TIME_INFO, value);
            break;
        case CLIENT_MSG_UPDATE_VOD_TYPE:
            client_info_set_property(me->client_info, CLIENT_INFO_TYPE, value);
            break;
        case CLIENT_MSG_PLAYER_DVB_INFO:
            me->cbf(me->msg_handle, EIS_EVENT_TYPE_IPTV, EIS_VOD_PLAY_DVB_INFO, value);           
            break;
        default:
            break;
    }

    INFO("%s[client_callback_message] success\n", CLIENT_MARK);    

    return IPANEL_OK;    
}

static int client_callback_get(client_t *me, unsigned int prop, void *value)
{
    int ret = 0;
    
    FAILED_RETURNX(NULL == me || NULL == me->client_info, IPANEL_ERR);

    INFO("%s[client_callback_get] prop = %d\n", CLIENT_MARK,prop);

    ret = client_info_get_property(me->client_info, (int)prop, value);

    INFO("%s[client_callback_get] success\n", CLIENT_MARK);

    return ret;
}


