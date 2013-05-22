package edu.ntu.csie.agent.sweetreward;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class SettingActivity extends FragmentActivity {
	//static private String TAG = "Setting";
	
	private LoginFragment mFacebookLoginFragment;
		
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        
        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            mFacebookLoginFragment = new LoginFragment();
            getSupportFragmentManager()
            .beginTransaction()
            .add(android.R.id.content, mFacebookLoginFragment)
            .commit();
        } else {
            // Or set the fragment from restored state info
            mFacebookLoginFragment = (LoginFragment) getSupportFragmentManager()
            .findFragmentById(android.R.id.content);
        }
        
   
    }
}
