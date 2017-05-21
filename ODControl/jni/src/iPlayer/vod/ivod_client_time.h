/*********************************************************************
	Copyright (c) 2012 by iPanel Technologies, Ltd.
	All rights reserved. You are not allowed to copy or distribute
	the code without permission.
*********************************************************************/


#ifndef __IVOD_CLIENT_TIME_H__
#define __IVOD_CLIENT_TIME_H__

#include "ipanel_porting.h"

#define EIS_TIME_T	time_t 
#define EIS_TIME	struct tm


typedef enum
{ 
	CLIENT_TIME_TYPE_NONE = 0x0,
	CLIENT_TIME_TYPE_NPT,
	CLIENT_TIME_TYPE_UTC
}time_type_e;

typedef struct 
{
	int tm_sec_1;
    int tm_sec;     // seconds after the minute - [0,59]
    int tm_min;     // minutes after the hour - [0,59]
    int tm_hour;    // hours since midnight - [0,23]
    int tm_mday;    // day of the month - [1,31]
    int tm_mon;     // months since January - [0,11]
    int tm_year;    // years since 1900
} UTC_time_t;


typedef struct
{
    int          time_type;                  //��ǰʱ������,ȡֵnpt��utc
	unsigned int start_time;                 //��ʼ����ʱ��
	unsigned int end_time;                   //��������ʱ��
	unsigned int present_time;               //��ǰ����ʱ��

	char     seektime[128];                  //seekʱ���
	int      duration;                       //����ʱ�䳤

	unsigned int  base_pts;                  //��׼pts
	unsigned int  current_pts;               //��ǰpts
	unsigned int  last_update_start_time;    //�����¿�ʼ����ʱ���ʱ��
	unsigned int  last_update_time;          //�����µ�ǰ����ʱ��
	unsigned int  last_pause_time;           //��ͣʱ��
    
	unsigned int  flag_support_pts:1;	     //�Ƿ�֧��PTS����ʱ��
}client_time_t;


/*���ַ���ʱ��ת��Ϊ��*/
int 
client_time_string_to_seconds(char *string, unsigned int *time,int *time_type);


/*��UTCʱ��ת��Ϊ��*/
int 
client_time_UTC_to_seconds(UTC_time_t *utc_time, unsigned int *time);


/*����ת��ΪUTCʱ��*/
int 
client_time_seconds_to_UTC(unsigned int time, UTC_time_t *utc_time);


/*��UTCʱ�����Ϊ�ַ���*/
int 
client_time_UTC_time_print(UTC_time_t *utc_time, char *str_time);


/*���µ�ǰʱ��*/
int 
client_time_update_current_time(client_time_t *client_time, unsigned int current_time);



/*���㵱ǰʱ��*/
int 
client_time_calculate_current_time(client_time_t *client_time, int speed, unsigned int *current_time);


/*���ַ�����ʾ��ʱ����ȡ����*/
int 
client_time_parse_string_time(char *string,char *from,char *to);



#endif //__RTSP_TIME_H__

