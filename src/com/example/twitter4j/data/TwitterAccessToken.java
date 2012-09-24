package com.example.twitter4j.data;

import twitter4j.auth.AccessToken;

public class TwitterAccessToken extends AccessToken {
	private static final long serialVersionUID = -4240886576472670498L;
	private final String screenName;

	public TwitterAccessToken(String token, String secret, String screenName) {
		super(token, secret);
		this.screenName = screenName;
	}

	@Override
	public String getScreenName() {
		return screenName;
	}
}
