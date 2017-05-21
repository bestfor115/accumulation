/*******************************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
*******************************************************************************/

#ifndef __IPANEL_PORTING_TASK_H______
#define __IPANEL_PORTING_TASK_H______

#include "ipanel_porting.h"

#ifdef __cplusplus
extern "C" {
#endif


#define IPANEL_NO_WAIT              (0)
#define IPANEL_WAIT_FOREVER         (-1)

enum
{
    IPANEL_TASK_WAIT_FIFO, //推荐使用的模式
    IPANEL_TASK_WAIT_PRIO
};


typedef  void (*IPANEL_TASK_PROC)(void *param);

/*******************************************************************************
参数:
name：          任务的名字
Func:       函数入口地址:typedef void(*IPANEL_TASK_PROC)(void *);
Param:      参数列表(一般置为NULL)
stack_size: 栈大小(>0有效)
priority:   优先级别...(ipanel优先级从0到31,31最高,0最低)
返回值:
handle( 0 失败)
*******************************************************************************/
unsigned int ipanel_porting_task_create(CONST char *name, IPANEL_TASK_PROC func, void *param,
                                    int priority, unsigned int stack_size);

/*******************************************************************************
参数:
handle: task handle(非0,且存在,有效)
返回值:
0:success
-1:failed
*******************************************************************************/
int ipanel_porting_task_destroy(unsigned int handle);

/*******************************************************************************
参数:
       　休眠时间，单位毫秒
返回值:　无
*******************************************************************************/
void ipanel_porting_task_sleep(int ms);

/*******************************************************************************
功能: 获取当前task identify（Id，ipanel_porting_task_create返回的task Id）
输入参数: 无
输出参数: 无
返回值: 0: failed,  >0: Task Id
*******************************************************************************/
unsigned int ipanel_porting_task_identify(void);

/*******************************************************************************
参数:
name: semaphore name(好像只有pSOS的sm_create()需要的name, 原型是: char name[4])
initial_count:最大初始化计数(大于等于0有效)this is the initial token count
        for the semaphore being created. This value will determine the maximum
        number of simultaneous accesses allowed to whatever resource is
        being protected by the semaphore.
wait_mode: this parameter determines how tasks will wait on a
        token from an 'empty' semaphore. There are two options for this
        parameter;
        IPANEL_TASK_WAIT_FIFO - the first task to start pending on the token, will
                    receive the token when is made available.先进入等待队列的任务先
                    获得信号量
        IPANEL_TASK_WAIT_PRIO - the highest priority task pending on the token,
                    will receive the token when it is made available.优先级高的任务
                    先获得信号量
        并非所有的RTOS都支持这两种等待模式。win32、OS20、Ecos、UCOS、Linux上
        不能设置，PSOS、VxWorks可以设置。
返回值:
Handle(0 失败)
*******************************************************************************/
int ipanel_porting_sem_create(CONST char *name, int initial_count, unsigned int wait_mode);

/*******************************************************************************
参数:
handle  semaphore handle(非0,且存在,有效)
返回值:
0:success
-1:failed
*******************************************************************************/
int ipanel_porting_sem_destroy(unsigned int handle);

/*******************************************************************************
参数:
handle  semaphore handle(非0,且存在,有效)
wait_time  等待semaphore的时间,  0 表示立即返回, -1 表示一直等待。
返回值:
EIS_OS_QUEUE_SEM_STATUS 信号的当前状态
当EIS_OS_QUEUE_SEM_STATUS_AVAILABLE时,表示等待到信号.
EIS_OS_QUEUE_SEM_STATUS_UNAVAILABLE:failed
*******************************************************************************/
int ipanel_porting_sem_wait(unsigned int handle, int wait_time);

/*******************************************************************************
参数:
handle  semaphore handle(非0,且存在,有效)
将handle释放一个信号
返回值:
0:success
-1:failed
*******************************************************************************/
int ipanel_porting_sem_release(unsigned int handle);



#ifdef __cplusplus
}
#endif

#endif // __IPANEL_PORTING_TASK_H______
