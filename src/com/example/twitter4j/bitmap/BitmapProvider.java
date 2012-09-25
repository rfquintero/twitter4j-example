package com.example.twitter4j.bitmap;

import android.content.Context;

public class BitmapProvider {
	private static IBitmapProvider instance;

	public synchronized static IBitmapProvider getInstance(Context context) {
		if (instance == null) {
			UrlBitmapProvider urlProvider = new UrlBitmapProvider();
			instance = new LruCacheBitmapProvider(context.getApplicationContext(), urlProvider);
		}
		return instance;
	}
}
