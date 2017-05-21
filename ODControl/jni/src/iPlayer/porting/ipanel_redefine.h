#ifndef __IPANEL_REDEFINE_H_
#define __IPANEL_REDEFINE_H_

#include <stdio.h>
//#define INFO(a) printf a
//#define	PTL_INFO(a) printf a 
//对一些数据进行重定义
typedef signed char     Int8;
typedef signed char			int8;
typedef signed short    Int16;
typedef signed short    int16;
typedef signed long     Int32;
typedef signed long			int32;
typedef unsigned char   UInt8;
typedef unsigned short  UInt16;
typedef unsigned long   UInt32;

typedef unsigned long   uint32;
typedef unsigned short  uint16;
typedef unsigned char   uint8;
typedef signed long     sint32;
typedef signed short    sint16;
typedef signed char     sint8;

#define time_ms ipanel_porting_time_ms
#define ipanel_porting_printf   iplayer_porting_dprintf
#define ipanel_porting_time_ms  iplayer_porting_time_ms
#define ipanel_porting_task_create       iplayer_porting_task_create
#define ipanel_porting_task_destroy      iplayer_porting_task_destroy
#define ipanel_porting_task_sleep        iplayer_porting_task_sleep
#define ipanel_porting_sem_create        iplayer_porting_sem_create
#define ipanel_porting_sem_destroy       iplayer_porting_sem_destroy
#define ipanel_porting_sem_wait          iplayer_porting_sem_wait
#define ipanel_porting_sem_release       iplayer_porting_sem_release
#define ipanel_porting_socket_open       iplayer_porting_socket_open
#define ipanel_porting_socket_bind       iplayer_porting_socket_bind
#define ipanel_porting_socket_listen     iplayer_porting_socket_listen
#define ipanel_porting_socket_accept     iplayer_porting_socket_accept
#define ipanel_porting_socket_connect    iplayer_porting_socket_connect
#define ipanel_porting_socket_send       iplayer_porting_socket_send
#define ipanel_porting_socket_sendto     iplayer_porting_socket_sendto
#define ipanel_porting_socket_recv       iplayer_porting_socket_recv
#define ipanel_porting_socket_recvfrom   iplayer_porting_socket_recvfrom
#define ipanel_porting_socket_getsockname iplayer_porting_socket_getsockname
#define ipanel_porting_socket_select     iplayer_porting_socket_select
#define ipanel_porting_socket_ioctl      iplayer_porting_socket_ioctl
#define ipanel_porting_socket_getsockopt iplayer_porting_socket_getsockopt
#define ipanel_porting_socket_setsockopt iplayer_porting_socket_setsockopt
#define ipanel_porting_socket_shutdown   iplayer_porting_socket_shutdown
#define ipanel_porting_socket_close      iplayer_porting_socket_close
#define ipanel_porting_socket_get_max_num iplayer_porting_socket_get_max_num

#endif
