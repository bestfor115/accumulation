package com.accumulation.app.ui.broadcast;

import java.util.regex.Matcher;

import android.webkit.WebSettings;
import android.webkit.WebView;

import com.accumulation.app.ESTPConfig;
import com.accumulation.app.R;
import com.accumulation.app.base.BaseActivity;

public class ArticleHomeActivity extends BaseActivity {
	private WebView webView;

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_article_home;
	}

	@Override
	protected String getTopTitle() {
		// TODO Auto-generated method stub
		return "ндуб";
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		webView = (WebView) findViewById(R.id.webview);
		webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);
		webView.requestFocus();
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(false);
		webSettings.setSupportZoom(false);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
	}
	@Override
	protected void startLoadData() {
		// TODO Auto-generated method stub
		super.startLoadData();
		webView.loadUrl("file:///android_asset/index.html"); 
//		if (getIntent().getData() != null) {
//			String dataurl = getIntent().getData().getQueryParameter("id");
//			if (dataurl != null && !dataurl.equals("")) {
//				dataurl="fa8b033b-33cf-4ac3-a956-ff45bc123278";
//				webView.loadUrl(ESTPConfig.DOMAIN+"/article/index?id="+dataurl);
//			}
//		}
	}

}
