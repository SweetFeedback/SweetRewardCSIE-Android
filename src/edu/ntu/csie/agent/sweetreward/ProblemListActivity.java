package edu.ntu.csie.agent.sweetreward;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.anim;
import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ProblemListActivity extends Activity {
	private ListView mProblemList;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problem_list);
        
        List<Map<String,String>> data = ServerConnection.getServerConnection().getProblemList();  	
        SimpleAdapter adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2, new String[] {"title", "time"}, new int[] { android.R.id.text1, android.R.id.text2 });
        mProblemList = (ListView) findViewById(R.id.ProblemListLinearLayout);
        mProblemList.setAdapter(adapter);
        
    }
}
