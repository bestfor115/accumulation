/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:VOD内部公共函数
*********************************************************************/
#include <stdlib.h>
#include <stdio.h>      
#include <sys/types.h>
#include <string.h> 

#include <arpa/inet.h>
#include <netinet/in.h>
#include <netinet/ip.h>
#include <netinet/udp.h>
#include <netinet/tcp.h>
#include <net/if.h>
#include <netdb.h>
#include <sys/socket.h>
#include <unistd.h>
#include <net/if_arp.h>

#include "iplayer_common.h"


/*****************************************************************************
*宏定义
*****************************************************************************/
#define COMMON_MARK "[IVOD][BASE][COMMON]"


/*****************************************************************************
*函数声明
*****************************************************************************/



/*****************************************************************************
*函数定义
*****************************************************************************/

/*从点播地址中解析出服务器ip地址及端口*/
int ivod_common_parse_url(char *url, unsigned int ip, unsigned int *port_out)
{
	char *temp_buf = NULL,*end_buf = NULL,*mid_buf = NULL;
	char  host[64] = {0};
	int loop = 0, port = 0, flag_ipv6 = 0, ret = IPANEL_ERR;
	
	FAILED_RETURNX( !url || !ip || !port_out ,IPANEL_ERR );
	
	*port_out = 554;
	
	temp_buf = strstr(url, "://");
	temp_buf += 3;
	
	/* 清除空格,IPv6地址以'['和']'开始和结束，也要去掉 */
	while ( *temp_buf == ' ' || *temp_buf == '\t' || *temp_buf == '[')
		temp_buf++;

    /*IPv6地址的结束标志*/
    end_buf = strchr(temp_buf,']');

    if(end_buf == NULL)
    {
        end_buf = strchr(temp_buf,'/');
    }

    if(end_buf)
    {
        /*查找ipv6地址标志:,但:也有可能是IPV4端口号的分界号，处理时需要进一步判断*/
        mid_buf = strchr(temp_buf,':');
        if(mid_buf)
        {
            if(mid_buf < end_buf && mid_buf - temp_buf <=4)
            {
                /*确定是ipv6地址*/
                flag_ipv6 = 1;
            }
        }
    }

    
	/* 提取host */
	loop = 0;
	while ( *temp_buf != 0 && *temp_buf != '/' && *temp_buf != '?' 
            && ((flag_ipv6 == 0 && *temp_buf != ':') ||(flag_ipv6 && *temp_buf != ']')))
	{
		host[loop++] = *temp_buf++;
	}
	host[loop] = 0;


    if(flag_ipv6 && *temp_buf == ']')
    {
        temp_buf++;
    }
	
	/* 是否指定端口 */
	if ( *temp_buf == ':' )
	{
		temp_buf++;
		if ( *temp_buf >= '0' && *temp_buf <= '9' )
		{
			port = 0;
			while ( *temp_buf >= '0' && *temp_buf <= '9' )
				port = port * 10 + ((*temp_buf++) - '0');
			
			*port_out = port;
		}
	}
	

    if(flag_ipv6)
    {
        //将ipv6字符串地址转换为IPAddr0结构体
        ret = ivod_common_ipv6_str_to_int(host, (IPAddr0*)ip);
    }
    else
    {
        //将ipv4字符串地址转换为IPAddr0结构体
        ret = ivod_common_ipv4_str_to_int(host, (IPAddr0*)ip);
    }

    if(IPANEL_ERR == ret)
        ret = ivod_common_parse_domin(host, (IPAddr0*)ip);

    return ret;
	
}


/*将ipv6字符串地址转换为IPAddr0*/
int ivod_common_ipv6_str_to_int(char *ipv6,IPAddr0 *ipaddr)
{
    char *start_str = NULL,*p = NULL;
    char ip[8][5]={0},buf[8] = {0},temp[8] = {0};
    int i = 0,j = 0;
    
	FAILED_RETURNX( !ipv6 || !ipaddr ,IPANEL_ERR );

    ipaddr->version = IPANEL_IP_VERSION_6;

    start_str = strstr(ipv6,"::");
    if(start_str)
    {
        p = start_str + strlen("::");

        if(p < ipv6 + strlen(ipv6))
        {
            p = strstr(p,"::");
            if(p)
            {
                /*如果一个IPv6地址里面有两个::,那么该地址就是错误的*/
        		INFO("%s ipv6_str_to_int ipv6 addr = %s is error!!\n",COMMON_MARK,ipv6);
                return IPANEL_ERR;
            }
        }


        /*如果是正确的地址，需要按照含有::地址处理*/
        if(start_str == ipv6)
        {
            /*::A:B这种地址*/
            p = start_str + strlen("::");
            j = 0;
            while( p < ipv6 + strlen(ipv6) )
            {
                if(*p != ':')
                {
                    temp[j++] = *(p++);
                }
                else
                {
                    sprintf(buf,"0x%s",temp);
                    ipaddr->addr.ipv6.ip[i++] = (UINT16_T)strtol(buf,NULL,16);
                    memset(buf,0,sizeof(buf));
                    memset(temp,0,sizeof(temp));
                    j = 0;
                    p++;
                }
            }
            sprintf(buf,"0x%s",temp);
            ipaddr->addr.ipv6.ip[i++] = (UINT16_T)strtol(buf,NULL,16);

            /*交互数据*/
            i = 8 - i;
            for(j=7;j>=i;j--)
            {
                ipaddr->addr.ipv6.ip[j] = ipaddr->addr.ipv6.ip[j-i];
            }

            for(j=0;j<i;j++)
            {
                ipaddr->addr.ipv6.ip[j] = 0;
            }

        	INFO("%s ipv6_str_to_int normal format success NO1!!\n",COMMON_MARK);
            return IPANEL_OK;            
            
        }
        else if(start_str + strlen("::") < ipv6 + strlen(ipv6))
        {
            int left_num = 0;
            
            /*A::B这种地址*/


            /*处理::左边的ip地址*/
            p = ipv6;
            i = 0;
            j = 0;
            
            while( p < start_str )
            {
                if(*p != ':')
                {
                    temp[j++] = *(p++);
                }
                else
                {
                    sprintf(buf,"0x%s",temp);
                    ipaddr->addr.ipv6.ip[i++] = (UINT16_T)strtol(buf,NULL,16);
                    memset(buf,0,sizeof(buf));
                    memset(temp,0,sizeof(temp));
                    j = 0;
                    p++;
                }
            }
            sprintf(buf,"0x%s",temp);
            ipaddr->addr.ipv6.ip[i++] = (UINT16_T)strtol(buf,NULL,16);
            memset(buf,0,sizeof(buf));
            memset(temp,0,sizeof(temp));
            left_num = i;

            
            /*处理::右边的ip地址*/
            p = start_str + strlen("::");
            j = 0;
            while( p < ipv6 + strlen(ipv6) )
            {
                if(*p != ':')
                {
                    temp[j++] = *(p++);
                }
                else
                {
                    sprintf(buf,"0x%s",temp);
                    ipaddr->addr.ipv6.ip[i++] = (UINT16_T)strtol(buf,NULL,16);
                    memset(buf,0,sizeof(buf));
                    memset(temp,0,sizeof(temp));
                    j = 0;
                    p++;
                }
            }
            sprintf(buf,"0x%s",temp);
            ipaddr->addr.ipv6.ip[i++] = (UINT16_T)strtol(buf,NULL,16);


            /*交互数据*/
            i = 8 - i;
            for(j=7;j>=i+left_num;j--)
            {
                ipaddr->addr.ipv6.ip[j] = ipaddr->addr.ipv6.ip[j-i];
            }

            for(j=left_num;j<i+left_num;j++)
            {
                ipaddr->addr.ipv6.ip[j] = 0;
            }

        	INFO("%s ipv6_str_to_int normal format success NO2!!\n",COMMON_MARK);
            return IPANEL_OK;             
        }
        else if(start_str + strlen("::") == ipv6 + strlen(ipv6))
        {
            /*A:B::这种地址*/
            p = ipv6;
            j = 0;
            while( p < start_str )
            {
                if(*p != ':')
                {
                    temp[j++] = *(p++);
                }
                else
                {
                    sprintf(buf,"0x%s",temp);
                    ipaddr->addr.ipv6.ip[i++] = (UINT16_T)strtol(buf,NULL,16);
                    memset(buf,0,sizeof(buf));
                    memset(temp,0,sizeof(temp));
                    j = 0;
                    p++;
                }
            }
            sprintf(buf,"0x%s",temp);
            ipaddr->addr.ipv6.ip[i++] = (UINT16_T)strtol(buf,NULL,16);

            for(j=i;j<8;j++)
            {
                ipaddr->addr.ipv6.ip[j] = 0;
            }

        	INFO("%s ipv6_str_to_int normal format success NO3!!\n",COMMON_MARK);
            return IPANEL_OK;           
        }       
    }
    else
    {
        /*如果ipv6地址里面没有::,按照标准ipv6地址处理*/
        if (strlen(ipv6)<15 ||strlen(ipv6)>39)
            return IPANEL_ERR;
        
        if (8==sscanf(ipv6,"%[^:]:%[^:]:%[^:]:%[^:]:%[^:]:%[^:]:%[^:]:%s",
                            ip[0],ip[1],ip[2],ip[3],ip[4],ip[5],ip[6],ip[7]))
        {
            for(i=0;i<8;i++)
            {
                if(strlen(ip[i])>4)
                    return  IPANEL_ERR;
            }
        }
        else
            return IPANEL_ERR; 

        /*将16进制ip地址存入数组*/
        for(i=0;i<8;i++)
        {
            sprintf(buf,"0x%s",ip[i]);
            ipaddr->addr.ipv6.ip[i] = (UINT16_T)strtol(buf,NULL,16);
        	INFO("%s ipv6 ip[%d] = %s ip[%d] = %x!!\n",COMMON_MARK,i,ip[i],i,ipaddr->addr.ipv6.ip[i]);
        }

    	INFO("%s ipv6_str_to_int normal format success!!\n",COMMON_MARK);
        return IPANEL_OK;
    }

    return IPANEL_ERR;
}


/*将ipv4字符串地址转换为IPAddr0*/
int  ivod_common_ipv4_str_to_int(char *ipv4,IPAddr0 *ipaddr)
{
    int a=0,b=0,c=0,d=0,ret=IPANEL_ERR;
    char str_1[15]={0},str_2[15]={0},str_3[15]={0},str_4[15]={0};
    char t;

	FAILED_RETURNX( !ipv4 || !ipaddr ,IPANEL_ERR );
    
    if (strlen(ipv4)<7 ||strlen(ipv4)>15)
        return IPANEL_ERR;
    if (4==sscanf(ipv4,"%[^.].%[^.].%[^.].%s",str_1,str_2,str_3,str_4))
    {
        if (strlen(str_1)>3 || strlen(str_2)>3 || strlen(str_3)>3 || strlen(str_4)>3)
            return IPANEL_ERR;            
    }
    else
        return IPANEL_ERR; 
    
	if (4==sscanf(ipv4,"%d.%d.%d.%d%c",&a,&b,&c,&d,&t)) 
	{
		if (0<=a && a<=255
		 && 0<=b && b<=255
		 && 0<=c && c<=255
		 && 0<=d && d<=255) 
		{
            ipaddr->version = IPANEL_IP_VERSION_4;
            ipaddr->addr.ipv4 = (a<<24)|(b<<16)|(c<<8)|d;
            ret = IPANEL_OK;
		} 
	} 
    return ret;
}



/*获取一个动态端口*/
int ivod_common_get_dynamic_port(void)
{
	/******************************************************************************
	*端口号的划分以及用途:
	*熟知端口:0-1023 由IANA指派和控制
	*注册端口:1024-49151 IANA不指派也不控制,它们可在IANA注册以防止重复
	*动态端口:49152-65535 不用指派也不用注册,它们可以由任何进程使用,这些都是短暂端口
	*******************************************************************************/
	/*vod使用50000-60000之间的端口吧*/
	srand((unsigned) time(NULL)); 
	return (50000 + rand()%10000);
}


/*获取本机ip地址*/
int ivod_common_get_localIP(char *name,char *ipv4,char *ipv6)
{
#if 0
    int ret = IPANEL_ERR;
    struct ifaddrs * ifAddrStruct=NULL;
    void * tmpAddrPtr=NULL;

    getifaddrs(&ifAddrStruct);

    while (ifAddrStruct!=NULL) 
    {
        if (AF_INET == ifAddrStruct->ifa_addr->sa_family) 
        {
            //IPV4地址
            if(ipv4 && !strcmp(ifAddrStruct->ifa_name,"eth0"))
            {
                tmpAddrPtr=&((struct sockaddr_in *)ifAddrStruct->ifa_addr)->sin_addr;
                inet_ntop(AF_INET, tmpAddrPtr, ipv4, INET_ADDRSTRLEN);
                ret = IPANEL_OK;
            }
        } 

        if (AF_INET6 == ifAddrStruct->ifa_addr->sa_family) 
        {
            //IPV6地址
            if(ipv6 && !strcmp(ifAddrStruct->ifa_name,"eth0"))
            {
                tmpAddrPtr=&((struct sockaddr_in *)ifAddrStruct->ifa_addr)->sin_addr;
                inet_ntop(AF_INET6, tmpAddrPtr, ipv6, INET6_ADDRSTRLEN);
                ret = IPANEL_OK;
            }
        } 
        ifAddrStruct=ifAddrStruct->ifa_next;
    }
    return ret;
#else
    int ret = IPANEL_ERR;
    int sockfd;
    struct ifreq req;
    struct sockaddr_in *host;

	if (-1 == (sockfd = socket(AF_INET, SOCK_STREAM, 0))) 
    {
		return ret;
	}

	bzero(&req, sizeof(struct ifreq));
	strcpy(req.ifr_name, "eth0");
	if(ioctl(sockfd, SIOCGIFADDR, &req) < 0)
	{
        return ret;
    }
    
	host = (struct sockaddr_in*) &req.ifr_addr;

	 if (ipv4 && AF_INET == host->sin_family) 
    {
		//IPV4地址
		inet_ntop(AF_INET, &host->sin_addr, ipv4, INET_ADDRSTRLEN);
		INFO("%s[ivod_common_get_localIP] ipv4=%s \n",COMMON_MARK,ipv4);
		ret = IPANEL_OK;
	}


	 if (ipv6 && AF_INET6 == host->sin_family) 
    {
		 //IPV6地址
		inet_ntop(AF_INET6, &host->sin_addr, ipv6, INET6_ADDRSTRLEN);
		INFO("%s[ivod_common_get_localIP] ipv6=%s \n",COMMON_MARK,ipv6);
		ret = IPANEL_OK;
	}

	close(sockfd);
	return ret;
#endif
}



/*域名解析*/
int ivod_common_parse_domin(char *name,IPAddr0 *ipaddr)
{
    int ret = 0;
    struct addrinfo *result = NULL,*p = NULL;
    struct sockaddr_in *host = NULL;
    char strIP[64] = {0};

    if(NULL == name || NULL == ipaddr)
    {
        return IPANEL_ERR;
    }
    
	INFO("%s[ivod_common_parse_domin] domin=%s \n",COMMON_MARK,name);
    
	//获取主机信息
	ret = getaddrinfo(name,NULL,NULL,&result);
	if( ret != 0 )
	{
		INFO("%s[ivod_common_parse_domin] gethostbyname failed\n",COMMON_MARK);
        return IPANEL_ERR;
	}

    for(p=result;p!=NULL;p=p->ai_next)
    {
        host = (struct sockaddr_in *)p->ai_addr;
        if (AF_INET == host->sin_family) 
        {
            //IPV4地址
            inet_ntop(AF_INET, &host->sin_addr, strIP, INET_ADDRSTRLEN);
            INFO("%s[ivod_common_parse_domin] ipv4=%s \n",COMMON_MARK,strIP);
            freeaddrinfo(result);
            //将ipv4字符串地址转换为IPAddr0结构体
            return ivod_common_ipv4_str_to_int(strIP, ipaddr);
        }


        if (AF_INET6 == host->sin_family) 
        {
             //IPV6地址
            inet_ntop(AF_INET6, &host->sin_addr, strIP, INET6_ADDRSTRLEN);
            INFO("%s[ivod_common_parse_domin] ipv6=%s \n",COMMON_MARK,strIP);
            freeaddrinfo(result);
            //将ipv6字符串地址转换为IPAddr0结构体
            return ivod_common_ipv6_str_to_int(strIP, ipaddr);
        }
         
    }

    //释放空间
    freeaddrinfo(result);
    return IPANEL_ERR;
}

/************************************End Of File**********************************/

