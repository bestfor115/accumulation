/*********************************************************************
    Copyright (c) 2012 iPanel Technologies, Ltd.
    All rights reserved. You are not allowed to copy or distribute
    the code without permission.
 	History:	2012-9-5 pdc-develop7 Created
*********************************************************************/
#ifndef _MPLAYER_H__
#define _MPLAYER_H__

#ifdef __cplusplus
extern "C" {
#endif

/*�궨��*/
#define MPLAYER_OK  (0)
#define MPLAYER_ERR (-1)

/*����������ö��*/
typedef enum
{
    MPLAYER_SERVER_NONE         = 0,
    MPLAYER_SERVER_SEACHANGE    = 1, /*˼ǨVOD*/
    MPLAYER_SERVER_ISMA         = 2, /*˼��VOD*/
    MPLAYER_SERVER_DMX          = 3, /*DMXЭ��VOD*/
    MPLAYER_SERVER_TELECOM      = 4, /*����VOD*/
    MPLAYER_SERVER_UT           = 5, /*UT VOD*/
    MPLAYER_SERVER_CISCO_DMX    = 6, /*NGODЭ��VOD*/
    MPLAYER_SERVER_IPANEL       = 7  /*IPANEL VOD*/
}mplayer_server_e;

/*����ģʽö��*/
typedef enum
{
    MPLAYER_MODE_NONE   = 0,
    MPLAYER_MODE_IP     = 1,    /*IPģʽ*/
    MPLAYER_MODE_DVB    = 2     /*DVBģʽ*/
}mplayer_mode_e;

/*��������ö��*/
typedef enum
{
    MPLAYER_TYPE_NONE   = 0,
    MPLAYER_TYPE_LiveTV = 1,    /*ֱ��*/
    MPLAYER_TYPE_VOD    = 2,    /*�㲥*/
    MPLAYER_TYPE_TSTV   = 3     /*ʱ��*/    
}mplayer_type_e;

/*��Ϣö��*/
typedef enum 
{
	MPLAYER_PREPAREPLAY_SUCCESS = 0,/*׼�����ųɹ�,�ϲ�Ӧ���յ�����Ϣ���Ե���play*/
	MPLAYER_CONNECT_FAILED      = 1,/*���ӷ�����ʧ��,�����Ựʧ�ܻ��߷��������س�ʱ*/
	MPLAYER_PLAY_SUCCESS        = 2,/*����ý��ɹ�*/
	MPLAYER_PLAY_FAILED	        = 3,/*����ý��ʧ��*/
	MPLAYER_PROGRAM_BEGIN       = 4,/*ʱ��Ƶ����VODӰƬ���ŵ��˵㲥��ʼ��λ��*/
	MPLAYER_PROGRAM_END         = 5,/*ʱ��Ƶ����VODӰƬ���ŵ��˵㲥������λ��*/
	MPLAYER_OUT_OF_RANGE        = 6,/*ʱ�ƻ�VODӰƬ���󳬹���ʱ�䷶Χ*/
	MPLAYER_RELEASE_SUCCESS     = 7,/*�ر�ý��ɹ�*/
	MPLAYER_RELEASE_FAIL        = 8,/*�ر�ý��ʧ��*/
	MPLAYER_SET_MEDIA_INFO      = 9, /*����ý�岥�Ų���,p1Ϊmplayer_media_t*/
	MPLAYER_STOP_MEDIA
}mplayer_msg_e;

/*��������ö��*/
typedef enum
{
	MPLAYER_PROP_TYPE           	= 0,
	MPLAYER_PROP_URL            	= 1,
	MPLAYER_PROP_DURATION       	= 2,
	MPLAYER_PROP_SPEED          	= 3,
	MPLAYER_PROP_STATUS         	= 4,
	MPLAYER_PROP_ELAPSED        	= 5,
	MPLAYER_PROP_TSTV_STARTTIME     = 6,
	MPLAYER_PROP_TSTV_PRESENTTIME   = 7,
	MPLAYER_PROP_TSTV_ENDTIME       = 8,
	MPLAYER_PROP_PROGRESS       	= 9,
	MPLAYER_PROP_TSTV_URL
}mplayer_prop_e;

/*���Ų�����Ϣ*/
typedef struct mplayer_media_s
{
	unsigned int    frequency;		/*Ƶ����Ϣ��hex: 0x0FFF0000*/
	unsigned int	symbolrate;		/*hex: 0x00068750*/
	unsigned char	qam;			/*1:16-QAM 2:32-QAM 3:64-QAM 4:128-QAM 5:256=QAM*/
	unsigned short	service_id;		/*ǰ��������ServiceId*/
	unsigned short	pmt_pid;	    /* PMT PID(һЩCAҪ��PMT��PID��ʼ, ECM/EMM��PID����!) */
}mplayer_media_t;

/*****************************************************************
 * @brief ��Ϣ�ص�����,��������Ϣ���ϲ�Ӧ��
 
 * @param player mplayer_open�򿪵Ĳ��ž��
 * @param msg �ص�������Ϣ����,ö����mplayer_msg_e
 * @param p1 ��Ϣ���͵Ĳ���
 * @param p2 ��Ϣ���͵Ĳ���
 *
 * @return ��
******************************************************************/
typedef void (*mplayer_cbf)(void *player, int msg, int p1, int p2);

/*****************************************************************
 * @brief ��ʼ��mplayerģ��,һ��ϵͳ����ʱ����
 
 * @param ��
 *
 * @return ģ���� 
******************************************************************/
void *mplayer_init(void);

/*****************************************************************
 * @brief ����mplayerģ��
 
 * @param handle ��ʼ��ʱ�õ��ľ��
 *
 * @return ��
******************************************************************/
void mplayer_exit(void *handle);

/*****************************************************************
 * @brief �л���ǰvod�����Լ���ʽ,������open url֮ǰ��������л�
 
 * @param server vod����������,������mplayer_server_e
 * @param mode vod����ģʽ,������mplayer_mode_e
 *
 * @return �ɹ�:MPLAYER_OK ʧ��:MPLAYER_ERR
******************************************************************/
int mplayer_change_server(int server, int mode);

/*****************************************************************
 * @brief ��mplayer����ʵ��
 
 * @param url ����·��,rtsp://
 * @param type mplayer��������,������mplayer_type_e
 * @param cbf ע��ĺ���ָ��
 *
 * @return player���
******************************************************************/
void *mplayer_open(const char *url, int type, mplayer_cbf cbf);

/*****************************************************************
 * @brief �ر�mplayer����ʵ��
 
 * @param player ���ž��
 *
 * @return �ɹ�:MPLAYER_OK ʧ��:MPLAYER_ERR
******************************************************************/
int mplayer_close(void *player);

/*****************************************************************
 * @brief ֹͣ����
 
 * @param player ���ž��
 * @param keep_last_frame �Ƿ������һ֡ 1:�������һ֡ 0:����
 *
 * @return �ɹ�:MPLAYER_OK ʧ��:MPLAYER_ERR
******************************************************************/
int mplayer_stop(void *player, int keep_last_frame);

/*****************************************************************
 * @brief ��ͣ����
 
 * @param player ���ž��
 *
 * @return �ɹ�:MPLAYER_OK ʧ��:MPLAYER_ERR
******************************************************************/
int mplayer_pause(void *player);

/*****************************************************************
 * @brief ָ�����ʲ���
 
 * @param player ���ž��
 * @param speed ����
 *
 * @return �ɹ�:MPLAYER_OK ʧ��:MPLAYER_ERR
******************************************************************/
int mplayer_play(void *player, int speed);

/*****************************************************************
 * @brief ָ��λ�ò���
 
 * @param player ���ž��
 * @param seek_time seek��ʱ��
 *
 * @return �ɹ�:MPLAYER_OK ʧ��:MPLAYER_ERR
******************************************************************/
int mplayer_seek(void *player, char *seek_time);

/*****************************************************************
 * @brief ��ȡ������Ϣ
 
 * @param player ���ž��
 * @param prop ��������,mplayer_prop_e
 * @param value ��ȡ��ֵ
 *
 * @return �ɹ�:MPLAYER_OK ʧ��:MPLAYER_ERR
******************************************************************/
int mplayer_get_property(void *player, int prop, void *value);

/*****************************************************************
 * @brief ����������Ϣ
 
 * @param prop ��������,mplayer_prop_e
 * @param value ���õ�ֵ
 *
 * @return �ɹ�:MPLAYER_OK ʧ��:MPLAYER_ERR
******************************************************************/
int mplayer_set_property(void *player, int prop, int value);

/*����������ʵ���ϲ��ǹ�����android�õ�,Ӧ����android�ϲ�Ӧ�ù�����iPlayer�õ�-��ʱ����ô��һ�°�*/
int ipanel_dvb_play(mplayer_media_t *media);

int ipanel_dvb_stop_play(void);


#ifdef __cplusplus
}
#endif

#endif
