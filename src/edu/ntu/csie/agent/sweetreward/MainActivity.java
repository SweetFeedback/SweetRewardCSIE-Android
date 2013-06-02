package edu.ntu.csie.agent.sweetreward;

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
	static private String TAG = "SweetReward";
	private WebView webView;
	
	private User mUser;
	private ServerConnection serverConnection;

	private MediaPlayer mMediaPlayer;
	
	public static final String PROPERTY_REG_ID = "registration_id";
	String GCM_SENDER_ID = "411973252223";
	
	private GoogleCloudMessaging gcm;
	SharedPreferences prefs;
	String regid;

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mUser = User.getUser(getApplicationContext());
        serverConnection = ServerConnection.getServerConnection(getApplicationContext());
        
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
		Log.d(TAG, "regid = " + regid);
        if (regid == null) {
        	Log.d(TAG, "start register");
            registerBackground();
        }
		
    }

    
    private void registerBackground() {
        new AsyncTask<Object, Object, Object>() {
        	
            @Override
            protected Object doInBackground(Object... params) {
            	Log.d(TAG, "do in background");
                String msg = "";
                try {
                    regid = gcm.register(GCM_SENDER_ID);
                    msg = "Device registered, registration id=" + regid;

                    // You should send the registration ID to your server over HTTP, 
                    // so it can use GCM/HTTP or CCS to send messages to your app.

                    // For this demo: we don't need to send it because the device  
                    // will send upstream messages to a server that will echo back 
                    // the message using the 'from' address in the message. 
            
                    // Save the regid for future use - no need to register again.
                    SharedPreferences.Editor editor = prefs.edit();
                    Log.d(TAG, "regid: " + regid);
                    editor.putString(PROPERTY_REG_ID, regid);
                    editor.commit();
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }
            // Once registration is done, display the registration status
            // string in the Activity's UI.
            @Override
            protected void onPostExecute(Object msg) {
            	Log.d(TAG, "regid: " + msg);
            	//Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
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
                
                serverConnection.reportWindow(windowID, action);
                
                
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
                
            }
        }
    }
    
	@Override
	public void onTaskCompleted(String result) {
		// TODO Auto-generated method stub
		
	}
    
        
    
}
