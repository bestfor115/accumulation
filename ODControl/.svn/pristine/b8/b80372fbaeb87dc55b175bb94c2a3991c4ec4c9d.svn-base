/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:对接Android第三方播放器的API实现
*********************************************************************/
#include "ipanel_media_processor.h"
//#include "join_player.h"

/************************************************************************************************
 *宏定义
 ************************************************************************************************/

#define MEDIAPROCESSOR_MARK "[IVOD][MEDIAPROCESSOR]"

static int flag_fast_play = 0;
/************************************************************************************************
 *结构体定义
 ************************************************************************************************/

typedef struct
{
	void *player;         //底层播放器
	int   player_type;    //播放器类型

	int   flag_open:1;    //播放器是否打开
}mediaProcessor_t;


/************************************************************************************************
 *函数声明
 ************************************************************************************************/


/************************************************************************************************
 *函数定义
 ************************************************************************************************/

/*打开底层一个播放器*/
int ipanel_mediaProcessor_open(int type)
{
    #if 0
	mediaProcessor_t *me = NULL;

	//type主要用于指示创建的是ts播放器，还是其他类型播放器，
	me = (mediaProcessor_t*)calloc(1,sizeof(mediaProcessor_t));

	//INFO("%s[ipanel_mediaProcessor_open]start!!\n",MEDIAPROCESSOR_MARK);

	if(NULL == me)
		return (int)NULL;
	else
	{
		memset(me,0,sizeof(mediaProcessor_t));

		//join_player_init();
		me->player = join_player_get_instance();
		if(NULL == me->player)
		{
			//INFO("%s[ipanel_mediaProcessor_open]create mediaProcessor failed!!\n",MEDIAPROCESSOR_MARK);
		}
		else
		{
			me->player_type = type;
		}
	}
	return (int)me;
    #endif
    
    return 1;
}

#if 0

/*设置音视频播放参数*/
int ipanel_mediaProcessor_set_param(int handle,IPANEL_VDEC_PARAM *vParam,IPANEL_ADEC_PARAM *aParam)
{
	mediaProcessor_t *me = (mediaProcessor_t*)handle;
    int audio_type = 0,video_type = 0;

	if(NULL == me || NULL == vParam || NULL == aParam)
		return IPANEL_ERR;

	join_player_set_pid(me->player, JOIN_PLAYER_AUDIO, aParam->audio_pid);
	join_player_set_pid(me->player, JOIN_PLAYER_VIDEO, vParam->video_pid);

    switch(aParam->codec_id)
    {
    	case MEDIAPROCESSOR_AUDIO_TYPE_MPEG1:
            audio_type = AUDIO_STREAM_MPEG;
            break;
    	case MEDIAPROCESSOR_AUDIO_TYPE_MPEG2:
            audio_type = AUDIO_STREAM_MPEG;
            break;
    	case MEDIAPROCESSOR_AUDIO_TYPE_MP3:
            audio_type = AUDIO_STREAM_MP3;
            break;
    	case MEDIAPROCESSOR_AUDIO_TYPE_AAC_1:
            audio_type = AUDIO_STREAM_AAC_1;
            break;
        case MEDIAPROCESSOR_AUDIO_TYPE_AAC_2:
            audio_type = AUDIO_STREAM_AAC_2;
            break;
        case MEDIAPROCESSOR_AUDIO_TYPE_AAC_3:
            audio_type = AUDIO_STREAM_AAC_3;
            break;
    	case MEDIAPROCESSOR_AUDIO_TYPE_AC3_1:
            audio_type = AUDIO_STREAM_AC3_1;
            break;
    	case MEDIAPROCESSOR_AUDIO_TYPE_AC3_2:
            audio_type = AUDIO_STREAM_AC3_2;
            break;
    	case MEDIAPROCESSOR_AUDIO_TYPE_AC3_3:
            audio_type = AUDIO_STREAM_AC3_3;
            break;            
        case MEDIAPROCESSOR_AUDIO_TYPE_DTS:
            break;
    	case MEDIAPROCESSOR_AUDIO_TYPE_PCM:
            break;
    	case MEDIAPROCESSOR_AUDIO_TYPE_LPCMBLURAY:
            break;
        default:
            break;
       
    }

    switch(vParam->codec_id)
    {
    	case MEDIAPROCESSOR_VIDEO_TYPE_MPEG1:
            video_type = VIDEO_STREAM_MPEG1;
            break;
    	case MEDIAPROCESSOR_VIDEO_TYPE_MPEG2:
            video_type = VIDEO_STREAM_MPEG2;
            break;
    	case MEDIAPROCESSOR_VIDEO_TYPE_H264:
            video_type = VIDEO_STREAM_H264;
            break;
    	case MEDIAPROCESSOR_VIDEO_TYPE_AVS:
            video_type = VIDEO_STREAM_AVS;
            break;
    	case MEDIAPROCESSOR_VIDEO_TYPE_WMV:
            break;
    	case MEDIAPROCESSOR_VIDEO_TYPE_MPEG4:
            video_type = VIDEO_STREAM_MPEG4;
            break;
        default:
            break;
    }
	join_player_set_fmt(me->player, JOIN_PLAYER_AUDIO, audio_type);
	join_player_set_fmt(me->player, JOIN_PLAYER_VIDEO, video_type);
	
	return IPANEL_OK;
}

#else

/*设置音视频全部播放参数*/
int ipanel_mediaProcessor_set_param(int handle,IPANEL_VDEC_PARAM *vParam,IPANEL_ADEC_PARAM *aParam,int adec_param_len)
{
    #if 0
	mediaProcessor_t *me = (mediaProcessor_t*)handle;
    int audio_type = 0,video_type = 0,i = 0;
    JOIN_VIDEO_PARAM j_vparam[1] = {0};
    JOIN_AUDIO_PARAM j_aparam[16] = {0};

	if(NULL == me || NULL == vParam || NULL == aParam)
		return IPANEL_ERR;

    switch(vParam->codec_id)
    {
    	case MEDIAPROCESSOR_VIDEO_TYPE_MPEG1:
            video_type = VIDEO_STREAM_MPEG1;
            break;
    	case MEDIAPROCESSOR_VIDEO_TYPE_MPEG2:
            video_type = VIDEO_STREAM_MPEG2;
            break;
    	case MEDIAPROCESSOR_VIDEO_TYPE_H264:
            video_type = VIDEO_STREAM_H264;
            break;
    	case MEDIAPROCESSOR_VIDEO_TYPE_AVS:
            video_type = VIDEO_STREAM_AVS;
            break;
    	case MEDIAPROCESSOR_VIDEO_TYPE_WMV:
            break;
    	case MEDIAPROCESSOR_VIDEO_TYPE_MPEG4:
            video_type = VIDEO_STREAM_MPEG4;
            break;
        default:
            break;
    }

    j_vparam->vFmt = video_type;
    j_vparam->vPid = vParam->video_pid;
    j_vparam->nVideoWidth = vParam->width;
    j_vparam->nVideoHeight= vParam->height;
    
	join_player_set_param(me->player, JOIN_PLAYER_VIDEO, (void*)j_vparam,1);

    if(adec_param_len <=0)
        return IPANEL_OK;

    for(i=0;i<adec_param_len;i++)
    {
        audio_type = 0;
        
        switch(aParam[i].codec_id)
        {
        	case MEDIAPROCESSOR_AUDIO_TYPE_MPEG1:
                audio_type = AUDIO_STREAM_MPEG;
                break;
        	case MEDIAPROCESSOR_AUDIO_TYPE_MPEG2:
                audio_type = AUDIO_STREAM_MPEG;
                break;
        	case MEDIAPROCESSOR_AUDIO_TYPE_MP3:
                audio_type = AUDIO_STREAM_MP3;
                break;
        	case MEDIAPROCESSOR_AUDIO_TYPE_AAC_1:
                audio_type = AUDIO_STREAM_AAC_1;
                break;
            case MEDIAPROCESSOR_AUDIO_TYPE_AAC_2:
                audio_type = AUDIO_STREAM_AAC_2;
                break;
            case MEDIAPROCESSOR_AUDIO_TYPE_AAC_3:
                audio_type = AUDIO_STREAM_AAC_3;
                break;
        	case MEDIAPROCESSOR_AUDIO_TYPE_AC3_1:
                audio_type = AUDIO_STREAM_AC3_1;
                break;
        	case MEDIAPROCESSOR_AUDIO_TYPE_AC3_2:
                audio_type = AUDIO_STREAM_AC3_2;
                break;
        	case MEDIAPROCESSOR_AUDIO_TYPE_AC3_3:
                audio_type = AUDIO_STREAM_AC3_3;
                break;            
            case MEDIAPROCESSOR_AUDIO_TYPE_DTS:
                break;
        	case MEDIAPROCESSOR_AUDIO_TYPE_PCM:
                break;
        	case MEDIAPROCESSOR_AUDIO_TYPE_LPCMBLURAY:
                break;
            default:
                break;
           
        }
        j_aparam[i].afmt = audio_type;
        j_aparam[i].aPid = aParam[i].audio_pid;
    }

	join_player_set_param(me->player, JOIN_PLAYER_AUDIO, (void*)j_aparam,adec_param_len);

    #endif
    
	return IPANEL_OK;    
}

#endif

/*播放视频*/
int ipanel_mediaProcessor_play(int handle)
{
    #if 0
	mediaProcessor_t *me = (mediaProcessor_t*)handle;

	if(NULL == me)
		return IPANEL_ERR;
	
	me->flag_open = 1;
	
	return join_player_start(me->player);
    #endif
    
    return 1;
}

/*暂停播放*/
int ipanel_mediaProcessor_pause(int handle)
{
    #if 0
	mediaProcessor_t *me = (mediaProcessor_t*)handle;

	if(NULL == me)
		return IPANEL_ERR;	
	
	return join_player_pause(me->player);
    #endif
    
    return 1;
}

/*恢复播放*/
int ipanel_mediaProcessor_resume(int handle)
{
    #if 0
	mediaProcessor_t *me = (mediaProcessor_t*)handle;

	if(NULL == me)
		return IPANEL_ERR;	
	
	return join_player_resume(me->player);
    #endif
    
    return 1;
}

/*seek播放*/
int ipanel_mediaProcessor_seek(int handle)
{
    #if 0
	mediaProcessor_t *me = (mediaProcessor_t*)handle;

	if(NULL == me)
		return IPANEL_ERR;	
	if(flag_fast_play ==1){
		flag_fast_play = 0;
		join_player_set_rate(me->player,1);
	}
    return join_player_clear_cache(me->player);
    #endif
    
    return 1;
}

/*快进快退播放*/
int ipanel_mediaProcessor_fast(int handle,int rate)
{
    #if 0
	int ret = 0;
	mediaProcessor_t *me = (mediaProcessor_t*)handle;

	if(NULL == me)
		return IPANEL_ERR;
    
	if(flag_fast_play == 0){
		flag_fast_play =1;
		ret = join_player_set_rate(me->player,rate);
	}
	return ret;
    #endif
    
    return 1;
}

/*快进快退转正常播放*/
int ipanel_mediaProcessor_stopFast(int handle)
{
    #if 0
	int ret = 0;
	mediaProcessor_t *me = (mediaProcessor_t*)handle;

	if(NULL == me)
		return IPANEL_ERR;	
	
	if(flag_fast_play == 1){
		flag_fast_play =0;
		join_player_set_rate(me->player,1);
	}
	return join_player_clear_cache(me->player);
    #endif
    
    return 1;
}

/*停止播放*/
int ipanel_mediaProcessor_stop(int handle,int value)
{
    #if 0
    //value为关闭播放器是否清除最后一帧数据，0表示不清楚，1表示清除
	mediaProcessor_t *me = (mediaProcessor_t*)handle;

	if(NULL == me)
		return IPANEL_ERR;	

	me->flag_open = 0;
	if(flag_fast_play == 1){
		flag_fast_play =0;
		join_player_set_rate(me->player,1);
	}
	//TODO
	return join_player_stop(me->player, value);
    #endif
    
    return 1;
}

/*关闭播放器*/
int ipanel_mediaProcessor_close(int handle)
{
    #if 0
	mediaProcessor_t *me = (mediaProcessor_t*)handle;

	if(NULL == me)
		return IPANEL_ERR;	

	if(me->flag_open)
	{
		ipanel_mediaProcessor_stop(handle,1);
		me->flag_open = 0;
	}

	//join_player_finalize();

	memset(me,0,sizeof(mediaProcessor_t));

	free(me);
	me = NULL;
    #endif
    
	return IPANEL_OK;
	
}

/*向底层解码器注入数据流*/
int ipanel_mediaProcessor_push_stream(int handle,unsigned char* buffer,int len)
{
    #if 0
	int ret = 0;
	mediaProcessor_t *me = (mediaProcessor_t*)handle;

	if(NULL == me)
		return IPANEL_ERR;	
	
	ret = join_player_push_data(me->player,buffer,len);

	if(0 == ret)
		return IPANEL_ERR;
	else
		return IPANEL_OK;
    #endif
    
    return IPANEL_OK;
}


/*切换音轨信息*/
int ipanel_mediaProcessor_switch_AudioTrack(int handle, int pid)
{
    #if 0
	mediaProcessor_t *me = (mediaProcessor_t*)handle;
	if(NULL == me)
		return IPANEL_ERR;	
    
    join_player_switchAudioTrack(me->player, pid);
    #endif
    
    return IPANEL_OK;
}


/*获取参数*/
int ipanel_mediaProcessor_get_property(int handle,int prop,void *value)
{
	return IPANEL_OK;
}

/*设置参数*/
int ipanel_mediaProcessor_set_property(int handle,int prop,int value)
{
    #if 0
	int ret = IPANEL_OK;
	mediaProcessor_t *me = (mediaProcessor_t*)handle;

	if(NULL == me)
		return IPANEL_ERR;	
	
    switch(prop)
    {
        case MEDIAPROCESSOR_PROP_SET_AUDIOBALANCE:
            {
                ret = join_player_set_channel_mode(me->player, value);
                break;
            }
        case MEDIAPROCESSOR_PROP_SET_MUTE:
            {
                ret = join_player_set_mute(me->player, value);
                break;
            }
        case MEDIAPROCESSOR_PROP_SET_VOLUME:
            {
                ret = join_player_set_volume(me->player, value);
                break;
            }
        case MEDIAPROCESSOR_PROP_SET_VIDEOWINDOW:
            {
                mediaProcessor_rect *t = (mediaProcessor_rect*)value;
                if(t)
                {
                    ret = join_player_set_bounds(me->player, t->x, t->y, t->w, t->h);
                }
                break;
            }
        default:
            break;
    }

    return ret;
    #endif
    
    return IPANEL_OK;
}

/***********************************End of file**********************************************/


