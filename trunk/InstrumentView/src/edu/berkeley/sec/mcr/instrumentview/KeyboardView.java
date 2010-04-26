package edu.berkeley.sec.mcr.instrumentview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.view.View;

public class KeyboardView extends View {
	public KeyboardView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawColor(Color.WHITE);
		
		int[] pressedKeys = {10, 17};
		
		drawKeyboard(canvas, 10f, 10f, canvas.getWidth()-20f, 7, pressedKeys);
	}
	
	private void drawKeyboard(Canvas canvas, float offsetX, float offsetY, float width, int octaves, int[] pressedKeys) {
		float whiteKeyWidth = width / (octaves*7f);
		float blackKeyWidth = whiteKeyWidth * 0.75f;
		
		float whiteKeyLength = whiteKeyWidth*7.5f;
		float blackKeyLength = whiteKeyLength*2f/3f;
		
		for(int octave=0; octave<7 ; octave++)
			drawOctave(canvas, octave, offsetX, offsetY, whiteKeyWidth, blackKeyWidth, whiteKeyLength, blackKeyLength);
		
		Paint pressedPaint = new Paint();
		pressedPaint.setColor(Color.BLUE);
		pressedPaint.setStyle(Style.FILL);
		
		for(int key : pressedKeys)
			canvas.drawCircle(offsetX+(key+0.5f)*whiteKeyWidth, offsetY+whiteKeyLength*0.9f, whiteKeyWidth*0.45f, pressedPaint);
	}
	
	private void drawOctave(Canvas canvas, int num, float offsetX, float offsetY, float whiteKeyWidth, float blackKeyWidth, float whiteKeyLength, float blackKeyLength) {
		for(int i=num; i<7*(num+1); i++)
			drawWhiteKey(canvas, i, offsetX, offsetY, whiteKeyWidth, whiteKeyLength);

		int[] keys = {0, 1, 3, 4, 5};
		for(int key : keys)
			drawBlackKey(canvas, 7*num+key, offsetX, offsetY, whiteKeyWidth, blackKeyWidth, blackKeyLength);
	}
	
	private void drawWhiteKey(Canvas canvas, int num, float offsetX, float offsetY, float whiteKeyWidth, float whiteKeyLength) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(0.5f);

		canvas.drawRect(new RectF(offsetX+num*whiteKeyWidth, offsetY, offsetX+(num+1)*whiteKeyWidth, offsetY+whiteKeyLength), paint);
	}

	private void drawBlackKey(Canvas canvas, int num, float offsetX, float offsetY, float whiteKeyWidth, float blackKeyWidth, float blackKeyLength) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);

		canvas.drawRect(new RectF(offsetX+(num+1)*whiteKeyWidth-blackKeyWidth/2f, offsetY, offsetX+(num+1)*whiteKeyWidth+blackKeyWidth/2f, offsetY+blackKeyLength), paint);
	}
}
