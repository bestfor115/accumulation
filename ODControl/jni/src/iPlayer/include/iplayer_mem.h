/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
功能:MEM管理
*********************************************************************/
#ifndef __IVOD_MEM_H_
#define __IVOD_MEM_H_

#ifdef __cplusplus
extern "C"{
#endif

struct imem_node_s
{
    unsigned char*    node_buffer;                   //内存地址
    unsigned int      node_buffer_data_len;          //所有内存块中存储的数据大小
};//内存块
typedef struct imem_node_s       imem_node_t;
typedef struct imem_node_list_s  imem_node_list_t;
typedef struct imem_mgr_s        imem_mgr_t;

#define MAX_IMEM_NODE_BUFFER_LEN 1500

/*创建内存管理器,并且分配指定大小的内存*/
imem_mgr_t *
imem_mgr_new(int size);

/*销毁*/
int
imem_mgr_destory(imem_mgr_t *handle);

/*获取可用node*/
imem_node_t *
imem_mgr_get_unused_block(imem_mgr_t *handle, int size);

/*删除*/
int
imem_mgr_remove(imem_mgr_t *handle, imem_node_t *mem);

/*清除*/
int
imem_mgr_clear(imem_mgr_t *handle);

/*获取剩余大小*/
int
imem_mgr_get_free_size(imem_mgr_t *handle);

/*获取已使用率*/
int
imem_mgr_get_rate(imem_mgr_t *handle);

/*创建node list*/
imem_node_list_t *
imem_node_list_new(imem_mgr_t *mem_mgr, int mem_node_number);

/*销毁node list*/
int
imem_node_list_destory(imem_node_list_t *handle);

/*添加到node list尾端*/
int
imem_node_list_apend(imem_node_list_t *handle,imem_node_t *mem);

/*删除node list第一个节点*/
int
imem_node_list_remove(imem_node_list_t *handle,imem_node_t *mem);

/*获取node list第一个节点*/
imem_node_t *
imem_node_list_get(imem_node_list_t *handle); 

/*获取node list指定序号的节点*/
imem_node_t *
imem_node_list_get_index(imem_node_list_t *handle,int index);

/*清空node list所有节点*/
int 
imem_node_list_clear(imem_node_list_t *handle);


#ifdef __cplusplus
}
#endif

#endif
/************************************End Of File**********************************/

