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
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Debug;
import android.util.Log;
import android.widget.Toast;


public class ServerConnection {
	private static final String TAG = ServerConnection.class.getSimpleName();

	/**
	 * @param args
	 */
	private String APIPath = "~blt/sweetreward/php";
	private String APIDomain = "http://disa.csie.ntu.edu.tw";
	
	private User mUser = User.getUser();
	
	private Context mContext;
	
	private ArrayList<Map<String,String>> mProblems;
	
	public static ServerConnection sSingleton;
	
	//private SharedPreferences mSettings;
	//private String mToken;
	
	public static ServerConnection getServerConnection() {
	    return sSingleton;
	}

	
	public static ServerConnection getServerConnection(Context context) {
		if(sSingleton == null)
			sSingleton = new ServerConnection();
		
		sSingleton.setContext(context);
		return sSingleton;
	}
	
	private ServerConnection() {
	    
	}
	
	private void setContext(Context context) {
	    this.mContext = context;
	}
	
	public void login(String account, String password, LoginFragment context) {
		
    	String httpUrl = String.format("%s/%s/mobile/createNewUser.php?account=%s&password=%s", APIDomain, APIPath, account, password);
    	
    	LoginTask task = new LoginTask(context);
    	task.execute(httpUrl);
    }
	
	public void reportWindow(int windowID, int action) {
	    String token = mUser.getToken();
		String httpUrl = "";
		if (action == -1) {
			httpUrl = String.format("%s/%s/userActionTrigger.php?window_id=%d&token=%s", APIDomain, APIPath, windowID, token);
		} else if (action != -1) {
			httpUrl= String.format("%s/%s/userActionTrigger.php?window_id=%d&token=%s&action=%d", APIDomain, APIPath, windowID, token, action);
		}
		
		//PostWindowTask task = new PostWindowTask((MainActivity)mContext);
		//task.execute(httpUrl);
	}
	
	public void getProblemListFromServer(OnTaskCompleted listener) {
		String httpUrl = String.format("%s/%s/getUnsolveProblemRoomRank.php", APIDomain, APIPath);
		GetProblemTask task = new GetProblemTask(listener);
		task.execute(httpUrl);
	}
	
	public ArrayList<Map<String,String>> getProblemList() {
		Log.d(TAG, "get problem");
		ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
		
		for (int i = 0; i < 10; i++) {
			Map<String,String> item = new HashMap<String,String>();
			item.put("title", Integer.toString(i));
			item.put("time", Integer.toString(i+100));
			list.add(item);
		}
		
			
		mProblems = list;
		
		return mProblems;
	}
	
	private class GetProblemTask extends AsyncTask <String, Integer, String> {
		private OnTaskCompleted listener;
		
		public GetProblemTask(OnTaskCompleted listener) {
			this.listener = listener;
		}
		
    	@Override
    	protected void onPreExecute() {
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
	        super.onPostExecute(result);
	        Log.d(TAG, result);
	        listener.onTaskCompleted(result);
	    }
	}
	

    private class LoginTask extends AsyncTask <String, Integer, String> {
    	private OnTaskCompleted listener;
    	
    	public LoginTask(OnTaskCompleted listener) {
    		this.listener = listener;
    	}
    	
    	@Override
    	protected void onPreExecute() {
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
	        super.onPostExecute(result);
	    	if(result == null) {
	    		return;
	    	}
        	JSONObject json = null;
        	String token = "";
	    	
			try {
				json = new JSONObject(result);
				token = json.get("token").toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			listener.onTaskCompleted(token);
	    }
    }
    
    
    private class PostWindowTask extends AsyncTask <String, Integer, String> {
        private OnTaskCompleted listener;
        
        public PostWindowTask(OnTaskCompleted listener) {
            this.listener = listener;
        }
        
        @Override
        protected void onPreExecute() {
            // set loading icon
        }
        
    	@Override
		protected String doInBackground(String... params) {
    		//Log.e(TAG, "url: " + params[0]);
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
	        super.onPostExecute(result);
	        
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
				Toast.makeText(mContext, "Thank you!", Toast.LENGTH_SHORT).show();
			} else if(status == 0 && getFeedback == 1) {
				Toast.makeText(mContext, "Thank you! Go get some candies!", Toast.LENGTH_SHORT).show();
				//mMediaPlayer.start();
			} else {
				Toast.makeText(mContext, "Error!", Toast.LENGTH_SHORT).show();
			}
			
	    	
	    }

    }
    


	
}
