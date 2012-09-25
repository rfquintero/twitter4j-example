package com.example.twitter4j;

import java.util.HashMap;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.User;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.net.Uri;

import com.example.twitter4j.data.ICallback;
import com.example.twitter4j.data.IResultCallback;
import com.example.twitter4j.data.ITwitter4jStorage;
import com.example.twitter4j.data.TwitterAccessToken;

public class Twitter4jModel {
	private static final String OAUTH_VERIFIER = "oauth_verifier";

	private final ITwitter4jStorage storage;
	private final Twitter twitter;
	private final HashMap<Activity, ICallback> authorizationCallbacks;

	public Twitter4jModel(ITwitter4jStorage storage, Twitter twitter) {
		this.storage = storage;
		this.twitter = twitter;
		this.authorizationCallbacks = new HashMap<Activity, ICallback>();
	}

	public void getFollowers(final Activity activity, final IResultCallback<List<User>> callback) {
		authorize(activity, new ICallback() {
			@Override
			public void perform() {

			}
		});
	}

	public void getFollowing(final Activity activity, final IResultCallback<List<User>> callback) {
		authorize(activity, new ICallback() {
			@Override
			public void perform() {

			}
		});
	}

	public void sendTweet(final Activity activity, final String text, final ICallback successCallback) {
		authorize(activity, new ICallback() {
			@Override
			public void perform() {

			}
		});
	}

	public void authorize(Activity activity, ICallback callback) {
		if (isAuthorized()) {
			callback.perform();
		} else {
			authorizationCallbacks.put(activity, callback);
		}
	}

	public void requestTokenWithCallbackUrl(final TwitterAuthorizationActivity activity, String callbackUrl) {

	}

	public void setOAuthTokenFromCallbackUri(Uri uri, final RequestToken requestToken) {
		final String oauthVerifier = uri.getQueryParameter(OAUTH_VERIFIER);
		if (requestToken != null && oauthVerifier != null) {
			TwitterAccessToken twitterToken = null;
			twitter.setOAuthAccessToken(twitterToken);
			storage.saveTwitterAccessToken(twitterToken);

			ICallback callback = authorizationCallbacks.get(null);
			if (callback != null) {
				callback.perform();
				callback = null;
			}
		}
	}

	public void invalidateCredentials() {
		twitter.setOAuthAccessToken(null);
		storage.removeTwitterAccessToken();
	}

	private boolean isAuthorized() {
		TwitterAccessToken twitterToken = storage.getTwitterAccessToken();
		twitter.setOAuthAccessToken(twitterToken);
		if (twitterToken != null) {
			return true;
		} else {
			return false;
		}
	}

	public void logOut() {
		invalidateCredentials();
	}
}
