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
    int          time_type;                  //当前时间类型,取值npt或utc
	unsigned int start_time;                 //开始播放时间
	unsigned int end_time;                   //结束播放时间
	unsigned int present_time;               //当前播放时间

	char     seektime[128];                  //seek时间点
	int      duration;                       //播放时间长

	unsigned int  base_pts;                  //基准pts
	unsigned int  current_pts;               //当前pts
	unsigned int  last_update_start_time;    //最后更新开始播放时间的时间
	unsigned int  last_update_time;          //最后更新当前播放时间
	unsigned int  last_pause_time;           //暂停时间
    
	unsigned int  flag_support_pts:1;	     //是否支持PTS计算时间
}client_time_t;


/*将字符串时间转换为秒*/
int 
client_time_string_to_seconds(char *string, unsigned int *time,int *time_type);


/*将UTC时间转换为秒*/
int 
client_time_UTC_to_seconds(UTC_time_t *utc_time, unsigned int *time);


/*将秒转换为UTC时间*/
int 
client_time_seconds_to_UTC(unsigned int time, UTC_time_t *utc_time);


/*将UTC时间输出为字符串*/
int 
client_time_UTC_time_print(UTC_time_t *utc_time, char *str_time);


/*更新当前时间*/
int 
client_time_update_current_time(client_time_t *client_time, unsigned int current_time);



/*计算当前时间*/
int 
client_time_calculate_current_time(client_time_t *client_time, int speed, unsigned int *current_time);


/*将字符串表示的时间提取出来*/
int 
client_time_parse_string_time(char *string,char *from,char *to);



#endif //__RTSP_TIME_H__

