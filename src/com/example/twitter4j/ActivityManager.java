package com.example.twitter4j;

import twitter4j.TwitterException;

import com.example.twitter4j.ops.IAuthCallback;

public class ActivityManager implements IProgressDialogLaucher, ITwitterExceptionHandler {

	private static ActivityManager instance;

	public static ActivityManager getInstance() {
		if (instance == null) {
			instance = new ActivityManager();
		}
		return instance;
	}

	private ITwitterExceptionHandler handler;
	private IProgressDialogLaucher launcher;

	public void setCurrentHandlers(IProgressDialogLaucher launcher, ITwitterExceptionHandler handler) {
		this.handler = handler;
		this.launcher = launcher;
	}

	public void removeHandlers() {
		handler = null;
		launcher = null;
	}

	@Override
	public void showProgressDialog(boolean show) {
		if (launcher != null) {
			launcher.showProgressDialog(show);
		}
	}

	@Override
	public void handle(TwitterException e) {
		if (handler != null) {
			handler.handle(e);
		}
	}

	@Override
	public void handleAuthorizationException(IAuthCallback authCallback) {
		if (handler != null) {
			handler.handleAuthorizationException(authCallback);
		}
	}

}
