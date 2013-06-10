package edu.ntu.csie.agent.sweetreward;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class User {
    private String mAccount;
    private String mPassword;
    private String mToken;
    
    private String mFacebookID;
    private String mFacebookName;
    
    private String mGCMID;
    private Boolean mIsGCMRegistered;
    
    private SharedPreferences mSettings;
    private SharedPreferences.Editor mSettingEditor;
    
    private static User sSingleton;
    
    public static User getUser() {
        return sSingleton;
    }
    
    public static User getUser(Context context) {
        if (sSingleton == null) {
            sSingleton = new User(context);
        }

        return sSingleton;
    }
    
    private User(Context context) {
        mSettings = context.getSharedPreferences ("SweetReward", Context.MODE_PRIVATE);
        mSettingEditor = mSettings.edit();
    }
    
    public String getAccount() {
    	if(mAccount == null)
    		mAccount = mSettings.getString("account", "");
        return mAccount;
    }
    
    public String getPassword() {
    	if(mPassword == null)
    		mPassword = mSettings.getString("password", "");
        return mPassword;
    }
    
    public String getToken() {
    	if(mToken == null)
    		mToken = mSettings.getString("token", "");
        return mToken;
    }
    
    public String getFacebookName() {
    	if(mFacebookName == null)
    		mFacebookName = mSettings.getString("facebook_name", "");
        return mFacebookName;
    }
    
    public String getFacebookID() {
    	if(mFacebookID == null)
    		mFacebookID = mSettings.getString("facebook_id", "");
        return mFacebookID;
    }
    
    public String getGCMID() {
    	if(mGCMID == null)
    		mGCMID = mSettings.getString("registration_id", "");
    	return mGCMID;
    }
    
    public Boolean isRegisteredGCMId() {
    	if(mIsGCMRegistered == null)
    		mIsGCMRegistered = mSettings.getBoolean("is_registered", false);
    	Log.d("OHO", "registered " + mIsGCMRegistered.toString());
    	return mIsGCMRegistered;
    }
    
    public void setAccount(String account) {
        mAccount = account;
        mSettingEditor.putString("account", account);
        mSettingEditor.commit();
    }
    
    public void setPassword(String password) {
        mPassword = password;
        mSettingEditor.putString("password", password);
        mSettingEditor.commit();
    }
    
    public void setToken(String token) {
        mToken = token;
        mSettingEditor.putString("token", token);
        mSettingEditor.commit();
    }
    
    public void setFacebookID(String id) {
        mFacebookID = id;
        mSettingEditor.putString("facebook_id", mFacebookID);
        mSettingEditor.commit();
    }
    
    public void setFacebookName(String name) {
        mFacebookName = name;
        mSettingEditor.putString("facebook_name", mGCMID);
        mSettingEditor.commit();
    }
    
    public void setGCMID(String GCMID) {
    	mGCMID = GCMID;
    	mSettingEditor.putString("registration_id", mGCMID);
        mSettingEditor.commit();
    }
    
    public void setIsGCMRegistered(Boolean isGCMRegistered) {
    	mIsGCMRegistered = isGCMRegistered;
    	Log.d("OHO", "set registered " + mIsGCMRegistered.toString());
    	mSettingEditor.putBoolean("is_registered", mIsGCMRegistered);
    	mSettingEditor.commit();
    }
}
