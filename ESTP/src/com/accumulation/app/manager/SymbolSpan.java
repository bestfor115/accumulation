package com.accumulation.app.manager;

import android.graphics.Color;
import android.os.Parcel;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class SymbolSpan extends ClickableSpan implements ParcelableSpan {

	public static final int SPAN_TYPE_ID=0xffff00;
	public static final int USER_TYPE=0;
	public static final int GROUP_TYPE=1;
	public static final int COMPANY_TYPE=2;
	public static final int VOTE_TYPE=3;

    private final String mURL;
    private final int mType;

    public SymbolSpan(String url,int type) {
        mURL = url;
        mType=type;
    }

    public SymbolSpan(Parcel src) {
        mURL = src.readString();
        mType = src.readInt();

    }
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
        dest.writeString(mURL);
        dest.writeInt(mType);
	}

	@Override
	public int getSpanTypeId() {
		// TODO Auto-generated method stub
		return SPAN_TYPE_ID;
	}

    public String getURL() {
        return mURL;
    }

    @Override
    public void onClick(View widget) {
//        Uri uri = Uri.parse(getURL());
//        Context context = widget.getContext();
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
//        context.startActivity(intent);
    }
    
    public void updateDrawState(TextPaint ds) {
        ds.setColor(Color.GRAY);
    }

}
