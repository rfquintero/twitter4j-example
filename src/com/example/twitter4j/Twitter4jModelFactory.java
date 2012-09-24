package com.example.twitter4j;

import twitter4j.TwitterFactory;
import android.content.Context;

import com.example.twitter4j.data.Twitter4jDatabase;

public class Twitter4jModelFactory {

	private static Twitter4jModel instance;

	public synchronized static Twitter4jModel getInstance(Context context) {
		if (instance == null) {
			instance = new Twitter4jModel(Twitter4jDatabase.getInstance(context), new TwitterFactory().getInstance());
		}
		return instance;
	}
}
