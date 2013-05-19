package edu.ntu.csie.agent.sweetreward;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FacebookLoginFragment extends Fragment implements OnTaskCompleted {
    private static final String TAG = "FacebookLoginFragment";
    
    private UiLifecycleHelper uiHelper;
    
    private User mUser;
    
    private EditText mEditTextAccount;
    private EditText mEditTextPassword;
    private TextView mWelcome;
    private Button mButtonSubmit;
    
    private LinearLayout mProgress;


    private ServerConnection serverConnection;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
        
        mUser = User.getUser();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.facebook_login_fragment, container, false);
        
        LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        
        mWelcome = (TextView) view.findViewById(R.id.welcome);
        
        
        
        /*
        serverConnection = ServerConnection.getServerConnection(this.getActivity().getApplicationContext());
        
        mProgress = (LinearLayout) view.findViewById(R.id.headerProgressLinearLayout);
        mEditTextAccount = (EditText) view.findViewById(R.id.edit_text_account);
        mEditTextPassword = (EditText) view.findViewById(R.id.edit_text_password);
        
        mWelcome = (TextView) view.findViewById(R.id.welcome);
        
        String account = mUser.getAccount();
        String password = mUser.getPassword();
        mEditTextAccount.setText(account);
        mEditTextPassword.setText(password);

        mButtonSubmit = (Button) view.findViewById(R.id.button_submit);
        mButtonSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = mEditTextAccount.getText().toString();
                String password = mEditTextPassword.getText().toString();
                FacebookLoginFragment.this.mProgress.setVisibility(View.VISIBLE);
                
                serverConnection.login(account, password, FacebookLoginFragment.this);

            }
        });
        */

        return view;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
               (session.isOpened() || session.isClosed()) ) {
            Log.d(TAG, "on resume");
            onSessionStateChange(session, session.getState(), null);
        }
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
    
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            makeMeRequest(session);
        } else if (state.isClosed()) {
            Log.d(TAG, "Logged out...");
            mWelcome.setText("LOGout");
            mUser.setFacebookID("");
            mUser.setFacebookName("");
        }
    }
    
    
    @Override
    public void onTaskCompleted(String token) {
        /*
        this.mProgress.setVisibility(View.GONE);
        String account = mEditTextAccount.getText().toString();
        String password = mEditTextPassword.getText().toString();
        
        mUser.setAccount(account);
        mUser.setPassword(password);
        mUser.setToken(token);
        */
    }
    
    private void makeMeRequest(final Session session) {
        // Make an API call to get user data and define a 
        // new callback to handle the response.
        Request request = Request.newMeRequest(session, 
                new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                // If the response is successful
                if (session == Session.getActiveSession()) {
                    if (user != null) {
                        mUser.setFacebookID(user.getId());
                        mUser.setFacebookName(user.getName());
                        mWelcome.setText("hi " + mUser.getFacebookID() + " " + mUser.getFacebookName());
                    }
                }
                if (response.getError() != null) {
                    // Handle errors, will do so later.
                }
            }
        });
        request.executeAsync();
    } 
    
    

}
