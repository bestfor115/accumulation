package com.accumulation.app.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.accumulation.app.AccumulationAPP;
import com.accumulation.app.config.Config;
import com.accumulation.app.manager.SaveManager;
import com.accumulation.lib.sociability.data.JDUpload;
import com.accumulation.lib.tool.debug.Logger;
import com.accumulation.lib.tool.net.http.AsyncHttpClient;
import com.accumulation.lib.tool.net.http.RequestParams;
import com.accumulation.lib.tool.time.TimeFormateTool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HttpHelper {

	public final static String LINE_SEPARATOR = System
			.getProperty("line.separator");

	static ExecutorService threadPool = Executors.newSingleThreadExecutor();

	public static String md5(String s) {
		try {
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String convertStreamToString(InputStream is) {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				is));
		final StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + LINE_SEPARATOR);
			}
		} catch (IOException e) {
		} finally {
			try {
				is.close();
			} catch (IOException e) {

			}
		}
		return sb.toString();
	}

	public static void loadImage(String urlStr, ImageView imageView) {
		loadImage(urlStr, imageView, 0, 0);
	}

	public static void loadImage(String urlStr, ImageView imageView, int width,
			int height) {
		try {
			URL url = new URL(urlStr);
			ImageLoader imageLoader = ImageLoader.getInstance();
			/*
			 * LinearLayout.LayoutParams layoutParams = new
			 * LinearLayout.LayoutParams( width, height);
			 * layoutParams.leftMargin = 5; layoutParams.rightMargin = 5;
			 * layoutParams.topMargin = 5;
			 * imageView.setLayoutParams(layoutParams);
			 */

			if (url != null) {
				imageLoader.displayImage(url.toString(), imageView);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadAvatarImage(String urlStr, ImageView imageView) {
		loadImage(urlStr, imageView, 96, 96);
	}

	public static InputStream getInputStreamFromURL(String urlstr) {
		HttpURLConnection connection;
		URL url;
		InputStream stream = null;
		try {
			url = new URL(urlstr);
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			stream = connection.getInputStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return stream;
	}

	public static boolean uploadFile(File file, String url, Context cxt) {
		Logger.d("start upload file");
		if (!file.exists()) {
			return false;
		}
		Logger.d("file path:" + file.getPath());

		boolean result = false;
		PostMethod postMethod = new PostMethod(url);
		try {
			// FilePart�������ϴ��ļ�����
			FilePart fp = new FilePart("filedata", file);
			Part[] parts = { fp };
			// ����MIME���͵�����httpclient����ȫ��MulitPartRequestEntity���а�װ
			MultipartRequestEntity mre = new MultipartRequestEntity(parts,
					postMethod.getParams());
			postMethod.setRequestEntity(mre);
			HttpClient client = new HttpClient();
			client.getHttpConnectionManager().getParams()
					.setConnectionTimeout(50000);// ��������ʱ��
//			client.getHttpConnectionManager()
//					.getParams()
//					.setParameter("cookie",
//							SaveManager.getInstance(cxt).getLoginCookie());
			int status = client.executeMethod(postMethod);
			result = status == HttpStatus.SC_OK;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			// �ͷ�����
			postMethod.releaseConnection();
		}
		Logger.e("upgrade result :" + result);
		return result;
	}

	/* �ϴ��ļ���Server�ķ��� */
	public static String uploadFile(Context cxt, String upgrade_url,
			String path, boolean flag) {
		Logger.d("upload :" + upgrade_url);
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		String result = null;
		try {
			if (flag) {
				upgrade_url += "&w=240&h=240";
			}
			URL url = new URL(upgrade_url);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* ����Input��Output����ʹ��Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* ���ô��͵�method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
//			con.setRequestProperty("cookie", SaveManager.getInstance(cxt)
//					.getLoginCookie());
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

			/* ��Response��ʾ��Dialog */
			Logger.d("�ϴ��ɹ�" + b.toString().trim());
			try {
				if (flag) {
					GsonBuilder builder = new GsonBuilder();
					Gson gson = builder.create();
					JDUpload upload = gson.fromJson(b.toString().trim(),
							JDUpload.class);
					if (upload != null && upload.success) {
						result = upload.data.storeResult.imgId;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			/* �ر�DataOutputStream */
			ds.close();
		} catch (Exception e) {
			Logger.d("�ϴ�ʧ��" + e);
		}
		Logger.d("result id :" + result);
		return result;
	}

	/* �ϴ��ļ���Server�ķ��� */
	public static boolean uploadFile(Context cxt, String upgrade_url,
			String path, RequestParams params) {
		Logger.d("upload :" + upgrade_url);
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		boolean result = true;
		try {
			upgrade_url = AsyncHttpClient.getUrlWithQueryString(upgrade_url,
					params);
			URL url = new URL(upgrade_url);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* ����Input��Output����ʹ��Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* ���ô��͵�method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
//			con.setRequestProperty("cookie", SaveManager.getInstance(cxt)
//					.getLoginCookie());
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
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
			StringBuffer b = new StringBuffer();

			// int ch;
			// while ((ch = is.read()) != -1) {
			// b.append((char) ch);
			// }
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String line = null;
			while ((line = reader.readLine()) != null) {
				b.append(line + "\n");
			}
			Logger.d("" + b.toString().trim());
			/* �ر�DataOutputStream */
			ds.close();
		} catch (Exception e) {
			Logger.d("�ϴ�ʧ��" + e);
			result = false;
		}
		Logger.d("result :" + result);
		return result;
	}

	public static String convertStreamToString1(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		StringBuilder sb = new StringBuilder();

		String line = null;

		try {

			while ((line = reader.readLine()) != null) {

				sb.append(line + "/n");

			}

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				is.close();

			} catch (IOException e) {

				e.printStackTrace();

			}

		}

		return sb.toString();

	}

	public static void getHtmlTitle(final String htmlUrl, final TitleCallback c) {
		threadPool.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String htmlSource = "";
				htmlSource = getHtmlSource(htmlUrl);// ��ȡhtmlUrl��ַ��ҳ��Դ��
				final String title = getTitle(htmlSource);
				AccumulationAPP.getInstance().handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						c.onGetTitle(title);
					}
				});
			}
		});
	}

	public interface TitleCallback {
		public void onGetTitle(String title);
	}

	/**
	 * ������ַ������ҳ��Դ��
	 * 
	 * @param htmlUrl
	 * @return
	 */
	private static String getHtmlSource(String htmlUrl) {
		URL url;
		StringBuffer sb = new StringBuffer();
		try {
			url = new URL(htmlUrl);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream(), "UTF-8"));// ��ȡ��ҳȫ������
			String temp;
			while ((temp = in.readLine()) != null) {
				sb.append(temp);
			}
			in.close();
		} catch (MalformedURLException e) {
			System.out.println("�������URL��ʽ�����⣡����ϸ����");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * ��htmlԴ��(�ַ���)��ȥ������
	 * 
	 * @param htmlSource
	 * @return
	 */
	private static String getTitle(String htmlSource) {
		List<String> list = new ArrayList<String>();
		String title = "";

		// Pattern pa = Pattern.compile("<title>.*?</title>",
		// Pattern.CANON_EQ);Ҳ����
		Pattern pa = Pattern.compile("<title>.*?</title>");// Դ���б���������ʽ
		Matcher ma = pa.matcher(htmlSource);
		while (ma.find())// Ѱ�ҷ���el���ִ�
		{
			list.add(ma.group());// ������el���ִ����뵽list��
		}
		for (int i = 0; i < list.size(); i++) {
			title = title + list.get(i);
		}
		return outTag(title);
	}

	/**
	 * ȥ��htmlԴ���еı�ǩ
	 * 
	 * @param s
	 * @return
	 */
	private static String outTag(String s) {
		return s.replaceAll("<.*?>", "");
	}

	public static String saveBitmap(Bitmap bmp) {
		File f = new File(
				Config.IMAGE_FILE_PATH,
				TimeFormateTool
						.getCurrentTime(TimeFormateTool.FORMAT_DATE_TIME_FOR_SAVE)
						+ ".jpg");
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return f.getPath();
	}

}