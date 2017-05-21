/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
����:SOCKET����ʵ��
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
���ܣ�����һ���׽���
ԭ�ͣ�INT32_T ipanel_porting_socket_open(INT32_T family, INT32_T type, INT32_T protocol)
����˵����
���������
family��ָ��Ҫ�������׽���Э���塣IPANEL_AF_INET����ʾIPv4Э�飬IPANEL_AV_INET6��ʾIPv6Э�飬����ֵ����
type��ָ��Ҫ�������׽��ֵ����͡�IPANEL_SOCK_STREAM����ʾSOCK_STREAM��IPANEL_SOCK_DGRAM����ʾSOCK_DGRAM������ֵ����
protocol��ָ������Э�顣ͨ������ΪIPANEL_IPPROTO_IP����ʾ��typeƥ���Ĭ��Э�飬����ֵ����
�����������
��    �أ�
���׽��ֽ����ɹ����򷵻طǸ������������򷵻�IPANEL_ERR��
>=0: �׽��ֽ����ɹ����򷵻طǸ���������
IPANEL_ERR: �����׽���ʧ�ܡ�
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
���ܣ���ָ���Ķ˿ں����׽��ְ���һ��
ԭ�ͣ�INT32_T ipanel_porting_socket_bind(INT32_T sockfd, IPANEL_IPADDR* ipaddr, INT32_T   port)
����˵����
���������
sockfd���׽���������
ipaddr����������IP��ַ��IP�汾�����ṹ���ָ��
port���˿ں�
�����������
��    �أ�
IPANEL_OK:�ɹ�;
IPANEL_ERR:ʧ�ܡ�
********************************************************************************************************/
INT32_T ipanel_porting_socket_bind(INT32_T sockfd,
								   IPANEL_IPADDR* ipaddr,
								   INT32_T   port)
{
	int ret     = IPANEL_ERR;
    struct sockaddr_in serv_addr = {0} ;
    struct sockaddr_in6     dest = {0};      // IPv6��ַ�ṹ
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
        /* IPv6Э�� */
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
���ܣ���һ���׽���ת���ɼ����׽���
ԭ�ͣ�INT32_T ipanel_porting_socket_listen(INT32_T sockfd, INT32_T backlog)
����˵����
���������
sockfd���׽���������
backlog�����ն��е���󳤶�
�����������
��    �أ�
IPANEL_OK:�ɹ�;
IPANEL_ERR:ʧ�ܡ�
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
���ܣ���Ӧ�������󣬽������Ӳ�����һ���µ�socket������������������
ԭ�ͣ�INT32_T ipanel_porting_socket_accept(INT32_T sockfd, IPANEL_IPADDR* ipaddr, INT32_T*   port)
����˵����
���������
sockfd�������׽���������
ipaddr������IP�汾�����Ľṹ��ָ��
�����������
ipaddr���������ӶԷ�IP��ַ�Ľṹ��ָ��
port���������ӶԷ��Ķ˿ں�
��    �أ�
>=0:ִ�гɹ��򷵻طǸ���������
IPANEL_ERR:ʧ�ܡ�
********************************************************************************************************/
INT32_T ipanel_porting_socket_accept(INT32_T sockfd,
									 IPANEL_IPADDR* ipaddr,
									 INT32_T*   port)
{
	int ret     = IPANEL_ERR;
	struct sockaddr_in serv_addr = {0};
    struct sockaddr_in6     dest = {0};      // IPv6��ַ�ṹ 
    
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
        /* IPv6Э�� */
        int len = sizeof(dest);
        UINT16_T temp_ip = 0;
        int i = 0,j = 0;
        
		memset(&dest, 0, sizeof(dest));
        
        ret = accept(fd, (struct sockaddr*)&dest, &len);
        if (ret >= 0)
        {
            //���������ip��ַ���˿�
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
���ܣ����׽�����ָ�����׽�����������
ԭ�ͣ�INT32_T ipanel_porting_socket_connect(INT32_T sockfd, IPANEL_IPADDR *ipaddr, INT32_T   port)
����˵����
���������
sockfd���׽���������
ipaddr������������IP��ַ��IP�汾�����Ľṹ��ָ��
port�������ӵĶ˿ں�
�����������
��    �أ�
IPANEL_OK:�ɹ�;
IPANEL_ERR:ʧ�ܡ�
********************************************************************************************************/
INT32_T ipanel_porting_socket_connect(INT32_T sockfd,
									  IPANEL_IPADDR *ipaddr,
									  INT32_T   port)
{
	int ret = IPANEL_ERR;
    struct sockaddr_in serv_addr = {0};
    struct sockaddr_in6     dest = {0};      // IPv6��ַ�ṹ
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
        /* IPv6Э�� */
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
���ܣ��������ݵ��׽���
ԭ�ͣ�INT32_T ipanel_porting_socket_send(INT32_T sockfd, CHAR_T   *buf, INT32_T buflen, INT32_T  flags)
����˵����
���������
sockfd���׽���������
buf�����������ݵĻ�����
len�������������ݵĳ���
flags������ѡ�һ����Ϊ0������ֵ����
�����������
��    �أ�
>0:ִ�гɹ�����ʵ�ʷ��͵����ݳ���;
IPANEL_ERR:ʧ�ܡ�
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
���ܣ��������ݵ�ָ���׽���
ԭ�ͣ�INT32_T ipanel_porting_socket_sendto(INT32_T sockfd, CHAR_T*    buf,
INT32_T buflen, INT32_T  flags, IPANEL_IPADDR* ipaddr, INT32_T   port)
����˵����
���������
sockfd���׽���������
buf�����������ݵĻ�����
len�������������ݵĳ���
flags������ѡ�һ����Ϊ0������ֵ����
ipaddr������Ŀ��IP��ַ��IP�汾�����Ľṹ��ָ��
port��Ŀ�Ķ˿ں�
�����������
��    �أ�
>0:ִ�гɹ�����ʵ�ʷ��͵����ݳ���;
IPANEL_ERR:ʧ�ܡ�
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
    struct sockaddr_in6     dest = {0};      // IPv6��ַ�ṹ
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
        /* IPv6Э�� */
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
���ܣ���һ���׽��ֽ�������
ԭ�ͣ�INT32_T ipanel_porting_socket_recv(INT32_T sockfd, CHAR_T    *buf, INT32_T buflen, INT32_T  flags)
����˵����
���������
sockfd���׽���������
buf�����ڽ������ݵĻ�����
len���������ĳ���
flags������ѡ�һ����Ϊ0������ֵ����
�����������
��    �أ�
>0:ִ�гɹ�����ʵ�ʽ��յ����ݳ���;
IPANEL_ERR:ʧ�ܡ�
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
���ܣ���һ���׽��ֽ�������
ԭ�ͣ�INT32_T ipanel_porting_socket_recvfrom(INT32_T sockfd, CHAR_T*    buf,
INT32_T buflen, INT32_T  flags, IPANEL_IPADDR* ipaddr, INT32_T*   port)
����˵����
���������
sockfd���׽���������
buf�����ڽ������ݵĻ�����
len���������ĳ���
flags������ѡ�һ����Ϊ0������ֵ����
ipaddr������Ŀ��IP��ַ��IP�汾�����Ľṹ��ָ��
port��Ŀ�Ķ˿ں�
�������:
ipaddr�����淢�ͷ�IP��ַ�Ľṹ��ָ��
port�����淢�ͷ��Ķ˿ں�
��    �أ�
>0:ִ�гɹ�����ʵ�ʽ��յ����ݳ���;
IPANEL_ERR:ʧ�ܡ�
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
    struct sockaddr_in6     dest = {0};      // IPv6��ַ�ṹ
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
             /* IPv6Э�� */
            int len = sizeof(dest);
            UINT16_T temp_ip = 0;
             
			memset(&dest, 0, sizeof(dest));
            ret = recvfrom(fd, buf, buflen, flags, (struct sockaddr*)&dest, &len);
            if (ret > 0)
            {
                //���������ip��ַ���˿�
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
���ܣ���ȡ���׽����йصı���Э���ַ����������ַ
ԭ�ͣ�INT32_T ipanel_porting_socket_getsockname(INT32_T sockfd, IPANEL_IPADDR* ipaddr, INT32_T*   port)
����˵����
���������
sockfd���׽���������
ipaddr������IP�汾�����Ľṹ��ָ��
�������:
ipaddr����������Э��IP��ַ�Ľṹ��ָ��
port����������Э��Ķ˿ں�
��    �أ�
IPANEL_OK:�ɹ�;
IPANEL_ERR:ʧ�ܡ�
********************************************************************************************************/
INT32_T ipanel_porting_socket_getsockname(INT32_T sockfd,
										  IPANEL_IPADDR* ipaddr,
										  INT32_T*   port)
{
	int ret = IPANEL_ERR;
    struct sockaddr_in serv_addr = {0};
    struct sockaddr_in6     dest = {0};      // IPv6��ַ�ṹ
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
        /* IPv6Э�� */
        int len = sizeof(dest);
        UINT16_T temp_ip = 0;
        
		memset(&dest, 0, sizeof(dest));
        
        ret = getsockname(fd, (struct sockaddr*)&dest, &len);
        if (ret >= 0)
        {
            //���������ip��ַ���˿�
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
���ܣ�ȷ��һ�������׽��ֵ�״̬���ж��׽������Ƿ������ݣ������ܷ���һ���׽���д�����ݡ�
ԭ�ͣ�INT32_T ipanel_porting_socket_select(INT32_T nfds, IPANEL_FD_SET_S*   readset,
IPANEL_FD_SET_S*  writeset, IPANEL_FD_SET_S* exceptset, INT32_T   timeout)
����˵����
���������
nfds��    ��Ҫ�����ļ���������������ֵӦ�ñ�readset��writeset��exceptset����������󣬶�����ʵ�ʵ��ļ�����������
readset�� �������ɶ��Ե�һ���ļ�������
writeset����������д�Ե�һ���ļ�������
exceptset�������������״̬���ļ���������������������״̬
timeout�� ����0��ʾ�ȴ����ٺ��룻ΪIPANEL_NO_WAIT(0)ʱ��ʾ���ȴ��������أ�ΪIPANEL_WAIT_FOREVER(-1)��ʾ���õȴ���
�������: ��
��    �أ�
��Ӧ�����Ķ�Ӧ�����ļ�����������������readset��writeset��exceptset�������ݾ���ǡ��λ�ñ��޸ģ�
IPANEL_OK:Ҫ��ѯ���ļ��������׼����;
IPANEL_ERR:ʧ�ܣ��ȴ���ʱ�����
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
���ܣ��ı��׽�������Ϊ��������ע���˺���ֻ�ṩ���׽�����������Ϊ�����������ṩ���׽�����������Ϊ�����Ĺ���
ԭ�ͣ�INT32_T ipanel_porting_socket_ioctl(INT32_T sockfd, INT32_T cmd, INT32_T arg)
����˵����
���������
sockfd���׽���������
cmd���������ͣ�ΪIPANEL_FIONBIO
arg�����������1��ʾ������
�������:��
��    �أ�
IPANEL_OK:�ɹ�;
IPANEL_ERR:ʧ�ܡ�
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
����˵����
��ȡ�׽��ֵ����ԡ��ɻ�ȡ�׽��ֵ����Լ��±���get��ΪY�������
����˵����
���������
sockfd�� �׽���������
level�� ѡ���Ĳ��
optname����Ҫ��ȡ��������
+---------------------+-------------------------+-------+-----+---------------+
|  level              |   optname               |  get  | set | ��������      |
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
���������
optval��ָ�򱣴�����ֵ�Ļ�����
optlen��ָ�򱣴�����ֵ�ĳ���
��    �أ�
IPANEL_OK:�ɹ�
IPANEL_ERR:ʧ�ܡ�

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
����˵����
�����׽��ֵ����ԡ������õ��׽��ֵ����Լ��±���set��ΪY�������
����˵����
���������
sockfd���׽���������
level�� ѡ���Ĳ��
optname����Ҫ���õ�������
optval��ָ�򱣴�����ֵ�Ļ�����
optlen�� optval�������ĳ���

 +---------------------+-------------------------+-------+-----+---------------+
 |  level              |   optname               |  get  | set | ��������      |
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
 �����������
 ��    �أ�
 IPANEL_OK:�ɹ���
 IPANEL_ERR:ʧ�ܡ�
 
  
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
            /*����LOOPΪ0,�����ձ����������鲥����*/
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

        //����ip��ַʹ��Ĭ�ϵ�ַ
        mreq.ipv6mr_interface = 0;

							
        if(IPANEL_IP_ADD_MEMBERSHIP == optname)
		{
            /*����LOOPΪ0,�����ձ����������鲥����*/
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
���ܣ���ֹ��һ���׽����Ͻ������ݵĽ����뷢�͡�
ԭ�ͣ�INT32_T ipanel_porting_socket_shutdown(INT32_T sockfd, INT32_T what)
����˵����
���������
sockfd���׽���������
what��ΪIPANEL_DIS_RECEIVE��ʾ��ֹ���գ�ΪIPANEL_DIS_SEND��ʾ��ֹ���ͣ�ΪIPANEL_DIS_ALL��ʾͬʱ��ֹ���ͺͽ��ա�
�������:��
��    �أ�
IPANEL_OK:�ɹ�;
IPANEL_ERR:ʧ�ܡ�
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
���ܣ��ر�ָ�����׽���
ԭ�ͣ�INT32_T ipanel_porting_socket_close(INT32_T sockfd)
����˵����
���������
sockfd���׽���������
�������:��
��    �أ�
IPANEL_OK:�ɹ�;
IPANEL_ERR:ʧ�ܡ�
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
���ܣ���ȡ׼����iPanel MiddleWareͬʱ����socket����������ע�⣺����Ŀ������8�����ϣ�16�����£���[8~16]��
ȱʡΪ8����iPanel MiddleWareֻ�������ҳ�Ļ���8��socket���㹻�ˣ���iPanel MiddleWare�������ҳ��ʱ��
ͬʱ����Ҫʹ��VOIP��VOD�㲥��Ӧ�þ���Ҫ���socket������ٶȣ�������ܳ���socket���Ѿ�ʹ�ã���Ҫ�ر�
һЩsocket��������ĳ��Ӧ�õ����.
ԭ�ͣ�ipanel_porting_socket_get_max_num(VOID)
����˵����
�������:��
�������:��
��    �أ�
>0:����iPanel MiddleWare�ɴ��������socket��Ŀ;
IPANEL_ERR:ʧ�ܡ�
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


