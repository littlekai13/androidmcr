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

	static final int TRANSFORM = 97;
	static final int LOAD_PHOTO = 98;
	static final int BROWSE_MUSIC = 99;

	/*
	 * This is called by the "Load photo from library" button. It calls up the
	 * photo chooser gallery. The resulting file is sent to onActivityResult.
	 */
	public void loadPhoto(View v) {
		Intent picker = new Intent(Intent.ACTION_GET_CONTENT);
		picker.setType("image/*");
		startActivityForResult(picker, LOAD_PHOTO);
	}

	public void loadPhotoCallback(Intent data) {
		Uri thePhoto = data.getData();
		Intent transformer = new Intent(GUI.this, GotPhoto.class);
		transformer.putExtra("thePhoto", thePhoto);
		startActivityForResult(transformer, TRANSFORM);
	}

	/*
	 * This is called by the "Saved music" button. It calls up the music chooser
	 * gallery. The resulting file is sent to onActivity Result.
	 */
	public void browseMusic(View v) {
		Intent browser = new Intent(Intent.ACTION_GET_CONTENT);
		browser.setType("audio/mid");
		// When back button pressed, we should get RESULT_CANCELED
		// result
		startActivityForResult(browser, BROWSE_MUSIC);
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
				loadPhotoCallback(data);
				/*
				 * Uri thePhoto = data.getData(); Intent i = new
				 * Intent(GUI.this, GotPhoto.class); i.putExtra("thePhoto",
				 * thePhoto);
				 * 
				 * startActivityForResult(i, TRANSFORM);
				 */
			}
		}

		// Came back from GotPhoto.
		else if (requestCode == TRANSFORM) {
			if (resultCode == RESULT_OK) {
				// I don't think we'll ever get here since we only return by
				// pressing back button.
			} else if (resultCode == RESULT_CANCELED) {
				// Back button was pressed.

			}
		}
		// Go to nice music player.
		else if (requestCode == BROWSE_MUSIC) {
			if (resultCode == RESULT_OK) {
				Uri midiFile = data.getData();
				Intent i = new Intent(Intent.ACTION_VIEW);
				// i.setType("audio");
				i.setData(midiFile);
				startActivity(i);
			}
		}
	}
}