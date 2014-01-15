package com.dan.wizardduel.duelists;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class StatMeterView extends View{
	Paint paint = new Paint();
	public int percentFull = 100;
	public int height;
	public int width;
	public int color = Color.GREEN;
	
	public StatMeterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		final StatMeterView self = this;
		this.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener(){
			@Override
			public void onGlobalLayout() {
				// TODO Auto-generated method stub
				height = self.getMeasuredHeight();
				width = self.getMeasuredWidth();
			}
			
		});
		// TODO Auto-generated constructor stub
	}

	public void setPercentFull(int percent){
		percentFull = percent;
		this.invalidate();
	}
	
	@Override
	public void onDraw(Canvas canvas) {
        paint.setColor(color);
        int top = height - height*percentFull/100;
        canvas.drawRect(0, top, width, height, paint );

    }
}
