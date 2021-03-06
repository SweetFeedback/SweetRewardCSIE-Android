package edu.ntu.csie.agent.sweetreward;

import java.security.SecureRandom;
import java.util.Random;
import java.math.BigInteger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

public class TaskActivity extends Activity {
	private static final String TAG = TaskActivity.class.getSimpleName();

	private SeekBar mSeekBar;
	private ServerConnection mServerConnection;
	private int mNotificationId;
	private AlertDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "on create task activity");
		super.onCreate(savedInstanceState);

		Bundle bundle = this.getIntent().getExtras();
		String msg = bundle.getString("content");
		mNotificationId = bundle.getInt("notification_id");
		Log.d(TAG, "content: " + msg + " task id: " + String.valueOf(mNotificationId));
		
		init();

		mServerConnection.openNotification(mNotificationId);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = getLayoutInflater();;
		View view = inflater.inflate(R.layout.dialog_notification, null);
		mSeekBar = (SeekBar) view.findViewById(R.id.dialog_notification_annoy_seekbar);


		builder.setView(view);
		builder.setMessage(msg + "\n\nDo you feel annoyed?");
		builder.setTitle("Would you please ...");
		builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				int annoy = mSeekBar.getProgress();
				mServerConnection.responseNotification(mNotificationId, 1, annoy);
				Log.d(TAG, "dismiss dialog");
				dialog.dismiss();
				Log.d(TAG, "finish activity");
				finish();
			}
		});
		builder.setNegativeButton(getString(R.string.cencel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				int annoy = mSeekBar.getProgress();
				mServerConnection.responseNotification(mNotificationId, 0, annoy);
				Log.d(TAG, "dismiss dialog");
				dialog.dismiss();
				Log.d(TAG, "finish activity");
				finish();
			}
		});
		
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				Log.d(TAG, "on cancel dialog");
				int annoy = mSeekBar.getProgress();
				mServerConnection.responseNotification(mNotificationId, 2, annoy);
				finish();
			}
		});

		mDialog = builder.create();
		mDialog.show();
	}
	
	@Override
	protected void onStop() {
		Log.d(TAG, "on stop task activity");
		if (mDialog != null) {
			mDialog.dismiss();
		}
		super.onStop();
		finish();
	}
	
	private void init() {
		User.getUser(getApplicationContext());
		mServerConnection = ServerConnection.getServerConnection();
		mServerConnection.setContext(getApplicationContext());
	}

}
