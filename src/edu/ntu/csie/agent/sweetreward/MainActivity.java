package edu.ntu.csie.agent.sweetreward;

import org.json.JSONException;
import org.json.JSONObject;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
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
		
	//GoogleCloudMessaging gcm;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mUser = User.getUser(getApplicationContext());
        serverConnection = ServerConnection.getServerConnection();
        
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
