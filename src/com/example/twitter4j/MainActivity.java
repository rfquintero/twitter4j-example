package com.example.twitter4j;

import java.util.ArrayList;
import java.util.List;

import twitter4j.TwitterException;
import twitter4j.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.example.twitter4j.ops.IAuthCallback;
import com.example.twitter4j.ops.ICallback;
import com.example.twitter4j.ops.IResultCallback;

public class MainActivity extends Activity implements ITwitterExceptionHandler, IProgressDialogLaucher {

	private List<User> users;
	private Twitter4jModel model;
	private Bitmap defaultIcon;
	private IBitmapProvider bitmapProvider;
	private ProgressDialog progressDialog;
	private ArrayAdapter<User> listAdapter;
	private ViewAnimator animator;
	private EditText composeField;
	private boolean composing;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Activity activity = this;

		CookieSyncManager.createInstance(this);
		model = Twitter4jModelFactory.getInstance(this);
		defaultIcon = BitmapFactory.decodeResource(getResources(), R.drawable.twitter_default);
		bitmapProvider = BitmapProvider.getInstance(this);
		users = new ArrayList<User>();

		View view = getLayoutInflater().inflate(R.layout.activity_main, null);

		animator = (ViewAnimator) view.findViewById(R.id.view_animator);
		composeField = (EditText) view.findViewById(R.id.compose_tweet_field);
		composing = false;

		listAdapter = createListAdapter();

		ListView listView = (ListView) view.findViewById(R.id.listview);

		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				User user = users.get(position);
				composeField.setText("@" + user.getScreenName() + " ");
				showComposeTweet(true);
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
				model.getFriends(activity, new IResultCallback<List<User>>() {
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
				model.sendTweet(activity, composeField.getText().toString(), new ICallback() {
					@Override
					public void perform() {
						showComposeTweet(false);
					}
				});
			}
		});
		setContentView(view);
	}

	private void showComposeTweet(boolean show) {
		long duration = 300;
		Animation stayStill = new TranslateAnimation(0, 0, 0, 0);
		stayStill.setDuration(duration);

		if (show && !composing) {
			Animation inAnimation = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0,
					Animation.RELATIVE_TO_SELF, 1.0f, Animation.ABSOLUTE, 0);
			inAnimation.setDuration(duration);
			animator.setInAnimation(inAnimation);
			animator.setOutAnimation(stayStill);
			animator.showNext();
			composing = true;
		} else if (!show && composing) {
			Animation outAnimation = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0,
					Animation.ABSOLUTE, 0, Animation.RELATIVE_TO_SELF, 1.0f);
			outAnimation.setDuration(duration);
			animator.setOutAnimation(outAnimation);
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

				final String url = user.getProfileImageURL().toExternalForm();
				bitmapProvider.getBitmap(url, new IBitmapCallback() {
					@Override
					public void bitmapFound(String id, Bitmap bitmap) {
						if (id.equals(url)) {
							holder.avatarImage.setImageBitmap(bitmap);
						}
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_logout:
			model.logOut();
			break;
		case R.id.menu_invalid_creds:
			model.invalidateCredentials();
			break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		if (composing) {
			showComposeTweet(false);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		ActivityManager.getInstance().setCurrentHandlers(this, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		ActivityManager.getInstance().removeHandlers();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		defaultIcon.recycle();
	}

	@Override
	public void handle(TwitterException e) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Twitter Error!").setMessage(e.getErrorMessage());
		builder.create().show();
	}

	@Override
	public void handleAuthorizationException(IAuthCallback authCallback) {
		model.logOut();
		model.authorize(this, authCallback);
	}

	@Override
	public void showProgressDialog(boolean show) {
		if (show) {
			if (progressDialog == null) {
				progressDialog = ProgressDialog.show(this, "", "Loading", true);
				progressDialog.show();
			}
		} else {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
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
