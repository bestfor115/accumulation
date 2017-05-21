/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:PLAYER管理
*********************************************************************/
#include "iplayer_player.h"
#include "iplayer_ts_player.h"
#include "ipanel_porting.h"
#include "iplayer_qam_player.h"
/************************************************************************************************
 *宏定义
 ************************************************************************************************/

#define MAX_IPLAYER_NUM  (8)
#define IPLAYER_MARK "[IVOD][PLAYER]"


#define IPLAYER_MGR_LOCK(me) if(me) ipanel_porting_sem_wait(me->sem, IPANEL_WAIT_FOREVER)
#define IPLAYER_MGR_UNLOCK(me) if(me) ipanel_porting_sem_release(me->sem)

/************************************************************************************************
 *结构体定义
 ************************************************************************************************/

typedef enum
{
    IPLAYER_THREAD_STATE_IDLE   = 0,
    IPLAYER_THREAD_STATE_WORKING,        
    IPLAYER_THREAD_STATE_STOP
}iplayer_thread_state_e; //player管理器线程工作状态



struct iplayer_s
{
    unsigned int client_handle;           //与播放器关联的client句柄
    unsigned int play_handle;             //ts或es播放器handle
    iplayer_mode_e  play_mode;            //播放器类型

    
    unsigned int flag_used:1;             //指示播放器是否在使用
    int  (*proc)(int player_id, unsigned int op, int p1,int p2);
    
    struct iplayer_s *next;               //指向下一个player的地址
    
};//player的结构类似client



struct iplayer_mgr_s
{
    unsigned int sem;                    //player管理器互斥量
    unsigned int thread_id;              //player管理器的线程id号
    iplayer_thread_state_e thread_state; //player管理器线程状态
    iplayer_t *mem;                      //player管理器管理的player的内存地址
    iplayer_t *head;                     //player管理器管理的player指针链表
};



/************************************************************************************************
 *函数声明
 ************************************************************************************************/
static void 
iplayer_mgr_proc(void *param);

static int
iplayer_callback(void *handle, unsigned int event, int p1, int p2);




static iplayer_mgr_t *g_player_mgr = NULL;

/************************************************************************************************
 *函数定义
 ************************************************************************************************/

/*创建player管理器*/
iplayer_mgr_t *iplayer_mgr_new(void)
{
    iplayer_mgr_t *me = NULL;

    INFO("%s[iplayer_mgr_new] start\n", IPLAYER_MARK);
    //分配player管理内存
    me = (iplayer_mgr_t *)calloc(1, sizeof(iplayer_mgr_t));
    if(NULL == me)
    {
        INFO("%s[iplayer_mgr_new] player mgr calloc memory failed\n", IPLAYER_MARK);
        goto PLAYER_FAILED;
    } 

    memset(me,0,sizeof(iplayer_mgr_t));
    me->thread_state = IPLAYER_THREAD_STATE_IDLE;
    

    //分配play的内存空间
    me->mem = (iplayer_t*)calloc(MAX_IPLAYER_NUM, sizeof(iplayer_t));
    if(NULL == me->mem)
    {
        INFO("%s[iplayer_mgr_new] player calloc memory failed\n", IPLAYER_MARK);
        goto PLAYER_FAILED;
    } 


    //创建信号量
    me->sem = ipanel_porting_sem_create("PLAM", 1, IPANEL_TASK_WAIT_FIFO);
    if(0 == me->sem)
    {
        INFO("%s[iplayer_mgr_new] create sem failed\n", IPLAYER_MARK);
        goto PLAYER_FAILED;        
    }


    //创建player线程
    me->thread_id = ipanel_porting_task_create("PLAM", iplayer_mgr_proc, (void*)me, 6, 64*1024);
    if(me->thread_id <= 0)
    {
        INFO("%s[iplayer_mgr_new]thread create is failed\n", IPLAYER_MARK);
        goto PLAYER_FAILED;        
    }
    me->thread_state = IPLAYER_THREAD_STATE_WORKING;

    INFO("%s[iplayer_mgr_new]player mgr create success!!\n", IPLAYER_MARK);        
    g_player_mgr = me;

    return me;
    
PLAYER_FAILED:
    if(me)
    {
        if(me->thread_id > 0)
        {
            ipanel_porting_task_destroy(me->thread_id);
            me->thread_id = 0;
        }
        if(me->sem > 0)
        {
            ipanel_porting_sem_destroy(me->sem);
            me->sem = 0;
        }
        if(me->mem)
        {
            free(me->mem);
            me->mem = NULL;
        }
        free(me);
        me = NULL;
    }
    
    INFO("%s[iplayer_mgr_new]player mgr create failed!!\n", IPLAYER_MARK);
    g_player_mgr = NULL;
    
    return NULL;
}


/*销毁player管理器*/
int iplayer_mgr_destory(iplayer_mgr_t *handle)
{
    iplayer_mgr_t *me = handle;
    iplayer_t *head = NULL;
    
    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    INFO("%s[iplayer_mgr_destory]player mgr delete start(thread_state = %d)!!\n", IPLAYER_MARK,me->thread_state);

    if( IPLAYER_THREAD_STATE_WORKING == me->thread_state )
    {
        me->thread_state = IPLAYER_THREAD_STATE_IDLE;
        
        while(IPLAYER_THREAD_STATE_STOP!= me->thread_state)
        {
            //等待socket线程结束
            ipanel_porting_task_sleep(3);
        }
    }

    INFO("%s[iplayer_mgr_destory]destory player thread!!\n", IPLAYER_MARK);
    if(me->thread_id > 0)
    {
        ipanel_porting_task_destroy(me->thread_id);
        me->thread_id = 0;
    }

    //关闭所有player
    head = me->head;
    while(head)
    {
        if(head->flag_used)
        {
            iplayer_close((int)head);
        }
        else
        {
            //异常情况直接退出
            break;
        }
        head = me->head;
    }

    INFO("%s[iplayer_mgr_destory]free player mgr mem!!\n", IPLAYER_MARK);
    if(me->mem)
    {
        free(me->mem);
        me->mem = NULL;
    }

    
    INFO("%s[iplayer_mgr_destory]destory player mgr sem!!\n", IPLAYER_MARK);
    if(me->sem > 0)
    {
        ipanel_porting_sem_destroy(me->sem);
        me->sem = 0;
    }
    INFO("%s[iplayer_mgr_destory]free player mgr!!\n", IPLAYER_MARK);
    if(me)
    {
        free(me);
    }
    g_player_mgr = NULL;
    INFO("%s[iplayer_mgr_destory]player mgr destory end!!\n", IPLAYER_MARK);
    
    return IPANEL_OK;
}



/*player管理器线程*/
static void iplayer_mgr_proc(void *param)
{
    iplayer_mgr_t *me = (iplayer_mgr_t*)param;
    iplayer_t *player = NULL;
    
    FAILED_RETURN(NULL == me);

    me->thread_state = IPLAYER_THREAD_STATE_WORKING;
    
    while(IPLAYER_THREAD_STATE_WORKING == me->thread_state)
    {
        IPLAYER_MGR_LOCK(me);
        
        player = me->head;
        if(NULL == player)
            goto SLEEP;

        //遍历player链表        
        while(player)
        {
            if(player->proc && player->play_handle)
            {
                player->proc(player->play_handle,IPLAYER_OP_TIME,0,0);
            }
            player = player->next;
        }
        
     SLEEP:
        IPLAYER_MGR_UNLOCK(me);
        ipanel_porting_task_sleep(10);
    }    

	INFO("%s[iplayer_mgr_proc]player mgr thread stop working\n", IPLAYER_MARK);
    //设置线程结束工作标记
    me->thread_state = IPLAYER_THREAD_STATE_STOP;

    return ;
}



/*打开player播放实例*/
int iplayer_open(client_t *client_main, iplayer_info_t *info)
{
    iplayer_mgr_t *me = g_player_mgr;
    iplayer_t *player = NULL,*head = NULL;
    int i = 0;
    
    FAILED_RETURNX(NULL == g_player_mgr || NULL == client_main || NULL == info, IPANEL_ERR);

    IPLAYER_MGR_LOCK(me);
    for(i=0;i<MAX_IPLAYER_NUM;i++)
    {
        if(!me->mem[i].flag_used)
        {
            me->mem[i].flag_used = 1;
            player = &me->mem[i];
            player->next = NULL;
            break;
        }
    }
    IPLAYER_MGR_UNLOCK(me);
    
    if(NULL == player)
    {
    	INFO("%s[iplayer_open]NO more enough player to open!!\n", IPLAYER_MARK);
        return IPANEL_ERR;
    }
    else
    {
    	INFO("%s[iplayer_open]player open mode is %d!!\n", IPLAYER_MARK,(iplayer_mode_e)info->mode);
        if(info->mode == IPLAYER_MODE_IP_TS )
        {
            player->play_handle = ts_iplayer_open(player,info,iplayer_callback);
            if(player->play_handle)
            {
                player->proc = ts_iplayer_proc;
            }
        }
        else if(info->mode == IPLAYER_MODE_QAM_TS)
        {
            player->play_handle = ts_qamplayer_open(player, info);
            if(player->play_handle)
            {
                player->proc = ts_qamplayer_proc;
            }
        }
        else if(info->mode == IPLAYER_MODE_IP_ES || info->mode == IPLAYER_MODE_QAM_ES)
        {
            /*player->play_handle = es_player_open(player,info);
            if(player->play_handle)
            {
                player->proc = es_player_proc;
            }*/
        }
        else
        {
            return IPANEL_ERR;
        }
        
    }

	INFO("%s[iplayer_open]player = 0x%x,player_handle = 0x%x!!\n", IPLAYER_MARK,player,player->play_handle);

    if(player->play_handle > 0)
    {
        player->play_mode = (iplayer_mode_e)info->mode;
        player->client_handle = (unsigned int)client_main;
        player->next = NULL;

        //将player加入player mgr的链表之中
        IPLAYER_MGR_LOCK(me);
        
        head = me->head;
        if(NULL == head)
        {
            me->head = player;
        }
        else
        {
            while(head->next)
            {
                head = head->next;
            }
            head->next = player;
        }
        
        IPLAYER_MGR_UNLOCK(me);
        
        return (int)player;
    }
    else
    {
        player->flag_used = 0;
        player->proc = NULL;
        return IPANEL_ERR;
    }

}



/*关闭player播放实例*/
int iplayer_close(int player_id)
{ 
    iplayer_mgr_t *me = g_player_mgr;
    iplayer_t *player = (iplayer_t*)player_id,*top = NULL,*bottom = NULL;
    
    FAILED_RETURNX(NULL == me || NULL == player, IPANEL_ERR);

    if((!player->flag_used) || (!player->play_handle) || (!player->proc))
    {
        return IPANEL_ERR;
    }
    
	INFO("%s[iplayer_close]player = 0x%x,player_handle = 0x%x!!\n", IPLAYER_MARK,player,player->play_handle);
    /*close的时候就把play_handle给free了,这里加上锁*/
    IPLAYER_MGR_LOCK(me);
    player->proc(player->play_handle,IPLAYER_OP_CLOSE,0,0);

    top = me->head;
	INFO("%s[iplayer_close] remove player from list,top = 0x%x\n", IPLAYER_MARK, top);

    if(top != NULL)
    {
        if(top == player)
        {
            me->head = player->next;
        }
        else
        {
            bottom = top->next;
            while(bottom && bottom != player)
            {
                top = bottom;
                bottom = bottom->next;
            }

            if(bottom && bottom == player)
            {
                top->next = bottom->next;
            }
        }
    }
	INFO("%s[iplayer_close] remove player success\n", IPLAYER_MARK);
    player->client_handle = 0;
    player->play_handle = 0;
    player->play_mode = IPLAYER_MODE_NONE;
    player->flag_used = 0;
    player->proc = NULL;
    player->next = NULL;
    
    IPLAYER_MGR_UNLOCK(me);

	INFO("%s[iplayer_close]player close success!!\n", IPLAYER_MARK);
    return IPANEL_OK;
}



/*停止player播放实例*/
int iplayer_stop(int player_id, int keep_last_frame)
{

    iplayer_t *player = (iplayer_t*)player_id;
    
    FAILED_RETURNX(NULL == player, IPANEL_ERR);

    if((!player->flag_used) || (!player->play_handle) || (!player->proc))
    {
        return IPANEL_ERR;
    }
    
	INFO("%s[iplayer_stop]player = 0x%x,player_handle = 0x%x!!\n", IPLAYER_MARK,player,player->play_handle);

    return player->proc(player->play_handle,IPLAYER_OP_STOP,keep_last_frame,0);

}



/*暂停*/
int iplayer_pause(int player_id)
{
    iplayer_t *player = (iplayer_t*)player_id;
    
    FAILED_RETURNX(NULL == player, IPANEL_ERR);

    if((!player->flag_used) || (!player->play_handle) || (!player->proc))
    {
        return IPANEL_ERR;
    }
    
	INFO("%s[iplayer_pause]player = 0x%x,player_handle = 0x%x!!\n", IPLAYER_MARK,player,player->play_handle);

    return player->proc(player->play_handle,IPLAYER_OP_PAUSE,0,0);

}



/*恢复*/
int iplayer_resume(int player_id)
{
    iplayer_t *player = (iplayer_t*)player_id;
    
    FAILED_RETURNX(NULL == player, IPANEL_ERR);

    if((!player->flag_used) || (!player->play_handle) || (!player->proc))
    {
        return IPANEL_ERR;
    }
    
	INFO("%s[iplayer_resume]player = 0x%x,player_handle = 0x%x!!\n", IPLAYER_MARK,player,player->play_handle);

    return player->proc(player->play_handle,IPLAYER_OP_RESUME,0,0);

}



/*播放前的notify*/
int iplayer_notify(int player_id, int op, int speed)
{
    iplayer_t *player = (iplayer_t*)player_id;
    
    FAILED_RETURNX(NULL == player, IPANEL_ERR);

    if((!player->flag_used) || (!player->play_handle) || (!player->proc))
    {
        return IPANEL_ERR;
    }
    
	INFO("%s[iplayer_notify]player = 0x%x,player_handle = 0x%x!!\n", IPLAYER_MARK,player,player->play_handle);

    //op为player_op_e里面的枚举值，不包括PLAYER_OP_NOTIFY
    return player->proc(player->play_handle,IPLAYER_OP_NOTIFY,op,speed);
}



/*播放*/
int iplayer_play(int player_id, int op, active_info_t *info)
{
    iplayer_t *player = (iplayer_t*)player_id;
    
    FAILED_RETURNX(NULL == player, IPANEL_ERR);

    if((!player->flag_used) || (!player->play_handle) || (!player->proc))
    {
        return IPANEL_ERR;
    }
    
	INFO("%s[iplayer_play]player = 0x%x,player_handle = 0x%x!!\n", IPLAYER_MARK,player,player->play_handle);

    return player->proc(player->play_handle,op,(int)info,0);

}



/*设置播放器参数*/
int iplayer_set_prop(int player_id, int op, int value)
{
    iplayer_t *player = (iplayer_t*)player_id;
    
    FAILED_RETURNX(NULL == player, IPANEL_ERR);

    if((!player->flag_used) || (!player->play_handle) || (!player->proc))
    {
        return IPANEL_ERR;
    }
    
	INFO("%s[iplayer_set_prop]player = 0x%x,player_handle = 0x%x!!\n", IPLAYER_MARK,player,player->play_handle);

    //op为iplayer_param_e里面的枚举值
    return player->proc(player->play_handle,IPLAYER_OP_SET_PROP,op,(int)value);
}



/*获取播放器参数*/
int iplayer_get_prop(int player_id, int op, int value)
{
    iplayer_t *player = (iplayer_t*)player_id;
    
    FAILED_RETURNX(NULL == player, IPANEL_ERR);

    if((!player->flag_used) || (!player->play_handle) || (!player->proc))
    {
        return IPANEL_ERR;
    }
    
	INFO("%s[iplayer_get_prop]player = 0x%x,player_handle = 0x%x!!\n", IPLAYER_MARK,player,player->play_handle);

    //op为iplayer_param_e里面的枚举值
    return player->proc(player->play_handle,IPLAYER_OP_GET_PROP,op,(int)value);
}



/*获取可用内存块*/
imem_node_t *iplayer_get_unused_block(int player_id, int size)
{
    iplayer_t *player = (iplayer_t*)player_id;
    
    FAILED_RETURNX(NULL == player, NULL);

    if((!player->flag_used) || (!player->play_handle) || (!player->proc))
    {
        return NULL;
    }
    
	//INFO("%s[iplayer_get_unused_block]player = 0x%x,player_handle = 0x%x!!\n", IPLAYER_MARK,player,player->play_handle);

    return (imem_node_t *)player->proc(player->play_handle,IPLAYER_OP_GET_BLOCK,size,0);
}



/*添加block到player 链表里面*/
int iplayer_append_data(int player_id, int value)
{
    iplayer_t *player = (iplayer_t*)player_id;
    
    FAILED_RETURNX(NULL == player, IPANEL_ERR);

    if((!player->flag_used) || (!player->play_handle) || (!player->proc))
    {
        return IPANEL_ERR;
    }
    
	//INFO("%s[iplayer_append_data]player = 0x%x,player_handle = 0x%x!!\n", IPLAYER_MARK,player,player->play_handle);

    return player->proc(player->play_handle,IPLAYER_OP_APPEND_DATA,value,0);
}



/*各播放器实例的回调函数，返回相关消息*/
static int iplayer_callback(void *handle, unsigned int event, int p1, int p2)
{
    iplayer_t *player = (iplayer_t*)handle;
    
    FAILED_RETURNX(NULL == player, IPANEL_ERR);

	INFO("%s[iplayer_callback]player = 0x%x,event = %d,p1 = %d,p2 = %d!!\n", IPLAYER_MARK,player,event,p1,p2);

    if(player->client_handle)
        client_callback_proc(player->client_handle,event,p1,p2);

    return IPANEL_OK;
}



/************************************End Of File**********************************/




