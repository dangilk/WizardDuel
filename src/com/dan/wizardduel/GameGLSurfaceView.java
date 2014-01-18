package com.dan.wizardduel;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class GameGLSurfaceView extends GLSurfaceView {
	GameRenderer mRenderer;
	int[] surfaceOffset = new int[2];
	int surfaceHeight;
	
	public GameGLSurfaceView(Context context,AttributeSet attrSet) {
		super(context,attrSet);
		// Create an OpenGL ES 2.0 context
		setEGLContextClientVersion(2);
		mRenderer = new GameRenderer();
		setRenderer(mRenderer);
		
	}
	
	@Override
	protected void onLayout (boolean changed, int left, int top, int right, int bottom){
		this.getLocationOnScreen(surfaceOffset);
		surfaceHeight = bottom - top;
	}
	
	public void clear(){
		mRenderer.pointGroup.clear();
	}
	
	public void forceFade(){
		mRenderer.pointGroup.alphaDecayFactor = 5.0f;
	}
	public void resetFade(){
		mRenderer.pointGroup.alphaDecayFactor = 1.0f;
		
	}
	
	public boolean onTouchEvent(final MotionEvent event) {
        queueEvent(new Runnable(){
            public void run() {
            	PointGroup pg = mRenderer.pointGroup;
            	for(int i=0;i<5;i++){
            		pg.points.add(new Point(event.getX(),surfaceHeight-event.getY()+surfaceOffset[1],true));
            	}
            	
            }});
            return true;
        }

}
