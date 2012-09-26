package com.example.twitter4j;

import twitter4j.TwitterException;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

public class TwitterOpManager<T> {

	private TwitterException exception;
	private final Context context;

	public TwitterOpManager(Context context) {
		this.context = context;
	}

	public void perform(final ITwitterOp<T> operation) {
		exception = null;
		AsyncTask<Void, Void, T> task = new AsyncTask<Void, Void, T>() {
			@Override
			protected T doInBackground(Void... params) {
				try {
					return operation.perform();
				} catch (TwitterException e) {
					exception = e;
					return null;
				}
			}

			@Override
			protected void onPostExecute(T result) {
				if (exception == null) {
					operation.onSuccess(result);
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setTitle("Error!").setMessage(exception.getMessage());
					builder.create().show();
				}
			}
		};

		task.execute();
	}
}
