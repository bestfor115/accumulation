/*********************************************************************
    Copyright (c) 2007 iPanel Technologies, Ltd.
    All rights reserved. You are not allowed to copy or distribute
    the code without permission.
    
    Note: There are default task priority definitions of app. layer in this file.
		  任何应用层的task的优先级, 只能在这个文件里统一定义.

    $ver0.0.0.1 $author McKing 2008/05/28	composed
*********************************************************************/

#ifndef __IPANEL_PRIORITY_H__
#define __IPANEL_PRIORITY_H__

/* 调整前旧的数据, 仅供参考:
**	平台      操作系统     优先级范围                iPanel任务级别
**	cnova     vxworks     0－255，0  优先级最高       100
**	conexant  nucleus     0－255，255优先级最高       64
**	philips   ecos        0－31， 0  优先级最高       15
**	philips   psos        0－239，239优先级最高       120
**	st5107    os20        1－10， 10 优先级最高       3  
**  统计: iPanel的任务优先级系数coef(假设最高为1)是0.25~0.61
*/

/*
** iPanel主任务优先级由porting层创建时指定. 暂时默认为8(valid range: 0~31).
** on WIN32, for THREAD_PRIORITY_NORMAL, priority shall be 14~17.
*/
#if defined(WIN32)
#define PRIO_CRITICAL		30 // 28~31
#define PRIO_HIGHEST		25 // 23~27
#define PRIO_ABOVE_NORMAL	20 // 18~22
#define PRIO_NORMAL			16 // 14~17
#define PRIO_BELOW_NORMAL	11 // 9~13
#define PRIO_LOWEST			6  // 4~8
#define PRIO_IDLE			2  // 0~3
#endif
	
/* if use debug task, we shall define its default priority. *******************************/
#ifndef IPANEL_DEBUG_TASK_PRIO
#ifdef WIN32
#	define IPANEL_DEBUG_TASK_PRIO	PRIO_BELOW_NORMAL
#else
#	define IPANEL_DEBUG_TASK_PRIO	6
#endif
#endif

/* if use socket task, we shall define its default priority. *****************************/
#ifndef IPANEL_SOCKET_TASK_PRIO
#ifdef WIN32
#	define IPANEL_SOCKET_TASK_PRIO	(PRIO_NORMAL+1)
#else
#	define IPANEL_SOCKET_TASK_PRIO	10
#endif
#endif

/* if use uniform NVRAM task, we shall define its default priority. **********************/
#ifndef IPANEL_NVRAM_TASK_PRIO
#ifdef WIN32
#	define IPANEL_NVRAM_TASK_PRIO	PRIO_NORMAL
#else
#	define IPANEL_NVRAM_TASK_PRIO	11
#endif
#endif

/* CAM task个数未知,故仅定义一个CAM的基准PRIO*/
#ifndef IPANEL_CAM_TASK_PRIO_BASE
#ifdef WIN32
#	define IPANEL_CAM_TASK_PRIO_BASE PRIO_NORMAL
#else
#	define IPANEL_CAM_TASK_PRIO_BASE 10
#endif
#endif

/* RTSOCK task, 使用一个稍低的优先级. */
#ifndef IPANEL_RTSOCK_TASK_PRIORITY
#ifdef WIN32
#	define IPANEL_RTSOCK_TASK_PRIORITY	(PRIO_NORMAL-2)
#else
#	define IPANEL_RTSOCK_TASK_PRIORITY	6
#endif
#endif

/* ICTV task, 应该使用一个稍低的优先级. (原代码使用了0这个优先级?! please check!) */
#ifndef IPANEL_ICTV_TASK_PRIORITY
#ifdef WIN32
#	define IPANEL_ICTV_TASK_PRIORITY	(PRIO_NORMAL-2)
#else
#	define IPANEL_ICTV_TASK_PRIORITY	6
#endif
#endif

/* MediaPlayer任务默认优先级, 应该使用一个稍低的优先级. 可是原code使用了21这个较高的优先级--待定! --McKing  */
#ifndef IPANEL_MP_TASK_PRIORITY
#ifdef WIN32
#	define IPANEL_MP_TASK_PRIORITY		(PRIO_ABOVE_NORMAL+1)
#else
#	define IPANEL_MP_TASK_PRIORITY		21
#endif
#endif

/* 科大讯飞语音task, 使用跟socket task一样的优先级. (not sure --McKing) */
#ifndef IPANEL_KEDA_TASK_PRIORITY
#ifdef WIN32
#	define IPANEL_KEDA_TASK_PRIORITY	(PRIO_NORMAL+1)
#else
#	define IPANEL_KEDA_TASK_PRIORITY	10
#endif
#endif

/* UDC task, 应该使用一个稍低的优先级. 可是原code使用了16这个较高的优先级--待定! --McKing */
#ifndef IPANEL_UDC_TASK_PRIORITY
#ifdef WIN32
#	define IPANEL_UDC_TASK_PRIORITY		(PRIO_NORMAL- 3)
#else
#	define IPANEL_UDC_TASK_PRIORITY		6
#endif
#endif

/* if use http transport task, we shall define its default priority. *****************************/
#ifndef IPANEL_HTTP_TASK_PRIO
#ifdef WIN32
#	define IPANEL_HTTP_TASK_PRIO	(PRIO_NORMAL)
#else
#	define IPANEL_HTTP_TASK_PRIO	9
#endif
#endif

/* MP3 task. 应当是音频高于图形 */
#ifndef IPANEL_MP3_TASK_PRIORITY
#ifdef WIN32
#	define IPANEL_MP3_TASK_PRIORITY	(PRIO_ABOVE_NORMAL)
#else
#	define IPANEL_MP3_TASK_PRIORITY	17
#endif
#endif

//海荣德股票里的time定时器用到的，优先级低
#ifndef IPANEL_HTRD_TASK_PRIORITY
#ifdef WIN32
#	define IPANEL_HTRD_TASK_PRIORITY	(PRIO_NORMAL-2)
#else
#	define IPANEL_HTRD_TASK_PRIORITY	7
#endif
#endif

/* graphics task. 不能高于主任务, 不然主任务控制不了 */
#ifndef IPANEL_GRAPHICS_TASK_PRIORITY
#ifdef WIN32
#	define IPANEL_GRAPHICS_TASK_PRIORITY	(PRIO_NORMAL+1)
#else
#	define IPANEL_GRAPHICS_TASK_PRIORITY	16
#endif
#endif

#ifndef IPANEL_OPENGL_TASK_PRIORITY
#ifdef WIN32
#	define IPANEL_OPENGL_TASK_PRIORITY	(PRIO_NORMAL+1)
#else
#	define IPANEL_OPENGL_TASK_PRIORITY	16
#endif
#endif

#endif // __IPANEL_PRIORITY_H__
