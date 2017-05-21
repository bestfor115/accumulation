/******************************************************************************/
/*    Copyright (c) 2005 iPanel Technologies, Ltd.                     */
/*    All rights reserved. You are not allowed to copy or distribute          */
/*    the code without permission.                                            */
/*    This is the demo implenment of the Porting APIs needed by iPanel        */
/*    MiddleWare.                                                             */
/*    Maybe you should modify it accorrding to Platform.                      */
/*                                                                            */
/*    $author huzh 2007/11/20                                                 */
/******************************************************************************/

#ifndef _IPANEL_MIDDLEWARE_PORTING_NETWORK_API_FUNCTOTYPE_H_
#define _IPANEL_MIDDLEWARE_PORTING_NETWORK_API_FUNCTOTYPE_H_

#include "ipanel_porting.h"

#ifdef __cplusplus
extern "C" {
#endif

/* network relative */
typedef enum
{
    IPANEL_NETWORK_DEVICE_LAN          	= 1,  // LAN
    IPANEL_NETWORK_DEVICE_DIALUP		= 2,  // Dialup
    IPANEL_NETWORK_DEVICE_PPPOE			= 3,  // PPPoE
    IPANEL_NETWORK_DEVICE_CABLEMODEM  	= 4,  // CableModem
    IPANEL_NETWORK_DEVICE_GPRS          = 5,  // GPRS
	IPANEL_NETWORK_DEVICE_3G            = 6,  // 3G
	IPANEL_NETWORK_DEVICE_WIFI_AP       = 7,  // WIFI AP
	IPANEL_NETWORK_DHCP_SERVER          = 8,  // DHCP Server
	IPANEL_NETWORK_DEVICE_WIFI_DONGLE   = 9,  // WIFI DONGLE
	IPANEL_NETWORK_DEVICE_ROUTING       = 10  // Routing
} IPANEL_NETWORK_DEVICE_e;

/* 'op' for ipanel_porting_network_ioctl() */
typedef enum
{
    IPANEL_NETWORK_CONNECT          	= 1,
    IPANEL_NETWORK_DISCONNECT       	= 2,
    IPANEL_NETWORK_SET_IFCONFIG     	= 3,
    IPANEL_NETWORK_GET_IFCONFIG     	= 4,
    IPANEL_NETWORK_GET_STATUS       	= 5,
    IPANEL_NETWORK_SET_STREAM_HOOK  	= 6,
    IPANEL_NETWORK_DEL_STREAM_HOOK  	= 7,
    IPANEL_NETWORK_SET_USER_NAME    	= 8,  
    IPANEL_NETWORK_SET_PWD          	= 9,
    IPANEL_NETWORK_SET_DIALSTRING   	= 10,
    IPANEL_NETWORK_SET_DNS_CONFIG   	= 11,
    IPANEL_NETWORK_GET_DNS_CONFIG   	= 12,
    IPANEL_NETWORK_SET_NIC_MODE  		= 13,
    IPANEL_NETWORK_GET_NIC_MODE  		= 14,
    IPANEL_NETWORK_SET_NIC_BUFFER_SIZE 	= 15,
    IPANEL_NETWORK_GET_NIC_BUFFER_SIZE 	= 16,
    IPANEL_NETWORK_RENEW_IP 			= 17,
    IPANEL_NETWORK_GET_MAC 				= 18,
    IPANEL_NETWORK_GET_NIC_SEND_PACKETS = 19,
    IPANEL_NETWORK_GET_NIC_REVD_PACKETS = 20,
    IPANEL_NETWORK_SEND_PING_REQ 		= 21,
    IPANEL_NETWORK_STOP_PING_REQ 		= 22,
	IPANEL_NETWORK_GET_UPLINKFREQ       = 23,	/* 获取Cable Modem的上行频率参数*/
	IPANEL_NETWORK_GET_DOWNLINKFREQ     = 24,	/* 获取Cable Modem的下行频率参数*/
	IPANEL_NETWORK_SET_DOWNLINKFREQ		= 25,	/* 设置Cable Modem的下行频率参数*/
	IPANEL_NETWORK_GET_CM_DEVICE_INFO	= 26,   /* 获取机顶盒是否存在CM设备及物理连接状态*/    
    IPANEL_NETWORK_GET_DOWNLINK_STATUS  = 27,
    IPANEL_NETWORK_GET_UPLINK_STATUS    = 28,
    IPANEL_NETWORK_GET_SIGNAL_STRENGTH  = 29,
    IPANEL_NETWORK_GET_APN              = 30,
    IPANEL_NETWORK_SET_APN              = 31,
    IPANEL_NETWORK_DTU_REBOOT           = 32,
    IPANEL_NETWORK_SET_PDP_INACTIVE_TIMEOUT = 33,
	IPANEL_NETWORK_SET_DHCP_OPTION      = 34,
	IPANEL_NETWORK_CM_REGISTRATIONSTATUS = 35,
	IPANEL_NETWORK_GET_DOWNLINKELEVEL 	= 36,
	IPANEL_NETWORK_GET_UPLINKELEVEL 	= 37,
	IPANEL_NETWORK_GET_PROVISION_STATUS = 38,
	IPANEL_NETWORK_GET_CM_VERSION		= 39,	/* 获取cable modem的版本号 */	
	IPANEL_NETWORK_SET_WIFI_AP_OPTION   = 40,   // WIFI AP
	IPANEL_NETWORK_SET_DHCP_SERVER_OPTION = 41,   // DHCP Server 	
	IPANEL_NETWORK_GET_DHCP_SERVER_INFO = 42,
	IPANEL_NETWORK_GET_PHONE_NUM     	= 43,
	IPANEL_NETWORK_GET_3G_INFO          = 44,
	IPANEL_NETWORK_GET_WIFI_AP_NUMBER   = 45,
	IPANEL_NETWORK_GET_WIFI_AP_INFO     = 46,
	IPANEL_NETWORK_SET_WIFI_DONGLE_OPTION = 47,
    IPANEL_NETWORK_WIFI_DONGLE_SCAN       = 48,
    IPANEL_NETWORK_GET_NIC_SEND_BYTES     = 49, /*发送IP数据字节数*/
    IPANEL_NETWORK_GET_NIC_REVD_BYTES     = 50, /*接收IP数据字节数*/
    IPANEL_NETWORK_GET_PING_INFO          = 51, /*获取ping功能相关信息*/
    IPANEL_NETWORK_SET_PING_PARAM         = 52, /*设置ping功能相关参数*/
    IPANEL_NETWORK_GET_ROUTE_TRACE_INFO   = 53, /*获取trace相关信息*/
    IPANEL_NETWORK_SET_ROUTE_TRACE_PARAM  = 54, /*设置trace相关参数*/
	IPANEL_NETWORK_GET_DHCP_OPTION		  = 55, /* 获取DHCP相关参数信息 */
	IPANEL_NETWORK_GET_PING_RESPONSE      = 56, /* 获取ping包响应信息 */
	IPANEL_NETWORK_SET_DHCPCONFIG		  = 57,  /* 设置option的值，默认为151，广东省网定义 */
	IPANEL_NETWORK_GET_BANDWIDTH		  = 58,
	IPANEL_NETWORK_GET_ROUTING_INFO       = 59,   /*获取路由相关属性信息*/
	IPANEL_NETWORK_ROUTING_SET_MACADDR_PERMISSION = 60,  /*设置路由MAC控制权限*/ 	
	IPANEL_NETWORK_GET_ROUTING_MAC_ADDRESS        = 61,  /*获取路由的MAC地址*/
	IPANEL_NETWORK_ROUTING_DEL_MAC_ADDRESS        = 62,  /*删除MAC控制的地址*/
	IPANEL_NETWORK_ROUTING_ADD_MAC_ADDRESS        = 63,  /*添加MAC控制的地址*/
	IPANEL_NETWORK_ROUTING_RESTORE        = 64,   /*恢复路由缺省设置*/
	IPANEL_NETWORK_GET_ROUTING_DIAGNOSTIC_STATUS  = 65,   /*诊断路由工作状态*/
	IPANEL_NETWORK_GET_UPLINK_BAND        = 66 ,	/*获取cable modem的上行通道带宽，以Mbps为单位的整数值 */
	IPANEL_NETWORK_GET_CURRWIFI_AP_INFO =	67,  	//获取当前WiFi网络信息
	IPANEL_NETWORK_UNDEFINED  	

} IPANEL_NETWORK_IOCTL_e;

typedef VOID (*IPANEL_NETWORK_STREAM_HOOK)(IPANEL_XMEMBLK *pmemblk); // IPANEL_NETWORK_SET_STREAM_HOOK

typedef struct
{
	UINT32_T							ipv4addr;	//接收端ip地址
	UINT32_T							port;		//接收端ip端口号
	IPANEL_NETWORK_STREAM_HOOK			func;
}IPANEL_NETWORK_STREAM_HOOK_PARA;

typedef enum										//IPANEL_NETWORK_SET_DNS_FROM
{
    IPANEL_NETWORK_DNS_FROM_SERVER		= 1,
    IPANEL_NETWORK_DNS_FROM_USER		= 2
}IPANEL_NETWORK_DNS_MODE_e;

typedef enum										//IPANEL_NETWORK_CONNECT
{
    IPANEL_NETWORK_LAN_ASSIGN_IP 		= 1,
    IPANEL_NETWORK_LAN_DHCP				= 2      
} IPANEL_NETWORK_LAN_MODE_e;

typedef enum	// IPANEL_NETWORK_GET_CM_DEVICE_INFO
{ 
	IPANEL_NETWORK_CM_NOEXIST			=  0,  /*表示不存在内置CM*/
	IPANEL_NETWORK_CM_DISCONNECTED		=  1,  /*表示有内置CM，但物理上未连接*/
	IPANEL_NETWORK_CM_CONNECTED			=  2   /*表示有内置CM，且物理上已连接*/
}IPANEL_NETWORK_CM_DEVICE_e;

typedef struct										//IPANEL_NETWORK_SET_IFCONFIG & IPANEL_NETWORK_GET_IFCONFIG
{
    UINT32_T							ipaddr;
    UINT32_T							netmask;
    UINT32_T							gateway;
} IPANEL_NETWORK_IF_PARAM;

typedef enum										//IPANEL_NETWORK_GET_STATUS
{
    IPANEL_NETWORK_IF_CONNECTED  		= 1,
    IPANEL_NETWORK_IF_CONNECTING		= 2,
    IPANEL_NETWORK_IF_DISCONNECTED 		= 3,
	IPANEL_NETWORK_LINK_INVALID			= 4
}IPANEL_NETWORK_IF_STATUS_e;

typedef enum 
{
    IPANEL_NETWORK_NIC_CONFIG_UNKNOW 	= 0,
    IPANEL_NETWORK_AUTO_CONFIG 			= 1,
    IPANEL_NETWORK_10BASE_HALF_DUMPLEX 	= 2,
    IPANEL_NETWORK_10BASE_FULL_DUMPLEX 	= 3,
    IPANEL_NETWORK_100BASE_HALF_DUMPLEX = 4,
    IPANEL_NETWORK_100BASE_FULL_DUMPLEX = 5
}IPANEL_NETWORK_NIC_MODE_e;

//DHCP SERVER设备状态参数数据结构
typedef struct		// (IPANEL_NETWORK_SET_DHCP_SERVER_OPTION | IPANEL_NETWORK_CONNECT) || IPANEL_NETWORK_GET_DHCP_SERVER_INFO
{
    UINT32_T		ipaddr_start;
    UINT32_T		ipaddr_end;
    UINT32_T		gateway;
} IPANEL_NETWORK_DHCP_SERVER_INFO;

//WIFI网络要求密钥使用的网络验证模式
typedef enum
{
	IPANEL_WIFI_AUTH_AUTO      = 0, /*AUTO*/		
	IPANEL_WIFI_AUTH_OPEN      = 1, /*开发式*/
	IPANEL_WIFI_AUTH_SHARED    = 2, /*共享式*/
	IPANEL_WIFI_AUTH_WPA       = 3, /*WPA*/
	IPANEL_WIFI_AUTH_WPAPSK    = 4, /*WPA-PSK*/
	IPANEL_WIFI_AUTH_WPA2      = 5, /*WPA2*/
	IPANEL_WIFI_AUTH_WPA2PSK   = 6 	/*WPA2-PSK*/	
}IPANEL_NETWORK_WIFI_AUTH_MODE;

//WIFI网络要求密钥使用的数据加密算法
typedef enum
{
	IPANEL_WIFI_ENCRYP_AUTO    = 0, /*AUTO*/		
	IPANEL_WIFI_ENCRYP_NONE    = 1, /*无加密，适用于开发式的网络验证模式*/
	IPANEL_WIFI_ENCRYP_WEP     = 2, /*WEB,适用于开发式和共享式的网络验证模式*/
	IPANEL_WIFI_ENCRYP_TKIP    = 3, /*TKIP,适用于WPA和WPA-PSK网络验证模式*/
	IPANEL_WIFI_ENCRYP_AES     = 4  /*AES,适用于WPA和WPA-PSK网络验证模式*/
}IPANEL_NETWORK_WIFI_ENCRYP_TYPE;

//WIFI AP设备参数控制数据结构
typedef struct		// IPANEL_NETWORK_CONNECT
{
    CHAR_T							ssid[36]; 	// SSID描述符字符串
    CHAR_T							key[36];  	// 登录密码字符串
    UINT32_T						gateway;
    IPANEL_NETWORK_WIFI_AUTH_MODE 	auth_mode; /* 网络验证模式 */
	IPANEL_NETWORK_WIFI_ENCRYP_TYPE encryp_type; /* 数据加密算法 */
    UINT32_T 						channel; 	// 无线网络使用的信道,取值范围:1~11
} IPANEL_NETWORK_WIFI_AP_PARAM;

//WIFI AP设备状态参数数据结构
typedef struct		// IPANEL_NETWORK_GET_WIFI_AP_CONINFO
{
    CHAR_T							ssid[36]; 	// SSID描述符字符串
    CHAR_T							key[36];  	// 登录密码字符串
    UINT32_T						level;		// 0~100, 0:无信号,100:信号最强
    IPANEL_NETWORK_WIFI_AUTH_MODE 	auth_mode; /* 网络验证模式 */
    IPANEL_NETWORK_WIFI_ENCRYP_TYPE encryp_type; /* 数据加密算法 */
    UINT32_T 						channel; 	// 无线网络使用的信道,取值范围:1~11
} IPANEL_NETWORK_WIFI_AP_INFO;

/* 获取WIFI DONGLE 搜索区域中的可用无线网络 */
typedef struct { 
	CHAR_T ssid[64]; /* 网络名，最大长度32个字符 */
	UINT32_T channel; /* 信道标识符，IEEE 802.11b/g划分14个信道，802.11a/n使用更多信道 */
	UINT32_T level; /* 0~100, 0:无信号,100:信号最强 */
	UINT32_T encryption; /* 0:无加密；1:启用了加密 */
	UINT32_T bit_rates; /* 速度，单位Mbps */
	IPANEL_NETWORK_WIFI_AUTH_MODE    auth_mode; /* 网络验证模式 */      
	IPANEL_NETWORK_WIFI_ENCRYP_TYPE encryp_type; /* 数据加密算法 */
	UINT32_T 						network_type; /* 0:infrastructure mode; 1:adhoc mode */
	CHAR_T  MACAddr[32]; /* 网络路由的mac地址，最大长度32个字符*/ 
}IPANEL_NETWORK_WIFI_SCAN_INFO;


/*获取当前已经连接上网络的WiFi对象网络信息*/
typedef struct { 
       CHAR_T ssid[64]; /* 网络名，最大长度64个字符 */
       UINT32_T channel; /* 信道标识符，IEEE 802.11b/g划分14个信道，802.11a/n使用更多信道 */
       UINT32_T level; /* 0~100, 0:无信号,100:信号最强 */
       UINT32_T encryption; /* 0:无加密；1:启用了加密 */
       UINT32_T bit_rates; /* 速度，单位Mbps */
       IPANEL_NETWORK_WIFI_AUTH_MODE  auth_mode; /* 网络验证模式 */
       IPANEL_NETWORK_WIFI_ENCRYP_TYPE encryp_type; /* 数据加密算法 */
       UINT32_T 						network_type; /* 0:infrastructure mode; 1:adhoc mode */
       CHAR_T   MACAddr[32]; /* 网络路由的mac地址，最大长度32个字符*/
       UINT32_T  key_type; /* 0:十六进制；1:ASSIC 密码类型 */
       CHAR_T   key[64];   // 登录密码字符串
}IPANEL_NETWORK_CURRWIFI_INFO;


/* 无线网络配置参数 */
typedef struct
{
	CHAR_T 							ssid[64]; /* 网络名，最大长度32个字符 */
	UINT32_T 						network_type; /* 0:infrastructure mode; 1:adhoc mode */
	IPANEL_NETWORK_WIFI_AUTH_MODE 	auth_mode; /* 网络验证模式 */
	IPANEL_NETWORK_WIFI_ENCRYP_TYPE    encryp_type; /* 数据加密算法 */
	UINT32_T 						key_type; /* 0:十六进制；1:ASSIC */
	CHAR_T 							key_str[64]; /* 10 or 26 characters (key_type=0); 5 or 13 characters? (key_type=1) */
	UINT32_T							encryption; /* 0:无加密；1:启用了加密 */
	CHAR_T        					        MACAddr[32]; /* 网络路由的mac地址，最大长度32个字符*/ 
}IPANEL_NETWORK_WIFI_DONGLE_OPTION;

typedef enum		//IPANEL_NETWORK_DEVICE_3G
{
    IPANEL_NETWORK_INVALID	= 1,	// 未知类型
	IPANEL_NETWORK_2G  		= 2,	// 3G网络
	IPANEL_NETWORK_3G		= 3		// 2G网络
}IPANEL_NETWORK_3G_TYPE_e;

/*3G设备状态参数数据结构*/
typedef struct		// IPANEL_NETWORK_GET_3G_INFO 
{
	UINT32_T					level;	// 0~100, 0:无信号(下线状态),100:信号最强
	IPANEL_NETWORK_3G_TYPE_e	type;	// 网络类型
	UINT32_T					up_speed; // 上行速率, 单位为bps(bits/second)    	UINT32_T	down_speed; // 下行速率,单位为bps(bits/second)
} IPANEL_NETWORK_3G_INFO;

/*3G设备来电号码数据结构*/
typedef struct		// IPANEL_NETWORK_GET_PHONE_NUM
{
    CHAR_T		num[30];	// 来电号码, 包含所有有效字符的字符串
} IPANEL_NETWORK_3G_PHONE_NUM;

typedef struct  //IPANEL_NETWORK_GET_PING_INFO 
{
    UINT32_T  successCount;     //ping成功的数据包个数
    UINT32_T  failureCount;     //ping失败的数据包个数
    UINT32_T  avgResponseTime ; // ping的平均响应时间(毫秒,ms)
    UINT32_T  minResponseTime ; // ping的最小响应时间(毫秒,ms)
    UINT32_T  maxResponseTime ; // ping的最大响应时间(毫秒,ms)
} IPANEL_NETWORK_PING_INFO;

typedef struct  // IPANEL_NETWORK_SET_PING_PARAM 
{
    CHAR_T    host[128];    // Ping的主机名或者IP地址
    UINT32_T  numberOfRepetitions;    //Ping诊断的次数
    UINT32_T  timeOut;      //超时时间(毫秒,ms)
    UINT32_T  dataBlockSize;    // Ping数据包的大小[1:65535]
    UINT32_T  DSCP;    //测试包中用于DiffServ的码点[0:63]，默认为0
} IPANEL_NETWORK_PING_PARAM ;

typedef struct  // IPANEL_NETWORK_GET_ROUTE_TRACE_INFO    
{
    UINT32_T  ResponseTime;    //响应时间，默认为0
    UINT32_T  numberOfRepetitions;    //路由的跳数，默认为0
    UINT32_T  hops;    //跳转总数
    UINT32_T  hopHostsBufLen; //Buffer长度
    CHAR_T*   hopHostsBuf;     //用分号分隔域名，路由过程中经过的主机名或IP地址
} IPANEL_NETWORK_ROUTE_TRACE_INFO;

typedef struct   // IPANEL_NETWORK_SET_ROUTE_TRACE_PARAM 
{
    CHAR_T    host[256];    //路由诊断的主机名或域名
    UINT32_T  timeOut;      //超时时间(毫秒,ms)
    UINT32_T  dataBlockSize;    // 路由诊断发送数据包的大小[1:65535]
    UINT32_T  maxHopCount;    // 数据包最大跳数[1:64],默认32
    UINT32_T  DSCP;   //测试包中用于DiffServ的码点[0:63]，默认为0
} IPANEL_NETWORK_ROUTE_TRACE_PARAM;

typedef struct    //IPANEL_NETWORK_GET_DHCP_OPTION
{	
	UINT32_T   address;  		//DHCP服务器IP地址 
	UINT16_T   port;     		//DHCP的端口号 
	UINT32_T   leaseObtained; 	//租用IP地址开始时间，类型： UTC时间
	UINT32_T   leaseExpires;   	//租用IP地址结束时间，类型： UTC时间
    CHAR_T	   private_data[256]; //获取dhcp option中的用户自定义数据
} IPANEL_NETWORK_DHCP_OPTION;

typedef struct{        		// IPANEL_NETWORK_GET_PING_RESPONSE	
  	UINT32_T      recv;   	//接收数据，单位bytes	
  	UINT32_T      ttl;  	//生存周期	
  	UINT32_T      address; 	//提供数据的IP地址 	
  	UINT32_T      time; 	//ping操作所用的时间，单位ms
} IPANEL_NETWORK_PING_RESPONSE; 


typedef struct
{
	UINT32_T   avgBandWidth;    //1分总内的平均带宽(单位mbps)
	UINT32_T   curBandWidth;     //实时带宽(单位mbps)
}IPANEL_NETWORK_BANDWIDTH;

//获取路由设备相关属性信息
typedef struct		// IPANEL_NETWORK_GET_ROUTING_INFO=59
{
	UINT32_T       DHCPEnable;       //DHCP服务，1启动，0关闭 
	UINT32_T       MACAddrEnable;    //MAC控制，1表示允许，0表示拒绝
}IPANEL_NETWORK_ROUTING_INFO;

INT32_T ipanel_porting_network_ioctl(IPANEL_NETWORK_DEVICE_e device, IPANEL_NETWORK_IOCTL_e op, VOID *arg);

#ifdef __cplusplus
}
#endif

#endif // _IPANEL_MIDDLEWARE_PORTING_NETWORK_API_FUNCTOTYPE_H_
