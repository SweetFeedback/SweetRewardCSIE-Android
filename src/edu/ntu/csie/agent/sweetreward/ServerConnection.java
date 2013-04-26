package edu.ntu.csie.agent.sweetreward;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


public class ServerConnection {

	/**
	 * @param args
	 */
	private String APIPath = "~blt/sweetreward/php";
	private String APIDomain = "http://disa.csie.ntu.edu.tw";
	
	
	private Context context_;
	
	public static ServerConnection serverConnection;

	public static ServerConnection getServerConnection() {
		if(serverConnection == null)
			serverConnection = new ServerConnection();
		return serverConnection;
	}
	
	private ServerConnection() {
		
	}
	
	public void login(String account, String password, SettingActivity context) {
		context_ = context;
    	String httpUrl = String.format("%s/%s/mobile/createNewUser.php?account=%s&password=%s", APIDomain, APIPath, account, password);
    	
    	LoginTask task = new LoginTask((SettingActivity)context_);
    	task.execute(httpUrl);
    }
	

    private class LoginTask extends AsyncTask <String, Integer, String> {
    	private OnTaskCompleted listener;
    	
    	public LoginTask(OnTaskCompleted listener) {
    		this.listener = listener;
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		// set loading icon
    	}
    	
		@Override
		protected String doInBackground(String... params) {
            HttpGet request = new HttpGet(params[0]);
            HttpClient httpClient = new DefaultHttpClient();
            
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
	    	// remove loading icon
	    	
	    	// parse result
	    	if(result == null) {
	    		Toast.makeText(context_, "Login failed", Toast.LENGTH_LONG).show();
	    		return;
	    	}
	    	//Log.e(TAG, result);
        	
        	JSONObject json = null;
        	String token = "";
	    	
			try {
				json = new JSONObject(result);
				token = json.get("token").toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			listener.onTaskCompleted(token);
        	Toast.makeText(context_, "Login successfully", Toast.LENGTH_SHORT).show();
        	
	    	super.onPostExecute(result);
	    }
    }
    
    /*
    private class PostWindow extends AsyncTask <String, Integer, String> {
    	@Override
		protected String doInBackground(String... params) {
    		Log.e(TAG, "url: " + params[0]);
            HttpGet request = new HttpGet(params[0]);
            HttpClient httpClient = new DefaultHttpClient();
            
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
			
			Log.e(TAG, "result: " + result + " feedback: " + getFeedback);
					
			if(status == 0 && getFeedback == 0) {
				Toast.makeText(context_, "Thank you!", Toast.LENGTH_SHORT).show();
			} else if(status == 0 && getFeedback == 1) {
				Toast.makeText(context_, "Thank you! Go get some candies!", Toast.LENGTH_SHORT).show();
				mMediaPlayer.start();
			} else {
				Toast.makeText(context_, "Error!", Toast.LENGTH_SHORT).show();
			}
			
	    	super.onPostExecute(result);
	    }

    }
    */


	
}
