package com.zyl.push.sdk;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FilenameUtils;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.zyl.push.sdk.constant.Constant;
import com.zyl.push.sdk.model.FileStorage;
import com.zyl.push.sdk.model.GCMessage;
import com.zyl.push.sdk.script.ScriptManager;

public class FileTransportManager implements EventListener {
	private static String TAG = "FILE-transport";
	private static HashMap<String, FileTask> mTransporting = new HashMap<String, FileTask>();
	private static ConcurrentHashMap<String, FileStorage> fileTable = new ConcurrentHashMap<String, FileStorage>(16);

	private static volatile FileTransportManager mInstance;
	private Context mContext;
	private String mBaseDir;
	public static FileTransportManager getManager(Context context) {
		if (mInstance == null) {
			synchronized (FileTransportManager.class) {
				if (mInstance == null) {
					mInstance = new FileTransportManager(context);
				}
			}
		}
		return mInstance;
	}

	private FileTransportManager(Context context) {
		mContext = context.getApplicationContext();
		mBaseDir = mContext.getDir("translate", 0).getPath();
		ListenerManager.registerMessageListener(this, ScriptManager.getManager().getContext());
	}

	public void onReceiveFileTransport(GCMessage message) {

	}

	public void noticeTaskStarted(String key) {

	}

	public void requestFileTask(String from, String to, String path) {
		File file = new File(path);
		long fileSize = file.length();
		int blockSize = 256;
		String key = StringUtil.getKeyID();
		GCMessage message = GCMessage.createMessage(from, to, Constant.MessageType.TYPE_FILE_REQUEST);
		message.getBody().setContent(key);
		message.putExtra("fileName", file.getName());
		message.putExtra("md5", "");
		message.putExtra("blockSize", blockSize + "");
		message.putExtra("fileSize", fileSize + "");
		Intent serviceIntent = new Intent(mContext, PushService.class);
		serviceIntent.putExtra(PushManager.KEY_SEND_BODY, message);
		serviceIntent.setAction(PushManager.ACTION_SEND_REQUEST_BODY);
		mContext.startService(serviceIntent);
		FileTask task = new FileTask();
		task.from = from;
		task.to = to;
		task.path = path;
		task.blockSize = blockSize;
		task.done=false;
		task.fileSize=fileSize;
		mTransporting.put(key, task);
	}

	@Override
	public void onMessageReceived(final GCMessage message) {
		if (Constant.MessageType.TYPE_FILE_REQUEST.equals(message.getType())) {
			if (message.isReply()) {
				String key = message.getBody().getContent();
				if ((Constant.ReturnCode.CODE_200 + "").equals(message.getBody().getCode() + "")) {
					FileTask task = mTransporting.get(key);
					if(task==null){
						return ;
					}
					RandomAccessFile raf = null;
					FileChannel channel = null;
					File file = new File(task.path);
					int blockSize = task.blockSize;
					long fileSize=task.fileSize;
					long totalSize=task.fileSize;
					try {
						raf = new RandomAccessFile(file, "r");
						channel = raf.getChannel();
						long position = 0;
						ByteBuffer buffer = ByteBuffer.allocate(blockSize);
						int size = blockSize;
						while (mTransporting.get(key)!=null&&!mTransporting.get(key).done) {
							GCMessage send = GCMessage.createMessage(task.from, task.to,
									Constant.MessageType.TYPE_FILE_TRANSPORT);
							send.getBody().setContent(key);
							send.putExtra("position", position + "");
							channel.position(position);
							channel.read(buffer);
							if (position + blockSize >= fileSize) {
								size = (int) (file.length() - position);
								mTransporting.get(key).done = true;
								byte[] b = new byte[size];
								buffer.flip();
								buffer.get(b, 0, size);
								send.putExtra("current", bytes2HexString(b));
							} else {
								send.putExtra("current", bytes2HexString(buffer.array()));
							}
							buffer.clear();
							Intent serviceIntent = new Intent(mContext, PushService.class);
							serviceIntent.putExtra(PushManager.KEY_SEND_BODY, send);
							serviceIntent.setAction(PushManager.ACTION_SEND_REQUEST_BODY);
							mContext.startService(serviceIntent);
							position += size;
						}
						if(position !=totalSize){
							Toast.makeText(mContext, "发送失败 "+ position+"  "+totalSize, Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							channel.close();
							raf.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} else {
					mTransporting.remove(key);
					Toast.makeText(mContext, message.getBody().getCode() + " 请求你不成功", Toast.LENGTH_SHORT).show();
				}
			} else {
				replyFileTaskRequest(message);
				Toast.makeText(mContext, "向你发送了文件", Toast.LENGTH_SHORT).show();

				// AlertDialog.Builder builder = new
				// AlertDialog.Builder(mContext).setTitle("提示")
				// .setNegativeButton("取消", new OnClickListener() {
				// @Override
				// public void onClick(DialogInterface dialog, int which) {
				//
				// }
				// }).setPositiveButton("确定", new OnClickListener() {
				// @Override
				// public void onClick(DialogInterface dialog, int which) {
				// replyFileTaskRequest(message);
				// }
				// }).setMessage("向你发送了文件，是否接收？");
				// AlertDialog alert=builder.create();
				// alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				// alert.show();
			}
		}

		if (Constant.MessageType.TYPE_FILE_TRANSPORT.equals(message.getType())) {
			String key = message.getBody().getContent();
			if (message.isReply()) {
				if ((Constant.ReturnCode.CODE_200 + "").equals(message.getBody().getCode() + "")) {
					Toast.makeText(mContext, message.getBody().getCode() + " 传输成功", Toast.LENGTH_SHORT).show();
				}else{
					mTransporting.remove(key);
					Toast.makeText(mContext, message.getBody().getCode() + " 传输失败", Toast.LENGTH_SHORT).show();
				}
			}else{
				GCMessage reply = GCMessage.cloneMessage(message, true);
				reply.getBody().setContent(key);
				reply.setReply(true);
				FileStorage fileStorage = fileTable.get(key);
				if (fileStorage == null) {
					reply = null;
				} else {
					try {
						long position = Long.parseLong(message.getExtra("position"));
						byte[] current = hexStringToString(message.getExtra("current")).getBytes();
						fileStorage.write(position, current);
						if (fileStorage.isFinished()) {
							System.out.println("完成");
							fileStorage.close();
							fileTable.remove(key);
							reply.getBody().setCode(Constant.ReturnCode.CODE_200);
							reply.setReply(true);
						} else {
							reply = null;
						}
					} catch (Exception e) {
						reply.getBody().setCode(Constant.ReturnCode.CODE_FILE_TASK_EXCEPTION);
					}
				}
				if (reply != null) {
					Intent serviceIntent = new Intent(mContext, PushService.class);
					serviceIntent.putExtra(PushManager.KEY_SEND_BODY, reply);
					serviceIntent.setAction(PushManager.ACTION_SEND_REQUEST_BODY);
					mContext.startService(serviceIntent);
				}
			}
		}
	}

	private void replyFileTaskRequest(GCMessage message) {
		GCMessage reply = GCMessage.cloneMessage(message, true);
		reply.setReply(true);
		try {
			long fileSize = Long.parseLong(message.getExtra("fileSize"));
			int blockSize = Integer.parseInt(message.getExtra("blockSize"));
			String key = message.getBody().getContent();
			String md5 = message.getExtra("md5");
			String fileName = message.getExtra("fileName");
			String filePath = FilenameUtils.normalize(mBaseDir + File.separator + fileName);
			FileChannel fileChannel = new RandomAccessFile(filePath, "rw").getChannel();
			FileStorage fileStorage = new FileStorage(fileChannel, fileSize, md5, blockSize);
			fileTable.put(key, fileStorage);
			reply.getBody().setContent(key);
			reply.getBody().setCode(Constant.ReturnCode.CODE_200);
		} catch (Exception e) {
			reply.getBody().setCode(Constant.ReturnCode.CODE_FILE_TASK_EXCEPTION);
		}
		Intent serviceIntent = new Intent(mContext, PushService.class);
		serviceIntent.putExtra(PushManager.KEY_SEND_BODY, reply);
		serviceIntent.setAction(PushManager.ACTION_SEND_REQUEST_BODY);
		mContext.startService(serviceIntent);
	}

	@Override
	public void onNetworkChanged(NetworkInfo networkinfo) {

	}

	@Override
	public void onConnectionSuccessed(boolean hasAutoBind) {

	}

	@Override
	public void onConnectionClosed() {

	}

	@Override
	public void onConnectionFailed(Exception e) {

	}

	class FileTask {
		public String from;
		public String to;
		public String path;
		public int blockSize;
		public long fileSize;
		public boolean done=true;
	}

	public static String bytes2HexString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}

	public static String hexStringToString(String s) {
		if (s == null || s.equals("")) {
			return null;
		}
		s = s.replace(" ", "");
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(baKeyword, "utf-8");
			new String();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}
}
