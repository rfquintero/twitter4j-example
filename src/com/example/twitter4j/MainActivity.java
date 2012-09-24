package com.example.twitter4j;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.example.twitter4j_example.R;

public class MainActivity extends Activity {

	private Twitter4jModel model;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		model = Twitter4jModelFactory.getInstance(this);

		View view = getLayoutInflater().inflate(R.layout.activity_main, null);
		setContentView(view);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
