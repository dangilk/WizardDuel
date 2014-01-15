package com.dan.wizardduel.combat;

public class Effect {

	public int type;
	public int duration;
	public String name;
	public Boolean buff;
	
	public static int SLOW_CAST=1;
	
	public Effect(int type,int duration){
		this.type = type;
		this.duration = duration;
	}
}
