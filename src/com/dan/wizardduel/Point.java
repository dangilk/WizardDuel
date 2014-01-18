package com.dan.wizardduel;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.util.Log;

public class Point {
	private static final float maxSize = 5f;
	private static final float minSize = 1f;
	private static final float minAlpha = 0.5f;
	private static final float maxAlpha = 1f;
	private static final float alphaDecay = -0.0001f;
	private static final float minVel = -0.01f;
	private static final float maxVel = 0.01f;
	private static final float fuzzyRange = 10f;

	
	public float size = 10f;
	public float[] coords = {0 , 0, 0};
	public float vX = 0;
	public float vY = 0;
	
	float color[] = { 1.0f, 0.0f, 0.0f, 1.0f };
	private static Random rand = new Random();
	
	Point(float x, float y,Boolean fuzzy){
		if(fuzzy){
			x += rand.nextFloat()*fuzzyRange*2-fuzzyRange;
			y += rand.nextFloat()*fuzzyRange*2-fuzzyRange;
		}
		this.coords[0]=x;
		this.coords[1]=y;

		size = rand.nextFloat()*(maxSize-minSize)+minSize;
		color[3] = rand.nextFloat()*(maxAlpha-minAlpha)+minAlpha;
		this.vX = rand.nextFloat()*(maxVel-minVel)+minVel;
		this.vY = rand.nextFloat()*(maxVel-minVel)+minVel;
		
	}
	
	public Boolean update(float timeDelta,float alphaFactor){
		coords[0]+= timeDelta*vX;
		coords[1]+= timeDelta*vY;
		color[3]+= timeDelta*alphaDecay*alphaFactor;
		if(color[3]<=0){
			return false;
		}
		return true;
	}
	

	

}
