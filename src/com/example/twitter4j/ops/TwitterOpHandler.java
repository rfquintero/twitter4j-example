package com.example.twitter4j.ops;

import java.util.List;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.os.AsyncTask;
import android.util.Log;

import com.example.twitter4j.ActivityManager;
import com.example.twitter4j.data.TwitterAccessToken;

public class TwitterOpHandler {
	private TwitterException exception;
	private final IAuthCallback authCallback;

	public TwitterOpHandler(IAuthCallback authCallback) {
		this.authCallback = authCallback;
	}

	public TwitterOpHandler() {
		this.authCallback = null;
	}

	public void getFollowers(final Twitter twitter, final IResultCallback<List<User>> callback) {
		perform(new ITwitterOp<List<User>>() {
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

	public void getFriends(final Twitter twitter, final IResultCallback<List<User>> callback) {
		perform(new ITwitterOp<List<User>>() {
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

	public void sendTweet(final Twitter twitter, final ICallback callback, final String text) {
		perform(new ITwitterOp<Void>() {

			@Override
			public Void perform() throws TwitterException {
				twitter.updateStatus(text);
				return null;
			}

			@Override
			public void onSuccess(Void result) {
				callback.perform();
			}
		});
	}

	public void requestTokenWithCallbackUrl(final Twitter twitter, final IResultCallback<RequestToken> callback,
			final String callbackUrl) {
		perform(new ITwitterOp<RequestToken>() {
			@Override
			public RequestToken perform() throws TwitterException {
				twitter.setOAuthAccessToken(null);
				return twitter.getOAuthRequestToken(callbackUrl);
			}

			@Override
			public void onSuccess(RequestToken result) {
				callback.perform(result);
			}
		});
	}

	public void getAccessToken(final Twitter twitter, final IResultCallback<TwitterAccessToken> callback,
			final RequestToken requestToken, final String oauthVerifier) {
		perform(new ITwitterOp<TwitterAccessToken>() {
			@Override
			public TwitterAccessToken perform() throws TwitterException {
				AccessToken token = twitter.getOAuthAccessToken(requestToken, oauthVerifier);
				return new TwitterAccessToken(token.getToken(), token.getTokenSecret(), token.getScreenName());
			}

			@Override
			public void onSuccess(TwitterAccessToken result) {
				callback.perform(result);
			}

		});
	}

	public <T> void perform(final ITwitterOp<T> operation) {
		exception = null;
		AsyncTask<Void, Void, T> task = new AsyncTask<Void, Void, T>() {
			@Override
			protected T doInBackground(Void... params) {
				try {
					return operation.perform();
				} catch (TwitterException e) {
					exception = e;
					return null;
				}
			}

			@Override
			protected void onPostExecute(T result) {
				ActivityManager.getInstance().showProgressDialog(false);
				if (exception == null) {
					operation.onSuccess(result);
				} else {
					Log.e("Error", "Error during twitter operation.", exception);
					if ((exception.getStatusCode() == TwitterException.UNAUTHORIZED || exception.getStatusCode() == -1)
							&& authCallback != null) {
						ActivityManager.getInstance().handleAuthorizationException(authCallback);
					} else {
						ActivityManager.getInstance().handle(exception);
					}
				}
			}
		};
		ActivityManager.getInstance().showProgressDialog(true);
		task.execute();
	}
}
