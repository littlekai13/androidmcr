package edu.berkeley.sec.mcr;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * This Activity takes in a URI for a photo. It displays the photo. It has
 * transform & play buttons.
 */
public class GotPhoto extends Activity {

	/*
	 * Expects an image Uri to be passed in as an extra, named "thePhoto".
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gotphoto);

		// Get photo URI
		Bundle extras = getIntent().getExtras();
		Uri URI = (Uri) extras.get("thePhoto");

		// Set the photo
		ImageView i = (ImageView) this.findViewById(R.id.sheet_music);
		i.setImageURI(URI);

		// To get the actual image data:
		// AssetFileDescriptor thePhoto =
		// getContentResolver().openAssetFileDescriptor(URI, "r");
	}

	static final int TRANS_LOCAL = 99;

	// Called when Transform - Local button is clicked
	public void startTransform(View v) {
		// This does nothing. Wanted to make it look selected. Not important.
		// ImageButton ib = (ImageButton)
		// this.findViewById(R.id.transform_local);
		// ib.setSelected(true);
		Intent transform = new Intent(GotPhoto.this, TransformLocal.class);
		// picker.setType("image/*");
		startActivityForResult(transform, TRANS_LOCAL);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TRANS_LOCAL) {
			if (resultCode == RESULT_OK) {
				// Yay. Let user play music.
				// Change the image to be the returned XML or marked-up sheet
				// music.
				Bundle extras = data.getExtras();
				Uri musicXMLURI = (Uri) extras.get("musicXMLSheet");
				ImageView i = (ImageView) this.findViewById(R.id.sheet_music);
				i.setImageURI(musicXMLURI);
			} else if (resultCode == RESULT_CANCELED) {
				// Crap.
			}
		}
	}

}
