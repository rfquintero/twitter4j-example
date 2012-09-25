package com.example.twitter4j.bitmap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class UrlBitmapProvider implements IBitmapProvider {

	private static final int IO_BUFFER_SIZE = 4 * 1024;

	@Override
	public void getBitmap(String url, IBitmapCallback callback) {
		Bitmap bitmap = null;
		InputStream in = null;
		BufferedOutputStream out = null;

		try {
			in = new BufferedInputStream(new URL(url).openStream(), IO_BUFFER_SIZE);

			final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
			out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
			copy(in, out);
			out.flush();

			final byte[] data = dataStream.toByteArray();
			BitmapFactory.Options options = new BitmapFactory.Options();
			// options.inSampleSize = 1;

			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
		} catch (IOException e) {
			Log.e("Bitmap", "Could not load Bitmap from: " + url);
		} finally {
			closeStream(in);
			closeStream(out);
		}

		if (bitmap != null) {
			callback.bitmapFound(bitmap);
		}
	}

	private static void copy(InputStream inputStream, BufferedOutputStream out) throws IOException {
		byte[] buffer = new byte[IO_BUFFER_SIZE];
		int len = 0;
		while ((len = inputStream.read(buffer)) != -1) {
			out.write(buffer, 0, len);
		}
	}

	private static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				Log.e("Bitmap", "Error closing stream.", e);
			}
		}
	}
}
