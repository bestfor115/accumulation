/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:time管理
*********************************************************************/
#include "ivod_client_time.h"

/************************************************************************************************
 *宏定义
 ************************************************************************************************/

#define CLIENT_TIME_MARK "[VOD][CLIENT][TIME]"

/************************************************************************************************
 *结构体定义
 ************************************************************************************************/



/************************************************************************************************
 *函数声明
 ************************************************************************************************/


/*去掉字符串前后的空格*/
static int 
client_time_string_trim(char *str);


/************************************************************************************************
 *函数定义
 ************************************************************************************************/

/*将字符串时间转换为秒*/
int client_time_string_to_seconds(char *string, unsigned int *time,int *time_type)
{
	int ret = IPANEL_OK;
	int hour = 0,minute = 0,second = 0;
	char  temp[16] = { 0 };
    UTC_time_t utc_time[1] = {0};

	FAILED_RETURNX( NULL==string || NULL==time || NULL==time_type,IPANEL_ERR );

	INFO("%s[client_time_string_to_seconds] string = %s\n", CLIENT_TIME_MARK,string);

		//清除空格
	while ( *string == ' ' || *string == '\t' )
		string++;

	if(strlen(string) == 8 && strchr(string,':')) //00:01:11
    { 
		*time_type = CLIENT_TIME_TYPE_NPT;
		memset(temp,0,sizeof(temp));
		strncpy(temp,string,2);
		hour = atoi(temp);
		memset(temp,0,sizeof(temp));
		strncpy(temp,string+3,2);
		minute = atoi(temp);
		memset(temp,0,sizeof(temp));
		strncpy(temp,string+6,2);
		second = atoi(temp);
		*time = hour * 3600 + minute * 60 + second;
	} 
    else if(strlen(string) == 15 || strchr(string,'T') || strchr(string,'Z'))//20090325T101010
    { 
		*time_type = CLIENT_TIME_TYPE_UTC;
		memset(temp,0,sizeof(temp));
		strncpy(temp,string,4);
		utc_time->tm_year = atoi(temp);
		memset(temp,0,sizeof(temp));
		strncpy(temp,string+4,2);
		utc_time->tm_mon = atoi(temp);
		memset(temp,0,sizeof(temp));
		strncpy(temp,string+6,2);
		utc_time->tm_mday = atoi(temp);
		memset(temp,0,sizeof(temp));
		strncpy(temp,string+9,2);
		utc_time->tm_hour = atoi(temp);
		memset(temp,0,sizeof(temp));
		strncpy(temp,string+11,2);
		utc_time->tm_min = atoi(temp);
		memset(temp,0,sizeof(temp));
		strncpy(temp,string+13,2);
		utc_time->tm_sec = atoi(temp);
		if( strchr(string,'.') ) 
        {
			strncpy(temp,string+16,2);
			utc_time->tm_sec_1 = atoi(temp);
		}
	}
    else if(strlen(string) == 19 && strchr(string,':'))// 2009:03:25 10:10:10 或者 2009/03/25 10:10:10
    {
		*time_type = CLIENT_TIME_TYPE_UTC;
		memset(temp,0,sizeof(temp));
		strncpy(temp,string,4);
		utc_time->tm_year = atoi(temp);
		memset(temp,0,sizeof(temp));
		strncpy(temp,string+5,2);
		utc_time->tm_mon = atoi(temp);
		memset(temp,0,sizeof(temp));
		strncpy(temp,string+8,2);
		utc_time->tm_mday = atoi(temp);
		memset(temp,0,sizeof(temp));
		strncpy(temp,string+11,2);
		utc_time->tm_hour = atoi(temp);
		memset(temp,0,sizeof(temp));
		strncpy(temp,string+14,2);
		utc_time->tm_min = atoi(temp);
		memset(temp,0,sizeof(temp));
		strncpy(temp,string+17,2);
		utc_time->tm_sec = atoi(temp);
	}
    else if( strlen(string) == 14 ) // 20090325101010 
    { 
		*time_type = CLIENT_TIME_TYPE_UTC;
		memset(temp,0,sizeof(temp));
		strncpy(temp,string,4);
		utc_time->tm_year = atoi(temp);
		memset(temp,0,sizeof(temp));
		strncpy(temp,string+4,2);
		utc_time->tm_mon = atoi(temp);
		memset(temp,0,sizeof(temp));
		strncpy(temp,string+6,2);
		utc_time->tm_mday = atoi(temp);
		memset(temp,0,sizeof(temp));
		strncpy(temp,string+8,2);
		utc_time->tm_hour = atoi(temp);
		memset(temp,0,sizeof(temp));
		strncpy(temp,string+10,2);
		utc_time->tm_min = atoi(temp);
		memset(temp,0,sizeof(temp));
		strncpy(temp,string+12,2);
		utc_time->tm_sec = atoi(temp);
	} 
    else
    {
		*time_type = CLIENT_TIME_TYPE_NPT;
		*time = strtol(string,NULL,10);
	}

    //如果是UTC时间转换为秒
    if(*time_type == CLIENT_TIME_TYPE_UTC)
    {
        ret = client_time_UTC_to_seconds(utc_time,time);
    }
	return ret;   
}


/*将UTC时间转换为秒*/
int client_time_UTC_to_seconds(UTC_time_t *utc_time, unsigned int *time)
{
	EIS_TIME tm = {0};
	
	FAILED_RETURNX(NULL == utc_time || NULL == time,IPANEL_ERR);

	tm.tm_year = utc_time->tm_year - 1900;
	tm.tm_mon  = utc_time->tm_mon - 1;
	tm.tm_mday = utc_time->tm_mday;
	tm.tm_hour = utc_time->tm_hour;
	tm.tm_min  = utc_time->tm_min;
	tm.tm_sec  = utc_time->tm_sec;

    *time = (unsigned int)mktime(&tm);

	return  IPANEL_OK;    
}


/*将秒转换为UTC时间*/
int client_time_seconds_to_UTC(unsigned int time, UTC_time_t *utc_time)
{
	EIS_TIME *tm = NULL;
	EIS_TIME_T t = (EIS_TIME_T)time;

	FAILED_RETURNX(!time || NULL == utc_time,IPANEL_ERR);

	tm = localtime((const EIS_TIME_T *)&t);
	
	utc_time->tm_year  = tm->tm_year + 1900;
	utc_time->tm_mon = tm->tm_mon + 1;
	utc_time->tm_mday = tm->tm_mday;
	utc_time->tm_hour = tm->tm_hour;
	utc_time->tm_min = tm->tm_min;
	utc_time->tm_sec = tm->tm_sec;

	return IPANEL_OK;    
}


/*将UTC时间输出为字符串*/
int client_time_UTC_time_print(UTC_time_t *utc_time, char *str_time)
{
	FAILED_RETURNX(NULL == utc_time || NULL == str_time,IPANEL_ERR);
	sprintf(str_time,"%4d%02d%02dT%02d%02d%02dZ",				
	utc_time->tm_year,			
	utc_time->tm_mon,				
	utc_time->tm_mday,		
	utc_time->tm_hour,			
	utc_time->tm_min,			
	utc_time->tm_sec);				

	INFO("%s[client_time_UTC_time_print] string = %s\n", CLIENT_TIME_MARK,str_time);
    return IPANEL_OK;			
}


/*更新当前时间*/
int client_time_update_current_time(client_time_t *client_time, unsigned int current_time)
{
	FAILED_RETURNX(NULL == client_time , IPANEL_ERR);

    client_time->present_time = current_time;
    client_time->base_pts = 0;
    client_time->current_pts = 0;
    client_time->last_update_time = (unsigned int)ipanel_porting_time_ms();
    client_time->last_pause_time = 0;

    return IPANEL_OK;
}


/*计算当前时间*/
int client_time_calculate_current_time(client_time_t *client_time, int speed, unsigned int *current_time)
{
    int temp = 0,now = 0;
	FAILED_RETURNX(NULL == client_time || NULL == current_time, IPANEL_ERR);

    *current_time = client_time->present_time;

	INFO("%s[client_time_calculate_current_time] %d.%u.%u.%u.%u.%u.%d\n", 
        CLIENT_TIME_MARK,
        speed,
        client_time->current_pts,
        client_time->base_pts,
        client_time->present_time,
        client_time->last_update_time,
        client_time->last_pause_time,
        client_time->flag_support_pts);

    if(client_time->flag_support_pts)
    {
        //如果pts都没有获取到，直接返回
        if(client_time->base_pts == 0 || client_time->current_pts == 0)
        {
            return IPANEL_OK;
        }
        else
        {
            if(speed < 0)
            {
                if(client_time->base_pts < client_time->current_pts)
                    temp = (int)(client_time->base_pts - client_time->current_pts);
                else
                    temp = (int)(client_time->current_pts - client_time->base_pts);
            }
            else
            {
                temp = (int)(client_time->current_pts - client_time->base_pts);
            }

            temp = temp/1000;
            now = (int)client_time->present_time + temp;
        }
    }
    else
    {
        //如果是暂停状态，取暂停时间计算时间
        if(client_time->last_pause_time > 0 && client_time->last_update_time > 0)
        {
            temp = (int)(client_time->last_pause_time - client_time->last_update_time);
            temp = (temp/1000)*speed;
        }
        else if(client_time->last_update_time > 0)
        {
            temp = (int)(ipanel_porting_time_ms() - client_time->last_update_time);
            temp = (temp/1000)*speed;
        }
        else
        {
            return IPANEL_OK;
        }

        now = (int)client_time->present_time + temp;
    }

    if(CLIENT_TIME_TYPE_NPT == client_time->time_type)
    {
        if(client_time->duration > 0 && now > client_time->duration)
        {
            now = client_time->duration;
        }
        
    }
    else if(CLIENT_TIME_TYPE_UTC == client_time->time_type)
    {
        if(client_time->duration > 0 && temp > client_time->duration)
        {
            temp = client_time->duration;
            now = (int)client_time->present_time + temp;
        }

        if(now > client_time->end_time)
            now = client_time->end_time;
        else if(now < client_time->start_time)
            now = client_time->start_time;
    }

    if(now < 0)
        now = 0;

    *current_time = (unsigned int)now;
	INFO("%s[client_time_calculate_current_time] current_time=%d,duration=%d(time = %u)\n", CLIENT_TIME_MARK,now,client_time->duration,time_ms());
    
    return IPANEL_OK;
}


/*将字符串表示的时间提取出来*/
int client_time_parse_string_time(char *string,char *from,char *to)
{
	char *distance = NULL;
	
	FAILED_RETURNX(!string || !from || !to, IPANEL_ERR);
    
	INFO("%s[client_time_parse_string_time] string = %s\n", CLIENT_TIME_MARK,string);
    
	distance = strchr(string,'-');
	if(distance)
    {
		memcpy(from,string,distance-string);
		memcpy(to,distance+1,strlen(string)-(distance-string)-1);
	} 
    else
    {
		memcpy(from,string,strlen(string));
	}
    
	INFO("%s[client_time_parse_string_time] seek from = %s, to = %s\n", CLIENT_TIME_MARK, from, to);
    
	client_time_string_trim(from);
	client_time_string_trim(to);
	
	return IPANEL_OK;    
}



/*去掉字符串前后的空格*/
static int client_time_string_trim(char *str)
{
	char temp[128] = {0}, *str_temp = NULL;
	unsigned int i = 0;
	
	FAILED_RETURNX(!str, IPANEL_ERR);
    
	strncpy(temp, str, 128);
	memset(str, 0, strlen(str));
	str_temp = str;
	for(i = 0; i < strlen(temp); i++) 
    {
		if(temp[i] == ' ' || temp[i] == '-')
			continue;
		else 
        {
			*str_temp = temp[i];
			str_temp ++;
		}
	}	
	return IPANEL_OK;
}





