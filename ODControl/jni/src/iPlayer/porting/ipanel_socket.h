/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:SOCKET管理
*********************************************************************/

#ifndef _IPANEL_MIDDLEWARE_PORTING_SOCKET_API_FUNCTOTYPE_H_
#define _IPANEL_MIDDLEWARE_PORTING_SOCKET_API_FUNCTOTYPE_H_

#include "ipanel_porting.h"

#ifdef __cplusplus
extern "C" {
#endif

#ifndef IPANEL_INADDR_NONE
#define IPANEL_INADDR_NONE		  (-1)
#endif
	
#define IPANEL_AF_INET            (2)
#define IPANEL_AF_INET6           (23)
	
#define IPANEL_SOCK_STREAM        (1)
#define IPANEL_SOCK_DGRAM         (2)
#define IPANEL_XSOCK_STREAM		  (3)
#define IPANEL_XSOCK_DGRAM		  (4)

#define IPANEL_IPPROTO_IP         (0)
#define IPANEL_IPPROTO_IPV6       (1)
#define IPANEL_SOL_SOCKET         (0xFFFF)
#define IPANEL_SO_REUSEADDR       (0x0004)
#define IPANEL_SO_KEEPALIVE       (0x0008)
#define IPANEL_SO_BROADCAST       (0x0020)
#define IPANEL_SO_LINGER	      (0x0080)
#define IPANEL_SO_SNDBUF		  (0x1001)
#define IPANEL_SO_RCVBUF		  (0x1002)
#define IPANEL_SO_RCVTIMEO		  (0x1006)
#define IPANEL_SO_ERROR			  (0x1007)
#define IPANEL_INADDR_ANY         (0x00000000)

#define IPANEL_CUS_RCVBUFFER	  (0x1500)
#define IPANEL_IP_ADD_MEMBERSHIP  (12)
#define IPANEL_IP_DROP_MEMBERSHIP (13)

#define IPANEL_FIONBIO            (16)
                                  
#define IPANEL_DIS_RECEIVE        (0)     /* receives disallowed */
#define IPANEL_DIS_SEND           (1)     /* sends disallowed */
#define IPANEL_DIS_ALL            (2)     /* sends and receives disallowed */
                                  
typedef UINT32_T                  IPANEL_FD_MASK;
#define IPANEL_NFDBITS            (8 * sizeof(IPANEL_FD_MASK))
#define IPANEL_FD_SET_SIZE        (16)

#define IPANEL_FD_SET(n, p)       ((p)->fds_bits[(n) / IPANEL_NFDBITS] |=  (1 << ((n) % IPANEL_NFDBITS)))
#define IPANEL_FD_CLR(n, p)       ((p)->fds_bits[(n) / IPANEL_NFDBITS] &= ~(1 << ((n) % IPANEL_NFDBITS)))
#define IPANEL_FD_ISSET(n, p)     ((p)->fds_bits[(n) / IPANEL_NFDBITS] &   (1 << ((n) % IPANEL_NFDBITS)))
#define IPANEL_FD_ZERO(p)         do { \
                                      INT32_T _iii_ = 0; \
                                      for (; _iii_ < IPANEL_FD_SET_SIZE; _iii_++) { \
                                          (p)->fds_bits[_iii_] = 0; \
                                      } \
                                  } while (0)

/* 从ipaddr.h merge而来. by Dzp 2010-12-29 */
/* version definitions for IPANEL_IPADDR */
#define IPANEL_IP_VERSION_4       ((BYTE_T)4)
#define IPANEL_IP_VERSION_6       ((BYTE_T)6)

/* raw IP data definitions */
typedef UINT32_T    IPANEL_IPV4;
typedef struct
{
	UINT16_T ip[8];
} IPANEL_IPV6;

/* basic IP struct definition(HAVE_IPV6_SUPPORT is always defined) */
typedef struct
{
	BYTE_T  version;
	BYTE_T  padding1;
	BYTE_T  padding2;
	BYTE_T  padding3;
	union {
		IPANEL_IPV4 ipv4;
		IPANEL_IPV6 ipv6;
	} addr;
} IPAddr0;

/* IPANEL_IPADDR is for our own IPAddr type definition. */
typedef IPAddr0 IPANEL_IPADDR; 

/* macros for assigning raw IP data to an IPAddr0 struct pointer */	
#define IPAddr_setIPV4(ipaddr, ip4) \
			do{\
				IPAddr0 a={0};\
				*(ipaddr) = a;\
				(ipaddr)->version = IPANEL_IP_VERSION_4;\
				(ipaddr)->addr.ipv4 = (ip4);\
			} while(0)
#define IPAddr_setIPV6(ipaddr, p_ip6) \
			do{\
				IPAddr0 a={0};\
				*(ipaddr) = a;\
				(ipaddr)->version = IPANEL_IP_VERSION_6;\
				(ipaddr)->addr.ipv6 = *(IPANEL_IPV6*)(p_ip6);\
			} while(0)

#define IPAddr_getFamilyFromVersion(addr) \
    ((addr)->version==IPANEL_IP_VERSION_4 ? IPANEL_AF_INET : ((addr)->version==IPANEL_IP_VERSION_6 ? IPANEL_AF_INET6 : 0))

                               
typedef struct {
    IPANEL_FD_MASK fds_bits[IPANEL_FD_SET_SIZE];
} IPANEL_FD_SET_S;

typedef struct {
    UINT32_T addr;
} IPANEL_IN_ADDR;

typedef struct {
    IPANEL_IN_ADDR imr_multiaddr;
    IPANEL_IN_ADDR imr_interface;
} IPANEL_IP_MREQ;

typedef struct {
    IPANEL_IPADDR imr_multiaddr;
    IPANEL_IPADDR imr_interface;
} IPANEL_IPV6_MREQ;

typedef struct {
	UINT16_T l_onoff; 
	UINT16_T l_linger; 
} IPANEL_LINGER;

/* interfaces */
INT32_T ipanel_porting_socket_open(INT32_T family,
                                   INT32_T type,
                                   INT32_T protocol);

INT32_T ipanel_porting_socket_bind(INT32_T sockfd,
                                   IPANEL_IPADDR *ipaddr,
                                   INT32_T port);

INT32_T ipanel_porting_socket_listen(INT32_T sockfd, INT32_T backlog);

INT32_T ipanel_porting_socket_accept(INT32_T sockfd,
                                     IPANEL_IPADDR *ipaddr,
                                     INT32_T *port);

INT32_T ipanel_porting_socket_connect(INT32_T sockfd,
                                      IPANEL_IPADDR *ipaddr,
                                      INT32_T port);

INT32_T ipanel_porting_socket_send(INT32_T sockfd,
                                   CHAR_T *buf,
                                   INT32_T buflen,
                                   INT32_T flags);

INT32_T ipanel_porting_socket_sendto(INT32_T sockfd,
                                     CHAR_T *buf,
                                     INT32_T buflen,
                                     INT32_T flags,
                                     IPANEL_IPADDR *ipaddr,
                                     INT32_T port);

INT32_T ipanel_porting_socket_recv(INT32_T sockfd,
                                   CHAR_T *buf,
                                   INT32_T buflen,
                                   INT32_T flags);

INT32_T ipanel_porting_socket_recvfrom(INT32_T sockfd,
                                       CHAR_T *buf,
                                       INT32_T buflen,
                                       INT32_T flags,
                                       IPANEL_IPADDR *ipaddr,
                                       INT32_T *port);

INT32_T ipanel_porting_socket_getsockname(INT32_T sockfd,
                                          IPANEL_IPADDR *ipaddr,
                                          INT32_T *port);

INT32_T ipanel_porting_socket_select(INT32_T nfds,
                                     IPANEL_FD_SET_S* readset,
                                     IPANEL_FD_SET_S* writeset,
                                     IPANEL_FD_SET_S* exceptset,
                                     INT32_T timeout);

INT32_T ipanel_porting_socket_getsockopt(INT32_T sockfd,
                                         INT32_T level,
                                         INT32_T optname,
                                         VOID *optval,
                                         INT32_T *optlen);

INT32_T ipanel_porting_socket_setsockopt(INT32_T sockfd,
                                         INT32_T level,
                                         INT32_T optname,
                                         VOID *optval,
                                         INT32_T optlen);

INT32_T ipanel_porting_socket_ioctl(INT32_T sockfd, INT32_T cmd, INT32_T arg);

INT32_T ipanel_porting_socket_shutdown(INT32_T sockfd, INT32_T what);

INT32_T ipanel_porting_socket_close(INT32_T sockfd);

INT32_T ipanel_porting_socket_get_max_num(VOID);

#ifdef __cplusplus
}
#endif

#endif//_IPANEL_MIDDLEWARE_PORTING_SOCKET_API_FUNCTOTYPE_H_

