package com.accumulation.lib.utility.base;

import android.content.Context;
import com.accumulation.lib.utility.debug.Logger;
import com.accumulation.lib.utility.io.FileUtils;
import com.accumulation.lib.utility.io.SDCardUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidParameterException;

/**
 * Created by zhangyl on 2016/7/30.
 */
public class DownLoadUtils {

    private static final String TAG = DownLoadUtils.class.getSimpleName();

    /**
     * 下载文件
     *@param cxt 上下文
     * @param downloadUrl 文件下载地址
     * @param folderName 文件保存目录
     * @param fileName 文件保存名
     * @param  threadNum 下载线程数
     * @param  listener 下载进度或者进度监听者
     * @param  cancelListener 下载是否已被取消
     * @return  下载结果
     * */
    public static Result syncDownloadFile(Context cxt,
                                          String downloadUrl,
                                          String folderName,
                                          String fileName,
                                          int threadNum,
                                          DownloadListener listener,
                                          DownLoadCancelListener cancelListener){

        Result result=new Result();
        if (listener != null) {
            listener.onStateUpdate(downloadUrl, DownloadListener.DOWNLOAD_STATE_START, null);
        }
        try {
            if(!SDCardUtils.isSDCardEnable()){
                throw new InvalidParameterException("SD卡不可用");
            }
            if(!FileUtils.makeDirs(folderName)){
                throw new InvalidParameterException("创建文件失败");
            }
            FileDownloadThead[] threads = new FileDownloadThead[threadNum];
            URL url = new URL(downloadUrl);
            Logger.d(TAG, "download file http path:" + downloadUrl);
            URLConnection conn = url.openConnection();
            // 读取下载文件总大小
            int fileSize = conn.getContentLength();
            if (fileSize <= 0) {
                result.setCode(Result.FAIL);
                result.setMsg("读取文件失败");
                throw new InvalidParameterException("获取文件大小失败");
            }
            // 设置ProgressBar最大的长度为文件Size
            if (listener != null) {
                listener.onStateUpdate(downloadUrl, DownloadListener.DOWNLOAD_STATE_INIT, fileSize);
            }

            // 计算每条线程下载的数据长度
            int blockSize = (fileSize % threadNum) == 0 ? fileSize / threadNum
                    : fileSize / threadNum + 1;

            Logger.d(TAG, "fileSize:" + fileSize + "  blockSize:");
            File file = new File(folderName+fileName);
            for (int i = 0; i < threads.length; i++) {
                // 启动线程，分别下载每个线程需要下载的部分
                threads[i] = new FileDownloadThead(url, file, blockSize,
                        (i + 1));
                threads[i].setName("Thread:" + i);
                threads[i].start();
            }

            boolean isfinished = false;
            boolean isCanceled = false;
            boolean isError =false;
            int downloadedAllSize = 0;
            if (listener != null) {
                listener.onStateUpdate(downloadUrl, DownloadListener.DOWNLOAD_STATE_PROGRESS, null);
                listener.onProgressUpdate(downloadUrl, 0.0f, fileSize, fileSize);
            }
            while (!isfinished) {
                isfinished = true;
                // 当前所有线程下载总量
                downloadedAllSize = 0;
                for (int i = 0; i < threads.length; i++) {
                    downloadedAllSize += threads[i].getDownloadLength();
                    if (!threads[i].isCompleted()) {
                        isfinished = false;
                    }
                    if(threads[i].isCanceled){
                        isCanceled=true;
                        isError=true;
                    }
                    if(cancelListener!=null&&cancelListener.isDownloadCanceled()){
                        isCanceled=true;
                    }
                    if(isCanceled){
                        isfinished=true;
                    }
                }
                if (listener != null) {
                    listener.onStateUpdate(downloadUrl, DownloadListener.DOWNLOAD_STATE_PROGRESS, null);
                    listener.onProgressUpdate(downloadUrl, (float) downloadedAllSize/fileSize, downloadedAllSize, fileSize);
                }
                Logger.d(TAG, "current downloadSize:" + downloadedAllSize);
                Thread.sleep(1000);// 休息1秒后再读取下载进度
            }
            if(isCanceled){
                for (int i = 0; i < threads.length; i++) {
                    threads[i].isCanceled =true;
                }
                throw new IOException(isError?"下載出錯":"用戶取消");
            }
            if (listener != null) {
                listener.onStateUpdate(downloadUrl, DownloadListener.DOWNLOAD_STATE_PROGRESS, null);
                listener.onProgressUpdate(downloadUrl, 1.0f, fileSize, fileSize);
            }
            Logger.d(TAG, " all of downloadSize:" + downloadedAllSize);
        }catch (InvalidParameterException e){
            result.setCode(Result.FAIL);
            result.setMsg(e.getMessage());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMsg("无效的下载地址");
        } catch (IOException e) {
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMsg("网络异常");
        } catch (InterruptedException e) {
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMsg("未知错误");
        }

        if(listener!=null){
            if(result.isSuccess()){
                listener.onStateUpdate(downloadUrl,DownloadListener.UPLOAD_STATE_SUCCESS,null);
            }else{
                listener.onStateUpdate(downloadUrl,DownloadListener.DOWNLOAD_STATE_ERROR,result.getMsg());
            }
            listener.onStateUpdate(downloadUrl,DownloadListener.DOWNLOAD_STATE_COMPLETE,null);
        }
        return result;
    }
    /***
     * 是否取消下载
     * */
    public static interface DownLoadCancelListener{
        /**
         * 是否已经取消下载
         * */
        public boolean isDownloadCanceled();
    }
    /***
     * 上传回调接口
     * */
    public static interface DownloadListener {
        /**
         * 下载开始
         * */
        public static final int DOWNLOAD_STATE_START=0;
        /**
         * 下载进度更新
         * */
        public static final int DOWNLOAD_STATE_INIT=1;
        /**
         * 下载进度更新
         * */
        public static final int DOWNLOAD_STATE_PROGRESS=2;
        /**
         * 下载结束
         * */
        public static final int DOWNLOAD_STATE_COMPLETE=3;
        /**
         * 下载成功
         * */
        public static final int UPLOAD_STATE_SUCCESS=4;
        /**
         * 下载失败
         * */
        public static final int DOWNLOAD_STATE_ERROR=-1;

        /***
         * 上传状态改变状态
         * */
        public void onStateUpdate(String path, int state, Object message);
        /**
         * 上传进度更新回调
         * */
        public void onProgressUpdate(String path, float percent, long cur, long max);
    }

    private static class FileDownloadThead extends Thread{
        private static final String TAG = FileDownloadThead.class.getSimpleName();

        /** 当前下载是否完成 */
        private boolean isCompleted = false;
        /** 当前下载强制结束 */
        private boolean isCanceled = false;
        /** 当前下载文件长度 */
        private int downloadLength = 0;
        /** 文件保存路径 */
        private File file;
        /** 文件下载路径 */
        private URL downloadUrl;
        /** 当前下载线程ID */
        private int threadId;
        /** 线程下载数据长度 */
        private int blockSize;

        public FileDownloadThead(URL downloadUrl, File file, int blocksize,
                                 int threadId) {
            this.downloadUrl = downloadUrl;
            this.file = file;
            this.threadId = threadId;
            this.blockSize = blocksize;
        }
        @Override
        public void run() {
            BufferedInputStream bis = null;
            RandomAccessFile raf = null;

            try {
                URLConnection conn = downloadUrl.openConnection();
                conn.setAllowUserInteraction(true);
                int startPos = blockSize * (threadId - 1);//开始位置
                int endPos = blockSize * threadId - 1;//结束位置
                //设置当前线程下载的起点、终点
                conn.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
                System.out.println(Thread.currentThread().getName() + "  bytes="
                        + startPos + "-" + endPos);

                byte[] buffer = new byte[1024];
                bis = new BufferedInputStream(conn.getInputStream());

                raf = new RandomAccessFile(file, "rwd");
                raf.seek(startPos);
                int len;
                while ((len = bis.read(buffer, 0, 1024)) != -1&&!isCanceled) {
                    raf.write(buffer, 0, len);
                    downloadLength += len;
                }
                isCompleted = true;
                Logger.d(TAG, "current thread task has finished,all size:"
                        + downloadLength);
            } catch (IOException e) {
                e.printStackTrace();
                isCanceled =true;
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (raf != null) {
                    try {
                        raf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        /**
         * 线程文件是否下载完毕
         */
        public boolean isCompleted() {
            return isCompleted;
        }
        /**
         * 线程文件是否下载错误
         */
        public boolean isCanceled(){
            return isCanceled;
        }

        /**
         * 线程下载文件长度
         */
        public int getDownloadLength() {
            return downloadLength;
        }
    }
}
