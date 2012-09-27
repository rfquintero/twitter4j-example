package com.example.twitter4j;

import twitter4j.TwitterException;

import com.example.twitter4j.ops.IAuthCallback;

public interface ITwitterExceptionHandler {
	public void handle(TwitterException e);

	public void handleAuthorizationException(IAuthCallback authCallback);
}
