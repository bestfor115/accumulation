/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
����:PLAYER����
*********************************************************************/
#include "iplayer_qam_player.h"
//#include "mplayer.h"

/*��qam������*/
int ts_qamplayer_open(iplayer_t *handle, iplayer_info_t *info)
{
#if 0
    mplayer_media_t media[1] = {0};

    if(NULL == handle || NULL == info)
        return 0;
    
    /*�������dvb�Ľӿڽ��в���*/ 
    media->frequency = info->dvb_info.frequency;
    media->symbolrate = info->dvb_info.symbolrate;
    media->qam = info->dvb_info.qam;
    media->service_id = info->dvb_info.service_id;
    media->pmt_pid = info->dvb_info.pmt_pid;    
    if(media->symbolrate == 0)
        media->symbolrate = 0x68750;
    if(media->qam == 0)
        media->qam = 3;
    //ipanel_dvb_play(media);
#endif
    return 0x1234;
}

/*ts player����������*/
int ts_qamplayer_proc(int player_id, unsigned int op, int p1,int p2)
{
    
    //ipanel_dvb_stop_play();
    
    return 0;
}
