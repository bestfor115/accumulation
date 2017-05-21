/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:PLAYER管理
*********************************************************************/
#include <netinet/in.h>
#include "iplayer_ts_player.h"
#include "iplayer_ts_parse.h"



/************************************************************************************************
 *宏定义
 ************************************************************************************************/

#if defined(HAVE_SUPPORT_HD)
#define USE_TS_BUFFER
#endif

#define TS_IPLAYER_PACKET_LEN		188
#define TS_IPLAYER_BUFFER_MEM_SIZE  (4*1024*1024)
#define TS_IPLAYER_DATA_BLOCK_NUM   3000


#define	MAX_TS_IPLAYER_BUFFER_SIZE	(650*188) // 120K


#ifdef WIN32
#define TS_IPLAYER_TO_FILE
#endif

#define TS_IPLAYER_MARK "[IVOD][TS_IPLAYER]"

#define TS_GET_INT16(value,buf) \
	do							\
	{							\
		unsigned short temp = 0;		 \
		memcpy((unsigned char *)&temp,(unsigned char *)buf,2); \
		value = ntohs(temp);			\
	}while(0)

#define TS_IPLAYER_LOCK(me) if(me) ipanel_porting_sem_wait(me->sem, IPANEL_WAIT_FOREVER)
#define TS_IPLAYER_UNLOCK(me) if(me) ipanel_porting_sem_release(me->sem)

/************************************************************************************************
 *结构体定义
 ************************************************************************************************/
typedef struct rtp_packet_info_s
{
    int        rtp_head_length;              //rtp包的头部长度
	int        last_recv_rtp_seq;	         //接收到的上一个rtp seq
	int        last_push_rtp_seq;            //上一个push的rtp
	int        last_continuity_counter;      //上一个continuity counter

    int        lost_rtp_packet_num;          //丢失的rtp包总数
}rtp_packet_info;

/*字幕block,注意需要与中间件的block保持一致*/
typedef struct subtitle_block_s
{
	unsigned int size;
	unsigned int type;
	unsigned int time;
	unsigned int pic_type;
    /*其实主要是字幕使用，但是音视频应该也可以得出，
    音视频部分需要验证，或可用于音视频同步*/
    unsigned int duration; 
	IPANEL_UINT64_T start_pos;//现在都还没实现
	IPANEL_UINT64_T end_pos;
    unsigned char *buf;
}subtitle_block_t;

struct ts_iplayer_s
{
    unsigned int     player_handle;           //与ts播放器关联的player句柄
    unsigned int     sem;                     //ts player互斥量
    unsigned int     av_handle;               //创建的底层播放器句柄
    imem_mgr_t        *mem_mgr;               //播放器对应的缓存
    imem_node_list_t  *mem_list;              //播放器缓存对应的数据链表
    imem_node_t       *mem_node;              //播放器数据块，用于取数据推流     
    iplayer_mode_e    play_mode;              //播放器类型 IP TS或QAM TS
    iplayer_cbf      cbf;                     //播放器mgr设置的回调函数

    dvb_info_t       dvb_info[1];             //主要是与流相关的频点之类的信息
    rtp_packet_info  rtp_info[1];             //rtp包信息

    ts_stream_info   stream_info[1];          //ts流音视频信息
    int              parse_pmt_index;         //解析PAT\PMT的次数
    unsigned int     parse_pts_time;          //解析PTS的起始时间

	unsigned char	 temp_packet[MAX_TS_IPLAYER_BUFFER_SIZE * 2];   //用于搜索I帧的buffer
	int  			 temp_packet_len;                               //搜索I帧的数据量
    
    unsigned char    pes_buffer[2048];                      //pes数据缓存buffer
    int              pes_buffer_len;                        //pes数据长度

    
    int          rtp_seq_filter;          //过滤rtp数据包的基准seq
    int          speed;                   //播放速率
    int          current_player_op;       //当前播放执行的播放状态

    unsigned int last_push_stream_time;   //最后一次推流时间
    
    unsigned int flag_used:1;             //指示播放器是否在使用
    unsigned int flag_open:1;             //ts播放器是否打开
    unsigned int flag_av_open:1;          //底层解码器是否打开
    unsigned int flag_decoder_success:1;  //ts播放器解析音视频信息是否成功
    unsigned int flag_push_stream:1;      //ts播放器是否可以推流
    unsigned int flag_paused:1;           //ts播放器是否暂停
    unsigned int flag_end:1;              //ts播放器是否播放结束
    unsigned int flag_first_play:1;       //ts播放器是否第一次调用play
	unsigned int flag_find_I_frame:1;     //ts播放器是否寻找I帧
	unsigned int flag_send_error:1;       //是否发送错误
    /*--------下面是一些特殊宏控制的变量，必须注明含义----------------------*/

#ifdef TS_IPLAYER_TO_FILE
    unsigned int ts_file;                 //保存本地ts流的文件句柄
#endif


    unsigned int qos_bytes_out;           //每秒推送的数据量，用于QOS计算
    unsigned int qos_push_data_time;      //上次推流时间，用于QOS计算
    unsigned int qos_data_full_time;      //上次数据缓冲满时间，用于qos计算
    unsigned int qos_data_empty_time;     //上次数据不足时间，用于qos计算
    
    int subt_pid;                         //当前的subt_pid
    unsigned char subt_buffer[7*188];     //用来保存subtitle数据
};


/************************************************************************************************
 *函数声明
 ************************************************************************************************/

static int
ts_iplayer_close(ts_iplayer_t* ts_player);

static int
ts_iplayer_stop(ts_iplayer_t* ts_player, int keep_last_frame);

static int
ts_iplayer_pause(ts_iplayer_t* ts_player);

static int
ts_iplayer_resume(ts_iplayer_t* ts_player);

static int
ts_iplayer_notify(ts_iplayer_t* ts_player, int op, int speed);

static int
ts_iplayer_play(ts_iplayer_t* ts_player, int op, active_info_t *info);

static imem_node_t*
ts_iplayer_get_unused_block(ts_iplayer_t* ts_player, int size);

static int
ts_iplayer_append_data(ts_iplayer_t* ts_player, int value);

static int
ts_iplayer_parse_stream_info(ts_iplayer_t* ts_player);

static int
ts_iplayer_push_stream(ts_iplayer_t* ts_player);

static int 
ts_iplayer_mpeg2ts_search_I_frame(ts_iplayer_t* ts_player,unsigned char *buf,int len,int ifm_flag,int *new_pos);

static int
ts_iplayer_clear_data(ts_iplayer_t* ts_player);

static int
ts_iplayer_set_prop(ts_iplayer_t* ts_player, int op, int value);

static int
ts_iplayer_get_prop(ts_iplayer_t* ts_player, int op, int value);

static int 
ts_iplayer_write_ts_file(ts_iplayer_t *ts_player,unsigned char *buffer,int len);

static int
ts_iplayer_data_check(ts_iplayer_t *ts_player);

static void 
ts_iplayer_calculate_rtp(ts_iplayer_t *ts_player, int value);

/************************************************************************************************
 *函数定义
 ************************************************************************************************/

/*打开ts 播放器*/
int ts_iplayer_open(iplayer_t *handle, iplayer_info_t *info, iplayer_cbf cbf)
{
    ts_iplayer_t *me = NULL;

    INFO("%s[ts_iplayer_open]ts player open called\n", TS_IPLAYER_MARK);
    //申请一块ts player内存空间
    me = (ts_iplayer_t*)calloc(1, sizeof(ts_iplayer_t));
    if(NULL == me)
    {
        INFO("%s[ts_iplayer_open] ts player calloc memory failed\n", TS_IPLAYER_MARK);
        goto TS_PLAYER_FAILED;
    } 

    memset(me,0,sizeof(ts_iplayer_t));

    
    //创建信号量
    me->sem = ipanel_porting_sem_create("TPLY", 1, IPANEL_TASK_WAIT_FIFO);
    if(0 == me->sem)
    {
        INFO("%s[ts_iplayer_open] create sem failed\n", TS_IPLAYER_MARK);
        goto TS_PLAYER_FAILED;        
    }
    

    //创建播放器对应的缓存
    me->mem_mgr = imem_mgr_new(TS_IPLAYER_BUFFER_MEM_SIZE);
    if(NULL == me->mem_mgr)
    {
        INFO("%s[ts_iplayer_open] create mem mgr failed\n", TS_IPLAYER_MARK);
        goto TS_PLAYER_FAILED;        
    }
    

    //创建播放器对应的数据链表
    me->mem_list = imem_node_list_new(me->mem_mgr, TS_IPLAYER_DATA_BLOCK_NUM);
    if(NULL == me->mem_list)
    {
        INFO("%s[ts_iplayer_open] create mem list failed\n", TS_IPLAYER_MARK);
        goto TS_PLAYER_FAILED;        
    }

    //获取一个音视频播放器handle
	me->av_handle = ipanel_mediaProcessor_open(MEDIAPROCESSOR_TYPE_TS);

    //按顺序初始化，便于检查代码，后续添加其他变量初始化也请按顺序
    me->player_handle = (unsigned int)handle;
    me->play_mode = (iplayer_mode_e)info->mode;
    me->cbf = cbf;
    memcpy(me->dvb_info,&info->dvb_info,sizeof(dvb_info_t));
	me->rtp_info->last_recv_rtp_seq = -1;
	me->rtp_info->last_push_rtp_seq = -1;
	me->rtp_info->last_continuity_counter = -1;
    me->stream_info->pmt_pid = -1;
    me->rtp_seq_filter = -1;
    me->speed = 1;
    me->flag_used = 1;
    me->flag_open = 1;
    me->flag_first_play = 1;
    
    INFO("%s[ts_iplayer_open]ts player open success!!\n", TS_IPLAYER_MARK);        

    return (int)me;
    
TS_PLAYER_FAILED:
    if(me)
    {
        if(me->sem > 0)
        {
            ipanel_porting_sem_destroy(me->sem);
            me->sem = 0;
        }

        if(me->mem_list)
        {
            imem_node_list_destory(me->mem_list);
            me->mem_list = NULL;
        }

        if(me->mem_mgr)
        {
            imem_mgr_destory(me->mem_mgr);
            me->mem_mgr = NULL;
        }        
        
        free(me);
        me = NULL;
    }
    
    INFO("%s[ts_iplayer_open]ts player open failed!!\n", TS_IPLAYER_MARK);
    
    return 0;
}


/*ts player操作处理函数*/
int ts_iplayer_proc(int player_id, unsigned int op, int p1,int p2)
{
    int ret = IPANEL_ERR;
    ts_iplayer_t *me = (ts_iplayer_t*)player_id;

    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    switch(op)
    {
        case IPLAYER_OP_OPEN:
            {
                break;
            }
        case IPLAYER_OP_PLAY:
            {
                ret = ts_iplayer_play(me, op, (active_info_t*)p1);
                break;
            }
        case IPLAYER_OP_SEEK:
            {
                ret = ts_iplayer_play(me, op, (active_info_t*)p1);
                break;
            }            
        case IPLAYER_OP_PAUSE:
            {
                ret = ts_iplayer_pause(me);
                break;
            }            
        case IPLAYER_OP_RESUME:
            {
                ret = ts_iplayer_resume(me);
                break;
            }            
        case IPLAYER_OP_FWBW:
            {
                ret = ts_iplayer_play(me, op, (active_info_t*)p1);
                break;
            }            
        case IPLAYER_OP_NOTIFY:
            {
                ret = ts_iplayer_notify(me, p1, p2);
                break;
            }            
        case IPLAYER_OP_STOP:
            {
                ret = ts_iplayer_stop(me, p1);
                break;
            }            
        case IPLAYER_OP_CLOSE:
            {
                ret = ts_iplayer_close(me);
                break;
            }
        case IPLAYER_OP_SET_PROP:
            {
                ret = ts_iplayer_set_prop(me, p1, p2);
                break;
            }
        case IPLAYER_OP_GET_PROP:
            {
                ret = ts_iplayer_get_prop(me, p1, p2);
                break;
            }
        case IPLAYER_OP_GET_BLOCK:
            {
                ret = (int)ts_iplayer_get_unused_block(me, p1);
                break;
            }
        case IPLAYER_OP_APPEND_DATA:
            {
                ts_iplayer_calculate_rtp(me, p1);
                ret = ts_iplayer_append_data(me, p1);
                break;
            }
        case IPLAYER_OP_TIME:
            {
                /*消息在timer里面发,是个独立线程*/
                ts_iplayer_data_check(me);
                
                if(!me->flag_decoder_success)
                    ret = ts_iplayer_parse_stream_info(me);
                else
                    ret = ts_iplayer_push_stream(me);
                break;
            }            
        default:
            {
                break;
            }            
    }
    return ret;
}



/*关闭ts播放器并销毁*/
static int ts_iplayer_close(ts_iplayer_t* ts_player)
{
    int ret = IPANEL_ERR;
    ts_iplayer_t *me = ts_player;

    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    INFO("%s[ts_iplayer_close]ts player close called\n", TS_IPLAYER_MARK);

    me->flag_push_stream = 0;   //首先停止推流
    me->flag_open        = 0;   //其实改变play状态，防止再接收数据

    //关闭底层播放器
    if(me->flag_av_open)
    {
        ipanel_mediaProcessor_stop((int)me->av_handle,1);
        ipanel_mediaProcessor_close((int)me->av_handle);
        me->flag_av_open = 0;
        me->av_handle = 0;
    }

    TS_IPLAYER_LOCK(me);

    if(me->mem_list)
    {
        imem_node_list_destory(me->mem_list);
        me->mem_list = NULL;
    }

    if(me->mem_mgr)
    {
        imem_mgr_destory(me->mem_mgr);
        me->mem_mgr = NULL;
    } 


    TS_IPLAYER_UNLOCK(me);

    if(me->sem > 0)
    {
        ipanel_porting_sem_destroy(me->sem);
        me->sem = 0;
    }

    if(me)
    {
        memset(me,0,sizeof(ts_iplayer_t));
        free(me);
        me = NULL;
    }
    INFO("%s[ts_iplayer_close]ts player close success\n", TS_IPLAYER_MARK);
    
    return IPANEL_OK;
}



/*停止ts播放器,不会销毁ts播放器*/
static int ts_iplayer_stop(ts_iplayer_t* ts_player, int keep_last_frame)
{
    int ret = IPANEL_ERR;
    ts_iplayer_t *me = ts_player;

    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    me->flag_push_stream = 0;   //首先停止推流

    //关闭底层播放器
    if(me->flag_av_open)
    {
        ipanel_mediaProcessor_stop((int)me->av_handle,1);
        ipanel_mediaProcessor_close((int)me->av_handle);
        me->flag_av_open = 0;
        me->av_handle = 0;
    }

    //只清空缓存数据，不销毁缓存buffer
    ts_iplayer_clear_data(me);
    me->mem_node = NULL;

    /************************************************************************
     *下面状态量初始化请按顺序，便于查阅，除了不能初始化和规定初始化顺序的
     ************************************************************************/
    me->flag_paused = 0;
    me->flag_end    = 0;

    //这里初始化为1,如果播放器复用时会用到
    me->flag_first_play = 1;
    me->flag_find_I_frame = 0;

    //flag_decoder_success必须放在最后初始化，涉及时机问题，其他状态初始化放在前面
    me->flag_decoder_success = 0;

    return IPANEL_OK;
}



/*暂停ts播放器*/
static int ts_iplayer_pause(ts_iplayer_t* ts_player)
{
    int ret = IPANEL_ERR;
    ts_iplayer_t *me = ts_player;

    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    me->flag_push_stream = 0;

    //暂停底层解码器
    if(me->flag_av_open)
    {
        ipanel_mediaProcessor_pause((int)me->av_handle);
    }

    me->flag_paused = 1;

    return IPANEL_OK;
}



/*恢复ts播放器播放*/
static int ts_iplayer_resume(ts_iplayer_t* ts_player)
{
    int ret = IPANEL_ERR;
    ts_iplayer_t *me = ts_player;

    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    me->flag_push_stream = 1;

    //恢复底层解码器
	if( me->flag_av_open && me->flag_paused )
    {
        ipanel_mediaProcessor_resume((int)me->av_handle);
	}

    //恢复播放不需要根据seq过滤数据
    me->rtp_seq_filter = -1;

     //刷新推流时间信息，避免把暂停的时间算进去了，导致发出没有数据流的消息
    me->last_push_stream_time = time_ms();
    me->qos_data_empty_time = me->last_push_stream_time;
   
    me->flag_paused = 0; 
   
    return IPANEL_OK;
}



/*通知播放器进行一些操作,在play命令之前调用*/
static int ts_iplayer_notify(ts_iplayer_t* ts_player, int op, int speed)
{
    int ret = IPANEL_OK;
    ts_iplayer_t *me = ts_player;

    FAILED_RETURNX(NULL == me, IPANEL_ERR);
    
    switch(op)
    {
        case IPLAYER_OP_PLAY:
        case IPLAYER_OP_FWBW:
        case IPLAYER_OP_SEEK:
            {
                //暂停推流
                me->flag_push_stream = 0;  

                if(!me->flag_first_play)
                {
                    //清空中间件缓存
                    ret = ts_iplayer_clear_data(me);
                }

                //清空底层解码器buffer

                me->current_player_op = op;
                me->speed = speed;                
                break;
            }
        case IPLAYER_OP_RESUME:
            {
                me->current_player_op = op;
                me->speed = speed;                
                break;
            }
        default:
            {
                break;
            }
    }

    return ret;
}



/*开始播放*/
static int ts_iplayer_play(ts_iplayer_t* ts_player, int op, active_info_t *info)
{
    int ret = IPANEL_ERR;
    ts_iplayer_t *me = ts_player;

    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    INFO("%s[ts_iplayer_play] start play op = %d\n", TS_IPLAYER_MARK,op);

    if(me->rtp_seq_filter == -1 || me->rtp_info->rtp_head_length == 0)
    {
        if(!me->flag_first_play)
        {        
            //如果没有过滤RTP数据包seq，或者前端下发的是纯ts数据，就寻找I帧
            //me->flag_find_I_frame = 1;
            memset(me->temp_packet,0,sizeof(me->temp_packet));
            me->temp_packet_len = 0;
            memset(me->pes_buffer,0,sizeof(me->pes_buffer));
            me->pes_buffer_len = 0;


            //寻找I帧还需要将play与play应答之间的数据清空，否则可能寻找的I帧不是新数据
            ts_iplayer_clear_data(me);
            me->mem_node = NULL;
                       
        }
    }

    //如果处于暂停状态，就恢复解码器
	if( me->flag_av_open && me->flag_paused )
    {
        ipanel_mediaProcessor_resume((int)me->av_handle);
        me->flag_paused = 0;
	}    

    //快进快退，正常播放，seek，快进快退转正常播放，都调用的是不一样的接口，这里需要分情况处理
    switch(op)
    {
        case IPLAYER_OP_PLAY:
            {
                //停止快进快退转入正常播放
                if(!me->flag_first_play)
                    ipanel_mediaProcessor_stopFast((int)me->av_handle);
            }
            break;
        case IPLAYER_OP_SEEK:
            {
                //seek播放
                ipanel_mediaProcessor_seek((int)me->av_handle);
            }
            break;
        case IPLAYER_OP_FWBW:
            {
                //快进快退播放
                ipanel_mediaProcessor_fast((int)me->av_handle,me->speed);
            }
            break;
        default:
            break;
    }
    
    me->flag_push_stream = 1;
    me->flag_first_play = 0;
    me->last_push_stream_time = time_ms();
    
    return IPANEL_OK;
}



/*设置播放器的相关参数*/
static int ts_iplayer_set_prop(ts_iplayer_t* ts_player, int op, int value)
{
    int ret = IPANEL_ERR;
    ts_iplayer_t *me = ts_player;

    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    switch(op)
    {
        case IPLAYER_PARAM_RTP_SEQ:
            {
                me->rtp_seq_filter = value;
                INFO("%s[ts_iplayer_set_prop]IPLAYER_PARAM_RTP_SEQ value = %d\n", TS_IPLAYER_MARK,value);
                break;
            }
        case IPLAYER_PARAM_MEDIA_INFO:
            {
                break;
            }
        case IPLAYER_PARAM_AUDIO_CHANGE_TRACK_ID:
            {
                INFO("%s[ts_iplayer_set_prop]IPLAYER_PARAM_AUDIO_CHANGE_TRACK_ID value = %d\n", TS_IPLAYER_MARK,value);
                if(value >= 0 
                    && value < me->stream_info->avinfo->audio_channel_num
                    && value != me->stream_info->avinfo->audio_current_channel)
                { 
                    IPANEL_ADEC_PARAM aud_cfg[1] ={0};
                    IPANEL_VDEC_PARAM vid_cfg ={0};
                    int audio_num = 0;
                    
                    //暂停推流
                    me->flag_push_stream = 0;  

                    //关闭播放器
                    ipanel_mediaProcessor_stop((int)me->av_handle,0);
                    
                    //重新设置参数，打开播放

                    if(me->stream_info->avinfo->audinfo[value].audio_pid > 0
                        && me->stream_info->avinfo->audinfo[value].audio_pid < 0x1fff)
                    {
                        aud_cfg[audio_num].valid = 1;
                        aud_cfg[audio_num].audio_pid = me->stream_info->avinfo->audinfo[value].audio_pid;
                        aud_cfg[audio_num].codec_id = me->stream_info->avinfo->audinfo[value].codec_id;
                        aud_cfg[audio_num].stream_type = IPANEL_XMEM_TS;
                        audio_num++;
                    }
                    if(me->stream_info->avinfo->vidinfo[0].video_pid > 0 
                        && me->stream_info->avinfo->vidinfo[0].video_pid < 0x1fff)
                    {
                        vid_cfg.valid = 1;
                        vid_cfg.video_pid = me->stream_info->avinfo->vidinfo[0].video_pid;
                        vid_cfg.codec_id = me->stream_info->avinfo->vidinfo[0].codec_id;
                        vid_cfg.width = me->stream_info->avinfo->vidinfo[0].width;
                        vid_cfg.height = me->stream_info->avinfo->vidinfo[0].height;
                        vid_cfg.stream_type = IPANEL_XMEM_TS;
                    }          

                    
                    INFO("%s[ts_iplayer_set_prop]mediaProcessor open start time=%u.\n", TS_IPLAYER_MARK, time_ms());
                    ret = ipanel_mediaProcessor_set_param((int)me->av_handle, &vid_cfg, aud_cfg, audio_num);
    				if( ret == IPANEL_OK ) 
                    {
                        ret = ipanel_mediaProcessor_play((int)me->av_handle);
                        if(ret == IPANEL_OK)
                        {
                            //重新开始推流
                            me->flag_push_stream = 1;                            
                            INFO("%s[ts_iplayer_set_prop]mediaProcessor open success time=%u.\n", TS_IPLAYER_MARK, time_ms());
                        }
                        else
                        {
                            INFO("%s[ts_iplayer_set_prop]mediaProcessor open faied time=%u.\n", TS_IPLAYER_MARK, time_ms());
                        }
    				}
                    else 
                    {
                        INFO("%s[parse_stream_info]mediaProcessor set param faied time=%u.\n", TS_IPLAYER_MARK, time_ms());
    				}  
 
                    me->stream_info->avinfo->audio_current_channel = value;
                }
                break;
            }
        case IPLAYER_PARAM_AUDIO_MODE:
            {
                INFO("%s[ts_iplayer_set_prop]IPLAYER_PARAM_AUDIO_MODE value = %d\n", TS_IPLAYER_MARK,value);
                if(me->av_handle)
                {
                    ret = ipanel_mediaProcessor_set_property((int)me->av_handle, MEDIAPROCESSOR_PROP_SET_AUDIOBALANCE, value);
                }                
                break;
            }
        case IPLAYER_PARAM_MUTE:
            {
                INFO("%s[ts_iplayer_set_prop]IPLAYER_PARAM_MUTE value = %d\n", TS_IPLAYER_MARK,value);
                if(me->av_handle)
                {
                    ret = ipanel_mediaProcessor_set_property((int)me->av_handle, MEDIAPROCESSOR_PROP_SET_MUTE, value);
                }
                break;
            }        
        case IPLAYER_PARAM_VOLUME:
            {
                INFO("%s[ts_iplayer_set_prop]IPLAYER_PARAM_VOLUME value = %d\n", TS_IPLAYER_MARK,value);
                if(me->av_handle)
                {
                    ret = ipanel_mediaProcessor_set_property((int)me->av_handle, MEDIAPROCESSOR_PROP_SET_VOLUME, value);
                }
                break;
            }
        case IPLAYER_PARAM_WIN_LOCATION:
            {
                iplayer_rect *p = (iplayer_rect*)value;

                if(NULL == p)
                    return IPANEL_ERR;
                
                INFO("%s[ts_iplayer_set_prop]IPLAYER_PARAM_WIN_LOCATION x=%d,y=%d,w=%d,h=%d\n", TS_IPLAYER_MARK,p->x,p->y,p->w,p->h);
                if(me->av_handle)
                {
                    mediaProcessor_rect location[1] ={0};

                    location->x = p->x;
                    location->y = p->y;
                    location->w = p->w;
                    location->h = p->h;
                    
                    ret = ipanel_mediaProcessor_set_property((int)me->av_handle, MEDIAPROCESSOR_PROP_SET_VIDEOWINDOW, (int)location);
                }
                break;
            }
        case IPLAYER_PARAM_SUBTITLE_SWITCH_LANG:
            {
                INFO("%s[ts_iplayer_set_prop]IPLAYER_PARAM_SUBTITLE_SWITCH_LANG value = %d\n", TS_IPLAYER_MARK,value);
                if(value >= 0 && value < me->stream_info->avinfo->subt_channel_num)
                {
                    me->stream_info->avinfo->subt_current_channel = value;
                    if(me->stream_info->avinfo->subtinfo[value].subt_pid > 0)
                    {
                        me->subt_pid = me->stream_info->avinfo->subtinfo[value].subt_pid;
                    }
                    ret = IPANEL_OK;
                }
                break;
            }
        default:
            {
                break;
            }
    }

    return ret;
}



/*获取播放器的相关参数*/
static int ts_iplayer_get_prop(ts_iplayer_t* ts_player, int op, int value)
{
    int ret = IPANEL_ERR;
    ts_iplayer_t *me = ts_player;

    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    switch(op)
    {

        case IPLAYER_PARAM_MEDIA_INFO:
            {
                break;
            }
        case IPLAYER_PARAM_AUDIO_TRACKS:
            {
                if(value)
                {
                    memcpy((IPANEL_MEDIA_AUDIO_TRACKS*)value,me->stream_info->audio_track,sizeof(IPANEL_MEDIA_AUDIO_TRACKS));
                    ret = IPANEL_OK;
                }
                break;
            }            
        case IPLAYER_PARAM_AUDIO_TRACK_ID:
            {
                if(value)
                {
                    *(int*)value = me->stream_info->avinfo->audio_current_channel;
                    ret = IPANEL_OK;
                }
                break;
            }
        case IPLAYER_PARAM_VIDEO_PTS:
            {
                if(value)
                    *((unsigned int*)value) = me->stream_info->current_pts;
                break;
            }
        case IPLAYER_PARAM_VOD_BUFFER_SIZE:
            {
                break;
            }
        case IPLAYER_PARAM_VOD_BUFFER_RATE:
            {
                break;
            }
        case IPLAYER_PARAM_PORTING_VIDEO_BUFFER_SIZE:
            {
                break;
            }
        case IPLAYER_PARAM_PORTING_VIDEO_BUFFER_RATE:
            {
                break;
            }
        case IPLAYER_PARAM_PORTING_AUDIO_BUFFER_SIZE:
            {
                break;
            }
        case IPLAYER_PARAM_PORTING_AUDIO_BUFFER_RATE:
            {
                break;
            }
        case IPLAYER_PARAM_SUBTITLE_GET_LANGUAGES:
            {
                if(value)
                {
                    memcpy((IPANEL_MEDIA_SUBT_INFO*)value,me->stream_info->subt_track,sizeof(IPANEL_MEDIA_SUBT_INFO));
                    ret = IPANEL_OK;
                }
                break;
            }
        case IPLAYER_PARAM_SUBTITLE_GET_CURR_LANG:
            {
                if(value)
                {
                    int num = me->stream_info->avinfo->subt_current_channel;
                    if(num >= 0)
                    {
                        strcpy((char*)value,me->stream_info->subt_track->subts[num].langdesc);
                        ret = IPANEL_OK;
                    }
                }
                break;
            }        
        default:
            {
                break;
            }
    }
    
    return ret;
}



/*获取播放器对应的一块内存空间*/
static imem_node_t* ts_iplayer_get_unused_block(ts_iplayer_t* ts_player, int size)
{
    ts_iplayer_t *me = ts_player;

    FAILED_RETURNX(NULL == me || size < 0, NULL);

    return imem_mgr_get_unused_block(me->mem_mgr, size);

}

/*统计接收到的RTP包数据*/
static void ts_iplayer_calculate_rtp(ts_iplayer_t* ts_player, int value)
{
    int ret = IPANEL_ERR;
    ts_iplayer_t *me = ts_player;
    imem_node_t *node = (imem_node_t*)value;
    char *ptr = NULL;
    unsigned short current_seq = 0;	
    
    FAILED_RETURNX(NULL == me || NULL == node, IPANEL_ERR);

    if(0 == me->flag_open || node->node_buffer_data_len <= 0)
    {
        imem_mgr_remove(me->mem_mgr, node);
        return;
    }
    ptr = (char *)node->node_buffer;

    //代表不是188的整数，也就是说有杂数据，这个就是rtp
	if(node->node_buffer && ((node->node_buffer[0] == 0x24)?((node->node_buffer_data_len - 4)%188):(node->node_buffer_data_len%188)))
	{
		//tcp interleaved方式	
		if(node->node_buffer[0] == 0x24)
			ptr += 4;
		TS_GET_INT16(current_seq, ptr + 2);//将数据获取出来
		if(me->rtp_info->last_recv_rtp_seq != -1 && current_seq != ((unsigned short)me->rtp_info->last_recv_rtp_seq + 1))
		{
			unsigned short lost_packet = 0;
			if( current_seq > (unsigned short)me->rtp_info->last_recv_rtp_seq ) 
			{
				lost_packet = current_seq - (unsigned short)me->rtp_info->last_recv_rtp_seq - 1;
			}
			else
			{
				if(me->rtp_info->last_recv_rtp_seq == 65535 && current_seq == 0)
					lost_packet = 0;
				else
				{
					lost_packet = current_seq + 65535 - (unsigned short)me->rtp_info->last_recv_rtp_seq;
				}				
			}
			if(lost_packet)
			{
                me->rtp_info->lost_rtp_packet_num += lost_packet;
                INFO("%s[ts_iplayer_calculate_rtp]recv rtp packet lost = %hu, current seq = %hu, last_seq = %hu\n", TS_IPLAYER_MARK, lost_packet, current_seq, (unsigned short)me->rtp_info->last_recv_rtp_seq);
			}
		}
		me->rtp_info->last_recv_rtp_seq = current_seq;

        
		/*检查continuity_counter*/
		{
			unsigned short pid = 0, continue_counter = 0, lost_ts_packet = 0;
			unsigned char adaptation_field_control = 0,discontinuity_indicator = 0, flag_calcuate = 1;
			/*计算continue counter*/			
			ptr = node->node_buffer + node->node_buffer_data_len%188;
			while(ptr < node->node_buffer + node->node_buffer_data_len)
			{
				lost_ts_packet = 0;
				pid = (ptr[1]&0x1f)<<8 | ptr[2];
				if(me->stream_info->avinfo->vidinfo[0].video_pid == 0 || pid != me->stream_info->avinfo->vidinfo[0].video_pid)
					flag_calcuate = 0;
				else
				{
					adaptation_field_control = (ptr[3]>>4)&0x3;
					switch(adaptation_field_control)
					{
						case 0x00:
							flag_calcuate = 0;
							break;
						case 0x01:
							/*无调整字段*/
							break;
						case 0x10:
							flag_calcuate = 0;
							break;
						case 0x11:
							/*计算discontinuity_indicator*/
							discontinuity_indicator = (ptr[5]>>7)&0x01;
							if(discontinuity_indicator)
								flag_calcuate = 0;
							break;
						default:
							break;
					}
					continue_counter = ptr[3]&0x0f;
					if(me->rtp_info->last_continuity_counter != -1)
					{
						if(continue_counter != (unsigned short)me->rtp_info->last_continuity_counter + 1)
						{
							if(continue_counter > (unsigned short)me->rtp_info->last_continuity_counter)
								lost_ts_packet = continue_counter - (unsigned short)me->rtp_info->last_continuity_counter - 1;
							else
							{
								if(me->rtp_info->last_continuity_counter == 15 && continue_counter == 0)
									lost_ts_packet = 0;
								else
								{
									lost_ts_packet = continue_counter + 15 - me->rtp_info->last_continuity_counter;
								}
							}
							if(lost_ts_packet && flag_calcuate)
							{
                                INFO("%s[ts_iplayer_calculate_rtp]recv ts packet lost = %hu, last_continuity_counter = %hu, cur_continuity_counter = %hu\n", TS_IPLAYER_MARK, lost_ts_packet, (unsigned short)me->rtp_info->last_continuity_counter, continue_counter);
							}							
						}
					}
					me->rtp_info->last_continuity_counter = (int)continue_counter;
				}
				ptr += 188;
			}
		}
	}    
    return;
}

/*将ts数据加入数据队列中*/
static int ts_iplayer_append_data(ts_iplayer_t* ts_player, int value)
{
    int ret = IPANEL_ERR;
    ts_iplayer_t *me = ts_player;
    imem_node_t *node = (imem_node_t*)value;

    FAILED_RETURNX(NULL == me || NULL == node, IPANEL_ERR);

    if(0 == me->flag_open || node->node_buffer_data_len <= 0)
    {
        imem_mgr_remove(me->mem_mgr, node);
        return IPANEL_OK;
    }

    ret = imem_node_list_apend(me->mem_list, node);
    if(IPANEL_ERR == ret)
    {
        imem_mgr_remove(me->mem_mgr, node);
    }
    
    return ret;
}



/*解析ts流的音视频信息*/
static int ts_iplayer_parse_stream_info(ts_iplayer_t* ts_player)
{
    int ret = IPANEL_ERR,head = 0,buffer_len = 0,i = 0,audio_num = 0;
    ts_iplayer_t *me = ts_player;
    IPANEL_ADEC_PARAM aud_cfg[16] ={0};
    IPANEL_VDEC_PARAM vid_cfg ={0};
    unsigned char *buffer = NULL;
	unsigned int  parse_start_time = (unsigned int)time_ms();

    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    //每一次解析音视频信息在500ms以内
    while(time_ms()-parse_start_time < 500 && me->flag_decoder_success == 0)
    {
        me->mem_node = imem_node_list_get_index(me->mem_list,me->parse_pmt_index);

        if(me->mem_node)
        {
            int pmt_pid = me->stream_info->pmt_pid;
            
            head = me->mem_node->node_buffer_data_len % TS_IPLAYER_PACKET_LEN;
            me->rtp_info->rtp_head_length = head;
            buffer = me->mem_node->node_buffer + head;
            buffer_len = me->mem_node->node_buffer_data_len - head; 

            INFO("%s[parse_stream_info]header = %u\n", TS_IPLAYER_MARK, head);
            ret = ts_iparse_stream_info(me->stream_info, buffer, buffer_len);

            if(ret == IPANEL_OK)
            {
                INFO("%s[parse_stream_info] parse success\n", TS_IPLAYER_MARK);
                //解析音视频信息成功
                me->mem_node = NULL;
                me->flag_decoder_success = 1;
                me->parse_pmt_index = 0;

                //获取视频图片的宽度和高度
                while(me->mem_node = imem_node_list_get_index(me->mem_list,me->parse_pmt_index))
                {
                    head = me->mem_node->node_buffer_data_len % TS_IPLAYER_PACKET_LEN;
                    buffer = me->mem_node->node_buffer + head;
                    buffer_len = me->mem_node->node_buffer_data_len - head; 
                    
                    if(IPANEL_OK == ts_iparse_stream_video_picture(me->stream_info, buffer, buffer_len))
                        break;
                    me->parse_pmt_index++;
                }
                me->mem_node = NULL;
                me->parse_pmt_index = 0;


                memset(me->stream_info->audio_track,0,sizeof(IPANEL_MEDIA_AUDIO_TRACKS));
                me->stream_info->audio_track->len = me->stream_info->avinfo->audio_channel_num;
                for(i=0;i<me->stream_info->avinfo->audio_channel_num;i++)
                {
                    me->stream_info->audio_track->tracks[i].trackID = i;
                    me->stream_info->audio_track->tracks[i].audio_pid = me->stream_info->avinfo->audinfo[i].audio_pid;
                    memcpy(me->stream_info->audio_track->tracks[i].trackdesc,me->stream_info->avinfo->audinfo[i].desc,32);
                    memcpy(me->stream_info->audio_track->tracks[i].langdesc, me->stream_info->avinfo->audinfo[i].lang, 32);
                    me->stream_info->audio_track->tracks[i].flag_support = 1;
                } 

                memset(me->stream_info->subt_track, 0, sizeof(IPANEL_MEDIA_SUBT_INFO));
                me->stream_info->subt_track->len = me->stream_info->avinfo->subt_channel_num;
                for(i=0;i<me->stream_info->avinfo->subt_channel_num;i++)
                {
                    me->stream_info->subt_track->subts[i].trackID = i;
                    me->stream_info->subt_track->subts[i].subt_pid = me->stream_info->avinfo->subtinfo[i].subt_pid;                            
                    memcpy(me->stream_info->subt_track->subts[i].subtdesc, me->stream_info->avinfo->subtinfo[i].desc, 32);
                    memcpy(me->stream_info->subt_track->subts[i].langdesc, me->stream_info->avinfo->subtinfo[i].lang, 32);
                }           
  
                //打开底层播放器
                audio_num = 0;
                for(i=0;i<me->stream_info->avinfo->audio_channel_num;i++)
                {
                    if(me->stream_info->avinfo->audinfo[i].audio_pid > 0
                        && me->stream_info->avinfo->audinfo[i].audio_pid < 0x1fff)
                    {
                        aud_cfg[audio_num].valid = 1;
                        aud_cfg[audio_num].audio_pid = me->stream_info->avinfo->audinfo[i].audio_pid;
                        aud_cfg[audio_num].codec_id = me->stream_info->avinfo->audinfo[i].codec_id;
                        aud_cfg[audio_num].stream_type = IPANEL_XMEM_TS;

                        audio_num++;
                    }
                }
                if(me->stream_info->avinfo->vidinfo[0].video_pid > 0 
                    && me->stream_info->avinfo->vidinfo[0].video_pid < 0x1fff)
                {
                    vid_cfg.valid = 1;
                    vid_cfg.video_pid = me->stream_info->avinfo->vidinfo[0].video_pid;
                    vid_cfg.codec_id = me->stream_info->avinfo->vidinfo[0].codec_id;
                    vid_cfg.width = me->stream_info->avinfo->vidinfo[0].width;
                    vid_cfg.height = me->stream_info->avinfo->vidinfo[0].height;
                    vid_cfg.stream_type = IPANEL_XMEM_TS;
                }          

                
                INFO("%s[parse_stream_info]mediaProcessor open start time=%u.\n", TS_IPLAYER_MARK, time_ms());
                //ret = ipanel_mediaProcessor_set_param((int)me->av_handle, &vid_cfg, &aud_cfg);
                //设置所有音视频信息到底层
                ret = ipanel_mediaProcessor_set_param((int)me->av_handle, &vid_cfg, aud_cfg, audio_num);
				if( ret == IPANEL_OK ) 
                {
                    ret = ipanel_mediaProcessor_play((int)me->av_handle);
                    if(ret == IPANEL_OK)
                    {
    					me->flag_av_open = 1;
                        INFO("%s[parse_stream_info]mediaProcessor open success time=%u.\n", TS_IPLAYER_MARK, time_ms());
                    }
                    else
                    {
                        INFO("%s[parse_stream_info]mediaProcessor open faied time=%u.\n", TS_IPLAYER_MARK, time_ms());
                    }
				}
                else 
                {
                    INFO("%s[parse_stream_info]mediaProcessor set param faied time=%u.\n", TS_IPLAYER_MARK, time_ms());
				}  
            }
            else
            {
                //解析音视频信息失败
                me->mem_node = NULL;
                me->parse_pmt_index ++;
                
				//刚好解析出PMT PID
                if(pmt_pid == -1 && pmt_pid != me->stream_info->pmt_pid)
                {
                    INFO("%s[parse_stream_info]parse pat success reset pmt index\n", TS_IPLAYER_MARK);
                    //兼容PMT在前,PAT在后的情况,回退50个包
                    if(me->parse_pmt_index > 50)
                    {
                        me->parse_pmt_index -= 50;
                    }
                }
            }
        }
        else
        {
            me->mem_node = NULL;
            break;            
        }
    }

    return ret;    
}



/*将ts数据推入底层解码器*/
static int ts_iplayer_push_stream(ts_iplayer_t* ts_player)
{
    int ret = IPANEL_OK,i = 0,need_push_num = 100,head = 0,buffer_len = 0,current_seq = 0,new_pos = 0;
    ts_iplayer_t *me = ts_player;
    unsigned char *buffer = NULL;


    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    if(!(me->flag_decoder_success && me->flag_push_stream && me->flag_av_open))
    {
        return IPANEL_ERR;
    }

    INFO("%s[push stream]push stream start(time:%d)\n", TS_IPLAYER_MARK,time_ms());

    for(i=0;i<need_push_num && me->flag_push_stream;i++)
    {
        if(NULL == me->mem_node)
            me->mem_node = imem_node_list_get(me->mem_list);

        if(me->mem_node)
        {
            head = me->mem_node->node_buffer_data_len % TS_IPLAYER_PACKET_LEN;
            buffer = me->mem_node->node_buffer + head;
            buffer_len = me->mem_node->node_buffer_data_len - head;

            //根据rtp的seq过来RTP包
            if(head > 4 && me->rtp_seq_filter > 0)
            {
        		if(me->mem_node->node_buffer[0] == 0x24)
            		TS_GET_INT16(current_seq, me->mem_node->node_buffer + 6);//将数据获取出来
                else
            		TS_GET_INT16(current_seq, me->mem_node->node_buffer + 2);//将数据获取出来

                if(me->rtp_seq_filter == 0)
                {
                    if(current_seq - me->rtp_seq_filter > 2)
                    {
                        //丢掉
                        INFO("%s[push_stream]lost data current_seq=%d,base_seq=%d\n", TS_IPLAYER_MARK, current_seq, me->rtp_seq_filter);
                        imem_mgr_remove(me->mem_mgr, me->mem_node);
                        me->mem_node = NULL;
                        continue;
                    }
                    else
                    {
                        me->rtp_seq_filter = -1;
                    }
                }
                else
                {
                    if(current_seq < me->rtp_seq_filter)
                    {
                        //丢掉
                        INFO("%s[push_stream]lost data current_seq=%d,base_seq=%d\n", TS_IPLAYER_MARK, current_seq, me->rtp_seq_filter);
                        imem_mgr_remove(me->mem_mgr, me->mem_node);
                        me->mem_node = NULL;
                        continue;
                    }
                    else
                    {
                        me->rtp_seq_filter = -1;
                    }
                }
                
            }

            //寻找I帧数据
            if(me->flag_find_I_frame)
            {
                //MPEG格式流寻找I帧
                if(me->temp_packet_len + buffer_len < sizeof(me->temp_packet))
                {
            		memcpy(me->temp_packet + me->temp_packet_len, buffer, buffer_len);
            		me->temp_packet_len += buffer_len;  

                    if(me->temp_packet_len >= 32*1024) //32K就寻找I帧
                    {
                        if(me->stream_info->avinfo->vidinfo->codec_id == MEDIAPROCESSOR_VIDEO_TYPE_MPEG1
                            || me->stream_info->avinfo->vidinfo->codec_id == MEDIAPROCESSOR_VIDEO_TYPE_MPEG2)
                        {  
                            //MPEG格式TS流寻找I帧
                            ret = ts_iplayer_mpeg2ts_search_I_frame(me, me->temp_packet, me->temp_packet_len, 1, &new_pos);
                        }
                        else if(me->stream_info->avinfo->vidinfo->codec_id == MEDIAPROCESSOR_VIDEO_TYPE_H264)
                        {
                            #if 0
                              char* h264_s_if = NULL;
                              h264_s_if = ini_get_option("vod-h264-search-iframe");

                              if(h264_s_if && strlen(h264_s_if)>0)
                              {
                                 if (stristr(h264_s_if,"yes"))
                                 {
                                    /*H264格式TS流寻找I帧*/
                                    PTL_INFO(("[VOD][MISC][TS][TSMAIN] ini set 264 search Iframe,tm=%d\n",time_ms()));
                                    ret = ts_player_h264ts_search_I_frame(me, me->temp_packet, me->temp_packet_len, 1, &new_pos);
                                 }
                              }
                              else
                              {
                                ret = IPANEL_OK;
                                PTL_INFO(("[VOD][MISC][TS][TSMAIN] ini no set 264 search Iframe\n"));
                              }
                            #endif
                            ret = IPANEL_OK;
                        }

                        if(ret == IPANEL_OK)
                        {
                            //找到I帧,并返回I帧位置
                        	buffer = me->temp_packet + new_pos;
                        	buffer_len = me->temp_packet_len - new_pos;
                            //刷新推流时间信息
                            me->last_push_stream_time = time_ms();
                            me->qos_data_empty_time = me->last_push_stream_time;
                        	ipanel_mediaProcessor_push_stream((int)me->av_handle, buffer, buffer_len);
                            me->flag_find_I_frame = 0;
                            INFO("%s[push_stream]find I frame success tm =%d\n", TS_IPLAYER_MARK, time_ms());
                        }
                        else
                        {
                            //搜索I帧失败，也要返回最后一帧的位置，下次再次搜索，避免本次数据不够误判情况
                            if(new_pos == 0)
                            {
                                memset(me->temp_packet,0,sizeof(me->temp_packet));
                                me->temp_packet_len = 0;
                                me->pes_buffer_len = 0;
                            }
                            else
                            {
                        		memmove(me->temp_packet, me->temp_packet + new_pos, me->temp_packet_len - new_pos);
                        		me->temp_packet_len -= new_pos; 
                                me->pes_buffer_len = 0;
                            }
                            
                            INFO("%s[push_stream]find I frame temp_packet_len = %d,new_pos = %d\n", TS_IPLAYER_MARK, me->temp_packet_len, new_pos);

                            imem_mgr_remove(me->mem_mgr, me->mem_node);
                            me->mem_node = NULL;
                            continue;
                        }
                    }
                    else
                    {
                        imem_mgr_remove(me->mem_mgr, me->mem_node);
                        me->mem_node = NULL;
                        continue;
                    }
                }
                else
                {
                    INFO("%s[push_stream]find I frame temp packet buffer is full\n", TS_IPLAYER_MARK);
                    ret = IPANEL_ERR;
                    break;
                }                        
            }
            else
            {
                //刷新推流时间信息
                me->last_push_stream_time = time_ms();
                me->qos_data_empty_time = time_ms();
                /*如果之前发送过播放异常的消息,这个时候有流来了,要发送播放成功消息,把提示框去掉*/
				if(me->flag_send_error)
				{
					me->flag_send_error = 0;
					me->cbf(me->player_handle, EIS_VOD_PLAY_ABNORMAL_RESUME, 0, 0);
				}
            	ret = ipanel_mediaProcessor_push_stream((int)me->av_handle, buffer, buffer_len);
            }
            
            if(IPANEL_OK == ret)
            {               
                //推流成功，更新PTS                
                if(1/*time_ms() - me->parse_pts_time >= 100*/)
                {
                    ts_iparse_stream_pts(me->stream_info, buffer, buffer_len);
                    me->parse_pts_time = time_ms();
                }

                /*************************************************************************
                 *qos信息统计，包括推流速率，实际推流数据
                 *************************************************************************/
                me->qos_bytes_out+= buffer_len;
                if(me->qos_push_data_time == 0)
                {
                    me->qos_push_data_time = time_ms();
                }
                else if(time_ms() - me->qos_push_data_time > 1000)
                {
                    int total_time = time_ms() - me->qos_push_data_time;
                    me->qos_push_data_time = time_ms();
                    implayer_qos_proc("set", VOD_QOS_BIT_RATE, me->qos_bytes_out*(1000*8)/total_time,0);
                    me->qos_bytes_out = 0;
                }
                /*QOS*/
                {
    				vod_qos_info_t qos_info[1] = {0};

    				qos_info->buffer = me->mem_node->node_buffer;
    				qos_info->buffer_len = me->mem_node->node_buffer_data_len;
    				qos_info->header_len = head;
    				
    				if( me->flag_decoder_success ) 
                    {
    					qos_info->video_pid = (unsigned short)(me->stream_info->avinfo->vidinfo[0].video_pid);
    					qos_info->pcr_pid = (unsigned short)(me->stream_info->pcr_pid);
    				} 
                    else 
                    {
    					qos_info->video_pid = 0xFFFF;
    					qos_info->pcr_pid = 0xFFFF;
    				}
    				implayer_qos_proc("proc",VOD_QOS_EVENT_RECV_TS_STREAM,(int32)qos_info,0);	
                    
                }
                /*************************************************************************
                 *qos信息统计结束
                 *************************************************************************/
                /*SUBTITLE*/
                if(me->subt_pid != 0)
                {
                    subtitle_block_t block[1] = {0};
                    unsigned char *search_pos = NULL, *copy_pos = NULL;
                    int current_pid = 0;
                    
                    block->buf = me->subt_buffer;
                    block->size = 0;
                    search_pos = buffer;
                    while(search_pos < me->mem_node->node_buffer + me->mem_node->node_buffer_data_len)
                    {
                        current_pid = ((search_pos[1]&0x1f)<<8) | search_pos[2];
                        if(current_pid == me->subt_pid)
                        {
                            /*记录下拷贝的初始位置*/
                            if(copy_pos == NULL)
                                copy_pos = search_pos;
                        }    
                        else if(copy_pos != NULL)
                        {
                            /*发现不同的pid,需要拷贝*/
                            memcpy(block->buf + block->size, copy_pos, search_pos - copy_pos);
                            block->size += search_pos - copy_pos;
                            copy_pos = NULL;
                        }                    
                        search_pos += 188;
                    }
                    if(copy_pos != NULL)
                    {
                        /*已经查找到尾了*/
                        memcpy(block->buf + block->size, copy_pos, search_pos - copy_pos);
                        block->size += search_pos - copy_pos;
                        copy_pos = NULL;                    
                    }                
                    if(block->size > 0)
                    {
                        implayer_qos_proc("subtitle", 0x100, 0x108, (int)block);    
                    }
                }
                
            #ifdef TS_IPLAYER_TO_FILE
                ts_iplayer_write_ts_file(me, buffer, buffer_len);
            #endif

                imem_mgr_remove(me->mem_mgr, me->mem_node);
                me->mem_node = NULL;
            }
            else
            {
                //推流失败
                break;
            }
        }
        else
        {
            //没有数据
            break;
        }
    }

    return ret;
}



/*清空ts播放器*/
static int ts_iplayer_clear_data(ts_iplayer_t* ts_player)
{
    int ret = IPANEL_ERR;
    ts_iplayer_t *me = ts_player;

    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    ret = imem_node_list_clear(me->mem_list);
	me->rtp_info->last_recv_rtp_seq = -1;
	me->rtp_info->last_push_rtp_seq = -1;
	me->rtp_info->last_continuity_counter = -1;
    me->rtp_info->lost_rtp_packet_num = 0;
    me->stream_info->base_pts = 0;
    me->stream_info->current_pts = 0;

    return ret;
}


/*搜索MPEG TS流中的I帧*/
static int ts_iplayer_mpeg2ts_search_I_frame(ts_iplayer_t* ts_player,unsigned char *buf,int len,int ifm_flag,int *new_pos)
{
    int pos = 0,find_I_pos = 0;
	int pes_header_complete = 1;
	int pes_start_pos = 0;
	unsigned char  *tsp=NULL,*p=NULL;
	int ts_payload_size=0;
	int PES_header_data_length=0;
	unsigned char picture_coding_type=0;
    ts_iplayer_t *me = ts_player;

    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    if( buf[0] != 0x47 || (len % 188) !=0 )
    {
        INFO("%s[mpeg2ts_search_I_frame]MPEG2 search I frame buf[0]=0x%x,len % 188=%d\n",TS_IPLAYER_MARK,buf[0],(len % 188));
        return IPANEL_ERR;
    }

    tsp = buf;

	//寻找VIDEO包 
	while(tsp + 188 <= ( buf + 188 * (len / 188)))
	{
		//过滤掉其他TS包
		if( me->stream_info->avinfo->vidinfo->video_pid != ((tsp[1]&0x1f)<<8)+(tsp[2]&0xFF))
			goto NEXT_PACKET;

		//发现新的PES开始，开始保存负载
		if( (tsp[1] & 0x40) != 0 )
		{
            find_I_pos = pos;
			pes_header_complete = 0;
			pes_start_pos = me->pes_buffer_len;
		}

		//剥出TS的负载保存到pes_buf中
		p = NULL;
		switch( (tsp[3]>>4)&0x3 ) //adaptation_field_control
		{
		case 0://保留
		case 2://无净荷
			break;
		case 1://无调整字段
			p = tsp + 4;
			break;
		case 3://调整字段后是净荷
			p = tsp + 5 + tsp[4];
			break;
		}
        
		if( p )
		{
			ts_payload_size = tsp + 188 - p;
			if( sizeof(me->pes_buffer) < me->pes_buffer_len + ts_payload_size )
				ts_payload_size = sizeof(me->pes_buffer) - me->pes_buffer_len;
			if( 0 < ts_payload_size )
			{
				memcpy( me->pes_buffer+me->pes_buffer_len, p, ts_payload_size );
				me->pes_buffer_len += ts_payload_size;
			}
		}

		if(0 == pes_header_complete)
		{
			//PES数据不够，继续剥出
			if( me->pes_buffer_len - pes_start_pos < 9 )
					goto NEXT_PACKET;
			p = me->pes_buffer + pes_start_pos;

			PES_header_data_length = *(p + 8);

			if((me->pes_buffer_len - pes_start_pos) < (9 + PES_header_data_length))
				goto NEXT_PACKET;
           
			//PES packet header complete
			pes_header_complete = 1;
			memmove(me->pes_buffer + pes_start_pos, me->pes_buffer + pes_start_pos + 9 + PES_header_data_length,
					me->pes_buffer_len - pes_start_pos - 9 - PES_header_data_length);
			me->pes_buffer_len = me->pes_buffer_len - PES_header_data_length - 9;
		}

		p = me->pes_buffer;
        
		//寻找PICTURE_START_CODE
		while( p + 5 < me->pes_buffer + me->pes_buffer_len)
		{
			if( p[0] == 0 && p[1] == 0 && p[2] == 0x1 )
			{
				if( p[3] == 0x0 )
				{
					picture_coding_type = (p[5]>>3) & 0x7;

					//必须是I帧
					if(ifm_flag && picture_coding_type == 0x01)
					{
                        *new_pos = find_I_pos;
						return IPANEL_OK;
					}
				}
			}
            
			p++;
		}
        
		memmove(me->pes_buffer, p, me->pes_buffer + me->pes_buffer_len - p);
		me->pes_buffer_len = me->pes_buffer + me->pes_buffer_len - p;

NEXT_PACKET:
		tsp += 188;
        pos += 188;
	}
    INFO("%s[mpeg2ts_search_I_frame]MPEG2 search I frame error!!!\n",TS_IPLAYER_MARK);

    *new_pos = find_I_pos;
	return IPANEL_ERR;
}



/*将ts流写入文件*/
static int ts_iplayer_write_ts_file(ts_iplayer_t *ts_player,unsigned char *buffer,int len)
{
#ifdef TS_IPLAYER_TO_FILE
    ts_iplayer_t *me = ts_player;

    FAILED_RETURNX(NULL == me, IPANEL_ERR);

	if( me->ts_file == 0 ) {
		me->ts_file = (unsigned int)fopen("ts_file.ts", "wb");
	} 
	
	FAILED_RETURNX( !me->ts_file,IPANEL_ERR);

	fwrite(buffer,1,len,(FILE *)me->ts_file);
	fflush((FILE *)me->ts_file);
#endif

	return IPANEL_OK;
}


/*统计ts数据，并进行校验*/
static int ts_iplayer_data_check(ts_iplayer_t *ts_player)
{
    ts_iplayer_t *me = ts_player;
    int recv_packet = 0;


    FAILED_RETURNX(NULL == me, IPANEL_ERR);

    //获取缓存的数据量百分比
    recv_packet = imem_mgr_get_rate(me->mem_mgr);

    if(recv_packet > 90)
        INFO("%s[ts_iplayer_data_check]data buffer rate is %d\n",TS_IPLAYER_MARK,recv_packet);
    
    /*************************************************************************
     *qos信息统计，主要是数据是否溢出、数据量不够、或不能接收数据
     *************************************************************************/
    if(me->flag_decoder_success && me->flag_push_stream)
    {
		if(recv_packet > 90)
		{
			if(me->qos_data_full_time == 0)
				me->qos_data_full_time = time_ms();
			else if(time_ms() - me->qos_data_full_time > 600)//600ms维持在90%以上,则表示上溢了
			{
				implayer_qos_proc("proc",VOD_QOS_EVENT_BUFFER_OVERFLOW, 0, 0);
				me->qos_data_full_time = 0;
			}
		}
        else if(recv_packet == 0)
        {
			if( me->qos_data_empty_time != 0 
				&& time_ms() - me->qos_data_empty_time > 2000 
				&& me->flag_paused == 0) 
			{
                INFO("%s[ts_iplayer_data_check]ts data is empty, time = %d\n", TS_IPLAYER_MARK, time_ms());
				me->qos_data_empty_time = 0;
				implayer_qos_proc("proc",VOD_QOS_EVENT_BUFFER_EMPTY,0,0);					
			}  

            if( me->last_push_stream_time != 0
                && time_ms() - me->last_push_stream_time > 10000
                && me->flag_paused == 0)
            {
                INFO("%s[ts_iplayer_data_check]ts data abnormal, time = %d\n", TS_IPLAYER_MARK, time_ms());
                //断流了,需要发5250消息，p2值为3
                me->cbf(me->player_handle,EIS_VOD_PLAY_ABNORMAL,3,0);
                me->last_push_stream_time = 0;
                /*QOS回调的消息在上层callback上发*/
                me->flag_send_error = 1;
            }
        }
		else
		{
			if(me->qos_data_full_time)
				me->qos_data_full_time = 0;
		}        
    }
    else
    {
        //如果从一开始就收不到数据，就发送播放错误消息
        if(recv_packet == 0
            && me->flag_push_stream == 1
            && me->last_push_stream_time != 0
            && me->qos_data_empty_time == 0
            && time_ms() - me->last_push_stream_time > 10000)
        {
            me->cbf(me->player_handle, EIS_VOD_PLAY_ABNORMAL, 2, 0);
            me->last_push_stream_time = 0;
            /*QOS回调的消息在上层callback上发*/
            me->flag_send_error = 1;
        }
    }
    
    
    return IPANEL_OK;
}


/************************************End Of File**********************************/




