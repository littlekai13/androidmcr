package edu.berkeley.sec.mcr;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface.OnClickListener;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * This Activity takes in a URI for a photo. It displays the photo. It has
 * transform & play buttons.
 */
public class GotPhoto extends Activity {

	private Uri midiFileUri;
	private FileDescriptor fd;
	private Uri musicPhotoUri;
	private boolean readPressed;
	private boolean broken;
	private ProgressDialog pd;

	/*
	 * Expects an image Uri to be passed in as an extra, named "thePhoto".
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gotphoto);
		readPressed = false;
		broken = false;

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
			readPressed = true;
			ImageButton read = (ImageButton) this
                .findViewById(R.id.transform_local);
			read.setImageResource(R.drawable.read_gray);
			badError("Unable to load that photo, please choose another");
		}

	}

	// Called when "Read music" button is clicked
	public void startTransform(View v) {
		if (readPressed == true || broken == true) {
			return;
		} else {
			readPressed = true;
		}
		ImageButton read = (ImageButton) this
				.findViewById(R.id.transform_local);
		read.setImageResource(R.drawable.read_gray);
		
		pd = ProgressDialog.show(this, "Reading file", "Getting file data", true, false);

		Thread t = new Thread() {
			public void run() {
				try {
					// Get the image
					AssetFileDescriptor thePhoto = getContentResolver()
							.openAssetFileDescriptor(musicPhotoUri, "r");
					FileChannel orig = thePhoto.createInputStream()
							.getChannel();
					// NOTE THAT WE ONLY HANDLE JPGS RIGHT NOW
					// I can't figure out how to find out the file type!
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
					progressHandler.sendEmptyMessage(1);
					HttpClient client = new DefaultHttpClient();
					String response;
					if (imageFile.exists()) {
						Map postData = new HashMap();
						Map postDataFiles = new HashMap();
						postDataFiles.put("file", imageFile);
						HttpData httpData = HttpRequest
								.postRequest(
										client,
										"http://gradgrind.erso.berkeley.edu:8080/Audiveris/RunAudiveris",
										postData, postDataFiles);
						// Check the response URL
						progressHandler.sendEmptyMessage(2);
						response = httpData.data.substring(0, httpData.data.length()-1);
					} else {
						Log.v("Debug", "File not found "
								+ imageFile.getAbsolutePath());
						throw new Exception();
					}
					
					// Download the MIDI file from the response
					if (response == null || response.equals("ERROR")) {
					    throw new Exception();
					} else if (response.substring(0, 40).equals("http://gradgrind.erso.berkeley.edu/midi/")) {  
					    URL url = new URL(response);
					    File midiFile = new File(getFilesDir()
					            + "/" + url.getFile());
					    Thread.sleep(10000); // give server time to write file to disk
					    progressHandler.sendEmptyMessage(3);
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
					    fd = openFileInput(url.getFile()).getFD();
					} else {
					    throw new Exception();
					}
				} catch (Exception e) {
				    broken = true;
					Log.v("Debug", "EXCEPTION! " + e.getMessage());
					e.printStackTrace();
					progressHandler.sendEmptyMessage(0);
					mHandler.post(mBadError);
				}
				Log.v("Debug", "Done with worker thread");
				if (!broken) {
				    mHandler.post(mUpdateGUI);
				    progressHandler.sendEmptyMessage(0);
				}
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
	final Runnable mBadError = new Runnable() {
	    public void run() {
	        badError();
	    }
	};
	final Handler progressHandler = new Handler() {
	    public void handleMessage(Message msg) {
	        if (msg.what == 0) {
	            pd.dismiss();
	        } else if (msg.what == 1) {
	            pd.setMessage("Uploading image file");
	        } else if (msg.what == 2) {
	            pd.setMessage("Server is processing");
	        } else if (msg.what == 3) {
	            pd.setMessage("Downloading MIDI file");
	        }
	        
	    }
	};

	private void updateGUI() {
		ImageButton play = (ImageButton) this.findViewById(R.id.play);
		ImageButton save = (ImageButton) this.findViewById(R.id.savetodisk);
		play.setImageResource(R.drawable.play_drawable);
		save.setImageResource(R.drawable.savetodisk_drawable);
	}
	
	OnClickListener accept;
	private void badError() {
	    new AlertDialog.Builder(this)
            .setMessage("Unable to correctly communicate with the server. " +
            		"Check your Internet connection or try another image.")
            .setPositiveButton("OK", accept)
            .show();
    }
	
	private void badError(String message) {
	    broken = true;
	    new AlertDialog.Builder(this)
        .setMessage(message)
        .setPositiveButton("OK", accept)
        .show();
	}
	

	/*
	 * This is called by the Play button. Plays the midi file returned by
	 * transform.
	 */
	public void playMusic(View v) {
		if (readPressed == false || broken == true) { return; }
		
		if (fd != null) {
			MediaPlayer mp = new MediaPlayer();
			try {
				mp.setDataSource(fd);
				mp.prepare();
				mp.start();
			} catch (IllegalArgumentException e) {
				Log.v("Debug", "Illegal argument exception");
				e.printStackTrace();
				badError("Error with music player");
			} catch (IllegalStateException e) {
				Log.v("Debug", "Illegal state exception");
				e.printStackTrace();
				badError("Error with music player");
			} catch (IOException e) {
				Log.v("Debug", "IOexception");
				e.printStackTrace();
				badError("Error with music player");
			} catch (Exception e) {
				Log.v("Debug", "don't know why!?");
				e.printStackTrace();
				badError("Error with music player");
			}
		} else {
			badError("Error reading MIDI file");
		}
	}

	/*
	 * This is called by the save button. Saves the midi file returned by
	 * transform to the Media content provider.
	 * 
	 * Still needs UI for changing name/title/etc.
	 */
	public void saveMusic(View v) {

		if (readPressed == false || broken == true) { return; }

		if (fd != null) {

			// To add audio to content provider.
			// First provide the meta data. (cv.put...)
			// Then add the metadata to the CP (cr.insert(...))
			// This returns a URI to the newly inserted record.
			// You use that URI to add in the real data
			// (cr.openOutputStream(insertedUri))
			// That should do it.

			// In general, anytime you access the contentresolver (i.e. the DB
			// w/all the media),
			// You reference your particular record by providing the URI to it
			// as the first arg.

			ContentValues cv = new ContentValues(3);
			cv.put(Media.DISPLAY_NAME, "transformed twinkle");
			cv.put(Media.TITLE, "Twinkle Twinkle Little Star");
			cv.put(Media.MIME_TYPE, "audio/mid");
			// This seems to be necessary to keep cr.insert from failing. It
			// should get overwriiten later in the try/catch block.

			// Adrienne, here's why I think it's failing. I have to
			// put in this fake data to keep it from crashing, andit should get
			// overwritten
			// in the try/catch, but it looks like it's throwing a filenotfound
			// exception I think on the cr.openOutputStream(insertedUri) part.
			// If you fix that, the rest of it might magically work.
			cv.put(Media.DATA, "fake/data");

			ContentResolver cr = getContentResolver();
			Uri insertedUri = cr.insert(Media.EXTERNAL_CONTENT_URI, cv);

			if (insertedUri == null) {
				// Well that sucks. No new content entry.
			} else {

				try {

					FileInputStream fis = new FileInputStream(fd);
					BufferedInputStream bis = new BufferedInputStream(fis);

					ByteArrayBuffer baf = new ByteArrayBuffer(50);
					int c = 0;
					while ((c = bis.read()) != -1) {
						baf.append((byte) c);
					}

					OutputStream os = cr.openOutputStream(insertedUri);
					os.write(baf.toByteArray());
					os.flush();
					os.close();

				} catch (Exception ex) {
					// Sucks to be here.
					Log.v("Debug", "Couldn't write audio file");
				}

			}
		} else {
			Log.v("Debug", "User is trying to save w/o first transforming");
		}
	}
}
