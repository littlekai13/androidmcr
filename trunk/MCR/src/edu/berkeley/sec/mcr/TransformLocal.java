package edu.berkeley.sec.mcr;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class TransformLocal extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transformlocal);

		// Set the photo
		// Probably won't need this in the actual Transform activity and
		// it doesn't work w/large images taken by the camera.
		// Tried to resize the image first. Still gives outofmemoryerror.
		// try {
		// // This is how to get InputStream from URI:
		// Bitmap lg_bm = BitmapFactory.decodeStream(getContentResolver()
		// .openInputStream(getIntent().getData()));
		//
		// float scale = (float) .3;
		// Matrix mtx = new Matrix();
		// mtx.postScale(scale, scale);
		// Bitmap sm_bm = Bitmap.createBitmap(lg_bm, 0, 0, lg_bm.getWidth(),
		// lg_bm.getHeight(), mtx, true);
		// // BitmapDrawable bmd = new BitmapDrawable(sm_bm);
		// ImageView iv = (ImageView) this.findViewById(R.id.sheet_music);
		// iv.setImageBitmap(sm_bm);
		//
		// // ImageView iv = (ImageView) this.findViewById(R.id.sheet_music);
		// BitmapDrawable bd = (BitmapDrawable) iv.getDrawable();
		// if (bd != null) {
		// bd.getBitmap().recycle();
		// }
		// iv.setImageURI(getIntent().getData());
		// } catch (Exception ex) {
		//
		// }
	}

	// @Override
	public void onDestroy() {
		super.onDestroy();

	}

	public void done(View v) {
		Intent resultIntent = new Intent();
		Uri midiFileUri = Uri
				.parse("android.resource://edu.berkeley.sec.mcr/raw/twinkle");
		resultIntent.setData(midiFileUri);
		setResult(RESULT_OK, resultIntent);
		finish();
	}

}
