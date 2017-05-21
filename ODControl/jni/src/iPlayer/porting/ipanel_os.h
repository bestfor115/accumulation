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
    IPANEL_TASK_WAIT_FIFO, //�Ƽ�ʹ�õ�ģʽ
    IPANEL_TASK_WAIT_PRIO
};


typedef  void (*IPANEL_TASK_PROC)(void *param);

/*******************************************************************************
����:
name��          ���������
Func:       ������ڵ�ַ:typedef void(*IPANEL_TASK_PROC)(void *);
Param:      �����б�(һ����ΪNULL)
stack_size: ջ��С(>0��Ч)
priority:   ���ȼ���...(ipanel���ȼ���0��31,31���,0���)
����ֵ:
handle( 0 ʧ��)
*******************************************************************************/
unsigned int ipanel_porting_task_create(CONST char *name, IPANEL_TASK_PROC func, void *param,
                                    int priority, unsigned int stack_size);

/*******************************************************************************
����:
handle: task handle(��0,�Ҵ���,��Ч)
����ֵ:
0:success
-1:failed
*******************************************************************************/
int ipanel_porting_task_destroy(unsigned int handle);

/*******************************************************************************
����:
       ������ʱ�䣬��λ����
����ֵ:����
*******************************************************************************/
void ipanel_porting_task_sleep(int ms);

/*******************************************************************************
����: ��ȡ��ǰtask identify��Id��ipanel_porting_task_create���ص�task Id��
�������: ��
�������: ��
����ֵ: 0: failed,  >0: Task Id
*******************************************************************************/
unsigned int ipanel_porting_task_identify(void);

/*******************************************************************************
����:
name: semaphore name(����ֻ��pSOS��sm_create()��Ҫ��name, ԭ����: char name[4])
initial_count:����ʼ������(���ڵ���0��Ч)this is the initial token count
        for the semaphore being created. This value will determine the maximum
        number of simultaneous accesses allowed to whatever resource is
        being protected by the semaphore.
wait_mode: this parameter determines how tasks will wait on a
        token from an 'empty' semaphore. There are two options for this
        parameter;
        IPANEL_TASK_WAIT_FIFO - the first task to start pending on the token, will
                    receive the token when is made available.�Ƚ���ȴ����е�������
                    ����ź���
        IPANEL_TASK_WAIT_PRIO - the highest priority task pending on the token,
                    will receive the token when it is made available.���ȼ��ߵ�����
                    �Ȼ���ź���
        �������е�RTOS��֧�������ֵȴ�ģʽ��win32��OS20��Ecos��UCOS��Linux��
        �������ã�PSOS��VxWorks�������á�
����ֵ:
Handle(0 ʧ��)
*******************************************************************************/
int ipanel_porting_sem_create(CONST char *name, int initial_count, unsigned int wait_mode);

/*******************************************************************************
����:
handle  semaphore handle(��0,�Ҵ���,��Ч)
����ֵ:
0:success
-1:failed
*******************************************************************************/
int ipanel_porting_sem_destroy(unsigned int handle);

/*******************************************************************************
����:
handle  semaphore handle(��0,�Ҵ���,��Ч)
wait_time  �ȴ�semaphore��ʱ��,  0 ��ʾ��������, -1 ��ʾһֱ�ȴ���
����ֵ:
EIS_OS_QUEUE_SEM_STATUS �źŵĵ�ǰ״̬
��EIS_OS_QUEUE_SEM_STATUS_AVAILABLEʱ,��ʾ�ȴ����ź�.
EIS_OS_QUEUE_SEM_STATUS_UNAVAILABLE:failed
*******************************************************************************/
int ipanel_porting_sem_wait(unsigned int handle, int wait_time);

/*******************************************************************************
����:
handle  semaphore handle(��0,�Ҵ���,��Ч)
��handle�ͷ�һ���ź�
����ֵ:
0:success
-1:failed
*******************************************************************************/
int ipanel_porting_sem_release(unsigned int handle);



#ifdef __cplusplus
}
#endif

#endif // __IPANEL_PORTING_TASK_H______
