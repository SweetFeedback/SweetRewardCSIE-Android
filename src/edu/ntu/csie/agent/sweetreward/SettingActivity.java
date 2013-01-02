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
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SettingActivity extends Activity {
	static private String TAG = "Setting";

	private ListView mListView;
	private SharedPreferences mSettings;
	private SharedPreferences.Editor mSettingEditor;
	private String mToken;
	private String APIDomain = "http://disa.csie.ntu.edu.tw";
	private String APIPath = "~blt/sweetreward/php";
	
	private EditText mEditTextAccount;
	private EditText mEditTextPassword;
	private Button mButtonSubmit;

	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        /*
        String[] strings = new String[] {
        	"account", "password"	
        };
		mListView = (ListView) findViewById(R.id.ListView);
		mListView.setAdapter(new ArrayAdapter<String>(this,
				 android.R.layout.simple_list_item_1, strings));
		
		mListView.setTextFilterEnabled(true);
		*/
		mSettings = getSharedPreferences ("SweetReward", MODE_PRIVATE);
		mSettingEditor = mSettings.edit();
		
        mEditTextAccount = (EditText) findViewById(R.id.edit_text_account);
        mEditTextPassword = (EditText) findViewById(R.id.edit_text_password);
        mButtonSubmit = (Button) findViewById(R.id.button_submit);
        mButtonSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String account = mEditTextAccount.getText().toString();
		    	String password = mEditTextPassword.getText().toString();
		    	
				login(account, password);
			}
        });
    }
	
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
        	result = result.replace("existed", "");
        	
        	JSONObject json = null;
        	String token = "";
			try {
				json = new JSONObject(result);
				token = json.get("token").toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			mToken = token;
        	mSettingEditor.putString("token", token);
        	mSettingEditor.commit();
        	Toast.makeText(getApplicationContext(), "Login successfully", Toast.LENGTH_SHORT).show();
	    	
        	mToken = mSettings.getString("token", "");
        	
        	
	    	super.onPostExecute(result);
	    }
    }


}
