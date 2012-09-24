package com.example.twitter4j.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Twitter4jDatabase extends SQLiteOpenHelper implements ITwitter4jStorage {
	static final int SCHEMA_VERSION = 1;
	static final String DATABASE_NAME = "twitter4j_example";
	private static Twitter4jDatabase instance;

	public static ITwitter4jStorage getInstance(Context context) {
		if (instance == null) {
			instance = new Twitter4jDatabase(context.getApplicationContext());
		}
		return instance;
	}

	public Twitter4jDatabase(Context context) {
		this(context, SCHEMA_VERSION);
	}

	Twitter4jDatabase(Context context, int version) {
		super(context, DATABASE_NAME, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE twitter(token TEXT PRIMARY KEY, token_secret TEXT, screen_name TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS twitter");
		onCreate(db);
	}

	private SQLiteDatabase db() {
		return getWritableDatabase();
	}

	@Override
	public void saveTwitterAccessToken(TwitterAccessToken accessToken) {
		removeTwitterAccessToken();

		String table = "twitter";
		ContentValues values = new ContentValues();
		values.put("token", accessToken.getToken());
		values.put("token_secret", accessToken.getTokenSecret());
		values.put("screen_name", accessToken.getScreenName());
		db().insert(table, null, values);
	}

	@Override
	public TwitterAccessToken getTwitterAccessToken() {
		String sql = "SELECT token, token_secret, screen_name FROM twitter";
		Cursor cursor = db().rawQuery(sql, null);

		TwitterAccessToken accessToken = null;
		if (cursor.moveToNext()) {
			String token = cursor.getString(0);
			String tokenSecret = cursor.getString(1);
			String screenName = cursor.getString(2);
			accessToken = new TwitterAccessToken(token, tokenSecret, screenName);
		}

		cursor.close();
		return accessToken;
	}

	@Override
	public void removeTwitterAccessToken() {
		db().execSQL("DELETE FROM twitter");
	}
}
