package com.uw.huskyglass.android;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.uw.huskyglass.android.R;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;

public class MainActivity extends Activity {
	private static final String TAG = "Glass";
	private static final String PARAM_AUTH_TOKEN = "com.example.mirror.android.AUTH_TOKEN";

	private static final int REQUEST_ACCOUNT_PICKER = 1;
	private static final int REQUEST_AUTHORIZATION = 2;

	private static final String GLASS_TIMELINE_SCOPE = "https://www.googleapis.com/auth/glass.timeline";
	private static final String GLASS_LOCATION_SCOPE = "https://www.googleapis.com/auth/glass.location";
	private static final String SCOPE = String.format("oauth2: %s %s",
			GLASS_TIMELINE_SCOPE, GLASS_LOCATION_SCOPE);

	private static ExecutorService sThreadPool = Executors
			.newSingleThreadExecutor();

	private final Handler mHandler = new Handler();

	private String mAuthToken;
	private Button mStartAuthButton;
	private ImageView playCard00;
	private ImageView playCard01;
	private ImageView playCard02;
	private ImageView playCard03;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Allow networking on the main thread (really bad)
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Define our layout
		setContentView(R.layout.activity_main);

		// Get our views
		mStartAuthButton = (Button) findViewById(R.id.oauth_button);
		playCard00 = (ImageView) findViewById(R.id.playCard1);
		playCard01 = (ImageView) findViewById(R.id.playCard2);
		playCard02 = (ImageView) findViewById(R.id.playCard3);
		playCard03 = (ImageView) findViewById(R.id.playCard4);

		// Restore any saved instance state
		if (savedInstanceState != null) {
			onTokenResult(savedInstanceState.getString(PARAM_AUTH_TOKEN));
		} else {
			mStartAuthButton.setEnabled(true);
			// mExpireTokenButton.setEnabled(false);
		}

		mStartAuthButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// Present the user with an account picker dialog with a list
				// of their Google accounts
				Intent intent = AccountPicker.newChooseAccountIntent(null,
						null,
						new String[] { GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE },
						false, null, null, null, null);
				startActivityForResult(intent, REQUEST_ACCOUNT_PICKER);
			}
		});

		playCard00.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String playName = "Glass Pass";
				String playDesc = "The Glass Pass is a short pass to a downfield receiver.";
				String playImg = "https://huskyglass.appspot.com/static/images/playcard_01.png";
				createNewTimelineItem(playName, playDesc, playImg);
			}
		});
		
		playCard01.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String playName = "Glass QB Sneak";
				String playDesc = "The Glass QB Sneak provides the QB an option to pass or run.";
				String playImg = "https://huskyglass.appspot.com/static/images/playcard_02.png";
				createNewTimelineItem(playName, playDesc, playImg);
			}
		});
		
		playCard02.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String playName = "Glass and Out";
				String playDesc = "The Glass and out is a play with options for short passes or a quick run.";
				String playImg = "https://huskyglass.appspot.com/static/images/playcard_03.png";
				createNewTimelineItem(playName, playDesc, playImg);
			}
		});
		
		playCard03.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String playName = "Glass Shuffle Pass";
				String playDesc = "The Glass Shuffle Pass provides the QB the option for a quick handoff.";
				String playImg = "https://huskyglass.appspot.com/static/images/playcard_01.png";
				createNewTimelineItem(playName, playDesc, playImg);
			}
		});

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(PARAM_AUTH_TOKEN, mAuthToken);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ACCOUNT_PICKER:
			if (RESULT_OK == resultCode) {
				String account = data
						.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				String type = data
						.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);

				// TODO: Cache the chosen account
				Log.i(TAG, String.format("User selected account %s of type %s",
						account, type));
				fetchTokenForAccount(account);
			}
			break;
		case REQUEST_AUTHORIZATION:
			if (RESULT_OK == resultCode) {
				String token = data
						.getStringExtra(AccountManager.KEY_AUTHTOKEN);

				Log.i(TAG, String.format(
						"Authorization request returned token %s", token));
				onTokenResult(token);
			}
			break;
		}
	}

	// private void createNewTimelineItem() {
	void createNewTimelineItem(String playName, String playDesc, String playImg) {
		if (!TextUtils.isEmpty(mAuthToken)) {
			try {

				String testCard = "<article class=\"photo\" style=\"left:0px;visibility:visible\">"
						+ "<img src=\""
						+ playImg
						+ "\" width=\"100%\" height=\"100%\">"
						+ "<section>"
						+ "<p class=\"text-normal\" style=\"text-align:right\"></p>"
						+ "</section></article>";

				JSONObject notification = new JSONObject();
				notification.put("level", "DEFAULT"); // Play a chime

				JSONArray menuItems = new JSONArray();

				JSONArray menuValues = new JSONArray();
				JSONObject cardIcon = new JSONObject();
				cardIcon.put("iconUrl",
						"https://huskyglass.appspot.com/static/images/football-icon.png");
				menuValues.put(cardIcon);

				JSONObject menuChild = new JSONObject();
				menuChild = new JSONObject();
				menuChild.put("action", "READ_ALOUD");
				menuChild.put("values", menuValues);

				menuItems.put(menuChild);

				JSONObject parentCard = new JSONObject();
				// Nested child JSON elements
				parentCard.put("notification", notification);

				// Top-level child JSON elements
				parentCard.put("title", playName);
				parentCard.put("text", playDesc);
				parentCard.put("html", testCard);

				parentCard.put("menuItems", menuItems);

				System.out.println(parentCard);

				MirrorApiClient client = MirrorApiClient.getInstance(this);
				client.createTimelineItem(mAuthToken, parentCard,
						new MirrorApiClient.Callback() {
							@Override
							public void onSuccess(HttpResponse response) {
								try {
									Log.v(TAG,
											"onSuccess: "
													+ EntityUtils.toString(response
															.getEntity()));
								} catch (IOException e1) {
									// Pass
								}
								Toast.makeText(MainActivity.this,
										"Play pushed to glass",
										Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onFailure(HttpResponse response,
									Throwable e) {
								try {
									Log.v(TAG,
											"onFailure: "
													+ EntityUtils.toString(response
															.getEntity()));
								} catch (IOException e1) {
									// Pass
								}
								Toast.makeText(MainActivity.this,
										"Failed to create new timeline item",
										Toast.LENGTH_SHORT).show();
							}
						});
			} catch (JSONException e) {
				Toast.makeText(this, "Sorry, can't serialize that to JSON",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "Sorry, can't create an empty timeline item",
					Toast.LENGTH_SHORT).show();
			// }
			// } else {
			// Toast.makeText(this,
			// "Sorry, can't create a new timeline card without a token",
			// Toast.LENGTH_LONG).show();
		}
	}

	private void onTokenResult(String token) {
		Log.d(TAG, "onTokenResult: " + token);
		if (!TextUtils.isEmpty(token)) {
			mAuthToken = token;
			// mExpireTokenButton.setEnabled(true);
			mStartAuthButton.setEnabled(false);
			Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT)
					.show();
		} else {
			// mExpireTokenButton.setEnabled(false);
			mStartAuthButton.setEnabled(true);
			Toast.makeText(this, "Unable to login", Toast.LENGTH_SHORT).show();
		}
	}

	private void fetchTokenForAccount(final String account) {
		// We fetch the token on a background thread otherwise Google Play
		// Services will throw an IllegalStateException
		sThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					// If this returns immediately the OAuth framework thinks
					// the token should be usable
					final String token = GoogleAuthUtil.getToken(
							MainActivity.this, account, SCOPE);

					if (token != null) {
						// Pass the token back to the UI thread
						Log.i(TAG, String.format("getToken returned token %s",
								token));
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								onTokenResult(token);
							}
						});
					}
				} catch (final UserRecoverableAuthException e) {
					// This means that the app hasn't been authorized by the
					// user for access
					// to the scope, so we're going to have to fire off the
					// (provided) Intent
					// to arrange for that. But we only want to do this once.
					// Multiple
					// attempts probably mean the user said no.
					Log.i(TAG, "Handling a UserRecoverableAuthException");

					mHandler.post(new Runnable() {
						@Override
						public void run() {
							startActivityForResult(e.getIntent(),
									REQUEST_AUTHORIZATION);
						}
					});
				} catch (IOException e) {
					// Something is stressed out; the auth servers are by
					// definition
					// high-traffic and you can't count on 100% success. But it
					// would be
					// bad to retry instantly, so back off
					Log.e(TAG, "Failed to fetch auth token!", e);

					mHandler.post(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(MainActivity.this,
									"Failed to fetch token, try again later",
									Toast.LENGTH_LONG).show();
						}
					});
				} catch (GoogleAuthException e) {
					// Can't recover from this!
					Log.e(TAG, "Failed to fetch auth token!", e);

					mHandler.post(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(MainActivity.this,
									"Failed to fetch token, can't recover",
									Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		});
	}
}