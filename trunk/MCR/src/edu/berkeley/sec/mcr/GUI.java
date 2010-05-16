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
	static final int BROWSE_MUSIC = 99;
	static final int SHARE_MUSIC =96;

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
	 * This is called by the "Saved music" button. It calls up the music chooser
	 * gallery. The resulting file is sent to onActivity Result.
	 */
	public void browseMusic(View v) {
		Intent browser = new Intent(Intent.ACTION_GET_CONTENT);
		browser.setType("audio/mid");
		startActivityForResult(browser, BROWSE_MUSIC);	
	}

	/*
	 * This is called by the "Share music" button. It allows 
	 * you to email something
	 */
	
	public void shareMusic(View v) {   
		Intent getsomethingtoemail = new Intent(Intent.ACTION_GET_CONTENT);
		getsomethingtoemail.setType("audio/mid");
		startActivityForResult(getsomethingtoemail, SHARE_MUSIC);
		// Or to just implement email directly from the button:
		//Intent emailIntent = new Intent(Intent.ACTION_SEND);
		//emailIntent.setType("plain/text");		
		//emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this music");
		//emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Sent from my Android's Music Recognition App."); 
		//startActivity(Intent.createChooser(emailIntent, "Send mail with...")); 
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Load the photo & transform it
		if (requestCode == LOAD_PHOTO) {
			if (resultCode == RESULT_OK) {
				Uri thePhoto = data.getData();
				Intent i = new Intent(GUI.this, GotPhoto.class);
				i.putExtra("thePhoto", thePhoto);
				startActivity(i);
			}
		}
		// Go to nice music player.
		if (requestCode == BROWSE_MUSIC) {
			if (resultCode == RESULT_OK) {			
				Uri midiFile = data.getData();
				Intent i = new Intent(Intent.ACTION_VIEW);
				// i.setType("audio");
				i.setData(midiFile);
				startActivity(i);
			}
		}
		// Share music
		if (requestCode == SHARE_MUSIC) {	
			if (resultCode == RESULT_OK) {
				Uri selectedFile = data.getData();
				Intent emailIntent = new Intent(Intent.ACTION_SEND);
				emailIntent.setType("plain/text");		
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this music");
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Sent from my Android's Music Recognition App."); 
				//if you want to put in an email address: i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"blah@blah.com"});
				emailIntent.putExtra(android.content.Intent.EXTRA_STREAM, selectedFile);
				startActivity(Intent.createChooser(emailIntent, "Send mail with..."));        			
			}
		}
	}
}