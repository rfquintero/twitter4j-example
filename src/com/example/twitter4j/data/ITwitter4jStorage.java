package com.example.twitter4j.data;

public interface ITwitter4jStorage {

	TwitterAccessToken getTwitterAccessToken();

	void removeTwitterAccessToken();

	void saveTwitterAccessToken(TwitterAccessToken accessToken);

}
