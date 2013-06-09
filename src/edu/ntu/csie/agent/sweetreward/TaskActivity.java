package edu.ntu.csie.agent.sweetreward;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class TaskActivity extends Activity {
	private static final String TAG = TaskActivity.class.getSimpleName();
	
	private SeekBar mSeekBar;
	private ServerConnection mServerConnection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = this.getIntent().getExtras();
		String msg = bundle.getString("content");
		Log.d(TAG, "content: " + msg);

		mServerConnection = ServerConnection.getServerConnection();

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = getLayoutInflater();;
		View view = inflater.inflate(R.layout.dialog_notification, null);
		mSeekBar = (SeekBar) view.findViewById(R.id.dialog_notification_annoy_seekbar);


		//TextView msgTextView = (TextView) view.findViewById(R.id.dialog_notification_msg_textview);
		//msgTextView.setText(msg);

		builder.setView(view);
		builder.setMessage(msg + "\n\nDo you feel annoyed?");
		builder.setTitle("Would you please ...");
		builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				int annoy = mSeekBar.getProgress();
				mServerConnection.responseNotification(1, annoy);
				finish();
			}
		});
		builder.setNegativeButton(getString(R.string.cencel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				int annoy = mSeekBar.getProgress();
				mServerConnection.responseNotification(0, annoy);
				finish();
			}
		});
		AlertDialog dialog = builder.create();

		dialog.show();
	}

}
