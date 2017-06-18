package com.accumulation.app.util;

import java.util.ArrayList;
import java.util.List;

import com.accumulation.app.data.ChooseFile;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

//�ļ�����
@SuppressLint("ParcelCreator")
public class FileTraversal implements Parcelable {
	public String filename;//����ͼƬ���ļ�����
	public List<ChooseFile> filecontent=new ArrayList<ChooseFile>();
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(filename);
		dest.writeList(filecontent);
	}
	
	public static final Parcelable.Creator<FileTraversal> CREATOR=new Creator<FileTraversal>() {
		
		@Override
		public FileTraversal[] newArray(int size) {
			return null;
		}
		
		@Override
		public FileTraversal createFromParcel(Parcel source) {
			FileTraversal ft=new FileTraversal();
			ft.filename= source.readString();
			ft.filecontent= source.readArrayList(FileTraversal.class.getClassLoader());
			
			return ft;
		}
		
		
	};
}
