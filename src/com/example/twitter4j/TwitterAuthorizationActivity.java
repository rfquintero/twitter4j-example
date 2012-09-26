package com.example.twitter4j;

import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.FrameLayout;

public class TwitterAuthorizationActivity extends Activity {

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
		webViewFrame = new FrameLayout(this);
		webViewFrame.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		webView = new WebView(this);
		webView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webViewFrame.addView(webView);

		webView.loadUrl(requestToken.getAuthorizationURL());

		setContentView(webViewFrame);
	}

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Uri uri = intent.getData();
		if (uri != null && uri.getScheme().equals(Twitter4jModel.CALLBACK_SCHEME)) {
			model.setOAuthTokenFromCallbackUri(uri, requestToken);
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		webViewFrame.removeAllViews();
		webView.destroy();
	}

	// @Override
	// protected void onRestart() {
	// super.onRestart();
	// finish();
	// }
}