/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:线程及信号量管理
*********************************************************************/
#include <stdio.h>
#include <string.h>
#include <sys/ipc.h>
#include <pthread.h>
#include <semaphore.h>
#include <time.h>
#include <errno.h>
#include <assert.h>
#include <sched.h>
#include <unistd.h>
#include <sys/time.h>

#include "ipanel_porting.h"


#define MAX_TASK_NUM            (32)
#define MAX_SEM_NUM             (32*2)

typedef struct tagOsSem
{
	unsigned int    used;
	sem_t   sem;
}OsSem;

typedef struct tagOsThread
{
	pthread_t       thread;
}OsThread;


typedef struct tagOsMgr
{
	OsSem       semMgr[MAX_SEM_NUM];
	OsThread    taskMgr[MAX_TASK_NUM];
}OsMgr;

static OsMgr hOsMgr[1] = {0};


/********************************************************************************************************
功能：创建一个线程/任务。
原型：unsigned int ipanel_porting_task_create(CONST char *name, IPANEL_TASK_PROC func, void *param, int priority, unsigned int stack_size)
参数说明：
输入参数：
name：一个最多四字节长字符串，系统中线程名称应该唯一；
func：线程主体函数入口地址，函数原型定义如下；
typedef void (*IPANEL_TASK_PROC)(void *param);
param：线程主体函数的参数列表指针(可置为IPANEL_NULL)；
priority：优先级别(ipanel优先级从0到31，0最低,31最高)；
stack_size：栈大小，以字节为单位
输出参数：无
返    回：
!= IPANEL_NULL：成功，返回线程实例句柄。
== IPANEL_NULL：失败
********************************************************************************************************/
unsigned int ipanel_porting_task_create(CONST char *name, IPANEL_TASK_PROC func, void *param,
                                    int priority, unsigned int stack_size)
{       
	pthread_t thread;
			
	if (pthread_create(&thread, NULL, (void *)func, param) != 0)
	{
		return 0;
	}
    
	return thread;
}


/********************************************************************************************************
功能：销毁一个线程/任务。
原型：int ipanel_porting_task_destroy(unsigned int handle);
参数说明：
输入参数：handle：线程句柄(非0且存在，有效)。
输出参数：无
返    回：
IPANEL_OK：成功，
IPANEL_ERR：失败
********************************************************************************************************/
int ipanel_porting_task_destroy(unsigned int handle)
{
	pthread_t thread = (pthread_t)handle;
	
	if (pthread_join(thread, NULL) != 0)
	{
		return IPANEL_ERR;
	}
	
	return IPANEL_OK;   
}


/********************************************************************************************************
功能：将当前线程挂起指定时间，同时让出CPU供其他线程使用。
原型：void ipanel_porting_task_sleep(int ms)
参数说明：
输入参数：ms：挂起线程的时间长度，单位为毫秒。
输出参数：无
返    回：无
********************************************************************************************************/
void ipanel_porting_task_sleep(int ms)
{
	usleep(ms*1000);
}

/********************************************************************************************************
功能：创建一个信号量，iPanel MiddleWare只使用互斥信号量，不使用计数信号量。
原型：int ipanel_porting_sem_create(CONST char *name, int initial_count, unsigned int wait_mode)
参数说明：
输入参数：
name：一个最多四字符长字符串，系统中信号量的名字应该唯一；
initial_count：最大初始化计数(只有0和1有效)
wait_mode：这个参数决定当信号量有效时，等待此信号量的线程获得满足的次序，有两个选项：
- IPANEL_TASK_WAIT_FIFO - 按先入先出的方式在等待线程中分发消息
- IPANEL_TASK_WAIT_PRIO - 优先满足高优先级的线程。
输出参数：无
返    回：
!= IPANEL_NULL：成功，返回线程实例句柄。
== IPANEL_NULL：失败
********************************************************************************************************/
int ipanel_porting_sem_create(CONST char *name, int initial_count, unsigned int wait_mode)								   
{
	int index;
	sem_t sem;
	
	if (sem_init(&sem, 0, initial_count) != 0) 
    {
		return 0;
	}
	
	for (index=0; index<MAX_SEM_NUM;index++) 
    {
		if (hOsMgr->semMgr[index].used == 0) 
        {
			hOsMgr->semMgr[index].used = 1;
			hOsMgr->semMgr[index].sem = sem;
			return (int)&hOsMgr->semMgr[index];
		}
	}
	
	return 0;
}


/********************************************************************************************************
功能：销毁一个信号量。
原型：int ipanel_porting_sem_destroy(unsigned int handle)
参数说明：
输入参数：handle：信号量句柄，由ipanel_porting_sem_create获得。
输出参数：无
返    回：
IPANEL_OK：成功，
IPANEL_ERR：失败
********************************************************************************************************/
int ipanel_porting_sem_destroy(unsigned int handle)
{
	OsSem *pSem = (OsSem*)handle;
	
	if (pSem) 
    {
		if (sem_destroy(&pSem->sem) == 0) 
        {
			pSem->used = 0;
			memset(&pSem->sem, 0, sizeof(sem_t));
			return IPANEL_OK;
		}
	}
	
	return IPANEL_ERR;
}

/********************************************************************************************************
功能：信号量等待。
原型：int ipanel_porting_sem_wait(unsigned int handle, int wait_time)
参数说明：
输入参数：
handle：信号量句柄，由ipanel_porting_sem_create获得。
wait_time：等待时间，单位为毫秒。为IPANEL_NO_WAIT(0)时表示不等待立即返回，为IPANEL_WAIT_FOREVER(-1)表示永久等待
输出参数：无
返    回：
IPANEL_OK：成功，
IPANEL_ERR：失败
********************************************************************************************************/
int ipanel_porting_sem_wait(unsigned int handle, int wait_time)
{
	OsSem *pSem = (OsSem*)handle;
	
	if (pSem==NULL) 
    {
		return IPANEL_ERR;
	}
	
	if (0==wait_time) 
    {
        if (sem_trywait(&pSem->sem) == 0)
            return IPANEL_OK;
	}
	else if (-1==wait_time) 
    {
        if (sem_wait(&pSem->sem) == 0)
            return IPANEL_OK;
	}
	else if (wait_time>0) 
    {
		struct timeval temptime;
		gettimeofday(&temptime, NULL);
		temptime.tv_sec += (wait_time/1000);
		temptime.tv_usec += (wait_time%1000)*1000;
		if (sem_timedwait(&pSem->sem, &temptime) == 0)
			return IPANEL_OK;
	}
	
	if (errno != ETIMEDOUT)
	{
		if (errno == EDEADLK)
		{
            ;
		}
	}
	
	return IPANEL_ERR;
}


/********************************************************************************************************
功能：释放信号量。
原型：int ipanel_porting_sem_release(unsigned int handle)
参数说明：
输入参数：handle：信号量句柄，由ipanel_porting_sem_create获得。
输出参数：无
返    回：
IPANEL_OK：成功，
IPANEL_ERR：失败
********************************************************************************************************/
int ipanel_porting_sem_release(unsigned int handle)

{
	OsSem *pSem = (OsSem*)handle;
	
	if (pSem) 
    {
		if (sem_post(&pSem->sem)==0) 
        {
			return IPANEL_OK;
		}
	}
	return IPANEL_ERR;
}


