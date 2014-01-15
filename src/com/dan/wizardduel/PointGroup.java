package com.dan.wizardduel;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.opengl.GLES20;
import android.os.SystemClock;

public class PointGroup {

	public ArrayList<Point> points;
	private static final int COORDS_PER_VERTEX = 3;
	private static final int COLOR_DIMENSIONS = 4;
	public FloatBuffer vertexBuffer;
	public FloatBuffer sizeBuffer;
	public FloatBuffer colorBuffer;
	public int mProgram;
	public int mPositionHandle;
	public int mColorHandle;
	int mMVPMatrixHandle;
	float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
	float[] serialCoords;
	float[] serialSizes;
	float[] serialColors;
	long uptimeMillis;
	float alphaDecayFactor = 1.0f;
	
	PointGroup(){
		points = new ArrayList<Point>();
		uptimeMillis = SystemClock.uptimeMillis();
		
        
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // creates OpenGL ES program executables
	}
	
	public void update(){
		long now = SystemClock.uptimeMillis();
		Boolean alive;
		for(int i=0;i<points.size();i++){
			Point p = points.get(i);
			alive = p.update(now - uptimeMillis,alphaDecayFactor);
			if(!alive){
				points.remove(i);
			}
			
		}
		uptimeMillis = now;
	}
	
	public void serializePoints()
	{
	    serialCoords = new float[points.size()*COORDS_PER_VERTEX];
	    serialSizes = new float[points.size()];
	    serialColors = new float[points.size()*COLOR_DIMENSIONS];
	    Point p;
	    for (int i=0; i < points.size(); i++)
	    {
	        p = points.get(i);
	        
	        serialCoords[3*i] = p.coords[0];
	        serialCoords[3*i+1] = p.coords[1];
	        serialCoords[3*i+2] = p.coords[2];
	        
	        serialSizes[i] = p.size;
	        
	        serialColors[4*i] = p.color[0];
	        serialColors[4*i+1] = p.color[1];
	        serialColors[4*i+2] = p.color[2];
	        serialColors[4*i+3] = p.color[3];
	    }
	}
	
	private void fillVertexBuffer(){
		// initialize vertex byte buffer for shape coordinates
		//float[] coords = convertPointsToFloats(points);
		serializePoints();
		
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
        		serialCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(serialCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
        
        bb = ByteBuffer.allocateDirect(
        		serialSizes.length * 4);
        bb.order(ByteOrder.nativeOrder());
        sizeBuffer = bb.asFloatBuffer();
        sizeBuffer.put(serialSizes);
        sizeBuffer.position(0);
        
        bb = ByteBuffer.allocateDirect(
        		serialColors.length * 4);
        bb.order(ByteOrder.nativeOrder());
        colorBuffer = bb.asFloatBuffer();
        colorBuffer.put(serialColors);
        colorBuffer.position(0);
        
        
	}
	
	public void clear(){
		points.clear();
	}
	
	public void draw(float[] mvpMatrix){
		update();
		
		fillVertexBuffer();

		// Add program to OpenGL ES environment
	    GLES20.glUseProgram(mProgram);

	    // get handle to vertex shader's vPosition member
	    mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

	    // Enable a handle to the triangle vertices
	    GLES20.glEnableVertexAttribArray(mPositionHandle);

	    // Prepare the triangle coordinate data
	    GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
	                                 GLES20.GL_FLOAT, false,
	                                 4*COORDS_PER_VERTEX, vertexBuffer);
	    
	    
	    int pSizeHandle = GLES20.glGetAttribLocation(mProgram, "vSize");
	    GLES20.glEnableVertexAttribArray(pSizeHandle);
	    GLES20.glVertexAttribPointer(pSizeHandle, 1, GLES20.GL_FLOAT, false, 4, sizeBuffer);
	    //GLES20.glUniform1f(pSizeHandle,10f);

	    // get handle to fragment shader's vColor member
	    mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
	    //mColorHandle = GLES20.glGetUniformLocation(mProgram, "aColor");
	    GLES20.glEnableVertexAttribArray(mColorHandle);
	    GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 4*4, colorBuffer);
	    
	    //GLES20.glUniform4fv(mColorHandle, 1, color, 0);
	    
	    GLES20.glDisable(GLES20.GL_DEPTH_TEST);
	    
	 // Blend particles
	    GLES20.glEnable ( GLES20.GL_BLEND );
	    GLES20.glBlendFunc ( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA );

	    
	    // get handle to shape's transformation matrix
	    mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
	    // Pass the projection and view transformation to the shader
	    GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
	    // Draw the triangle
	    GLES20.glDrawArrays(GLES20.GL_POINTS, 0, points.size());

	    // Disable vertex array
	    GLES20.glDisableVertexAttribArray(mPositionHandle);
	    GLES20.glDisableVertexAttribArray(pSizeHandle);
	    GLES20.glDisableVertexAttribArray(mColorHandle);
	    //GLES20.glDisable(GLES20.GL_BLEND);
	}
	
	
	private final String vertexShaderCode =
			"uniform mat4 uMVPMatrix;"+
		    "attribute vec4 vPosition;" +
		    "attribute float vSize;" +
		    "attribute vec4 aColor;" +
		    "varying vec4 vColor;" +
		    "void main() {" +
		    "  gl_Position = uMVPMatrix * vPosition;" +
		    "  gl_PointSize = vSize;" +
		    "  vColor = aColor;" +
		    "}";

		private final String fragmentShaderCode =
		    "precision mediump float;" +
		    "varying vec4 vColor;" +
		    "void main() {" +
		    "  gl_FragColor = vColor;" +
		    "}";
		
		public static int loadShader(int type, String shaderCode){

		    // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		    int shader = GLES20.glCreateShader(type);

		    // add the source code to the shader and compile it
		    GLES20.glShaderSource(shader, shaderCode);
		    GLES20.glCompileShader(shader);

		    return shader;
		}
}
