package edu.ntu.csie.agent.sweetreward;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ProblemListActivity extends Activity implements OnTaskCompleted {
	private static final String TAG = ProblemListActivity.class.getSimpleName();
	
	private ListView mProblemList;
	private LinearLayout mProgress;
	private TextView mNoNetworkTextView;
	private Handler mUpdateHandler;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "Start ProblemListActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_list);
        
        mUpdateHandler = new Handler();
        
        mProgress = (LinearLayout) findViewById(R.id.headerProgressLinearLayout);
        mProblemList = (ListView) findViewById(R.id.ProblemListLinearLayout);
        
        mNoNetworkTextView = (TextView) findViewById(R.id.noNetworkTextView_ProblemList);
        mNoNetworkTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Settings.ACTION_SETTINGS);
				startActivity(intent);
			}
		});
    }
	
	private int mUpdateInterival = 5000;
	
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	mProgress.setVisibility(View.VISIBLE);
    	Boolean success = ServerConnection.getServerConnection().getProblemList(ProblemListActivity.this);
    	if (!success) {
    		mNoNetworkTextView.setVisibility(View.VISIBLE);
    		mUpdateHandler.postDelayed(mUpdateTaskRunnable, mUpdateInterival);
    	}
    }
    
    

    private Runnable mUpdateTaskRunnable = new Runnable() {
	    public void run() {
	    	Log.d(TAG, "RUN");
	    	Boolean success = ServerConnection.getServerConnection().getProblemList(ProblemListActivity.this);
	    	if (!success) {
	    		mUpdateHandler.postDelayed(this, mUpdateInterival);
	        } else {
	        	mNoNetworkTextView.setVisibility(View.GONE);
	        	mUpdateHandler.removeCallbacks(mUpdateTaskRunnable);
	        }
	    }
    };
    
    
	
	@Override
    public void onTaskCompleted(String jsonString) {
		Log.d(TAG, "Received server result");
		List<Map<String,String>> problemList = new ArrayList<Map<String,String>>();
    	// parse result
    	JSONObject json = null;
		try {
			if (jsonString == null) {
				Log.d(TAG, "Return jsonString null");
				return;
			}
			json = new JSONObject(jsonString);
			JSONArray dataArray = json.getJSONArray("data");
			for (int i = 0; i < dataArray.length(); i++) {
				Map<String,String> dataMap = new HashMap<String,String>();
				JSONObject data = dataArray.getJSONObject(i);
				dataMap.put("title", data.getString("title"));
				dataMap.put("created_at", data.getString("created_at"));
				
				problemList.add(dataMap);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// set adapter
		Log.d(TAG, "Set listview adapter");
        SimpleAdapter adapter = new SimpleAdapter(this, problemList, android.R.layout.simple_list_item_2, new String[] {"title", "created_at"}, new int[] { android.R.id.text1, android.R.id.text2 });
        mProblemList.setAdapter(adapter);
        this.mProgress.setVisibility(View.GONE);    
	}
}
