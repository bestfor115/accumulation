/*********************************************************************
Copyright (c) 2012 by iPanel Technologies, Ltd.
All rights reserved. You are not allowed to copy or distribute
the code without permission.
����:MEM����
*********************************************************************/
#ifndef __IVOD_MEM_H_
#define __IVOD_MEM_H_

#ifdef __cplusplus
extern "C"{
#endif

struct imem_node_s
{
    unsigned char*    node_buffer;                   //�ڴ��ַ
    unsigned int      node_buffer_data_len;          //�����ڴ���д洢�����ݴ�С
};//�ڴ��
typedef struct imem_node_s       imem_node_t;
typedef struct imem_node_list_s  imem_node_list_t;
typedef struct imem_mgr_s        imem_mgr_t;

#define MAX_IMEM_NODE_BUFFER_LEN 1500

/*�����ڴ������,���ҷ���ָ����С���ڴ�*/
imem_mgr_t *
imem_mgr_new(int size);

/*����*/
int
imem_mgr_destory(imem_mgr_t *handle);

/*��ȡ����node*/
imem_node_t *
imem_mgr_get_unused_block(imem_mgr_t *handle, int size);

/*ɾ��*/
int
imem_mgr_remove(imem_mgr_t *handle, imem_node_t *mem);

/*���*/
int
imem_mgr_clear(imem_mgr_t *handle);

/*��ȡʣ���С*/
int
imem_mgr_get_free_size(imem_mgr_t *handle);

/*��ȡ��ʹ����*/
int
imem_mgr_get_rate(imem_mgr_t *handle);

/*����node list*/
imem_node_list_t *
imem_node_list_new(imem_mgr_t *mem_mgr, int mem_node_number);

/*����node list*/
int
imem_node_list_destory(imem_node_list_t *handle);

/*��ӵ�node listβ��*/
int
imem_node_list_apend(imem_node_list_t *handle,imem_node_t *mem);

/*ɾ��node list��һ���ڵ�*/
int
imem_node_list_remove(imem_node_list_t *handle,imem_node_t *mem);

/*��ȡnode list��һ���ڵ�*/
imem_node_t *
imem_node_list_get(imem_node_list_t *handle); 

/*��ȡnode listָ����ŵĽڵ�*/
imem_node_t *
imem_node_list_get_index(imem_node_list_t *handle,int index);

/*���node list���нڵ�*/
int 
imem_node_list_clear(imem_node_list_t *handle);


#ifdef __cplusplus
}
#endif

#endif
/************************************End Of File**********************************/

