package edu.berkeley.sec.mcr;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
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
	}

	static final int TRANS_LOCAL = 99;

	// Called when Read button is clicked
	public void startTransform(View v) {

	    try {
            AssetFileDescriptor thePhoto =
                getContentResolver().openAssetFileDescriptor(musicPhotoUri, "r");
            FileChannel orig = thePhoto.createInputStream().getChannel();
            File imageFile = File.createTempFile("musicImages", ".jpg");
            FileChannel tmpFile = new FileOutputStream(imageFile).getChannel();
            
            try { orig.transferTo(0, orig.size(), tmpFile); }
            catch (IOException e) { throw e; }
            finally { 
                if (orig != null) orig.close();
                if (tmpFile != null) tmpFile.close();
            }
          
            HttpClient client = new DefaultHttpClient();
            if (imageFile.exists()) {
                Log.v("Debug", "Image file exists");
                Map postData = new HashMap();
                Map postDataFiles = new HashMap();
                postDataFiles.put("file", imageFile);
                HttpData httpData = HttpRequest.postRequest(client, "http://gradgrind.erso.berkeley.edu:8080/Audiveris/RunAudiveris", postData, postDataFiles);
                Log.v("Debug",httpData.data);
            } else {
                Log.v("Debug","File not found "+ imageFile.getAbsolutePath());
            }
            
        } catch (Exception e) {
            Log.v("Debug",e.getMessage());
            e.printStackTrace();
        }
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
			// What should we do if the user hasn't transformed anything yet?
		}
	}

	/*
	 * This is called by the save button. Saves the midi file returned by
	 * transform to the Media content provider.
	 */
	public void saveMusic(View v) {
		ContentValues cv = new ContentValues(3);
		cv.put(Media.DISPLAY_NAME, "transformed twinkle");
		cv.put(Media.TITLE, "Twinkle Twinkle Little Star");
		cv.put(Media.MIME_TYPE, "audio/mid");

		ContentResolver cr = getContentResolver();
		Uri insertedUri = cr.insert(Media.EXTERNAL_CONTENT_URI, cv);

		// How this is done really depends on how the midi file is stored
		// up to this point.
		try {
			OutputStream os = cr.openOutputStream(insertedUri);

		} catch (Exception ex) {

		}

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
