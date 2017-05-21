/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:MEM管理
*********************************************************************/
#include "ipanel_porting.h"

#include "iplayer_mem.h"

/************************************************************************************************
 *宏定义
 ************************************************************************************************/

#define IMEM_MGR_LOCK(me)   if(me) ipanel_porting_sem_wait(me->mem_mgr_sem, IPANEL_WAIT_FOREVER)
#define IMEM_MGR_UNLOCK(me) if(me) ipanel_porting_sem_release(me->mem_mgr_sem)

#define NODE_LIST_LOCK(me)   if(me) ipanel_porting_sem_wait(me->mem_list_sem, IPANEL_WAIT_FOREVER)
#define NODE_LIST_UNLOCK(me) if(me) ipanel_porting_sem_release(me->mem_list_sem)

#define MARK_MEM "[IVOD][MEM]"


/************************************************************************************************
 *结构体定义
 ************************************************************************************************/
typedef struct imem_node_info_s   imem_node_info_t;

struct imem_node_info_s
{
    unsigned int      node_num;                      //一起使用的内存块数量
    int               used_time;                     //统计被同时使用的次数，可用于判断是否内存重复使用
    
    unsigned int      flag_used:1;                   //内存块是否在使用
    
};//内存块信息描述


struct imem_node_list_s
{
    imem_mgr_t       *mem_mgr_handle;               //内存管理句柄
    unsigned int     mem_list_sem;                  //内存块列表信号量

    unsigned int     mem_list_node_num;             //列表中包含的内存块数量
    imem_node_t      **mem_list;                    //列表中mem node数组

    int              mem_list_top;                  //列表中可写内存块索引
    int              mem_list_bottom;               //列表中可读内存块索引
};//内存块列表


struct imem_mgr_s
{
    unsigned int     mem_mgr_sem;          //内存管理器信号量

    unsigned char    *mem_mgr_buffer;      //内存管理器管理的内存地址
    unsigned int     mem_mgr_buffer_len;   //内存管理器管理的内存大小
    
    unsigned int     mem_node_total_num;   //管理器分配的内存块总数
    unsigned int     mem_node_buffer_len;  //管理器分配的内存块大小
    unsigned int     mem_node_used_num;    //使用中的内存块数量

    imem_node_t       *mem_node_group;     //内存管理器包括的内存块组
    imem_node_info_t  *mem_node_info;      //内存块信息描述
    
};//内存管理器



/************************************************************************************************
 *函数声明
 ************************************************************************************************/

imem_mgr_t *g_mem_mgr = NULL;
/************************************************************************************************
 *函数定义
 ************************************************************************************************/

/*创建内存管理器,并且分配指定大小的内存*/
imem_mgr_t *imem_mgr_new(int size)
{
    imem_mgr_t *me = (imem_mgr_t*)calloc(1,sizeof(imem_mgr_t));
    if((NULL == me) || (size<=0))
    {
        INFO("%s[imem_mgr_new]mem mgr calloc %d is failed!!!\n",MARK_MEM,size);
        goto MEM_ERR;
    }

    memset(me,0,sizeof(imem_mgr_t));
    
    //分配mem mgr的内存空间
    me->mem_mgr_buffer = (unsigned char*)calloc(1,size);
    if(NULL == me->mem_mgr_buffer)
    {
        INFO("%s[imem_mgr_new]mem mgr buffer calloc is failed\n", MARK_MEM);
        goto MEM_ERR;
    }
    me->mem_mgr_buffer_len = size;
    memset(me->mem_mgr_buffer,0,me->mem_mgr_buffer_len);
    

    //创建互斥量
    me->mem_mgr_sem = ipanel_porting_sem_create("MEM", 1, IPANEL_TASK_WAIT_FIFO);
    if(me->mem_mgr_sem <= 0)
    {
        INFO("%s[imem_mgr_new]sem create is failed\n", MARK_MEM);
        goto MEM_ERR;        
    }

    
    //初始化内存管理器各参数值
    me->mem_node_buffer_len = MAX_IMEM_NODE_BUFFER_LEN;
    me->mem_node_total_num = me->mem_mgr_buffer_len / me->mem_node_buffer_len;
    me->mem_node_used_num = 0;
    if(me->mem_node_total_num <= 0)
    {
        goto MEM_ERR;
    }


    //申请mem_node的空间
    me->mem_node_group = (imem_node_t*)calloc(me->mem_node_total_num,sizeof(imem_node_t));
    if(NULL == me->mem_node_group)
    {
        INFO("%s[imem_mgr_new]mem node group calloc is failed\n", MARK_MEM);
        goto MEM_ERR;
    }

    me->mem_node_info = (imem_node_info_t*)calloc(me->mem_node_total_num,sizeof(imem_node_info_t));
    if(NULL == me->mem_node_info)
    {
        INFO("%s[imem_mgr_new]mem node info calloc is failed\n", MARK_MEM);
        goto MEM_ERR;
    }

    memset(me->mem_node_group,0,me->mem_node_total_num*sizeof(imem_node_t));
    memset(me->mem_node_info,0,me->mem_node_total_num*sizeof(imem_node_info_t));
    
    g_mem_mgr = me;
    
    INFO("%s[imem_mgr_new]mem size = %d,total_node_num = %d,node_buffer_len = %d!!\n", 
        MARK_MEM,size,me->mem_node_total_num,me->mem_node_buffer_len);
    
    INFO("%s[imem_mgr_new]mem mgr new success!!\n", MARK_MEM);
    
    return me;

    
MEM_ERR:
    if(me)
    {
        if(me->mem_node_info)
        {
            free(me->mem_node_info);
            me->mem_node_info = NULL;
        }
        if(me->mem_node_group)
        {
            free(me->mem_node_group);
            me->mem_node_group = NULL;
        }
        if(me->mem_mgr_sem > 0)
        {
            ipanel_porting_sem_destroy(me->mem_mgr_sem);
            me->mem_mgr_sem = 0;
        }
        if(me->mem_mgr_buffer)
        {
            free(me->mem_mgr_buffer);
            me->mem_mgr_buffer = NULL;
        }
        free(me);
        me = NULL;
    }
    g_mem_mgr = NULL;
    INFO("%s[imem_mgr_new]mem mgr new failed!!\n", MARK_MEM);
    
    return NULL;
}

/*销毁*/
int imem_mgr_destory(imem_mgr_t *handle)
{

    imem_mgr_t *me = handle;
    
	FAILED_RETURNX(!me,IPANEL_ERR);
    
    INFO("%s[imem_mgr_destory]mem mgr destory start!!!\n",MARK_MEM);
    
    if(me)
    {
        IMEM_MGR_LOCK(me);
        
        if(me->mem_node_info)
        {
            memset(me->mem_node_info,0,me->mem_node_total_num*sizeof(imem_node_info_t));
            free(me->mem_node_info);
            me->mem_node_info = NULL;
        }
        INFO("%s[imem_mgr_destory]mem mgr destory node info end!!!\n",MARK_MEM);
        
        if(me->mem_node_group)
        {
            memset(me->mem_node_group,0,me->mem_node_total_num*sizeof(imem_node_t));
            free(me->mem_node_group);
            me->mem_node_group = NULL;
        }
        INFO("%s[imem_mgr_destory]mem mgr destory node group end!!!\n",MARK_MEM);

        if(me->mem_mgr_buffer)
        {
            memset(me->mem_mgr_buffer,0,me->mem_mgr_buffer_len);
            free(me->mem_mgr_buffer);
            me->mem_mgr_buffer = NULL;
            me->mem_node_total_num = 0;
        }
        INFO("%s[imem_mgr_destory]mem mgr destory mgr buffer end!!!\n",MARK_MEM);

        IMEM_MGR_UNLOCK(me);

        if(me->mem_mgr_sem > 0)
        {
            ipanel_porting_sem_destroy(me->mem_mgr_sem);
            me->mem_mgr_sem = 0;
        }
        INFO("%s[imem_mgr_destory]mem mgr destory mgr sem end!!!\n",MARK_MEM);

        free(me);
        me = NULL;
    }

    g_mem_mgr = NULL;

    INFO("%s[imem_mgr_destory]mem mgr destory success end!!!\n",MARK_MEM);
    
    return IPANEL_OK;
}

/*获取可用node*/
imem_node_t *imem_mgr_get_unused_block(imem_mgr_t *handle, int size)
{
	int i = 0,block_num = 0,j = 0,flag_ok = 0;
    imem_mgr_t *me = handle;
	
	FAILED_RETURNX(!me,NULL);

    IMEM_MGR_LOCK(me);
    
	block_num = ( size % me->mem_node_buffer_len) > 0 ? 1 : 0;
	block_num += size / me->mem_node_buffer_len;

	for( i = 0; i < (int)me->mem_node_total_num; i++ ) 
    {
		if(me->mem_node_info[i].flag_used == 0 ) 
        {
			for( j = 0; j < block_num ; j++ ) 
            {
				if( j + i >= (int)me->mem_node_total_num || me->mem_node_info[i+j].flag_used)
                    break;
			}

			if( j == block_num ) 
            {
				for( j = 0; j < block_num; j++ ) 
                {
					me->mem_node_info[i+j].flag_used = 1;
					me->mem_node_info[i+j].node_num = 0;
                    me->mem_node_info[i+j].used_time++;

                    //用于判断是否还有地方在使用这块内存
                    if(me->mem_node_info[i+j].used_time != 1)
                    {
                        INFO("%s[imem_mgr_get_unused_block]buffer used_time=%d!!!\n",MARK_MEM,me->mem_node_info[i+j].used_time);
                    }
				}
				me->mem_node_info[i].node_num = block_num;
				me->mem_node_group[i].node_buffer = me->mem_mgr_buffer + ( i * me->mem_node_buffer_len);
				me->mem_node_group[i].node_buffer_data_len = 0;
				me->mem_node_used_num += block_num;
				break;
			}
		}
	}


    IMEM_MGR_UNLOCK(me);
    
	return i == (int)me->mem_node_total_num ? NULL : &me->mem_node_group[i];

}


/*删除*/
int imem_mgr_remove(imem_mgr_t *handle, imem_node_t *mem)
{
	int i = 0,index = 0;
    imem_mgr_t *me = handle;
	
	FAILED_RETURNX(!me || !mem, IPANEL_ERR);

    IMEM_MGR_LOCK(me);
    
	index = ( mem->node_buffer - me->mem_mgr_buffer) / me->mem_node_buffer_len;
	
	if( index >= 0 && index < (int)me->mem_node_total_num && (&me->mem_node_group[index] == mem)) 
    {
		if( me->mem_node_info[index].flag_used == 1 ) 
        {
			for( i = 0; i < (int)me->mem_node_info[index].node_num && (i + index) < (int)me->mem_node_total_num; i++ ) 
            {
				me->mem_node_info[i+index].flag_used = 0;
                me->mem_node_info[i+index].used_time --;
				me->mem_node_used_num--;

                //判断是不是内存块被重复删除,或还有地方在使用
                if(me->mem_node_info[i+index].used_time != 0)
                {
                    INFO("%s[imem_mgr_remove]mem mgr remove used_time=%d!!!\n",MARK_MEM,me->mem_node_info[i+index].used_time);                    
                }
			}
			me->mem_node_info[index].node_num = 0;
            me->mem_node_group[index].node_buffer_data_len = 0;
		}
	} 
    else 
    {
		//EIS_ASSERT(1);
        INFO("%s[imem_mgr_remove]mem mgr remove index=%d mem_node_total_num=%d!!!\n",MARK_MEM,index,me->mem_node_total_num);                    
	}


    IMEM_MGR_UNLOCK(me);
    
	if( i == (int)me->mem_node_total_num ) 
    {
		//EIS_ASSERT(1);
        return IPANEL_ERR;
	}
    
	return IPANEL_OK;    
}


/*清除*/
int imem_mgr_clear(imem_mgr_t *handle)
{
	imem_mgr_t *me = handle;
	
	FAILED_RETURNX(!me,IPANEL_ERR);

    IMEM_MGR_LOCK(me);

    if(me->mem_node_info)
        memset(me->mem_node_info,0,me->mem_node_total_num*sizeof(imem_node_info_t));
    if(me->mem_node_group)
        memset(me->mem_node_group,0,me->mem_node_total_num*sizeof(imem_node_t));

    me->mem_node_used_num = 0;
    
    IMEM_MGR_UNLOCK(me);

    return IPANEL_OK;
}


/*获取剩余大小*/
int imem_mgr_get_free_size(imem_mgr_t *handle)
{
    imem_mgr_t *me = handle;
    
	FAILED_RETURNX(!me,0);
	return (me->mem_node_total_num - me->mem_node_used_num)*me->mem_node_buffer_len;
}


/*获取已使用率*/
int imem_mgr_get_rate(imem_mgr_t *handle)
{
	imem_mgr_t *me = handle;
    
	FAILED_RETURNX(!me || !me->mem_node_total_num,0);
	
	return ( me->mem_node_used_num * 100 ) / me->mem_node_total_num;
}



/*创建node list*/
imem_node_list_t *imem_node_list_new(imem_mgr_t *mem_mgr,int mem_node_number)
{
    imem_mgr_t *me = mem_mgr;
    imem_node_list_t *mem_node_list = NULL;

    
    if(NULL == me || mem_node_number<=0)
    {
        INFO("%s[imem_node_list_new]mem mgr is NULL or mem_node_number is %d return Failed!!!\n",MARK_MEM,mem_node_number);
        return NULL;
    }
    
    //分配mem node list空间
    mem_node_list = (imem_node_list_t*)calloc(1,sizeof(imem_node_list_t));
    if(NULL == mem_node_list)
    {
        INFO("%s[imem_node_list_new]mem node list calloc is failed\n", MARK_MEM);
        goto NODE_LIST_ERR;
    }    

    //创建互斥量
    mem_node_list->mem_list_sem = ipanel_porting_sem_create("MEML", 1, IPANEL_TASK_WAIT_FIFO);
    if(mem_node_list->mem_list_sem <= 0)
    {
        INFO("%s[imem_node_list_new]sem create is failed\n", MARK_MEM);
        goto NODE_LIST_ERR;        
    }


    mem_node_number = (mem_node_number>(int)me->mem_node_total_num)?me->mem_node_total_num:mem_node_number;

    //分配mem node链表空间
    mem_node_list->mem_list = (imem_node_t**)calloc(mem_node_number,sizeof(imem_node_t*));
    if(NULL == mem_node_list->mem_list)
    {
        INFO("%s[imem_node_list_new]mem node calloc is failed\n", MARK_MEM);
        goto NODE_LIST_ERR;
    }    
    
    
    //初始化内内存块列表各参数值
    mem_node_list->mem_mgr_handle = me;
    mem_node_list->mem_list_node_num = mem_node_number;
    mem_node_list->mem_list_top = 0;
    mem_node_list->mem_list_bottom = 0;
    memset(mem_node_list->mem_list,0,mem_node_list->mem_list_node_num * sizeof(imem_node_t*));
    

    INFO("%s[imem_node_list_new]node list = 0x%x,list_node_num = %d,mem_mgr_node_num = %d!!\n", 
        MARK_MEM,mem_node_list,mem_node_list->mem_list_node_num,me->mem_node_total_num);
    
    INFO("%s[imem_node_list_new]mem node list new success!!\n", MARK_MEM);
    
    return mem_node_list;

    
NODE_LIST_ERR:
    
    if(mem_node_list)
    {
        if(mem_node_list->mem_list)
        {
            free(mem_node_list->mem_list);
            mem_node_list->mem_list = NULL;
        }
        
        if(mem_node_list->mem_list_sem > 0)
        {
            ipanel_porting_sem_destroy(mem_node_list->mem_list_sem);
            mem_node_list->mem_list_sem = 0;
        }

        free(mem_node_list);
        mem_node_list = NULL;
    }

    INFO("%s[imem_node_list_new]mem node list new failed!!\n", MARK_MEM);
    
    return NULL;
}



/*销毁node list*/
int imem_node_list_destory(imem_node_list_t *handle)
{

    imem_node_list_t *me = handle;
    
	FAILED_RETURNX(!me,IPANEL_ERR);
    
    INFO("%s[imem_node_list_destory]mem node list destory start!!!\n",MARK_MEM);
    
    if(me)
    {
                
        if(me->mem_list)
        {
            imem_node_list_clear(me);
            
            NODE_LIST_LOCK(me);

            me->mem_list_node_num = 0;
            free(me->mem_list);
            me->mem_list = NULL;

            NODE_LIST_UNLOCK(me);
        }
        INFO("%s[imem_node_list_destory]mem list destory end!!!\n",MARK_MEM);


        if(me->mem_list_sem > 0)
        {
            ipanel_porting_sem_destroy(me->mem_list_sem);
            me->mem_list_sem = 0;
        }
        INFO("%s[imem_node_list_destory]mem list sem destory end!!!\n",MARK_MEM);

        free(me);
        me = NULL;
    }

    INFO("%s[imem_node_list_destory]mem node list destory success end!!!\n",MARK_MEM);
    
    return IPANEL_OK;
  
}



/*添加到node list尾端*/
int imem_node_list_apend(imem_node_list_t *handle,imem_node_t *mem)
{
    imem_node_list_t *me = handle;
    
	FAILED_RETURNX(!me || !mem,IPANEL_ERR);
	FAILED_RETURNX(!me->mem_list,IPANEL_ERR);
	
    NODE_LIST_LOCK(me);
    
	if( me->mem_list_top == me->mem_list_bottom) 
    {
		me->mem_list_top = 0;
		me->mem_list_bottom = 0;
	}

	if( me->mem_list_top >= ( (int)me->mem_list_node_num - 4) && me->mem_list_bottom > 0 ) 
    {
		memmove(&me->mem_list[0],&me->mem_list[me->mem_list_bottom],(me->mem_list_top - me->mem_list_bottom ) * sizeof(imem_node_t*));
		me->mem_list_top -= me->mem_list_bottom;
		me->mem_list_bottom = 0;
		memset(&me->mem_list[me->mem_list_top],0,( me->mem_list_node_num - me->mem_list_top - 1) * sizeof(imem_node_t*));
	} 
    else if( me->mem_list_top >= (int)me->mem_list_node_num && me->mem_list_bottom == 0 ) 
    {
        NODE_LIST_UNLOCK(me);
        INFO("%s[imem_node_list_apend]mem node list apend failed!!!\n",MARK_MEM);
		return IPANEL_ERR;
	} 
	
	me->mem_list[me->mem_list_top] = mem;
	me->mem_list_top++;
		
    NODE_LIST_UNLOCK(me);
    
	return IPANEL_OK;    
}



/*删除node list第一个节点*/
int imem_node_list_remove(imem_node_list_t *handle,imem_node_t *mem)
{
    imem_node_list_t *me = handle;
    
	FAILED_RETURNX(!me || !mem,IPANEL_ERR);
	FAILED_RETURNX(!me->mem_list,IPANEL_ERR);

    NODE_LIST_LOCK(me);
    
	if(me->mem_list_top < me->mem_list_bottom)
    {
        NODE_LIST_UNLOCK(me);
        imem_mgr_remove(me->mem_mgr_handle, mem);
		return IPANEL_ERR;
	}
	
	if( me->mem_list[me->mem_list_bottom] == mem ) 
    {
		me->mem_list[me->mem_list_bottom] = NULL;
		me->mem_list_bottom++;
	}
    else 
    {
        INFO("%s[imem_node_list_remove]the first mem node isn't in line with the requesting !!!\n",MARK_MEM);
	}

    NODE_LIST_UNLOCK(me);
    	
	return imem_mgr_remove(me->mem_mgr_handle, mem);
    
}



/*获取node list第一个节点*/
imem_node_t *imem_node_list_get(imem_node_list_t *handle)
{
    
    imem_node_list_t *me = handle;
    imem_node_t *mem = NULL;
    
	FAILED_RETURNX(!me || !me->mem_list,NULL);

	
	if(me->mem_list_top <= me->mem_list_bottom || me->mem_list_top < 1)
    {
		return NULL;
	}

    NODE_LIST_LOCK(me);
	
	if( me->mem_list[me->mem_list_bottom] == NULL )
    {
        INFO("%s[imem_node_list_get]first mem node is NULL !!!\n",MARK_MEM);
	}

	mem = me->mem_list[me->mem_list_bottom];
	me->mem_list[me->mem_list_bottom] = NULL;
	me->mem_list_bottom++;
	
	if( me->mem_list_bottom == me->mem_list_top) 
    {
		me->mem_list_bottom = 0;
		me->mem_list_top = 0;
	}

    NODE_LIST_UNLOCK(me);

	return mem;

}

/*获取node list指定序号的节点*/
imem_node_t *imem_node_list_get_index(imem_node_list_t *handle,int index)
{	
    imem_node_list_t *me = handle;
    
	FAILED_RETURNX(!me || !me->mem_list,NULL);

    NODE_LIST_LOCK(me);
	
	if( me->mem_list_top <= me->mem_list_bottom || index >= me->mem_list_top - me->mem_list_bottom ) 
    {
        NODE_LIST_UNLOCK(me);
		return NULL;
	}
	
	index = me->mem_list_bottom + index;

    NODE_LIST_UNLOCK(me);

	return me->mem_list[index];
}


/*清空node list所有节点*/
int imem_node_list_clear(imem_node_list_t *handle)
{
	int i = 0;
    imem_node_list_t *me = handle;
    
	FAILED_RETURNX(!me || !me->mem_list,IPANEL_ERR);
	
    NODE_LIST_LOCK(me);
    
	for( i = 0; i < (int)me->mem_list_node_num; i++ ) 
    {
		if( me->mem_list[i] ) 
        {
            imem_mgr_remove(me->mem_mgr_handle, me->mem_list[i]);
			me->mem_list[i] = NULL;
		}
	}

	memset(me->mem_list,0,me->mem_list_node_num * sizeof(imem_node_t*));
	me->mem_list_top = 0;
	me->mem_list_bottom = 0;
	
    NODE_LIST_UNLOCK(me);
    
	return IPANEL_OK;
}
/************************************End Of File**********************************/


