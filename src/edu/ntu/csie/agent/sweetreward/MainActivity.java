package edu.ntu.csie.agent.sweetreward;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnTaskCompleted {
	private static final String TAG = MainActivity.class.getSimpleName();

	private WebView webView;

	private User mUser;
	private ServerConnection serverConnection;

	private MediaPlayer mMediaPlayer;

	public static final String PROPERTY_REG_ID = "registration_id";

	// Louis' key: "411973252223"
	String GCM_SENDER_ID = "104069007708";

	private GoogleCloudMessaging gcm;
	SharedPreferences prefs;
	String regid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mUser = User.getUser(getApplicationContext());
		serverConnection = ServerConnection.getServerConnection();
		serverConnection.setContext(getApplicationContext());

		webView = (WebView) findViewById(R.id.webView);
		//webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl("http://disa.csie.ntu.edu.tw/~blt/sweetreward/");
		webView.getSettings().setJavaScriptEnabled(true);

		mMediaPlayer = MediaPlayer.create(this, R.raw.reward);

		prefs = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		regid = prefs.getString(PROPERTY_REG_ID, null);

		// If there is no registration ID, the app isn't registered.
		// Call registerBackground() to register it.
		gcm = GoogleCloudMessaging.getInstance(this);

		if (regid == null) {
			Log.d(TAG, "start register GCM");
			registerBackground();
		}


	}


	private void registerBackground() {
		new AsyncTask<Object, Object, Object>() {

			@Override
			protected Object doInBackground(Object... params) {
				String msg = "";
				try {
					regid = gcm.register(GCM_SENDER_ID);
					msg = "Device registered, registration id=" + regid;

					SharedPreferences.Editor editor = prefs.edit();
					editor.putString(PROPERTY_REG_ID, regid);
					editor.commit();
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				return msg;
			}

			@Override
			protected void onPostExecute(Object msg) {
				Log.d(TAG, "register done: " + msg);
			}
		}.execute(null, null, null);
	}



	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch(item.getItemId()) {
		case R.id.menu_scan:
			IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
			integrator.initiateScan();
			break;

		case R.id.menu_settings:
			intent = new Intent(this, SettingActivity.class);
			this.startActivity(intent);
			break;

		case R.id.menu_problem_list:
			intent = new Intent(this, ProblemListActivity.class);
			this.startActivity(intent);
			break;

		case R.id.menu_report_problem:
			intent = new Intent(this, ReportProblemActivity.class);
			this.startActivity(intent);
			break;

		case R.id.menu_send_feedback:
			Intent Email = new Intent(Intent.ACTION_SEND);
			Email.setType("text/email");
			Email.putExtra(Intent.EXTRA_EMAIL, new String[] { "admin@hotmail.com" });
			Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
			Email.putExtra(Intent.EXTRA_TEXT, "Dear SweetFeedback,\n\n");
			startActivity(Intent.createChooser(Email, "Send Feedback:"));
			break;

			// it seems this will work if the app is on the market
			/*
	    		intent = new Intent(Intent.ACTION_APP_ERROR);
	    	    startActivity(intent);
	    	    break;
			 */

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

				// Handle successful scan
				String[] contentsArray = contents.split(",");

				// call api
				int windowID = -1;
				int action = -1;
				//String httpUrl = "";
				if(contentsArray.length == 1) {
					windowID = Integer.valueOf(contentsArray[0]);
				} else if(contentsArray.length == 2) {
					windowID = Integer.valueOf(contentsArray[0]);
					action = Integer.valueOf(contentsArray[1]);
				} else {
					Log.e(TAG, "Error: unknown QRCode type");
				}

				serverConnection.reportWindow(this, windowID, action);


			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel

			}
		}
	}

	@Override
	public void onTaskCompleted(String result) {
		// parse result
		JSONObject json = null;
		int status = 1;
		int getFeedback = 0;
		try {
			json = new JSONObject(result);
			status = json.getInt("status");
			getFeedback = json.getInt("get_feedback");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		//Log.e(TAG, "result: " + result + " feedback: " + getFeedback);

		if(status == 0 && getFeedback == 0) {
			Toast.makeText(this, "Thank you!", Toast.LENGTH_SHORT).show();
		} else if(status == 0 && getFeedback == 1) {
			Toast.makeText(this, "Thank you! Go get some candies!", Toast.LENGTH_SHORT).show();
			mMediaPlayer.start();
		} else {
			Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
		}

	}



}
