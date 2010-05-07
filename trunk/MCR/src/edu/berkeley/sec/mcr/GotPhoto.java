package edu.berkeley.sec.mcr;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * This Activity takes in a URI for a photo. It displays the photo. It has
 * transform & play buttons.
 */
public class GotPhoto extends Activity {

	private Uri midiFileUri;
	private Uri musicPhotoUri;

	/*
	 * Expects an image Uri to be passed in as an extra, named "thePhoto".
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gotphoto);
		midiFileUri = null;

		// Get photo URI
		Bundle extras = getIntent().getExtras();
		musicPhotoUri = (Uri) extras.get("thePhoto");

		// Set the photo
		ImageView i = (ImageView) this.findViewById(R.id.sheet_music);
		i.setImageURI(musicPhotoUri);

		// To get the actual image data:
		// AssetFileDescriptor thePhoto =
		// getContentResolver().openAssetFileDescriptor(URI, "r");
	}

	static final int TRANS_LOCAL = 99;

	// Called when Transform - Local button is clicked
	public void startTransform(View v) {

		Intent transform = new Intent(GotPhoto.this, TransformLocal.class);
		transform.setData(musicPhotoUri);
		startActivityForResult(transform, TRANS_LOCAL);
	}

	/*
	 * This is called by the Play button. Plays the midi file returned by
	 * transform.
	 */
	public void playMusic(View v) {
		// Would be nice to get the built-in music player interface working,
		// but I give up.

		if (midiFileUri != null) {
			MediaPlayer mp = MediaPlayer.create(GotPhoto.this, midiFileUri);
			mp.start();
		}

		// No idea why the ACTION_VIEW works in GUI.java, but not here.
		// if (midiFileUri != null) {
		// Intent playMusic = new Intent(Intent.ACTION_VIEW);
		// playMusic.setData(midiFileUri);
		// playMusic.setData(Uri
		// .parse("android.resource://edu.berkeley.sec.mcr/raw/twinkle"));
		// playMusic.setType("audio/mid");
		// startActivity(playMusic);
		else {
			// // What should we do if the user hasn't transformed anything yet?
		}
	}

	/*
	 * This is called by the save button. Saves the midi file returned by
	 * transform.
	 */
	public void saveMusic(View v) {

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TRANS_LOCAL) {
			if (resultCode == RESULT_OK) {
				GotPhoto.this.midiFileUri = data.getData();
				// It would be cool to change the sheet_music image to be
				// something
				// that looked parsed in some way.

			} else if (resultCode == RESULT_CANCELED) {
				// Crap.
			}
		}
	}
}
