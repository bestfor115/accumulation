/******************************************************************************/
/*    Copyright (c) 2005 iPanel Technologies, Ltd.                     */
/*    All rights reserved. You are not allowed to copy or distribute          */
/*    the code without permission.                                            */
/*    This is the demo implenment of the Porting APIs needed by iPanel        */
/*    MiddleWare.                                                             */
/*    Maybe you should modify it accorrding to Platform.                      */
/*                                                                            */
/*    $author huzh 2007/11/19                                                 */
/******************************************************************************/

#ifndef _IPANEL_MIDDLEWARE_PORTING_VIDEO_DECODER_API_FUNCTOTYPE_H_
#define _IPANEL_MIDDLEWARE_PORTING_VIDEO_DECODER_API_FUNCTOTYPE_H_

#include "ipanel_porting.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum
{
    IPANEL_VDEC_LAST_FRAME,
    IPANEL_VDEC_BLANK
}IPANEL_VDEC_STOP_MODE_e;

typedef struct
{
	INT32_T len;
	BYTE_T *data;
}IPANEL_IOCTL_DATA;

typedef enum {
    IPANEL_VDEC_FRAMERATE_UNKNOWN = 0,//未知帧率
	IPANEL_VDEC_FRAMERATE_E23_976,    //帧率为23.976帧/s
	IPANEL_VDEC_FRAMERATE_E24,        //帧率为24帧/s
	IPANEL_VDEC_FRAMERATE_E25,        //帧率为25帧/s
	IPANEL_VDEC_FRAMERATE_E29_97,     //帧率为29.97帧/s
	IPANEL_VDEC_FRAMERATE_E30,        //帧率为30帧/s
	IPANEL_VDEC_FRAMERATE_E50,        //帧率为50帧/s
	IPANEL_VDEC_FRAMERATE_E59_94,     //帧率为59.94帧/s
	IPANEL_VDEC_FRAMERATE_E60,        //帧率为60帧/s
	IPANEL_VDEC_FRAMERATE_EMAX        //帧率为未知
}IPANEL_VDEC_FRAMERATE_e;

typedef struct
{
    UINT32_T valid;               //当前视频是否有效
    UINT32_T video_pid;           //如果存在pid下发视频pid
    UINT32_T codec_id;            //下发视频对应codec种类，与IPANEL_VDEC_START中附带参数相同
    UINT32_T frame_rate;          //帧率，对应IPANEL_VDEC_FRAMERATE_e
    UINT32_T width;               //视频宽度
    UINT32_T height;              //视频高度
    UINT32_T private_len;         //视频私有数据长度，一般初始化解码器使用
    BYTE_T   private_data[512];   //视频私有数据内容
    UINT32_T stream_type;         //当前数据的种类，对应IPANEL_XMEM_PAYLOAD_TYPE_e，用于部分平台要详细区分注入数据的种类
}IPANEL_VDEC_PARAM;

typedef enum
{
	IPANEL_VIDEO_STREAM_MPEG1,
	IPANEL_VIDEO_STREAM_MPEG2,
	IPANEL_VIDEO_STREAM_H264,
	IPANEL_VIDEO_STREAM_AVS,
	IPANEL_VIDEO_STREAM_WMV,
	IPANEL_VIDEO_STREAM_MPEG4,
    IPANEL_VIDEO_STREAM_VC1,
	IPANEL_VIDEO_STREAM_VC1SIMPLEMAIN,
	IPANEL_VIDEO_STREAM_DIVX311,
	IPANEL_VIDEO_STREAM_H263,
	IPANEL_VIDEO_STREAM_H263_SORENSON,
	IPANEL_VIDEO_STREAM_RV10,
	IPANEL_VIDEO_STREAM_RV20,
	IPANEL_VIDEO_STREAM_RV30,
	IPANEL_VIDEO_STREAM_RV40,
	IPANEL_VIDEO_STREAM_VP60,
	IPANEL_VIDEO_STREAM_VP61,
	IPANEL_VIDEO_STREAM_VP62,
	IPANEL_VIDEO_STREAM_MJPG,
	IPANEL_VIDEO_STREAM_MVC, 
	IPANEL_VIDEO_STREAM_UNDEFINED
}IPANEL_VIDEO_STREAM_TYPE_e;

typedef enum
{
	IPANEL_VDEC_CLEAR_NULL,         /*不清屏,保持屏幕显示内容*/
	IPANEL_VDEC_CLEAR_BLANK          /*清屏,使视频黑屏*/

}IPANEL_VDEC_CLEAR_MODE_e;

typedef enum
{
	IPANEL_VDEC_STOPPED,	//停止状态，没有任何输入输出/
	IPANEL_VDEC_STARTED,	//解码器已经处于工作状态，等待数据输入/
	IPANEL_VDEC_DECORDING,	//正常解码/
	IPANEL_VDEC_HUNGERING,	//视频数据被消耗完，解码器没有了数据输入/
	IPANEL_VDEC_PAUSED		//解码器处于暂停状态/
}IPANEL_VDEC_STATUS_e;

typedef enum //3D片源格式
{  
	IPANEL_VDEC_3D_NONE,			//2D
	IPANEL_VDEC_3D_SIDE_BY_SIDE, 	//Side By Side
	IPANEL_VDEC_3D_TOP_AND_BOTTOM,  //TopBottom
	IPANEL_VDEC_3D_FRAMEPACKING,	//FramePacking
	IPANEL_VDEC_3D_CHECKERBOARD	//Checkerboard
} IPANEL_VDEC_3D_FORMAT_e; 

typedef enum
{
    IPANEL_VDEC_SET_SOURCE       	= 1,
    IPANEL_VDEC_START            	= 2,
    IPANEL_VDEC_STOP             	= 3,
    IPANEL_VDEC_PAUSE            	= 4,
    IPANEL_VDEC_RESUME           	= 5,
    IPANEL_VDEC_CLEAR            	= 6,
    IPANEL_VDEC_SYNCHRONIZE      	= 7,
    IPANEL_VDEC_GET_BUFFER_RATE  	= 8,
    IPANEL_VDEC_PLAY_I_FRAME     	= 9,
    IPANEL_VDEC_STOP_I_FRAME     	= 10,
    IPANEL_VDEC_SET_STREAM_TYPE  	= 11,
    IPANEL_VDEC_SET_SYNC_OFFSET  	= 12,
    IPANEL_VDEC_GET_BUFFER       	= 13,
    IPANEL_VDEC_PUSH_STREAM      	= 14,
    IPANEL_VDEC_GET_BUFFER_CAP   	= 15,
    IPANEL_VDEC_SET_CONFIG_PARAMS 	= 16,
    IPANEL_VDEC_SET_RATE			= 17,
    IPANEL_VDEC_GET_CURRENT_DTS 	= 18,
    IPANEL_VDEC_GET_DEC_STATUS      = 19,
	IPANEL_VDEC_SET_DEVICE_OUTPUT   = 20,
	IPANEL_VDEC_SET_CODEC_TYPE		= 21,
	IPANEL_VDEC_SET_LOCAL_CONFIG	= 22,	
	IPANEL_VDEC_GET_RECV_FRAME_NUM	= 23,		//获取从解码器star开始到stop为止接收到的视频帧总个数
	IPANEL_VDEC_GET_BIT_RATE		= 24,		//获取解码码率	
	IPANEL_VDEC_GET_DEC_FRAME_CNT   = 25,
	IPANEL_VDEC_GET_FST_DEC_TIME    = 26,
	IPANEL_VDEC_GET_FST_DISPLAY_TIME = 27,
	IPANEL_VDEC_GET_FST_DISPLAY_PTS  = 28,
    IPANEL_VDEC_GET_CODEC_SUPPORT    = 29,
	IPANEL_VDEC_GET_3D_FORMAT        = 30,		//获取平台当前3D片源格式
	IPANEL_VDEC_SET_3D_FORMAT		 = 31,	
	IPANEL_VDEC_SET_FRAMERATE		 = 32,
	IPANEL_VDEC_GET_FRAMERATE		 = 33,
	IPANEL_VDEC_UNDEFINED
}IPANEL_VDEC_IOCTL_e;
    
/* video decode */
/*打开一个视频解码单元*/
UINT32_T ipanel_porting_vdec_open(VOID);

/*关闭指定的解码单元*/
INT32_T ipanel_porting_vdec_close(UINT32_T decoder);

/*对decoder进行一个操作，或者用于设置和获取decoder设备的参数和属性*/
INT32_T ipanel_porting_vdec_ioctl(UINT32_T decoder, IPANEL_VDEC_IOCTL_e op, VOID *arg);

#ifdef __cplusplus
}
#endif

#endif // _IPANEL_MIDDLEWARE_PORTING_VIDEO_DECODER_API_FUNCTOTYPE_H_

