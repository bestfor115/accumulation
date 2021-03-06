package com.accumulation.lib.utility.io;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.accumulation.lib.utility.matcher.StringUtils;

public class IOUtils {


	/**
	 * get an asset using ACCESS_STREAMING mode. This provides access to files that have been bundled with an
	 * application as assets -- that is, files placed in to the "assets" directory.
	 *
	 * @param context
	 * @param fileName The name of the asset to open. This name can be hierarchical.
	 * @return
	 */
	public static String geFileFromAssets(Context context, String fileName) {
		if (context == null || StringUtils.isEmpty(fileName)) {
			return null;
		}

		StringBuilder s = new StringBuilder("");
		try {
			InputStreamReader in = new InputStreamReader(context.getResources().getAssets().open(fileName));
			BufferedReader br = new BufferedReader(in);
			String line;
			while ((line = br.readLine()) != null) {
				s.append(line);
			}
			return s.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * get content from a raw resource. This can only be used with resources whose value is the name of an asset files
	 * -- that is, it can be used to open drawable, sound, and raw resources; it will fail on string and color
	 * resources.
	 *
	 * @param context
	 * @param resId The resource identifier to open, as generated by the appt tool.
	 * @return
	 */
	public static String geFileFromRaw(Context context, int resId) {
		if (context == null) {
			return null;
		}

		StringBuilder s = new StringBuilder();
		try {
			InputStreamReader in = new InputStreamReader(context.getResources().openRawResource(resId));
			BufferedReader br = new BufferedReader(in);
			String line;
			while ((line = br.readLine()) != null) {
				s.append(line);
			}
			return s.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * same to {@link IOUtils#geFileFromAssets(Context, String)}, but return type is List<String>
	 *
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static List<String> geFileToListFromAssets(Context context, String fileName) {
		if (context == null || StringUtils.isEmpty(fileName)) {
			return null;
		}

		List<String> fileContent = new ArrayList<String>();
		try {
			InputStreamReader in = new InputStreamReader(context.getResources().getAssets().open(fileName));
			BufferedReader br = new BufferedReader(in);
			String line;
			while ((line = br.readLine()) != null) {
				fileContent.add(line);
			}
			br.close();
			return fileContent;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * same to {@link IOUtils#geFileFromRaw(Context, int)}, but return type is List<String>
	 *
	 * @param context
	 * @param resId
	 * @return
	 */
	public static List<String> geFileToListFromRaw(Context context, int resId) {
		if (context == null) {
			return null;
		}

		List<String> fileContent = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			InputStreamReader in = new InputStreamReader(context.getResources().openRawResource(resId));
			reader = new BufferedReader(in);
			String line = null;
			while ((line = reader.readLine()) != null) {
				fileContent.add(line);
			}
			reader.close();
			return fileContent;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String loadAssetText(Context ctx, String fileName) {
		return loadAssetText(ctx, fileName, null);

	}

	public static String loadAssetText(Context ctx, String fileName, String encoding) {
		try {
			InputStream is = ctx.getAssets().open(fileName);
			String content = encoding != null ? new String(IS2ByteArray(is), encoding)
					: new String(IS2ByteArray(is));
			is.close();
			return content;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] IS2ByteArray(InputStream is) throws IOException {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		streamCopy(is, bao);
		byte[] result = bao.toByteArray();
		return result;
	}

	public static void streamCopy(InputStream is, OutputStream os) throws IOException {
		byte[] buf = new byte[8 * 1024];
		int len;
		while ((len = is.read(buf)) != -1) {
			os.write(buf, 0, len);
		}
		os.close();
		is.close();
	}
	
	public static void fileCopy(File from, File to) throws IOException {
		FileInputStream is = new FileInputStream(from);
		FileOutputStream os = new FileOutputStream(to);

		long offset = 0;
		long len = from.length();

		FileChannel isFc = is.getChannel();
		FileChannel osFc = os.getChannel();
		try {
			isFc.transferTo(offset, len, osFc);
		} finally {
			osFc.close();
			isFc.close();
			os.close();
			is.close();
		}
	}

	public static boolean saveSharedPreferencesToFile(SharedPreferences pref, File dst) {
		boolean res = false;
		ObjectOutputStream output = null;
		try {
			output = new ObjectOutputStream(new FileOutputStream(dst));

			output.writeObject(pref.getAll());

			res = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (output != null) {
					output.flush();
					output.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return res;
	}

	@SuppressWarnings({ "unchecked" })
	public static boolean loadSharedPreferencesFromFile(SharedPreferences pref, File src) {
		boolean res = false;
		ObjectInputStream input = null;
		try {
			input = new ObjectInputStream(new FileInputStream(src));
			Editor prefEdit = pref.edit();
			prefEdit.clear();
			Map<String, ?> entries = (Map<String, ?>) input.readObject();
			for (Entry<String, ?> entry : entries.entrySet()) {
				Object v = entry.getValue();
				String key = entry.getKey();

				if (v instanceof Boolean)
					prefEdit.putBoolean(key, ((Boolean) v).booleanValue());
				else if (v instanceof Float)
					prefEdit.putFloat(key, ((Float) v).floatValue());
				else if (v instanceof Integer)
					prefEdit.putInt(key, ((Integer) v).intValue());
				else if (v instanceof Long)
					prefEdit.putLong(key, ((Long) v).longValue());
				else if (v instanceof String)
					prefEdit.putString(key, ((String) v));
			}
			prefEdit.commit();
			res = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return res;
	}

	/**
	 * Close closable object and wrap {@link IOException} with {@link RuntimeException}
	 * @param closeable closeable object
	 */
	public static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				throw new RuntimeException("IOException occurred. ", e);
			}
		}
	}

	/**
	 * Close closable and hide possible {@link IOException}
	 * @param closeable closeable object
	 */
	public static void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				// Ignored
			}
		}
	}

}
