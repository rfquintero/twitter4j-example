package com.example.twitter4j;

import java.util.ArrayList;
import java.util.List;

import twitter4j.User;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.example.twitter4j.bitmap.BitmapProvider;
import com.example.twitter4j.bitmap.IBitmapProvider;
import com.example.twitter4j.bitmap.IBitmapProvider.IBitmapCallback;
import com.example.twitter4j.data.IResultCallback;

public class MainActivity extends Activity {

	private List<User> users;
	private Twitter4jModel model;
	private Bitmap defaultIcon;
	private IBitmapProvider bitmapProvider;
	private ArrayAdapter<User> listAdapter;
	private ViewAnimator animator;
	private EditText composeField;
	private boolean composing;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Activity activity = this;

		defaultIcon = BitmapFactory.decodeResource(getResources(), R.drawable.twitter_default);
		bitmapProvider = BitmapProvider.getInstance(this);
		model = Twitter4jModelFactory.getInstance(this);
		users = new ArrayList<User>();

		View view = getLayoutInflater().inflate(R.layout.activity_main, null);

		animator = (ViewAnimator) view.findViewById(R.id.view_animator);
		composeField = (EditText) view.findViewById(R.id.compose_tweet_field);
		composing = false;

		listAdapter = createListAdapter();

		ListView listView = (ListView) view.findViewById(R.id.listview);

		listView.setAdapter(listAdapter);
		listView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				User user = users.get(position);
				composeField.setText("@" + user.getScreenName() + " ");
				showComposeTweet(true);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		Button tweetButton = (Button) view.findViewById(R.id.button_tweet);
		tweetButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				composeField.setText("");
				showComposeTweet(true);
			}
		});

		Button followersButton = (Button) view.findViewById(R.id.button_followers);
		followersButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				model.getFollowers(activity, new IResultCallback<List<User>>() {
					@Override
					public void perform(List<User> result) {
						setUsers(result);
					}
				});
			}
		});

		Button followingButton = (Button) view.findViewById(R.id.button_following);
		followingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				model.getFollowing(activity, new IResultCallback<List<User>>() {
					@Override
					public void perform(List<User> result) {
						setUsers(result);
					}
				});
			}
		});

		Button sendTweetButton = (Button) view.findViewById(R.id.button_compose_tweet);
		sendTweetButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showComposeTweet(false);
			}
		});
		setContentView(view);
	}

	private void showComposeTweet(boolean show) {
		long duration = 500;
		Animation stayStill = new TranslateAnimation(0, 0, 0, 0);
		stayStill.setDuration(duration);

		if (show && !composing) {
			Animation fadeIn = new AlphaAnimation(0, 1.0f);
			fadeIn.setDuration(duration);
			animator.setInAnimation(fadeIn);
			animator.setOutAnimation(stayStill);
			animator.showNext();
			composing = true;
		} else if (!show && composing) {
			Animation fadeOut = new AlphaAnimation(1.0f, 0);
			fadeOut.setDuration(duration);
			animator.setOutAnimation(fadeOut);
			animator.setInAnimation(stayStill);
			animator.showPrevious();
			composing = false;
		}
	}

	private void setUsers(List<User> users) {
		this.users.clear();
		this.users.addAll(users);
		listAdapter.notifyDataSetChanged();
	}

	private ArrayAdapter<User> createListAdapter() {
		return new ArrayAdapter<User>(this, 0, users) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = convertView;
				if (view == null) {
					view = getLayoutInflater().inflate(R.layout.user_cell, null);
					view.setTag(new ViewHolder(view));
				}

				final ViewHolder holder = (ViewHolder) view.getTag();
				User user = users.get(position);

				holder.nameLabel.setText(user.getScreenName());
				holder.avatarImage.setImageBitmap(defaultIcon);

				bitmapProvider.getBitmap(user.getProfileImageURL().toExternalForm(), new IBitmapCallback() {
					@Override
					public void bitmapFound(Bitmap bitmap) {
						holder.avatarImage.setImageBitmap(bitmap);
					}
				});
				return view;
			}
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		defaultIcon.recycle();
	}

	private class ViewHolder {
		public final TextView nameLabel;
		public final ImageView avatarImage;

		public ViewHolder(View userCell) {
			nameLabel = (TextView) userCell.findViewById(R.id.user_name);
			avatarImage = (ImageView) userCell.findViewById(R.id.user_avatar);
		}
	}
}
