/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:系统函数
*********************************************************************/
#include <stdio.h>
#include <stdarg.h>
#include <memory.h>
#include <sys/time.h>
#include <time.h>

#include "ipanel_porting.h"

#ifdef WIN32
static FILE *log_file = NULL;
static UINT32_T s_log_sem = 0;
#endif

/******************************************************************************/
/*Description: iPanel Browser Runtime Base time.                              */
/*             and Unit is millisecond (1/1000 second).                       */
/*Input      : No                                                             */
/*Output     : No                                                             */
/*Return     : counts of millisecond.                                         */
/******************************************************************************/
UINT32_T ipanel_porting_time_ms(VOID)
{
#if 0
	struct timeval tm;
	unsigned int tmptm;

	static struct timeval tmstart = {0, 0};
	
	if (!tmstart.tv_sec && !tmstart.tv_usec) {
		gettimeofday(&tmstart,NULL);
		return 0;
	}
	
	gettimeofday(&tm, NULL);
 
	tmptm = (tm.tv_sec-tmstart.tv_sec) * 1000 + (tm.tv_usec-tmstart.tv_usec) /1000;
	  
	return tmptm;
#else
    struct timespec tm;
    unsigned int tmptm;    
    static struct timespec tmstart = {0, 0};

	if(!tmstart.tv_sec && !tmstart.tv_nsec)
    {
        clock_gettime(CLOCK_MONOTONIC, &tmstart);
		return 0;
	}    
    clock_gettime(CLOCK_MONOTONIC, &tm);
    
	tmptm = (tm.tv_sec-tmstart.tv_sec) * 1000 + ((tm.tv_nsec - tmstart.tv_nsec)/1000000);
	  
	return tmptm;    
#endif
}


INT32_T ipanel_porting_dprintf(CONST CHAR_T *fmt, ...)
{
#ifdef WIN32
    char msg[1024] = {0};
    va_list args;

	if (log_file == NULL) 
	{
        log_file = fopen("logfile.log", "wb");
    }
	if(s_log_sem == 0)
	{
		s_log_sem = ipanel_porting_sem_create("DPRI", 1, IPANEL_TASK_WAIT_FIFO);
	}

	if(s_log_sem)
	{
		ipanel_porting_sem_wait(s_log_sem, IPANEL_WAIT_FOREVER);
	}
		
    va_start(args, fmt);
    vfprintf(log_file, fmt, args);
    fflush(log_file);
	vsprintf(msg, fmt, args);
    va_end(args);

	if(s_log_sem)
	{
		ipanel_porting_sem_release(s_log_sem);
	}
#else
	va_list args;
	va_start(args,fmt);
	vprintf(fmt,args);
	va_end(args);
	fflush(stdout);
#endif

	return IPANEL_OK;
}



