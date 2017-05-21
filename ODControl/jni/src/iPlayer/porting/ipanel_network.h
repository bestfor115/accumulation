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
	IPANEL_NETWORK_GET_UPLINKFREQ       = 23,	/* ��ȡCable Modem������Ƶ�ʲ���*/
	IPANEL_NETWORK_GET_DOWNLINKFREQ     = 24,	/* ��ȡCable Modem������Ƶ�ʲ���*/
	IPANEL_NETWORK_SET_DOWNLINKFREQ		= 25,	/* ����Cable Modem������Ƶ�ʲ���*/
	IPANEL_NETWORK_GET_CM_DEVICE_INFO	= 26,   /* ��ȡ�������Ƿ����CM�豸����������״̬*/    
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
	IPANEL_NETWORK_GET_CM_VERSION		= 39,	/* ��ȡcable modem�İ汾�� */	
	IPANEL_NETWORK_SET_WIFI_AP_OPTION   = 40,   // WIFI AP
	IPANEL_NETWORK_SET_DHCP_SERVER_OPTION = 41,   // DHCP Server 	
	IPANEL_NETWORK_GET_DHCP_SERVER_INFO = 42,
	IPANEL_NETWORK_GET_PHONE_NUM     	= 43,
	IPANEL_NETWORK_GET_3G_INFO          = 44,
	IPANEL_NETWORK_GET_WIFI_AP_NUMBER   = 45,
	IPANEL_NETWORK_GET_WIFI_AP_INFO     = 46,
	IPANEL_NETWORK_SET_WIFI_DONGLE_OPTION = 47,
    IPANEL_NETWORK_WIFI_DONGLE_SCAN       = 48,
    IPANEL_NETWORK_GET_NIC_SEND_BYTES     = 49, /*����IP�����ֽ���*/
    IPANEL_NETWORK_GET_NIC_REVD_BYTES     = 50, /*����IP�����ֽ���*/
    IPANEL_NETWORK_GET_PING_INFO          = 51, /*��ȡping���������Ϣ*/
    IPANEL_NETWORK_SET_PING_PARAM         = 52, /*����ping������ز���*/
    IPANEL_NETWORK_GET_ROUTE_TRACE_INFO   = 53, /*��ȡtrace�����Ϣ*/
    IPANEL_NETWORK_SET_ROUTE_TRACE_PARAM  = 54, /*����trace��ز���*/
	IPANEL_NETWORK_GET_DHCP_OPTION		  = 55, /* ��ȡDHCP��ز�����Ϣ */
	IPANEL_NETWORK_GET_PING_RESPONSE      = 56, /* ��ȡping����Ӧ��Ϣ */
	IPANEL_NETWORK_SET_DHCPCONFIG		  = 57,  /* ����option��ֵ��Ĭ��Ϊ151���㶫ʡ������ */
	IPANEL_NETWORK_GET_BANDWIDTH		  = 58,
	IPANEL_NETWORK_GET_ROUTING_INFO       = 59,   /*��ȡ·�����������Ϣ*/
	IPANEL_NETWORK_ROUTING_SET_MACADDR_PERMISSION = 60,  /*����·��MAC����Ȩ��*/ 	
	IPANEL_NETWORK_GET_ROUTING_MAC_ADDRESS        = 61,  /*��ȡ·�ɵ�MAC��ַ*/
	IPANEL_NETWORK_ROUTING_DEL_MAC_ADDRESS        = 62,  /*ɾ��MAC���Ƶĵ�ַ*/
	IPANEL_NETWORK_ROUTING_ADD_MAC_ADDRESS        = 63,  /*���MAC���Ƶĵ�ַ*/
	IPANEL_NETWORK_ROUTING_RESTORE        = 64,   /*�ָ�·��ȱʡ����*/
	IPANEL_NETWORK_GET_ROUTING_DIAGNOSTIC_STATUS  = 65,   /*���·�ɹ���״̬*/
	IPANEL_NETWORK_GET_UPLINK_BAND        = 66 ,	/*��ȡcable modem������ͨ��������MbpsΪ��λ������ֵ */
	IPANEL_NETWORK_GET_CURRWIFI_AP_INFO =	67,  	//��ȡ��ǰWiFi������Ϣ
	IPANEL_NETWORK_UNDEFINED  	

} IPANEL_NETWORK_IOCTL_e;

typedef VOID (*IPANEL_NETWORK_STREAM_HOOK)(IPANEL_XMEMBLK *pmemblk); // IPANEL_NETWORK_SET_STREAM_HOOK

typedef struct
{
	UINT32_T							ipv4addr;	//���ն�ip��ַ
	UINT32_T							port;		//���ն�ip�˿ں�
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
	IPANEL_NETWORK_CM_NOEXIST			=  0,  /*��ʾ����������CM*/
	IPANEL_NETWORK_CM_DISCONNECTED		=  1,  /*��ʾ������CM����������δ����*/
	IPANEL_NETWORK_CM_CONNECTED			=  2   /*��ʾ������CM����������������*/
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

//DHCP SERVER�豸״̬�������ݽṹ
typedef struct		// (IPANEL_NETWORK_SET_DHCP_SERVER_OPTION | IPANEL_NETWORK_CONNECT) || IPANEL_NETWORK_GET_DHCP_SERVER_INFO
{
    UINT32_T		ipaddr_start;
    UINT32_T		ipaddr_end;
    UINT32_T		gateway;
} IPANEL_NETWORK_DHCP_SERVER_INFO;

//WIFI����Ҫ����Կʹ�õ�������֤ģʽ
typedef enum
{
	IPANEL_WIFI_AUTH_AUTO      = 0, /*AUTO*/		
	IPANEL_WIFI_AUTH_OPEN      = 1, /*����ʽ*/
	IPANEL_WIFI_AUTH_SHARED    = 2, /*����ʽ*/
	IPANEL_WIFI_AUTH_WPA       = 3, /*WPA*/
	IPANEL_WIFI_AUTH_WPAPSK    = 4, /*WPA-PSK*/
	IPANEL_WIFI_AUTH_WPA2      = 5, /*WPA2*/
	IPANEL_WIFI_AUTH_WPA2PSK   = 6 	/*WPA2-PSK*/	
}IPANEL_NETWORK_WIFI_AUTH_MODE;

//WIFI����Ҫ����Կʹ�õ����ݼ����㷨
typedef enum
{
	IPANEL_WIFI_ENCRYP_AUTO    = 0, /*AUTO*/		
	IPANEL_WIFI_ENCRYP_NONE    = 1, /*�޼��ܣ������ڿ���ʽ��������֤ģʽ*/
	IPANEL_WIFI_ENCRYP_WEP     = 2, /*WEB,�����ڿ���ʽ�͹���ʽ��������֤ģʽ*/
	IPANEL_WIFI_ENCRYP_TKIP    = 3, /*TKIP,������WPA��WPA-PSK������֤ģʽ*/
	IPANEL_WIFI_ENCRYP_AES     = 4  /*AES,������WPA��WPA-PSK������֤ģʽ*/
}IPANEL_NETWORK_WIFI_ENCRYP_TYPE;

//WIFI AP�豸�����������ݽṹ
typedef struct		// IPANEL_NETWORK_CONNECT
{
    CHAR_T							ssid[36]; 	// SSID�������ַ���
    CHAR_T							key[36];  	// ��¼�����ַ���
    UINT32_T						gateway;
    IPANEL_NETWORK_WIFI_AUTH_MODE 	auth_mode; /* ������֤ģʽ */
	IPANEL_NETWORK_WIFI_ENCRYP_TYPE encryp_type; /* ���ݼ����㷨 */
    UINT32_T 						channel; 	// ��������ʹ�õ��ŵ�,ȡֵ��Χ:1~11
} IPANEL_NETWORK_WIFI_AP_PARAM;

//WIFI AP�豸״̬�������ݽṹ
typedef struct		// IPANEL_NETWORK_GET_WIFI_AP_CONINFO
{
    CHAR_T							ssid[36]; 	// SSID�������ַ���
    CHAR_T							key[36];  	// ��¼�����ַ���
    UINT32_T						level;		// 0~100, 0:���ź�,100:�ź���ǿ
    IPANEL_NETWORK_WIFI_AUTH_MODE 	auth_mode; /* ������֤ģʽ */
    IPANEL_NETWORK_WIFI_ENCRYP_TYPE encryp_type; /* ���ݼ����㷨 */
    UINT32_T 						channel; 	// ��������ʹ�õ��ŵ�,ȡֵ��Χ:1~11
} IPANEL_NETWORK_WIFI_AP_INFO;

/* ��ȡWIFI DONGLE ���������еĿ����������� */
typedef struct { 
	CHAR_T ssid[64]; /* ����������󳤶�32���ַ� */
	UINT32_T channel; /* �ŵ���ʶ����IEEE 802.11b/g����14���ŵ���802.11a/nʹ�ø����ŵ� */
	UINT32_T level; /* 0~100, 0:���ź�,100:�ź���ǿ */
	UINT32_T encryption; /* 0:�޼��ܣ�1:�����˼��� */
	UINT32_T bit_rates; /* �ٶȣ���λMbps */
	IPANEL_NETWORK_WIFI_AUTH_MODE    auth_mode; /* ������֤ģʽ */      
	IPANEL_NETWORK_WIFI_ENCRYP_TYPE encryp_type; /* ���ݼ����㷨 */
	UINT32_T 						network_type; /* 0:infrastructure mode; 1:adhoc mode */
	CHAR_T  MACAddr[32]; /* ����·�ɵ�mac��ַ����󳤶�32���ַ�*/ 
}IPANEL_NETWORK_WIFI_SCAN_INFO;


/*��ȡ��ǰ�Ѿ������������WiFi����������Ϣ*/
typedef struct { 
       CHAR_T ssid[64]; /* ����������󳤶�64���ַ� */
       UINT32_T channel; /* �ŵ���ʶ����IEEE 802.11b/g����14���ŵ���802.11a/nʹ�ø����ŵ� */
       UINT32_T level; /* 0~100, 0:���ź�,100:�ź���ǿ */
       UINT32_T encryption; /* 0:�޼��ܣ�1:�����˼��� */
       UINT32_T bit_rates; /* �ٶȣ���λMbps */
       IPANEL_NETWORK_WIFI_AUTH_MODE  auth_mode; /* ������֤ģʽ */
       IPANEL_NETWORK_WIFI_ENCRYP_TYPE encryp_type; /* ���ݼ����㷨 */
       UINT32_T 						network_type; /* 0:infrastructure mode; 1:adhoc mode */
       CHAR_T   MACAddr[32]; /* ����·�ɵ�mac��ַ����󳤶�32���ַ�*/
       UINT32_T  key_type; /* 0:ʮ�����ƣ�1:ASSIC �������� */
       CHAR_T   key[64];   // ��¼�����ַ���
}IPANEL_NETWORK_CURRWIFI_INFO;


/* �����������ò��� */
typedef struct
{
	CHAR_T 							ssid[64]; /* ����������󳤶�32���ַ� */
	UINT32_T 						network_type; /* 0:infrastructure mode; 1:adhoc mode */
	IPANEL_NETWORK_WIFI_AUTH_MODE 	auth_mode; /* ������֤ģʽ */
	IPANEL_NETWORK_WIFI_ENCRYP_TYPE    encryp_type; /* ���ݼ����㷨 */
	UINT32_T 						key_type; /* 0:ʮ�����ƣ�1:ASSIC */
	CHAR_T 							key_str[64]; /* 10 or 26 characters (key_type=0); 5 or 13 characters? (key_type=1) */
	UINT32_T							encryption; /* 0:�޼��ܣ�1:�����˼��� */
	CHAR_T        					        MACAddr[32]; /* ����·�ɵ�mac��ַ����󳤶�32���ַ�*/ 
}IPANEL_NETWORK_WIFI_DONGLE_OPTION;

typedef enum		//IPANEL_NETWORK_DEVICE_3G
{
    IPANEL_NETWORK_INVALID	= 1,	// δ֪����
	IPANEL_NETWORK_2G  		= 2,	// 3G����
	IPANEL_NETWORK_3G		= 3		// 2G����
}IPANEL_NETWORK_3G_TYPE_e;

/*3G�豸״̬�������ݽṹ*/
typedef struct		// IPANEL_NETWORK_GET_3G_INFO 
{
	UINT32_T					level;	// 0~100, 0:���ź�(����״̬),100:�ź���ǿ
	IPANEL_NETWORK_3G_TYPE_e	type;	// ��������
	UINT32_T					up_speed; // ��������, ��λΪbps(bits/second)    	UINT32_T	down_speed; // ��������,��λΪbps(bits/second)
} IPANEL_NETWORK_3G_INFO;

/*3G�豸����������ݽṹ*/
typedef struct		// IPANEL_NETWORK_GET_PHONE_NUM
{
    CHAR_T		num[30];	// �������, ����������Ч�ַ����ַ���
} IPANEL_NETWORK_3G_PHONE_NUM;

typedef struct  //IPANEL_NETWORK_GET_PING_INFO 
{
    UINT32_T  successCount;     //ping�ɹ������ݰ�����
    UINT32_T  failureCount;     //pingʧ�ܵ����ݰ�����
    UINT32_T  avgResponseTime ; // ping��ƽ����Ӧʱ��(����,ms)
    UINT32_T  minResponseTime ; // ping����С��Ӧʱ��(����,ms)
    UINT32_T  maxResponseTime ; // ping�������Ӧʱ��(����,ms)
} IPANEL_NETWORK_PING_INFO;

typedef struct  // IPANEL_NETWORK_SET_PING_PARAM 
{
    CHAR_T    host[128];    // Ping������������IP��ַ
    UINT32_T  numberOfRepetitions;    //Ping��ϵĴ���
    UINT32_T  timeOut;      //��ʱʱ��(����,ms)
    UINT32_T  dataBlockSize;    // Ping���ݰ��Ĵ�С[1:65535]
    UINT32_T  DSCP;    //���԰�������DiffServ�����[0:63]��Ĭ��Ϊ0
} IPANEL_NETWORK_PING_PARAM ;

typedef struct  // IPANEL_NETWORK_GET_ROUTE_TRACE_INFO    
{
    UINT32_T  ResponseTime;    //��Ӧʱ�䣬Ĭ��Ϊ0
    UINT32_T  numberOfRepetitions;    //·�ɵ�������Ĭ��Ϊ0
    UINT32_T  hops;    //��ת����
    UINT32_T  hopHostsBufLen; //Buffer����
    CHAR_T*   hopHostsBuf;     //�÷ֺŷָ�������·�ɹ����о�������������IP��ַ
} IPANEL_NETWORK_ROUTE_TRACE_INFO;

typedef struct   // IPANEL_NETWORK_SET_ROUTE_TRACE_PARAM 
{
    CHAR_T    host[256];    //·����ϵ�������������
    UINT32_T  timeOut;      //��ʱʱ��(����,ms)
    UINT32_T  dataBlockSize;    // ·����Ϸ������ݰ��Ĵ�С[1:65535]
    UINT32_T  maxHopCount;    // ���ݰ��������[1:64],Ĭ��32
    UINT32_T  DSCP;   //���԰�������DiffServ�����[0:63]��Ĭ��Ϊ0
} IPANEL_NETWORK_ROUTE_TRACE_PARAM;

typedef struct    //IPANEL_NETWORK_GET_DHCP_OPTION
{	
	UINT32_T   address;  		//DHCP������IP��ַ 
	UINT16_T   port;     		//DHCP�Ķ˿ں� 
	UINT32_T   leaseObtained; 	//����IP��ַ��ʼʱ�䣬���ͣ� UTCʱ��
	UINT32_T   leaseExpires;   	//����IP��ַ����ʱ�䣬���ͣ� UTCʱ��
    CHAR_T	   private_data[256]; //��ȡdhcp option�е��û��Զ�������
} IPANEL_NETWORK_DHCP_OPTION;

typedef struct{        		// IPANEL_NETWORK_GET_PING_RESPONSE	
  	UINT32_T      recv;   	//�������ݣ���λbytes	
  	UINT32_T      ttl;  	//��������	
  	UINT32_T      address; 	//�ṩ���ݵ�IP��ַ 	
  	UINT32_T      time; 	//ping�������õ�ʱ�䣬��λms
} IPANEL_NETWORK_PING_RESPONSE; 


typedef struct
{
	UINT32_T   avgBandWidth;    //1�����ڵ�ƽ������(��λmbps)
	UINT32_T   curBandWidth;     //ʵʱ����(��λmbps)
}IPANEL_NETWORK_BANDWIDTH;

//��ȡ·���豸���������Ϣ
typedef struct		// IPANEL_NETWORK_GET_ROUTING_INFO=59
{
	UINT32_T       DHCPEnable;       //DHCP����1������0�ر� 
	UINT32_T       MACAddrEnable;    //MAC���ƣ�1��ʾ����0��ʾ�ܾ�
}IPANEL_NETWORK_ROUTING_INFO;

INT32_T ipanel_porting_network_ioctl(IPANEL_NETWORK_DEVICE_e device, IPANEL_NETWORK_IOCTL_e op, VOID *arg);

#ifdef __cplusplus
}
#endif

#endif // _IPANEL_MIDDLEWARE_PORTING_NETWORK_API_FUNCTOTYPE_H_
