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

import com.facebook.*;
import com.facebook.model.*;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends FragmentActivity implements OnTaskCompleted {
	static private String TAG = "Setting";
	
	private FacebookLoginFragment mFacebookLoginFragment;
	
	private User mUser = User.getUser(this);
	
	private EditText mEditTextAccount;
	private EditText mEditTextPassword;
	private Button mButtonSubmit;
	
	private LinearLayout mProgress;


	private ServerConnection serverConnection;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        
        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            mFacebookLoginFragment = new FacebookLoginFragment();
            getSupportFragmentManager()
            .beginTransaction()
            .add(android.R.id.content, mFacebookLoginFragment)
            .commit();
        } else {
            // Or set the fragment from restored state info
            mFacebookLoginFragment = (FacebookLoginFragment) getSupportFragmentManager()
            .findFragmentById(android.R.id.content);
        }
        
        
        
        serverConnection = ServerConnection.getServerConnection(getApplicationContext());
        
        mProgress = (LinearLayout) findViewById(R.id.headerProgressLinearLayout);
        mEditTextAccount = (EditText) findViewById(R.id.edit_text_account);
        mEditTextPassword = (EditText) findViewById(R.id.edit_text_password);
        
        String account = mUser.getAccount();
        String password = mUser.getPassword();
        mEditTextAccount.setText(account);
        mEditTextPassword.setText(password);

        mButtonSubmit = (Button) findViewById(R.id.button_submit);
        mButtonSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String account = mEditTextAccount.getText().toString();
		    	String password = mEditTextPassword.getText().toString();
		    	SettingActivity.this.mProgress.setVisibility(View.VISIBLE);
		    	
		    	serverConnection.login(account, password, SettingActivity.this);
		    	
				finish();
			}
        });
        
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	
	@Override
	public void onTaskCompleted(String token) {
	    this.mProgress.setVisibility(View.GONE);
		String account = mEditTextAccount.getText().toString();
    	String password = mEditTextPassword.getText().toString();
    	
    	mUser.setAccount(account);
    	mUser.setPassword(password);
    	mUser.setToken(token);
	}
	


}
