package edu.berkeley.sec.mcr.instrumentview;

import android.app.Activity;
import android.os.Bundle;

public class InstrumentActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new KeyboardView(this));
	}
}
