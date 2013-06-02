package edu.ntu.csie.agent.sweetreward;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class TaskActivity extends Activity {
	private static final String TAG = TaskActivity.class.getSimpleName();
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 1. Instantiate an AlertDialog.Builder with its constructor
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	Bundle bundle = this.getIntent().getExtras();
    	
    	String msg = bundle.getString("content");
    	Log.d(TAG, "content: " + msg);
    	builder.setMessage(msg).setTitle("Would you please ...");
    	builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   Log.d(TAG, getString(R.string.ok));
    	        	   finish();
    	           }
    	       });
    	builder.setNegativeButton(getString(R.string.cencel), new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   Log.d(TAG, getString(R.string.cencel));
    	               finish();
    	           }
    	       });

    	// 3. Get the AlertDialog from create()
    	AlertDialog dialog = builder.create();
    	dialog.show();
	}

}
