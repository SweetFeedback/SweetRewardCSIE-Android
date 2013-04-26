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

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends Activity implements OnTaskCompleted {
	static private String TAG = "Setting";

	private SharedPreferences mSettings;
	private SharedPreferences.Editor mSettingEditor;
	private String APIDomain = "http://disa.csie.ntu.edu.tw";
	private String APIPath = "~blt/sweetreward/php";
	
	private EditText mEditTextAccount;
	private EditText mEditTextPassword;
	private Button mButtonSubmit;

	private ServerConnection serverConnection = ServerConnection.getServerConnection();
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        
		mSettings = getSharedPreferences ("SweetReward", MODE_PRIVATE);
		mSettingEditor = mSettings.edit();
		
        mEditTextAccount = (EditText) findViewById(R.id.edit_text_account);
        mEditTextPassword = (EditText) findViewById(R.id.edit_text_password);
        
        String account = mSettings.getString("account", "");
        String password = mSettings.getString("password", "");
        
        mEditTextAccount.setText(account);
        mEditTextPassword.setText(password);
        
        mButtonSubmit = (Button) findViewById(R.id.button_submit);
        mButtonSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String account = mEditTextAccount.getText().toString();
		    	String password = mEditTextPassword.getText().toString();
		    	
		    	serverConnection.login(account, password, SettingActivity.this);
				finish();
			}
        });
    }
	
	public void onLoginSuccessfully(String token) {
		
	}
	/*
	private void login(String account, String password) {
    	String httpUrl = String.format("%s/%s/mobile/createNewUser.php?account=%s&password=%s", APIDomain, APIPath, account, password);
    	
    	CreateAccount c = new CreateAccount();
    	c.execute(httpUrl);
    }	

    private class CreateAccount extends AsyncTask <String, Integer, String> {
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
	    	// parse result
	    	if(result == null) {
	    		Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
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
			
        	String account = mEditTextAccount.getText().toString();
	    	String password = mEditTextPassword.getText().toString();
	    	//Log.e(TAG, account + " " + password);
        	mSettingEditor.putString("account", account);
        	mSettingEditor.putString("password", password);
        	mSettingEditor.putString("token", token);
        	mSettingEditor.commit();
        	Toast.makeText(getApplicationContext(), "Login successfully", Toast.LENGTH_SHORT).show();
        	
	    	super.onPostExecute(result);
	    }
    }
    */

	@Override
	public void onTaskCompleted(String token) {
		//Toast.makeText(this, "Login successfully", Toast.LENGTH_SHORT).show();
		String account = mEditTextAccount.getText().toString();
    	String password = mEditTextPassword.getText().toString();
    	//Log.e(TAG, account + " " + password);
    	
    	mSettingEditor.putString("account", account);
    	mSettingEditor.putString("password", password);
    	mSettingEditor.putString("token", token);
    	mSettingEditor.commit();
		
	}


}
