package com.example.twitter4j;

import java.util.HashMap;
import java.util.List;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.example.twitter4j.data.ICallback;
import com.example.twitter4j.data.IResultCallback;
import com.example.twitter4j.data.ITwitter4jStorage;
import com.example.twitter4j.data.TwitterAccessToken;

public class Twitter4jModel {
	public static final String CALLBACK_SCHEME = "oauth-twitter4j";
	public static final String CALLBACK_URL = CALLBACK_SCHEME + "://callback";

	private static final String OAUTH_VERIFIER = "oauth_verifier";
	private static final String CONSUMER_KEY = "";
	private static final String CONSUMER_SECRET = "";

	private final ITwitter4jStorage storage;
	private final Twitter twitter;
	private final HashMap<RequestToken, ICallback> authorizationCallbacks;

	public Twitter4jModel(ITwitter4jStorage storage, Twitter twitter) {
		this.storage = storage;
		this.twitter = twitter;
		this.authorizationCallbacks = new HashMap<RequestToken, ICallback>();
		this.twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
	}

	public void getFollowers(final Activity activity, final IResultCallback<List<User>> callback) {
		authorize(activity, new ICallback() {
			@Override
			public void perform() {
				new TwitterOpManager<List<User>>(activity).perform(new ITwitterOp<List<User>>() {
					@Override
					public List<User> perform() throws TwitterException {
						IDs ids = twitter.getFollowersIDs(-1);
						return twitter.lookupUsers(ids.getIDs());
					}

					@Override
					public void onSuccess(List<User> result) {
						callback.perform(result);
					}
				});
			}
		});
	}

	public void getFollowing(final Activity activity, final IResultCallback<List<User>> callback) {
		authorize(activity, new ICallback() {
			@Override
			public void perform() {
				new TwitterOpManager<List<User>>(activity).perform(new ITwitterOp<List<User>>() {
					@Override
					public List<User> perform() throws TwitterException {
						IDs ids = twitter.getFriendsIDs(-1);
						return twitter.lookupUsers(ids.getIDs());
					}

					@Override
					public void onSuccess(List<User> result) {
						callback.perform(result);
					}
				});
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

	public void authorize(final Activity activity, final ICallback callback) {
		if (isAuthorized()) {
			callback.perform();
		} else {
			requestTokenWithCallbackUrl(activity, new IResultCallback<RequestToken>() {
				@Override
				public void perform(RequestToken result) {
					authorizationCallbacks.put(result, callback);
					Intent intent = new Intent(activity, TwitterAuthorizationActivity.class);
					intent.putExtra(TwitterAuthorizationActivity.REQUEST_TOKEN, result);
					activity.startActivity(intent);
				}
			});
		}
	}

	private void requestTokenWithCallbackUrl(final Activity activity, final IResultCallback<RequestToken> callback) {
		new TwitterOpManager<RequestToken>(activity).perform(new ITwitterOp<RequestToken>() {
			@Override
			public RequestToken perform() throws TwitterException {
				twitter.setOAuthAccessToken(null);
				return twitter.getOAuthRequestToken(CALLBACK_URL);
			}

			@Override
			public void onSuccess(RequestToken result) {
				callback.perform(result);
			}
		});
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
