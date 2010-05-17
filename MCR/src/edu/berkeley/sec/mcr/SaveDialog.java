package edu.berkeley.sec.mcr;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SaveDialog extends Dialog {

    public interface ReadyListener {
        public void ready(String title, String artist);
    }

    private String name;
    private ReadyListener readyListener;
    EditText title;
    EditText artist;

    public SaveDialog(Context context, String name, 
            ReadyListener readyListener) {
        super(context);
        this.name = name;
        this.readyListener = readyListener;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.savedialog);
        setTitle("Set title and artist");
        Button buttonOK = (Button) findViewById(R.id.Button01);
        buttonOK.setOnClickListener(new OKListener());
        title = (EditText) findViewById(R.id.EditText01);
        artist = (EditText) findViewById(R.id.EditText02);
    }

    private class OKListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            readyListener.ready(String.valueOf(title.getText()),String.valueOf(artist.getText()));
            SaveDialog.this.dismiss();
        }
    }

}
