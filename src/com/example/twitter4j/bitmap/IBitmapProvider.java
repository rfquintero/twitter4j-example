package com.example.twitter4j.bitmap;

import android.graphics.Bitmap;

public interface IBitmapProvider {

	public void getBitmap(String id, IBitmapCallback callback);

	public interface IBitmapCallback {
		public void bitmapFound(String id, Bitmap bitmap);
	}
}
