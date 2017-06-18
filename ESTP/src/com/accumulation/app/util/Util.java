package com.accumulation.app.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.accumulation.app.data.ChooseFile;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

public class Util {
	
	Context context;
	long time;
	
	public Util(Context context,long time) {
		this.context=context;
		this.time=time;
	}
	
	/**
	 * ��ȡȫ��ͼƬ��ַ
	 * @return
	 */
	public ArrayList<String>  listAlldir(){
    	Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    	Uri uri = intent.getData();
    	ArrayList<String> list = new ArrayList<String>();
    	String[] proj ={MediaStore.Images.Media.DATA};
    	Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);//managedQuery(uri, proj, null, null, null);
    	while(cursor.moveToNext()){
    		String path =cursor.getString(0);
    		list.add(new File(path).getAbsolutePath());
    	}
		return list;
    }
	
	public List<FileTraversal> LocalImgFileList(){
		ArrayList<FileTraversal> data=new ArrayList<FileTraversal>();
		String filename="";
		List<String> allimglist=listAlldir();
		List<String> retulist=new ArrayList<String>();
		if (allimglist!=null) {
			Set set = new TreeSet();
			String []str;
			for (int i = 0; i < allimglist.size(); i++) {
				retulist.add(getfileinfo(allimglist.get(i)));
			}
			for (int i = 0; i < retulist.size(); i++) {
				set.add(retulist.get(i));
			}
			str= (String[]) set.toArray(new String[0]);
			for (int i = 0; i < str.length; i++) {
				filename=str[i];
				FileTraversal ftl= new FileTraversal();
				ftl.filename=filename;
				data.add(ftl);
			}
			
			for (int i = 0; i < data.size(); i++) {
				for (int j = allimglist.size()-1; j >=0 ; j--) {
					if (data.get(i).filename.equals(getfileinfo(allimglist.get(j)))) {
						ChooseFile choose=new ChooseFile(allimglist.get(j), time);
						data.get(i).filecontent.add(choose);
					}
				}
			}
		}
		FileTraversal all= new FileTraversal();
		all.filename="����ͼƬ";
		all.filecontent=new ArrayList<ChooseFile>();
		int N=data.size();
		for (int i = 0; i < N; i++) {
			all.filecontent.addAll(data.get(i).filecontent);
		}
		data.add(0, all);
		return data;
	}
	
	//��ʾԭ��ͼƬ�ߴ��С
	public Bitmap getPathBitmap(Uri imageFilePath,int dw,int dh)throws FileNotFoundException{
		//��ȡ��Ļ�Ŀ�͸�  
        /** 
         * Ϊ�˼������ŵı�����������Ҫ��ȡ����ͼƬ�ĳߴ磬������ͼƬ 
         * BitmapFactory.Options������һ�������ͱ���inJustDecodeBounds����������Ϊtrue 
         * ���������ǻ�ȡ���ľ���ͼƬ�ĳߴ磬�����ü���ͼƬ�ˡ� 
         * �������������ֵ��ʱ�����ǽ��žͿ��Դ�BitmapFactory.Options��outWidth��outHeight�л�ȡ��ֵ 
         */  
        BitmapFactory.Options op = new BitmapFactory.Options();  
        op.inJustDecodeBounds = true;  
        //����ʹ����MediaStore�洢���������URI��ȡ����������ʽ    
        Bitmap pic = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageFilePath),  
                null, op);
        
        int wRatio = (int) Math.ceil(op.outWidth / (float) dw); //�����ȱ���  
        int hRatio = (int) Math.ceil(op.outHeight / (float) dh); //����߶ȱ���
        
        /** 
         * �����������Ǿ���Ҫ�ж��Ƿ���Ҫ�����Լ����׶Կ��Ǹ߽������š� 
         * ����ߺͿ���ȫ����������Ļ����ô�������š� 
         * ����ߺͿ���������Ļ��С�������ѡ�������ء� 
         * ����Ҫ�ж�wRatio��hRatio�Ĵ�С 
         * ���һ���������ţ���Ϊ���Ŵ��ʱ��С��Ӧ���Զ�����ͬ�������š� 
         * ����ʹ�õĻ���inSampleSize���� 
         */  
        if (wRatio > 1 && hRatio > 1) {  
            if (wRatio > hRatio) {  
                op.inSampleSize = wRatio;  
            } else {  
                op.inSampleSize = hRatio;  
            }  
        }  
        op.inJustDecodeBounds = false; //ע�����һ��Ҫ����Ϊfalse����Ϊ�������ǽ�������Ϊtrue����ȡͼƬ�ߴ���  
        pic = BitmapFactory.decodeStream(context.getContentResolver()  
                .openInputStream(imageFilePath), null, op);  
        
        return pic;
	}
	
	public String getfileinfo(String data){
		String filename[]= data.split("/");
		if (filename!=null) {
			return filename[filename.length-2];
		}
		return null;
	}
	
	public void imgExcute(ImageView imageView,ImgCallBack icb, String... params){
		LoadBitAsynk loadBitAsynk=new LoadBitAsynk(imageView,icb);
		loadBitAsynk.execute(params);
	}
	
	public class LoadBitAsynk extends AsyncTask<String, Integer, Bitmap>{

		ImageView imageView;
		ImgCallBack icb;
		
		LoadBitAsynk(ImageView imageView,ImgCallBack icb){
			this.imageView=imageView;
			this.icb=icb;
		}
		
		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bitmap=null;
			try {
				if (params!=null) {
					for (int i = 0; i < params.length; i++) {
						bitmap=getPathBitmap(Uri.fromFile(new File(params[i])), 200, 200);
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if (result!=null) {
//				imageView.setImageBitmap(result);
				icb.resultImgCall(imageView, result);
			}
		}
		
		
	}
	
}
