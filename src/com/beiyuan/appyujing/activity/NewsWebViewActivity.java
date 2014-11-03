package com.beiyuan.appyujing.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.beiyuan.appyujing.MyActivity;
import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.StudentActivity;
import com.beiyuan.appyujing.view.TitleView;
import com.beiyuan.appyujing.view.TitleView.OnLeftButtonClickListener;
import com.beiyuan.appyujing.view.TitleView.OnRightButtonClickListener;

public class NewsWebViewActivity extends MyActivity {
	private WebView webview;
	private TitleView mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 实例化WebView对象
		Intent intent = getIntent();
		final String url = intent.getStringExtra("Url");
		Log.i("NEWS", "url=" + url);

		setContentView(R.layout.webview_activity);
		mTitle = (TitleView) findViewById(R.id.title);
		mTitle.setTitle("校园新闻");
		webview = (WebView) findViewById(R.id.webview);
		// 设置WebView属性，能够执行Javascript脚本
		webview.getSettings().setJavaScriptEnabled(true);
		// 加载需要显示的网页
		webview.loadUrl(url);
		// webview.loadUrl("http://www.baidu.com");
		// 设置Web视图
		webview.setWebViewClient(new HelloWebViewClient());
		Drawable leftImg = getResources().getDrawable(R.drawable.back_img);
		Drawable rightImg = getResources().getDrawable(R.drawable.refuce_img);
		mTitle.setLeftButton(leftImg, new OnLeftButtonClickListener() {
			@Override
			public void onClick(View button) {
				finish();
			}

		});
		mTitle.setRightButton(rightImg, new OnRightButtonClickListener() {
			@Override
			public void onClick(View button) {
				webview.loadUrl(url);
				webview.setWebViewClient(new HelloWebViewClient());
			}
		});
	
	
	}



	// Web视图
	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
	//
	// @Override
	// //设置回退
	// //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
	// webview.goBack(); //goBack()表示返回WebView的上一页面
	// return true;
	// }
	// return false;
	// }

}
