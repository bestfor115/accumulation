package com.accumulation.lib.utility.base;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import android.content.Context;
import android.support.annotation.NonNull;
import com.accumulation.lib.utility.io.FileUtils;
import com.accumulation.lib.utility.matcher.MatcherUtils;

public class UploadUtils {

	/**
	 * 上次文件到服务器
	 *
	 * @param cxt 上下文
	 * @param path 文件本地地址
	 * @param  fileName 上次文件保存名字
	 * @param uploadURL 服务器地址
	 * @param charset 编码
	 * @param  headers 请求头
	 * @param params 请求参数
	 * @return 服务器上传返回结果
	 * */
	public static Result syncUploadFile(@NonNull Context cxt,
										@NonNull String path,
										String fileName,
										@NonNull String uploadURL,
										String charset,
										Map<String, String> headers,
										Map<String, String> params,
										UploadListener listener) {
		Result result = new Result();
		if (listener!=null){
			listener.onStateUpdate(path, UploadListener.UPLOAD_STATE_START,null);
		}
		if(!FileUtils.isFileExist(path)){
			result.setCode(Result.FAIL);
			result.setMsg("无效文件");
		}else{
			if(MatcherUtils.isEmpty(fileName)){
				fileName=FileUtils.getFileName(path);
			}
			if(MatcherUtils.isEmpty(charset)){
				charset="UTF-8";
			}
			String BOUNDARY = java.util.UUID.randomUUID().toString();
			String PREFIX = "--";
			String LINEND = "\r\n";
			String CHARSET = "UTF-8";
			try {
				URL url = new URL(uploadURL);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setUseCaches(false);
				conn.setReadTimeout(5 * 1000);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("Charset", charset);
				conn.setRequestProperty("contentType", "application/octet-stream");
				conn.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + BOUNDARY);
				if(headers!=null){
					for (Map.Entry<String, String> entry : headers.entrySet()) {
						conn.setRequestProperty(entry.getKey(), entry.getValue());
					}
				}

			/* 设置DataOutputStream */
				DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
				if(params!=null){
					// 首先组拼文本类型的参数
					StringBuilder sb = new StringBuilder();
					for (Map.Entry<String, String> entry : params.entrySet()) {
						sb.append(PREFIX);
						sb.append(BOUNDARY);
						sb.append(LINEND);
						sb.append("Content-Disposition: form-data; name=\""
								+ entry.getKey() + "\"" + LINEND);
						sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
						sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
						sb.append(LINEND);
						sb.append(entry.getValue());
						sb.append(LINEND);
					}
					dos.write(sb.toString().getBytes());
				}
				dos.writeBytes(PREFIX + BOUNDARY + LINEND);
				dos.writeBytes("Content-Disposition: form-data; "
						+ "name=\"file1\";filename=\"" + fileName + "\"" + LINEND);
				dos.writeBytes(LINEND);

			/* 取得文件的FileInputStream */
				FileInputStream fs = new FileInputStream(path);
				long maxSize=FileUtils.getFileSize(path);
				long curSize=0;
			/* 设置每次写入1024bytes */
				int bufferSize = 1024;
				byte[] fileBuffer = new byte[bufferSize];
				int length = -1;
				if(listener!=null){
					listener.onProgressUpdate(path,0f,curSize,maxSize);
				}
				if (listener!=null){
					listener.onStateUpdate(path, UploadListener.UPLOAD_STATE_PROGRESS,null);
				}
			/* 从文件读取数据至缓冲区 */
				while ((length = fs.read(fileBuffer)) != -1) {
				/* 将资料写入DataOutputStream中 */
					dos.write(fileBuffer, 0, length);
					curSize+=length;
					if(listener!=null){
						listener.onProgressUpdate(path,(float)curSize/maxSize,curSize,maxSize);
					}
					if (listener!=null){
						listener.onStateUpdate(path, UploadListener.UPLOAD_STATE_PROGRESS,null);
					}
				}
				dos.writeBytes(LINEND);
				dos.writeBytes(PREFIX + BOUNDARY + PREFIX + LINEND);
			/* close streams */
				fs.close();
				dos.flush();
				if(listener!=null){
					listener.onProgressUpdate(path,1.0f,maxSize,maxSize);
				}
				if (listener!=null){
					listener.onStateUpdate(path, UploadListener.UPLOAD_STATE_PROGRESS,null);
				}
			/* 取得Response内容 */
				InputStream is = conn.getInputStream();
				int ch;
				StringBuffer responseBuffer = new StringBuffer();
				while ((ch = is.read()) != -1) {
					responseBuffer.append((char) ch);
				}
				result.setExtra(responseBuffer.toString());
				dos.close();
				result.setMsg("上传成功");
				result.setCode(Result.SUCCESS);
			}catch (IOException e){
				result.setCode(Result.FAIL);
				result.setExtra(e.getMessage());
				result.setMsg("网络异常");
			}catch (Exception e){
				result.setCode(Result.FAIL);
				result.setExtra(e.getMessage());
				result.setMsg("未知错误");
			}
		}
		if (listener!=null){
			if(result.isSuccess()){
				listener.onStateUpdate(path, UploadListener.UPLOAD_STATE_SUCCESS,result.getExtra());
			}else{
				listener.onStateUpdate(path, UploadListener.UPLOAD_STATE_ERROR,result.getMsg());
			}
			listener.onStateUpdate(path, UploadListener.UPLOAD_STATE_COMPLETE,null);
		}
		return result;
	}

	/***
	 * 上传回调接口
	 * */
	public interface UploadListener {
		/**
		 * 上传开始
		 * */
		public static final int UPLOAD_STATE_START=0;
		/**
		 * 上传进度更新
		 * */
		public static final int UPLOAD_STATE_PROGRESS=1;
		/**
		 * 上传结束
		 * */
		public static final int UPLOAD_STATE_COMPLETE=2;
		/**
		 * 上传成功
		 * */
		public static final int UPLOAD_STATE_SUCCESS=3;
		/**
		 * 上传失败
		 * */
		public static final int UPLOAD_STATE_ERROR=-1;

		/***
		 * 上传状态改变状态
		 * */
		public void onStateUpdate(String path, int state, Object message);
		/**
		 * 上传进度更新回调
		 * */
		public void onProgressUpdate(String path, float percent, long cur, long max);
	}
}
