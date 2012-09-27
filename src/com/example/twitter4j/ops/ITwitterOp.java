package com.example.twitter4j.ops;

import twitter4j.TwitterException;

public interface ITwitterOp<T> {
	public T perform() throws TwitterException;

	public void onSuccess(T result);
}
