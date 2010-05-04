package edu.berkeley.sec.mcr;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class GUI extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	static final int LOAD_PHOTO = 98;

	/*
	 * This is called by the "Load photo from library" button. It calls up the
	 * photo chooser gallery. The resulting file is sent to onActivityResult.
	 */
	public void loadPhoto(View v) {
		Intent picker = new Intent(Intent.ACTION_GET_CONTENT);
		picker.setType("image/*");
		startActivityForResult(picker, LOAD_PHOTO);
	}

	/*
	 * Called when the user picks a photo from the photo gallery. Passes the URL
	 * of the file to the GotPhoto activity.
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == LOAD_PHOTO) {
			if (resultCode == RESULT_OK) {
				Uri thePhoto = data.getData();
				Intent i = new Intent(GUI.this, GotPhoto.class);
				i.putExtra("thePhoto", thePhoto);
				startActivity(i);
			}
		}
	}
}