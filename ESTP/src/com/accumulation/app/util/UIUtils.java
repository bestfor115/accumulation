package com.accumulation.app.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.accumulation.app.R;
import com.accumulation.app.view.RotateLoading;

public class UIUtils {
	/**
	 * @param context
	 * @return
	 */
	public static ProgressDialog createLoadingDlg(Context context) {

		final ProgressDialog mypDialog = new ProgressDialog(context);
		mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mypDialog.setMessage("Мгдижа...");
		mypDialog.show();
		return mypDialog;
	}

	public static Dialog createLoadingDlg(Context cxt, String msg) {

		final ProgressDialog mypDialog = new ProgressDialog(cxt);
		mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mypDialog.setMessage(msg);
		mypDialog.show();
		return mypDialog;
	}

	public static Dialog createLoadingDlg(Context cxt, int msg) {

		final ProgressDialog mypDialog = new ProgressDialog(cxt);
		mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mypDialog.setMessage(cxt.getString(msg));
		mypDialog.show();
		return mypDialog;
	}
	public static Dialog showLoadingDialog(Context cxt) {
		return showLoadingDialog(cxt,false);
	}
	public static Dialog showLoadingDialog(Context cxt,boolean focus) {
		View  mContentView= LayoutInflater.from(cxt).inflate(
				R.layout.pop_loading, null, false);
		final Dialog dialog = new Dialog(cxt, R.style.dialog);
		dialog.setContentView(mContentView);
		try{
			dialog.show();
		}catch(Exception e){
			
		}
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(true);
		return dialog;
	}
	
	public static Dialog createLoadingDialog(Context cxt,boolean focus) {
		View  mContentView= LayoutInflater.from(cxt).inflate(
				R.layout.pop_loading, null, false);
		final RotateLoading vloading=(RotateLoading) mContentView.findViewById(R.id.loading);
		vloading.start();
		final Dialog dialog = new Dialog(cxt, R.style.dialog);
		dialog.setContentView(mContentView);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(true);
		dialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				vloading.stop();
			}
		});
		return dialog;
	}
	
	
	public static void hideDialog(Context cxt){
		
	}

	public static void showTast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static void showTast(Context context, int msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	
	public static  int getThemeResource(Context context,int attr){
		TypedValue typedValue = new TypedValue();
		context.getTheme().resolveAttribute(attr, typedValue, true);
		return typedValue.resourceId;
	}
}
