package com.accumulation.lib.sociability.util;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.Context;
import com.accumulation.lib.sociability.SociabilityClient;

public class UploadHelper {
	
	/* �ϴ��ļ���Server�ķ��� */
	public static String uploadFile(Context cxt, String uploadURL,
			String path) {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		String result = null;
		try {
			URL url = new URL(uploadURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestMethod("POST");
			con.setRequestProperty("cookie", SociabilityClient.getClient().getLoginCookie());
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("contentType", "application/octet-stream");
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			/* ����DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; "
					+ "name=\"file1\";filename=\"" + path + "\"" + end);
			ds.writeBytes(end);
			/* ȡ���ļ���FileInputStream */
			FileInputStream fStream = new FileInputStream(path);
			/* ����ÿ��д��1024bytes */
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int length = -1;
			/* ���ļ���ȡ������������ */
			while ((length = fStream.read(buffer)) != -1) {
				/* ������д��DataOutputStream�� */
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			/* close streams */
			fStream.close();
			ds.flush();
			/* ȡ��Response���� */
			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			result=b.toString();
			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}


}
