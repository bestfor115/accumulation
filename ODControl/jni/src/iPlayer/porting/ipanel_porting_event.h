/******************************************************************************/
/*    Copyright (c) 2009 iPanel Technologies, Ltd.                            */
/*    All rights reserved. You are not allowed to copy or distribute          */
/*    the code without permission.                                            */
/*                                                                            */
/*    $author Zouxianyun 2005/04/28                                           */
/******************************************************************************/

#ifndef _IPANEL_MIDDLEWARE_PORTING_EVENT_H_
#define _IPANEL_MIDDLEWARE_PORTING_EVENT_H_

/**
 * messages same in Event[0] area.
 */
enum
{
    EIS_EVENT_TYPE_TIMER        			= 0x000,
    EIS_EVENT_TYPE_SYSTEM,
    EIS_EVENT_TYPE_KEYDOWN,
    EIS_EVENT_TYPE_KEYUP,
    EIS_EVENT_TYPE_NETWORK,
    EIS_EVENT_TYPE_CHAR         			= 0x005,
    EIS_EVENT_TYPE_MOUSE,
    EIS_EVENT_TYPE_IRKEY,
    EIS_EVENT_TYPE_MKEY,
    EIS_EVENT_TYPE_AUDIO,
	EIS_EVENT_TYPE_MULTIFNIRKEY,			/* 多功能遥控器,该枚举值从2010-05-01开始已不再使用 */
	EIS_EVENT_TYPE_MINPUT,					/* 多功能输入设备,与EIS_EVENT_TYPE_MULTIFNIRKEY有点重复，因为需要兼容以前项目*/

    EIS_EVENT_TYPE_DVB          			= 0x100,
    EIS_EVENT_TYPE_IPTV,
    EIS_EVENT_TYPE_CAM,
	EIS_EVENT_TYPE_MESSAGE					= 0x103,
	EIS_EVENT_TYPE_TIANBAI					= 0x150 
};

enum
{
	/***********************************************
	* Event Type:EIS_EVENT_TYPE_IRKEY              *
	*            EIS_EVENT_TYPE_KEYDOWN            *
 	*            EIS_EVENT_TYPE_KEYUP         P1:  *
	***********************************************/
    EIS_IRKEY_NULL      					= 0x0100,	/* bigger than largest ASCII*/
    EIS_IRKEY_POWER     					= 0x0101,
    EIS_IRKEY_STANDBY,

    /* generic remote controller keys */
    EIS_IRKEY_NUM0      					= 0x110,
    EIS_IRKEY_NUM1,
    EIS_IRKEY_NUM2,
    EIS_IRKEY_NUM3,
    EIS_IRKEY_NUM4,
    EIS_IRKEY_NUM5,     					// 0x115
    EIS_IRKEY_NUM6,
    EIS_IRKEY_NUM7,
    EIS_IRKEY_NUM8,
    EIS_IRKEY_NUM9,
    EIS_IRKEY_UP,       					// 0x11A
    EIS_IRKEY_DOWN,
    EIS_IRKEY_LEFT,
    EIS_IRKEY_RIGHT,
    EIS_IRKEY_SELECT,   					// 0x11E

    /* generic editting/controling keys */
    EIS_IRKEY_INSERT    					= 0x130,
    EIS_IRKEY_DELETE,
    EIS_IRKEY_HOME,
    EIS_IRKEY_END,
    EIS_IRKEY_ESC,
    EIS_IRKEY_CAPS,     					// 0x135
	EIS_IRKEY_TAB							= 0x136,
	EIS_IRKEY_SHIFT	,
	EIS_IRKEY_CTRL,
	EIS_IRKEY_ALT,
	EIS_IRKEY_PAUSE_BREAK,
	EIS_IRKEY_PRINT_SCREEN,
	EIS_IRKEY_LWIN,
	EIS_IRKEY_RWIN,
	EIS_IRKEY_RIGHTK,
	EIS_IRKEY_NUN_LOCK,
	EIS_IRKEY_SCROLL_LOCK,

    EIS_IRKEY_PREVLINK  					= 0x150,
    EIS_IRKEY_NEXTLINK,
    EIS_IRKEY_REFRESH,
    EIS_IRKEY_EXIT,
    EIS_IRKEY_BACK,     					/*backspace*/
    EIS_IRKEY_CANCEL,

    /* generic navigating keys */
    EIS_IRKEY_SCROLL_UP     				= 0x170,
    EIS_IRKEY_SCROLL_DOWN,
    EIS_IRKEY_SCROLL_LEFT,
    EIS_IRKEY_SCROLL_RIGHT,
    EIS_IRKEY_PAGE_UP,
    EIS_IRKEY_PAGE_DOWN,    				// 0x175
    EIS_IRKEY_HISTORY_FORWARD,
    EIS_IRKEY_HISTORY_BACKWARD,
    EIS_IRKEY_SHOW_URL,

    /* function remote controller keys */
    EIS_IRKEY_HOMEPAGE      				= 0x200,
    EIS_IRKEY_MENU,
    EIS_IRKEY_EPG,
    EIS_IRKEY_HELP,
    EIS_IRKEY_MOSAIC,
    EIS_IRKEY_VOD,          				// 0x205
    EIS_IRKEY_NVOD,
    EIS_IRKEY_SETTING,
    EIS_IRKEY_STOCK,
	EIS_IRKEY_EMAIL,						/*邮件键*/
	EIS_IRKEY_ASR,					        // 0x20a	/* 语音键*/
	EIS_IRKEY_3D,					        // 0x20B	/* 3D按键*/

    /* special remote controller keys */
    EIS_IRKEY_SOFT_KEYBOARD 				= 0x230,
    EIS_IRKEY_IME,
    EIS_IRKEY_DATA_BROADCAST,
    EIS_IRKEY_VIDEO,            			/*视频键*/
    EIS_IRKEY_AUDIO,            			/*音频键*/
    EIS_IRKEY_LANGUAGE,     				// 0x235
    EIS_IRKEY_SUBTITLE,
    EIS_IRKEY_INFO,
    EIS_IRKEY_RECOMMEND,        			/*推荐键*/
    EIS_IRKEY_FORETELL,         			/*预告键*/
    EIS_IRKEY_FAVORITE,         			/*收藏键*/
	EIS_IRKEY_SWITCH,						/*切换键*/
	EIS_IRKEY_STATUS,						/*状态键*/
	EIS_IRKEY_PLAYLIST,						/*频道列表键*/
	EIS_IRKEY_PROGRAM_TYPE,					/*节目分类键*/

    /* user controling remote controller keys */
    EIS_IRKEY_LAST_CHANNEL  				= 0x250,
    EIS_IRKEY_CHANNEL_UP,
    EIS_IRKEY_CHANNEL_DOWN,
    EIS_IRKEY_VOLUME_UP,
    EIS_IRKEY_VOLUME_DOWN,
    EIS_IRKEY_VOLUME_MUTE,
    EIS_IRKEY_AUDIO_MODE,

    /* virtual function keys */
    EIS_IRKEY_VK_F1         				= 0x300,
    EIS_IRKEY_VK_F2,
    EIS_IRKEY_VK_F3,
    EIS_IRKEY_VK_F4,
    EIS_IRKEY_VK_F5,
    EIS_IRKEY_VK_F6,        				// 0x305
    EIS_IRKEY_VK_F7,
    EIS_IRKEY_VK_F8,
    EIS_IRKEY_VK_F9,
    EIS_IRKEY_VK_F10,
    EIS_IRKEY_VK_F11,       				// 0x30A
    EIS_IRKEY_VK_F12,

    /* special function keys class A */
    EIS_IRKEY_FUNCTION_A    				= 0x320,
    EIS_IRKEY_FUNCTION_B,
    EIS_IRKEY_FUNCTION_C,
    EIS_IRKEY_FUNCTION_D,
    EIS_IRKEY_FUNCTION_E,
    EIS_IRKEY_FUNCTION_F,

    /* special function keys class B */
    EIS_IRKEY_RED           				= 0x340,
    EIS_IRKEY_GREEN,
    EIS_IRKEY_YELLOW,
    EIS_IRKEY_BLUE,

	EIS_IRKEY_ASTERISK						= 0x350,	/* 功能键'*'	*/
	EIS_IRKEY_NUMBERSIGN     				= 0x351, 	/* 功能键'#'  	*/

    /* VOD/DVD controling keys */
    EIS_IRKEY_PLAY          				= 0x400,
    EIS_IRKEY_STOP,
    EIS_IRKEY_PAUSE,
    EIS_IRKEY_RECORD,
    EIS_IRKEY_FASTFORWARD,
    EIS_IRKEY_REWIND,
    EIS_IRKEY_STEPFORWARD,
    EIS_IRKEY_STEPBACKWARD,
    EIS_IRKEY_DVD_AB,
    EIS_IRKEY_DVD_MENU,
    EIS_IRKEY_DVD_TITILE,
    EIS_IRKEY_DVD_ANGLE,
    EIS_IRKEY_DVD_ZOOM,
    EIS_IRKEY_DVD_SLOW,
    EIS_IRKEY_TV_SYSTEM,
    EIS_IRKEY_DVD_EJECT,
	EIS_IRKEY_BACKWARD						= 0x410,	/*VOD或DVD快退键*/
	EIS_IRKEY_POSITION,									/*VOD或DVD定位键*/
	EIS_IRKEY_TIMESHIFT                     = 0x412,    /*针对时移的按键*/
	/* 飞梭按键 */
	EIS_IRKEY_TURN_LEFT						= 0x41B,
	EIS_IRKEY_TURN_RIGHT					= 0x41C,

	EIS_IRKEY_UNDEFINED
};

/* 飞梭按键, 向左旋转 P2值. 1-9:表示的是旋转速度级别，数值越大表示旋转越快 */
enum
{
	LEFT_LEVEL_1		= 1,
	LEFT_LEVEL_2 		= 2,
	LEFT_LEVEL_3		= 3,
	LEFT_LEVEL_4		= 4,
	LEFT_LEVEL_5		= 5,
	LEFT_LEVEL_6		= 6,
	LEFT_LEVEL_7		= 7,
	LEFT_LEVEL_8		= 8,
	LEFT_LEVEL_9		= 9
};

/* 飞梭按键, 向右旋转 P2值 */
enum
{
	RIGHT_LEVEL_1		= 1,
	RIGHT_LEVEL_2 		= 2,
	RIGHT_LEVEL_3		= 3,
	RIGHT_LEVEL_4		= 4,
	RIGHT_LEVEL_5		= 5,
	RIGHT_LEVEL_6		= 6,
	RIGHT_LEVEL_7		= 7,
	RIGHT_LEVEL_8		= 8,
	RIGHT_LEVEL_9		= 9
};

enum
{
	/***********************************************
	* Event Type:EIS_EVENT_TYPE_MULTIFNIRKEY, P1:  *
	***********************************************/
	EIS_MULTIFNIRKEY_MOTION					= 3700,
	EIS_MULTIFNIRKEY_POSITION				= 3701
};

enum
{
	/***********************************************
	* Event Type:EIS_EVENT_TYPE_MINPUT, P1:  *
	***********************************************/
	EIS_MINPUT_MOTION						= 3750,
	EIS_MINPUT_POSITION						= 3751
};

#define MAKE_MOUSE_POS(t, x, y)     		(((((long)(x)) & 0xffff) << 16) | ((((long)(y)) & 0xffff)))
#define GET_MOUSE_XPOS(e)           		(((e) >> 16) & 0xffff)
#define GET_MOUSE_YPOS(e)           		((e) & 0xffff)
enum
{
	/***********************************************
	* Event Type:EIS_EVENT_TYPE_MULTIFNIRKEY, P1:  *
	***********************************************/
    EIS_MOUSE_MOUSEMOVE						= 3800,
    EIS_MOUSE_LBUTTONDOWN					= 3801,
    EIS_MOUSE_LBUTTONUP						= 3802,
    EIS_MOUSE_RBUTTONDOWN					= 3803,
    EIS_MOUSE_RBUTTONUP						= 3804,
	EIS_MOUSE_MBUTTONDOWN					= 3805,
	EIS_MOUSE_MBUTTONUP						= 3806,
	EIS_MOUSE_LBUTTONDCLICK					= 3807,
	EIS_MOUSE_RBUTTONDCLICK					= 3808,
    EIS_MOUSE_UNDEFINED
};

enum
{
	/***********************************************
	* Event Type:EIS_EVENT_TYPE_MKEY,         P1:  *
	***********************************************/
    EIS_MKEY_NUM0							= 0x1000, //4096
    EIS_MKEY_NUM1,
    EIS_MKEY_NUM2,
    EIS_MKEY_NUM3,
    EIS_MKEY_NUM4,
    EIS_MKEY_NUM5,
    EIS_MKEY_NUM6,
    EIS_MKEY_NUM7,
    EIS_MKEY_NUM8,
    EIS_MKEY_NUM9,
    EIS_MKEY_STAR,
    EIS_MKEY_HASH,
    //
    EIS_MKEY_LBUTTON,
    EIS_MKEY_RBUTTON,
    EIS_MKEY_LEFT,
    EIS_MKEY_RIGHT,
    EIS_MKEY_UP,
    EIS_MKEY_DOWN,
    EIS_MKEY_SELECT,
    //
    EIS_MKEY_NEXT_LINK,
    EIS_MKEY_PREV_LINK,
    EIS_MKEY_SCROLL_UP,
    EIS_MKEY_SCROLL_DOWN,
    EIS_MKEY_SCROLL_LEFT,
    EIS_MKEY_SCROLL_RIGHT,
    EIS_MKEY_PAGE_BEGIN,
    EIS_MKEY_PAGE_END,
    EIS_MKEY_UNDEFINED
};

/* 杭州VOD使用到的消息 */
enum
{
	EIS_VOD_GETDATA_SUCCESS					= 5200, /* 下载数据成功,请求返回时移频道列表 */
	EIS_VOD_GETDATA_FAILED					= 5201, /* 下载数据失败,请求未返回时移频道列表 */
	EIS_VOD_PREPAREPLAY_SUCCESS				= 5202, /* 准备播放媒体成功 */
	EIS_VOD_CONNECT_FAILED					= 5203, /* 连接服务器失败,建立会话失败或者服务器返回超时 */
	EIS_VOD_DVB_SEARCH_FAILED				= 5204, /* DVB在VOD指定的IPQAM中搜索数据失败 */
	EIS_VOD_PLAY_SUCCESS					= 5205, /* 播放媒体成功 */
	EIS_VOD_PLAY_FAILED						= 5206, /* 播放媒体失败 */
	EIS_VOD_PREPARE_PROGRAMS_SUCCESS		= 5207, /* 更新时移频道的节目列表成功 */
	EIS_VOD_PREPARE_PROGRAMS_FAILED			= 5208, /* 更新时移频道的节目列表失败 */
	EIS_VOD_PROGRAM_BEGIN					= 5209, /* 时移频道或VOD影片播放到了点播开始的位置 */
	EIS_VOD_PROGRAM_END						= 5210, /* 时移频道或VOD影片播放到了点播结束的位置 */
	EIS_VOD_RELEASE_SUCCESS					= 5211, /* only DMX need */
	EIS_VOD_RELEASE_FAILED					= 5212, /* only DMX need */
	EIS_VOD_TSTVREQUEST_FAILED				= 5213, /* 时移请求服务器失败 */
	EIS_VOD_EPGURL_REQUEST_FINISHED			= 5214, /* hangzhou don't need */
	EIS_VOD_CHANGE_SUCCESS					= 5220, /* 以后要改成IPANEL_VOD_START_SUCCESS */
	EIS_VOD_CHANGE_FAIL						= 5221, /* 以后要改成IPANEL_VOD_START_FAILED */
	EIS_VOD_START_BUFF						= 5222,
	EIS_VOD_STOP_BUFF						= 5223,
	EIS_VOD_OUT_OF_RANGE					= 5224,
	EIS_VOD_USER_EXCEPTION					= 5225,
	//下面3个需要相关人确认一下
	EIS_VOD_GET_PARAMETER_SUCCESS			= 5226,
	EIS_VOD_GET_PARAMETER_FAILED			= 5227,
	
	EIS_MULTI_PLAYER_EVENT					= 5228,	/* 请相关人修改这个枚举值。。。。。*/

	EIS_VOD_PREPARE_TO_DOWNLOAD           	= 5229,	/* 寻找可播放的视频资源，准备下载资源，网络状态为LOADING */
	EIS_VOD_BEING_DOWNLOADED             	= 5230, /* 正在下载媒体数据，网络状态为：LOADING */
	EIS_VOD_DOWNLOAD_SUCCESS             	= 5231,	/* 媒体数据下载完毕，下载数据的网络连接有可能已经断开，但不会影响媒体的播放。网络状态为LOADED */
	EIS_VOD_RELOADING_PLUGIN               	= 5232,	/* 调用load()方法后，断开已经连接了的网络资源连接，重新加载video插件。网络状态为EMPTY */
	EIS_VOD_TRY_TO_DOWNLOAD                	= 5233,	/* 尝试下载数据，但是下载不下来。网络状态为LOADING。*/
	EIS_VOD_EXECUTE_PAUSE                	= 5234, /* 执行pause()方法，返回此消息，paused 属性为true。*/
	EIS_VOD_EXECUTE_SEEKING               	= 5235,	/* 执行the seek operation需要花费一些时间，期间.seeking属性改变为true。*/
	EIS_VOD_SEEK_SUCCESS                  	= 5236,	/* 执行完the seek operation操作后，seeking属性已经为false. */
	EIS_VOD_MEDIA_STATE_CHANGE             	= 5237,	/* 快进、快退、正常播放切换 */
	EIS_VOD_DOWNLOAD_CACHEING               = EIS_VOD_START_BUFF,	/* 正在缓冲. */
	EIS_VOD_MESSAGE_OPEN					= 5239, /*vod模块通知应用获取相关信息*/
    EIS_VOD_PLAY_ABNORMAL					= 5250, /*上海电信使用,媒体播放异常,p2值区分具体含义:1:流媒体播放时发生轻微断流现象 2:连接流媒体服务器后未收到媒体流 3:流媒体播放时发生严重断流现象(丢包率不小于1%,持续20s时间)*/
	EIS_VOD_RTSP_CMD_ERROR					= 5251, /*上海电信使用,rtsp控制异常,p2值区分具体含义:1:无法读取从服务器发来的RTSP命令 2:发送流媒体命令不成功 3:服务器关闭了RTSP连接*/
	EIS_VOD_MEMBERSHIP_ERROR				= 5252, /*上海电信使用,组播控制异常,p2值区分具体含义:1:组播源错误,无法播放 2:组播时移服务器连接不上*/
    EIS_VOD_PLAY_ABNORMAL_RESUME            = 5253, /*断流恢复*/

    EIS_VOD_PLAY_DVB_INFO                   = 5290, /*DVB播放信息*/
    EIS_VOD_UNDEFINED
};

enum
{
	/***********************************************
	* Event Type:EIS_EVENT_TYPE_NETWORK,      P1:  *
	***********************************************/
	EIS_IP_NETWORK_CONNECT						= 5500,		/*physical signal*/
	EIS_IP_NETWORK_DISCONNECT					= 5501,		/*physical signal*/
	EIS_IP_NETWORK_READY						= 5502,		/*logistic signal*/
	EIS_IP_NETWORK_FAILED						= 5503,		/*logistic signal*/
	EIS_IP_NETWORK_DHCP_MODE					= 5504,		/*logistic signal*/
	EIS_IP_NETWORK_PROVISION_STATUS				= 5505,     /*logistic signal*/
	EIS_IP_NETWORK_GET_DNS_STATUS				= 5506,
	EIS_IP_NETWORK_TEST_OPEN_PAGE				= 5507,
	//
	EIS_IP_NETWORK_NTP_READY					= 5510,		/*logistic signal*/
	EIS_IP_NETWORK_NTP_TIMEOUT					= 5511,		/*logistic signal*/
	EIS_IP_NETWORK_SET_NTP_SERVER_NOTIFY		= 5512,
	EIS_IP_NETWORK_CLEAN						= 5513,
	//
	EIS_IP_NETWORK_PING_RESPONSE				= 5520,		/*physical signal*/
    EIS_IP_NETWORK_PING_RESULTS                 = 5521,
    EIS_IP_NETWORK_ROUTE_TRACE_RESULTS          = 5522,
	//pppoe,added by chenhuan.
    EIS_IP_NETWORK_PPPOE_READY					= 5532,     /*logistic signal*/
    EIS_IP_NETWORK_PPPOE_FAILED					= 5533,     /*logistic signal*/
	//
	EIS_CABLE_NETWORK_CONNECT					= 5550,		/*physical signal*/
	EIS_CABLE_NETWORK_DISCONNECT				= 5551,		/*physical signal*/
	//
	EIS_CABLE_NETWORK_CM_DISCONNECTED			= 5560,
	EIS_CABLE_NETWORK_CM_CONNECTED				= 5561,
	EIS_CABLE_NETWORK_CM_CONNECTING				= 5562,
	EIS_CABLE_NETWORK_CM_UPLINK_STATUS			= 5563,
    EIS_CABLE_NETWORK_CM_DOWNLINK_STATUS		= 5564,

	EIS_HDMI_3DTV_LINKED                        = 5850,    /*HDMI connect TV*/
	EIS_HDMI_3DTV_UNLINK                        = 5851,    /*HDMI disconnect TV*/
	//
	EIS_MISC_THIRD_PARTY_STOCK_EXIT				= 5990,
	EIS_MISC_INFOLAYOUT_MESSAGE					= 5998,	/*提示进行应用信息的显示*/
	//6500-6999这个区域是给无线网络 划定的消息区域
	EIS_GPRS_OFFLINE                            = 6500,
	EIS_GPRS_ONLINE                             = 6501,

    EIS_IP_NETWORK_WIFI_AP_NETOK                = 6600, //WIFI模块网络正常
    EIS_IP_NETWORK_WIFI_AP_NETERR               = 6601, //WIFI模块网络错误

	EIS_IP_NETWORK_DHCPSERVER_CONNECT_SUCCESS   = 6650, // DHCP SERVER启动成功
	EIS_IP_NETWORK_DHCPSERVER_CONNECT_FAILED    = 6651, // DHCP SERVER启动失败

    EIS_IP_NETWORK_3G_HAVESIGN                  = 6700, // 3G模块检测到网络信号
	EIS_IP_NETWORK_3G_NOSIGN                    = 6701, // 3G模块无网络信号
	EIS_IP_NETWORK_3G_HAVESIMCARD               = 6702, // 3G模块检测到SIM卡
	EIS_IP_NETWORK_3G_NOSIMCARD                 = 6703, // 3G模块没有SIM卡
    EIS_IP_NETWORK_3G_NETOK                     = 6704, // 无线网络ppp成功,网络有效
	EIS_IP_NETWORK_3G_NETERR                    = 6705, // 无线网络ppp失败,网络无效
	EIS_IP_NETWORK_3G_NETTYPE                   = 6706, // IPANEL_NETWORK_3G_TYPE_e
	EIS_IP_NETWORK_3G_PHONE_NUM                 = 6707, // IPANEL_NETWORK_3G_PHONE_NUM
	EIS_IP_NETWORK_3G_REINIT                    = 6708, // 3G模块重新初始化, 0: 开始,1:完成,
	

	EIS_IP_NETWORK_WIFI_DONGLE_PHYSICAL_CONNECT = 6750,/*physical signal*/
	EIS_IP_NETWORK_WIFI_DONGLE_PHYSICAL_DISCONNECT= 6751,/*physical signal*/
	EIS_IP_NETWORK_WIFI_DONGLE_NETWORK_READY    = 6752,/*logistic signal*/
	EIS_IP_NETWORK_WIFI_DONGLE_NETWORK_FAILED   = 6753,/*logistic signal*/
	EIS_IP_NETWORK_WIFI_DONGLE_SCAN_FINISH      = 6754,/*logistic signal*/
	EIS_IP_NETWORK_WIFI_DONGLE_SCAN_TIMEOUT     = 6755,/*logistic signal*/

	//
	EIS_NETWORK_UNDEFINED
};

//语音识别消息
enum
{
	/***********************************************
	* Event Type:EIS_EVENT_TYPE_IPTV,         P1:  *
	***********************************************/
	EIS_SPEECH_RECOGNITION_NOTIFY 				= 5710, //语音识别失败消息通知
	EIS_SPEECH_RECOGNITION_COMMAND 				= 5711, //语音识别成功消息
	EIS_SPEECH_RECOGNITION_SUCCESS        		= 5712,	//语音连接成功 
	EIS_SPEECH_RECOGNITION_FAILED 				= 5713, //语音连接失败
	EIS_SPEECH_RECOGNITION_WARNING 				= 5714,	//到页面的通知消息
	EIS_SPEECH_RECOGNITION_DEV_ID 				= 5715
};
//p2值
enum
{
	EIS_SPEECH_RECOGNITION_ACL_CONNECTED		= 1, 	//ACL控制连接成功
	EIS_SPEECH_RECOGNITION_SCO_CONNECTED 		= 2,  	//语音连接成功
	EIS_SPEECH_RECOGNITION_HCID_RUNING 			= 3, 	//HCID正在启动
	EIS_SPEECH_RECOGNITION_HCID_IDLE 			= 4 	//HCID正常工作
};
enum
{
	EIS_SPEECH_RECOGNITION_ACL_DISCONNECTED 	= 1,  	//ACL控制连接断开
	EIS_SPEECH_RECOGNITION_ACL_ERROR 			= 2,  	//ACL控制连接失败
	EIS_SPEECH_RECOGNITION_SCO_DISCONNECTED 	= 3, 	//语音连接断开
	EIS_SPEECH_RECOGNITION_SCO_ERROR 			= 4,  	//语音连接失败
	EIS_SPEECH_RECOGNITION_HCID_ERROR 			= 5, 	//HCID启动失败
	EIS_SPEECH_RECOGNITION_NO_PANEL 			= 6,	//搜索不到遥控器
	EIS_SPEECH_RECOGNITION_OPEN_DANGLE_ERROR 	= 7    	//设备打开失败
};
enum
{
	EIS_SPEECH_RECOGNITION_WAITING 				= 1,	//通知用户等待ACL配对成功
	EIS_SPEECH_RECOGNITION_PANEL_LOW_POWER 		= 2  	//遥控器电量低
};

enum
{
	/***********************************************
	* Event Type:EIS_EVENT_TYPE_DVB,          P1:  *
	***********************************************/
	EIS_PVR_RECORD_SPACE_NOT_ENOUGH				= 5755	//PVR录制空间不足
};

enum
{
	/***********************************************
	* Event Type:EIS_EVENT_TYPE_IPTV,          P1:  *
	***********************************************/
	EIS_OTHER_APP_MESSAGE_OPEN					= 5780,	//邦天信息提示，作用：打开信息。该消息流向：porting》应用
	EIS_OTHER_APP_MESSAGE_CLOSE					= 5781,  //邦天信息提示，作用：关闭信息。该消息流向：porting》应用
	EIS_OTHER_APP_MESSAGE_SHINEDOT				= 5791
};
enum
{
   	/***********************************************
	* Event Type:EIS_EVENT_TYPE_IPTV,          P1:  *
	***********************************************/
    EIS_USB_UPGRADE_SUCCESS                     =5870, //USB升级检测成功，需要重启进入升级
    EIS_USB_UPGRADE_FAILED                      =5871  //USB升级检测失败，具体失败原因由P2值定
};

enum
{
	/***********************************************
	* Event Type:EIS_EVENT_TYPE_IPTV,         P1:  *
	***********************************************/
	EIS_DEVICE_USB_INSERT						= 6001,	//有USB设备插入，此时物理设备已加电，设备尚未正常工作。P2值为设备的驱动器号
	EIS_DEVICE_USB_INSTALL						= 6002,	//USB设备就绪，此时物理设备已安装，如果是存储类，表明文件系统已经可以正常工作。P2值为设备的驱动器号
	EIS_DEVICE_USB_DELETE						= 6003,	//USB设备移除，此时物理设备已被从系统中移除，如果是存储设备，设备已经处于可移动状态。P2值为设备的驱动器号
	EIS_DEVICE_USB_UNAVAILABLE					= 6004,	//USB设备不可用（可能是驱动程序找不到或其它原因）。P2值为设备的驱动器号
	//
	EIS_DEVICE_DISK_MOUNTING					= 6010,	//磁盘正在初始化
	EIS_DEVICE_DISK_MOUNTED						= 6011,	//磁盘初始化完毕，可以使用
	EIS_DEVICE_DISK_UNMOUNTED					= 6012,	//
	//
	EIS_DEVICE_PARTITION_FORMAT_START			= 6020,	//存储设备中某个分区开始格式化 P2值为分区id
	EIS_DEVICE_PARTITION_FORMAT_SUCCESS			= 6021, //存储设备其中某个分区格式化成功
	EIS_DEVICE_PARTITION_FORMAT_FAILED			= 6022, //存储设备其中某个磁盘格式化失败
	EIS_DEVICE_PARTITION_CANCEL_FORMAT_SUCCESS	= 6023,	//取消存储设备某个分区格式化操作成功。P2值为分区id
	EIS_DEVICE_PARTITION_CANCEL_FORMAT_FAILED	= 6024,	//取消存储设备某个分区格式化操作失败。P2值为分区id
	EIS_DEVICE_PARTITION_CREATE_SUCCESS			= 6025,	//存储设备某个分区创建成功。P2值为分区id
	EIS_DEVICE_PARTITION_CREATE_FAILED			= 6026,	//存储设备某个分区创建失败。P2值为分区id
	EIS_DEVICE_PARTITION_DELETE_SUCCESS			= 6027,	//存储设备某个分区删除成功。P2值为分区id
	EIS_DEVICE_PARTITION_DELETE_FAILED			= 6028,	//存储设备某个分区删除失败。P2值为分区id
	EIS_DEVICE_PARTITION_INSTALL_SUCCESS		= 6029,	//存储设备某个分区挂载成功。P2值为分区id
	EIS_DEVICE_PARTITION_INSTALL_FAILED			= 6030,	//存储设备某个分区挂载失败。P2值为分区id
	//
	EIS_DEVICE_CDROM_DISC_READY                 = 6040, // CDROM中有光盘并且可用的消息
    EIS_DEVICE_CDROM_DISC_UNAVAILABLE           = 6041, //CDROM没有光盘或者不可用的消息
    EIS_DEVICE_CDROM_EJECT_SUCCESS              = 6042,   //CDROM 弹出成功
    EIS_DEVICE_CDROM_EJECT_FAILED               = 6043,   //CDROM 弹出失败
    EIS_DEVICE_CDROM_CLOSE_SUCCESS              = 6044,   //CDROM 关闭成功  
    EIS_DEVICE_CDROM_CLOSE_FAILED               = 6045,  // CDROM 关闭失败
//
	EIS_DEVICE_DECODER_NORMAL					= 6050, //音视频解码器工作正常
	EIS_DEVICE_DECODER_HUNGER					= 6051, //音视频解码器没有数据输入
	//
	EIS_DEVICE_TUNER_FAULT_ALARM				= 6060, //告警上报功能:需要检测eLevel门限,BER门限,CN门限,记录恢复门限状态
	//用于USBDriver的下载和安装消息,临时测试
	EIS_DRIVE_UNINSTALL							= 6070,
	EIS_SPACE_NOENOUGH							= 6071,
	EIS_DRIVE_INSTALL_SUCCESS					= 6072,
	EIS_DRIVE_DOWNLOAD_SUCCESS 					= 6073, 
	EIS_DRIVE_DOWNLOAD_FAIL						= 6074,

	EIS_FILE_NOT_WRITABLE                       = 6107,//  修改 EIS_DEVICE_NOT_WRITABLE = 6080,	为 EIS_FILE_NOT_WRITABLE=6107

	EIS_DEVICE_UNKNOWN
};

enum
{
	EIS_SERVER_LOGIN_SUCCESS				= 6870,/*登陆server成功*/
	EIS_SERVER_LOGIN_FAILED					= 6871,/*登陆server失败*/
	EIS_SERVER_LOGIN_UNDEFINED
};

enum
{
	/***********************************************
	* Event Type:EIS_EVENT_TYPE_DVB,          P1:  *
	***********************************************/
    EIS_DVB_TUNE_SUCCESS 						= 8001,
    EIS_DVB_TUNE_FAILED             			= 8002,
    EIS_DVB_UPGRADE_MESSAGE        				= 8387
};

enum
{
	EIS_SMS_RECEIVE_NEW                         = 9310, //收到新消息
	EIS_SMS_SEND_SUCCESS                        = 9311, //发送成功
	EIS_SMS_SEND_FAILD                          = 9312  //发送失败
};

#endif//_IPANEL_MIDDLEWARE_PORTING_EVENT_H_

