package com.accumulation.app.util;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.accumulation.app.R;

public class FileUtil {
	public static int caculeFileType(File entry) {
		if (entry == null) {
			return R.drawable.back;
		}
		if (entry.isDirectory()) {
			return R.drawable.format_folder;
		} else {
			String path = entry.getAbsolutePath();
			if (isValidMusicFile(path)) {
				return R.drawable.format_music;
			} else if (isValidPictureFile(path)) {
				return R.drawable.format_picture;
			} else if (isValidVideoFile(path)) {
				return R.drawable.format_media;
			}else if(isTxtFile(path)){
				return R.drawable.format_text;
			}else if(isPPTFile(path)){
				return R.drawable.format_ppt;
			}else if(isPDFFile(path)){
				return R.drawable.format_pdf;
			}else if(isZIPFile(path)){
				return R.drawable.format_zip;
			}else if(isDOCFile(path)){
				return R.drawable.format_word;
			}else if(isHtmlFile(path)){
				return R.drawable.format_html;
			} else {
				return R.drawable.format_unkown;
			}
		}
	}
	
//	public static int caculateKnowledgeType(Knowledge entry) {
//		if (entry == null) {
//			return R.drawable.back;
//		}
//		if (entry.IsDir) {
//			return R.drawable.format_folder;
//		} else {
//			String path = entry.Name;
//			if (isValidMusicFile(path)) {
//				return R.drawable.format_music;
//			} else if (isValidPictureFile(path)) {
//				return R.drawable.format_picture;
//			} else if (isValidVideoFile(path)) {
//				return R.drawable.format_media;
//			}else if(isTxtFile(path)){
//				return R.drawable.format_text;
//			}else if(isPPTFile(path)){
//				return R.drawable.format_ppt;
//			}else if(isPDFFile(path)){
//				return R.drawable.format_pdf;
//			}else if(isZIPFile(path)){
//				return R.drawable.format_zip;
//			}else if(isDOCFile(path)){
//				return R.drawable.format_word;
//			}else if(isHtmlFile(path)){
//				return R.drawable.format_html;
//			} else {
//				return R.drawable.format_unkown;
//			}
//		}
//	}
	public static boolean isPPTFile(String path) {
		String end = path.substring(path.lastIndexOf(".") + 1, path.length())
				.toLowerCase();
		if (end.equals("ppt") ) {
			return true;
		}
		return false;
	}
	
	public static String getFileEnd(File f){
		String path=f.getName();
		return 	 path.substring(path.lastIndexOf(".") + 1, path.length())
				.toLowerCase();
	}
	public static boolean isDOCFile(String path) {
		String end = path.substring(path.lastIndexOf(".") + 1, path.length())
				.toLowerCase();
		if (end.equals("doc") || end.equals("docx") ) {
			return true;
		}
		return false;
	}
	public static boolean isZIPFile(String path) {
		String end = path.substring(path.lastIndexOf(".") + 1, path.length())
				.toLowerCase();
		if (end.equals("zip") ) {
			return true;
		}
		return false;
	}
	public static boolean isPDFFile(String path) {
		String end = path.substring(path.lastIndexOf(".") + 1, path.length())
				.toLowerCase();
		if (end.equals("pdf") ) {
			return true;
		}
		return false;
	}
	public static boolean isTxtFile(String path) {
		String end = path.substring(path.lastIndexOf(".") + 1, path.length())
				.toLowerCase();
		if (end.equals("txt") || end.equals("log") || end.equals("config")
				|| end.equals("ini") ) {
			return true;
		}
		return false;
	}
	public static boolean isHtmlFile(String path) {
		String end = path.substring(path.lastIndexOf(".") + 1, path.length())
				.toLowerCase();
		if (end.equals("html") || end.equals("xml") ) {
			return true;
		}
		return false;
	}
	public static boolean isWordFile(String path) {
		String end = path.substring(path.lastIndexOf(".") + 1, path.length())
				.toLowerCase();
		if (end.equals("wav") || end.equals("mp3") || end.equals("wma")
				|| end.equals("ogg") || end.equals("ape") || end.equals("rtx")
				|| end.equals("flac") || end.equals("xmf") || end.equals("acc")
				|| end.equals("wav")) {
			return true;
		}

		return false;
	}
	public static boolean isValidMusicFile(String path) {
		String end = path.substring(path.lastIndexOf(".") + 1, path.length())
				.toLowerCase();
		// return
		// MimeTypeMap.getSingleton().getExtensionFromMimeType(end).startsWith("audio");
		if (end.equals("wav") || end.equals("mp3") || end.equals("wma")
				|| end.equals("ogg") || end.equals("ape") || end.equals("rtx")
				|| end.equals("flac") || end.equals("xmf") || end.equals("acc")
				|| end.equals("wav")) {
			return true;
		}

		return false;
	}

	public static boolean isValidPictureFile(String path) {
		String end = path.substring(path.lastIndexOf(".") + 1, path.length())
				.toLowerCase();
		if (end.equals("jpg") || end.equals("png") || end.equals("gif")
				|| end.equals("webp") || end.equals("bmp")) {
			return true;
		}
		return false;
	}

	public static boolean isValidVideoFile(String path) {
		String end = path.substring(path.lastIndexOf(".") + 1, path.length())
				.toLowerCase();
		if (end.equals("vga") || end.equals("mp4") || end.equals("avi")
				|| end.equals("wmv") || end.equals("rmvb") || end.equals("3gp")
				|| end.equals("rm") || end.equals("flv") || end.equals("mpg")
				|| end.equals("ts") || end.equals("mkv") || end.equals("mov")
				|| end.equals("f4v")) {
			return true;
		}
		return false;
	}

	public boolean isValidSubTitleFile(String path) {
		String end = path.substring(path.lastIndexOf(".") + 1, path.length())
				.toLowerCase();
		if (end.equals("srt") || end.equals("ass") || end.equals("scc")
				|| end.equals("stl") || end.equals("xml") || end.equals("sub")) {
			return true;
		}
		return false;
	}
	/**
	 * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
	 * @param activity
	 * @param imageUri
	 * @author yaoxing
	 * @date 2014-10-12
	 */
	@TargetApi(19)
	public static String getImageAbsolutePath(Activity context, Uri imageUri) {
		if (context == null || imageUri == null)
			return null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
			if (isExternalStorageDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
			} else if (isDownloadsDocument(imageUri)) {
				String id = DocumentsContract.getDocumentId(imageUri);
				Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			} else if (isMediaDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				String selection = MediaStore.Images.Media._ID + "=?";
				String[] selectionArgs = new String[] { split[1] };
				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		} // MediaStore (and general)
		else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(imageUri))
				return imageUri.getLastPathSegment();
			return getDataColumn(context, imageUri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
			return imageUri.getPath();
		}
		return null;
	}

	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = null;
		String column = MediaStore.Images.Media.DATA;
		String[] projection = { column };
		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}
}
