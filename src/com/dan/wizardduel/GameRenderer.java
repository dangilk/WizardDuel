package com.dan.wizardduel;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;
public class GameRenderer implements GLSurfaceView.Renderer{
	private float mRed;
    private float mGreen;
    private float mBlue;
    public ArrayList<Float> points = new ArrayList<Float>();
    public Point point;
    float[] mViewMatrix = new float[16];
    float[] mMVPMatrix = new float[16];
    float[] mProjectionMatrix = new float[16];
    int surfaceHeight = 0;
    int surfaceWidth = 0;
    public PointGroup pointGroup;
	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		//gl.glClearColor(mRed, mGreen, mBlue, 1.0f);
		gl.glClearColor(0, 0, 0, 1.0f);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		/*float[] pArray = {0.5f,0.5f,1,1};//convertFloats(points);
		ByteBuffer vbb = ByteBuffer.allocateDirect(pArray.length * 4); 
		vbb.order(ByteOrder.nativeOrder());    // use the device hardware's native byte order
		FloatBuffer fb = vbb.asFloatBuffer();  // create a floating point buffer from the ByteBuffer
		fb.put(pArray);    // add the coordinates to the FloatBuffer
		fb.position(0); 
		gl.glEnableClientState(gl.GL_VERTEX_ARRAY);
		
		
		gl.glColor4f(1, 0, 0, 1);
		
		gl.glPointSize(10);
		gl.glVertexPointer(2, gl.GL_FLOAT, 4*pArray.length, fb);
		gl.glDrawArrays(gl.GL_POINTS, 0, pArray.length);
		//gl.glDrawElements(gl.GL_POINTS, pArray.length, gl.GL_FLOAT, fb);
		gl.glDisableClientState(gl.GL_VERTEX_ARRAY);*/
		
		

	    // Calculate the projection and view transformation
	    Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

	    // Draw shape
	    pointGroup.draw(mMVPMatrix);

	}
	
	public static float[] convertFloats(ArrayList<Float> floats)
	{
	    float[] ret = new float[floats.size()];
	    for (int i=0; i < ret.length; i++)
	    {
	        ret[i] = floats.get(i).floatValue();
	        
	    }
	    return ret;
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		GLES20.glViewport(0, 0, width, height);

	    float ratio = (float) width / height;
	    surfaceHeight = height;
	    surfaceWidth = width;
	    
	    //ratio = 100;

	    // this projection matrix is applied to object coordinates
	    // in the onDrawFrame() method
	    //Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
	    Matrix.orthoM(mProjectionMatrix, 0, 0, width, 0, height, 3, 7);
	    //Matrix.orthoM(mProjectionMatrix,  0, -ratio, ratio, -1, 1, 3, 7);
	    
	    // Set the camera position (View matrix)
 		float yCenter = (float)surfaceHeight/2;
 		float xCenter = (float)surfaceWidth/2;
 	    //Matrix.setLookAtM(mViewMatrix, 0, xCenter, yCenter, 3, xCenter, yCenter, 0f, 0f, 1.0f, 0.0f);
 	    Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 3, 0, 0, 0f, 0f, 1.0f, 0.0f);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		pointGroup = new PointGroup();
		
	}
	public void setColor(float r, float g, float b) {
        mRed = r;
        mGreen = g;
        mBlue = b;
    }

}
