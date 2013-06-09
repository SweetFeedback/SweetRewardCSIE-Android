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
        return mAccount;
    }
    
    public String getPassword() {
        return mPassword;
    }
    
    public String getToken() {
        return mToken;
    }
    
    public String getFacebookName() {
        return mFacebookName;
    }
    
    public String getFacebookID() {
        return mFacebookID;
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
        mSettingEditor.putString("fb_id", mFacebookID);
        mSettingEditor.commit();
    }
    
    public void setFacebookName(String name) {
        mFacebookName = name;
    }
    
    

}
