package com.example.twitter4j;

import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.FrameLayout;

public class Twitter4jAuthorizationActivity extends Activity {

	protected static final String REQUEST_TOKEN = "request_token";
	private RequestToken requestToken;
	private Twitter4jModel model;
	private WebView webView;
	private FrameLayout webViewFrame;

	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		model = Twitter4jModelFactory.getInstance(this);
		requestToken = (RequestToken) getIntent().getSerializableExtra(REQUEST_TOKEN);

		CookieSyncManager.getInstance().sync();
		CookieManager.getInstance().removeAllCookie();
		webViewFrame = new FrameLayout(this);
		webViewFrame.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		webView = new WebView(this);
		webView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webViewFrame.addView(webView);

		setContentView(webViewFrame);
	}

	@Override
	protected void onStart() {
		super.onStart();
		webView.loadUrl(requestToken.getAuthorizationURL());
	}

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Uri uri = intent.getData();
		if (isCallbackUri(uri)) {
			model.setOAuthTokenFromCallbackUri(uri, requestToken);
			finish();
		}
	}

	private boolean isCallbackUri(Uri uri) {
		return (uri != null && uri.getScheme().equals(Twitter4jModel.CALLBACK_SCHEME));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		webViewFrame.removeAllViews();
		webView.destroy();
	}
}