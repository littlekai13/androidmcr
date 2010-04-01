package edu.berkeley.sec.mcr;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * This Activity takes in a URI for a photo.
 * It displays the photo.
 * It has transform & play buttons.
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
        // AssetFileDescriptor thePhoto = getContentResolver().openAssetFileDescriptor(URI, "r");
    }

}
