/******************************************************************************/
/*    Copyright (c) 2005 iPanel Technologies, Ltd.                            */
/*    All rights reserved. You are not allowed to copy or distribute          */
/*    the code without permission.                                            */
/*    This is the demo implenment of the basic type definitions needed by     */
/*    iPanel MiddleWare.                                                      */
/*                                                                            */
/*    此文件只能放置全局性的数据类型定义或者宏定义，不得依赖任何别的头文件!!! */	
/*										---huangsl  2008-02                   */
/******************************************************************************/


#ifndef _IPANEL_VANE_PORTING_TYPEDEF_FUNCTOTYPE_H_
#define _IPANEL_VANE_PORTING_TYPEDEF_FUNCTOTYPE_H_

#ifdef __cplusplus
extern "C" {
#endif

#include <android/log.h>
#include <time.h>

/******************************************************************************/
/* generic type redefinitions */
typedef int					INT32_T;
typedef unsigned int		UINT32_T;
typedef short				INT16_T;
typedef unsigned short		UINT16_T;
typedef char				CHAR_T;
typedef unsigned char		BYTE_T;
#define CONST				const
#define VOID				void




typedef struct
{
    UINT32_T uint64_32h;
    UINT32_T uint64_32l;
}IPANEL_UINT64_T;

typedef struct
{
    UINT32_T uint64_32h;
    UINT32_T uint64_32l;
}IPANEL_INT64_T;

/* general return values */
#define IPANEL_OK	0
#define IPANEL_ERR	(-1)

/* 根据讨论的结果, 应tujz的要求, 改一下这个宏的定义, 避免很多的编译问题. --McKing 2008-12-4
#define IPANEL_NULL (void *)0 */
#define IPANEL_NULL 0

/* general iPanel version values */
#define IPANEL_MAJOR_VERSION   3
#define IPANEL_MINOR_VERSION   0



/* AV sources */
typedef enum 
{
	IPANEL_AV_SOURCE_DEMUX,	//从Demux模块输入数据
	IPANEL_AV_SOURCE_MANUAL	//系统软件推入的数据
} IPANEL_AV_SOURCE_TYPE_e;

/* iPanel switch modes */
typedef enum{
	IPANEL_DISABLE,
	IPANEL_ENABLE
} IPANEL_SWITCH_e;

typedef enum
{
    IPANEL_DEV_USE_SHARED,               /* 和其他用户共享使用设备 */
    IPANEL_DEV_USE_EXCUSIVE              /* 独占使用设备 */
} IPANEL_DEV_USE_MODE;


/**************************Defines for  stream data exchange******************/
// 定义数据块释放函数指针
typedef VOID (*IPANEL_XMEM_FREE)(VOID *pblk);

typedef struct
{
	VOID *pdes;                          /* pbuf中的数据属性描述 */
	IPANEL_XMEM_FREE pfree;   			/* IPANEL_XMEMBLK数据块释放函数指针 */    
	UINT32_T *pbuf;                      /* 数据缓冲区地址 */
	UINT32_T len;                        /* 缓冲区长度 */
} IPANEL_XMEMBLK, *pIPANEL_XMEMBLK;

/* XMEM block descriptor types*/
typedef enum
{
	IPANEL_XMEM_PCM = 1,
	IPANEL_XMEM_MP3	= 2,
	IPANEL_XMEM_TS	= 3,
	IPANEL_XMEM_ES	= 4,
	IPANEL_XMEM_GEN	= 5
} IPANEL_XMEM_PAYLOAD_TYPE_e;

typedef struct 
{
	IPANEL_XMEM_PAYLOAD_TYPE_e destype;    
	UINT32_T len;      /* buffer中数据长度 */
} IPANEL_XMEM_GEN_DES;


/* MP3 descriptor definition*/
typedef struct
{
	IPANEL_XMEM_PAYLOAD_TYPE_e destype;  /* 1，表示PCM数据描叙符。见流式数据描叙符类型定义 */
	UINT32_T samplerate;                 /* 采样频率 HZ */
	UINT16_T channelnum;                 /* 通道个数，1单声道，2双声道 */
	UINT16_T bitspersample;              /* 采样精度，bpp */
	UINT16_T bsigned;                    /* 有符号还是无符号, 1有符号，0无符号 */
	UINT16_T bmsbf;                      /* 是否高位在前（most significant bit first）。1，高位在前，0低位在前 */
	UINT32_T samples;                    /* 采样个数 */
} IPANEL_PCMDES, *pIPANEL_PCMDES;

/* ES descriptor definition*/
typedef struct 
{
	IPANEL_XMEM_PAYLOAD_TYPE_e destype;  /* IPANEL_XMEM_ES，表示ES数据描叙符。见流式数据描叙符类型定义 */
	UINT32_T timestamp;    /* 时间戳 ，以90K为单位[因为dvb是以33bit记录90k时钟的，是否有必要将单位定义成180K] */
	BYTE_T *ppayload;  /*有效数据起始地址*/   
	UINT32_T len;      /*有效数据长度 */
} IPANEL_XMEM_ES_DES;

typedef struct 
{
    IPANEL_XMEM_PAYLOAD_TYPE_e destype;  /* IPANEL_XMEM_TS， 表示TS数据描叙符。见流式数据描叙符类型定义 */
    UINT32_T timestamp;    /* 长期置为-1，但先 保留以备后用 */
}IPANEL_XMEM_TS_DES;

/*IPANEL_XMEM_ES_DES以及IPANEL_XMEM_TS_DES在本地播放的
应用场景中timestamp不建议客户使用，如果需要时间信息，可从PTS中解出 */

typedef struct
{
    BYTE_T     *buf;
    UINT32_T    len;
} IPANEL_IOCTL_BUFFER;

/**************************Defines for  stream data exchange end *******************/
typedef enum
{
	IPANEL_PIXEL_4BPP,
	IPANEL_PIXEL_8BPP,
	IPANEL_PIXEL_ARGB1555,
	IPANEL_PIXEL_ARGB565,
	IPANEL_PIXEL_ARGB8888,
	IPANEL_PIXEL_ARGB4444
} IPANEL_PIXEL_FORMAT_e;

#ifndef INFO
#ifndef LOG_TAG
#define LOG_TAG "mplayer-jni"
#endif
#define MCONTROL_LOGD(fmt, ...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, fmt, ##__VA_ARGS__)
#define MCONTROL_LOGI(fmt, ...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, fmt, ##__VA_ARGS__)
#define MCONTROL_LOGE(fmt, ...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, fmt, ##__VA_ARGS__)
#define MCONTROL_LOGV(fmt, ...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, fmt, ##__VA_ARGS__)

//extern void inner_player_dprintf(const char *fmt, ...);
//#define MCONTROL_LOGD(FORMAT, ARGS... )		inner_player_dprintf("[mplayer-jni]  "FORMAT"\n",##ARGS)
//#define MCONTROL_LOGI(FORMAT, ARGS... ) 	inner_player_dprintf("[mplayer-jni]  "FORMAT"\n",##ARGS)
//#define MCONTROL_LOGE(FORMAT, ARGS... )		inner_player_dprintf("[mplayer-jni]  "FORMAT"\n",##ARGS)
//#define MCONTROL_LOGV(FORMAT, ARGS... )		inner_player_dprintf("[mplayer-jni]  "FORMAT"\n",##ARGS)

#define INFO    MCONTROL_LOGI
#endif

#ifndef FAILED_RETURNX
#define FAILED_RETURNX(a, b)  \
	do {  \
	if( (a) )  {  \
	return b;  \
	} \
	}while(0)
#endif

#ifndef FAILED_RETURN
#define FAILED_RETURN(a)  \
	do {  \
	if( (a) )  {  \
	return;  \
	} \
	}while(0)
#endif
#ifdef __cplusplus
}
#endif

#endif // _IPANEL_VANE_PORTING_TYPEDEF_FUNCTOTYPE_H_
