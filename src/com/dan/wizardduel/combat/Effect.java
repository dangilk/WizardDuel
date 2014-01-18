package com.dan.wizardduel.combat;

public class Effect {

	public int type;
	public long expires;
	public String name;
	public Boolean buff;
	public int amplitude=3;
	
	public static final int SLOW=1;
	public static final int HASTE=2;
	public static final int POISON=3;
	public static final int LOCK=4;
	public static final int SHIELD=5;
	
	public Effect(int type){
		long now = System.currentTimeMillis();
		this.type = type;
		switch(type){
		case SLOW:
			expires=now+10*1000;
			buff = false;
			break;
		case HASTE:
			expires=now+10*1000;
			buff = true;
			break;
		case POISON:
			expires=now+10*1000;
			buff = false;
		}
	}
	
	public Boolean isExpired(){
		if (System.currentTimeMillis()>=expires){
			return true;
		}
		return false;
	}
}
