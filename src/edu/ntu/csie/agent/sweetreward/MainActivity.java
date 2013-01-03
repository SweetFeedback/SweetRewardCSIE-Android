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

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

public class MainActivity extends Activity {
	static private String TAG = "SweetReward";
	private WebView webView;
	private SharedPreferences mSettings;
	private String mToken;
	private String APIDomain = "http://disa.csie.ntu.edu.tw";
	private String APIPath = "~blt/sweetreward/php";
	private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        webView = (WebView) findViewById(R.id.webView);
		//webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl("http://disa.csie.ntu.edu.tw/~blt/sweetreward/");
		
		mMediaPlayer = MediaPlayer.create(this, R.raw.reward);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();

		mSettings = getSharedPreferences ("SweetReward", MODE_PRIVATE);
		mToken = mSettings.getString("token", "");
		Log.e(TAG, "token; " + mToken);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
	    	case R.id.menu_scan:
	    		IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
	    		integrator.initiateScan();
	    		/*
	    		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
	            startActivityForResult(intent, 0);
	            */
	    		break;
	    	case R.id.menu_settings:
	    		Intent intent = new Intent(this, SettingActivity.class);
	            this.startActivity(intent);
	            
	    		break;
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
                String httpUrl = "";
                if(contentsArray.length == 1) {
                	int windowID = Integer.valueOf(contentsArray[0]);
                	httpUrl = String.format("%s/%s/userActionTrigger.php?window_id=%d&token=%s", APIDomain, APIPath, windowID, mToken);
                } else if(contentsArray.length == 2) {
                	int windowID = Integer.valueOf(contentsArray[0]);
                	int action = Integer.valueOf(contentsArray[1]);
                	httpUrl = String.format("%s/%s/userActionTrigger.php?window_id=%d&token=%s&action=%d", APIDomain, APIPath, windowID, mToken, action);
                } else {
                	httpUrl = "";
                	Log.e(TAG, "Error: unknown QRCode type");
                }
                
                if(httpUrl != "") {
                	PostWindow p = new PostWindow();
                	p.execute(httpUrl);
                }
                
                
                
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
                
            }
        }
    }
    
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
				Toast.makeText(getApplicationContext(), "Thank you!", Toast.LENGTH_SHORT).show();
			} else if(status == 0 && getFeedback == 1) {
				Toast.makeText(getApplicationContext(), "Thank you! Go get some candies!", Toast.LENGTH_SHORT).show();
				mMediaPlayer.start();
			} else {
				Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
			}
			
	    	super.onPostExecute(result);
	    }

    }
        
    
}
