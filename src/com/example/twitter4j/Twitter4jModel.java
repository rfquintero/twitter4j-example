package com.example.twitter4j;

import java.util.List;

import twitter4j.Twitter;
import twitter4j.User;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.example.twitter4j.data.ITwitter4jStorage;
import com.example.twitter4j.data.TwitterAccessToken;
import com.example.twitter4j.ops.IAuthCallback;
import com.example.twitter4j.ops.ICallback;
import com.example.twitter4j.ops.IResultCallback;
import com.example.twitter4j.ops.TwitterOpHandler;

public class Twitter4jModel {
	public static final String CALLBACK_SCHEME = "oauth-twitter4j";
	public static final String CALLBACK_URL = CALLBACK_SCHEME + "://callback";

	private static final String OAUTH_VERIFIER = "oauth_verifier";

	private final ITwitter4jStorage storage;
	private final Twitter twitter;
	private IAuthCallback authorizationCallback;

	public Twitter4jModel(ITwitter4jStorage storage, Twitter twitter) {
		this.storage = storage;
		this.twitter = twitter;
		this.twitter.setOAuthConsumer(TwitterKeys.CONSUMER_KEY, TwitterKeys.CONSUMER_SECRET);
	}

	public void getFollowers(final Activity activity, final IResultCallback<List<User>> callback) {
		authorize(activity, new IAuthCallback() {
			@Override
			public void perform(TwitterOpHandler twitterOp) {
				twitterOp.getFollowers(twitter, callback);
			}
		});
	}

	public void getFriends(final Activity activity, final IResultCallback<List<User>> callback) {
		authorize(activity, new IAuthCallback() {
			@Override
			public void perform(TwitterOpHandler twitterOp) {
				twitterOp.getFriends(twitter, callback);
			}
		});
	}

	public void sendTweet(final Activity activity, final String text, final ICallback callback) {
		authorize(activity, new IAuthCallback() {
			@Override
			public void perform(TwitterOpHandler twitterOp) {
				twitterOp.sendTweet(twitter, callback, text);
			}
		});
	}

	public void authorize(final Activity activity, final IAuthCallback authCallback) {
		if (isAuthorized()) {
			authCallback.perform(new TwitterOpHandler(authCallback));
		} else {
			requestTokenWithCallbackUrl(new IResultCallback<RequestToken>() {
				@Override
				public void perform(RequestToken result) {
					authorizationCallback = authCallback;
					Intent intent = new Intent(activity, Twitter4jAuthorizationActivity.class);
					intent.putExtra(Twitter4jAuthorizationActivity.REQUEST_TOKEN, result);
					activity.startActivity(intent);
				}
			});
		}
	}

	private void requestTokenWithCallbackUrl(final IResultCallback<RequestToken> callback) {
		new TwitterOpHandler().requestTokenWithCallbackUrl(twitter, callback, CALLBACK_URL);
	}

	public void setOAuthTokenFromCallbackUri(Uri uri, final RequestToken requestToken) {
		final String oauthVerifier = uri.getQueryParameter(OAUTH_VERIFIER);
		if (requestToken != null && oauthVerifier != null) {
			new TwitterOpHandler().getAccessToken(twitter, new IResultCallback<TwitterAccessToken>() {
				@Override
				public void perform(TwitterAccessToken result) {
					twitter.setOAuthAccessToken(result);
					storage.saveTwitterAccessToken(result);

					if (authorizationCallback != null) {
						authorizationCallback.perform(new TwitterOpHandler(authorizationCallback));
					}
				}
			}, requestToken, oauthVerifier);
		}
	}

	// this is just to show handling of invalid credentials
	public void invalidateCredentials() {
		storage.saveTwitterAccessToken(new TwitterAccessToken("1234-notvalid", "blahblah", "user"));
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
		twitter.setOAuthAccessToken(null);
		storage.removeTwitterAccessToken();
	}
}
