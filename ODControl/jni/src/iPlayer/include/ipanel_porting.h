/*********************************************************************
    Copyright (c) 2005 by iPanel Technologies, Ltd.
    All rights reserved. You are not allowed to copy or distribute
    the code without permission.
	Description: porting��ͳһ�ӿ��ļ�. ���������ն�ƽ̨��.
*********************************************************************/

/*
** �κ�struct/union��, �����ģ��˽�е�(����������ģ��䰴Լ���������ݵ�), �Ͻ����ڹ���header��! ��, ���Է�����
** ����: typedef struct tagMyStruct MyStruct; ��struct tagMyStruct��ʵ�嶨��һ��Ҫ����ģ���ڲ���.c�ļ���!
*/

#ifndef __IPANEL_PORTING_H__
#define __IPANEL_PORTING_H__

#include "../porting/ipanel_redefine.h"
#include "../porting/ipanel_porting_event.h"
#include "../porting/ipanel_prio.h" // Ӧ�ò�task���ȼ�����
#include "../porting/ipanel_typedef.h"
#include "../porting/ipanel_base.h"

#include "../porting/ipanel_media_processor.h"
#include "../porting/ipanel_mediaplayer.h"
#include "../porting/ipanel_network.h"
#include "../porting/ipanel_socket.h"
#include "../porting/ipanel_os.h"

#endif /* __IPANEL_PORTING_H__ */

