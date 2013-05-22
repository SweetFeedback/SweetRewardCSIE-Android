package edu.ntu.csie.agent.sweetreward;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class MainActivity extends Activity implements OnTaskCompleted {
	static private String TAG = "SweetReward";
	private WebView webView;
	
	private User mUser;
	private ServerConnection serverConnection;

	private MediaPlayer mMediaPlayer;
	
	
	//GoogleCloudMessaging gcm;
	
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
