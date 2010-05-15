package edu.berkeley.sec.mcr;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
	private FileDescriptor fd;
	private Uri musicPhotoUri;
	private String hi = "Hello";

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

		try {
			InputStream is = getContentResolver()
					.openInputStream(musicPhotoUri);

			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;

			Bitmap bmp = BitmapFactory.decodeStream(is, null, opts);

			// hard-coded in ImageView width from gotphoto.xml
			int scale = Math.round(opts.outWidth / 200);

			opts = new BitmapFactory.Options();

			opts.inSampleSize = scale;
			is = getContentResolver().openInputStream(musicPhotoUri);
			bmp = BitmapFactory.decodeStream(is, null, opts);
			i.setImageBitmap(bmp);
		} catch (FileNotFoundException ex) {
			// Should probably display some icon here.
		}

	}

	// Called when "Read music" button is clicked
	public void startTransform(View v) {

		Thread t = new Thread() {
			public void run() {
				try {
					// Get the image
					AssetFileDescriptor thePhoto = getContentResolver()
							.openAssetFileDescriptor(musicPhotoUri, "r");
					FileChannel orig = thePhoto.createInputStream()
							.getChannel();
					File imageFile = File.createTempFile("musicImages", ".jpg");
					FileChannel tmpFile = new FileOutputStream(imageFile)
							.getChannel();
					try {
						orig.transferTo(0, orig.size(), tmpFile);
					} catch (IOException e) {
						throw e;
					} finally {
						if (orig != null)
							orig.close();
						if (tmpFile != null)
							tmpFile.close();
					}
					// Send it
					HttpClient client = new DefaultHttpClient();
					if (imageFile.exists()) {
						Log.v("Debug", "Image file exists");
						Map postData = new HashMap();
						Map postDataFiles = new HashMap();
						postDataFiles.put("file", imageFile);
						HttpData httpData = HttpRequest
								.postRequest(
										client,
										"http://gradgrind.erso.berkeley.edu:8080/Audiveris/RunAudiveris",
										postData, postDataFiles);
						Log.v("Debug", httpData.data);
					} else {
						Log.v("Debug", "File not found " + imageFile.getAbsolutePath());
					}

					// Download the MIDI file from the response
					URL url = new URL(
							"http://gradgrind.erso.berkeley.edu/midi/example_1701664456.png.mid");
					File midiFile = new File(getFilesDir()
							+ "/example_650415305.png.mid");
					URLConnection ucon = url.openConnection();
					InputStream is = ucon.getInputStream();
					BufferedInputStream bis = new BufferedInputStream(is);
					ByteArrayBuffer baf = new ByteArrayBuffer(50);
					int c = 0;
					while ((c = bis.read()) != -1) {
						baf.append((byte) c);
					}
					FileOutputStream fos = new FileOutputStream(midiFile);
					fos.write(baf.toByteArray());
					fos.close();
					midiFileUri = Uri.fromFile(midiFile);
					fd = openFileInput("example_650415305.png.mid").getFD();
				} catch (Exception e) {
					Log.v("Debug", "EXCEPTION! " + e.getMessage());
					e.printStackTrace();
				}
				Log.v("Debug", "Done with worker thread");
				mHandler.post(mUpdateGUI);
			}
		};
		t.start();
	}

	// Threading for doing the HTTP POST in the background
	final Handler mHandler = new Handler();
	final Runnable mUpdateGUI = new Runnable() {
		public void run() {
			updateGUI();
		}
	};

	private void updateGUI() {
		// update the UI
	}

	/*
	 * This is called by the Play button. Plays the midi file returned by
	 * transform.
	 */
	public void playMusic(View v) {
		if (fd != null) {
			MediaPlayer mp = new MediaPlayer();
			try {
				mp.setDataSource(fd);
				mp.prepare();
				mp.start();
			} catch (IllegalArgumentException e) {
				Log.v("Debug", "Illegal argument exception");
				e.printStackTrace();
			} catch (IllegalStateException e) {
				Log.v("Debug", "Illegal state exception");
				e.printStackTrace();
			} catch (IOException e) {
				Log.v("Debug", "IOexception");
				e.printStackTrace();
			} catch (Exception e) {
				Log.v("Debug", "don't know why!?");
				e.printStackTrace();
			}
		} else {
			// What should we do if the user hasn't transformed anything yet?
			Log.v("Debug", "SOMETHING WENT HORRIBLY WRONG");
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
}
