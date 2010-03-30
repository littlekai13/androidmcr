package edu.berkeley.sec.mcr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GUI extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    public void sayHello(View v) {
        Intent i = new Intent(GUI.this, GotPhoto.class);
        startActivity(i);
    }
    
}