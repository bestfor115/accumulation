/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:SOCKET管理
*********************************************************************/
#include <netinet/in.h>
#include "iplayer_socket.h"
#include "iplayer_mem.h"
#include "ipanel_porting.h"


/************************************************************************************************
 *宏定义
 ************************************************************************************************/

#define ISOCKET_MAX_NUMS         (8)     //同时使用的rtsock个数非常有限，最多支持16个
#define ISOCKET_MAX_INDEX_START  (100)   //socket起始index
#define HAVE_SUPPORT_HD

#define ISOCKET_MAX_BUFFER_LEN     (2048)  //数据接收buffer的大小


#define HAVE_RTSOCK_ASYNC


#if defined(HAVE_SUPPORT_HD)
    #define ISOCKET_MAX_RECEIVE_NUM (500)
#else
    #define ISOCKET_MAX_RECEIVE_NUM (20)
#endif


#define MARK_SOCKET             "[IVOD][ISOCKET]"
#define ISOCKET_MGR_LOCK(me) if(me) ipanel_porting_sem_wait(me->sem, IPANEL_WAIT_FOREVER)
#define ISOCKET_MGR_UNLOCK(me) if(me) ipanel_porting_sem_release(me->sem)

#define ISOCKET_ASSERT(a) if(a) { INFO("%s(%d) : assert failed!\n", __FILE__, __LINE__);}
/************************************************************************************************
 *结构体定义
 ************************************************************************************************/

/*socket线程工作状态*/
typedef enum
{
    ISOCKET_THREAD_STATE_IDLE   = 0,
    ISOCKET_THREAD_STATE_WORKING,        
    ISOCKET_THREAD_STATE_STOP
}isocket_thread_state_e;


struct isocket_s
{
    unsigned int	  client_handle;              //创建socket的模块handle               
    
    int               sockId;                     //porting返回的socket id
	unsigned int	  connect_start_time;         //连接服务器开始时间

    unsigned int      indx;                       //socket索引
    
    unsigned char     is_error;                   //是否是活动的socket G_TRUE/G_FALSE
    unsigned char     interleave_type;            // EN_RTSOCK_INTER_NULL  EN_RTSOCK_INTER_RTP  EN_RTSOCK_INTER_TS

    unsigned char     is_connect;                 //是否正在连接
	unsigned char     is_read;                    //是否可读
	unsigned char     is_write;                   //是否可写
    unsigned char     is_listen;                  //是否处于监听
    unsigned char     type;                       // socket类型TCP OR UDP
    unsigned char     protocol;                   // ipv4或ipv6

	IPAddr0           multicast_addr;             //保存加入组播时的地址

	unsigned int      flag_wait_rtsp:1;           //接收rtsp应答标志
	unsigned int      flag_wait_data:1;           //接收数据标志
	unsigned int      flag_http_head:1;
	unsigned int      flag_used:1;                //是否处于可用状态

    imem_node_t        *mem_block;                 //可用的数据块地址     
    
	unsigned short    need_recv_len;              //需要接收的数据包大小
	char              recv_buffer[ISOCKET_MAX_BUFFER_LEN+10];
	int               recv_buffer_len;            //数据缓冲区中的数据量

	int               recv_packet_len;            //用于控制udp最大可接收的数据长度
};


struct isocket_mgr_s
{
    unsigned int    thread_id;              //socket线程id
    unsigned int    sem;                    //socket互斥量
 
	isocket_t        *socket_mem;           //socket队列

	unsigned int	base_index;             //socket起始索引

	isocket_thread_state_e  thread_state;   //socket线程执行状态
};



/************************************************************************************************
 *函数声明
 ************************************************************************************************/

static void 
isocket_mgr_thread_proc(void *handle);

static int
isocket_mgr_select(isocket_mgr_t *handle);

static int
isocket_mgr_notify_proc(isocket_mgr_t *handle,isocket_t *socket,int msg);

static isocket_t*
isocket_mgr_open_item(isocket_mgr_t *handle,int index);

static int
isocket_mgr_close_item(isocket_mgr_t *handle,isocket_t *item);

static int
isocket_interleave_recv(isocket_mgr_t *handle,isocket_t *item);

static int
isocket_http_recv(isocket_mgr_t *handle,isocket_t *item);

static int
isocket_tcp_recv(isocket_mgr_t *handle,isocket_t *item);

static int
isocket_udp_recvfrom(isocket_mgr_t *handle,isocket_t *item);

static int 
isocket_analysis_rtsp_command(isocket_mgr_t *handle,isocket_t* item);

static char* 
isocket_search_rtsp_command(isocket_mgr_t *handle,isocket_t* item);

static int 
isocket_handle_error_data(isocket_mgr_t *handle,isocket_t* item);


static isocket_mgr_t *g_socket_mgr = NULL;
/************************************************************************************************
 *函数定义
 ************************************************************************************************/

/*创建socket管理器*/
void *isocket_mgr_new(void)
{
    isocket_mgr_t *me = (isocket_mgr_t*)calloc(1,sizeof(isocket_mgr_t));
    if(NULL == me)
    {
        INFO("%s[isocket_mgr_new]socket mgr calloc is failed!!!\n",MARK_SOCKET);
        goto SOCKET_ERR;
    }

    //初始化参数
    me->thread_state = ISOCKET_THREAD_STATE_IDLE;

    
    //分配可用socket的内存空间
    me->socket_mem = (isocket_t *)calloc(ISOCKET_MAX_NUMS,sizeof(isocket_t));
    if(NULL == me->socket_mem)
    {
        INFO("%s[isocket_mgr_new]socket calloc is failed\n", MARK_SOCKET);
        goto SOCKET_ERR;
    }

    
    //创建互斥量
    me->sem = ipanel_porting_sem_create("SOCK", 1, IPANEL_TASK_WAIT_FIFO);
    if(me->sem <= 0)
    {
        INFO("%s[isocket_mgr_new]sem create is failed\n", MARK_SOCKET);
        goto SOCKET_ERR;        
    }

    
    //创建socket线程
    me->thread_id = ipanel_porting_task_create("SOCK", isocket_mgr_thread_proc, (void*)me, 6, 64*1024);
    if(me->thread_id <= 0)
    {
        INFO("%s[isocket_mgr_new]thread create is failed\n", MARK_SOCKET);
        goto SOCKET_ERR;        
    }
    me->thread_state = ISOCKET_THREAD_STATE_WORKING;
    g_socket_mgr = me;
    INFO("%s[isocket_mgr_new]socket mgr create success!!\n", MARK_SOCKET);
    
    return (void*)me;

    
SOCKET_ERR:
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
        if(me->socket_mem)
        {
            free(me->socket_mem);
            me->socket_mem = NULL;
        }
        free(me);
        me = NULL;
    }
    g_socket_mgr = NULL;
    INFO("%s[isocket_mgr_new]socket mgr create failed!!\n", MARK_SOCKET);
    
    return NULL;
}

/*销毁socket管理器*/
void isocket_mgr_delete(void *handle)
{
    int i = 0;
    isocket_mgr_t *me = (isocket_mgr_t*)handle;
    FAILED_RETURN(NULL == me);
    
    INFO("%s[isocket_mgr_delete]socket mgr delete start(thread_state = %d)!!\n", MARK_SOCKET,me->thread_state);

    if( ISOCKET_THREAD_STATE_WORKING == me->thread_state )
    {
        me->thread_state = ISOCKET_THREAD_STATE_IDLE;
        
        while(ISOCKET_THREAD_STATE_STOP!= me->thread_state)
        {
            //等待socket线程结束
            ipanel_porting_task_sleep(3);
        }
    }

    INFO("%s[isocket_mgr_delete]destory socket thread!!\n", MARK_SOCKET);
    if(me->thread_id > 0)
    {
        ipanel_porting_task_destroy(me->thread_id);
        me->thread_id = 0;
    }
    
	for( i = 0; i < ISOCKET_MAX_NUMS; i++ ) 
    {
		if( me->socket_mem[i].flag_used == 1 ) 
        {
            isocket_close(me->socket_mem[i].indx);
		}
	}

    INFO("%s[isocket_mgr_delete]destory socket sem!!\n", MARK_SOCKET);
    if(me->sem > 0)
    {
        ipanel_porting_sem_destroy(me->sem);
        me->sem = 0;
    }
    
    INFO("%s[isocket_mgr_delete]free socket mem!!\n", MARK_SOCKET);   
    if(me->socket_mem)
    {
        free(me->socket_mem);
        me->socket_mem = NULL;
    }
    
    INFO("%s[isocket_mgr_delete]free socket mgr!!\n", MARK_SOCKET);
    if(me)
    {
        free(me);
    }
    g_socket_mgr = NULL;
    INFO("%s[isocket_mgr_delete]socket mgr delete end!!\n", MARK_SOCKET);
    
    return;
}


/*socket线程*/
static void isocket_mgr_thread_proc(void *handle)
{
    isocket_mgr_t *me = (isocket_mgr_t*)handle;
    int i = 0,repeat_time = 3,select_result = 0;
    
    FAILED_RETURN(NULL == me);

    //设置线程开始工作标记
    me->thread_state = ISOCKET_THREAD_STATE_WORKING;
	INFO("%s[isocket_mgr_thread_proc]socket thread is working time = %d\n", MARK_SOCKET,time_ms());

    
	while( ISOCKET_THREAD_STATE_WORKING == me->thread_state )
	{
		select_result = 0;
		
		ISOCKET_MGR_LOCK( me );
		
		for( i = 0; i < repeat_time; i++ ) 
        {
			select_result = isocket_mgr_select(me);
			
			if( select_result < 0 ) 
            {
				break;
			}
		}
		
		ISOCKET_MGR_UNLOCK( me );
        
		ipanel_porting_task_sleep(5);

	}
    
	INFO("%s[isocket_mgr_thread_proc]socket thread stop working time = %d\n", MARK_SOCKET,time_ms());
    //设置线程结束工作标记
    me->thread_state = ISOCKET_THREAD_STATE_STOP;
    
    return;
}


/*扫描socket*/
static int isocket_mgr_select(isocket_mgr_t *handle)
{
    int sockid = 0, select_result =0, i = 0,ret = -1; 
	int tm = 0, maxfds = -1;
    int evntType = ISOCKET_EVENT_UNKNOWN;
    isocket_mgr_t *me = handle;

    IPANEL_FD_SET_S fds_r;
    IPANEL_FD_SET_S fds_w;
    IPANEL_FD_SET_S fds_e;

    IPANEL_FD_ZERO(&fds_r);
    IPANEL_FD_ZERO(&fds_w);
    IPANEL_FD_ZERO(&fds_e);

    for(i = 0 ; i < ISOCKET_MAX_NUMS ; i++)
	{
        sockid = me->socket_mem[i].sockId;
            
        if( !me->socket_mem[i].is_error
        	&& sockid >= 0 
        	&& me->socket_mem[i].flag_used )
        {		       
            IPANEL_FD_SET(sockid, &fds_e);

            // 只有当有一个socket发出tcp建链请求时，才需要检查write事件!
           
            if( me->socket_mem[i].type == ISOCKET_TYPE_TCP && me->socket_mem[i].is_connect ) 
            {
                IPANEL_FD_SET(sockid, &fds_w);
            }

			if( me->socket_mem[i].type == ISOCKET_TYPE_UDP || me->socket_mem[i].is_write == 1) 
            {
				IPANEL_FD_SET(sockid, &fds_r);		
			}

            if( maxfds < sockid ) 
            {
                maxfds = sockid;
			}

        }

		if( me->socket_mem[i].connect_start_time > 0 
			&& time_ms() - me->socket_mem[i].connect_start_time > 10000 ) 
		{
			isocket_mgr_notify_proc(me,&me->socket_mem[i], ISOCKET_EVENT_FAILED);
			me->socket_mem[i].connect_start_time = 0;
			me->socket_mem[i].is_error = 1;
			INFO("%s[isocket_mgr_select] socket connect first error sockid = %d.\n",MARK_SOCKET,sockid);
		}

	}
	
	if( maxfds < 0 ) 
    {
		return -3;
	}

    // 返回值为IPANEL_OK or IPANEL_ERR
	select_result = ipanel_porting_socket_select(maxfds+1, &fds_r, &fds_w, &fds_e, tm);

	if ( select_result < 0 ) 
    {
        return -1;
	}

    for (i = 0 ; i < ISOCKET_MAX_NUMS ; i++)
    {
        sockid = me->socket_mem[i].sockId;
        if ( !me->socket_mem[i].is_error
			&& sockid >= 0 
			&& me->socket_mem[i].flag_used )
        {
            evntType = ISOCKET_EVENT_UNKNOWN;
			
			if( me->socket_mem[i].connect_start_time > 0 
				&& time_ms() - me->socket_mem[i].connect_start_time > 10000 ) 
			{
				isocket_mgr_notify_proc(me,&me->socket_mem[i], ISOCKET_EVENT_FAILED);
				me->socket_mem[i].connect_start_time = 0;
				me->socket_mem[i].is_error = 1;
    			INFO("%s[isocket_mgr_select] socket connect second error sockid = %d.\n",MARK_SOCKET,sockid);
				break;
			}

			// check excption 
			if (IPANEL_FD_ISSET(sockid, &fds_e))
			{
                //【这个地方可以判断网络是否断开，然后再根据需要决定是否发送数据】

				ISOCKET_ASSERT(1);
				me->socket_mem[i].is_error = 1;
				evntType = ISOCKET_EVENT_ERROR;
				isocket_mgr_notify_proc(me,&me->socket_mem[i], evntType);
				INFO("%s[isocket_mgr_select] socket select error event=%d,sockid = %d.\n",MARK_SOCKET,evntType,sockid);
				break;
            }

			// check read 
			if (IPANEL_FD_ISSET(sockid, &fds_r))
			{
				// 一个处于listen状态的tcp socket的read事件代表的是accept事件!
				if( me->socket_mem[i].type == ISOCKET_TYPE_TCP && me->socket_mem[i].is_listen ) 
                {
					evntType = ISOCKET_EVENT_ACCEPT;
					INFO("%s[isocket_mgr_select] socket is accept event=%d,sockid = %d.\n",MARK_SOCKET,evntType,sockid);
				} 
                else 
                {                   
					evntType = ISOCKET_EVENT_READ;
					me->socket_mem[i].is_read = 1;
					//INFO("%s[isocket_mgr_select] socket is ready event=%d,sockid = %d.\n",MARK_SOCKET,evntType,sockid);
				}
				ret = isocket_mgr_notify_proc(me,&me->socket_mem[i], evntType);
			}

			// check write
			if (IPANEL_FD_ISSET(sockid, &fds_w))
			{
    			INFO("%s[isocket_mgr_select]IPANEL_FD_ISSET SOCKET_EVENT_WRITE type = %d, is_connect = %d.\n",MARK_SOCKET,me->socket_mem[i].type, me->socket_mem[i].is_connect);

                if( me->socket_mem[i].type == ISOCKET_TYPE_TCP && me->socket_mem[i].is_connect ) 
                {
					me->socket_mem[i].is_connect = 0; // 连接建立ok，则标志取消
					me->socket_mem[i].is_write   = 1;
                    evntType = ISOCKET_EVENT_CONNECT;
					me->socket_mem[i].connect_start_time = 0;
					isocket_mgr_notify_proc(me,&me->socket_mem[i], evntType);
				}
            }
        }
    }

    return ret;
    
}

/******************************************************************************************
函数名称    : socket_mgr_notify_proc(socket_mgr_t *handle,socket_t *socket,int msg)
功能        : 处理socket的各种事件
输入参数    :
              handle:      socket mgr
              socket:      事件对应的socket
              msg:         socket事件
              
输出参数    : 无.
返回值      :  0-->失败;1-->正常退出;2-->可以继续收数据
*******************************************************************************************/
static int isocket_mgr_notify_proc(isocket_mgr_t *handle,isocket_t *socket,int msg)
{
    int i = 0,ret = 2,repeat_timer = ISOCKET_MAX_RECEIVE_NUM;
    isocket_mgr_t *me = handle;

	FAILED_RETURNX( !me || !socket,IPANEL_ERR);
	
	// 直接将数据读取出来然后传送给指定模块 除了RTP模块其他的数据都写读出来
    if ( msg == ISOCKET_EVENT_READ )
    { 
        if(socket->sockId<=0)
        {
            return IPANEL_ERR;
        }
        
        do 
        {
            if( socket->interleave_type != ISOCKET_INTER_HTTP && socket->type == ISOCKET_TYPE_TCP ) 
            {
                
				for( i = 0; i < repeat_timer; i++  ) 
                {
					ret = isocket_interleave_recv( me,socket);
					if( ret <= 0 ) 
                    {
						break;
					}
				}
            }
            else if(socket->interleave_type > 0 && socket->interleave_type == ISOCKET_INTER_HTTP) 
            {

                for( i = 0; i < repeat_timer; i++  ) 
                {
					ret = isocket_http_recv(me,socket);
					if( ret <= 0 ) 
                    {
						break;
					}
				}
			}
            else 
            {
			
				if ( socket->type == ISOCKET_TYPE_TCP || socket->type == ISOCKET_TYPE_TCP_ACCEPT ) 
                {   
					ret = isocket_tcp_recv( me, socket);
				} 
                else 
                {
					for( i = 0; i < repeat_timer; i++  ) 
                    {
						ret = isocket_udp_recvfrom( me, socket);
						if( ret <= 0 ) 
                        {
							break;
						}
					}
				}
            }

			#ifdef 	HAVE_SUPPORT_HD
			ISOCKET_ASSERT(i == repeat_timer);
			#endif

            
			break;
		} while( repeat_timer > 0  );

		//INFO("%s[isocket_mgr_notify_proc]repeat_timer = %d.\n",MARK_SOCKET,i);
    }
    else if ( msg == ISOCKET_EVENT_CONNECT)
    {
        client_proc_socket((client_t*)socket->client_handle, CLIENT_EVENT_SOCKET_MSG, socket->indx, SOCKET_CONNECT_SUCCESS);
        ret = -1;
    } 
    else if ( msg == ISOCKET_EVENT_FAILED)
    {
        client_proc_socket((client_t*)socket->client_handle, CLIENT_EVENT_SOCKET_MSG, socket->indx, SOCKET_CONNECT_FAILED);
        ret = -1;
	}
    else if ( msg == ISOCKET_EVENT_ERROR)
    {
        client_proc_socket((client_t*)socket->client_handle, CLIENT_EVENT_SOCKET_MSG, socket->indx, SOCKET_EXCEPTION_ERROR);
        ret = -1;
	}
	
	return ret;
    
}

/******************************************************************************************
函数名称    : socket_mgr_open_item( socket_mgr_t *handle,int index )
功能        : 根据index获取socket mgr item
输入参数    :
              handle:      socket mgr
              index:       -1表示获取一个空的item；大于0表示获取index指示item

              
输出参数    : 无.
返回值      : index指示的socket mgr item
*******************************************************************************************/

static isocket_t* isocket_mgr_open_item( isocket_mgr_t *handle,int index )
{
	int i = 0;
    isocket_mgr_t *me = handle;

	FAILED_RETURNX( !me,NULL);

	if( index == -1 ) 
    {
		INFO("%s[isocket_mgr_open_item]open a new socket item\n",MARK_SOCKET);
		me->base_index = me->base_index > 65000 ? 0: me->base_index;
		
		for( i = 0; i < ISOCKET_MAX_NUMS; i++ ) 
        {
			if( me->socket_mem[i].flag_used == 0 ) 
            {
                memset(&me->socket_mem[i],0,sizeof(isocket_t));
				me->socket_mem[i].indx = i+ISOCKET_MAX_INDEX_START+10+me->base_index;
				me->socket_mem[i].flag_used = 1;
				me->socket_mem[i].recv_packet_len   = 1400;                
                me->socket_mem[i].recv_buffer_len = 0;

				me->base_index += ISOCKET_MAX_NUMS;
				break;
			}
		}	
	} 
    else 
    {
		for( i = 0; i < ISOCKET_MAX_NUMS; i++ ) 
        {
			if( me->socket_mem[i].flag_used ) 
            {
				if( me->socket_mem[i].indx == (unsigned int)index )
				{
        			INFO("%s[isocket_mgr_open_item]me->socket_mem[i].flag_used = %d,indx=%d,sock=%d.\n",MARK_SOCKET,me->socket_mem[i].flag_used,me->socket_mem[i].indx,index);
					break;
				}
			}
		}	
	}

	if( i >= ISOCKET_MAX_NUMS ) 
    {
		ISOCKET_ASSERT(1);
		return NULL;
	}

	return &me->socket_mem[i];
}


static int isocket_mgr_close_item( isocket_mgr_t *handle,isocket_t *item )
{
	int i = 0;
    isocket_mgr_t *me = handle;
    
	FAILED_RETURNX( !me || !item ,IPANEL_ERR);

	for( i = 0; i < ISOCKET_MAX_NUMS; i++ ) 
    {
		if( &me->socket_mem[i] == item ) 
        {
			memset(&me->socket_mem[i],0,sizeof(isocket_t));
			break;
		}
	}

	FAILED_RETURNX( i >= ISOCKET_MAX_NUMS, -1 );

	return IPANEL_OK;
}


/*socket open*/
int isocket_open(int type, int protocol, client_t *handle)
{
	isocket_t *item = NULL;
#ifdef HAVE_SUPPORT_HD
	int		 recv_buf_size = 512 * 1024;
#else
	int		 recv_buf_size = 256 * 1024;
#endif

	FAILED_RETURNX( g_socket_mgr == NULL || (type != ISOCKET_TYPE_TCP &&  type != ISOCKET_TYPE_UDP), IPANEL_ERR);
 
	INFO("%s[isocket_open] type = %d, protocol = %d, time = %d\n", MARK_SOCKET, type, protocol, time_ms());

	ISOCKET_MGR_LOCK( g_socket_mgr );
		
	item = isocket_mgr_open_item( g_socket_mgr,-1 );
	
	if( item ) 
	{

        if(IPANEL_IP_VERSION_6 != protocol)
        {
    		item->sockId = ipanel_porting_socket_open(IPANEL_AF_INET,
    							(type == ISOCKET_TYPE_TCP)?IPANEL_SOCK_STREAM : IPANEL_SOCK_DGRAM,
                                 IPANEL_IPPROTO_IP);
            item->protocol = IPANEL_IP_VERSION_4;
        }
        else
        {
    		item->sockId = ipanel_porting_socket_open(IPANEL_AF_INET6,
    							(type == ISOCKET_TYPE_TCP)?IPANEL_SOCK_STREAM : IPANEL_SOCK_DGRAM,
                                 IPANEL_IPPROTO_IP);
            item->protocol = IPANEL_IP_VERSION_6;
        }

        
		item->type = type;
		if( item->sockId < 0 ) 
		{
			INFO("%s[isocket_open] call ipanel_porting_socket_open is failed\n", MARK_SOCKET);
			isocket_mgr_close_item( g_socket_mgr, item );
			item = NULL;
		} 
		else 
		{
		#ifdef HAVE_RTSOCK_ASYNC
			ipanel_porting_socket_ioctl(item->sockId,IPANEL_FIONBIO,1) ;
		#endif
			ipanel_porting_socket_setsockopt(item->sockId,IPANEL_SOL_SOCKET,IPANEL_SO_RCVBUF,&recv_buf_size,sizeof(int));
			item->connect_start_time = 0;
		}
	}
	else 
	{	
		INFO("%s[isocket_open] call socket_mgr_open_item is failed\n", MARK_SOCKET);
	}

	ISOCKET_MGR_UNLOCK( g_socket_mgr );

	FAILED_RETURNX( !item, -1 );
	INFO("%s[isocket_open] open socket success, socket index = %d,time = %d\n", MARK_SOCKET,item->indx,time_ms());
    item->client_handle = (unsigned int)handle;

    return item->indx;
}


/*socket close*/
int isocket_close(unsigned int socket_id)
{
	isocket_t *item = NULL;
	int ret = -1,sock_id = -1,index = socket_id;
	
	FAILED_RETURNX( g_socket_mgr == NULL || index < ISOCKET_MAX_INDEX_START, IPANEL_ERR );

	INFO("%s[isocket_close] close socket index = %d\n", MARK_SOCKET,index);	

	ISOCKET_MGR_LOCK( g_socket_mgr );
	
	item = isocket_mgr_open_item( g_socket_mgr,index );
	
	if( item ) 
	{
		sock_id = item->sockId;
		isocket_mgr_close_item( g_socket_mgr,item );
        item = NULL;
	}
	
	ISOCKET_MGR_UNLOCK( g_socket_mgr );
	
	if( sock_id != -1 ) 
	{
		ret = ipanel_porting_socket_close( sock_id );
	}
	INFO("%s[isocket_close] close socket success ret = %d\n", MARK_SOCKET,ret);	

	return ret;
}

/*socket bind*/
int isocket_bind(unsigned int socket_id, unsigned int addr, int port)
{
	isocket_t *item = NULL;
	int ret = -1,sockId = -1,reuseAddr = 1,index = socket_id;
    IPAddr0 *portingIPAddr = NULL;

	FAILED_RETURNX( g_socket_mgr == NULL || index < ISOCKET_MAX_INDEX_START || !addr, IPANEL_ERR );
    
	INFO("%s[isocket_bind] index = %d, ip = %x, port = %d, time = %d\n",MARK_SOCKET, index, addr, port, time_ms());
	
    portingIPAddr = (IPAddr0*)addr;
	
	ISOCKET_MGR_LOCK( g_socket_mgr );
	
	item = isocket_mgr_open_item( g_socket_mgr,index );
	
	if( item && !item->is_error ) 
	{
		sockId = item->sockId;
	} 
	else 
	{
		ISOCKET_ASSERT(1);
	}
	
	ISOCKET_MGR_UNLOCK( g_socket_mgr );

	if( sockId != -1 ) 
	{
		ipanel_porting_socket_setsockopt(item->sockId,
			IPANEL_SOL_SOCKET, IPANEL_SO_REUSEADDR,
			&reuseAddr, sizeof(reuseAddr));

		ret = ipanel_porting_socket_bind( sockId, portingIPAddr, port);

		if( ret < 0 ) 
		{
			INFO("%s[isocket_bind] bind failed socket = %d\n",MARK_SOCKET,sockId);
		}
	} 
	else
	{
		INFO("%s[isocket_bind] rtsock_mgr_open_item failed\n",MARK_SOCKET);
	}

	return ret >= 0 ? 1 : -1;
    
}

/*socket connect*/
int isocket_connect(unsigned int socket_id, unsigned int addr, int port)
{
	isocket_t *item = NULL;
	int ret = -1,sock_id = -1,index = socket_id;
    IPAddr0 *portingIPAddr = NULL;

	FAILED_RETURNX( g_socket_mgr == NULL || index < ISOCKET_MAX_INDEX_START || !addr, IPANEL_ERR );
	
	ISOCKET_MGR_LOCK( g_socket_mgr );
	
	item = isocket_mgr_open_item( g_socket_mgr,index );
	
	if( item && !item->is_error ) 
	{
		sock_id = item->sockId;
	}
	else 
	{
		ISOCKET_ASSERT(1);
	}    
	
	ISOCKET_MGR_UNLOCK( g_socket_mgr );

    portingIPAddr = (IPAddr0*)addr;

	if( sock_id != -1 && portingIPAddr) 
	{
        if(IPANEL_IP_VERSION_6 != portingIPAddr->version)
        {
        	INFO("%s[isocket_connect]connect ipv4:%d.%d.%d.%d,port:%d.index:%d, time = %u\n",
                        MARK_SOCKET,
        				(portingIPAddr->addr.ipv4>>24) & 0xFF,
        				(portingIPAddr->addr.ipv4>>16) & 0xFF,
        				(portingIPAddr->addr.ipv4>>8) & 0xFF,
        				(portingIPAddr->addr.ipv4) & 0xFF,
        				port,
        				item->indx,
        				time_ms()
        			);
        }
        else
        {
            INFO("%s[isocket_connect]connect ipv6 %x:%x:%x:%x:%x:%x:%x:%x,port:%d,index:%d,time=%u\n",
                    MARK_SOCKET,
                    portingIPAddr->addr.ipv6.ip[0],
                    portingIPAddr->addr.ipv6.ip[1],
                    portingIPAddr->addr.ipv6.ip[2],
                    portingIPAddr->addr.ipv6.ip[3],
                    portingIPAddr->addr.ipv6.ip[4],
                    portingIPAddr->addr.ipv6.ip[5],
                    portingIPAddr->addr.ipv6.ip[6],
                    portingIPAddr->addr.ipv6.ip[7],
                    port,
                    item->indx,
                    time_ms()
                    );
        }
  	
        ret = ipanel_porting_socket_connect( sock_id, portingIPAddr, port);
	
    	ISOCKET_MGR_LOCK( g_socket_mgr );

#ifdef HAVE_RTSOCK_ASYNC
    	ret = item->type == ISOCKET_TYPE_TCP ? 0 :ret;
    	item->connect_start_time = (unsigned int)time_ms();
#endif

    	if( ret == 0 ) 
    	{
    		item->is_connect = 1;
    		ret = 0;
    	}

    	ISOCKET_MGR_UNLOCK( g_socket_mgr );
	}

	ISOCKET_ASSERT( ret != 0);

	return ret == 0 ? 1 : -1;
}

/*socket send*/
int isocket_send(unsigned int socket_id, char *buf, unsigned int buf_len, int flags)
{
    
	isocket_t *item = NULL;
	int ret = -1,sock_id = -1,start_time = (int)time_ms(),can_send = 1,index = socket_id;
	
	FAILED_RETURNX( g_socket_mgr == NULL || index < ISOCKET_MAX_INDEX_START || !buf, IPANEL_ERR );

	INFO("%s[isocket_send] index = %d, buf_len = %d, flags = %d, time = %d\n", MARK_SOCKET, index, buf_len, flags, time_ms());
	
	//ISOCKET_MGR_LOCK( g_socket_mgr );
	
	item = isocket_mgr_open_item( g_socket_mgr,index );
	
	if( item && !item->is_error ) {
		sock_id = item->sockId;
	}
	
	//ISOCKET_MGR_UNLOCK( g_socket_mgr );

	if( sock_id != -1 ) {
		#ifdef HAVE_RTSOCK_ASYNC
			IPANEL_FD_SET_S fds_w, fds_e, fds_r;
			int resend_timer = 10,send_size = 0;			
			ret = 0;			
			do {
 
 				IPANEL_FD_ZERO(&fds_w);
 				IPANEL_FD_ZERO(&fds_e);
 				IPANEL_FD_ZERO(&fds_r);
				
 				IPANEL_FD_SET(sock_id, &fds_w);
 				IPANEL_FD_SET(sock_id, &fds_e);
				IPANEL_FD_SET(sock_id, &fds_r);
				
				if(ipanel_porting_socket_select(sock_id + 1, &fds_r, &fds_w, &fds_e, 0) < 0)
				{
					INFO("%s[isocket_send] ipanel_porting_socket_select faild\n",MARK_SOCKET);
					ipanel_porting_task_sleep(10);
					continue;
				}
				else if(IPANEL_FD_ISSET(sock_id, &fds_w))
				{
					ret = ipanel_porting_socket_send( sock_id, buf,buf_len,flags);
					if( ret > 0 && ret < (int)buf_len ) 
					{
						send_size += ret;
						buf = buf + ret;
						buf_len -= ret;
						ret = ret >=0 ? send_size : -1;
						resend_timer--;
						ipanel_porting_task_sleep(10);
					} 
					else 
					{
						send_size += ret;
						ret = ret >=0 ? send_size : -1;
						break;
					}					
				}
				else
					ipanel_porting_task_sleep(10);
			} while( time_ms() - start_time < 1000 && resend_timer > 0 ); 
		#else
			ret = ipanel_porting_socket_send( sock_id, buf,buf_len,flags);
		#endif
	}

	ISOCKET_ASSERT(ret < (int)buf_len);
	INFO("%s[isocket_send] send data len = %d, time = %d\n", MARK_SOCKET, ret, time_ms());
	
	return ret;   
}

/*socket sendto*/
int isocket_sendto(unsigned int socket_id, char *buf, unsigned int buf_len, int flags, unsigned int addr, int port)
{
	isocket_t *item = NULL;
	int ret = -1,sock_id = -1,index = socket_id;
    IPAddr0 *portingIPAddr = NULL;
  
	FAILED_RETURNX( g_socket_mgr == NULL || index < ISOCKET_MAX_INDEX_START || !buf || !addr, IPANEL_ERR );

    portingIPAddr = (IPAddr0*)addr;
	
	//ISOCKET_MGR_LOCK( g_socket_mgr );
	
	item = isocket_mgr_open_item( g_socket_mgr,index );
	
	if( item && !item->is_error ) 
	{
		sock_id = item->sockId;
	}
	
	//ISOCKET_MGR_UNLOCK( g_socket_mgr );
	
	if( sock_id != -1 ) 
	{
		ret = ipanel_porting_socket_sendto( sock_id, buf,buf_len,flags,portingIPAddr,port);
	}

	return ret;
}

/*socket recv*/
int isocket_recv(unsigned int socket_id, char *buf, unsigned int buf_len, int flags)
{
	isocket_t *item = NULL;
	int ret = -1,sock_id = -1,index = socket_id;
  
	FAILED_RETURNX( g_socket_mgr == NULL || index < ISOCKET_MAX_INDEX_START|| !buf, IPANEL_ERR );

	ISOCKET_MGR_LOCK( g_socket_mgr );
	
	item = isocket_mgr_open_item( g_socket_mgr,index );
	
	if( item && !item->is_error ) 
	{
		sock_id = item->sockId;
	}
	
	ISOCKET_MGR_UNLOCK( g_socket_mgr );
	
	if( sock_id != -1 ) 
	{
		ret = ipanel_porting_socket_recv( sock_id, buf,buf_len,flags);
	}
	
	return ret;
}

/*recvfrom*/
int isocket_recvfrom(unsigned int socket_id, char *buf, unsigned int buf_len, int flags, unsigned int addr, int *port)
{
	isocket_t *item = NULL;
	int   ret = -1,sock_id = -1,index = socket_id,from_port = 0;
    IPAddr0 *portingIPAddr = NULL;
    IPAddr0 AddrV6 = {0};  

	FAILED_RETURNX( g_socket_mgr == NULL || index < ISOCKET_MAX_INDEX_START || !buf, IPANEL_ERR );

    portingIPAddr = &AddrV6;

	ISOCKET_MGR_LOCK( g_socket_mgr );
	
	item = isocket_mgr_open_item( g_socket_mgr,index );
	
	if( item && !item->is_error ) 
	{
		sock_id = item->sockId;
	}
	
	ISOCKET_MGR_UNLOCK( g_socket_mgr );

    portingIPAddr->version = item->protocol;
	
	if( sock_id != -1 ) 
	{
		ret = ipanel_porting_socket_recvfrom( sock_id, buf,buf_len,flags,portingIPAddr,&from_port);
	
		if( ret > 0 ) 
		{
			if( addr ) 
			{
				memcpy((IPAddr0*)addr,portingIPAddr,sizeof(IPAddr0));
			}
			if( port ) 
			{
				*port = from_port;
			}
		}
	}

	return ret;
}

/*join multicast*/
int isocket_join_multicast(unsigned int socket_id, unsigned int addr)
{
	isocket_t *item = NULL;
	IPANEL_IP_MREQ		ipMreq = {0};
    IPANEL_IPV6_MREQ    ipv6Mreq = {0};
	int ret = -1,reuseAddr = 1,sock_id = -1,index = socket_id;
    IPAddr0 *portingIPAddr = NULL;


	FAILED_RETURNX( g_socket_mgr == NULL || index < ISOCKET_MAX_INDEX_START || !addr, IPANEL_ERR );

    portingIPAddr = (IPAddr0*)addr;

    if(IPANEL_IP_VERSION_4 == portingIPAddr->version)
    {
        FAILED_RETURNX( !((portingIPAddr->addr.ipv4>>24) >= 224 && (portingIPAddr->addr.ipv4>>24) <= 239),IPANEL_ERR );
    }

	ISOCKET_MGR_LOCK( g_socket_mgr );
	
	item = isocket_mgr_open_item( g_socket_mgr,index );

	if( item && !item->is_error ) 
	{
		sock_id = item->sockId;
	} 
	else 
	{
		INFO("%s[isocket_join_multicast] rtsock_mgr_open_item failed\n",MARK_SOCKET);
	}

	ISOCKET_MGR_UNLOCK( g_socket_mgr );

	INFO("%s[isocket_join_multicast] item = 0x%x, item->sockId = %d\n", MARK_SOCKET, item, item->sockId);

	if( sock_id != -1 && portingIPAddr) 
	{
		ipanel_porting_socket_setsockopt(sock_id,
			IPANEL_SOL_SOCKET, IPANEL_SO_REUSEADDR,
			&reuseAddr, sizeof(reuseAddr));
		

        if(IPANEL_IP_VERSION_6 != portingIPAddr->version)
        {
			ipMreq.imr_multiaddr.addr = portingIPAddr->addr.ipv4;		
		#ifdef HAVE_PROTOCOLS_OSI
			//Voip_OSI_gethostaddr((unsigned int*)&(ipMreq.imr_interface.addr)); // local IP , or use ANY
		#endif

    		ret = ipanel_porting_socket_setsockopt( sock_id,
    			IPANEL_IPPROTO_IP, IPANEL_IP_ADD_MEMBERSHIP,
    			(char *)&ipMreq, sizeof(IPANEL_IP_MREQ));
        }
        else
        {

    		memcpy(&ipv6Mreq.imr_multiaddr,portingIPAddr,sizeof(IPAddr0));		

    		ret = ipanel_porting_socket_setsockopt( sock_id,
    			IPANEL_IPPROTO_IPV6, IPANEL_IP_ADD_MEMBERSHIP,
    			(char *)&ipv6Mreq, sizeof(IPANEL_IPV6_MREQ));
        }
    

		if( ret < 0 ) 
		{
			INFO("%s[isocket_join_multicast] join multicast failed\n",MARK_SOCKET);
		} 
		else 
		{
			INFO("%s[isocket_join_multicast] join multicast success\n",MARK_SOCKET);
        	ISOCKET_MGR_LOCK( g_socket_mgr );
    		memcpy(&item->multicast_addr,portingIPAddr,sizeof(IPAddr0));		
        	ISOCKET_MGR_UNLOCK( g_socket_mgr );
		}
	}
	return ret >= 0 ? 1 : ret;
}

/*leave multicast*/
int isocket_leave_multicast(unsigned int socket_id, unsigned int addr)
{
	isocket_t *item = NULL;
	IPANEL_IP_MREQ ipMreq = {0};
    IPANEL_IPV6_MREQ    ipv6Mreq = {0};
	int ret = -1,sock_id = -1,index = socket_id;
	
	FAILED_RETURNX( g_socket_mgr == NULL || index < ISOCKET_MAX_INDEX_START, IPANEL_ERR );
	
	ISOCKET_MGR_LOCK( g_socket_mgr );
	
	item = isocket_mgr_open_item( g_socket_mgr,index );
	
	if( item && !item->is_error && item->multicast_addr.version != 0 ) 
	{
		sock_id = item->sockId;		
	} 
	else if( !item || item->is_error) 
	{
		INFO("%s[isocket_leave_multicast] rtsock_mgr_open_item failed\n",MARK_SOCKET);
	}
	
	ISOCKET_MGR_UNLOCK( g_socket_mgr );
	
	FAILED_RETURNX(!item,-1);

	INFO("%s[isocket_leave_multicast] leave multicast start time %d\n",MARK_SOCKET,time_ms());

	if( sock_id != -1 ) 
	{

		INFO("%s[isocket_leave_multicast] item = 0x%x, item->sockId = %d\n", MARK_SOCKET, item, item->sockId);

        if(IPANEL_IP_VERSION_6 != item->multicast_addr.version)
        {
            ipMreq.imr_multiaddr.addr = item->multicast_addr.addr.ipv4;
		#ifdef HAVE_PROTOCOLS_OSI
    		//Voip_OSI_gethostaddr((unsigned int*)&(ipMreq.imr_interface.addr)); // local IP , or use ANY
        #endif
    		ret = ipanel_porting_socket_setsockopt( sock_id,
    			IPANEL_IPPROTO_IP, IPANEL_IP_DROP_MEMBERSHIP,
    			(char *)&ipMreq, sizeof(IPANEL_IP_MREQ));
        }
        else
        {
    		memcpy(&ipv6Mreq.imr_multiaddr,(IPAddr0*)&item->multicast_addr,sizeof(IPAddr0));		

    		ret = ipanel_porting_socket_setsockopt( sock_id,
    			IPANEL_IPPROTO_IPV6, IPANEL_IP_DROP_MEMBERSHIP,
    			(char *)&ipv6Mreq, sizeof(IPANEL_IPV6_MREQ));
        }

    
		if( ret < 0 ) 
		{
			INFO("%s[isocket_leave_multicast] leave multicast failed\n",MARK_SOCKET);
		} 
		else 
		{
			INFO("%s[isocket_leave_multicast] leave multicast success\n",MARK_SOCKET);
		}
	}
	INFO("%s[isocket_leave_multicast] leave multicast end time %d\n",MARK_SOCKET,time_ms());

	memset(&item->multicast_addr,0,sizeof(IPAddr0));		
	
	return ret;
}

/*socket set interleave*/
int isocket_set_interleave(unsigned int socket_id, unsigned char interleave_type)
{
	isocket_t *item = NULL;
	int ret = -1,index = socket_id;
	
	FAILED_RETURNX( g_socket_mgr == NULL || index < ISOCKET_MAX_INDEX_START, IPANEL_ERR );

	INFO("%s[isocket_set_interleave] index = %d, interleave_type = %d\n", MARK_SOCKET, index, interleave_type);	

	ISOCKET_MGR_LOCK( g_socket_mgr );
	
	item = isocket_mgr_open_item( g_socket_mgr,index );
	
	if( item ) 
	{
		item->interleave_type = interleave_type;
		ret = IPANEL_OK;
	}
	
	ISOCKET_MGR_UNLOCK( g_socket_mgr );
	
	
	return ret;
}


/*socket getsockname*/
int isocket_getsockname(unsigned int socket_id, unsigned int addr, int *port)
{
	isocket_t *item = NULL;
	int   ret = IPANEL_ERR,sock_id = -1,index = socket_id;
    IPAddr0 *portingIPAddr = NULL;

	FAILED_RETURNX( g_socket_mgr == NULL || index < ISOCKET_MAX_INDEX_START || !addr || !port, IPANEL_ERR );

    portingIPAddr = (IPAddr0*)addr;

	//ISOCKET_MGR_LOCK( g_socket_mgr );
	
	item = isocket_mgr_open_item( g_socket_mgr,index );
	
	if( item && !item->is_error ) 
	{
		sock_id = item->sockId;
	}
	
	//ISOCKET_MGR_UNLOCK( g_socket_mgr );

	INFO("%s[isocket_getsockname] index = %d, version = %d\n", MARK_SOCKET, index, portingIPAddr->version);	

	if( sock_id != -1 && portingIPAddr && port)
        ret = ipanel_porting_socket_getsockname(sock_id, portingIPAddr, port);

	INFO("%s[isocket_getsockname] index = %d, port = %d\n", MARK_SOCKET, index, *port);	

    return ret;    
}


static int isocket_interleave_recv(isocket_mgr_t *handle,isocket_t *item)
{
	int data_len = 0, i = 0,content_length = 0,rtsp_len = 0,ret = 0;
	char *content_end = NULL, *content_have_length = NULL;
	int flag_no_data = 0,flag_another_rtsp = 0;/*以\r\n结尾的*/
    isocket_mgr_t *me = handle;
	
	FAILED_RETURNX( !me || !item , IPANEL_ERR );

	if( item->flag_wait_rtsp ) 
    {

        char *rtsp_start = NULL;
        
        flag_no_data = 1;
		flag_another_rtsp = 0;
        content_length = 0;
		INFO("%s[isocket_interleave_recv]rtsock_mgr_interleave_recv rtsp packet\n",MARK_SOCKET);

NEXT_RTSP_REPLAY:
        rtsp_start = (char*)isocket_search_rtsp_command(me,item);
        if(!rtsp_start)	
        {
            ISOCKET_ASSERT(1);                
			isocket_handle_error_data(me,item);
			return 0;
        }
        
		content_end = strstr( rtsp_start,"\r\n\r\n" );
        
		if(!content_end)
		{
			content_have_length = strstr(rtsp_start, "Content-Length");
			if( content_have_length == NULL ) 
            {
				content_have_length = strstr(rtsp_start, "Content-length");
			}
			if(content_have_length && strstr(rtsp_start,"\r\n")&&(content_have_length < strstr(rtsp_start,"\r\n")))
				flag_another_rtsp = 1;
		}

        
		if( content_end || flag_another_rtsp) 
        {
			char *content_length_pos = NULL;
            
			content_length_pos = strstr(rtsp_start+4, "Content-Length");

			INFO("%s[isocket_interleave_recv] recv RTSP:\n",MARK_SOCKET);	
			if( content_length_pos == NULL ) 
            {
				content_length_pos = strstr(rtsp_start+4, "Content-length");
			}

			if( content_length_pos && content_length_pos < item->recv_buffer + item->recv_buffer_len ) 
            {
				content_length_pos += sizeof("Content-length");

				while( content_length_pos[0] == ':' 
					|| content_length_pos[0] == ' '
					|| content_length_pos[0] == '\t' ) 
					content_length_pos++;
                
				content_length = strtol(content_length_pos,NULL,10);
			}
            
			if(flag_another_rtsp)
			{
                content_end = strstr(rtsp_start,"\r\n");
				rtsp_len =  content_end + 2 - rtsp_start + content_length;
			}
			else
			{
				rtsp_len =  content_end + 4 - rtsp_start + content_length;
			}

			if( rtsp_len <= item->recv_buffer + item->recv_buffer_len - rtsp_start) 
            {
				if( IPANEL_OK != client_proc_socket((client_t*)item->client_handle,CLIENT_EVENT_SOCKET_DATA,(int)rtsp_start,rtsp_len) ) 
                {
					ISOCKET_ASSERT(1);
				}

				if( (item->recv_buffer + item->recv_buffer_len > rtsp_start + rtsp_len)
					&& rtsp_start[rtsp_len] == 0x0 ) 
				{
					ISOCKET_ASSERT(1);
					rtsp_len++;
				}

				ISOCKET_ASSERT((item->recv_buffer + item->recv_buffer_len) - (rtsp_start + rtsp_len) < 0);
				
				memmove(rtsp_start,rtsp_start+rtsp_len,(item->recv_buffer + item->recv_buffer_len) - (rtsp_start + rtsp_len));
				item->flag_wait_rtsp = 0;
				item->recv_buffer_len -= rtsp_len;

                //检查是否还有一个rtsp紧接着在buffer中，如果存在就再接收一次
                if(item->recv_buffer_len > 8)
                {
                    if(isocket_analysis_rtsp_command(me,item)==IPANEL_OK)
                    {
                        content_end = NULL;
                        content_length = 0;
                		flag_another_rtsp = 0;
                        rtsp_start = NULL;
                        goto NEXT_RTSP_REPLAY;
                    }
                }                
                //检查结束
                
				return -1;
			} 
		}
        else
        {
            //RTSP应答不完整，继续下次接收数据
			item->flag_wait_rtsp = 0;  
			INFO("%s[isocket_interleave_recv] recv RTSP,but rtsp is not complete!!\n",MARK_SOCKET);	
        }
	} 
    else if( item->flag_wait_data ) 
    {

        char *rtsp_start = NULL;
		INFO("%s[isocket_interleave_recv] recv data:\n",MARK_SOCKET);

        //检查一个完整的RTP包是否被RTSP分开了
        rtsp_start = (char*)isocket_search_rtsp_command(me,item);
        if(rtsp_start)
        {
            if(item->recv_buffer + item->need_recv_len > rtsp_start)
            {
    			INFO("%s[isocket_interleave_recv] recv data but data is separate by rtsp replay!!\n",MARK_SOCKET);
                item->flag_wait_data = 0;
                item->flag_wait_rtsp = 1;
                return 1;
            }
        }        

        //保证完整的RTP包再接收，否则就放在接收buffer里面，等待后续数据
        if(item->need_recv_len > item->recv_buffer_len)
        {
            item->flag_wait_data = 0;
            return -1;
        }
        
		if(NULL == item->mem_block) 
        {
        	item->mem_block = client_get_unused_block((client_t*)item->client_handle,item->need_recv_len);
			
			if(NULL == item->mem_block) 
            { 
				INFO("%s[isocket_interleave_recv] no new buffer calloc.skip.\n",MARK_SOCKET);
				return -1;
			}
		}
		
		data_len = (int)item->need_recv_len;

		if( data_len <= item->recv_buffer_len ) 
        {
			memcpy((unsigned char *)(item->mem_block->node_buffer), (unsigned char *)(item->recv_buffer), data_len);
			memmove((char *)(item->recv_buffer), (char *)(item->recv_buffer+data_len), item->recv_buffer_len - data_len);

			item->mem_block->node_buffer_data_len = (unsigned int)data_len;
			item->recv_buffer_len -= data_len;
			item->flag_wait_data = 0;
            client_proc_socket((client_t*)item->client_handle, CLIENT_EVENT_SOCKET_MEDIA, (int)item->mem_block, 0);
			item->mem_block = NULL;
			flag_no_data = 0;
		} 
		
	} 
    else 
    {
		unsigned short temp_len = 0;
		
		if( item->recv_buffer_len < ISOCKET_MAX_BUFFER_LEN )
        {
			ret = ipanel_porting_socket_recv( item->sockId,(char *)item->recv_buffer+item->recv_buffer_len,
				ISOCKET_MAX_BUFFER_LEN - item->recv_buffer_len,0);
			
			if( ret <= 0 ) 
            {
				INFO("%s[isocket_interleave_recv] recv 0,data_len = %d.\n",MARK_SOCKET,item->recv_buffer_len);
                if(item->recv_buffer_len <= 4)
    				return -1;
			}
            
			flag_no_data = ret < ISOCKET_MAX_BUFFER_LEN - item->recv_buffer_len ? 1 : 0;

		} 

        if(ret > 0)
    		item->recv_buffer_len += ret;

		if( item->recv_buffer_len >= 4 ) 
        {
			if( item->recv_buffer[0] == 0x24 
                && (item->recv_buffer[1] == 0x00 || item->recv_buffer[1] == 0x01 ||item->recv_buffer[1] == 0x02)) 
            {
				item->flag_wait_data = 1;
				memcpy((unsigned char*)&temp_len,item->recv_buffer+2,2);				
                item->need_recv_len = ntohs(temp_len) + 4;
			} 
            else 
            {
				item->flag_wait_rtsp = 1;
			}

			flag_no_data = 0;
		}
	}

	return flag_no_data ? -1 : 1;

}

static int isocket_http_recv(isocket_mgr_t *handle,isocket_t *item)
{
    return -1;
}

static int isocket_tcp_recv(isocket_mgr_t *handle,isocket_t *item)
{
    return -1;
}

static int isocket_udp_recvfrom(isocket_mgr_t *handle,isocket_t *item)
{
	IPAddr0 ipAddr = {0};
	int   port = 0,data_len = 0;
    isocket_mgr_t *me = handle;
    
	
	FAILED_RETURNX( !me || !item, IPANEL_ERR );

	ipAddr.version = item->protocol;

	if( item->recv_packet_len < 1500 ) 
    {
		item->recv_packet_len = 1500;
	}

    if(NULL == item->mem_block)
    	item->mem_block = client_get_unused_block((client_t*)item->client_handle,item->recv_packet_len );

	if(item->mem_block) 
    {
		if ( item->sockId >= 0) 
        {
			data_len = ipanel_porting_socket_recvfrom( item->sockId,
				(char *)item->mem_block->node_buffer, item->recv_packet_len, 0,
				&ipAddr, &port);

			if( data_len == item->recv_packet_len ) 
            {
				ISOCKET_ASSERT(1);
			}

			item->mem_block->node_buffer_data_len = data_len;
		} 
        else 
		{
			data_len = -1;
		} 
	} 
    else 
	{
		ISOCKET_ASSERT(1);
		//此时buffer已经撑爆了,sleep 1ms,让出时间片,避免频繁与推流线程竞争semphore
        ipanel_porting_task_sleep(1);
		return -1;
	}
    
	if( data_len > 0 ) 
    {
        //如果收到中兴NAT数据，就删除
        if(strncasecmp(item->mem_block->node_buffer, "ZXV10USS",8)==0)
        {
        	INFO("%s[isocket_udp_recvfrom] recv ZXV10USS NAT data!!!\n", MARK_SOCKET);
    		item->mem_block->node_buffer_data_len = 0;
        }
        client_proc_socket((client_t*)item->client_handle, CLIENT_EVENT_SOCKET_MEDIA, (int)item->mem_block, 0);
        item->mem_block = NULL;
		return data_len;
	} 
    else 
    {
        //这个地方也需要释放item->mem_block占用的空间
		item->mem_block->node_buffer_data_len = 0;
        client_proc_socket((client_t*)item->client_handle, CLIENT_EVENT_SOCKET_MEDIA, (int)item->mem_block, 0);
        item->mem_block = NULL;
    	INFO("%s[isocket_udp_recvfrom] no data to recv!!!\n", MARK_SOCKET);
		return -1;
	}
}

/***********************************************************************************************
 *该函数为寻找buffer中是否还有rtsp应答，偶尔rtsp命令太快，服务器会连续发送多次应答
 ***********************************************************************************************/
static int isocket_analysis_rtsp_command(isocket_mgr_t *handle,isocket_t* item)
{
    
	FAILED_RETURNX(!handle || !item,IPANEL_ERR);
    #if 0
	INFO("%s[isocket_analysis_rtsp_command]buffer data 0x%x,0x%x,0x%x,0x%x,0x%x,0x%x,0x%x,0x%x\n",MARK_SOCKET,
        item->recv_buffer[0],item->recv_buffer[1],item->recv_buffer[2],item->recv_buffer[3],
        item->recv_buffer[4],item->recv_buffer[5],item->recv_buffer[6],item->recv_buffer[7]);
    #endif
    if((strstr(item->recv_buffer,"RTSP/1.0") || strstr(item->recv_buffer,"ANNOUNCE")
        ||strstr(item->recv_buffer,"SET_PARAMETER")||strstr(item->recv_buffer,"REDIRECT"))
        && strstr(item->recv_buffer,"\r\n\r\n")
        && item->recv_buffer[0] != 0x24
        && item->recv_buffer[1] != 0x00
        && item->recv_buffer[1] != 0x01
        && item->recv_buffer[1] != 0x02)
        return IPANEL_OK;
    else
        return IPANEL_ERR;
    
}

/**************************************************************************************************
 *重新实现处理通过TCP通道下发的RTP数据包及RTSP交互命令，原因在于现在当RTSP把一个RTP分割开的时，我们
 *还无法有效处理。故重新实现。该函数为搜索RTSP应答
 **************************************************************************************************/
static char* isocket_search_rtsp_command(isocket_mgr_t *handle,isocket_t* item)
{
	char *str = NULL,*temp_str = NULL;

	FAILED_RETURNX(!handle || !item,NULL);

	str = strstr(item->recv_buffer,"ANNOUNCE");      
    if(str) 
    {
        temp_str = strstr(item->recv_buffer,"RTSP/1.0");
        if(temp_str && temp_str < str)
            return temp_str;
        else
            return str;
    }
	str = strstr(item->recv_buffer,"SET_PARAMETER"); 
    if(str) 
    {
        temp_str = strstr(item->recv_buffer,"RTSP/1.0");
        if(temp_str && temp_str < str)
            return temp_str;
        else
            return str;
    }
	str = strstr(item->recv_buffer,"REDIRECT");      if(str) return str;
	str = strstr(item->recv_buffer,"RTSP/1.0");          if(str) return str;

	return NULL;
}

/***********************************************************************************************************
 *该函数为数据异常处理函数
 ***********************************************************************************************************/
static int isocket_handle_error_data(isocket_mgr_t *handle,isocket_t* item)
{
	int i = 0;

	FAILED_RETURNX(!handle || !item,IPANEL_ERR);

	INFO("%s[isocket_handle_error_data] start-----------!!!!!\n",MARK_SOCKET);
	
	//hex_printout(("[VOD][SOCKET]dirty data",item->recv_buffer,4));

	for( i = 0; i < item->recv_buffer_len - 4; i++ ) 
	{
        //查找RTSP数据
		if( strncasecmp(item->recv_buffer+i,"RTSP/1.0",8) == 0
			|| strncasecmp(item->recv_buffer+i,"ANNOUNCE",8) == 0
			|| strncasecmp(item->recv_buffer+i,"SET_PARAMETER",13) == 0
			|| strncasecmp(item->recv_buffer+i,"REDIRECT",8) == 0 )
		{
			item->recv_buffer_len -= i;
			item->flag_wait_rtsp = 0;
			item->flag_wait_data = 0;
			memmove(item->recv_buffer,item->recv_buffer+i,item->recv_buffer_len);
			return IPANEL_OK;
		}

        //查找RTP数据包,TCP_head中的第二个字节为0x01，其实是无效数据，是不会保存的。但这里认为是数据，保证数据处理通畅
        
		if( item->recv_buffer[i] == 0x24 
			&& (item->recv_buffer[i+1] == 0x00 || item->recv_buffer[i+1] == 0x01 || item->recv_buffer[i+1] == 0x02) ) 
		{
			unsigned short temp_len = 0;
			memcpy((unsigned char *)&temp_len,item->recv_buffer+i+2,2);
			temp_len = ntohs(temp_len) + 4;

			if( temp_len > 0) 
            {
				item->recv_buffer_len -= i;
				item->flag_wait_data = 0;
				item->flag_wait_rtsp = 0;
				memmove(item->recv_buffer,item->recv_buffer+i,item->recv_buffer_len);
				return IPANEL_OK;
			}
		}
	}
	
	item->flag_wait_rtsp = 0;
	item->flag_wait_data = 0;
	item->recv_buffer_len = 0;
	memset(item->recv_buffer,0,sizeof(item->recv_buffer));

	return IPANEL_OK;
}


