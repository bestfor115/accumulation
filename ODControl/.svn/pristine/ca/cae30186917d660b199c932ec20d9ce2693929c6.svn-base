/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:SOCKET操作实现
*********************************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <sys/param.h>
#include <sys/stat.h>
#include <sys/ioctl.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <sys/file.h>
#include <netdb.h>
#include <netinet/in.h>
#include <netinet/ip.h>
#include <arpa/inet.h>
#include <sys/param.h>

#include <sys/ioctl.h> 
#include <net/if.h> 
#include <signal.h>

 
#include "ipanel_porting.h"


#define MAX_SOCKETS             (10)
#define CONNECT_TIMEOUT         (8)
#define MAXINTERFACES           (16) 


#define FD_AVAILAB_CHECK(fd) if(fd<0) \
{ \
    return IPANEL_ERR; \
}

typedef struct tagSocketMgr
{
    INT32_T enable;
    INT32_T sockfd;
} socketMgr;

static socketMgr pSocketMgr[MAX_SOCKETS] = {0};
static unsigned int max_nfds = 0;

static INT32_T socketMgr_open(INT32_T sockfd)
{
    INT32_T index=0;

    if (sockfd<0)
    {
        return IPANEL_ERR;
    }
    for (index=0; index<MAX_SOCKETS; index++)
    {
        if (pSocketMgr[index].enable==0)
        {
            pSocketMgr[index].enable = 1;
            pSocketMgr[index].sockfd = sockfd;
            max_nfds = (max_nfds<sockfd) ? sockfd : max_nfds;
            return (index+1);
        }
    }
    return IPANEL_ERR;
}

static void socketMgr_close(INT32_T sockfd)
{
    INT32_T index=0;

    pSocketMgr[sockfd-1].enable = 0;
    max_nfds = 0;
    for (index=0; index<MAX_SOCKETS; index++)
    {
        if (pSocketMgr[index].enable)
            max_nfds = (max_nfds<pSocketMgr[index].sockfd) ? pSocketMgr[index].sockfd : max_nfds;
    }
}

static INT32_T socketMgr_get(INT32_T sockfd)
{
    if ((sockfd<=0) || (sockfd>MAX_SOCKETS))
    {
        return IPANEL_ERR;
    }

    return pSocketMgr[sockfd-1].sockfd;
}

void processsignal(int signal)
{
    ;
}

/********************************************************************************************************
功能：建立一个套接字
原型：INT32_T ipanel_porting_socket_open(INT32_T family, INT32_T type, INT32_T protocol)
参数说明：
输入参数：
family：指定要创建的套接字协议族。IPANEL_AF_INET：表示IPv4协议，IPANEL_AV_INET6表示IPv6协议，其他值保留
type：指定要创建的套接字的类型。IPANEL_SOCK_STREAM：表示SOCK_STREAM，IPANEL_SOCK_DGRAM：表示SOCK_DGRAM，其他值保留
protocol：指定哪种协议。通常设置为IPANEL_IPPROTO_IP，表示与type匹配的默认协议，其他值保留
输出参数：无
返    回：
若套接字建立成功，则返回非负描述符，否则返回IPANEL_ERR。
>=0: 套接字建立成功，则返回非负描述符；
IPANEL_ERR: 创建套接字失败。
********************************************************************************************************/
INT32_T ipanel_porting_socket_open(INT32_T family,
								   INT32_T type,
								   INT32_T protocol)
{
	int m_s = IPANEL_ERR;
	int domain = 0;

	if ((IPANEL_AF_INET==family||IPANEL_AF_INET6==family) 
		&& (IPANEL_SOCK_STREAM==type||IPANEL_SOCK_DGRAM==type) 
		&& (IPANEL_IPPROTO_IP==protocol))
	{
		domain = (IPANEL_AF_INET6 !=family ) ? AF_INET : AF_INET6;
		type = (IPANEL_SOCK_STREAM == type) ? SOCK_STREAM : SOCK_DGRAM;
		m_s = socket(domain, type, 0);

        if(m_s > 0)
            return socketMgr_open(m_s);
    }
	
	return m_s;
}

/********************************************************************************************************
功能：将指定的端口号与套接字绑定在一起
原型：INT32_T ipanel_porting_socket_bind(INT32_T sockfd, IPANEL_IPADDR* ipaddr, INT32_T   port)
参数说明：
输入参数：
sockfd：套接字描述符
ipaddr：包含待绑定IP地址和IP版本描述结构体的指针
port：端口号
输出参数：无
返    回：
IPANEL_OK:成功;
IPANEL_ERR:失败。
********************************************************************************************************/
INT32_T ipanel_porting_socket_bind(INT32_T sockfd,
								   IPANEL_IPADDR* ipaddr,
								   INT32_T   port)
{
	int ret     = IPANEL_ERR;
    struct sockaddr_in serv_addr = {0} ;
    struct sockaddr_in6     dest = {0};      // IPv6地址结构
    int i = 0,j = 0;
    
    int fd = socketMgr_get(sockfd);

    FD_AVAILAB_CHECK(fd);

    if(IPANEL_IP_VERSION_6 != ipaddr->version )
    {
		memset(&serv_addr, 0, sizeof(serv_addr));
        serv_addr.sin_family     = AF_INET;
    	serv_addr.sin_addr.s_addr= htonl(ipaddr->addr.ipv4);
    	serv_addr.sin_port       = htons(port);

        ret = bind(fd, (struct sockaddr*)&serv_addr, sizeof(serv_addr));
    }
    else
    {
        /* IPv6协议 */
		memset(&dest, 0, sizeof(dest));
        dest.sin6_family = AF_INET6;
        dest.sin6_port = htons(port);

        if(ipaddr->addr.ipv6.ip[0] == 0
            && ipaddr->addr.ipv6.ip[1] == 0
            && ipaddr->addr.ipv6.ip[2] == 0
            && ipaddr->addr.ipv6.ip[3] == 0
            && ipaddr->addr.ipv6.ip[4] == 0
            && ipaddr->addr.ipv6.ip[5] == 0
            && ipaddr->addr.ipv6.ip[6] == 0
            && ipaddr->addr.ipv6.ip[7] == 0)
        {
            dest.sin6_addr = in6addr_any;
        }
        else
        {
            for(i=0;i<16;i+=2)
            {
                dest.sin6_addr.s6_addr[i] = (BYTE_T)((ipaddr->addr.ipv6.ip[j] & 0xff00)>>8);
                dest.sin6_addr.s6_addr[i+1] = (BYTE_T)(ipaddr->addr.ipv6.ip[j] & 0x00ff);
                j++;
            }
        }
        
        ret = bind(fd, (struct sockaddr*)&dest, sizeof(dest)); 
    }

    
    return ret;
}

/********************************************************************************************************
功能：将一个套接字转换成监听套接字
原型：INT32_T ipanel_porting_socket_listen(INT32_T sockfd, INT32_T backlog)
参数说明：
输入参数：
sockfd：套接字描述符
backlog：接收队列的最大长度
输出参数：无
返    回：
IPANEL_OK:成功;
IPANEL_ERR:失败。
********************************************************************************************************/
INT32_T ipanel_porting_socket_listen(INT32_T sockfd, INT32_T backlog)
{
	int ret = IPANEL_ERR;
    int fd = socketMgr_get(sockfd);

    FD_AVAILAB_CHECK(fd);

    ret = listen(fd, backlog);

    if (ret != IPANEL_OK)
    {
        ret = IPANEL_ERR;
    }
	return ret;
}

/********************************************************************************************************
功能：响应连接请求，建立连接并产生一个新的socket描述符来描述该连接
原型：INT32_T ipanel_porting_socket_accept(INT32_T sockfd, IPANEL_IPADDR* ipaddr, INT32_T*   port)
参数说明：
输入参数：
sockfd：监听套接字描述符
ipaddr：包含IP版本描述的结构体指针
输出参数：无
ipaddr：保存连接对方IP地址的结构体指针
port：保存连接对方的端口号
返    回：
>=0:执行成功则返回非负描述符。
IPANEL_ERR:失败。
********************************************************************************************************/
INT32_T ipanel_porting_socket_accept(INT32_T sockfd,
									 IPANEL_IPADDR* ipaddr,
									 INT32_T*   port)
{
	int ret     = IPANEL_ERR;
	struct sockaddr_in serv_addr = {0};
    struct sockaddr_in6     dest = {0};      // IPv6地址结构 
    
    int fd = socketMgr_get(sockfd);

    if (fd<0 || NULL == ipaddr)
    {
        return IPANEL_ERR;
    }

    if(IPANEL_IP_VERSION_6 != ipaddr->version)
    {
        int len = sizeof(serv_addr);
		memset(&serv_addr, 0, sizeof(serv_addr));
        
        ret = accept(fd, (struct sockaddr*)&serv_addr, &len);
        if (ret >= 0)
        {
            ipaddr->addr.ipv4 = ntohl(serv_addr.sin_addr.s_addr);
            ipaddr->version = IPANEL_IP_VERSION_4;

            if (port)
            {
                *port = ntohs(serv_addr.sin_port);
            }

            ret = socketMgr_open(ret);
        }
    }
    else
    {
        /* IPv6协议 */
        int len = sizeof(dest);
        UINT16_T temp_ip = 0;
        int i = 0,j = 0;
        
		memset(&dest, 0, sizeof(dest));
        
        ret = accept(fd, (struct sockaddr*)&dest, &len);
        if (ret >= 0)
        {
            //保存服务器ip地址及端口
            for(i=0;i<16;i+=2)
            {
                temp_ip = (dest.sin6_addr.s6_addr[i]<<8) | dest.sin6_addr.s6_addr[i+1];
                ipaddr->addr.ipv6.ip[j++] = temp_ip;                        
            }

            ipaddr->version = IPANEL_IP_VERSION_6;

            if (port)
            {
                *port = ntohs(dest.sin6_port);
            }                    
            
            ret = socketMgr_open(ret);
        }    
    }
    
    return ret;
}

/********************************************************************************************************
功能：将套接字与指定的套接字连接起来
原型：INT32_T ipanel_porting_socket_connect(INT32_T sockfd, IPANEL_IPADDR *ipaddr, INT32_T   port)
参数说明：
输入参数：
sockfd：套接字描述符
ipaddr：包含待连接IP地址及IP版本描述的结构体指针
port：待连接的端口号
输出参数：无
返    回：
IPANEL_OK:成功;
IPANEL_ERR:失败。
********************************************************************************************************/
INT32_T ipanel_porting_socket_connect(INT32_T sockfd,
									  IPANEL_IPADDR *ipaddr,
									  INT32_T   port)
{
	int ret = IPANEL_ERR;
    struct sockaddr_in serv_addr = {0};
    struct sockaddr_in6     dest = {0};      // IPv6地址结构
    int i = 0,j = 0;
    
    int fd = socketMgr_get(sockfd);

    if (fd<0 || NULL == ipaddr)
    {
        return ret;
    }

    if(IPANEL_IP_VERSION_6 != ipaddr->version)
    {
		memset(&serv_addr, 0, sizeof(serv_addr));
        serv_addr.sin_family     = AF_INET;
        serv_addr.sin_addr.s_addr= htonl(ipaddr->addr.ipv4);
        serv_addr.sin_port       = htons(port);

        ret = connect(fd, (struct sockaddr*)&serv_addr, sizeof(serv_addr));
    }
    else
    {
        /* IPv6协议 */
		memset(&dest, 0, sizeof(dest));
        dest.sin6_family = AF_INET6;
        dest.sin6_port = htons(port);

        for(i=0;i<16;i+=2)
        {
            dest.sin6_addr.s6_addr[i] = (BYTE_T)((ipaddr->addr.ipv6.ip[j] & 0xff00)>>8);
            dest.sin6_addr.s6_addr[i+1] = (BYTE_T)(ipaddr->addr.ipv6.ip[j] & 0x00ff);

            j++;
        }
        ret = connect(fd, (struct sockaddr*)&dest, sizeof(dest));
    }

    if (ret != IPANEL_OK)
    {
        signal(SIGPIPE,processsignal);
        ret = IPANEL_ERR;
    }    
    
	return ret;
}

/********************************************************************************************************
功能：发送数据到套接字
原型：INT32_T ipanel_porting_socket_send(INT32_T sockfd, CHAR_T   *buf, INT32_T buflen, INT32_T  flags)
参数说明：
输入参数：
sockfd：套接字描述符
buf：待发送数据的缓冲区
len：缓冲区中数据的长度
flags：操作选项，一般设为0，其他值保留
输出参数：无
返    回：
>0:执行成功返回实际发送的数据长度;
IPANEL_ERR:失败。
********************************************************************************************************/
INT32_T ipanel_porting_socket_send(INT32_T sockfd,
								   CHAR_T   *buf,
								   INT32_T buflen,
								   INT32_T  flags)
{
	int ret = IPANEL_ERR;
    int fd = socketMgr_get(sockfd);

    FD_AVAILAB_CHECK(fd);

    if (NULL == buf || buflen<=0)
    {
        return IPANEL_ERR;
    }

    ret = send(fd, buf, buflen, 0);

    if (ret < 0)
    {
        ret = IPANEL_ERR;
    }
    
    return ret;
}

/********************************************************************************************************
功能：发送数据到指定套接字
原型：INT32_T ipanel_porting_socket_sendto(INT32_T sockfd, CHAR_T*    buf,
INT32_T buflen, INT32_T  flags, IPANEL_IPADDR* ipaddr, INT32_T   port)
参数说明：
输入参数：
sockfd：套接字描述符
buf：待发送数据的缓冲区
len：缓冲区中数据的长度
flags：操作选项，一般设为0，其他值保留
ipaddr：包含目的IP地址和IP版本描述的结构体指针
port：目的端口号
输出参数：无
返    回：
>0:执行成功返回实际发送的数据长度;
IPANEL_ERR:失败。
********************************************************************************************************/
INT32_T ipanel_porting_socket_sendto(INT32_T sockfd,
									 CHAR_T*    buf,
									 INT32_T buflen,
									 INT32_T  flags,
									 IPANEL_IPADDR* ipaddr,
									 INT32_T   port)
{
	int ret     = IPANEL_ERR;
    struct sockaddr_in serv_addr = {0};
    struct sockaddr_in6     dest = {0};      // IPv6地址结构
    int i = 0,j = 0;
    int fd = socketMgr_get(sockfd);

    FD_AVAILAB_CHECK(fd);

    if (NULL == buf || NULL == ipaddr || (buflen<=0) || (!port))
    {
        return ret;
    }


    if( IPANEL_IP_VERSION_6 != ipaddr->version)
    {
		memset(&serv_addr, 0, sizeof(serv_addr));
        serv_addr.sin_family     = AF_INET;
        serv_addr.sin_addr.s_addr= htonl(ipaddr->addr.ipv4);
        serv_addr.sin_port       = htons(port);

        ret = sendto(fd, buf, buflen, flags, (struct sockaddr*)&serv_addr, sizeof(serv_addr));
    }
    else
    {
        /* IPv6协议 */
		memset(&dest, 0, sizeof(dest));
        dest.sin6_family = AF_INET6;
        dest.sin6_port = htons(port);

        for(i=0;i<16;i+=2)
        {
            dest.sin6_addr.s6_addr[i] = (BYTE_T)((ipaddr->addr.ipv6.ip[j] & 0xff00)>>8);
            dest.sin6_addr.s6_addr[i+1] = (BYTE_T)(ipaddr->addr.ipv6.ip[j] & 0x00ff);
            j++;
        }
        ret = sendto(fd, buf, buflen, flags, (struct sockaddr*)&dest, sizeof(dest));
    }
    
	return ret;
}

/********************************************************************************************************
功能：从一个套接字接收数据
原型：INT32_T ipanel_porting_socket_recv(INT32_T sockfd, CHAR_T    *buf, INT32_T buflen, INT32_T  flags)
参数说明：
输入参数：
sockfd：套接字描述符
buf：用于接收数据的缓冲区
len：缓冲区的长度
flags：操作选项，一般设为0，其他值保留
输出参数：无
返    回：
>0:执行成功返回实际接收的数据长度;
IPANEL_ERR:失败。
********************************************************************************************************/
INT32_T ipanel_porting_socket_recv(INT32_T sockfd,
								   CHAR_T    *buf,
								   INT32_T buflen,
								   INT32_T  flags)
{
	int ret = IPANEL_ERR;
    int fd = socketMgr_get(sockfd);

    FD_AVAILAB_CHECK(fd);

    if ((!buf) || buflen<=0)
    {
    	return ret;
    }

    ret = recv(fd, buf, buflen, 0);

    if (ret < 0)
    {
        ret = IPANEL_ERR;
    }

	return ret;
}

/********************************************************************************************************
功能：从一个套接字接收数据
原型：INT32_T ipanel_porting_socket_recvfrom(INT32_T sockfd, CHAR_T*    buf,
INT32_T buflen, INT32_T  flags, IPANEL_IPADDR* ipaddr, INT32_T*   port)
参数说明：
输入参数：
sockfd：套接字描述符
buf：用于接收数据的缓冲区
len：缓冲区的长度
flags：操作选项，一般设为0，其他值保留
ipaddr：包含目的IP地址和IP版本描述的结构体指针
port：目的端口号
输出参数:
ipaddr：保存发送方IP地址的结构体指针
port：保存发送方的端口号
返    回：
>0:执行成功返回实际接收的数据长度;
IPANEL_ERR:失败。
********************************************************************************************************/
INT32_T ipanel_porting_socket_recvfrom(INT32_T sockfd,
									   CHAR_T*    buf,
									   INT32_T buflen,
									   INT32_T  flags,
									   IPANEL_IPADDR* ipaddr,
									   INT32_T*   port)
{
	int ret     = IPANEL_ERR;
    struct sockaddr_in serv_addr = {0};
    struct sockaddr_in6     dest = {0};      // IPv6地址结构
    int i = 0,j = 0;
    
    int fd = socketMgr_get(sockfd);

    FD_AVAILAB_CHECK(fd);

    if(NULL == buf || (buflen<=0))
    {
        return ret;
    }

    if (NULL != ipaddr)
    {
        if(IPANEL_IP_VERSION_6 != ipaddr->version)
        {
            int len = sizeof(serv_addr);

			memset(&serv_addr, 0, sizeof(serv_addr));
            ret = recvfrom(fd, buf, buflen, flags, (struct sockaddr*)&serv_addr, &len);
            if (ret > 0)
            {
                ipaddr->addr.ipv4 = ntohl(serv_addr.sin_addr.s_addr);
                ipaddr->version = IPANEL_IP_VERSION_4;

                if (port)
                {
                    *port = ntohs(serv_addr.sin_port);
                }
            }
        }
        else
        {
             /* IPv6协议 */
            int len = sizeof(dest);
            UINT16_T temp_ip = 0;
             
			memset(&dest, 0, sizeof(dest));
            ret = recvfrom(fd, buf, buflen, flags, (struct sockaddr*)&dest, &len);
            if (ret > 0)
            {
                //保存服务器ip地址及端口
                for(i=0;i<16;i+=2)
                {
                    temp_ip = (dest.sin6_addr.s6_addr[i]<<8) | dest.sin6_addr.s6_addr[i+1];
                    ipaddr->addr.ipv6.ip[j++] = temp_ip;                        
                }
                
                ipaddr->version = IPANEL_IP_VERSION_6;
                
                if (port)
                {
                    *port = ntohs(dest.sin6_port);
                }                    
            }        
        }
    }
    else
    {
        ret = recvfrom(fd, buf, buflen, flags, NULL, 0);
    }

	return ret;
}

/********************************************************************************************************
功能：获取与套接字有关的本地协议地址，即主机地址
原型：INT32_T ipanel_porting_socket_getsockname(INT32_T sockfd, IPANEL_IPADDR* ipaddr, INT32_T*   port)
参数说明：
输入参数：
sockfd：套接字描述符
ipaddr：包含IP版本描述的结构体指针
输出参数:
ipaddr：保存主机协议IP地址的结构体指针
port：保存主机协议的端口号
返    回：
IPANEL_OK:成功;
IPANEL_ERR:失败。
********************************************************************************************************/
INT32_T ipanel_porting_socket_getsockname(INT32_T sockfd,
										  IPANEL_IPADDR* ipaddr,
										  INT32_T*   port)
{
	int ret = IPANEL_ERR;
    struct sockaddr_in serv_addr = {0};
    struct sockaddr_in6     dest = {0};      // IPv6地址结构
    int i = 0,j = 0;
    int fd = socketMgr_get(sockfd);

    FD_AVAILAB_CHECK(fd);

    if(NULL == ipaddr || NULL == port)
        return IPANEL_ERR;


    if(IPANEL_IP_VERSION_6 != ipaddr->version)
    {
        int len = sizeof(serv_addr);
		memset(&serv_addr, 0, sizeof(serv_addr));
        
        ret = getsockname(fd, (struct sockaddr*)&serv_addr, &len);
        if (ret >= 0)
        {
            ipaddr->addr.ipv4 = ntohl(serv_addr.sin_addr.s_addr);
            ipaddr->version = IPANEL_IP_VERSION_4;
            *port = ntohs(serv_addr.sin_port);
        }
    }
    else
    {
        /* IPv6协议 */
        int len = sizeof(dest);
        UINT16_T temp_ip = 0;
        
		memset(&dest, 0, sizeof(dest));
        
        ret = getsockname(fd, (struct sockaddr*)&dest, &len);
        if (ret >= 0)
        {
            //保存服务器ip地址及端口
            for(i=0;i<16;i+=2)
            {
                temp_ip = (dest.sin6_addr.s6_addr[i]<<8) | dest.sin6_addr.s6_addr[i+1];
                ipaddr->addr.ipv6.ip[j++] = temp_ip;                        
            }
            
            ipaddr->version = IPANEL_IP_VERSION_6;
            *port = ntohs(dest.sin6_port);            
        }
    }    

    if (ret != IPANEL_OK)
    {
        ret = IPANEL_ERR;
    }
    
	return ret;
}

/********************************************************************************************************
功能：确定一个或多个套接字的状态，判断套接字上是否有数据，或者能否向一个套接字写入数据。
原型：INT32_T ipanel_porting_socket_select(INT32_T nfds, IPANEL_FD_SET_S*   readset,
IPANEL_FD_SET_S*  writeset, IPANEL_FD_SET_S* exceptset, INT32_T   timeout)
参数说明：
输入参数：
nfds：    需要检查的文件描述符个数，数值应该比readset、writeset、exceptset中最大数更大，而不是实际的文件描述符总数
readset： 用来检查可读性的一组文件描述符
writeset：用来检查可写性的一组文件描述符
exceptset：用来检查意外状态的文件描述符，错误不属于意外状态
timeout： 大于0表示等待多少毫秒；为IPANEL_NO_WAIT(0)时表示不等待立即返回，为IPANEL_WAIT_FOREVER(-1)表示永久等待。
输出参数: 无
返    回：
响应操作的对应操作文件描述符的总数，且readset、writeset、exceptset三组数据均在恰当位置被修改；
IPANEL_OK:要查询的文件描叙符已准备好;
IPANEL_ERR:失败，等待超时或出错。
********************************************************************************************************/
INT32_T ipanel_porting_socket_select(INT32_T      nfds,
									 IPANEL_FD_SET_S*   readset,
									 IPANEL_FD_SET_S*  writeset,
									 IPANEL_FD_SET_S* exceptset,
									 INT32_T   timeout)
{
    int ret = IPANEL_ERR;
    struct timeval tm;
    fd_set fds_r ,fds_w ,fds_e;
    int index=0, select_flag = 0;
    int i = 0,j = 0;

    if (timeout==-1)
    {
        tm.tv_sec = 0xffffffff;
        tm.tv_usec = 0xffffffff;
    }
    else if (timeout>=0)
    {
        tm.tv_sec = timeout/1000;
        tm.tv_usec = (timeout%1000)*1000;
    }
    else
    {
        return ret;
    }

    FD_ZERO(&fds_r);
    FD_ZERO(&fds_w);
    FD_ZERO(&fds_e);

    for (index=0; index<MAX_SOCKETS; index++)
    {
        i = index/IPANEL_NFDBITS;
        j = index%IPANEL_NFDBITS;
        if (pSocketMgr[index].enable)
        {
            if (readset)
            {
                if ((readset->fds_bits[i]>>(j+1))&1)
                {
                    FD_SET(pSocketMgr[index].sockfd, &fds_r);
                    select_flag = 1;
                }
            }
            if (writeset)
            {
                if ((writeset->fds_bits[i]>>(j+1))&1)
                {
                    FD_SET(pSocketMgr[index].sockfd, &fds_w);
                    select_flag = 1;
                }
            }
            if (exceptset)
            {
                if ((exceptset->fds_bits[i]>>(j+1))&1)
                {
                    FD_SET(pSocketMgr[index].sockfd, &fds_e);
                    select_flag = 1;
                }
            }
        }
    }
    if (select_flag == 0)
    {
        return IPANEL_OK;
    }
    
    ret = select(max_nfds+1, &fds_r, &fds_w, &fds_e, &tm);
    
    if (ret < 0)
    {
        return IPANEL_ERR;
    }
    
    if (readset)
        memset(readset->fds_bits, 0, sizeof(IPANEL_FD_MASK));
    if (writeset)
        memset(writeset->fds_bits, 0, sizeof(IPANEL_FD_MASK));
    if (exceptset)
        memset(exceptset->fds_bits, 0, sizeof(IPANEL_FD_MASK));

    for (index=0; index<MAX_SOCKETS; index++)
    {
        i = index/IPANEL_NFDBITS;
        j = index%IPANEL_NFDBITS;
        
        if (pSocketMgr[index].enable)
        {
            if (readset)
            {
                if (FD_ISSET(pSocketMgr[index].sockfd, &fds_r))
                {
                    readset->fds_bits[i] |= (1<<(j+1));
                }
            }
            if (writeset)
            {
                if (FD_ISSET(pSocketMgr[index].sockfd, &fds_w))
                {
                    writeset->fds_bits[i] |= (1<<(j+1));
                }
            }
            if (exceptset)
            {
                if (FD_ISSET(pSocketMgr[index].sockfd, &fds_e))
                {
                    exceptset->fds_bits[i] |= (1<<(j+1));
                }
            }
        }
    }

    return IPANEL_OK;
}

/********************************************************************************************************
功能：改变套接字属性为非阻塞。注：此函数只提供将套接字属性设置为非阻塞，不提供将套接字属性设置为阻塞的功能
原型：INT32_T ipanel_porting_socket_ioctl(INT32_T sockfd, INT32_T cmd, INT32_T arg)
参数说明：
输入参数：
sockfd：套接字描述符
cmd：命令类型，为IPANEL_FIONBIO
arg：命令参数，1表示非阻塞
输出参数:无
返    回：
IPANEL_OK:成功;
IPANEL_ERR:失败。
********************************************************************************************************/
INT32_T ipanel_porting_socket_ioctl(INT32_T sockfd, INT32_T cmd, INT32_T arg)
{
	int ret = IPANEL_ERR;
    int flags = 0;
    int fd = socketMgr_get(sockfd);

    FD_AVAILAB_CHECK(fd);

    if(IPANEL_FIONBIO == cmd)
    {
        flags = fcntl(fd, F_GETFL);

        if((1 == arg)&& !(flags & O_NONBLOCK))
        {
            ret = fcntl(fd, F_SETFL, flags | O_NONBLOCK);
        }

        if((0 == arg) && (flags & O_NONBLOCK))
        {
            ret = fcntl(fd, F_SETFL, flags & ~O_NONBLOCK);
        } 
    }
    
    if (ret != IPANEL_OK)
    {
        ret = IPANEL_ERR;
    }

    return ret;
}
/********************************************************************************************************
功能说明：
获取套接字的属性。可获取套接字的属性见下表中get项为Y的属性项。
参数说明：
输入参数：
sockfd： 套接字描述符
level： 选项定义的层次
optname：需要获取的属性名
+---------------------+-------------------------+-------+-----+---------------+
|  level              |   optname               |  get  | set | 数据类型      |
+---------------------+-------------------------+-------+-----+---------------+
|                                       |IPANEL_SO_BROADCAST            |   Y       |  Y    |   INT32_T     |
|                     |                             |       |     |               |
|  IPANEL_SOL_SOCKET  |IPANEL_SO_KEEPALIVE          |   Y       |  Y    |   INT32_T     |
|                     |                             |       |     |               |
|                     |IPANEL_SO_REUSEADDR          |   Y       |  Y    |   INT32_T     |
|                     |                                                 |           |       |                     |
+---------------------+-------------------------+-----------------------------+
|                                   |IPANEL_IP_ADD_MEMBERSHIP   |   N       |  Y    | PANEL_IP_MREQ |
|IPANEL_IPPROTO_IP    |                             |       |     |               |
|                                       |IPANEL_IP_DROP_MEMBERSHIP|   N     |  Y    | PANEL_IP_MREQ |              
|                     |                             |       |     |               |
+---------------------+-------------------------+-----------------------------+
输出参数：
optval：指向保存属性值的缓冲区
optlen：指向保存属性值的长度
返    回：
IPANEL_OK:成功
IPANEL_ERR:失败。

********************************************************************************************************/
INT32_T ipanel_porting_socket_getsockopt(INT32_T sockfd,
										 INT32_T level,
										 INT32_T optname,
										 VOID *optval,
										 INT32_T *optlen)
{
	return IPANEL_ERR;
}

/********************************************************************************************************
功能说明：
设置套接字的属性。可设置的套接字的属性见下表中set项为Y的属性项。
参数说明：
输入参数：
sockfd：套接字描述符
level： 选项定义的层次
optname：需要设置的属性名
optval：指向保存属性值的缓冲区
optlen： optval缓冲区的长度

 +---------------------+-------------------------+-------+-----+---------------+
 |  level              |   optname               |  get  | set | 数据类型      |
 +---------------------+-------------------------+-------+-----+---------------+
 |                                       |IPANEL_SO_BROADCAST            |   Y       |  Y    |   INT32_T     |
 |                     |                             |       |     |               |
 |  IPANEL_SOL_SOCKET  |IPANEL_SO_KEEPALIVE          |   Y       |  Y    |   INT32_T     |
 |                     |                             |       |     |               |
 |                     |IPANEL_SO_REUSEADDR          |   Y       |  Y    |   INT32_T     |
 |                     |                                                 |           |       |                     |
 +---------------------+-------------------------+-----------------------------+
 |                                   |IPANEL_IP_ADD_MEMBERSHIP   |   N       |  Y    | PANEL_IP_MREQ |
 |IPANEL_IPPROTO_IP    |                             |       |     |               |
 |                                       |IPANEL_IP_DROP_MEMBERSHIP|   N     |  Y    | PANEL_IP_MREQ |              
 |                     |                             |       |     |               |
 +---------------------+-------------------------+-----------------------------+
 输出参数：无
 返    回：
 IPANEL_OK:成功；
 IPANEL_ERR:失败。
 
  
********************************************************************************************************/
INT32_T ipanel_porting_socket_setsockopt(INT32_T sockfd,
										 INT32_T level,
										 INT32_T optname,
										 VOID *optval,
										 INT32_T optlen)
{
    int ret = IPANEL_ERR;
	int loop = 0;
    int fd = socketMgr_get(sockfd);

    FD_AVAILAB_CHECK(fd);

    if (level == IPANEL_SOL_SOCKET )
    {
    	if( optname == IPANEL_SO_LINGER )
    	{
            struct linger value = {0};
            IPANEL_LINGER *val = (IPANEL_LINGER *)optval;
            value.l_onoff = val->l_onoff;
            value.l_linger = val->l_linger;
            ret = setsockopt(fd, SOL_SOCKET, SO_LINGER, &value, sizeof(struct linger));
    	}
    	else if( optname == IPANEL_SO_RCVBUF )
    	{
    		ret = setsockopt(fd, SOL_SOCKET, SO_RCVBUF, (char *)optval, optlen );
    	}
    	else if( optname == IPANEL_SO_RCVTIMEO )
    	{
    		ret = setsockopt(fd, SOL_SOCKET, SO_RCVTIMEO, (char *)optval, optlen );
    	}
    	else if( optname == IPANEL_SO_SNDBUF )
    	{
    		ret = setsockopt(fd, SOL_SOCKET, SO_SNDBUF, (char *)optval, optlen );
    	}
        else if( optname == IPANEL_SO_REUSEADDR)
        {
    		ret = setsockopt(fd, SOL_SOCKET, SO_REUSEADDR, (char *)optval, optlen );
        }
    }
    else if (level == IPANEL_IPPROTO_IP && (optname==IPANEL_IP_ADD_MEMBERSHIP || optname==IPANEL_IP_DROP_MEMBERSHIP))
    {
        struct ip_mreq ipMreq = {0};
        IPANEL_IP_MREQ *mreq = (IPANEL_IP_MREQ *)optval;
        ipMreq.imr_multiaddr.s_addr = htonl(mreq->imr_multiaddr.addr);
        ipMreq.imr_interface.s_addr = htonl(mreq->imr_interface.addr);
        if (IPANEL_IP_ADD_MEMBERSHIP == optname)
        {
            /*设置LOOP为0,不接收本机发出的组播数据*/
			setsockopt(fd, IPPROTO_IP, IP_MULTICAST_LOOP, (char *)&loop, sizeof(loop));
            
            ret = setsockopt(fd, IPPROTO_IP, IP_ADD_MEMBERSHIP, (char *)&ipMreq, sizeof(ipMreq));
        }
        else
            ret = setsockopt(fd, IPPROTO_IP, IP_DROP_MEMBERSHIP, (char *)&ipMreq, sizeof(ipMreq));
    }
    else if (level == IPANEL_IPPROTO_IPV6 && (optname==IPANEL_IP_ADD_MEMBERSHIP || optname==IPANEL_IP_DROP_MEMBERSHIP))
    {
        /*
        struct ipv6_mreq 
        {
              struct in6_addr  ipv6mr_multiaddr;
              unsigned int     ipv6mr_interface;
        }*/ 
        struct ipv6_mreq mreq = { 0 };
        int i = 0,j = 0;        
        IPANEL_IPV6_MREQ *val = (IPANEL_IPV6_MREQ*)optval;

        for(i=0;i<16;i+=2)
        {
            mreq.ipv6mr_multiaddr.s6_addr[i] = (BYTE_T)((val->imr_multiaddr.addr.ipv6.ip[j] & 0xff00)>>8);
            mreq.ipv6mr_multiaddr.s6_addr[i+1] = (BYTE_T)(val->imr_multiaddr.addr.ipv6.ip[j] & 0x00ff);

            j++;
        }

        //本地ip地址使用默认地址
        mreq.ipv6mr_interface = 0;

							
        if(IPANEL_IP_ADD_MEMBERSHIP == optname)
		{
            /*设置LOOP为0,不接收本机发出的组播数据*/
            setsockopt(fd,IPPROTO_IPV6, IPV6_MULTICAST_LOOP, (char *)&loop, sizeof(loop));
            
            ret = setsockopt(fd,IPPROTO_IPV6, IPV6_ADD_MEMBERSHIP, (char*)&mreq, sizeof(mreq));
        }
        else if(IPANEL_IP_DROP_MEMBERSHIP == optname)
		{
            ret = setsockopt(fd,IPPROTO_IPV6, IPV6_DROP_MEMBERSHIP,(char*)&mreq, sizeof(mreq));
        }       
    }

    if (ret != IPANEL_OK)
    {
        ret = IPANEL_ERR;
    }
    
    return ret;
}

/********************************************************************************************************
功能：禁止在一个套接字上进行数据的接收与发送。
原型：INT32_T ipanel_porting_socket_shutdown(INT32_T sockfd, INT32_T what)
参数说明：
输入参数：
sockfd：套接字描述符
what：为IPANEL_DIS_RECEIVE表示禁止接收；为IPANEL_DIS_SEND表示禁止发送，为IPANEL_DIS_ALL表示同时禁止发送和接收。
输出参数:无
返    回：
IPANEL_OK:成功;
IPANEL_ERR:失败。
********************************************************************************************************/
INT32_T ipanel_porting_socket_shutdown(INT32_T sockfd, INT32_T what)
{
	int ret = IPANEL_ERR;
    INT32_T fd = socketMgr_get(sockfd);

    FD_AVAILAB_CHECK(fd);

    if ((sockfd >= 0) && ((what >= 0) && (what <= 2)))
    {
        ret = shutdown(fd, what);
    }
    if (ret != IPANEL_OK)
    {
        ret = IPANEL_ERR;
    }
    return ret;
}

/********************************************************************************************************
功能：关闭指定的套接字
原型：INT32_T ipanel_porting_socket_close(INT32_T sockfd)
参数说明：
输入参数：
sockfd：套接字描述符
输出参数:无
返    回：
IPANEL_OK:成功;
IPANEL_ERR:失败。
********************************************************************************************************/
INT32_T ipanel_porting_socket_close(INT32_T sockfd)
{
	int ret = IPANEL_ERR;
    int fd = socketMgr_get(sockfd);

    FD_AVAILAB_CHECK(fd);

    socketMgr_close(sockfd);
    
    ret = close(fd);

    if (ret != IPANEL_OK)
    {
        ret = IPANEL_ERR;
    }
    return ret;
}

/********************************************************************************************************
功能：获取准备让iPanel MiddleWare同时处理socket的最大个数。注意：该数目建议在8个以上，16个以下，即[8~16]，
缺省为8。若iPanel MiddleWare只是浏览网页的话，8个socket就足够了，若iPanel MiddleWare在浏览网页的时候
同时又需要使用VOIP、VOD点播等应用就需要多个socket以提高速度，否则可能出现socket都已经使用，需要关闭
一些socket才能运行某个应用的情况.
原型：ipanel_porting_socket_get_max_num(VOID)
参数说明：
输入参数:无
输出参数:无
返    回：
>0:返回iPanel MiddleWare可创建的最大socket数目;
IPANEL_ERR:失败。
********************************************************************************************************/
INT32_T ipanel_porting_socket_get_max_num(VOID)
{
    return MAX_SOCKETS;
}

/*****************************************************************************/
/* get local IP on settop                                                    */
/* success: return  0       failed: return -1                                */
/*****************************************************************************/
int get_LocalIP(char *ip, char *macAddr)
{	
	register int fd, interface;
	struct ifreq ifr[MAXINTERFACES];
	struct ifconf ifc;
	
	if ((fd = socket(AF_INET, SOCK_DGRAM, 0)) < 0) {
		return -1;
	} 
	
	ifc.ifc_len = sizeof(ifr);         
	ifc.ifc_buf = (caddr_t) ifr;
	if (ioctl(fd, SIOCGIFCONF, (char *) &ifc) < 0) { 
		return -1; 
	}
	
	interface = ifc.ifc_len / sizeof (struct ifreq); 
	
	while (interface-- > 0) 
    { 
		
		if ((ioctl (fd, SIOCGIFFLAGS, (char *) &ifr[interface])) < 0) {
			continue; 
		}     
		
		if (ip && (ioctl (fd, SIOCGIFADDR, (char *) &ifr[interface]))==0) { 
			memcpy(ip,inet_ntoa(((struct sockaddr_in*)(&ifr[interface].ifr_addr))->sin_addr),15);
			close (fd);
			return 0;   
		} 
		if (macAddr && (ioctl (fd, SIOCGIFHWADDR, (char *) &ifr[interface]))==0) { 
			sprintf(macAddr, "%.2x-%.2x-%.2x-%.2x-%.2x-%.2x", 
				(unsigned char)ifr[interface].ifr_hwaddr.sa_data[0],
				(unsigned char)ifr[interface].ifr_hwaddr.sa_data[1],
				(unsigned char)ifr[interface].ifr_hwaddr.sa_data[2],
				(unsigned char)ifr[interface].ifr_hwaddr.sa_data[3],
				(unsigned char)ifr[interface].ifr_hwaddr.sa_data[4],
				(unsigned char)ifr[interface].ifr_hwaddr.sa_data[5]);
			close (fd);
			return 0;   
		} 
	}
	
	close (fd); 
	return -1;  
}


