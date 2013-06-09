package edu.ntu.csie.agent.sweetreward;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.bool;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Debug;
import android.util.Log;
import android.widget.Toast;


public class ServerConnection {
	private static final String TAG = ServerConnection.class.getSimpleName();

	public static ServerConnection sSingleton;

	private String APIPath = "/";
	private String APIDomain = "http://disa.csie.ntu.edu.tw:1234";

	private User mUser = User.getUser();


	private static ConnectivityManager mConnectivityManager;
	private Context mAppContext;

	public static ServerConnection getServerConnection() {
		if(sSingleton == null)
			sSingleton = new ServerConnection();
		return sSingleton;
	}

	public void setContext(Context context) {
		mAppContext = context;
	}

	private Boolean isNetworkAvailable() {
		if(null == mConnectivityManager) {  
			mConnectivityManager = (ConnectivityManager)mAppContext.getSystemService(Context.CONNECTIVITY_SERVICE);  
		}
		NetworkInfo wifiInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobileInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (wifiInfo.isAvailable()) {
			return true;
		} else if (mobileInfo.isAvailable()) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean clickNotification(int taskId) {
		if (!isNetworkAvailable()) {
			Log.d(TAG, "click notification, NO network");
			return false;
		}

		String user_id = "223";
		String httpUrl = String.format("%s/notification_click?task_id=%s&user_id=%s", APIDomain, taskId, user_id);
		Log.d(TAG, httpUrl);
		ServerTask task = new ServerTask();
		task.execute(httpUrl);

		return true;
	}

	public Boolean responseNotification(int taskId, int ok, int annoy_level) {
		if (!isNetworkAvailable()) {
			Log.d(TAG, "response notification, NO network");
			return false;
		}

		String user_id = "223";
		String httpUrl = String.format("%s/notification_response?task_id=%s&user_id=%s&ok=%d&annoy_level=%d", APIDomain, taskId, user_id, ok, annoy_level);
		Log.d(TAG, httpUrl);
		ServerTask task = new ServerTask();
		task.execute(httpUrl);

		return true;
	}

	public Boolean login(String account, String password, OnTaskCompleted listener) {
		if (!isNetworkAvailable()) {
			Log.d(TAG, "login, NO network");
			return false;
		}
		String httpUrl = String.format("%s/%s/mobile/createNewUser.php?account=%s&password=%s", APIDomain, APIPath, account, password);

		ServerTask task = new ServerTask(listener);
		task.execute(httpUrl);
		return true;
	}

	public Boolean reportWindow(OnTaskCompleted listener, int windowID, int action) {
		if (!isNetworkAvailable()) {
			Log.d(TAG, "report window, NO network");
			return false;
		}

		String token = mUser.getToken();
		String httpUrl = "";
		if (action == -1) {
			httpUrl = String.format("%s/%s/userActionTrigger.php?window_id=%d&token=%s", APIDomain, APIPath, windowID, token);
		} else if (action != -1) {
			httpUrl= String.format("%s/%s/userActionTrigger.php?window_id=%d&token=%s&action=%d", APIDomain, APIPath, windowID, token, action);
		}

		ServerTask task = new ServerTask(listener);
		task.execute(httpUrl);

		return true;
	}

	public Boolean reportProblem(OnTaskCompleted listener) {
		if (!isNetworkAvailable()) {
			Log.d(TAG, "login, NO network");
			return false;
		}
		Log.d(TAG, "Send problem");
		String httpUrl = "";
		ServerTask task = new ServerTask(listener);
		//task.execute(httpUrl);
		return true;
	}


	public Boolean getProblemList(OnTaskCompleted listener) {
		if (!isNetworkAvailable()) {
			Log.d(TAG, "get problem list, NO network");
			return false;
		}

		String api = "reports/unsolved";
		String httpUrl = String.format("%s/%s", APIDomain, api);
		ServerTask task = new ServerTask(listener);
		task.execute(httpUrl);

		return true;
	}

	private class ServerTask extends AsyncTask <String, Integer, String> {
		private OnTaskCompleted listener;

		public ServerTask() {
			this.listener = null;
		}

		public ServerTask(OnTaskCompleted listener) {
			this.listener = listener;
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {
			HttpGet request = new HttpGet(params[0]);
			HttpClient httpClient = new DefaultHttpClient();

			Log.d(TAG, "Run request: " + request);

			try {
				HttpResponse response = httpClient.execute(request);
				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String str = EntityUtils.toString(response.getEntity());
					return str;
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();    
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}


		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (listener != null) {
				listener.onTaskCompleted(result);
			}
		}
	}




}
