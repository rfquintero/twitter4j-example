package com.example.twitter4j.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

class LruCacheBitmapProvider implements IBitmapProvider {
	private final LruCache<String, Bitmap> cache;
	private final IBitmapProvider delegate;

	public LruCacheBitmapProvider(Context context, IBitmapProvider delegate) {
		cache = new LruCache<String, Bitmap>(4 * 1024 * 1024) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight();
			}
		};

		this.delegate = delegate;
	}

	@Override
	public void getBitmap(final String id, final IBitmapCallback callback) {
		Bitmap bitmap = cache.get(id);
		if (bitmap == null) {
			delegate.getBitmap(id, new IBitmapCallback() {
				@Override
				public void bitmapFound(Bitmap bitmap) {
					cache.put(id, bitmap);
					callback.bitmapFound(bitmap);
				}
			});
		} else {
			callback.bitmapFound(bitmap);
		}
	}
}
