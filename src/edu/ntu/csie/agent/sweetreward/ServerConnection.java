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

import android.content.Context;
import android.os.AsyncTask;
import android.os.Debug;
import android.util.Log;
import android.widget.Toast;


public class ServerConnection {
	private static final String TAG = ServerConnection.class.getSimpleName();

	private String APIPath = "/";
	private String APIDomain = "http://disa.csie.ntu.edu.tw:1234";
	
	private User mUser = User.getUser();
	
	public static ServerConnection sSingleton;
	
	
	public static ServerConnection getServerConnection() {
		if(sSingleton == null)
			sSingleton = new ServerConnection();
	    return sSingleton;
	}
	
	public void login(String account, String password, OnTaskCompleted listener) {
		
    	String httpUrl = String.format("%s/%s/mobile/createNewUser.php?account=%s&password=%s", APIDomain, APIPath, account, password);
    	
    	ServerTask task = new ServerTask(listener);
    	task.execute(httpUrl);
    }
	
	public void reportWindow(OnTaskCompleted listener, int windowID, int action) {
	    String token = mUser.getToken();
		String httpUrl = "";
		if (action == -1) {
			httpUrl = String.format("%s/%s/userActionTrigger.php?window_id=%d&token=%s", APIDomain, APIPath, windowID, token);
		} else if (action != -1) {
			httpUrl= String.format("%s/%s/userActionTrigger.php?window_id=%d&token=%s&action=%d", APIDomain, APIPath, windowID, token, action);
		}
		
		ServerTask task = new ServerTask(listener);
		task.execute(httpUrl);
	}
	
	public void reportProblem(OnTaskCompleted listener) {
		Log.d(TAG, "Send problem");
		String httpUrl = "";
		ServerTask task = new ServerTask(listener);
		//task.execute(httpUrl);
	}
	
	public void getProblemList(OnTaskCompleted listener) {
		String api = "reports/unsolved";
		String httpUrl = String.format("%s/%s", APIDomain, api);
		ServerTask task = new ServerTask(listener);
		task.execute(httpUrl);
	}
    
    private class ServerTask extends AsyncTask <String, Integer, String> {
        private OnTaskCompleted listener;
        
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
	        listener.onTaskCompleted(result);
	    }
    }
    


	
}
