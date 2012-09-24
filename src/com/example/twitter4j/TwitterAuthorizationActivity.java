package com.example.twitter4j;

import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class TwitterAuthorizationActivity extends Activity {

	private static final String CALLBACK_SCHEME = "oauth-twitter4j";
	private static final String CALLBACK_URL = CALLBACK_SCHEME + "://callback";
	private RequestToken requestToken;
	private Twitter4jModel model;

	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		model = Twitter4jModelFactory.getInstance(this);
		model.requestTokenWithCallbackUrl(this, CALLBACK_URL);
	}

	public void launchAuthorizationRequest(RequestToken requestToken) {
		this.requestToken = requestToken;
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthorizationURL()))
				.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY
						| Intent.FLAG_FROM_BACKGROUND);
		this.startActivity(intent);
	}

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Uri uri = intent.getData();
		if (uri != null && uri.getScheme().equals(CALLBACK_SCHEME)) {
			model.setOAuthTokenFromCallbackUri(uri, requestToken);
			finish();
		}
	}

	// @Override
	// protected void onRestart() {
	// super.onRestart();
	// finish();
	// }
}