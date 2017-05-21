/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
����:VOD�ڲ�����ͷ�ļ�����
*********************************************************************/
#ifndef __IVOD_COMMON_H_
#define __IVOD_COMMON_H_

#ifdef __cplusplus
extern "C"{
#endif

#include "ipanel_porting.h"


/*VOD�����ص���������*/
typedef int (*ivod_msg_func)(void *handle, unsigned int event, int p1, int p2);

/*VOD���ƶ���*/
#define IVOD_NAME_NONE       0x00000000
#define IVOD_NAME_TELECOM    0x00000002
#define IVOD_NAME_NGOD       0x00000004
#define IVOD_NAME_IPANEL     0x00000008
#define IVOD_NAME_UNICOM     0x00000010
#define IVOD_NAME_DMX        0x00000020
#define IVOD_NAME_ISMA       0x00000040


/*BIND TYPE*/
#define BIND_TYPE(n)        (1<<n)

typedef enum
{
    IVOD_RTSP_SERVER_NONE   = 0,
    IVOD_RTSP_SERVER_IPANEL = 321,
    IVOD_RTSP_SERVER_HUAWEI = 322,
    IVOD_RTSP_SERVER_ZTE    = 323,
    IVOD_RTSP_SERVER_ISMA   = 324
}ivod_rtsp_server_e;


/*�ӵ㲥��ַ�н�����������ip��ַ���˿�*/
int 
ivod_common_parse_url(char *url, unsigned int ip, unsigned int *port);


/*��ipv6�ַ�����ַת��ΪIPAddr0*/
int 
ivod_common_ipv6_str_to_int(char *ipv6,IPAddr0 *ipaddr);


/*��ipv4�ַ�����ַת��ΪIPAddr0*/
int  
ivod_common_ipv4_str_to_int(char *ipv4,IPAddr0 *ipaddr);


/*��ȡһ����̬�˿�*/
int 
ivod_common_get_dynamic_port(void);


/*��ȡ����ip��ַ*/
int
ivod_common_get_localIP(char *name,char *ipv4,char *ipv6);


/*��������*/
int
ivod_common_parse_domin(char *name,IPAddr0 *ipaddr);



#ifdef __cplusplus
}
#endif

#endif
