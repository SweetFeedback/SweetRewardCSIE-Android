package edu.ntu.csie.agent.sweetreward;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ReportProblemActivity extends Activity implements OnTaskCompleted {
		private static final String TAG = ReportProblemActivity.class.getSimpleName();
		
		private ServerConnection serverConnection;
		private EditText mProblemTitleEditText;
		private EditText mProblemDescEditText;
		private Button mSubmitButton;
		
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_report_problem);
	        
	        mProblemTitleEditText = (EditText) findViewById(R.id.report_problem_edit_text_problem_title);
	        mProblemDescEditText = (EditText) findViewById(R.id.report_problem_edit_text_problem_desc);
	        mSubmitButton = (Button) findViewById(R.id.report_problem_button_submit);
	        
	        mSubmitButton.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                String title = mProblemTitleEditText.getText().toString();
	                String desc = mProblemDescEditText.getText().toString();
	                serverConnection.reportProblem(ReportProblemActivity.this);
	            }
	        });
	        
	        serverConnection = ServerConnection.getServerConnection();
	    }

		@Override
		public void onTaskCompleted(String jsonString) {
			// TODO Auto-generated method stub
			
		}

}
