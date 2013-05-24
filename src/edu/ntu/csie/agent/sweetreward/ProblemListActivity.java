package edu.ntu.csie.agent.sweetreward;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ProblemListActivity extends Activity implements OnTaskCompleted {
	private static final String TAG = ProblemListActivity.class.getSimpleName();
	
	private ListView mProblemList;
	
	private LinearLayout mProgress;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problem_list);
        
        mProgress = (LinearLayout) findViewById(R.id.headerProgressLinearLayout);
        mProblemList = (ListView) findViewById(R.id.ProblemListLinearLayout);
        
        mProgress.setVisibility(View.VISIBLE);
        ServerConnection.getServerConnection().getProblemListFromServer(this);
        
        
    }
	
	@Override
    public void onTaskCompleted(String status) {
        this.mProgress.setVisibility(View.GONE);
        
        List<Map<String,String>> data = ServerConnection.getServerConnection().getProblemList();
        SimpleAdapter adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2, new String[] {"title", "time"}, new int[] { android.R.id.text1, android.R.id.text2 });
        mProblemList.setAdapter(adapter);
    
	}
}
